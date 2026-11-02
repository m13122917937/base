package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysRoleBO;
import com.ruoyi.system.model.bo.SysUserRoleBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.facade.ISysRoleFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysRoleFacade implements ISysRoleFacade {
    private final ISysRoleService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysRoleFacade(ISysRoleService service) {
        this.service = service;
    }

    /**
     * selectRoleList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysRoleBO> queryRoleList(SysRoleBO role) {
        return BoundaryPages.map(service.selectRoleList(BoundaryConvert.INSTANCE.toSysRole(role)), BoundaryConvert.INSTANCE::toSysRoleBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param role 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysRoleBO> queryRoleList(SysRoleBO role, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectRoleList(BoundaryConvert.INSTANCE.toSysRole(role), pageDomain), BoundaryConvert.INSTANCE::toSysRoleBOList);
    }

    /**
     * selectRolesByUserId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysRoleBO> queryRolesByUserId(Long userId) {
        return BoundaryPages.map(service.selectRolesByUserId(userId), BoundaryConvert.INSTANCE::toSysRoleBOList);
    }

    /**
     * selectRoleAll：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysRoleBO> queryRoleAll() {
        return BoundaryPages.map(service.selectRoleAll(), BoundaryConvert.INSTANCE::toSysRoleBOList);
    }

    /**
     * selectRoleById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId roleId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysRoleBO queryRoleById(Long roleId) {
        return BoundaryConvert.INSTANCE.toSysRoleBO(service.selectRoleById(roleId));
    }

    /**
     * checkRoleNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkRoleNameUnique(SysRoleBO role) {
        return service.checkRoleNameUnique(BoundaryConvert.INSTANCE.toSysRole(role));
    }

    /**
     * checkRoleKeyUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkRoleKeyUnique(SysRoleBO role) {
        return service.checkRoleKeyUnique(BoundaryConvert.INSTANCE.toSysRole(role));
    }

    /**
     * checkRoleAllowed：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     */
    @Override
    public void checkRoleAllowed(SysRoleBO role) {
        service.checkRoleAllowed(BoundaryConvert.INSTANCE.toSysRole(role));
    }

    /**
     * checkRoleDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleIds roleIds 领域参数
     */
    @Override
    public void checkRoleDataScope(Long... roleIds) {
        service.checkRoleDataScope(roleIds);
    }

    /**
     * insertRole：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createRole(SysRoleBO role) {
        return service.insertRole(BoundaryConvert.INSTANCE.toSysRole(role));
    }

    /**
     * updateRole：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyRole(SysRoleBO role) {
        return service.updateRole(BoundaryConvert.INSTANCE.toSysRole(role));
    }

    /**
     * updateRoleStatus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyRoleStatus(SysRoleBO role) {
        return service.updateRoleStatus(BoundaryConvert.INSTANCE.toSysRole(role));
    }

    /**
     * authDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int authDataScope(SysRoleBO role) {
        return service.authDataScope(BoundaryConvert.INSTANCE.toSysRole(role));
    }

    /**
     * deleteRoleByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleIds roleIds 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeRoleByIds(Long[] roleIds) {
        return service.deleteRoleByIds(roleIds);
    }

    /**
     * deleteAuthUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userRole userRole 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeAuthUser(SysUserRoleBO userRole) {
        return service.deleteAuthUser(BoundaryConvert.INSTANCE.toSysUserRole(userRole));
    }

    /**
     * deleteAuthUsers：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId  roleId 领域参数
     * @param userIds userIds 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeAuthUsers(Long roleId, Long[] userIds) {
        return service.deleteAuthUsers(roleId, userIds);
    }

    /**
     * insertAuthUsers：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId  roleId 领域参数
     * @param userIds userIds 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createAuthUsers(Long roleId, Long[] userIds) {
        return service.insertAuthUsers(roleId, userIds);
    }
}
