package com.ruoyi.common.mybatis;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.utils.PageUtils;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static com.ruoyi.common.mybatis.DynamicCondition.Operator.*;
import static org.assertj.core.api.Assertions.*;

/** 验证参考查询能力、非法条件保护及既有 MyBatis XML/PageHelper 在 Plus 下的真实执行。 */
class DynamicConditionTest {
    /** 逐个比较操作符生成参数化条件，并检查别名、空白、忽略及基础设施字段。 */
    @Test
    void bindsComparisonsAndSkipsNonQueryFields() {
        QueryWrapper<Object> wrapper = DynamicCondition.toWrapper(new ComparisonQuery());
        assertThat(wrapper.getSqlSegment()).contains("plain_value =", "enum_value =", "different <>",
                "sku_name LIKE", "prefix LIKE", "suffix LIKE", "greater >", "less <", "minimum >=", "maximum <=")
                .doesNotContain("blank", "ignored", "constant", "temporary", "page_num", "page_size", "is_asc");
        assertThat(wrapper.getParamNameValuePairs().values()).containsExactlyInAnyOrder(
                1, 2, 3, "%word%", "start%", "%end", 4, 5, 6, 7);
        EntityQuery query = new EntityQuery();
        query.setCreateBy("context");
        query.getParams().put("dataScope", "must-not-be-a-condition");
        assertThat(DynamicCondition.toWrapper(query).getSqlSegment()).contains("business_id =")
                .doesNotContain("create_by", "params", "dataScope");
    }

    /** 空输入不阻止独立排序，显式 queryEmpty 才绑定空白字符串；false/0 仍可等值过滤。 */
    @Test
    void honorsEmptyPolicyAndSortsWithoutFilters() {
        assertThat(DynamicCondition.toWrapper(null).getSqlSegment()).isEmpty();
        assertThat(DynamicCondition.toWrapper(null, SortBy.of("-createdAt,+id")).getSqlSegment().trim())
                .isEqualTo("ORDER BY created_at DESC,id ASC");
        QueryWrapper<Object> wrapper = DynamicCondition.toWrapper(new EmptyQuery());
        assertThat(wrapper.getSqlSegment()).contains("explicit_blank =", "enabled =", "quantity =")
                .doesNotContain("default_blank", "absent");
        assertThat(wrapper.getParamNameValuePairs().values()).containsExactlyInAnyOrder(" ", false, 0);
        assertThat(DynamicCondition.toWrapper(new ChildQuery()).getSqlSegment())
                .contains("child_id =", "parent_id =").doesNotContain("parent_shadow");
    }

