package com.ruoyi.web.controller.system;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.vo.system.SysRoleVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import com.ruoyi.biz.system.SysRoleBiz;
import com.ruoyi.web.convert.WebBoundaryConvert;
import com.ruoyi.web.convert.WebResponses;
import com.ruoyi.web.form.system.*;
import com.ruoyi.web.form.quartz.*;
import com.ruoyi.web.form.framework.*;

/**
 * HTTP 协议入口；业务调用只通过 Biz，保留既有权限和审计注解。
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {
    private final SysRoleBiz biz;

    /**
     * 通过构造器接收应用入口，不在控制器内访问领域服务或数据库。
     *
     * @param biz 应用业务入口
     */
    public SysRoleController(SysRoleBiz biz) {
        this.biz = biz;
    }

    /**
     * 读取请求分页参数并经 Biz、Facade 传给 Service，查询完成后保留原分页响应。
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param role role 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRoleForm role) {
        return WebResponses.toWeb(getDataTable(biz.list(WebBoundaryConvert.INSTANCE.toSysRoleBO(role), getPageDomain())));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param response response 请求参数或领域数据
     * @param role     role 请求参数或领域数据
     */
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRoleForm role) {
        java.util.List<SysRoleVO> rows = biz.export(WebBoundaryConvert.INSTANCE.toSysRoleBO(role)).stream()
                .map(WebBoundaryConvert.INSTANCE::toSysRoleVO).toList();
        new ExcelUtil<SysRoleVO>(SysRoleVO.class).exportExcel(response, rows, "角色数据");
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param roleId roleId 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId) {
        return WebResponses.toWeb(AjaxResult.success(biz.getInfo(roleId)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param role role 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRoleForm role) {
        return toAjax(biz.add(WebBoundaryConvert.INSTANCE.toSysRoleBO(role)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param role role 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRoleForm role) {
        biz.edit(WebBoundaryConvert.INSTANCE.toSysRoleBO(role));
        return AjaxResult.success();
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param role role 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRoleForm role) {
        return toAjax(biz.dataScope(WebBoundaryConvert.INSTANCE.toSysRoleBO(role)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param role role 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRoleForm role) {
        return toAjax(biz.changeStatus(WebBoundaryConvert.INSTANCE.toSysRoleBO(role)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param roleIds roleIds 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        return toAjax(biz.remove(roleIds));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        return WebResponses.toWeb(AjaxResult.success(biz.optionselect()));
    }

    /**
     * 读取请求分页参数并经 Biz、Facade 传给 Service，查询完成后保留原分页响应。
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param user user 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUserForm user) {
        return WebResponses.toWeb(getDataTable(biz.allocatedList(WebBoundaryConvert.INSTANCE.toSysUserBO(user), getPageDomain())));
    }

    /**
     * 读取请求分页参数并经 Biz、Facade 传给 Service，查询完成后保留原分页响应。
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param user user 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUserForm user) {
        return WebResponses.toWeb(getDataTable(biz.unallocatedList(WebBoundaryConvert.INSTANCE.toSysUserBO(user), getPageDomain())));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param userRole userRole 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SysUserRoleForm userRole) {
        return toAjax(biz.cancelAuthUser(WebBoundaryConvert.INSTANCE.toSysUserRoleBO(userRole)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param roleId  roleId 请求参数或领域数据
     * @param userIds userIds 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds) {
        return toAjax(biz.cancelAuthUserAll(roleId, userIds));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param roleId  roleId 请求参数或领域数据
     * @param userIds userIds 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds) {
        return toAjax(biz.selectAuthUserAll(roleId, userIds));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param roleId roleId 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/deptTree/{roleId}")
    public AjaxResult deptTree(@PathVariable("roleId") Long roleId) {
        return WebResponses.withFields(biz.deptTree(roleId));
    }
}
