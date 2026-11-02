package com.ruoyi.biz.quartz;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.quartz.model.bo.SysJobLogBO;
import com.ruoyi.quartz.facade.ISysJobLogFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.quartz.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysJobLogController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysJobLogBiz {

    private final ISysJobLogFacade jobLogFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysJobLogBiz(ISysJobLogFacade jobLogFacade) {
        this.jobLogFacade = jobLogFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 查询定时任务调度日志列表：按原顺序执行 selectJobLogList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param sysJobLog sysJobLog 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysJobLogBO> list(SysJobLogBO sysJobLog, PageDomain pageDomain) {
        List<SysJobLogBO> list = jobLogFacade.queryJobLogList(sysJobLog, pageDomain);
        return list;
    }

    /**
     * 导出定时任务调度日志列表：按原顺序执行 selectJobLogList、返回导出数据 等步骤，并保留原判断、返回及异常语义。
     *
     * @param sysJobLog sysJobLog 请求参数或领域数据
     */
    public List<SysJobLogBO> export(SysJobLogBO sysJobLog) {
        return jobLogFacade.queryJobLogList(sysJobLog);
    }

    /**
     * 根据调度编号获取详细信息：按原顺序执行 selectJobLogById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param jobLogId jobLogId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysJobLogBO getInfo(Long jobLogId) {
        return jobLogFacade.queryJobLogById(jobLogId);
    }

    /**
     * 删除定时任务调度日志：按原顺序执行 deleteJobLogByIds 等步骤，并保留原判断、返回及异常语义。
     *
     * @param jobLogIds jobLogIds 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int remove(Long[] jobLogIds) {
        return jobLogFacade.removeJobLogByIds(jobLogIds);
    }

    /**
     * 清空定时任务调度日志：按原顺序执行 cleanJobLog、业务动作等步骤，并保留原判断、返回及异常语义。
     */
    public void clean() {
        jobLogFacade.cleanJobLog();
    }
}
