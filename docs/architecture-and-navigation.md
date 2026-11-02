# 后端规范与导航适配

参考项目：极链-门店宝-代发（`D:\project\cj\df-bak`）。当前项目仍采用 Java 17 / Spring Boot 3 / Vue3。

## 后端规范落地

根 `AGENTS.md` 为项目入口；两个已复制的 ADM skill 保留调用名，正文改为本项目技术栈、实际目录与验证方式。

新增业务链路：

```text
ruoyi-admin
  Controller → Biz → Web Convert
                   ↓
领域模块
  Facade → Service → Mapper / XML
  Param / Query / BO ↔ Entity
```

Controller 负责协议与权限，Biz 编排领域，Facade 暴露完整领域动作，Service 承担事务，Mapper 承担持久化。跨领域组合上移 Biz，外部调用不进入数据库事务。

17 个手写控制器的 107 个 HTTP 方法已全部迁移为 Controller → Biz 具体类。Biz 使用 `@Component`，Controller 通过构造器直接注入，不再创建 Biz 接口和实现类配对；已移除 24 个重复的 Biz 接口。内部系统、调度及认证能力经过 18 个 Facade 入口；文件等基础设施调用仍放在 Biz。

使用 MapStruct 1.6.3 完成 Web Form → BO → Service 内部实体及 BO → Web VO 的类型转换，保留验证、Excel 和 JSON 注解。`BoundaryPages` 避免列表转换丢失数据库分页总数；`WebResponses` 转换若依响应的 data、rows 以及 roles/menus 等附加键。

17 个 Biz 均为普通 `@Component` 类，不继承 BaseController，也不接收 Servlet 请求/响应或返回 AjaxResult、R、TableDataInfo。Biz 返回业务对象、列表、组合数据、影响行数或 void；业务校验失败使用现有 ServiceException，由 Web 异常处理器保留错误/警告码和消息。登录身份通过 SecurityUtils 或会话 Facade 显式获取。

Controller 通过 `getPageDomain()` 提取 HTTP 分页参数，经 Biz、Facade 显式传递至 Service；这些上层不启动 PageHelper，避免编排中的前置查询误消耗分页。Service 紧邻目标 Mapper 调用通用的 `PageUtils.startPage(pageDomain, 查询回调)`，工具在 finally 中统一清理线程状态，查询失败时原样抛出异常。`PageDomain` 不依赖 HTTP，支持页码、每页条数、排序和 reasonable；缺省或非正的页码、每页条数分别使用 1、10。排序沿用 SQL 安全校验，前端 ascending/descending 仍映射为 asc/desc。

```java
// Controller：仅提取并传递参数
return getDataTable(biz.list(query, getPageDomain()));
// Biz / Facade：继续传参，Facade 通过 BoundaryPages.map 保留转换后的总数
// Service：回调中只执行目标 Mapper 列表查询
return PageUtils.startPage(pageDomain, () -> mapper.selectList(condition));
```

分页重载保留原全量查询入口供导出及内部调用使用；显式 null 分页参数也表示全量查询。带 `@DataScope` 的分页 Service 重载保留相同数据权限切面。`BoundaryPages` 在边界转换时保留数据库总数，响应信封只在 Web 构造，原 `rows/total/code/msg` 协议不变。组合数据通过 `WebResponses.withFields` 保留原顶层键。Excel 导入解析与模板/导出编码、ZIP 响应头和输出流都在 Controller；Biz 提供导入动作、导出数据、ZIP 字节和下载路径校验。

所有控制器统一位于 admin 的 `com.ruoyi.web.controller` 下：调度控制器放 `quartz/`，其余按 common、system、monitor、tool 分组。迁移 Java 包名时保留 URL、HTTP 方法、鉴权和审计标识；架构测试会检查该目录约束。领域模块不再承载 HTTP 控制器，也没有反向依赖 admin。已接入 MyBatis-Plus Boot 3 Starter：16 个 Mapper 使用 BaseMapper，13 个数据库 Service 使用 IService/ServiceImpl，9 类普通列表通过 MapStruct 映射领域 Query 后交给 DynamicCondition。普通单表读写使用通用方法，必要的关联、权限、批量及幂等 SQL 保留 XML。SQL 表结构和认证保留；JSON 与 Redis 缓存统一使用 Jackson，用法见 [dynamic-condition.md](dynamic-condition.md)。

登录会话通过独立 BO 读取，在写入 TokenService 前转换回原 LoginUser；失败前的资料修改不会提前污染缓存主体。公告删除将已读记录清理与公告删除放入一个 Service 事务，空编号集合在 SQL 执行前拒绝。已读用户列表在领域内从原 Mapper 投影转换为类型化 BO，SQL 保持不变。

使用指定 JDK 构建，不改环境变量：

```powershell
./scripts/maven.ps1 -MavenArguments @('-version')
./scripts/maven.ps1 -MavenArguments @('-pl', 'ruoyi-admin', '-am', 'test')
```

脚本默认 JDK 路径为 `D:\software\Java\jdk-17.0.20.1+1`；settings 使用现有的 `D:\project\cj\df-bak\setting.xml`，不复制凭据。可通过 `-JavaHome` / `-SettingsPath` 覆盖。

