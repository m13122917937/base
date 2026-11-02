package com.ruoyi.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 有序的查询排序参数；客户端字段须先由业务层映射到允许的数据库列。 */
@Data
public class SortBy implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Field> orders = new ArrayList<>();

    /** 检查是否没有排序项；显式设置为 null 也视为无排序。 */
    public boolean isEmpty() {
        return orders == null || orders.isEmpty();
    }

    /** 按输入顺序加入非重复排序项，null 方向按升序处理，空字段直接拒绝。 */
    public SortBy addField(Direction direction, String property) {
        if (property == null || property.isBlank()) {
            throw new IllegalArgumentException("排序字段不能为空");
        }
        Field field = new Field(direction == null ? Direction.ASC : direction, property.trim());
        if (orders == null) {
            orders = new ArrayList<>();
        }
        if (!orders.contains(field)) {
            orders.add(field);
        }
        return this;
    }

    /** 按原顺序返回指定方向的属性名，不修改排序参数。 */
    public List<String> get(Direction direction) {
        return isEmpty() ? List.of() : orders.stream()
                .filter(field -> field.getDirection() == direction).map(Field::getProperty).toList();
    }

    /**
     * 解析逗号分隔的 name asc/name desc 或 +name/-name，未指定方向时升序。
     * 空输入返回无排序；非法方向和空排序项抛出异常，列名在构造 Wrapper 时再次校验。
     */
    public static SortBy of(String orderBy) {
        SortBy sort = new SortBy();
        if (orderBy == null || orderBy.isBlank()) {
            return sort;
        }
        for (String item : orderBy.split(",", -1)) {
            String value = item.trim();
            if (value.startsWith("+") || value.startsWith("-")) {
                sort.addField(value.startsWith("-") ? Direction.DESC : Direction.ASC, value.substring(1));
            } else {
                String[] parts = value.split("\\s+");
                if (parts.length > 2) {
                    throw new IllegalArgumentException("排序格式不正确");
                }
                Direction direction;
                try {
                    direction = parts.length == 1 ? Direction.ASC
                            : Direction.valueOf(parts[1].toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("排序方向只支持 ASC 或 DESC", ex);
                }
                sort.addField(direction, parts[0]);
            }
        }
        return sort;
    }

    /** 单个排序项，按方向和属性名判重。 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Field implements Serializable {
        private static final long serialVersionUID = 1L;
        private Direction direction;
        private String property;
    }

    /** SQL 排序方向，禁止直接传入任意 SQL 文本。 */
    public enum Direction {
        ASC, DESC
    }
}
