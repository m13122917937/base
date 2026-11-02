"""Offline HTTP contract tests. Never contact the production Jenkins."""

import importlib.util
import io
import json
import os
from pathlib import Path
import sys
import threading
import unittest
from http.server import BaseHTTPRequestHandler, HTTPServer
from unittest.mock import patch
from types import SimpleNamespace
from urllib.parse import parse_qs, urlsplit


MODULE_PATH = Path(__file__).resolve().parents[1] / "scripts" / "jenkins_api.py"
SPEC = importlib.util.spec_from_file_location("jenkins_api", MODULE_PATH)
api = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(api)
JOB = "/job/prod-jl-storepal-ui/"


class Handler(BaseHTTPRequestHandler):
    def log_message(self, *args):
        pass

    def do_GET(self):
        self.respond()

    def do_POST(self):
        self.respond()

    def respond(self):
        path = urlsplit(self.path).path
        body = self.rfile.read(int(self.headers.get("Content-Length", 0)))
        self.server.requests.append((self.command, path, dict(self.headers), body))
        code, headers, data = self.server.routes.get((self.command, path), (404, {}, {}))
        raw = data if isinstance(data, bytes) else json.dumps(data).encode("utf-8")
        self.send_response(code)
        for key, value in headers.items():
            self.send_header(key, value)
        self.send_header("Content-Length", str(len(raw)))
        self.end_headers()
        self.wfile.write(raw)


