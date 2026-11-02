---
name: adm-backend-architecture
description: 当前 CF 项目的 Java 17 / Spring Boot 3 后端架构、功能开发、重构和审查规范，适用于 Java、MyBatis、SQL、事务、配置与 Maven 模块依赖。
---

# CF 后端架构与规范

参考“极链-门店宝-代发”的分层、领域隔离与对象边界，遵循仓库根目录 `AGENTS.md`。适用技术栈和模块以当前仓库为准，不照搬参考项目的 Java 11 / Spring Boot 2 或业务模块。

## 技术栈与迁移边界

- 保留 Java 17、Spring Boot 3.5、Spring Security、MyBatis-Plus、DynamicCondition、PageHelper、MySQL、Redis（Redisson 3.52.0）；使用 Jakarta API。
- 数据库使用 Druid Starter 自动装配单个数据源，标准 `spring.datasource` 连接参数与 `spring.datasource.druid` 连接池参数；不添加主从或动态数据源。
- MyBatis-Plus 使用 Boot 3 对应的 `mybatis-plus-spring-boot3-starter` 3.5.18，工厂、模板和 SpringBootVFS 由 Starter 装配；全局设置统一使用 `mybatis-plus.configuration`，不混用原生 MyBatis Starter，不手动创建工厂。必要业务 XML、别名包和 PageHelper 保持兼容，不叠加 Plus 分页拦截器。
- 固定中文提示，异常直接携带业务消息；不使用自定义国际化资源、语言切换或 MessageUtils。不引入 devtools、springdoc/Swagger 或相关示例代码。
- Redis 使用 `redisson-spring-boot-starter`，通过 `redisson-spring-data-35` 提供连接工厂，现有 RedisTemplate/RedisCache 使用 Jackson 缓存编解码，保留缓存键、TTL 和 Lua 语义；禁止重新引入 Lettuce/Jedis。升级 Starter 前核对 Spring Boot / Spring Data 版本，连接参数沿用 `spring.data.redis`。
- 现有 MyBatis XML、权限标识、登录认证、缓存格式和数据权限均为若依兼容基线，不因规范同步批量改写。
- 用户明确精简登录：`/login` 仅使用 username/password，移除登录验证码、密码错误次数及锁定/账户解锁能力；密码仍由 Spring Security BCrypt 校验，保留账号状态和登录日志。注册验证码及其独立参数不受影响，禁止从登录参数继承验证码字段。
- MapStruct 1.6.3 已通过父 POM 管理并启用注解处理；复用各模块转换器。MyBatis-Plus 和 DynamicCondition 已接入：数据库 Mapper 继承 BaseMapper，Service 接口继承 IService，实现继承 ServiceImpl；3.5.18 的 Service 包为 com.baomidou.mybatisplus.spring.service。普通列表将允许的条件经 MapStruct 转为领域 Query，再使用 DynamicCondition 和通用 list；普通单表写入使用通用 save、updateById/update、删除方法，保留原字段策略及影响行数。Hutool 尚未引入。详见 docs/dynamic-condition.md。
- 对象跨层转换使用 MapStruct，不得用 BeanUtils、反射或 JSON 序列化绕过对象边界。Facade 隐藏 Entity，Controller/Form 和 Web VO 保持独立；列表转换保留 PageHelper 元数据。
- 所有 JSON 统一使用 Spring 配置的 Jackson ObjectMapper / JsonNode，静态工具复用 JsonUtils；不引入其他 JSON 实现。Redis 使用 JacksonJsonRedisSerializer，写标准 JSON，兼容读取旧类型标记及 Set/Long 语法；缓存模型采用固定登记表，禁止 default typing 或由输入类名加载任意类。兼容旧缓存须用静态样本回归，不恢复旧库依赖。
- 通用工具优先复用现有实现；引入 Hutool 后优先使用对应工具能力，不重复手写。
- 禁止 var、System.out.println、敏感数据日志；金额使用 BigDecimal。
- Maven 通过 `scripts/maven.ps1` 使用指定 JDK 17 和显式 settings；等效命令为 `mvn -s D:\project\cj\df-bak\setting.xml ...`，其进程应运行于 JDK 17。不得修改用户/系统 JAVA_HOME 或 Path。

