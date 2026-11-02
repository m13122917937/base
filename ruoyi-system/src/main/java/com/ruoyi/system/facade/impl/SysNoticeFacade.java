package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysNoticeBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysNoticeService;
import com.ruoyi.system.facade.ISysNoticeFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysNoticeFacade implements ISysNoticeFacade {
    private final ISysNoticeService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysNoticeFacade(ISysNoticeService service) {
        this.service = service;
    }

    /**
     * selectNoticeById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param noticeId noticeId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysNoticeBO queryNoticeById(Long noticeId) {
        return BoundaryConvert.INSTANCE.toSysNoticeBO(service.selectNoticeById(noticeId));
    }

    /**
     * selectNoticeList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param notice notice 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysNoticeBO> queryNoticeList(SysNoticeBO notice) {
        return BoundaryPages.map(service.selectNoticeList(BoundaryConvert.INSTANCE.toSysNotice(notice)), BoundaryConvert.INSTANCE::toSysNoticeBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param notice 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysNoticeBO> queryNoticeList(SysNoticeBO notice, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectNoticeList(BoundaryConvert.INSTANCE.toSysNotice(notice), pageDomain), BoundaryConvert.INSTANCE::toSysNoticeBOList);
    }

    /**
     * insertNotice：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param notice notice 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createNotice(SysNoticeBO notice) {
        return service.insertNotice(BoundaryConvert.INSTANCE.toSysNotice(notice));
    }

    /**
     * updateNotice：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param notice notice 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyNotice(SysNoticeBO notice) {
        return service.updateNotice(BoundaryConvert.INSTANCE.toSysNotice(notice));
    }

    /**
     * deleteNoticeByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param noticeIds noticeIds 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeNoticeByIds(Long[] noticeIds) {
        return service.deleteNoticeByIds(noticeIds);
    }
}
