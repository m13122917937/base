"""Small Jenkins API client; credentials come from environment or private stdin."""

import argparse
import base64
import hashlib
import http.client
import http.cookiejar
import json
import os
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET


BASE_URL = "http://172.25.197.86:7080/"
JOBS = {target: "prod-jl-storepal-" + target for target in ("ui", "web", "api", "admin")}
SENSITIVE = re.compile(r"password|passwd|secret|token|credential|private.?key", re.I)
DEPLOY_MESSAGES = {"请确认是否继续部署到生产环境？"}


class ApiError(Exception):
    """An error that is safe to print without exposing HTTP response bodies."""


class NoRedirect(urllib.request.HTTPRedirectHandler):
    """Do not forward credentials to redirected destinations or replay POSTs."""

    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def same_server(left, right):
    left_url = urllib.parse.urlsplit(left)
    right_url = urllib.parse.urlsplit(right)
    return (left_url.scheme, left_url.netloc) == (right_url.scheme, right_url.netloc)


class Client:
    """Authenticated, bounded requests without automatic retries."""

    def __init__(self, base_url=BASE_URL, username=None, secret=None, token=None):
        self.base_url = base_url.rstrip("/") + "/"
        username = username or os.environ.get("JENKINS_USER")
        token = token if token is not None else os.environ.get("JENKINS_API_TOKEN")
        secret = secret or token or os.environ.get("JENKINS_PASSWORD")
        if not username or not secret:
            raise ApiError("Set JENKINS_USER and JENKINS_API_TOKEN (or JENKINS_PASSWORD) outside chat.")
        self.secret = secret
        self.token = bool(token)
        encoded = base64.b64encode((username + ":" + secret).encode("utf-8")).decode("ascii")
        self.headers = {"Authorization": "Basic " + encoded}
        self.opener = urllib.request.build_opener(
            urllib.request.ProxyHandler({}), NoRedirect(),
            urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()),
        )

    def request(self, method, path, form=None):
        url = urllib.parse.urljoin(self.base_url, path)
        if not same_server(url, self.base_url):
            raise ApiError("Refusing a destination outside the configured Jenkins server.")
        headers = dict(self.headers)
        if method == "POST" and not self.token:
            crumb = self.get("crumbIssuer/api/json")
            field = crumb.get("crumbRequestField", "")
            if not re.fullmatch(r"[A-Za-z0-9-]+", field) or field.lower() in ("authorization", "cookie", "host"):
                raise ApiError("Invalid CSRF crumb response; no POST sent.")
            headers[field] = crumb["crumb"]
        data = None if form is None else urllib.parse.urlencode(form).encode("utf-8")
        request = urllib.request.Request(url, data=data, headers=headers, method=method)
        try:
            with self.opener.open(request, timeout=20) as response:
                return response.status, response.headers, response.read()
        except urllib.error.HTTPError as error:
            with error:
                if method == "POST" and error.code in (302, 303):
                    return error.code, error.headers, b""
                raise ApiError(f"Jenkins HTTP {error.code}; response body suppressed. No automatic retry.") from None
        except (urllib.error.URLError, TimeoutError, OSError, http.client.HTTPException):
            raise ApiError("Jenkins connection failed. A POST may have succeeded; inspect queue/build before retrying.") from None

    def get(self, path):
        _, _, data = self.request("GET", path)
        try:
            return json.loads(data)
        except (ValueError, UnicodeError):
            raise ApiError("Invalid JSON response; no empty/success result assumed.") from None

    def redact(self, value):
        if isinstance(value, dict):
            return {k: "[redacted]" if SENSITIVE.search(k) else self.redact(v) for k, v in value.items()}
        if isinstance(value, list):
            return [self.redact(item) for item in value]
        return value.replace(self.secret, "[redacted]") if isinstance(value, str) else value


def job_path(target):
    """Only the four explicitly configured production jobs are supported."""
    return "job/" + JOBS[target] + "/"


def json_path(path, tree):
    return path + "api/json?" + urllib.parse.urlencode({"tree": tree})


def config_summary(client, path):
    """Extract deployment metadata; never return raw XML or pipeline scripts."""
    _, _, raw = client.request("GET", path + "config.xml")
    try:
        if b"<!DOCTYPE" in raw.upper():
            raise ET.ParseError()
        root = ET.fromstring(raw)
    except ET.ParseError:
        raise ApiError("Invalid job config XML.") from None
    script = root.findtext(".//definition/script", "")
    branches = [node.text for node in root.findall(".//hudson.plugins.git.BranchSpec/name") if node.text]
    branches += re.findall(r"\bgit\s+branch\s*:\s*['\"]([^'\"]+)['\"]", script)
    return {"sha256": hashlib.sha256(raw).hexdigest(), "branches": sorted(set(branches)),
            "branch_source": "literal config; dynamic/shared-library branches require separate verification",
            "stages": re.findall(r"\bstage\s*\(\s*['\"]([^'\"]+)['\"]", script)}


