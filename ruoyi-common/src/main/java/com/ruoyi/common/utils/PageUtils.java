package com.ruoyi.common.utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.utils.sql.SqlUtil;

/**
 * Service 分页工具；只使用显式分页参数，不读取 HTTP 请求。
 */
public final class PageUtils {
    /**
     * 禁止实例化，分页状态只通过静态方法管理。
     */
    private PageUtils() {
    }

    /**
     * 校验排序后启动下一次 Mapper 查询的分页；缺失或非正的页码、页大小分别使用 1、10。
     * 调用方必须在 finally 中清理，优先使用带查询回调的重载。
     *
     * @param pageDomain 显式传入的通用分页参数，不允许为 null
     * @throws com.ruoyi.common.exception.UtilException 排序包含非法字符时拒绝查询
     */
    public static void startPage(PageDomain pageDomain) {
        Objects.requireNonNull(pageDomain, "分页参数不能为空");
        int pageNum = pageDomain.getPageNum() == null || pageDomain.getPageNum() < 1
                ? 1 : pageDomain.getPageNum();
        int pageSize = pageDomain.getPageSize() == null || pageDomain.getPageSize() < 1
                ? 10 : pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(pageDomain.getReasonable());
    }

    /**
     * 在 Service 的目标 Mapper 查询前开启分页，返回保留 Page 元数据的列表，并始终清理线程状态。
     * 回调只应执行目标列表查询；参数为 null 时不分页，异常原样向上传递，不声明事务。
     *
     * @param pageDomain 通用分页参数，null 表示全量查询
     * @param query 目标列表查询，不能为 null
     * @param <T> 列表元素类型
     * @return 查询原始列表，保留数据库总数
     */
    public static <T> List<T> startPage(PageDomain pageDomain, Supplier<List<T>> query) {
        try {
            Objects.requireNonNull(query, "分页查询不能为空");
            if (pageDomain != null) {
                startPage(pageDomain);
            } else {
                clearPage();
            }
            return query.get();
        } finally {
            clearPage();
        }
    }

    /**
     * 清理当前线程的分页状态，供手动开启分页的调用方在 finally 中使用。
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }
}
