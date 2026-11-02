# CF 项目开发约定

本项目以 RuoYi-Vue 3.9.2 的 springboot3 分支为基础，借鉴“极链-门店宝-代发”的工程规范，保留当前技术栈及若依基础权限能力。

## 产品命名

- 当前产品名称为“惠商经营”，智能体名称为“惠商AI”。导航品牌、网页标题、登录页和智能体相关文案统一使用这两个名称。
- “极链门店宝”等名称仅用于说明参考项目来源，不作为当前产品的展示名称。

## 实际目录与技术栈

- 后端：Java 17、Spring Boot 3.5、Spring Security、MyBatis-Plus、DynamicCondition、PageHelper、MySQL、Redis（Redisson 3.52.0 客户端）。
- 前端：`RuoYi-Vue3/`，Vue 3、Vue Router 4、Pinia、Element Plus、Vite；前端保留独立 Git 仓库。
- Maven 模块：`ruoyi-admin`、`ruoyi-framework`、`ruoyi-system`、`ruoyi-quartz`、`ruoyi-common`，以根 POM 为准。
- 不照搬参考项目的 Java 11、Spring Boot 2、Vue 2、业务模块、真实配置或数据库。

## 适用技能

- 后端开发、架构、SQL、配置与测试：先读 `.agents/skills/adm-backend-architecture/SKILL.md`。
- Vue3 页面、导航、菜单、API 与样式：先读 `.agents/skills/adm-frontend-standards/SKILL.md`。
- 文件名保留原技能名称，但正文已适配本项目；当前实际技术栈及本文件优先。
- 其他复制来的技能只有在任务相关时才读取，不能据其旧版本描述降级当前技术栈，不能推断已配置生产部署。

## 后端约束

1. 新业务采用 `Controller → Biz → Facade → Service → Mapper`。Controller/Biz 不依赖 Entity、Service、Mapper；Facade 不直接访问 Mapper；Service 是唯一数据库事务边界，Service 之间不互调。
2. Biz 位于应用模块，承担跨领域/业务包编排，直接使用 `@Component` 具体类，不创建 Biz 接口或 Impl 配对；Controller 通过构造器注入 Biz 类。各领域及业务包之间互不引用对方业务对象、不跨包 Join；公共模块不能作为绕行边界。
3. Facade 使用领域 Param/Query/BO；Web 使用 Form/VO。转换优先用 MapStruct，不用反射、BeanUtils 或 JSON 复制对象。
4. 已引入 MapStruct 1.6.3 并在父 POM 配置注解处理器；Web/领域转换使用各层转换器。MyBatis-Plus 3.5.18 使用 Boot 3 Starter，数据库 Mapper 继承 BaseMapper，数据库 Service 接口继承 IService、实现继承 ServiceImpl（当前版本包名为 com.baomidou.mybatisplus.spring.service）。普通单表查询、插入、更新和删除使用通用方法；列表筛选通过领域 Query、QueryField 和 DynamicCondition 构造，不把实体上的上下文、关联和展示字段当条件。关联/数据权限专用 SQL、CASE 批量更新、批量插入、幂等 INSERT IGNORE 和 TRUNCATE 保留必要 XML。Hutool 尚未接入。用法见 docs/dynamic-condition.md。
5. JSON 统一使用 Spring 配置的 Jackson ObjectMapper/JsonNode，公共静态场景复用 JsonUtils；应用时区统一使用 application.yml 的 spring.jackson.time-zone，不通过 Java 定制器或 JVM 默认时区覆盖；不引入其他 JSON 库，不使用 JSON 做跨层对象复制。Redis 使用 JacksonJsonRedisSerializer 写标准 JSON，只恢复明确登记的缓存模型；旧缓存只通过 Jackson 兼容读取，禁止开放 default typing 或按输入类名加载任意类型。
6. 每个新增/修改的显式 Java 方法及构造器都有准确的 JavaDoc，说明步骤、判断、事务/幂等/外部调用和失败语义。
7. 保留系统菜单/按钮权限、接口鉴权和数据权限。新增业务表和写接口须明确接入数据范围，不因有菜单就认定有数据隔离。
8. 已有系统/调度表结构保持兼容；新增表先确定领域与前缀。字段必须有 COMMENT，不新增外键、生成列或客户端可控 SQL 片段。
9. 新配置使用 ConfigurationProperties，敏感值外部化，日志不输出 Token、密码或个人信息。
10. 所有 HTTP Controller 统一放在 `ruoyi-admin/src/main/java/com/ruoyi/web/controller/<业务域>/`，包名为 `com.ruoyi.web.controller.<业务域>`；不得保留在 `com.ruoyi.quartz.controller` 或领域模块目录中。
11. Redis 客户端统一使用 Redisson Starter，当前通过 `redisson-spring-data-35` 兼容现有 RedisTemplate/RedisCache；保留缓存键和 Lua 限流；缓存序列化统一为 Jackson，兼容读取原会话/字典缓存，不引回 Lettuce/Jedis。升级时核对 Spring Boot / Spring Data 适配版本。
12. Biz 不继承 BaseController 或其他 Web 基类，不复制一份 BaseBiz 绕过边界；不接收 Servlet 请求/响应，不返回 AjaxResult、R、TableDataInfo。Biz 返回业务数据、列表、影响行数或 void，校验失败使用 ServiceException；状态码、消息和响应封装统一在 Web 层完成。登录上下文显式使用 SecurityUtils 或会话 Facade。
13. Controller 仅提取通用 PageDomain 分页参数，经 Biz、Facade 显式传入 Service；Service 在目标 Mapper 查询前使用 PageUtils.startPage(pageDomain, 查询回调)，由工具在 finally 中清理线程状态。Biz/Facade 返回保留 PageHelper 元数据的列表，导出使用不分页查询。Excel 的解析/模板/导出、ZIP 附件头与响应流在 Web 层；Biz 只查询/导入业务数据、生成 ZIP 字节或校验下载路径。
14. 登录仅接收 username/password，不启用登录验证码、密码错误计数或按失败次数锁定；保留 BCrypt 密码验证、账号状态检查和登录日志。注册也仅使用账号密码；验证码页面、接口、参数、缓存及依赖均已移除，不得重新引入。
15. 数据库只保留单个 Druid 数据源，连接配置使用标准 `spring.datasource.url/username/password`，连接池设置在 `spring.datasource.druid`；不引入主从、动态路由数据源及切换注解。
16. 提示消息固定中文，不维护语言切换、MessageSource 资源查找或国际化消息键；不重新引入 devtools 热部署、springdoc/Swagger 依赖或示例接口。
17. MyBatis-Plus 使用 `mybatis-plus-spring-boot3-starter` 自动装配，参数统一放 `application.yml` 的 `mybatis-plus.configuration`；不混入原生 MyBatis Starter，不恢复全局 XML 或手动 SqlSessionFactory。保留必要业务 Mapper XML、既有别名扫描与 PageHelper 分页，不添加 Plus 分页拦截器形成双重分页。单表更新使用 updateById 或通用 update + 受控 UpdateWrapper，明确可写列和 null/空串策略；写入前校验主键/集合，批量方法保留真实影响行数。
18. Java 日志统一使用 Lombok `@Slf4j` 与 `log`，禁止手写 LoggerFactory、System.out/System.err 日志及无参 printStackTrace；`sys-user` 专用日志用 `@Slf4j(topic = "sys-user")` 保留分类。
19. 已按用户要求移除代码生成、表单构建和 ruoyi-generator；不恢复生成器模块、模板、接口、页面、菜单或专用依赖。旧技能正文中关于保留生成器的约定已失效。历史操作日志使用固定编码，清空数据仍为 9。
20. 不使用 MyBatis-Plus MetaObjectHandler 或 FieldFill 自动填充；需要的创建/日志时间由业务 Service 新增方法显式设置，更新和密码时间保留受控数据库表达式。