def resolve_parameters(job, overrides):
    """Resolve supported non-secret parameters; fail on omitted/unknown values."""
    definitions = [d for p in job.get("property", []) for d in p.get("parameterDefinitions", [])]
    names = {d["name"] for d in definitions}
    if set(overrides) - names:
        raise ApiError("Unknown build parameter; recheck the job definition.")
    values = {}
    supported = {"StringParameterDefinition", "TextParameterDefinition",
                 "BooleanParameterDefinition", "ChoiceParameterDefinition"}
    for definition in definitions:
        name = definition["name"]
        kind = definition.get("_class", "").rsplit(".", 1)[-1]
        if SENSITIVE.search(name) or kind not in supported:
            raise ApiError("Secret/file/plugin-specific parameter requires a separately verified API contract.")
        default = definition.get("defaultParameterValue") or {}
        value = overrides[name] if name in overrides else default.get("value")
        if value is None:
            raise ApiError("A required build parameter has no explicit/default value.")
        if kind == "BooleanParameterDefinition":
            if str(value).lower() not in ("true", "false"):
                raise ApiError("Boolean parameter must be true or false.")
            value = str(value).lower()
        if kind == "ChoiceParameterDefinition" and value not in definition.get("choices", []):
            raise ApiError("Parameter value is outside the declared choices.")
        values[name] = value
    return values


def preflight(client, target, overrides):
    """GET-only job, config and complete queue checks; compact output."""
    path = job_path(target)
    tree = ("name,buildable,inQueue,lastBuild[number,result,url,building],builds[number,building],"
            "property[parameterDefinitions[name,_class,choices,defaultParameterValue[value]]]")
    job = client.get(json_path(path, tree))
    if job.get("name") != JOBS[target]:
        raise ApiError("Job identity does not match the requested target.")
    parameters = resolve_parameters(job, overrides)
    queue = client.get(json_path("queue/", "items[id,why,task[name,url]]"))
    queued = [item for item in queue["items"] if item.get("task", {}).get("name") == JOBS[target]]
    return {"target": target, "job": job["name"], "url": client.base_url + path,
            "buildable": job.get("buildable"), "parameters": parameters,
            "config": config_summary(client, path), "last_build": job.get("lastBuild"),
            "running": [b["number"] for b in job["builds"] if b.get("building")],
            "queued": queued, "in_queue": job.get("inQueue")}


def trigger(client, target, overrides, expected_sha=None):
    """Check the target internally and issue one explicitly authorized build POST."""
    summary = preflight(client, target, overrides)
    if expected_sha and summary["config"]["sha256"] != expected_sha:
        raise ApiError("Job config changed; show a new preflight summary and obtain approval.")
    if not summary["buildable"] or summary["running"] or summary["queued"] or summary["in_queue"]:
        raise ApiError("Job is disabled or already active/queued; no build POST sent.")
    suffix = "buildWithParameters" if summary["parameters"] else "build"
    code, headers, _ = client.request("POST", job_path(target) + suffix, summary["parameters"])
    location = urllib.parse.urljoin(client.base_url, headers.get("Location", ""))
    parsed = urllib.parse.urlsplit(location)
    valid_location = (same_server(location, client.base_url)
                      and re.fullmatch(r"/queue/item/\d+/", parsed.path)
                      and not parsed.query and not parsed.fragment)
    if code != 201 or not valid_location:
        raise ApiError("Build POST outcome uncertain; inspect queue/builds and do not resubmit.")
    return {"state": "QUEUED", "queue_id": int(parsed.path.rstrip("/").split("/")[-1]),
            "queue_url": location, "job": summary["job"], "branches": summary["config"]["branches"],
            "parameters": summary["parameters"]}


def extract_inputs(actions, path):
    """Use the standard InputAction export; no Pipeline REST plugin is needed."""
    pending = []
    for action in actions:
        if action.get("_class") != "org.jenkinsci.plugins.workflow.support.steps.input.InputAction":
            continue
        for execution in action["executions"]:
            if execution["settled"]:
                continue
            input_id = execution["id"]
            definition = execution["input"]
            parameters = definition["parameters"]
            if not isinstance(parameters, list) or not re.fullmatch(r"[A-Za-z0-9_-]+", input_id):
                raise ApiError("Unsupported input schema or input ID; no approval POST sent.")
            suffix = "proceed" if parameters else "proceedEmpty"
            pending.append({"id": input_id, "message": definition["message"],
                            "parameter_names": [p["name"] for p in parameters],
                            "proceedUrl": "/" + path + "input/" + input_id + "/" + suffix})
    return pending