## 调用链与模块职责

新增业务统一采用 `Controller → Biz → Facade → Service → Mapper`：

- Controller：仅协议适配、参数校验、权限注解、标准响应；内部业务仅调用 Biz，不暴露 Entity。
- 所有 HTTP Controller 的位置统一为 ruoyi-admin 的 `com.ruoyi.web.controller.<业务域>`，源码位于对应 `web/controller/<业务域>/` 目录；调度、生成等领域模块不得放置 Controller，不能只移动模块而保留旧控制器包名。
- Biz：处理登录上下文和跨领域编排，仅调用领域 Facade，不访问 Entity、Service、Mapper。使用具体类，不创建 Biz 接口或 Impl 配对，Controller 直接构造器注入该类。
- Biz 不继承 BaseController 或 Web 基类，不另造 BaseBiz 复刻 Controller 能力；不使用 Servlet 请求/响应、AjaxResult、R 或 TableDataInfo。返回领域数据、业务组合数据、列表、影响行数或 void，业务校验失败抛 ServiceException，Web 统一生成原协议响应。登录上下文显式使用 SecurityUtils 或会话 Facade。
- HTTP 分页在 Controller 开启，finally 清理线程变量；Biz / Facade 返回保留 Page 元数据的列表。Excel 解析/模板/编码和文件响应头/输出流属于 Web；Biz 接收业务导入数据、返回导出列表/ZIP 字节/校验后的下载路径。
- Facade：领域唯一出口；使用本领域 Param、Query、BO，通过本领域 Service 编排动作、构造条件和转换边界对象，不直接注入 Mapper。
- Service：可复用领域能力和唯一数据库事务边界；只访问本业务包 Mapper 或类型化基础设施 Client，不互调 Service。
- Mapper：仅持久化及 SQL；不调用 Service、Facade 或外部系统。
- Job / Listener：只触发与适配；调用 Biz 或 Facade，不直接操作 Service / Mapper。
- Controller 用 @RestController，Biz 具体类 / Facade 实现用 @Component，Service 用 @Service；统一使用构造器注入，Biz 直接注入具体类，Facade / Service 继续面向接口。
- Controller / Biz 可直接使用 Redis、HTTP 等基础设施，但不得把内部 Service 包装为 Client 绕过分层。
- Service 内访问外部系统通过类型化 Client，Facade 编排外部调用与事务 Service 的先后顺序；所有外部调用都在数据库事务之外。

当前 23 个控制器、141 个 HTTP 方法已迁移。兼容迁移沿用原字段的 BO，不改变现有请求与响应；新业务按具体查询/动作定义 Query、Param 和 BO，不继续复制完整模型。

现有模块：

| 模块 | 职责 |
| --- | --- |
| ruoyi-admin | 启动、Controller 和新增业务的 Biz / Web Form / VO / Web Convert |
| ruoyi-system | 现有系统领域，新增领域边界放 Facade / Service / Mapper / model / convert |
| ruoyi-framework | Spring Security、令牌、权限切面、框架配置；现有对 system 的依赖属于若依基线 |
| ruoyi-common | 真正通用的基础类型、注解和工具，不下沉业务对象规避边界 |
| ruoyi-quartz | 任务调度领域能力、Facade；HTTP 控制器在 admin 中 |
| ruoyi-generator | 代码生成领域能力、Facade；HTTP 控制器在 admin 中；旧生成模板不作为新业务分层示例 |

新领域模块须基于实际需求建立，不创建空的 user/order/finance/analysis/api/starter 模块。领域模块及同模块内不同业务包互不调用、不引用对方 Entity / BO / Param / Query、不跨域 Join；组合统一上移至 admin 的 Biz。不得修改若依框架依赖图来追求形式上的隔离。

## Facade 设计与对象边界

Facade 只提供以下稳定领域能力：

