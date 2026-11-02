"""Local desktop release panel. Opening the panel never contacts Jenkins."""

import ctypes
from ctypes import wintypes
import os
import queue
import threading
import tkinter as tk
from tkinter import messagebox, ttk
import webbrowser

from jenkins_api import ApiError, BASE_URL, Client, Deployment, JOBS, job_path
from jenkins_credentials import CredentialError, WindowsCredentials, validate_auth


TITLE = "ADM · Jenkins 发布"
TARGET_LABELS = {"ui": "管理前端", "web": "业务前端", "api": "API 服务", "admin": "Admin 服务"}
STATES = {"QUEUED": "已提交，等待 Jenkins 分配构建", "BUILDING": "构建中，等待部署确认",
          "DEPLOYMENT_SUBMITTED": "已触发构建并确定部署", "NEEDS_INPUT": "遇到新的审批表单，请打开构建页处理",
          "TIMEOUT": "等待超时，请打开构建页核实", "SUCCESS": "构建成功结束（未遇到部署确认）",
          "FAILURE": "构建失败", "ABORTED": "构建已中止", "UNSTABLE": "构建结果不稳定"}


class ReleaseController:
    """Keep HTTP work off the UI thread and block overlapping button clicks."""

    def __init__(self, client_factory=Client, deployment_factory=Deployment):
        self.events = queue.Queue()
        self.client_factory = client_factory
        self.deployment_factory = deployment_factory
        self.busy = False
        self.worker = None

    def start(self, target, auth):
        if target not in JOBS:
            raise ValueError("不支持的发布目标。")
        if self.busy:
            return False
        auth = validate_auth(auth)
        self.busy = True
        self.worker = threading.Thread(target=self._release, args=(target, auth), daemon=False)
        self.worker.start()
        return True

    def _release(self, target, auth):
        exit_code = 1
        client = None
        try:
            client = self.client_factory(username=auth["username"], secret=auth["secret"],
                                         token=auth["secret"] if auth["kind"] == "token" else "")
            emit = lambda event: self.events.put(client.redact(event))
            deployment = self.deployment_factory(client, target, emit=emit, wait_result=False)
            exit_code = deployment.run()
        except ApiError as error:
            message = str(error).replace(auth["secret"], "[redacted]")
            self.events.put({"state": "ERROR", "error": message})
        except Exception:
            self.events.put({"state": "ERROR", "error": "发布异常；请核实队列和构建，勿重复提交。"})
        finally:
            auth.clear()
            self.events.put({"state": "FINISHED", "exit_code": exit_code})


class SingleInstance:
    """Avoid competing panel processes on this Windows login session."""

    def __init__(self):
        self.api = ctypes.WinDLL("kernel32", use_last_error=True)
        self.api.CreateMutexW.argtypes = [wintypes.LPVOID, wintypes.BOOL, wintypes.LPCWSTR]
        self.api.CreateMutexW.restype = wintypes.HANDLE
        self.api.CloseHandle.argtypes = [wintypes.HANDLE]
        self.handle = self.api.CreateMutexW(None, False, "Local\\ADMJenkinsReleasePanel")
        if not self.handle:
            raise OSError("无法创建发布面板锁。")
        self.exists = ctypes.get_last_error() == 183

    def close(self):
        if self.handle:
            self.api.CloseHandle(self.handle)
            self.handle = None

    def focus_existing(self):
        user32 = ctypes.WinDLL("user32", use_last_error=True)
        user32.FindWindowW.argtypes = [wintypes.LPCWSTR, wintypes.LPCWSTR]
        user32.FindWindowW.restype = wintypes.HWND
        user32.ShowWindow.argtypes = [wintypes.HWND, ctypes.c_int]
        user32.SetForegroundWindow.argtypes = [wintypes.HWND]
        window = user32.FindWindowW(None, TITLE)
        if window:
            user32.ShowWindow(window, 9)
            user32.SetForegroundWindow(window)
        return bool(window)


