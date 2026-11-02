# 按需操作

正常发布使用会话助手。仅在以下场景读取本文件；无须读取整个脚本或测试文件。

## 常规调用与用量

- `await jenkins.run({target:'admin', auth})` 合并启动与等待，返回精简结果；未结束时仅调用 `await jenkins.next(id)`，不重复传凭据。
- 用户询问 Token 时，在 run 中加入 `usage:{homeDir:nodeRepl.homeDir,threadId:nodeRepl.requestMeta.threadId}`；已知准确日志路径也可用 `usage:{logPath:'当前会话JSONL路径'}`。仅读取明确标识的当前会话，统计与发布结果一起返回，不再发起单独统计工具调用。
- 用量仅为本轮截至工具返回时的本地计数快照，不含后续模型调用及最终回复，不代表账单或额度百分比。读取不到基线或日志时返回 `available:false`，如实说明，不估算为零。
- 不应声称技能能删减已有聊天历史或系统指令。减少工具轮次可减少重复带入上下文，但实际 Token 降幅取决于会话长度和缓存。
- 多目标按用户顺序逐个提交，未指定时采用 API、Admin、Web、UI；失败或不确定则暂停后续目标。前项必须最终成功才能继续时使用 `waitResult:true`。

## 参数、凭据与恢复

- `start({target:'web', params:{BRANCH:'master'}, auth})`：只在任务实际支持该参数且用户指定时传入。默认沿用任务配置，不包含本地未提交改动。
- `start({target:'web', build:82, auth})`：仅跟踪这个准确编号，不触发新构建。会话助手仍检查当前审批是否属于受支持的无参数生产部署确认。
- 默认以“确定部署”提交成功为完成，返回 `DEPLOYMENT_SUBMITTED` 和 `result_checked:false`，立即退出且不再轮询。只表示已提交，不保证最终构建或上线成功。
- 用户明确要求最终结果时，使用 `start({target:'web', waitResult:true, auth})`；CLI 对应 `deploy web --confirmed --wait-result`。已有构建需加 `build:82`（CLI 为 `--build 82`），避免触发新构建。
- `start({target:'web', timeout:1800, python:'已存在的Python路径', auth})`：可调整超时和现有解释器；不安装依赖。
- 环境认证为 `JENKINS_USER` + `JENKINS_API_TOKEN`，或 `JENKINS_USER` + `JENKINS_PASSWORD`。会话凭据由助手一次性通过 stdin 传入；不得保存到仓库、命令行或持久环境。
- 自动审批仅接受已核实消息“请确认是否继续部署到生产环境？”且无参数的 InputAction。新提示或参数返回 NEEDS_INPUT；核对目标/参数后使用下述单步接口，不盲目扩充消息白名单。
- 中断本地监控不会停止 Jenkins 构建。恢复时读取之前的事件获得准确 build 编号；仅有 queue_id 时先读取该队列，不能用 lastBuild 代替。

## 单步只读与异常定位

在仓库根目录通过现有 Python 执行；需要 stdin 认证时加 `--auth-stdin`，由进程工具发送凭据 JSON，不能拼入 Shell 命令：

```powershell
python -X utf8 -B .agents/skills/jenkins-production-deploy/scripts/jenkins_api.py status web --build 82
python -X utf8 -B .agents/skills/jenkins-production-deploy/scripts/jenkins_api.py queue web --queue-id 123
python -X utf8 -B .agents/skills/jenkins-production-deploy/scripts/jenkins_api.py preflight web
```

用户只要求查看时不触发发布。参数不支持、配置/认证读取失败、审批结构缺失、queueId 不匹配均报告为阻碍，不当作成功或空结果。POST 超时不自动重试，先通过 GET 核实。手动审批命令为 `approve web --build 82 --input-id '<实际ID>' --confirmed`，只用于当前用户已授权的目标和范围；不会跳过参数化审批。

不使用浏览器、不安装 wfapi 插件、不改变 CSRF、权限或 Job 配置。必要时仅截取并脱敏与失败相关的日志片段，避免输出完整配置与日志。

## 维护验证

```powershell
python -X utf8 -B -m unittest discover -s .agents/skills/jenkins-production-deploy/tests -p 'test_*.py'
node --test .agents/skills/jenkins-production-deploy/tests/test_session.mjs .agents/skills/jenkins-production-deploy/tests/test_usage.mjs
git diff --check -- .agents/skills/jenkins-production-deploy
```

测试使用本地模拟接口/进程，不访问生产。重点检查一次触发、精确队列关联、审批成功后立即退出、失败不报已提交、可选最终结果监控、凭据仅经 stdin 传递及异常不重试。

契约来源：[Jenkins Remote API](https://www.jenkins.io/doc/book/using/remote-access-api/)、[CSRF](https://www.jenkins.io/doc/book/security/csrf-protection/)、[Input Step](https://github.com/jenkinsci/pipeline-input-step-plugin/blob/master/src/main/java/org/jenkinsci/plugins/workflow/support/steps/input/InputStepExecution.java)。
