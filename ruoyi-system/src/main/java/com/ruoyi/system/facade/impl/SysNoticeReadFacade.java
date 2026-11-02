package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysNoticeBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.system.convert.NoticeReaderConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysNoticeReadService;
import com.ruoyi.system.facade.ISysNoticeReadFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysNoticeReadFacade implements ISysNoticeReadFacade {
    private final ISysNoticeReadService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysNoticeReadFacade(ISysNoticeReadService service) {
        this.service = service;
    }

    /**
     * markRead：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param noticeId noticeId 领域参数
     * @param userId   userId 领域参数
     */
    @Override
    public void markRead(Long noticeId, Long userId) {
        service.markRead(noticeId, userId);
    }

    /**
     * selectNoticeListWithReadStatus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @param limit  limit 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysNoticeBO> queryNoticeListWithReadStatus(Long userId, int limit) {
        return BoundaryPages.map(service.selectNoticeListWithReadStatus(userId, limit), BoundaryConvert.INSTANCE::toSysNoticeBOList);
    }

    /**
     * markReadBatch：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId    userId 领域参数
     * @param noticeIds noticeIds 领域参数
     */
    @Override
    public void markReadBatch(Long userId, Long[] noticeIds) {
        service.markReadBatch(userId, noticeIds);
    }

    /**
     * selectReadUsersByNoticeId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param noticeId    noticeId 领域参数
     * @param searchValue searchValue 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<NoticeReaderBO> queryReadUsersByNoticeId(Long noticeId, String searchValue) {
        return BoundaryPages.map(service.selectReadUsersByNoticeId(noticeId, searchValue), NoticeReaderConvert.INSTANCE::toBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param noticeId 原查询条件
     * @param searchValue 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<NoticeReaderBO> queryReadUsersByNoticeId(Long noticeId, String searchValue, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectReadUsersByNoticeId(noticeId, searchValue, pageDomain), NoticeReaderConvert.INSTANCE::toBOList);
    }
}
