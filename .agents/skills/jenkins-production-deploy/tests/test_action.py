"""Offline tests for the four direct Codex actions. No production requests."""

from contextlib import nullcontext, redirect_stdout, redirect_stderr
import io
import json
import os
from pathlib import Path
import sys
import tomllib
import unittest
from unittest.mock import Mock, patch


ROOT = Path(__file__).resolve().parents[4]
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "scripts"))
import jenkins_action as action


AUTH = {"username": "offline-user", "kind": "password", "secret": "offline-secret"}


class DirectActionTests(unittest.TestCase):
    def test_four_actions_call_the_matching_deployment_without_a_panel(self):
        config = tomllib.loads((ROOT / ".codex/environments/environment.toml").read_text(encoding="utf-8"))
        self.assertEqual(len(config["actions"]), 4)
        for item, target in zip(config["actions"], action.JOBS):
            self.assertIn(target.lower(), item["name"].lower())
            self.assertIn("jenkins_action.py", item["command"])
            self.assertTrue(item["command"].endswith(f" {target} --confirmed"))
            self.assertNotIn("panel", item["command"])
            self.assertEqual(item["platform"], "win32")

    def test_local_check_does_not_deploy_or_prompt(self):
        with patch.object(action, "stored_auth", return_value=None), \
                patch.object(action, "deploy") as deploy, \
                patch.object(action, "prompt_auth") as prompt, redirect_stdout(io.StringIO()) as output:
            self.assertEqual(action.main(["ui", "--check"]), 0)
        self.assertEqual(json.loads(output.getvalue()), {"target": "ui", "credential_ready": False,
                                                       "jenkins_contacted": False, "gui_opened": False})
        deploy.assert_not_called()
        prompt.assert_not_called()

    def test_stored_auth_runs_once_and_clears_local_copy(self):
        auth = dict(AUTH)
        calls = []
        with patch.object(action, "stored_auth", return_value=auth), \
                patch.object(action, "target_lock", return_value=nullcontext()), \
                patch.object(action, "prompt_auth") as prompt, \
                patch.object(action, "deploy", side_effect=lambda target, value: calls.append((target, dict(value))) or 0):
            self.assertEqual(action.main(["admin", "--confirmed"]), 0)
        self.assertEqual(calls, [("admin", AUTH)])
        self.assertEqual(auth, {})
        prompt.assert_not_called()

    def test_deploy_uses_known_approval_and_does_not_wait_for_final_result(self):
        client = Mock()
        client.redact.side_effect = lambda event: event
        deployment = Mock(run=Mock(return_value=0))
        factory = Mock(return_value=deployment)
        with redirect_stdout(io.StringIO()) as output:
            self.assertEqual(action.deploy("web", AUTH, Mock(return_value=client), factory), 0)
            factory.call_args.kwargs["emit"]({"state": "DEPLOYMENT_SUBMITTED", "build": 999})
        self.assertFalse(factory.call_args.kwargs["wait_result"])
        self.assertIn("已触发构建并确定部署", output.getvalue())
        factory.assert_called_once()
        deployment.run.assert_called_once()
        self.assertEqual(AUTH["secret"], "offline-secret")

    def test_errors_redact_secret_and_are_not_retried(self):
        factory = Mock(side_effect=action.ApiError("failed offline-secret"))
        with self.assertRaisesRegex(action.ApiError, r"failed \[redacted\]"):
            action.deploy("api", AUTH, Mock(return_value=Mock()), factory)
        factory.assert_called_once()

    def test_environment_auth_uses_token_and_skips_store(self):
        store_factory = Mock()
        with patch.dict(os.environ, {"JENKINS_USER": "offline-user", "JENKINS_API_TOKEN": "offline-token"}):
            auth = action.stored_auth(store_factory)
        self.assertEqual(auth, {"username": "offline-user", "secret": "offline-token", "kind": "token"})
        store_factory.assert_not_called()

    def test_saved_login_is_shared_with_existing_windows_store(self):
        store = Mock(load=Mock(return_value=AUTH))
        with patch.dict(os.environ, {name: "" for name in ("JENKINS_USER", "JENKINS_PASSWORD", "JENKINS_API_TOKEN")}):
            self.assertEqual(action.stored_auth(Mock(return_value=store)), AUTH)
        store.load.assert_called_once()

    def test_unsafe_echo_fallback_is_rejected(self):
        with patch("builtins.input", return_value="offline-user"), \
                patch.object(action.getpass, "getpass", side_effect=action.getpass.GetPassWarning):
            with self.assertRaises(action.CredentialError):
                action.prompt_auth()

    def test_setup_only_saves_login_and_cannot_deploy(self):
        auth = dict(AUTH)
        store = Mock()
        saved = []
        store.save.side_effect = lambda value: saved.append(dict(value))
        with patch.object(action, "prompt_auth", return_value=auth), \
                patch.object(action, "WindowsCredentials", return_value=store), \
                patch.object(action, "deploy") as deploy, redirect_stdout(io.StringIO()):
            self.assertEqual(action.main(["--setup-login"]), 0)
        self.assertEqual(saved, [AUTH])
        self.assertEqual(auth, {})
        deploy.assert_not_called()

    def test_unconfirmed_execution_and_combined_setup_are_rejected(self):
        for argv in (["ui"], ["ui", "--setup-login", "--confirmed"], ["ui", "--confirmed", "--token"]):
            with redirect_stderr(io.StringIO()), self.assertRaises(SystemExit):
                action.parse_args(argv)

    @unittest.skipUnless(os.name == "nt", "Windows mutex contract")
    def test_repeated_click_is_blocked_and_lock_released(self):
        with action.target_lock("ui"):
            with self.assertRaisesRegex(action.ApiError, "重复点击"):
                with action.target_lock("ui"):
                    self.fail("Second operation must not enter the deployment block")
        with action.target_lock("ui"):
            pass


if __name__ == "__main__":
    unittest.main()
