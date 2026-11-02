"""Codex top-bar actions: run a fixed Jenkins deployment in the integrated terminal."""

import argparse
from contextlib import contextmanager
import ctypes
from ctypes import wintypes
import getpass
import json
import os
import sys
import warnings

from jenkins_api import ApiError, Client, Deployment, JOBS
from jenkins_credentials import CredentialError, WindowsCredentials, validate_auth


MESSAGES = {"QUEUED": "已提交构建，等待队列", "BUILDING": "构建中，等待部署确认",
            "DEPLOYMENT_SUBMITTED": "已触发构建并确定部署，未等待最终上线结果",
            "NEEDS_INPUT": "遇到新的审批表单，请在 Jenkins 核实", "TIMEOUT": "等待超时，请核实构建，勿重复提交"}


def stored_auth(store_factory=WindowsCredentials):
    """Read existing authentication without opening a UI or contacting Jenkins."""
    username = os.environ.get("JENKINS_USER", "")
    token = os.environ.get("JENKINS_API_TOKEN", "")
    secret = token or os.environ.get("JENKINS_PASSWORD", "")
    if username and secret:
        return validate_auth({"username": username, "kind": "token" if token else "password", "secret": secret})
    return store_factory().load()


def prompt_auth(token=False):
    """Read a secret only with terminal echo disabled; never fall back to visible input."""
    username = input("Jenkins 用户名 [xiehaitao]: ").strip() or "xiehaitao"
    label = "Jenkins API Token" if token else "Jenkins 密码"
    with warnings.catch_warnings():
        warnings.simplefilter("error", getpass.GetPassWarning)
        try:
            secret = getpass.getpass(label + "（不回显）: ")
        except getpass.GetPassWarning:
            raise CredentialError("当前终端不能安全隐藏输入，请使用 Windows 凭据管理器或受保护的环境变量。") from None
    return validate_auth({"username": username, "secret": secret, "kind": "token" if token else "password"})


@contextmanager
def target_lock(target):
    """Block repeated clicks for the same job across concurrent terminal actions."""
    if target not in JOBS:
        raise ApiError("不支持的发布目标。")
    api = ctypes.WinDLL("kernel32", use_last_error=True)
    api.CreateMutexW.argtypes = [wintypes.LPVOID, wintypes.BOOL, wintypes.LPCWSTR]
    api.CreateMutexW.restype = wintypes.HANDLE
    api.CloseHandle.argtypes = [wintypes.HANDLE]
    handle = api.CreateMutexW(None, False, "Local\\ADMJenkinsDirectRelease_" + target)
    exists = ctypes.get_last_error() == 183
    if not handle:
        raise ApiError("无法创建发布锁，没有触发构建。")
    try:
        if exists:
            raise ApiError("该项目的发布操作仍在运行，已拦截重复点击。")
        yield
    finally:
        api.CloseHandle(handle)


def deploy(target, auth, client_factory=Client, deployment_factory=Deployment):
    """Submit the requested build and its known approval, then stop immediately."""
    auth = validate_auth(auth)
    try:
        client = client_factory(username=auth["username"], secret=auth["secret"],
                                token=auth["secret"] if auth["kind"] == "token" else "")

        def emit(event):
            result = {"message": MESSAGES.get(event["state"], event["state"]), **event}
            print(json.dumps(client.redact(result), ensure_ascii=False), flush=True)

        return deployment_factory(client, target, emit=emit, wait_result=False).run()
    except ApiError as error:
        raise ApiError(str(error).replace(auth["secret"], "[redacted]")) from None
    finally:
        auth.clear()


def parse_args(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("target", nargs="?", choices=JOBS)
    parser.add_argument("--confirmed", action="store_true", help="An explicit click authorizes this target.")
    parser.add_argument("--check", action="store_true", help="Check local authentication only; no Jenkins requests.")
    parser.add_argument("--setup-login", action="store_true", help="Save terminal-entered credentials to Windows Credential Manager; no deployment.")
    parser.add_argument("--token", action="store_true", help="Use an API Token when setting up login.")
    args = parser.parse_args(argv)
    if args.token and not args.setup_login:
        parser.error("--token is only used with --setup-login.")
    if args.setup_login and (args.target or args.check or args.confirmed):
        parser.error("Login setup cannot be combined with a deployment or check.")
    if not args.setup_login and not args.target:
        parser.error("A target is required.")
    if not args.setup_login and not args.check and not args.confirmed:
        parser.error("Deployment requires an explicit top-bar click / --confirmed.")
    return args


def main(argv=None):
    args = parse_args(argv)
    if args.setup_login:
        auth = prompt_auth(args.token)
        try:
            WindowsCredentials().save(auth)
        finally:
            auth.clear()
        print("登录信息已保存到 Windows 凭据管理器，没有触发发布。")
        return 0
    if args.check:
        print(json.dumps({"target": args.target, "credential_ready": bool(stored_auth()),
                          "jenkins_contacted": False, "gui_opened": False}))
        return 0
    with target_lock(args.target):
        auth = stored_auth()
        if auth is None:
            print("尚未配置登录信息，本次在终端输入；不会保存。可单独运行 --setup-login 保存供以后直接点击。")
            auth = prompt_auth()
        try:
            return deploy(args.target, auth)
        finally:
            auth.clear()


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (ApiError, CredentialError) as error:
        print(json.dumps({"state": "ERROR", "error": str(error)}, ensure_ascii=False), file=sys.stderr)
        sys.exit(1)
    except (EOFError, KeyboardInterrupt):
        print("操作中断；若已提交构建，请核实 Jenkins，勿重复点击。", file=sys.stderr)
        sys.exit(130)
    except Exception:
        print("操作异常；请核实 Jenkins 队列和构建，未自动重试。", file=sys.stderr)
        sys.exit(1)
