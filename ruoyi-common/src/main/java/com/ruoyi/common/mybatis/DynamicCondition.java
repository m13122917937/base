package com.ruoyi.common.mybatis;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.model.SortBy;
import lombok.Data;

/**
 * 从领域 Query 字段和 QueryField 注解构造参数绑定的查询条件。
 * 仅供服务端查询使用，不接收客户端 Wrapper，不自动附加数据权限，不用于更新或删除。
 * 反射只读取查询元数据和值，不承担跨层对象转换；不声明事务，不访问数据库。
 */
public final class DynamicCondition {
    private static final Pattern COLUMN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?");

    /** 阻止实例化；工具没有共享的可变查询状态。 */
    private DynamicCondition() {
    }

    /** 与参考项目保持命名一致的条件操作符。 */
    public enum Operator {
        EQ, ENUM_EQ, NE, LIKE, LIKE_LEFT, LIKE_RIGHT, GT, LT, GTE, LTE,
        BETWEEN, NOT_NULL, IS_NULL, GROUP, IN, NOT_IN, FIND_IN_SET, LIMIT
    }

    /** 区间参数；单端非空时使用大于等于或小于等于，两端为空时拒绝查询。 */
    @Data
    public static class BetweenQueryAttribute<T extends Comparable<T>> {
        private T start;
        private T end;
    }

