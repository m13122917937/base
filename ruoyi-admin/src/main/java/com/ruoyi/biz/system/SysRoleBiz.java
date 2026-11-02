package com.ruoyi.biz.system;

import com.ruoyi.common.core.page.PageDomain;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.List;

import com.ruoyi.system.model.bo.SysDeptBO;
import com.ruoyi.system.model.bo.SysRoleBO;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.model.bo.SysUserRoleBO;
import com.ruoyi.system.facade.ISysRoleFacade;
import com.ruoyi.framework.facade.ITokenFacade;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.system.facade.ISysDeptFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysRoleController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysRoleBiz {

    private final ISysRoleFacade roleFacade;

    private final ITokenFacade tokenFacade;

    private final ISysUserFacade userFacade;

    private final ISysDeptFacade deptFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysRoleBiz(ISysRoleFacade roleFacade, ITokenFacade tokenFacade, ISysUserFacade userFacade, ISysDeptFacade deptFacade) {
        this.roleFacade = roleFacade;
        this.tokenFacade = tokenFacade;
        this.userFacade = userFacade;
        this.deptFacade = deptFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * list：按原顺序执行 selectRoleList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param role role 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysRoleBO> list(SysRoleBO role, PageDomain pageDomain) {
        List<SysRoleBO> list = roleFacade.queryRoleList(role, pageDomain);
        return list;
    }

    /**
     * export：按原顺序执行 selectRoleList、返回导出数据 等步骤，并保留原判断、返回及异常语义。
     *
     * @param role role 请求参数或领域数据
     * @return 当前过滤条件和数据权限范围内的导出数据，由 Web 层编码为 Excel
     */
    public List<SysRoleBO> export(SysRoleBO role) {
        return roleFacade.queryRoleList(role);
    }

    /**
     * 根据角色编号获取详细信息：按原顺序执行 checkRoleDataScope、selectRoleById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param roleId roleId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysRoleBO getInfo(Long roleId) {
        roleFacade.checkRoleDataScope(roleId);
        return roleFacade.queryRoleById(roleId);
    }

    /**
     * 新增角色：按原顺序执行 checkRoleNameUnique、getRoleName、checkRoleKeyUnique、setCreateBy、getUsername、insertRole 等步骤，并保留原判断、返回及异常语义。
     *
     * @param role role 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysRoleBO role) {
        if (!roleFacade.checkRoleNameUnique(role)) {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleFacade.checkRoleKeyUnique(role)) {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(SecurityUtils.getUsername());
        return roleFacade.createRole(role);

    }

    /**
     * 修改保存角色：按原顺序执行 checkRoleAllowed、checkRoleDataScope、getRoleId、checkRoleNameUnique、getRoleName、checkRoleKeyUnique、setUpdateBy 等步骤，并保留原判断、返回及异常语义。
     *
     * @param role role 请求参数或领域数据
     */
    public void edit(SysRoleBO role) {
        roleFacade.checkRoleAllowed(role);
        roleFacade.checkRoleDataScope(role.getRoleId());
        if (!roleFacade.checkRoleNameUnique(role)) {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleFacade.checkRoleKeyUnique(role)) {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(SecurityUtils.getUsername());

        if (roleFacade.modifyRole(role) > 0) {
            // 刷新所有持有该角色的在线用户权限
            tokenFacade.refreshPermissionByRoleId(role.getRoleId());
            return;
        }
        throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限：按原顺序执行 checkRoleAllowed、checkRoleDataScope、getRoleId、authDataScope 等步骤，并保留原判断、返回及异常语义。
     *
     * @param role role 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int dataScope(SysRoleBO role) {
        roleFacade.checkRoleAllowed(role);
        roleFacade.checkRoleDataScope(role.getRoleId());
        return roleFacade.authDataScope(role);
    }

    /**
     * 状态修改：按原顺序执行 checkRoleAllowed、checkRoleDataScope、getRoleId、setUpdateBy、getUsername、updateRoleStatus 等步骤，并保留原判断、返回及异常语义。
     *
     * @param role role 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int changeStatus(SysRoleBO role) {
        roleFacade.checkRoleAllowed(role);
        roleFacade.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(SecurityUtils.getUsername());
        return roleFacade.modifyRoleStatus(role);
    }

    /**
     * 删除角色：按原顺序执行 deleteRoleByIds 等步骤，并保留原判断、返回及异常语义。
     *
     * @param roleIds roleIds 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int remove(Long[] roleIds) {
        return roleFacade.removeRoleByIds(roleIds);
    }

    /**
     * 获取角色选择框列表：按原顺序执行 selectRoleAll 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysRoleBO> optionselect() {
        return roleFacade.queryRoleAll();
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 查询已分配用户角色列表：按原顺序执行 selectAllocatedList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysUserBO> allocatedList(SysUserBO user, PageDomain pageDomain) {
        List<SysUserBO> list = userFacade.queryAllocatedList(user, pageDomain);
        return list;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 查询未分配用户角色列表：按原顺序执行 selectUnallocatedList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysUserBO> unallocatedList(SysUserBO user, PageDomain pageDomain) {
        List<SysUserBO> list = userFacade.queryUnallocatedList(user, pageDomain);
        return list;
    }

    /**
     * 取消授权用户：按原顺序执行 deleteAuthUser 等步骤，并保留原判断、返回及异常语义。
     *
     * @param userRole userRole 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int cancelAuthUser(SysUserRoleBO userRole) {
        return roleFacade.removeAuthUser(userRole);
    }

    /**
     * 批量取消授权用户：按原顺序执行 deleteAuthUsers 等步骤，并保留原判断、返回及异常语义。
     *
     * @param roleId  roleId 请求参数或领域数据
     * @param userIds userIds 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int cancelAuthUserAll(Long roleId, Long[] userIds) {
        return roleFacade.removeAuthUsers(roleId, userIds);
    }

    /**
     * 批量选择用户授权：按原顺序执行 checkRoleDataScope、insertAuthUsers 等步骤，并保留原判断、返回及异常语义。
     *
     * @param roleId  roleId 请求参数或领域数据
     * @param userIds userIds 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int selectAuthUserAll(Long roleId, Long[] userIds) {
        roleFacade.checkRoleDataScope(roleId);
        return roleFacade.createAuthUsers(roleId, userIds);
    }

    /**
     * 获取对应角色部门树列表：按原顺序执行 put、selectDeptListByRoleId、selectDeptTreeList 等步骤，并保留原判断、返回及异常语义。
     *
     * @param roleId roleId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> deptTree(Long roleId) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("checkedKeys", deptFacade.queryDeptListByRoleId(roleId));
        result.put("depts", deptFacade.queryDeptTreeList(new SysDeptBO()));
        return result;
    }
}
