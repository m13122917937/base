package com.ruoyi.quartz.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.quartz.model.bo.SysJobLogBO;
import com.ruoyi.quartz.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.quartz.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.quartz.service.ISysJobLogService;
import com.ruoyi.quartz.facade.ISysJobLogFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysJobLogFacade implements ISysJobLogFacade {
    private final ISysJobLogService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysJobLogFacade(ISysJobLogService service) {
        this.service = service;
    }

    /**
     * selectJobLogList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param jobLog jobLog 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysJobLogBO> queryJobLogList(SysJobLogBO jobLog) {
        return BoundaryPages.map(service.selectJobLogList(BoundaryConvert.INSTANCE.toSysJobLog(jobLog)), BoundaryConvert.INSTANCE::toSysJobLogBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param jobLog 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysJobLogBO> queryJobLogList(SysJobLogBO jobLog, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectJobLogList(BoundaryConvert.INSTANCE.toSysJobLog(jobLog), pageDomain), BoundaryConvert.INSTANCE::toSysJobLogBOList);
    }

    /**
     * selectJobLogById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param jobLogId jobLogId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysJobLogBO queryJobLogById(Long jobLogId) {
        return BoundaryConvert.INSTANCE.toSysJobLogBO(service.selectJobLogById(jobLogId));
    }

    /**
     * deleteJobLogByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param logIds logIds 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeJobLogByIds(Long[] logIds) {
        return service.deleteJobLogByIds(logIds);
    }

    /**
     * cleanJobLog：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     */
    @Override
    public void cleanJobLog() {
        service.cleanJobLog();
    }
}