def status(client, target, build):
    """Report one exact build and exported InputAction using the core JSON API."""
    path = job_path(target) + str(build) + "/"
    tree = ("number,url,building,result,queueId,"
            "actions[_class,executions[id,settled,input[message,parameters[name]]]]")
    result = client.get(json_path(path, tree))
    if result["number"] != build or not isinstance(result.get("actions"), list):
        raise ApiError("Build identity or action schema does not match the requested build.")
    result["pending_inputs"] = extract_inputs(result.pop("actions"), path)
    if result["pending_inputs"]:
        result["pipeline_status"] = "PAUSED_PENDING_INPUT"
    else:
        result["pipeline_status"] = "IN_PROGRESS" if result["building"] else result["result"]
    return result


def approve(client, target, build, input_id, verify=True):
    """Only approve the returned parameterless input of the exact build."""
    path = job_path(target) + str(build) + "/"
    pending = status(client, target, build)["pending_inputs"]
    matches = [item for item in pending if item.get("id") == input_id]
    if len(matches) != 1 or not re.fullmatch(r"[A-Za-z0-9_-]+", input_id):
        raise ApiError("Pending input ID not found or unsupported; no approval POST sent.")
    url = urllib.parse.urljoin(client.base_url, matches[0].get("proceedUrl", ""))
    parsed = urllib.parse.urlsplit(url)
    expected = "/" + path + "input/" + input_id + "/proceedEmpty"
    valid_input = (same_server(url, client.base_url) and parsed.path == expected
                   and not parsed.query and not parsed.fragment)
    if not valid_input:
        raise ApiError("Input has parameters or an unexpected proceed URL; verify its contract first.")
    code, headers, _ = client.request("POST", url, {})
    if code in (302, 303):
        destination = urllib.parse.urljoin(url, headers.get("Location", ""))
        if not headers.get("Location") or not destination.startswith(client.base_url + path):
            raise ApiError("Approval redirect unexpected; inspect build before any retry.")
    result = {"state": "APPROVAL_SUBMITTED", "http_status": code, "build": build,
              "input_id": input_id}
    if verify:
        result["verification"] = status(client, target, build)
    return result


def queue_status(client, target, queue_id):
    """Read the queue item associated with this deployment, never lastBuild."""
    result = client.get(json_path(f"queue/item/{queue_id}/",
                                 "id,cancelled,why,task[name],executable[number,url]"))
    if result.get("task", {}).get("name") != JOBS[target]:
        raise ApiError("Queue item belongs to a different target.")
    return result


def emit_event(client, event):
    print(json.dumps(client.redact(event), ensure_ascii=False, separators=(",", ":")), flush=True)


class Deployment:
    """One process owns trigger, queue tracking, approval and completion."""

    def __init__(self, client, target, overrides=None, expected_sha=None, build=None,
                 poll_seconds=15, timeout=1800, emit=None, wait_result=False):
        self.client = client
        self.target = target
        self.overrides = overrides or {}
        self.expected_sha = expected_sha
        self.build = build
        self.poll_seconds = poll_seconds
        self.timeout = timeout
        self.emit = emit or (lambda event: emit_event(client, event))
        self.approved = set()
        self.wait_result = wait_result

    def run(self):
        deadline = time.monotonic() + self.timeout
        queue_id = None
        if self.build is None:
            queued = trigger(self.client, self.target, self.overrides, self.expected_sha)
            queue_id = queued["queue_id"]
            self.emit({"state": "QUEUED", "target": self.target, "queue_id": queue_id})
        while time.monotonic() < deadline:
            if self.build is None:
                queued = queue_status(self.client, self.target, queue_id)
                if queued.get("cancelled"):
                    raise ApiError("Deployment queue item was cancelled; no retry performed.")
                self.build = queued.get("executable", {}).get("number")
                if self.build is not None:
                    self.emit({"state": "BUILDING", **self.identity()})
            if self.build is not None:
                current = status(self.client, self.target, self.build)
                if queue_id is not None and current.get("queueId") != queue_id:
                    raise ApiError("Build queueId does not match this deployment.")
                outcome = self.advance(current)
                if outcome is not None:
                    return outcome
            time.sleep(self.poll_seconds)
        self.emit({"state": "TIMEOUT", **self.identity(), "queue_id": queue_id})
        return 1

    def identity(self):
        result = {"target": self.target}
        if self.build is not None:
            result.update(build=self.build,
                          url=self.client.base_url + job_path(self.target) + str(self.build) + "/")
        return result

    def advance(self, current):
        if not current["building"]:
            result = current.get("result")
            if not result:
                raise ApiError("Completed build has no result; no success assumed.")
            self.emit({"state": result, **self.identity()})
            return 0 if result == "SUCCESS" else 1
        pending = current["pending_inputs"]
        if not pending:
            return None
        if any(p["parameter_names"] or p["message"] not in DEPLOY_MESSAGES for p in pending):
            self.emit({"state": "NEEDS_INPUT", **self.identity(), "pending_inputs": pending})
            return 2
        for item in pending:
            if item["id"] in self.approved:
                continue
            approve(self.client, self.target, self.build, item["id"], verify=self.wait_result)
            self.approved.add(item["id"])
            if self.wait_result:
                self.emit({"state": "APPROVED", **self.identity()})
        if not self.wait_result:
            self.emit({"state": "DEPLOYMENT_SUBMITTED", **self.identity(), "result_checked": False})
            return 0
        return None


