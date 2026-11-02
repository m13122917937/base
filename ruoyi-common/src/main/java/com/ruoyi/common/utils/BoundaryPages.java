package com.ruoyi.common.utils;

import java.util.List;
import java.util.function.Function;

import com.github.pagehelper.Page;

/**
 * 跨层转换时保留 PageHelper 查询元数据，不依赖任何具体领域。
 */
public final class BoundaryPages {
    /**
     * 限制实例化，分页转换只通过静态纯函数调用。
     */
    private BoundaryPages() {
    }

    /**
     * 先转换列表元素，再在原列表为 Page 时复制页码、页大小和总数，避免 DTO 转换丢失 total。
     * 不发起查询，也不修改 PageHelper 的线程变量。
     *
     * @param source    原查询列表，允许为 null
     * @param converter 编译期列表转换函数
     * @param <S>       Service 内部类型
     * @param <T>       领域或 Web 边界类型
     * @return 保留原分页总数的转换结果，null 输入返回 null
     */
    public static <S, T> List<T> map(List<S> source, Function<List<S>, List<T>> converter) {
        if (source == null) {
            return null;
        }
        List<T> values = converter.apply(source);
        if (source instanceof Page<?>) {
            Page<?> original = (Page<?>) source;
            Page<T> page = new Page<T>(original.getPageNum(), original.getPageSize());
            page.setTotal(original.getTotal());
            page.addAll(values);
            return page;
        }
        return values;
    }
}
