"""Windows Credential Manager storage shared by direct actions and the local panel."""

import ctypes
from ctypes import wintypes
import json
import os


TARGET = "ADM/Jenkins/172.25.197.86:7080"


class CredentialError(Exception):
    """A credential-store error without secret content."""


class Credential(ctypes.Structure):
    _fields_ = [
        ("Flags", wintypes.DWORD), ("Type", wintypes.DWORD),
        ("TargetName", wintypes.LPWSTR), ("Comment", wintypes.LPWSTR),
        ("LastWritten", wintypes.FILETIME), ("CredentialBlobSize", wintypes.DWORD),
        ("CredentialBlob", ctypes.POINTER(wintypes.BYTE)), ("Persist", wintypes.DWORD),
        ("AttributeCount", wintypes.DWORD), ("Attributes", wintypes.LPVOID),
        ("TargetAlias", wintypes.LPWSTR), ("UserName", wintypes.LPWSTR),
    ]


def validate_auth(auth):
    """Accept a username and one explicitly selected authentication method."""
    if (not isinstance(auth, dict) or auth.get("kind") not in ("password", "token")
            or not isinstance(auth.get("username"), str) or not auth["username"].strip()
            or not isinstance(auth.get("secret"), str) or not auth["secret"]):
        raise CredentialError("请填写用户名和密码 / API Token。")
    return {"username": auth["username"].strip(), "kind": auth["kind"], "secret": auth["secret"]}


class WindowsCredentials:
    """Store a generic credential for this fixed Jenkins on the current Windows account."""

    def __init__(self):
        if os.name != "nt":
            raise CredentialError("此凭据存储仅支持 Windows。")
        self.api = ctypes.WinDLL("Advapi32.dll", use_last_error=True)
        pointer = ctypes.POINTER(Credential)
        self.api.CredReadW.argtypes = [wintypes.LPCWSTR, wintypes.DWORD,
                                       wintypes.DWORD, ctypes.POINTER(pointer)]
        self.api.CredReadW.restype = wintypes.BOOL
        self.api.CredWriteW.argtypes = [pointer, wintypes.DWORD]
        self.api.CredWriteW.restype = wintypes.BOOL
        self.api.CredDeleteW.argtypes = [wintypes.LPCWSTR, wintypes.DWORD, wintypes.DWORD]
        self.api.CredDeleteW.restype = wintypes.BOOL
        self.api.CredFree.argtypes = [wintypes.LPVOID]
        self.api.CredFree.restype = None

    def load(self):
        pointer = ctypes.POINTER(Credential)()
        if not self.api.CredReadW(TARGET, 1, 0, ctypes.byref(pointer)):
            if ctypes.get_last_error() == 1168:
                return None
            raise CredentialError("读取 Windows 凭据失败，请手动填写。")
        try:
            item = pointer.contents
            raw = ctypes.string_at(item.CredentialBlob, item.CredentialBlobSize)
            auth = json.loads(raw.decode("utf-8"))
            auth["username"] = item.UserName
            return validate_auth(auth)
        except (ValueError, TypeError, KeyError, UnicodeError):
            raise CredentialError("已保存的登录信息格式无效，请重新保存。") from None
        finally:
            self.api.CredFree(pointer)

    def save(self, auth):
        auth = validate_auth(auth)
        raw = json.dumps({"kind": auth["kind"], "secret": auth["secret"]}).encode("utf-8")
        if len(raw) > 2560:
            raise CredentialError("凭据过长，无法保存。")
        blob = (wintypes.BYTE * len(raw)).from_buffer_copy(raw)
        item = Credential(Type=1, TargetName=TARGET, UserName=auth["username"],
                          CredentialBlobSize=len(raw), CredentialBlob=blob, Persist=2)
        if not self.api.CredWriteW(ctypes.byref(item), 0):
            raise CredentialError("保存 Windows 凭据失败。")

    def delete(self):
        if not self.api.CredDeleteW(TARGET, 1, 0) and ctypes.get_last_error() != 1168:
            raise CredentialError("删除 Windows 凭据失败。")
