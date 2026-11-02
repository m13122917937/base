package com.ruoyi.system.facade;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysRoleBO;
import com.ruoyi.system.model.bo.SysUserRoleBO;
import com.ruoyi.system.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysRoleFacade {
    /**
     * selectRoleList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysRoleBO> queryRoleList(SysRoleBO role);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param role 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysRoleBO> queryRoleList(SysRoleBO role, PageDomain pageDomain);

    /**
     * selectRolesByUserId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysRoleBO> queryRolesByUserId(Long userId);

    /**
     * selectRoleAll：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @return 原领域能力的处理结果
     */
    List<SysRoleBO> queryRoleAll();

    /**
     * selectRoleById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId roleId 领域参数
     * @return 原领域能力的处理结果
     */
    SysRoleBO queryRoleById(Long roleId);

    /**
     * checkRoleNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkRoleNameUnique(SysRoleBO role);

    /**
     * checkRoleKeyUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkRoleKeyUnique(SysRoleBO role);

    /**
     * checkRoleAllowed：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     */
    void checkRoleAllowed(SysRoleBO role);

    /**
     * checkRoleDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleIds roleIds 领域参数
     */
    void checkRoleDataScope(Long... roleIds);

    /**
     * insertRole：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    int createRole(SysRoleBO role);

    /**
     * updateRole：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyRole(SysRoleBO role);

    /**
     * updateRoleStatus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyRoleStatus(SysRoleBO role);

    /**
     * authDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param role role 领域参数
     * @return 原领域能力的处理结果
     */
    int authDataScope(SysRoleBO role);

    /**
     * deleteRoleByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleIds roleIds 领域参数
     * @return 原领域能力的处理结果
     */
    int removeRoleByIds(Long[] roleIds);

    /**
     * deleteAuthUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userRole userRole 领域参数
     * @return 原领域能力的处理结果
     */
    int removeAuthUser(SysUserRoleBO userRole);

    /**
     * deleteAuthUsers：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId  roleId 领域参数
     * @param userIds userIds 领域参数
     * @return 原领域能力的处理结果
     */
    int removeAuthUsers(Long roleId, Long[] userIds);

    /**
     * insertAuthUsers：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId  roleId 领域参数
     * @param userIds userIds 领域参数
     * @return 原领域能力的处理结果
     */
    int createAuthUsers(Long roleId, Long[] userIds);
}
