package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysOperLogBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysOperLogService;
import com.ruoyi.system.facade.ISysOperLogFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysOperLogFacade implements ISysOperLogFacade {
    private final ISysOperLogService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysOperLogFacade(ISysOperLogService service) {
        this.service = service;
    }

    /**
     * selectOperLogList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param operLog operLog 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysOperLogBO> queryOperLogList(SysOperLogBO operLog) {
        return BoundaryPages.map(service.selectOperLogList(BoundaryConvert.INSTANCE.toSysOperLog(operLog)), BoundaryConvert.INSTANCE::toSysOperLogBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param operLog 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysOperLogBO> queryOperLogList(SysOperLogBO operLog, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectOperLogList(BoundaryConvert.INSTANCE.toSysOperLog(operLog), pageDomain), BoundaryConvert.INSTANCE::toSysOperLogBOList);
    }

    /**
     * deleteOperLogByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param operIds operIds 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeOperLogByIds(Long[] operIds) {
        return service.deleteOperLogByIds(operIds);
    }

    /**
     * cleanOperLog：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     */
    @Override
    public void cleanOperLog() {
        service.cleanOperLog();
    }
}
