package com.ruoyi.biz.quartz;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.List;

import org.quartz.SchedulerException;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.model.bo.SysJobBO;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.ruoyi.quartz.facade.ISysJobFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.quartz.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysJobController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysJobBiz {

    private final ISysJobFacade jobFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysJobBiz(ISysJobFacade jobFacade) {
        this.jobFacade = jobFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 查询定时任务列表：按原顺序执行 selectJobList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param sysJob sysJob 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysJobBO> list(SysJobBO sysJob, PageDomain pageDomain) {
        List<SysJobBO> list = jobFacade.queryJobList(sysJob, pageDomain);
        return list;
    }

    /**
     * 导出定时任务列表：按原顺序执行 selectJobList、返回导出数据 等步骤，并保留原判断、返回及异常语义。
     *
     * @param sysJob sysJob 请求参数或领域数据
     */
    public List<SysJobBO> export(SysJobBO sysJob) {
        return jobFacade.queryJobList(sysJob);
    }

    /**
     * 获取定时任务详细信息：按原顺序执行 selectJobById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param jobId jobId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysJobBO getInfo(Long jobId) {
        return jobFacade.queryJobById(jobId);
    }

    /**
     * 新增定时任务：按原顺序执行 isValid、getCronExpression、getJobName、containsIgnoreCase、getInvokeTarget、containsAnyIgnoreCase、whiteList 等步骤，并保留原判断、返回及异常语义。
     *
     * @param job job 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     * @throws SchedulerException 原处理过程声明的失败
     * @throws TaskException      原处理过程声明的失败
     */
    public int add(SysJobBO job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setCreateBy(SecurityUtils.getUsername());
        return jobFacade.createJob(job);
    }

    /**
     * 修改定时任务：按原顺序执行 isValid、getCronExpression、getJobName、containsIgnoreCase、getInvokeTarget、containsAnyIgnoreCase、whiteList 等步骤，并保留原判断、返回及异常语义。
     *
     * @param job job 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     * @throws SchedulerException 原处理过程声明的失败
     * @throws TaskException      原处理过程声明的失败
     */
    public int edit(SysJobBO job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setUpdateBy(SecurityUtils.getUsername());
        return jobFacade.modifyJob(job);
    }

    /**
     * 定时任务状态修改：按原顺序执行 selectJobById、getJobId、setStatus、getStatus、changeStatus 等步骤，并保留原判断、返回及异常语义。
     *
     * @param job job 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     * @throws SchedulerException 原处理过程声明的失败
     */
    public int changeStatus(SysJobBO job) throws SchedulerException {
        SysJobBO newJob = jobFacade.queryJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return jobFacade.changeStatus(newJob);
    }

    /**
     * 定时任务立即执行一次：按原顺序执行 run、error 等步骤，并保留原判断、返回及异常语义。
     *
     * @param job job 请求参数或领域数据
     * @throws SchedulerException 原处理过程声明的失败
     */
    public void run(SysJobBO job) throws SchedulerException {
        boolean result = jobFacade.run(job);
        if (!result) {
            throw new ServiceException("任务不存在或已过期！");
        }
    }

    /**
     * 删除定时任务：按原顺序执行 deleteJobByIds、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param jobIds jobIds 请求参数或领域数据
     * @throws SchedulerException 原处理过程声明的失败
     */
    public void remove(Long[] jobIds) throws SchedulerException {
        jobFacade.removeJobByIds(jobIds);
    }
}
