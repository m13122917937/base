---
name: adm-frontend-standards
description: 当前 CF 项目 RuoYi-Vue3 的 Vue 3 / Pinia / Element Plus 前端开发、布局、菜单、API、状态、样式与构建规范。
---

# CF 前端规范

适用于本仓库内 `RuoYi-Vue3/`。参考代发项目的导航和工程规范，保留当前 Vue3 运行时、权限与请求链路。

## 技术与项目边界

- Vue 3、Composition API / script setup、Vue Router 4、Pinia、Element Plus、Vite，版本以 package.json 和 lockfile 为准。
- 复用 @ 路径别名、现有组件和局部风格；不引入 Vue 2、Vuex、Element UI 或参考项目的 AES 客户端链路。
- 前端是独立嵌套 Git 仓库，检查变更时分别查看后端及 RuoYi-Vue3 的 Git diff；不擅自删除嵌套 .git、改成 submodule 或移动目录。
- 默认单引号、无分号、两空格缩进；导入边界明确。新增异步逻辑覆盖 loading、成功、空数据和失败。

## 导航与菜单权限

- 参考代发项目采用桌面端横向多级导航，保留左侧/混合布局选择；窄屏恢复侧栏，不持久化覆盖用户的桌面布局选择。
- 菜单仅来自后端已授权 getRouters 与既有公共路由；保留当前功能菜单，不复制代发业务菜单，不在前端给无权限功能补静态入口。
- 使用 utils/navigation.js 生成独立展示树，不修改 Pinia 的原路由；支持 hidden、相对/绝对路径、任意深度、多级目录和 query。
- 菜单溢出按导航容器实际宽度计算，收入“更多菜单”；窗口变化或工具栏变化后仍可访问所有授权菜单。
- 详情页面通过 meta.activeMenu 高亮父菜单；目录仅展开，叶子节点才导航。外链新窗口使用 noopener/noreferrer，内嵌页面保留既有路由行为。
- 切换布局或屏幕尺寸时保持已授权菜单完整；源数据用 permission.defaultRoutes，不使用已被混合模式截取的 sidebarRouters 作为全量树。
- 布局缓存通过 utils/layoutSettings.js 校验，非法 JSON、类型错误或存储不可用时回退默认值，不让缓存阻止应用启动。
- 菜单和按钮权限不能替代后端接口鉴权、数据范围和写操作校验。

## API 与状态

- 请求只放 src/api，按业务域组织；复用 src/utils/request.js，不硬编码后端域名或新建 Axios 实例。
- 保留 Bearer Token、RuoYi code/msg/data/rows/total 响应、下载和重复提交行为；不复制参考项目的请求拦截器。
- 登录页仅输入账号密码，登录 API 仅发送 username/password，不请求或刷新验证码；注册页验证码独立保留。登录日志不显示已删除的账户解锁操作。
- 不提供 API 文档/Swagger 页面；旧库残留的 `tool/swagger/index` 路由须在注册和导航转换前排除，不为已删除的页面恢复入口。界面固定中文，不增加语言切换。
- 页面在 src/views/<业务域>；页面私有能力放同域 components，跨页面组件放 src/components。
- Props 定义类型和默认值；单向数据流，通过 emits 通知父组件；不直接修改 prop。
- Pinia 负责共享状态，临时页面状态留在页面；不为布局需求重复存储权限树。
- 写操作防重复提交，成功后只刷新必要数据；失败提供可理解提示，不静默吞掉业务异常。
- 禁止新增 console.log / console.warn、调试代码和敏感数据输出。

## 样式与交互

- 复用 Element Plus、现有语义变量、尺寸与主题；默认 style scoped lang=scss，Vue3 使用 :deep。
- 弹出菜单等 Teleport 样式需要全局作用域时，必须加专用 popper-class 并限制选择器范围。
- 维持紧凑工具型布局，导航与操作区层级明确；不复制参考项目所有全局 CSS 改写当前页面。
- 浅色、深色和窄屏都检查；不硬编码仅适用于浅色的菜单文字。
- 操作用 button / link 和组件库交互，提供 aria-label、键盘焦点和合理点击区域。
- 表格合理列宽、换行并保留横向滚动；不使用隐藏滚动条掩盖溢出问题。
- 不直接修改 dist / node_modules；不为局部问题新增全局覆盖或依赖。

## 验证与交付

- 修改前读取页面、关联 API、请求拦截器、路由/权限、相邻组件与样式。
- 执行 npm test 与 npm run build:prod；当前没有 ESLint 配置，不把不存在的 lint 命令当作已通过。
- 影响权限树、query、路由解析、布局缓存的逻辑补回归测试；简单样式改动不编写镜像测试。
- 非平凡 UI 必须在浏览器检查桌面、窄屏、深浅色、溢出菜单和布局切换；若仅使用模拟接口，明确说明没有验证真实后端。
- 输出具体页面/菜单变更、兼容性、验证结果和未验证风险。