    /** 完整和单端区间分别生成 BETWEEN/GE/LE；空端点、逆序及错误类型直接拒绝。 */
    @Test
    void validatesRangesAndSupportsOneSidedBounds() {
        RangeQuery query = new RangeQuery();
        query.range.setStart(10);
        query.range.setEnd(20);
        QueryWrapper<Object> wrapper = DynamicCondition.toWrapper(query);
        assertThat(wrapper.getSqlSegment()).contains("amount BETWEEN");
        assertThat(wrapper.getParamNameValuePairs().values()).containsExactlyInAnyOrder(10, 20);
        query.range.setEnd(null);
        assertThat(DynamicCondition.toWrapper(query).getSqlSegment()).contains("amount >=");
        query.range.setStart(null);
        query.range.setEnd(20);
        assertThat(DynamicCondition.toWrapper(query).getSqlSegment()).contains("amount <=");
        query.range.setStart(30);
        assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(query));
        query.range.setStart(null);
        query.range.setEnd(null);
        assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(query));
        assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(new InvalidRangeQuery()));
    }

    /** 集合、对象数组、基本数组和单值均逐项绑定；两种集合操作都拒绝空集合和 null 元素。 */
    @Test
    void bindsMembershipAndRejectsEmptyCollections() {
        for (Object value : new Object[]{List.of(1, 2), new Integer[]{1, 2}, new int[]{1, 2}, 1}) {
            MembershipQuery query = new MembershipQuery();
            query.in = value;
            query.notIn = value;
            QueryWrapper<Object> wrapper = DynamicCondition.toWrapper(query);
            assertThat(wrapper.getSqlSegment()).contains("in_ids IN", "not_in_ids NOT IN");
            assertThat(wrapper.getParamNameValuePairs()).hasSize(value instanceof Integer ? 2 : 4);
        }
        for (Object invalid : new Object[]{List.of(), new int[0], new Integer[0], Arrays.asList(1, null)}) {
            MembershipQuery query = new MembershipQuery();
            query.in = invalid;
            assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(query));
            query.in = null;
            query.notIn = invalid;
            assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(query));
        }
    }

    /** FIND_IN_SET 的危险字符串必须只出现在参数中；分组、空值开关、排序和 LIMIT 语义明确。 */
    @Test
    void bindsFindInSetAndValidatesStructuralConditions() {
        StructuralQuery query = new StructuralQuery();
        query.member = "1) OR 1=1 --";
        QueryWrapper<Object> wrapper = DynamicCondition.toWrapper(query, SortBy.of("createdAt desc,id asc"));
        assertThat(wrapper.getSqlSegment()).contains("present IS NOT NULL", "missing IS NULL", "FIND_IN_SET(#{",
                "category_ids)", "GROUP BY category_id", "ORDER BY created_at DESC,id ASC", "LIMIT 5")
                .doesNotContain(query.member, "disabled");
        assertThat(wrapper.getSqlSelect()).isEqualTo("category_id");
        assertThat(wrapper.getParamNameValuePairs().values()).containsExactly(query.member);
        query.grouping = "category_id); DROP TABLE fixture; --";
        assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(query));
        query.grouping = "category_id";
        for (Object invalid : new Object[]{0, -1L, "5", 1.5}) {
            query.limit = invalid;
            assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(query));
        }
        assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(new InvalidNullQuery()));
    }

    /** 排序和注解列名拒绝表达式、注释和 SQL 尾部；解析器保留每项方向及去重顺序。 */
    @Test
    void rejectsSqlInIdentifiersAndParsesSorts() {
        for (String input : List.of("id desc;drop table fixture", "id/**/", "id,(select 1)", "id,,name", "id sideways")) {
            assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(null, SortBy.of(input)));
        }
        assertThatIllegalArgumentException().isThrownBy(() -> DynamicCondition.toWrapper(new InvalidColumnQuery()));
        SortBy sort = SortBy.of("id DESC,createdAt ASC,id DESC");
        assertThat(sort.get(SortBy.Direction.DESC)).containsExactly("id");
        assertThat(DynamicCondition.toWrapper(null, sort).getSqlSegment().trim()).isEqualTo("ORDER BY id DESC,created_at ASC");
        assertThat(SortBy.of(null).isEmpty()).isTrue();
        assertThat(SortBy.of(" ").isEmpty()).isTrue();
    }

    /**
     * Plus Starter 将 Wrapper 参数通过既有 XML 绑定到隔离 H2；覆盖过滤、注入值、分页总数、分组和 LIMIT。
     * 不连接项目 MySQL/Redis，分页由现有 PageUtils finally 清理，会话工厂使用 Plus 自动装配。
     */
    @Test
    void executesXmlWithNativeMyBatisAndPageHelper() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MybatisPlusAutoConfiguration.class, PageHelperAutoConfiguration.class))
                .withBean(DataSource.class, () -> new DriverManagerDataSource(
                        "jdbc:h2:mem:dynamic_" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", ""))
                .withPropertyValues("mybatis-plus.mapper-locations=classpath:dynamiccondition/FixtureMapper.xml",
                        "pagehelper.helperDialect=h2")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(SqlSessionFactory.class);
                    JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
                    jdbc.execute("create table fixture (id bigint primary key, category_id varchar(20), name varchar(100), amount int)");
                    jdbc.update("insert into fixture values (1, 'A', 'phone', 10), (2, 'A', 'phone pro', 20), (3, 'B', 'other', 30)");
                    try (SqlSession session = context.getBean(SqlSessionFactory.class).openSession()) {
                        FixtureMapper mapper = session.getMapper(FixtureMapper.class);
                        FixtureQuery query = new FixtureQuery();
                        query.setName("phone");
                        QueryWrapper<Object> wrapper = DynamicCondition.toWrapper(query, SortBy.of("-id"));
                        assertThat(mapper.selectIds(wrapper)).containsExactly(2L, 1L);
                        PageDomain page = new PageDomain();
                        page.setPageNum(2);
                        page.setPageSize(1);
                        List<Long> result = PageUtils.startPage(page, () -> mapper.selectIds(wrapper));
                        assertThat(result).containsExactly(1L);
                        assertThat(((Page<?>) result).getTotal()).isEqualTo(2);
                        assertThat(PageHelper.getLocalPage()).isNull();
                        query.setName("' OR 1=1 --");
                        assertThat(mapper.selectIds(DynamicCondition.toWrapper(query))).isEmpty();
                        assertThat(mapper.selectIds(DynamicCondition.toWrapper(null))).hasSize(3);
                        assertThat(mapper.selectIds(DynamicCondition.toWrapper(new LimitQuery(), SortBy.of("-id"))))
                                .containsExactly(3L);
                        assertThat(mapper.selectGroups(DynamicCondition.toWrapper(new GroupQuery(), SortBy.of("categoryId"))))
                                .containsExactly("A", "B");
                    }
                });
    }

    /** 测试 Mapper 使用 ew 参数名与生产 MyBatis XML 的调用约定一致。 */
    public interface FixtureMapper {
        /** 执行绑定条件并返回 ID，排序及分页均由数据库完成。 */
        List<Long> selectIds(@Param("ew") Wrapper<Object> wrapper);

        /** 执行已验证列名的分组投影，返回分组列。 */
        List<String> selectGroups(@Param("ew") Wrapper<Object> wrapper);
    }

    /** 模拟包含分页基础设施的业务 Query。 */
    private static class ComparisonQuery extends PageDomain {
        private static final String CONSTANT = "ignored";
        private transient String temporary = "ignored";
        private Integer plainValue = 1;
        @QueryField(operator = ENUM_EQ) private Integer enumValue = 2;
        @QueryField(operator = NE) private Integer different = 3;
        @QueryField(operator = LIKE, field = "skuName") private String contains = "word";
        @QueryField(operator = LIKE_RIGHT) private String prefix = "start";
        @QueryField(operator = LIKE_LEFT) private String suffix = "end";
        @QueryField(operator = GT) private Integer greater = 4;
        @QueryField(operator = LT) private Integer less = 5;
        @QueryField(operator = GTE) private Integer minimum = 6;
        @QueryField(operator = LTE) private Integer maximum = 7;
        private String blank = " ";
        @QueryField(ignore = true) private String ignored = "ignored";
    }

    private static class EmptyQuery {
        private String defaultBlank = " ";
        @QueryField(queryEmpty = true) private String explicitBlank = " ";
        private Boolean enabled = false;
        private Integer quantity = 0;
        private String absent;
    }

    private static class EntityQuery extends BaseEntity {
        private Long businessId = 1L;
    }

    private static class ParentQuery {
        private Long parentId = 1L;
        @QueryField(field = "parent_shadow") private Long id = 2L;
    }

    private static class ChildQuery extends ParentQuery {
        @QueryField(field = "child_id") private Long id = 3L;
    }

    private static class RangeQuery {
        @QueryField(operator = BETWEEN, field = "amount")
        private DynamicCondition.BetweenQueryAttribute<Integer> range = new DynamicCondition.BetweenQueryAttribute<>();
    }

    private static class InvalidRangeQuery {
        @QueryField(operator = BETWEEN) private String range = "invalid";
    }

    private static class MembershipQuery {
        @QueryField(operator = IN, field = "inIds") private Object in;
        @QueryField(operator = NOT_IN, field = "notInIds") private Object notIn;
    }

    private static class StructuralQuery {
        @QueryField(operator = NOT_NULL) private Boolean present = true;
        @QueryField(operator = IS_NULL) private Boolean missing = true;
        @QueryField(operator = IS_NULL) private Boolean disabled = false;
        @QueryField(operator = FIND_IN_SET, field = "categoryIds") private String member;
        @QueryField(operator = GROUP) private String grouping = "categoryId";
        @QueryField(operator = LIMIT) private Object limit = 5L;
    }

    private static class InvalidNullQuery {
        @QueryField(operator = IS_NULL) private String missing = "true";
    }

    private static class InvalidColumnQuery {
        @QueryField(field = "id OR 1=1") private Integer id = 1;
    }

    @Data
    private static class FixtureQuery {
        @QueryField(operator = LIKE) private String name;
    }

    private static class LimitQuery {
        @QueryField(operator = LIMIT) private Integer limit = 1;
    }

    private static class GroupQuery {
        @QueryField(operator = GROUP) private String grouping = "categoryId";
    }
}
