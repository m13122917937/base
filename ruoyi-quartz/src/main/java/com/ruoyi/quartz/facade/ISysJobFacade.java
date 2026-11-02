package com.ruoyi.quartz.facade;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import org.quartz.SchedulerException;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.quartz.model.bo.SysJobBO;
import com.ruoyi.quartz.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysJobFacade {
    /**
     * selectJobList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysJobBO> queryJobList(SysJobBO job);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param job 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysJobBO> queryJobList(SysJobBO job, PageDomain pageDomain);

    /**
     * selectJobById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param jobId jobId 领域参数
     * @return 原领域能力的处理结果
     */
    SysJobBO queryJobById(Long jobId);

    /**
     * deleteJobByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param jobIds jobIds 领域参数
     * @throws SchedulerException 原领域能力声明的失败
     */
    void removeJobByIds(Long[] jobIds) throws SchedulerException;

    /**
     * changeStatus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     */
    int changeStatus(SysJobBO job) throws SchedulerException;

    /**
     * run：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     */
    boolean run(SysJobBO job) throws SchedulerException;

    /**
     * insertJob：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     * @throws TaskException      原领域能力声明的失败
     */
    int createJob(SysJobBO job) throws SchedulerException, TaskException;

    /**
     * updateJob：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param job job 领域参数
     * @return 原领域能力的处理结果
     * @throws SchedulerException 原领域能力声明的失败
     * @throws TaskException      原领域能力声明的失败
     */
    int modifyJob(SysJobBO job) throws SchedulerException, TaskException;
}
