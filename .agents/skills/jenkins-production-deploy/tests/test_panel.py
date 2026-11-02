"""Offline panel tests; no Jenkins requests or production credentials."""

import os
from pathlib import Path
import sys
import tkinter as tk
import unittest
from unittest.mock import Mock, patch
import uuid


SCRIPTS = Path(__file__).resolve().parents[1] / "scripts"
sys.path.insert(0, str(SCRIPTS))
import jenkins_credentials as credentials
import jenkins_panel as panel


AUTH = {"username": "offline-user", "kind": "password", "secret": "offline-secret"}


class ControllerTests(unittest.TestCase):
    def test_release_approves_without_waiting_for_final_result(self):
        client = Mock()
        client.redact.side_effect = lambda event: event
        deployment = Mock()
        deployment.run.return_value = 0
        factory = Mock(return_value=deployment)
        controller = panel.ReleaseController(Mock(return_value=client), factory)
        self.assertTrue(controller.start("ui", AUTH))
        controller.worker.join(2)
        self.assertFalse(controller.worker.is_alive())
        self.assertFalse(controller.start("ui", AUTH))
        factory.assert_called_once()
        self.assertFalse(factory.call_args.kwargs["wait_result"])
        self.assertEqual(controller.events.get_nowait(), {"state": "FINISHED", "exit_code": 0})
        self.assertEqual(AUTH["secret"], "offline-secret")

    def test_failure_redacts_credentials_and_never_retries(self):
        client = Mock()
        factory = Mock(side_effect=panel.ApiError("failed with offline-secret"))
        controller = panel.ReleaseController(Mock(return_value=client), factory)
        controller.start("admin", AUTH)
        controller.worker.join(2)
        event = controller.events.get_nowait()
        self.assertEqual(event, {"state": "ERROR", "error": "failed with [redacted]"})
        self.assertEqual(controller.events.get_nowait()["exit_code"], 1)
        factory.assert_called_once()

    def test_invalid_target_or_empty_secret_cannot_start_worker(self):
        factory = Mock()
        controller = panel.ReleaseController(factory)
        with self.assertRaises(ValueError):
            controller.start("unknown", AUTH)
        with self.assertRaises(credentials.CredentialError):
            controller.start("ui", {**AUTH, "secret": ""})
        self.assertIsNone(controller.worker)
        factory.assert_not_called()

    def test_password_selection_cannot_accidentally_use_environment_token(self):
        factory = Mock(return_value=Mock())
        deployment = Mock(return_value=Mock(run=Mock(return_value=0)))
        controller = panel.ReleaseController(factory, deployment)
        controller.start("web", AUTH)
        controller.worker.join(2)
        self.assertEqual(factory.call_args.kwargs["token"], "")


class PanelTests(unittest.TestCase):
    def setUp(self):
        self.env = patch.dict(os.environ, {name: "" for name in (
            "JENKINS_USER", "JENKINS_PASSWORD", "JENKINS_API_TOKEN")})
        self.env.start()
        self.root = tk.Tk()
        self.root.withdraw()
        self.store = Mock()
        self.store.load.return_value = None
        self.factory = Mock()
        self.controller = panel.ReleaseController(self.factory)
        self.ui = panel.ReleasePanel(self.root, self.controller, self.store)

    def tearDown(self):
        self.root.destroy()
        self.env.stop()

    def test_opening_is_offline_and_does_not_save_credentials(self):
        self.root.update_idletasks()
        self.assertEqual([button.cget("text") for button in self.ui.buttons],
                         ["发布 UI", "发布 WEB", "发布 API", "发布 ADMIN"])
        self.assertTrue(self.ui.secret_entry.cget("show"))
        self.factory.assert_not_called()
        self.store.save.assert_not_called()
        self.assertIsNone(self.controller.worker)

    def test_missing_auth_click_does_not_trigger_release(self):
        self.ui.buttons[0].invoke()
        self.assertIn("请填写", self.ui.auth_status.get())
        self.assertFalse(self.controller.busy)
        self.factory.assert_not_called()

    def test_submission_is_done_without_claiming_final_success(self):
        self.ui.target = "ui"
        self.ui._busy(True)
        self.assertIn("disabled", self.ui.buttons[0].state())
        self.ui._event({"state": "DEPLOYMENT_SUBMITTED", "build": 999})
        self.ui._event({"state": "FINISHED", "exit_code": 0})
        self.assertFalse(self.controller.busy)
        self.assertNotIn("disabled", self.ui.buttons[0].state())
        self.assertEqual(self.ui.status_text.get(), "已触发构建并确定部署")
        self.assertIn("未等待最终上线结果", self.ui.detail_text.get())
        self.assertTrue(self.ui.build_url.endswith("/job/prod-jl-storepal-ui/999/"))

    def test_busy_window_close_keeps_worker_and_minimizes(self):
        self.controller.busy = True
        with patch.object(panel.messagebox, "showinfo"), patch.object(self.root, "iconify") as minimize:
            self.ui._close()
        minimize.assert_called_once()
        self.assertTrue(self.root.winfo_exists())

    def test_persistence_only_happens_on_explicit_save(self):
        self.ui.username.set(AUTH["username"])
        self.ui.secret.set(AUTH["secret"])
        self.store.save.assert_not_called()
        self.ui._save_auth()
        self.store.save.assert_called_once_with(AUTH)
        self.ui._forget_auth()
        self.store.delete.assert_called_once()
        self.assertEqual(self.ui.secret.get(), "")

    def test_saved_auth_is_loaded_without_network(self):
        self.store.load.return_value = AUTH
        self.ui._load_auth()
        self.assertEqual(self.ui._auth(), AUTH)
        self.factory.assert_not_called()


@unittest.skipUnless(os.name == "nt", "Windows Credential Manager contract")
class WindowsContractTests(unittest.TestCase):
    def test_credential_store_round_trip_uses_only_a_temporary_fake_credential(self):
        target = "ADM/Jenkins/OfflineTest-" + uuid.uuid4().hex
        with patch.object(credentials, "TARGET", target):
            store = credentials.WindowsCredentials()
            self.assertIsNone(store.load())
            try:
                store.save(AUTH)
                self.assertEqual(store.load(), AUTH)
            finally:
                store.delete()
            self.assertIsNone(store.load())

    def test_multiple_panel_instances_are_detected(self):
        first = panel.SingleInstance()
        second = panel.SingleInstance()
        try:
            self.assertTrue(second.exists)
        finally:
            second.close()
            first.close()


if __name__ == "__main__":
    unittest.main()