    /**
     * 按字段声明及继承顺序构建条件，再追加排序；null 查询仍可单独排序。
     * 跳过静态、transient、合成、忽略字段和若依分页/实体基类；同名字段以子类为准。
     * 非法列名、操作符值类型、空集合或不完整结构参数立即抛出 IllegalArgumentException。
     *
     * @param query 领域查询对象，null 表示没有过滤条件
     * @param sort 服务端排序参数，客户端属性须先经过业务白名单映射
     * @param <T> Mapper 查询结果的实体类型
     * @return 每次新建的 QueryWrapper，仅在 Service/Mapper 内使用
     */
    public static <T> QueryWrapper<T> toWrapper(Object query, SortBy sort) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (query != null) {
            Set<String> names = new HashSet<>();
            Class<?> type = query.getClass();
            while (type != null && type != Object.class && type != PageDomain.class && type != BaseEntity.class) {
                for (Field field : type.getDeclaredFields()) {
                    if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())
                            && !Modifier.isTransient(field.getModifiers()) && names.add(field.getName())) {
                        appendField(wrapper, query, field);
                    }
                }
                type = type.getSuperclass();
            }
        }
        appendSort(wrapper, sort);
        return wrapper;
    }

    /** 使用默认无排序策略构造条件；校验及失败语义与双参数重载一致。 */
    public static <T> QueryWrapper<T> toWrapper(Object query) {
        return toWrapper(query, null);
    }

    /** 先检查忽略策略，再读取字段；null 和默认空白字符串不生成条件，读取失败立即报错。 */
    private static <T> void appendField(QueryWrapper<T> wrapper, Object query, Field field) {
        QueryField annotation = field.getAnnotation(QueryField.class);
        if (annotation != null && annotation.ignore()) {
            return;
        }
        Object value;
        try {
            if (!field.trySetAccessible()) {
                throw new IllegalArgumentException("无法读取查询字段：" + field.getName());
            }
            value = field.get(query);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("无法读取查询字段：" + field.getName(), ex);
        }
        if (value == null || (value instanceof String text && text.isBlank()
                && (annotation == null || !annotation.queryEmpty()))) {
            return;
        }
        Operator operator = annotation == null ? Operator.EQ : annotation.operator();
        String name = annotation == null || annotation.field().isBlank() ? field.getName() : annotation.field();
        appendCondition(wrapper, column(name), operator, value);
    }

    /** 使用 Wrapper 参数绑定追加比较或结构条件，所有列名已由调用方验证。 */
    private static <T> void appendCondition(QueryWrapper<T> wrapper, String column, Operator operator, Object value) {
        switch (operator) {
            case EQ, ENUM_EQ -> wrapper.eq(column, value);
            case NE -> wrapper.ne(column, value);
            case LIKE -> wrapper.like(column, value);
            case LIKE_LEFT -> wrapper.likeLeft(column, value);
            case LIKE_RIGHT -> wrapper.likeRight(column, value);
            case GT -> wrapper.gt(column, value);
            case LT -> wrapper.lt(column, value);
            case GTE -> wrapper.ge(column, value);
            case LTE -> wrapper.le(column, value);
            case BETWEEN -> appendBetween(wrapper, column, value);
            case IS_NULL, NOT_NULL -> {
                if (!(value instanceof Boolean enabled)) {
                    throw new IllegalArgumentException("空值条件必须使用 Boolean 开关");
                }
                if (enabled) {
                    if (operator == Operator.IS_NULL) {
                        wrapper.isNull(column);
                    } else {
                        wrapper.isNotNull(column);
                    }
                }
            }
            case IN, NOT_IN -> {
                List<Object> values = membership(value);
                if (operator == Operator.IN) {
                    wrapper.in(column, values);
                } else {
                    wrapper.notIn(column, values);
                }
            }
            case FIND_IN_SET -> wrapper.apply("FIND_IN_SET({0}, " + column + ")", value);
            case GROUP -> appendGroup(wrapper, value);
            case LIMIT -> {
                if (!(value instanceof Integer || value instanceof Long) || ((Number) value).longValue() <= 0) {
                    throw new IllegalArgumentException("LIMIT 必须为正整数");
                }
                wrapper.last("LIMIT " + value);
            }
        }
    }

    /** 验证区间参数后生成双端或单端条件；两端为空或起点大于终点时拒绝执行。 */
    private static <T> void appendBetween(QueryWrapper<T> wrapper, String column, Object value) {
        if (!(value instanceof BetweenQueryAttribute<?> range)) {
            throw new IllegalArgumentException("BETWEEN 必须使用 BetweenQueryAttribute");
        }
        if (range.getStart() == null && range.getEnd() == null) {
            throw new IllegalArgumentException("区间端点不能同时为空");
        }
        if (range.getStart() != null && range.getEnd() != null) {
            appendClosedRange(wrapper, column, range);
        } else if (range.getStart() != null) {
            wrapper.ge(column, range.getStart());
        } else {
            wrapper.le(column, range.getEnd());
        }
    }

    /** 比较同类型端点后绑定完整区间；非法逆序区间不生成 SQL。 */
    private static <T, V extends Comparable<V>> void appendClosedRange(QueryWrapper<T> wrapper, String column,
            BetweenQueryAttribute<V> range) {
        if (range.getStart().compareTo(range.getEnd()) > 0) {
            throw new IllegalArgumentException("区间起点不能大于终点");
        }
        wrapper.between(column, range.getStart(), range.getEnd());
    }

    /** 统一集合、对象/基本类型数组及单值；空集合或 null 元素直接拒绝，防止过滤条件丢失。 */
    private static List<Object> membership(Object value) {
        List<Object> values = new ArrayList<>();
        if (value instanceof Collection<?> collection) {
            values.addAll(collection);
        } else if (value.getClass().isArray()) {
            for (int index = 0; index < Array.getLength(value); index++) {
                values.add(Array.get(value, index));
            }
        } else {
            values.add(value);
        }
        if (values.isEmpty() || values.stream().anyMatch(item -> item == null)) {
            throw new IllegalArgumentException("IN/NOT IN 参数不能为空集合或包含 null");
        }
        return values;
    }

    /** 仅接受服务端指定的逗号分隔列名，验证后同时设置投影和分组，不允许函数或 SQL 表达式。 */
    private static <T> void appendGroup(QueryWrapper<T> wrapper, Object value) {
        if (!(value instanceof String columns) || columns.isBlank()) {
            throw new IllegalArgumentException("GROUP 必须使用非空列名字符串");
        }
        String[] names = columns.split(",", -1);
        for (int index = 0; index < names.length; index++) {
            names[index] = column(names[index].trim());
        }
        wrapper.select(names).groupBy(List.of(names));
    }

    /** 按输入顺序验证排序字段并追加方向；空排序不追加 SQL，非法项立即拒绝。 */
    private static <T> void appendSort(QueryWrapper<T> wrapper, SortBy sort) {
        if (sort == null || sort.isEmpty()) {
            return;
        }
        for (SortBy.Field order : sort.getOrders()) {
            if (order == null) {
                throw new IllegalArgumentException("排序项不能为空");
            }
            wrapper.orderBy(true, order.getDirection() != SortBy.Direction.DESC, column(order.getProperty()));
        }
    }

    /** 只允许列名或别名.列名，并将驼峰转换为下划线；拒绝 SQL 片段而不静默清洗。 */
    private static String column(String name) {
        if (name == null || !COLUMN.matcher(name).matches()) {
            throw new IllegalArgumentException("查询列名格式不正确");
        }
        return StringUtils.camelToUnderline(name);
    }
}