class ReleasePanel:
    """Four fixed production targets and explicit, optional credential storage."""

    def __init__(self, root, controller=None, store=None):
        self.root = root
        self.controller = controller or ReleaseController()
        self.store = store or WindowsCredentials()
        self.last_event = {}
        self.target = None
        self.build_url = None
        self.buttons = []
        self.auth_controls = []
        self.username = tk.StringVar(value=os.environ.get("JENKINS_USER", "xiehaitao"))
        token = os.environ.get("JENKINS_API_TOKEN", "")
        self.secret = tk.StringVar(value=token or os.environ.get("JENKINS_PASSWORD", ""))
        self.kind = tk.StringVar(value="token" if token else "password")
        self.status_text = tk.StringVar(value="准备就绪，选择要发布的项目")
        self.detail_text = tk.StringVar(value="点击即触发生产构建，并自动提交已约定的部署确认。")
        self.auth_status = tk.StringVar(value="登录信息仅保留在本窗口；可主动保存到 Windows 凭据管理器。")
        self._configure()
        self._layout()
        self._load_auth()
        self.root.protocol("WM_DELETE_WINDOW", self._close)
        self.root.after(100, self._drain)

    def _configure(self):
        self.root.title(TITLE)
        self.root.geometry("840x810")
        self.root.minsize(810, 780)
        self.root.configure(bg="#f4f6f8")
        style = ttk.Style(self.root)
        style.theme_use("clam")
        style.configure("TFrame", background="#f4f6f8")
        style.configure("Card.TFrame", background="white")
        style.configure("TLabel", background="#f4f6f8", foreground="#243047", font=("Microsoft YaHei UI", 10))
        style.configure("Card.TLabel", background="white")
        style.configure("Title.TLabel", font=("Microsoft YaHei UI", 21, "bold"))
        style.configure("TButton", font=("Microsoft YaHei UI", 10), padding=(12, 7))
        style.configure("Deploy.TButton", background="#087f5b", foreground="white", borderwidth=0)
        style.map("Deploy.TButton", background=[("disabled", "#c6cdd5"), ("active", "#056448")])
        style.configure("TEntry", padding=6, font=("Microsoft YaHei UI", 10))
        style.configure("TCombobox", padding=6, font=("Microsoft YaHei UI", 10))

    def _layout(self):
        body = ttk.Frame(self.root, padding=24)
        body.pack(fill="both", expand=True)
        header = ttk.Frame(body)
        header.pack(fill="x")
        ttk.Label(header, text="项目发布", style="Title.TLabel").pack(side="left")
        ttk.Label(header, text="●  生产环境", foreground="#c13535").pack(side="right")
        ttk.Label(body, text="Jenkins API  ·  本地执行  ·  不调用模型", foreground="#64748b").pack(anchor="w", pady=(6, 18))
        self._credentials(body)
        cards = ttk.Frame(body)
        cards.pack(fill="x", pady=(18, 18))
        for index, target in enumerate(JOBS):
            self._card(cards, target, index)
        self._result(body)
        ttk.Label(body, text="发布 Jenkins 任务配置的代码版本；本地未提交代码不会被上传。",
                  foreground="#64748b").pack(anchor="w", pady=(16, 0))

    def _credentials(self, body):
        frame = ttk.Frame(body, style="Card.TFrame", padding=14)
        frame.pack(fill="x")
        ttk.Label(frame, text="Jenkins 登录", style="Card.TLabel").grid(row=0, column=0, sticky="w", pady=(0, 8))
        username = ttk.Entry(frame, textvariable=self.username, width=19)
        username.grid(row=1, column=0, sticky="ew", padx=(0, 8))
        kind = ttk.Combobox(frame, values=("password", "token"), textvariable=self.kind, state="readonly", width=11)
        kind.grid(row=1, column=1, padx=(0, 8))
        self.secret_entry = ttk.Entry(frame, textvariable=self.secret, show="●", width=27)
        self.secret_entry.grid(row=1, column=2, sticky="ew")
        save = ttk.Button(frame, text="保存登录", command=self._save_auth)
        save.grid(row=1, column=3, padx=(8, 0))
        forget = ttk.Button(frame, text="忘记", command=self._forget_auth)
        forget.grid(row=1, column=4, padx=(8, 0))
        ttk.Label(frame, textvariable=self.auth_status, style="Card.TLabel", foreground="#64748b",
                  wraplength=700).grid(row=2, column=0, columnspan=5, sticky="w", pady=(10, 0))
        frame.columnconfigure(2, weight=1)
        self.auth_controls.extend((username, self.secret_entry, save, forget))
        self.kind_control = kind

    def _card(self, parent, target, index):
        row, column = divmod(index, 2)
        frame = ttk.Frame(parent, style="Card.TFrame", padding=16)
        frame.grid(row=row, column=column, sticky="nsew", padx=(0 if column == 0 else 12, 0), pady=(0, 12))
        parent.columnconfigure(column, weight=1, uniform="cards")
        ttk.Label(frame, text="ruoyi-" + target, style="Card.TLabel",
                  font=("Microsoft YaHei UI", 14, "bold")).pack(anchor="w")
        ttk.Label(frame, text=TARGET_LABELS[target], style="Card.TLabel", foreground="#64748b").pack(anchor="w", pady=(3, 10))
        button = ttk.Button(frame, text="发布 " + target.upper(), style="Deploy.TButton",
                            command=lambda: self._publish(target))
        button.pack(fill="x")
        self.buttons.append(button)

    def _result(self, body):
        result = ttk.Frame(body)
        result.pack(fill="x")
        ttk.Label(result, textvariable=self.status_text, font=("Microsoft YaHei UI", 12, "bold")).pack(anchor="w")
        ttk.Label(result, textvariable=self.detail_text, wraplength=720, foreground="#64748b").pack(anchor="w", pady=(6, 10))
        self.progress = ttk.Progressbar(result, mode="indeterminate")
        self.progress.pack(fill="x")
        self.link = ttk.Button(result, text="打开 Jenkins 构建页", command=self._open_build, state="disabled")
        self.link.pack(anchor="e", pady=(8, 0))

    def _auth(self):
        return validate_auth({"username": self.username.get(), "secret": self.secret.get(), "kind": self.kind.get()})

    def _load_auth(self):
        if self.secret.get():
            self.auth_status.set("已使用进程环境中的登录信息。")
            return
        try:
            auth = self.store.load()
            if auth:
                self.username.set(auth["username"])
                self.secret.set(auth["secret"])
                self.kind.set(auth["kind"])
                self.auth_status.set("已从 Windows 凭据管理器加载，可直接点击发布。")
        except CredentialError as error:
            self.auth_status.set(str(error))

    def _save_auth(self):
        try:
            self.store.save(self._auth())
            self.auth_status.set("已保存到当前 Windows 账户的凭据管理器，下次自动加载。")
        except CredentialError as error:
            self.auth_status.set(str(error))

    def _forget_auth(self):
        try:
            self.store.delete()
            self.secret.set("")
            self.auth_status.set("已删除保存的登录信息。")
        except CredentialError as error:
            self.auth_status.set(str(error))

    def _publish(self, target):
        try:
            if not self.controller.start(target, self._auth()):
                return
        except (CredentialError, ValueError) as error:
            self.auth_status.set(str(error))
            self.secret_entry.focus_set()
            return
        self.target = target
        self.last_event = {}
        self.build_url = BASE_URL + job_path(target)
        self.link.configure(state="normal")
        self.status_text.set("正在提交 ruoyi-" + target)
        self.detail_text.set("等待队列与部署确认；请保持面板运行，可以最小化。")
        self._busy(True)

    def _busy(self, active):
        self.controller.busy = active
        for widget in self.buttons + self.auth_controls:
            widget.configure(state="disabled" if active else "normal")
        self.kind_control.configure(state="disabled" if active else "readonly")
        if active:
            self.progress.start(12)
        else:
            self.progress.stop()

    def _drain(self):
        try:
            while True:
                self._event(self.controller.events.get_nowait())
        except queue.Empty:
            pass
        self.root.after(100, self._drain)

    def _event(self, event):
        state = event["state"]
        if state == "FINISHED":
            self._busy(False)
            if event["exit_code"] != 0 and self.last_event.get("state") != "ERROR":
                self.detail_text.set("请打开 Jenkins 核实；未自动重试，也未回滚。")
            return
        self.last_event = event
        if isinstance(event.get("build"), int) and event["build"] > 0:
            self.build_url = BASE_URL + job_path(self.target) + str(event["build"]) + "/"
        self.status_text.set(STATES.get(state, "发布未完成"))
        if state == "DEPLOYMENT_SUBMITTED":
            self.detail_text.set(f"ruoyi-{self.target} #{event['build']} · 已提交部署确认，未等待最终上线结果。")
        elif state == "ERROR":
            self.detail_text.set(event["error"])
        elif event.get("build"):
            self.detail_text.set(f"ruoyi-{self.target} #{event['build']} · {STATES.get(state, state)}")
        elif event.get("queue_id"):
            self.detail_text.set(f"ruoyi-{self.target} · 队列 #{event['queue_id']}")

    def _open_build(self):
        if self.build_url:
            webbrowser.open(self.build_url)

    def _close(self):
        if self.controller.busy:
            messagebox.showinfo("发布仍在处理中", "面板将最小化并继续提交部署确认，请勿结束进程。", parent=self.root)
            self.root.iconify()
            return
        self.secret.set("")
        self.root.destroy()


def main():
    root = tk.Tk()
    root.withdraw()
    instance = SingleInstance()
    try:
        if instance.exists:
            if not instance.focus_existing():
                messagebox.showinfo(TITLE, "发布面板正在启动，请稍候。", parent=root)
            root.destroy()
            return
        ReleasePanel(root)
        root.deiconify()
        root.mainloop()
    finally:
        instance.close()


if __name__ == "__main__":
    main()
