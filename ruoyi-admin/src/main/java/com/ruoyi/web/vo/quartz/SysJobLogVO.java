package com.ruoyi.web.vo.quartz;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定时任务调度日志表 sys_job_log
 *
 * @author ruoyi
 */
public class SysJobLogVO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Excel(name = "日志序号")
    private Long jobLogId;

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
     * 日志信息
     */
    @Excel(name = "日志信息")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @Excel(name = "执行状态", readConverterExp = "0=正常,1=失败")
    private String status;

    /**
     * 异常信息
     */
    @Excel(name = "异常信息")
    private String exceptionInfo;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 读取并返回当前对象的 JobLogId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getJobLogId() {
        return jobLogId;
    }

    /**
     * 将参数 jobLogId 写入当前对象的 JobLogId 属性，不执行持久化。
     *
     * @param jobLogId jobLogId 请求参数或领域数据
     */
    public void setJobLogId(Long jobLogId) {
        this.jobLogId = jobLogId;
    }

    /**
     * 读取并返回当前对象的 JobName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
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
     * 读取并返回当前对象的 JobMessage 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getJobMessage() {
        return jobMessage;
    }

    /**
     * 将参数 jobMessage 写入当前对象的 JobMessage 属性，不执行持久化。
     *
     * @param jobMessage jobMessage 请求参数或领域数据
     */
    public void setJobMessage(String jobMessage) {
        this.jobMessage = jobMessage;
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

    /**
     * 读取并返回当前对象的 ExceptionInfo 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getExceptionInfo() {
        return exceptionInfo;
    }

    /**
     * 将参数 exceptionInfo 写入当前对象的 ExceptionInfo 属性，不执行持久化。
     *
     * @param exceptionInfo exceptionInfo 请求参数或领域数据
     */
    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    /**
     * 读取并返回当前对象的 StartTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 将参数 startTime 写入当前对象的 StartTime 属性，不执行持久化。
     *
     * @param startTime startTime 请求参数或领域数据
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 读取并返回当前对象的 EndTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 将参数 endTime 写入当前对象的 EndTime 属性，不执行持久化。
     *
     * @param endTime endTime 请求参数或领域数据
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
