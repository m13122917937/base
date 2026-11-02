package com.ruoyi.web.form.quartz;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.util.CronUtils;

/**
 * 定时任务调度表 sys_job
 *
 * @author ruoyi
 */
public class SysJobForm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @Excel(name = "任务序号", cellType = ColumnType.NUMERIC)
    private Long jobId;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String jobName;

    /**
     * 任务组名
     */
    @Excel(name = "任务组名")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @Excel(name = "调用目标字符串")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @Excel(name = "执行表达式 ")
    private String cronExpression;

    /**
     * cron计划策略
     */
    @Excel(name = "计划策略 ", readConverterExp = "0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Excel(name = "并发执行", readConverterExp = "0=允许,1=禁止")
    private String concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @Excel(name = "任务状态", readConverterExp = "0=正常,1=暂停")
    private String status;

    /**
     * 读取并返回当前对象的 JobId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getJobId() {
        return jobId;
    }

    /**
     * 将参数 jobId 写入当前对象的 JobId 属性，不执行持久化。
     *
     * @param jobId jobId 请求参数或领域数据
     */
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    /**
     * 读取并返回当前对象的 JobName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(min = 0, max = 64, message = "任务名称不能超过64个字符")
    public String getJobName() {
        return jobName;
    }

    /**
     * 将参数 jobName 写入当前对象的 JobName 属性，不执行持久化。
     *
     * @param jobName jobName 请求参数或领域数据
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * 读取并返回当前对象的 JobGroup 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getJobGroup() {
        return jobGroup;
    }

    /**
     * 将参数 jobGroup 写入当前对象的 JobGroup 属性，不执行持久化。
     *
     * @param jobGroup jobGroup 请求参数或领域数据
     */
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    /**
     * 读取并返回当前对象的 InvokeTarget 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(min = 0, max = 500, message = "调用目标字符串长度不能超过500个字符")
    public String getInvokeTarget() {
        return invokeTarget;
    }

    /**
     * 将参数 invokeTarget 写入当前对象的 InvokeTarget 属性，不执行持久化。
     *
     * @param invokeTarget invokeTarget 请求参数或领域数据
     */
    public void setInvokeTarget(String invokeTarget) {
        this.invokeTarget = invokeTarget;
    }

    /**
     * 读取并返回当前对象的 CronExpression 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(min = 0, max = 255, message = "Cron执行表达式不能超过255个字符")
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * 将参数 cronExpression 写入当前对象的 CronExpression 属性，不执行持久化。
     *
     * @param cronExpression cronExpression 请求参数或领域数据
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * 读取并返回当前对象的 NextValidTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime() {
        if (StringUtils.isNotEmpty(cronExpression)) {
            return CronUtils.getNextExecution(cronExpression);
        }
        return null;
    }

    /**
     * 读取并返回当前对象的 MisfirePolicy 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getMisfirePolicy() {
        return misfirePolicy;
    }

    /**
     * 将参数 misfirePolicy 写入当前对象的 MisfirePolicy 属性，不执行持久化。
     *
     * @param misfirePolicy misfirePolicy 请求参数或领域数据
     */
    public void setMisfirePolicy(String misfirePolicy) {
        this.misfirePolicy = misfirePolicy;
    }

    /**
     * 读取并返回当前对象的 Concurrent 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getConcurrent() {
        return concurrent;
    }

    /**
     * 将参数 concurrent 写入当前对象的 Concurrent 属性，不执行持久化。
     *
     * @param concurrent concurrent 请求参数或领域数据
     */
    public void setConcurrent(String concurrent) {
        this.concurrent = concurrent;
    }

    /**
     * 读取并返回当前对象的 Status 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getStatus() {
        return status;
    }

    /**
     * 将参数 status 写入当前对象的 Status 属性，不执行持久化。
     *
     * @param status status 请求参数或领域数据
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
