package com.ruoyi.quartz.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import org.quartz.SchedulerException;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.quartz.model.bo.SysJobBO;
import com.ruoyi.quartz.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.quartz.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.quartz.facade.ISysJobFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysJobFacade implements ISysJobFacade {
    private final ISysJobService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysJobFacade(ISysJobService service) {
        this.service = service;
    }

    /**
     * selectJobList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysJobBO> queryJobList(SysJobBO job) {
        return BoundaryPages.map(service.selectJobList(BoundaryConvert.INSTANCE.toSysJob(job)), BoundaryConvert.INSTANCE::toSysJobBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param job 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysJobBO> queryJobList(SysJobBO job, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectJobList(BoundaryConvert.INSTANCE.toSysJob(job), pageDomain), BoundaryConvert.INSTANCE::toSysJobBOList);
    }

    /**
     * selectJobById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param jobId jobId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysJobBO queryJobById(Long jobId) {
        return BoundaryConvert.INSTANCE.toSysJobBO(service.selectJobById(jobId));
    }

    /**
     * deleteJobByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param jobIds jobIds 领域参数
     * @throws SchedulerException 原领域能力声明的失败
     */
    @Override
    public void removeJobByIds(Long[] jobIds) throws SchedulerException {
        service.deleteJobByIds(jobIds);
    }

    /**
     * changeStatus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     */
    @Override
    public int changeStatus(SysJobBO job) throws SchedulerException {
        return service.changeStatus(BoundaryConvert.INSTANCE.toSysJob(job));
    }

    /**
     * run：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     */
    @Override
    public boolean run(SysJobBO job) throws SchedulerException {
        return service.run(BoundaryConvert.INSTANCE.toSysJob(job));
    }

    /**
     * insertJob：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     * @throws TaskException      原领域能力声明的失败
     */
    @Override
    public int createJob(SysJobBO job) throws SchedulerException, TaskException {
        return service.insertJob(BoundaryConvert.INSTANCE.toSysJob(job));
    }

    /**
     * updateJob：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     * @throws TaskException      原领域能力声明的失败
     */
    @Override
    public int modifyJob(SysJobBO job) throws SchedulerException, TaskException {
        return service.updateJob(BoundaryConvert.INSTANCE.toSysJob(job));
    }
}