## 登录精简

`POST /login` 只提交 `username` 和 `password`，Web Form、应用参数、Facade 与 Service 均已移除登录验证码字段与校验。登录页不再显示验证码、不请求验证码图片，失败后也不再刷新验证码。

密码错误计数、失败次数锁定和登录日志中的“账户解锁”入口已删除，包括对应 Redis 计数常量、Service/Facade、解锁 HTTP 接口、前端按钮/API、配置及初始化 SQL 中的权限。现有业务数据库未执行变更；已有解锁权限行不会再被界面使用，历史计数缓存也不再被读取。

密码仍由 Spring Security 使用 BCrypt 比较，保留用户不存在、停用、删除等账号状态检查、登录日志和令牌签发。注册页与注册接口也仅使用账号密码，验证码控件、参数、图片接口、生成器、异常、缓存常量及 Kaptcha 依赖均已删除。初始化 SQL 不再创建验证码开关；已有数据库的旧开关可用 `sql/migrations/20261102_remove_captcha.sql` 清理，该脚本尚未执行。真实 Spring 认证回归验证连续输错 8 次后正确密码仍可立即登录，并检查错误密码与不可用账号仍被拒绝。

## 基础设施精简

Druid Starter 使用标准 `spring.datasource.url/username/password` 自动创建单个数据源，连接池参数保留在 `spring.datasource.druid`。已删除主从配置、动态数据源、切换注解/切面与手动路由装配。H2 回归加载实际 Druid YAML，验证只有一个连接池、参数绑定正确且事务回滚生效。

Druid 监控页面、专用图标、页面广告过滤器和 `/druid/**` 匿名放行已删除；监控 Servlet 与 Web 统计过滤器显式关闭。初始化 SQL 不再创建数据监控菜单及角色关联，前端过滤旧库残留的 `monitor/druid/index` 路由。旧库清理脚本为 `sql/migrations/20261102_remove_druid_monitor.sql`，尚未执行；Druid 数据源、连接池参数与 SQL 统计配置继续保留。

MyBatis-Plus 通过 `mybatis-plus-spring-boot3-starter:3.5.18` 自动创建 SqlSessionFactory/SqlSessionTemplate，并使用 SpringBootVFS。不再保留原生 MyBatis Starter；配置统一放在 `application.yml` 的 `mybatis-plus.configuration`。驼峰自动映射沿用关闭状态，Plus 实体以显式列名和 autoResultMap 映射；关联、展示及上下文字段不持久化，已有 XML resultMap 保留。不启用 MetaObjectHandler/FieldFill 自动填充；创建/日志时间由业务 Service 新增方法显式设置，更新使用数据库 CURRENT_TIMESTAMP；部分更新保留原 null/空字符串策略。回归覆盖所有表的通用 SELECT、实际新增/主键回填/部分更新/删除、用户部门角色加载、分页及事务回滚。MySQL 专属日期函数、FIND_IN_SET、INSERT IGNORE 和 TRUNCATE 未连接业务数据库执行。

提示固定中文，已删除 MessageUtils、国际化资源与语言拦截器；用户/文件异常直接生成中文消息及实际限制值。已删除 devtools 依赖及配置、springdoc/Swagger 依赖与配置、五个内存示例接口和前端“系统接口”页面。Element Plus 固定中文显示保留，未引入语言切换。

初始化 SQL 不再创建 API 文档菜单；前端会排除旧库残留的文档页面路由。`sql/migrations/20261102_remove_api_docs_menu.sql` 可用于清理旧库菜单关联，当前没有对用户数据库执行该脚本。

## Redis 客户端

Redis 依赖改为 `org.redisson:redisson-spring-boot-starter:3.52.0`，版本统一由根 POM 管理。该版本默认使用 `redisson-spring-data-35`，适配当前 Spring Boot 3.5 / Spring Data Redis 3.5；通过 RedissonConnectionFactory 执行现有 RedisTemplate/RedisCache 调用，运行包不包含 Lettuce/Jedis。需要分布式锁等能力时可构造器注入 RedissonClient。

连接地址、端口、数据库、密码及超时沿用 `spring.data.redis`；已移除 Lettuce 连接池配置与直接依赖 commons-pool2。JSON 时区统一由 `application.yml` 的 `spring.jackson.time-zone: Asia/Shanghai` 配置，可由外部配置覆盖；HTTP、JsonUtils 和 Redis 共用应用 ObjectMapper，不使用 Java 定制器读取 JVM 默认时区。缓存键、登录会话语义、TTL 和 Lua 限流脚本保持原协议。缓存改为 JacksonJsonRedisSerializer 写标准 JSON，通过固定登记类型及旧语法适配读取原会话、字典和 Map/Set 缓存，不依赖旧 JSON 库。Integer 限流参数保持裸数字；读取旧值不会改写缓存或刷新 TTL。 固定登记 LoginUser、系统用户/部门/角色/字典对象及必要标量；新增缓存模型须显式登记。新版本兼容读取旧缓存，不承诺旧后端读取新缓存格式，多实例部署应统一升级。