## 存量代码的边界

当前 18 个手写控制器、114 个 HTTP 方法已全部改为通过 Biz 具体类调用；内部领域调用经过 Facade，不向 Controller/Biz 暴露 Entity。所有控制器统一位于 ruoyi-admin 的 `com.ruoyi.web.controller` 下，定时任务放 `quartz` 子包；URL 保持兼容。纯文件等入口通过 Biz 使用原基础设施，不为无数据库能力制造空的 Service 转发层。

若依原有框架 → system 依赖、内部 Service/Mapper 实现继续作为兼容基线；JSON 与缓存统一使用 Jackson。新业务遵循完整分层。兼容迁移的领域 BO 沿用原字段结构，新业务应按动作分离 Param/Query/BO，避免复制整个持久化模型。

## 前端与菜单

- 默认桌面横向多级导航，空间不足使用“更多菜单”，移动端恢复侧栏；保留左侧/混合模式与个人布局偏好。
- 所有业务菜单沿用后端授权路由，复用导航布局不等于迁移代发业务菜单。
- 展示菜单不能改写权限路由。路由参数、外链、activeMenu、隐藏菜单、深层目录均保持兼容。
- 复用现有 API 与请求链路，不复制参考项目的 Vue2 代码、AES 逻辑或静态权限兜底。

## 本地构建与验证

- 用户要求不修改本机环境变量。JDK 17 安装在 `D:\software\Java\jdk-17.0.20.1+1`；构建通过 `scripts/maven.ps1` 直接调用该 Java，不修改用户、系统或当前进程的 JAVA_HOME/Path。
- Maven 必须显式指定 settings，默认沿用已存在的 `D:\project\cj\df-bak\setting.xml`；不复制该文件及其中的凭据，其他机器通过参数覆盖路径。
- 后端验证示例：`./scripts/maven.ps1 -MavenArguments @('-pl', 'ruoyi-admin', '-am', 'test')`。
- 前端验证：在 `RuoYi-Vue3` 内执行 `npm test` 和 `npm run build:prod`，UI 改动验证桌面/移动端/深浅色。
- 不擅自提交、推送、执行数据库脚本或触发部署；保留用户已有及已暂存的改动。
- 交付说明实际修改、验证结果和限制；分别检查父仓库与前端独立仓库的 diff，不提交构建产物、依赖目录或敏感数据。
