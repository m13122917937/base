package com.ruoyi.web.controller.quartz;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.vo.quartz.SysJobVO;
import jakarta.servlet.http.HttpServletResponse;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.biz.quartz.SysJobBiz;
import com.ruoyi.web.convert.WebBoundaryConvert;
import com.ruoyi.web.convert.WebResponses;
import com.ruoyi.web.form.system.*;
import com.ruoyi.web.form.quartz.*;
import com.ruoyi.web.form.framework.*;

/**
 * HTTP 协议入口；业务调用只通过 Biz，保留既有权限和审计注解。
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController {
    private final SysJobBiz biz;

    /**
     * 通过构造器接收应用入口，不在控制器内访问领域服务或数据库。
     *
     * @param biz 应用业务入口
     */
    public SysJobController(SysJobBiz biz) {
        this.biz = biz;
    }

    /**
     * 读取请求分页参数并经 Biz、Facade 传给 Service，查询完成后保留原分页响应。
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param sysJob sysJob 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysJobForm sysJob) {
        return WebResponses.toWeb(getDataTable(biz.list(WebBoundaryConvert.INSTANCE.toSysJobBO(sysJob), getPageDomain())));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param response response 请求参数或领域数据
     * @param sysJob   sysJob 请求参数或领域数据
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJobForm sysJob) {
        java.util.List<SysJobVO> rows = biz.export(WebBoundaryConvert.INSTANCE.toSysJobBO(sysJob)).stream()
                .map(WebBoundaryConvert.INSTANCE::toSysJobVO).toList();
        new ExcelUtil<SysJobVO>(SysJobVO.class).exportExcel(response, rows, "定时任务");
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param jobId jobId 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping(value = "/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId) {
        return WebResponses.toWeb(AjaxResult.success(biz.getInfo(jobId)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param job job 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     * @throws SchedulerException 原处理过程声明的失败
     * @throws TaskException      原处理过程声明的失败
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysJobForm job) throws SchedulerException, TaskException {
        return toAjax(biz.add(WebBoundaryConvert.INSTANCE.toSysJobBO(job)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param job job 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     * @throws SchedulerException 原处理过程声明的失败
     * @throws TaskException      原处理过程声明的失败
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysJobForm job) throws SchedulerException, TaskException {
        return toAjax(biz.edit(WebBoundaryConvert.INSTANCE.toSysJobBO(job)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param job job 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     * @throws SchedulerException 原处理过程声明的失败
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysJobForm job) throws SchedulerException {
        return toAjax(biz.changeStatus(WebBoundaryConvert.INSTANCE.toSysJobBO(job)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param job job 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     * @throws SchedulerException 原处理过程声明的失败
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public AjaxResult run(@RequestBody SysJobForm job) throws SchedulerException {
        biz.run(WebBoundaryConvert.INSTANCE.toSysJobBO(job));
        return AjaxResult.success();
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param jobIds jobIds 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     * @throws SchedulerException 原处理过程声明的失败
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException {
        biz.remove(jobIds);
        return AjaxResult.success();
    }
}