def parse_args():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("action", choices=("deploy", "preflight", "trigger", "queue", "status", "approve"))
    parser.add_argument("target", choices=JOBS)
    parser.add_argument("--param", action="append", default=[], metavar="NAME=VALUE")
    parser.add_argument("--build", type=int)
    parser.add_argument("--queue-id", type=int)
    parser.add_argument("--input-id")
    parser.add_argument("--config-sha", help="Optional hash when the user requested a prior config review.")
    parser.add_argument("--auth-stdin", action="store_true", help="Read username/token or username/password JSON from stdin.")
    parser.add_argument("--timeout", type=int, default=1800, help="Deployment timeout in seconds.")
    parser.add_argument("--wait-result", action="store_true", help="Opt in to monitoring after deployment approval.")
    parser.add_argument("--confirmed", action="store_true", help="Guard only; does not grant human authorization.")
    args = parser.parse_args()
    if args.action in ("deploy", "trigger", "approve") and not args.confirmed:
        parser.error("Use --confirmed only after an explicit user instruction authorizes this deployment.")
    for action, value in (("queue", args.queue_id), ("status", args.build),
                          ("approve", args.build and args.input_id)):
        if args.action == action and not value:
            parser.error("Missing required identifier/config hash for " + action)
    if args.timeout <= 0 or (args.build is not None and args.build <= 0):
        parser.error("Timeout and build number must be positive.")
    if args.action == "deploy" and args.build and (args.param or args.config_sha):
        parser.error("Cannot change parameters or config when resuming an existing build.")
    return args


def create_client(args):
    """Accept explicit in-memory credentials without writing files or CLI secrets."""
    if not args.auth_stdin:
        return Client()
    auth = json.load(sys.stdin)
    if not isinstance(auth, dict):
        raise ApiError("stdin credentials must be a JSON object.")
    username = auth.get("username")
    token = auth.get("token", "")
    secret = token or auth.get("password")
    if not isinstance(username, str) or not isinstance(token, str) or not isinstance(secret, str) or not username or not secret:
        raise ApiError("stdin credentials require username and token or password strings.")
    return Client(username=username, secret=secret, token=token)


def main():
    args = parse_args()
    client = create_client(args)
    overrides = {}
    for pair in args.param:
        if "=" not in pair or pair.split("=", 1)[0] in overrides:
            raise ApiError("Use distinct --param NAME=VALUE arguments.")
        name, value = pair.split("=", 1)
        overrides[name] = value
    if args.action == "deploy":
        return Deployment(client, args.target, overrides, args.config_sha,
                          args.build, timeout=args.timeout, wait_result=args.wait_result).run()
    if args.action == "preflight":
        result = preflight(client, args.target, overrides)
    elif args.action == "trigger":
        result = trigger(client, args.target, overrides, args.config_sha)
    elif args.action == "status":
        result = status(client, args.target, args.build)
    elif args.action == "approve":
        result = approve(client, args.target, args.build, args.input_id)
    else:
        result = queue_status(client, args.target, args.queue_id)
    print(json.dumps(client.redact(result), ensure_ascii=False, separators=(",", ":")))


if __name__ == "__main__":
    try:
        sys.exit(main() or 0)
    except (ApiError, KeyError, TypeError, ValueError) as error:
        message = str(error) if isinstance(error, ApiError) else "Unexpected Jenkins schema/value; no success assumed."
        print(json.dumps({"error": message}, ensure_ascii=False), file=sys.stderr)
        sys.exit(1)
