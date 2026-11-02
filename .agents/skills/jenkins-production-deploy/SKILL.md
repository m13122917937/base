---
name: jenkins-production-deploy
description: Jenkins API 发布 ui/web/api/admin，确定部署后结束；单次调用、精简结果，可同时统计 Token 快照。
---

# 发布

- 固定 Jenkins `http://172.25.197.86:7080/`；`ruoyi-ui/web/api/admin` 对应 `prod-jl-storepal-ui/web/api/admin`。用户已授权明确发布指令直接执行，免独立预检和二次确认；不扩大环境/任务，不改配置、权限或数据库。
- 正常只调用下述助手；已导入且未修改时复用，不重读技能/源码/测试、不重复发现工具、不操作浏览器。

```javascript
var jenkins = await import('file:///D:/project/cj/df-bak/.agents/skills/jenkins-production-deploy/scripts/jenkins_session.mjs');
nodeRepl.write(await jenkins.run({target:'admin'}));
```

- 凭据优先环境变量；已授权凭据通过 `auth:{username,password}` 或 `auth:{username,token}` 仅传一次。助手通过 stdin 送入子进程，不写文件/命令行/持久环境，不回显。
- 未完成只调用 `jenkins.next(返回的id)`；外层 exec 等待设 60 秒，Node 调用设 50 秒。内部自动排队和审批，只有状态变化才输出。
- DEPLOYMENT_SUBMITTED 且 exit_code=0 后结束，报告“已触发构建并确定部署”及链接，不声称上线成功。ERROR/NEEDS_INPUT/TIMEOUT 不算完成，不自动重试或回滚。仅用户要求最终结果时加 `waitResult:true`。
- 仅用户询问 Token 时，在 run 中加 `usage:{homeDir:nodeRepl.homeDir,threadId:nodeRepl.requestMeta.threadId}`，同次返回本轮快照；不额外调用工具算用量。不把缓存总量当新增输出或账单，说明快照不含后续模型调用。
- 多目标、参数、恢复、只读查询或异常才读 [手工操作](references/manual.md)。无发布指令不得触发生产。
- 点选发布使用 Codex 顶部“发布 UI / Web / API / Admin”四个独立操作，直接运行脚本，不打开面板、不调用模型；凭据配置与排查见 [顶部发布操作](references/panel.md)。
