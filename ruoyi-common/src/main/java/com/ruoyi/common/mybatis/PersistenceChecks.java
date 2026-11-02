package com.ruoyi.common.mybatis;

import java.util.Arrays;
import java.util.List;

/** 在构造通用写入条件前检查业务主键，避免空集合或 null 条件扩大写入范围。 */
public final class PersistenceChecks {
    /** 阻止实例化，无共享可变状态。 */
    private PersistenceChecks() {
    }

    /** 返回非空主键；null 输入立即拒绝，不调用数据库。 */
    public static <T> T id(T value) {
        if (value == null) {
            throw new IllegalArgumentException("记录主键不能为空");
        }
        return value;
    }

    /** 返回原顺序主键列表；数组为空、空数组或含 null 元素时立即拒绝。 */
    public static <T> List<T> ids(T[] values) {
        if (values == null || values.length == 0 || Arrays.stream(values).anyMatch(value -> value == null)) {
            throw new IllegalArgumentException("记录主键集合不能为空或包含 null");
        }
        return Arrays.asList(values);
    }
}
