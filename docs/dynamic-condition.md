# MyBatis-Plus 通用 Service 与 DynamicCondition

使用 `mybatis-plus-spring-boot3-starter:3.5.18` 统一装配会话工厂，配置前缀为 `mybatis-plus`。PageHelper 继续负责分页，不叠加 Plus 分页插件，不混用原生 MyBatis Starter。当前版本 IService/ServiceImpl 位于 `com.baomidou.mybatisplus.spring.service` 包。

16 个数据库 Mapper 已继承 BaseMapper，13 个数据库 Service 已接入 IService/ServiceImpl。配置、字典、菜单、公告、日志、调度等 9 类普通列表结合 DynamicCondition 使用通用 list。

## 普通单表查询

Query 只声明允许的筛选字段，避免实体上的关联、展示、审计和权限上下文被意外当作条件。兼容实体到领域 Query 使用 MapStruct；DynamicCondition 的反射仅用于读取查询注解和值。

```java
@Data
public class ItemQuery {
    private Long itemId; // 默认 EQ，驼峰转下划线

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "item_name")
    private String keyword;

    @QueryField(operator = DynamicCondition.Operator.IN, field = "status")
    private List<Integer> statuses;
}

public interface ItemMapper extends BaseMapper<ItemEntity> {
}

public interface IItemService extends IService<ItemEntity> {
    /** 按允许的条件查询本领域数据，保留分页元数据，数据范围须由领域显式约束。 */
    List<ItemEntity> query(ItemQuery query, PageDomain page);
}

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, ItemEntity> implements IItemService {
    /** 构造条件后仅对目标查询启动分页，PageUtils 在 finally 中清理线程变量。 */
    @Override
    public List<ItemEntity> query(ItemQuery query, PageDomain page) {
        QueryWrapper<ItemEntity> wrapper = DynamicCondition.toWrapper(query);
        return PageUtils.startPage(page, () -> list(wrapper));
    }
}
```

按主键查询本表使用 getById，唯一条件使用 getOne，列表使用 list(DynamicCondition.toWrapper(query))。分页仍在目标查询前由 PageUtils.startPage 启动，导出使用不分页入口。排序沿用 PageDomain，或传入服务端固定 SortBy，避免重复指定排序。客户端属性须先映射到业务允许列。

Wrapper 只留在 Service/Mapper 内；Facade、Biz、Controller 仍使用领域 Query/Param/BO 和 Web Form/VO。Facade 提供业务动作，不直接暴露整个通用 Service 的 CRUD。

## 单表写入

- 新增使用 save；TableId AUTO 回填主键，TableField 保留原插入策略。关联/展示字段使用 exist=false，各表缺少的基类字段用 excludeProperty 排除。
- 配置、字典、角色等普通更新使用 updateById，实体元数据明确 NOT_NULL/NOT_EMPTY/NEVER 策略。创建者和创建时间只读，更新时间使用数据库 CURRENT_TIMESTAMP。
- 用户资料、菜单、公告、部门和调度等需要限制可写列的更新使用通用 update 与受控 UpdateWrapper，保留原 null/空串策略。例如 null 保留原值，空串可清空邮箱/手机号/公告正文，空昵称等不覆盖。
- 状态、头像、登录信息、密码和逻辑删除通过通用方法设置明确列。逻辑删除继续写 del_flag='2'，不启用新的全局逻辑删除配置改变原查询规则。
- 写入前验证主键或非空主键集合；需要实际影响行数的批量动作调用 BaseMapper 通用方法，避免把布尔成功误报为固定的 1。原业务校验、关联清理和 Service 事务边界保留。
- 不使用 MyBatis-Plus MetaObjectHandler 或 FieldFill 自动填充；创建/日志时间由业务 Service 的新增方法显式设置，更新和密码时间继续使用数据库 CURRENT_TIMESTAMP。应用与数据库应使用一致的时间配置。

关联/数据权限专用 SQL、祖级 CASE 批量更新、批量插入、幂等 INSERT IGNORE 和 TRUNCATE 保留必要 XML。用户详情继续加载部门和角色。DynamicCondition 不自动提供鉴权或数据范围；保留 @DataScope 与受控 params.dataScope 链路，新查询必须显式约束可访问范围。

## DynamicCondition 规则

工具位置：com.ruoyi.common.mybatis.DynamicCondition、QueryField 和 com.ruoyi.common.model.SortBy。

| 操作符 | 行为 |
| --- | --- |
| EQ / ENUM_EQ / NE | 等于、枚举等于、不等于，保留 0、false |
| LIKE / LIKE_LEFT / LIKE_RIGHT | `%值%`、`%值`、`值%`，参数绑定 |
| GT / LT / GTE / LTE | 大于、小于、大于等于、小于等于 |
| BETWEEN | BetweenQueryAttribute 双端闭区间、单端 GE/LE；两端为空或逆序拒绝 |
| IN / NOT_IN | 集合、对象数组、基本数组或单值；空集合及 null 元素拒绝 |
| IS_NULL / NOT_NULL | Boolean 开关，仅 true 启用 |
| FIND_IN_SET | 值绑定，不拼接输入值 |
| GROUP | 服务端固定的逗号分隔列名，同时设置投影与分组，不接受 SQL 表达式 |
| LIMIT | 正 Integer/Long，仅供固定数量查询，不与 PageHelper 混用 |

默认忽略 null 和空白字符串；queryEmpty=true 才查询空字符串/纯空白。ignore=true 完全忽略字段。静态、transient、合成字段和 PageDomain/BaseEntity 基类字段不成为条件；业务父类字段参与，同名以子类为准。

列名只允许 columnName 或 alias.columnName，驼峰转下划线。SortBy.of("createTime desc,itemId asc") 和 SortBy.of("-createTime,+itemId") 均逐项指定方向，默认升序。合法标识符仍需业务层白名单映射。ENUM_EQ 使用等值绑定，枚举存储遵循当前 MyBatis-Plus TypeHandler 配置。

原参考类的 queryEmpty 无效、空集合丢弃、false 触发空值条件、FIND_IN_SET 拼接值等行为已修正，迁移代码遵守以上规则。

## 专用 XML

只有确需定制 SQL 时才编写 XML；需要 Wrapper 的 Mapper 参数命名 ew。`${ew.customSqlSegment}` 只能由服务端构造，过滤值仍通过 `#{ew.paramNameValuePairs...}` 绑定。已有 WHERE 不能直接再拼带 WHERE 的 customSqlSegment；应统一构造受控条件，或保留 XML 动态条件。固定投影不会自动读取 GROUP 的 sqlSelect；需要动态投影时只能使用服务端固定列名。

## 验证

```powershell
./scripts/maven.ps1 -MavenArguments @('-pl', 'ruoyi-admin', '-am', 'test')
```

DynamicConditionTest 验证操作符、非法输入与参数绑定。MybatisPlusPersistenceTest 使用当前表结构的隔离 H2 库，覆盖全部 Mapper 的通用查询、Service 主键回填/部分更新/字段清空、分页、逻辑删除及用户关联加载。原事务、HTTP、登录和架构测试继续回归。MySQL 专属日期函数、FIND_IN_SET、INSERT IGNORE 和 TRUNCATE 未连接业务数据库执行。
