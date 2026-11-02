package com.ruoyi.system.facade;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysNoticeBO;
import com.ruoyi.system.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysNoticeFacade {
    /**
     * selectNoticeById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param noticeId noticeId 领域参数
     * @return 原领域能力的处理结果
     */
    SysNoticeBO queryNoticeById(Long noticeId);

    /**
     * selectNoticeList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param notice notice 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysNoticeBO> queryNoticeList(SysNoticeBO notice);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param notice 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysNoticeBO> queryNoticeList(SysNoticeBO notice, PageDomain pageDomain);

    /**
     * insertNotice：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param notice notice 领域参数
     * @return 原领域能力的处理结果
     */
    int createNotice(SysNoticeBO notice);

    /**
     * updateNotice：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param notice notice 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyNotice(SysNoticeBO notice);

    /**
     * deleteNoticeByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param noticeIds noticeIds 领域参数
     * @return 原领域能力的处理结果
     */
    int removeNoticeByIds(Long[] noticeIds);
}