1. 通用抽象查询：以本领域 Query 表达条件，返回 BO、明确的集合或分页结果。
2. 专用领域查询：仅用于通用查询无法表达的关联、聚合或领域判断；SQL 下沉同领域 Mapper XML。
3. 行为动作：使用完整业务动词表达，例如启用配置、停用规则、确认结果；调用方不应知道内部写入顺序。

- 禁止照搬 Mapper 的 insert/updateById/deleteByIds 等零散 CRUD 对外暴露。
- 同业务包多个持久化动作须由一个 Service public 方法原子完成；不得让 Biz 拼接本包事务。
- 入出参仅使用本领域 Param / Query / BO 或通用基础类型；禁止无类型 Map、Entity、Wrapper、第三方 DTO。
- Web Form / VO 与领域对象、领域对象与 Entity 使用各层专属 MapStruct 转换器。没有引入转换器时先完成依赖配置，不复制参考项目的未解析 import。
- 参数、返回值及异常必须表达成功、失败、幂等与原子性语义。

## SQL、事务与数据权限

- 现有系统表 sys_*、调度表 QRTZ_*、生成器表 gen_* 保持原结构。
- 新业务先确定领域、业务包及表前缀；当前未约定新业务前缀，不沿用代发项目的 u_/o_/f_/ana_*。
- 新建或修改字段必须有非空且准确的 COMMENT；状态说明各取值，金额与数量说明单位。
- 不新增 FOREIGN KEY / REFERENCES，不使用 GENERATED 或虚拟/存储生成列；关联完整性在领域逻辑处理。
- 每个新增 Mapper 有同名 XML 且 namespace 与接口全限定名一致，多参数显式 @Param。
- 现有 MyBatis 查询采用参数绑定，禁止拼接用户输入。若引入 MyBatis-Plus，普通单表 CRUD 使用通用方法，关联、聚合、批量插入、原子 upsert 等确需定制的语义才写 XML。
- IN / NOT IN、更新和删除的集合入参必须校验非空，避免扩大作用范围。
- 新业务必须显式接入数据权限；菜单或按钮可见不代表数据权限。查询列表、详情、导出、修改、删除都需要核对范围。
- 若依 @DataScope 须和 Mapper 的受控 `params.dataScope` 片段共同工作，不能仅加注解；不得直接接收客户端传入的过滤片段。
- @Transactional(rollbackFor = Exception.class) 仅用于 Service public 写方法；事务内禁止 HTTP/RPC、文件、Excel、消息等待和大循环。
- 外部回调与同步使用业务唯一键及数据库唯一约束实现幂等，跨域一致性由 Biz 协调状态、重试或补偿。

## Java 方法注释

每个新增或修改的显式 Java 方法、接口方法、构造方法必须有紧邻声明的 JavaDoc，含 private、测试及配置方法。

- 首段说明实际执行顺序、判断条件、数据来源、状态变化、持久化与外部调用，不能只复述方法名。
- 实现类不得仅写 inheritDoc；接口说明契约、关键规则、成功及失败语义。
- 参数、返回值或异常不直观时补齐 @param / @return / @throws。
- 幂等、事务、缓存、远程调用、文件、批处理和补偿行为必须写清。
- 不要求为未触及的若依历史方法批量补注释。

## 验证与交付

- 先读目标文件、调用方、下游 Service/Mapper/XML 和相关测试。
- 涉及业务规则、权限、持久化及事务时补充对应单元/集成测试；验证空集合保护和数据范围，不能只搜索注解断言合规。
- 依赖变更检查 POM 和源码 import，防止反向依赖、跨领域调用及跨包 SQL。
- 配置使用 @ConfigurationProperties；敏感值外部化，不复制参考项目的连接串、账号、密钥、缓存及环境文件。
- 保持既有 URL、HTTP 方法、字段、认证、响应、菜单权限与数据范围语义，除非用户明确要求改变。
- 修改后执行受影响模块测试/打包，检查 Git diff、敏感数据、无关改动和构建产物。
- 交付区分“规范已落地”“具体调用链已迁移”“未迁移的若依基线”，说明改动范围、测试命令/结果和未验证部分。