自动配置测试检查实际连接工厂和序列化器。真实 Redis 集成测试仅在显式传入隔离实例端口时运行：

```powershell
./scripts/maven.ps1 -MavenArguments @('verify', '-Dredis.test.port=<隔离测试实例端口>')
```

集成测试只操作 `cf:redisson:test:*`，覆盖旧格式缓存读取、写入字节、TTL、List/Set/Hash、Lua 限流，并清理测试键；不要将端口指向业务实例。测试使用一次性本地 Redis 容器，结束后移除容器；结果以当前执行记录为准。

## 导航结构

采用参考项目的横向模块导航、多级下拉与溢出入口，保留当前后端返回的功能菜单。初始数据通常显示如下，实际条目以当前用户的授权结果为准：

```text
首页 | 系统管理 ▾ | 系统监控 ▾ | …更多菜单
          用户管理     定时任务
          角色管理
          菜单管理
          部门/字典/参数/通知/日志等现有功能
```

- 桌面默认顶部导航，保留页签、菜单搜索、刷新、主题、通知、布局、个人中心与锁屏入口。
- 少于 992px 使用完整权限侧栏；该回退不改写桌面布局偏好。
- “更多菜单”按导航实际空间计算，不以整个浏览器宽度猜测容量。
- 菜单以 `permission.defaultRoutes` 构建展示副本，不修改已注册路由、不增加业务权限。支持任意深度、绝对/相对路径、query、外链、隐藏目录及 activeMenu。
- 布局偏好保存到浏览器；损坏或类型不合法的缓存回退默认配置。已有用户保存的左侧布局继续生效，可在“布局设置”选择“顶部菜单”。
- 菜单沿用后端权限；旧库的已移除页面会在路由转换前过滤，过滤后为空的工具目录不再显示。

## 验证

全项目格式化使用本机 JetBrains 官方引擎：后端 IDEA 2025.2.6.2，Vue/JavaScript/样式及 SQL 使用 WebStorm 2026.2.1；沿用用户 Default 代码样式，与 IDE 的 Reformat Code 相同，不执行优化导入或重排成员。共处理 619 个源码与配置文件，依赖、构建产物、锁文件、技能文件及工作缓存不参与。399 个 Java 文件格式化前后词法内容一致，前端脚本另比较语法树并运行构建。

后端在根目录运行 `./scripts/maven.ps1 -MavenArguments @('clean', 'verify')`：HTTP 契约快照覆盖 107 个方法的路径/动词/权限/审计，架构测试检查控制器依赖 Biz 具体类、Biz 不继承控制器且不携带 Servlet/响应类型。MVC 回归覆盖分页总数与异常清理、顶层组合字段、字符串消息和警告码、Excel 模板；其他回归覆盖密码仅写入、登录缓存原类型、实际数据权限切面及真实 H2 事务回滚。默认测试不连接业务数据库或 Redis；真实 Redis 兼容测试按上节显式指定隔离端口运行。

前端在 `RuoYi-Vue3/` 内执行 `npm test` 与 `npm run build:prod`。测试覆盖权限树不变性、隐藏路由、深层菜单、路径/query、溢出容量与缓存损坏。通过 Vite 启动 `tests/navigation-preview.html` 可使用独立测试数据检查实际布局组件；该入口不进入生产构建、不访问真实后端、不改变生产鉴权。

既有 Service 内部依赖与原配置不在本次接口边界重构中批量改写。目标环境的 MySQL/Redis、文件与调度完整运行仍需按实际配置联调。

## 代码生成移除

已移除 ruoyi-generator、生成器 Controller/Biz/Form/VO、Velocity 模板与依赖，以及前端代码生成、表单构建、生成工具和专用依赖。初始化 SQL 不再创建对应菜单、按钮、角色关联及两张生成器配置表。旧库清理脚本为 `sql/migrations/20261102_remove_code_generation.sql`，尚未执行；该脚本保留业务表、自定义工具和历史操作日志。操作日志使用固定编码，清空数据仍为 9。

## 监控功能精简

已移除服务监控、缓存监控和缓存列表的页面、API、Controller/Biz、专用模型及转换、图标和 OSHI 依赖。初始化 SQL 不再创建这三个菜单及角色关联；前端在注册路由前过滤旧库残留的三个页面，保留其他授权菜单。

已有数据库可使用 `sql/migrations/20261102_remove_cache_server_monitor.sql` 清理对应菜单及角色关联，当前尚未执行。Redis 会话、字典、参数、防重复提交和限流仍正常使用原缓存基础设施。

在线用户页面、查询/强退接口及专用 Biz、Facade、Service、模型和转换已删除。初始化 SQL 不再创建在线用户菜单、按钮及角色关联；前端过滤旧库残留的 `monitor/online/index` 路由。已于 2026-11-02 对本机 `operate` 数据库执行 `sql/migrations/20261102_remove_online_users.sql` 并提交；执行时目标菜单、按钮权限及角色关联均已为 0，实际删除 0 行。重新连接复查，在线用户菜单、权限及旧菜单 ID 的孤立角色关联均无残留。登录会话与正常退出继续沿用现有认证链路。
