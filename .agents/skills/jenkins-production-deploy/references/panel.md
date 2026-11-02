# Codex 顶部发布操作

项目配置 `.codex/environments/environment.toml` 定义四个独立操作：**发布 UI、发布 Web、发布 API、发布 Admin**。每个操作直接运行 `scripts/jenkins_action.py <目标> --confirmed`，在 Codex 集成终端输出状态；不打开独立软件、浏览器或发布面板，不调用模型。

点击即授权对应的固定 Jenkins 生产任务，使用 Jenkins 当前参数与代码版本，本地未提交代码不会上传。自动提交已约定的“确定部署”后结束，显示“已触发构建并确定部署”，不等最终上线成功。相同项目的并发点击会被进程锁拦截；错误、未知审批或超时不自动重试。

## 首次登录

认证优先使用 `JENKINS_USER` + `JENKINS_API_TOKEN` / `JENKINS_PASSWORD` 进程环境；其次读取当前 Windows 账户的凭据管理器，与之前面板保存的登录信息兼容。

未配置时，点击发布操作会在集成终端询问用户名与密码（不回显，仅用于本次操作，不保存）。若希望以后点击直接发布，在项目根目录的 Codex 集成终端执行一次：

```powershell
python -X utf8 -B .agents/skills/jenkins-production-deploy/scripts/jenkins_action.py --setup-login
```

按提示填写后存入 Windows 凭据管理器；设置过程不触发发布。使用 API Token 时在此命令后添加 `--token`。凭据目标名为 `ADM/Jenkins/172.25.197.86:7080`，不把真实凭据写入仓库、命令行参数或日志。

## 显示与验证

- 在 Codex 本地环境设置选择项目的 `ADM` 环境；若动作列表未刷新，重新打开项目。
- Action 的具体排列、按钮是否收纳进下拉菜单由 Codex 当前版本和窗口布局决定；此配置提供四个独立动作，不能通过项目配置强制改变客户端顶部布局。未在当前客户端实见四按钮时，不声称已验证四个按钮同时展开。
- 顶部入口已不引用 `open_release_panel.ps1` 或 `jenkins_panel.py`；旧面板文件保留，不在发布路径上。
- 发布按钮自身只执行本地脚本，无模型 Token；通过聊天让助手发布仍有对话用量。
- 验证仅做本机检查与离线模拟，不实际点击生产发布。

```powershell
python -X utf8 -B -m unittest discover -s .agents/skills/jenkins-production-deploy/tests -p 'test_*.py'
python -X utf8 -B .agents/skills/jenkins-production-deploy/scripts/jenkins_action.py ui --check
git diff --check -- .codex/environments/environment.toml .agents/skills/jenkins-production-deploy
```

`--check` 只报告本地凭据是否配置，不连接 Jenkins、不打开窗口，不验证密码正确性。Actions 的顶部入口和集成终端执行方式参见 [OpenAI 官方本地环境文档](https://learn.chatgpt.com/docs/environments/local-environment)。