class JenkinsApiTests(unittest.TestCase):
    def setUp(self):
        self.env = patch.dict(os.environ, {name: "" for name in (
            "JENKINS_USER", "JENKINS_API_TOKEN", "JENKINS_PASSWORD")})
        self.env.start()
        self.server = HTTPServer(("127.0.0.1", 0), Handler)
        self.server.routes = {}
        self.server.requests = []
        self.worker = threading.Thread(target=self.server.serve_forever, daemon=True)
        self.worker.start()
        self.base = f"http://127.0.0.1:{self.server.server_port}/"
        self.client = api.Client(self.base, "offline-user", token="offline-token")
        self.job = {"name": "prod-jl-storepal-ui", "buildable": True, "inQueue": False,
                    "lastBuild": {"number": 122, "result": "SUCCESS"}, "builds": [], "property": []}
        self.route("GET", JOB + "api/json", self.job)
        self.route("GET", "/queue/api/json", {"items": []})
        self.route("GET", JOB + "config.xml", b"<flow-definition><definition><script>git branch: 'master'</script></definition></flow-definition>")
        self.route("POST", JOB + "build", {}, 201, {"Location": self.base + "queue/item/321/"})

    def tearDown(self):
        self.server.shutdown()
        self.worker.join()
        self.server.server_close()
        self.env.stop()

    def route(self, method, path, data, code=200, headers=None):
        self.server.routes[(method, path)] = (code, headers or {}, data)

    def posts(self):
        return [r for r in self.server.requests if r[0] == "POST"]

    def sha(self):
        return api.preflight(self.client, "ui", {})["config"]["sha256"]

    def pending(self, parameter_names=None):
        path = JOB + "123/"
        action = {"_class": "org.jenkinsci.plugins.workflow.support.steps.input.InputAction",
                  "executions": [{"id": "deploy", "settled": False,
                                  "input": {"message": "Deploy the confirmed UI version?",
                                            "parameters": [{"name": n} for n in parameter_names or []]}}]}
        self.route("GET", path + "api/json", {"number": 123, "building": True,
                                                "result": None, "actions": [action]})
        self.route("POST", path + "input/deploy/proceedEmpty", {}, 302,
                   {"Location": self.base + path.lstrip("/")})

    def test_preflight_only_reads_and_summarizes_branch(self):
        result = api.preflight(self.client, "ui", {})
        self.assertEqual(["master"], result["config"]["branches"])
        self.assertEqual({}, result["parameters"])
        self.assertFalse(self.posts())
        self.assertNotIn("script", json.dumps(result))

    def test_trigger_records_location_and_does_not_fetch_queue_as_latest_build(self):
        result = api.trigger(self.client, "ui", {}, self.sha())
        self.assertEqual(321, result["queue_id"])
        self.assertEqual(1, len(self.posts()))
        self.assertEqual(JOB + "build", self.posts()[0][1])

    def test_active_or_changed_job_does_not_send_post(self):
        sha = self.sha()
        self.job["builds"] = [{"number": 123, "building": True}]
        with self.assertRaises(api.ApiError):
            api.trigger(self.client, "ui", {}, sha)
        self.job["builds"] = []
        with self.assertRaises(api.ApiError):
            api.trigger(self.client, "ui", {}, "wrong-hash")
        self.assertFalse(self.posts())

    def test_queue_contains_target_no_post(self):
        sha = self.sha()
        self.route("GET", "/queue/api/json", {"items": [{"id": 2, "task": {"name": "prod-jl-storepal-ui"}}]})
        with self.assertRaises(api.ApiError):
            api.trigger(self.client, "ui", {}, sha)
        self.assertFalse(self.posts())

    def test_password_posts_crumb_with_same_session_cookie(self):
        self.route("GET", "/crumbIssuer/api/json", {"crumbRequestField": "Jenkins-Crumb", "crumb": "offline-crumb"},
                   headers={"Set-Cookie": "JSESSIONID=offline-session; Path=/"})
        client = api.Client(self.base, "offline-user", secret="offline-password")
        api.trigger(client, "ui", {}, self.sha())
        headers = self.posts()[0][2]
        self.assertEqual("offline-crumb", headers["Jenkins-Crumb"])
        self.assertIn("JSESSIONID=offline-session", headers["Cookie"])

    def test_crumb_failure_does_not_send_post(self):
        client = api.Client(self.base, "offline-user", secret="offline-password")
        with self.assertRaises(api.ApiError):
            api.trigger(client, "ui", {}, self.sha())
        self.assertFalse(self.posts())

    def test_parameter_encoding_and_unsupported_parameter(self):
        self.job["property"] = [{"parameterDefinitions": [
            {"name": "BRANCH", "_class": "hudson.model.StringParameterDefinition",
             "defaultParameterValue": {"value": "master"}}]}]
        self.route("POST", JOB + "buildWithParameters", {}, 201,
                   {"Location": self.base + "queue/item/321/"})
        api.trigger(self.client, "ui", {"BRANCH": "release/a&b"}, self.sha())
        self.assertEqual({"BRANCH": ["release/a&b"]}, parse_qs(self.posts()[0][3].decode()))
        self.job["property"][0]["parameterDefinitions"][0]["name"] = "PASSWORD"
        with self.assertRaises(api.ApiError):
            api.preflight(self.client, "ui", {})

    def test_boolean_and_choice_validation(self):
        job = {"property": [{"parameterDefinitions": [
            {"name": "DRY", "_class": "hudson.model.BooleanParameterDefinition", "defaultParameterValue": {"value": False}},
            {"name": "BRANCH", "_class": "hudson.model.ChoiceParameterDefinition", "choices": ["master"],
             "defaultParameterValue": {"value": "master"}}]}]}
        self.assertEqual("false", api.resolve_parameters(job, {})["DRY"])
        for overrides in ({"DRY": "yes"}, {"BRANCH": "other"}, {"unknown": "value"}):
            with self.assertRaises(api.ApiError):
                api.resolve_parameters(job, overrides)

    def test_exact_pending_input_approval_and_post_redirect_not_followed(self):
        self.pending()
        result = api.approve(self.client, "ui", 123, "deploy")
        self.assertEqual("APPROVAL_SUBMITTED", result["state"])
        self.assertEqual(1, len(self.posts()))
        self.assertEqual("PAUSED_PENDING_INPUT", result["verification"]["pipeline_status"])
        self.assertNotIn(("GET", JOB + "123/"), [(r[0], r[1]) for r in self.server.requests])

    def test_unknown_input_and_parameterized_approval_rejected(self):
        self.pending()
        with self.assertRaises(api.ApiError):
            api.approve(self.client, "ui", 123, "unknown")
        self.pending(["VERSION"])
        with self.assertRaises(api.ApiError):
            api.approve(self.client, "ui", 123, "deploy")
        for build in (124, 125):
            with self.assertRaises(api.ApiError):
                api.approve(self.client, "ui", build, "deploy")
        self.assertFalse(self.posts())

    def test_uncertain_post_not_retried_and_raw_error_body_hidden(self):
        self.route("POST", JOB + "build", b"secret-response-body", 500)
        with self.assertRaises(api.ApiError) as caught:
            api.trigger(self.client, "ui", {}, self.sha())
        self.assertNotIn("secret-response-body", str(caught.exception))
        self.assertEqual(1, len(self.posts()))

    def test_html_and_cross_origin_redirect_are_errors(self):
        self.route("GET", JOB + "api/json", b"<html>login</html>")
        with self.assertRaises(api.ApiError):
            api.preflight(self.client, "ui", {})
        self.route("GET", JOB + "api/json", {}, 302, {"Location": "http://example.invalid/login"})
        with self.assertRaises(api.ApiError):
            api.preflight(self.client, "ui", {})

    def test_redaction_and_missing_credentials(self):
        self.assertEqual({"password": "[redacted]", "message": "[redacted]"},
                         self.client.redact({"password": "anything", "message": "offline-token"}))
        with self.assertRaises(api.ApiError):
            api.Client(self.base)

    def test_missing_build_location_is_uncertain_without_retry(self):
        self.route("POST", JOB + "build", {}, 201)
        with self.assertRaises(api.ApiError):
            api.trigger(self.client, "ui", {}, self.sha())
        self.assertEqual(1, len(self.posts()))

    def test_core_api_reports_success_without_pipeline_rest_plugin(self):
        self.route("GET", JOB + "123/api/json", {"number": 123, "building": False,
                                                  "result": "SUCCESS", "actions": []})
        result = api.status(self.client, "ui", 123)
        self.assertEqual("SUCCESS", result["result"])
        self.assertEqual([], result["pending_inputs"])
        self.assertFalse(any("wfapi" in r[1] for r in self.server.requests))

    def test_direct_trigger_needs_no_separate_preflight_or_hash(self):
        result = api.trigger(self.client, "ui", {})
        self.assertEqual("QUEUED", result["state"])
        self.assertEqual(["master"], result["branches"])
        self.assertEqual(1, len(self.posts()))
        with patch.object(sys, "argv", ["jenkins_api.py", "trigger", "ui", "--confirmed"]):
            self.assertIsNone(api.parse_args().config_sha)

    def test_stdin_credentials_are_in_memory_and_do_not_change_environment(self):
        payload = json.dumps({"username": "offline-user", "password": "offline-password"})
        with patch("sys.stdin", io.StringIO(payload)):
            client = api.create_client(SimpleNamespace(auth_stdin=True))
        self.assertEqual("offline-password", client.secret)
        self.assertFalse(client.token)
        self.assertEqual("", os.environ["JENKINS_PASSWORD"])
        self.assertFalse(self.server.requests)
        for malformed in ('[]', '{"username":"offline-user"}', '{"username":"offline-user","token":null}'):
            with patch("sys.stdin", io.StringIO(malformed)):
                with self.assertRaises(api.ApiError):
                    api.create_client(SimpleNamespace(auth_stdin=True))

    def test_missing_input_schema_is_not_treated_as_no_pending_approval(self):
        self.route("GET", JOB + "123/api/json", {"number": 123, "building": True, "result": None})
        with self.assertRaises(api.ApiError):
            api.status(self.client, "ui", 123)
        self.pending()
        response = self.server.routes[("GET", JOB + "123/api/json")][2]
        del response["actions"][0]["executions"][0]["input"]["parameters"]
        with self.assertRaises(KeyError):
            api.approve(self.client, "ui", 123, "deploy")
        self.assertFalse(self.posts())

    def test_auth_failure_never_posts(self):
        self.route("GET", JOB + "api/json", b"do not expose this response", 403)
        with self.assertRaises(api.ApiError) as caught:
            api.preflight(self.client, "ui", {})
        self.assertIn("HTTP 403", str(caught.exception))
        self.assertNotIn("expose", str(caught.exception))
        self.assertFalse(self.posts())

    def test_cli_requires_approval_before_credentials_or_network(self):
        with patch.object(sys, "argv", ["jenkins_api.py", "trigger", "ui"]):
            with patch("sys.stderr"):
                with self.assertRaises(SystemExit):
                    api.parse_args()
        self.assertFalse(self.server.requests)

    def test_deploy_tracks_queue_approves_once_and_emits_no_idle_events(self):
        self.pending()
        self.route("GET", "/queue/item/321/api/json", {
            "id": 321, "task": {"name": "prod-jl-storepal-ui"}, "executable": {"number": 123}})
        waiting = {"number": 123, "queueId": 321, "building": True, "pending_inputs": []}
        approval = {**waiting, "pending_inputs": [{"id": "deploy", "message": next(iter(api.DEPLOY_MESSAGES)),
                                                  "parameter_names": []}]}
        complete = {**waiting, "building": False, "result": "SUCCESS"}
        events = []
        with patch.object(api, "status", side_effect=[waiting, waiting, approval, approval, complete]), \
                patch.object(api, "approve", return_value={}) as approve, patch.object(api.time, "sleep"):
            result = api.Deployment(self.client, "ui", emit=events.append, wait_result=True).run()
        self.assertEqual(0, result)
        self.assertEqual(["QUEUED", "BUILDING", "APPROVED", "SUCCESS"], [e["state"] for e in events])
        self.assertEqual(1, len(self.posts()))
        approve.assert_called_once_with(self.client, "ui", 123, "deploy", verify=True)

    def test_default_deploy_returns_immediately_after_approval_while_build_is_running(self):
        self.pending()
        response = self.server.routes[("GET", JOB + "123/api/json")][2]
        response["queueId"] = 321
        response["actions"][0]["executions"][0]["input"]["message"] = next(iter(api.DEPLOY_MESSAGES))
        self.route("GET", "/queue/item/321/api/json", {
            "id": 321, "task": {"name": "prod-jl-storepal-ui"}, "executable": {"number": 123}})
        events = []
        with patch.object(api.time, "sleep", side_effect=AssertionError("Must not wait after approval")):
            result = api.Deployment(self.client, "ui", emit=events.append).run()
        self.assertEqual(0, result)
        self.assertEqual(["QUEUED", "BUILDING", "DEPLOYMENT_SUBMITTED"], [e["state"] for e in events])
        self.assertFalse(events[-1]["result_checked"])
        self.assertTrue(response["building"])
        reads = [r for r in self.server.requests if r[0] == "GET" and r[1] == JOB + "123/api/json"]
        self.assertEqual(2, len(reads))
        self.assertEqual(2, len(self.posts()))

    def test_failed_approval_is_not_reported_as_submitted_or_retried(self):
        self.pending()
        response = self.server.routes[("GET", JOB + "123/api/json")][2]
        response["actions"][0]["executions"][0]["input"]["message"] = next(iter(api.DEPLOY_MESSAGES))
        self.route("POST", JOB + "123/input/deploy/proceedEmpty", {}, 500)
        events = []
        with self.assertRaises(api.ApiError):
            api.Deployment(self.client, "ui", build=123, emit=events.append).run()
        self.assertNotIn("DEPLOYMENT_SUBMITTED", [e["state"] for e in events])
        self.assertEqual(1, len(self.posts()))

    def test_resume_does_not_trigger_and_new_approval_scope_stops(self):
        events = []
        pending = {"building": True, "pending_inputs": [{"id": "other", "message": "Deploy another environment?",
                                                         "parameter_names": []}]}
        with patch.object(api, "status", return_value=pending), patch.object(api, "approve") as approve:
            result = api.Deployment(self.client, "ui", build=123, emit=events.append).run()
        self.assertEqual(2, result)
        self.assertEqual("NEEDS_INPUT", events[-1]["state"])
        self.assertFalse(self.posts())
        approve.assert_not_called()

    def test_timeout_and_failure_never_report_success_or_retry(self):
        events = []
        with patch.object(api.time, "monotonic", side_effect=[0, 2]):
            result = api.Deployment(self.client, "ui", build=123, timeout=1, emit=events.append).run()
        self.assertEqual(1, result)
        self.assertEqual("TIMEOUT", events[-1]["state"])
        failed = {"building": False, "result": "FAILURE", "pending_inputs": []}
        with patch.object(api, "status", return_value=failed):
            result = api.Deployment(self.client, "ui", build=123, emit=events.append).run()
        self.assertEqual(1, result)
        self.assertEqual("FAILURE", events[-1]["state"])
        self.assertFalse(self.posts())

    def test_queue_mismatch_or_cancelled_queue_stops_without_approval(self):
        self.route("GET", "/queue/item/321/api/json", {
            "id": 321, "task": {"name": "prod-jl-storepal-ui"}, "executable": {"number": 123}})
        with patch.object(api, "status", return_value={"queueId": 999}), \
                patch.object(api, "approve") as approve:
            with self.assertRaises(api.ApiError):
                api.Deployment(self.client, "ui", emit=lambda event: None).run()
        self.assertEqual(1, len(self.posts()))
        approve.assert_not_called()
        self.route("GET", "/queue/item/321/api/json", {
            "id": 321, "task": {"name": "prod-jl-storepal-ui"}, "cancelled": True})
        with self.assertRaises(api.ApiError):
            api.Deployment(self.client, "ui", emit=lambda event: None).run()
        self.assertEqual(2, len(self.posts()))

    def test_deploy_requires_explicit_authorization(self):
        with patch.object(sys, "argv", ["jenkins_api.py", "deploy", "ui"]), patch("sys.stderr"):
            with self.assertRaises(SystemExit):
                api.parse_args()
        self.assertFalse(self.server.requests)


if __name__ == "__main__":
    unittest.main()
