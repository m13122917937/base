package com.ruoyi.biz.system;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.domain.TreeSelect;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ruoyi.common.exception.ServiceException;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import com.ruoyi.system.model.bo.SysDeptBO;
import com.ruoyi.system.model.bo.SysRoleBO;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.system.facade.ISysRoleFacade;
import com.ruoyi.system.facade.ISysDeptFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysUserController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysUserBiz {

    private final ISysUserFacade userFacade;

    private final ISysRoleFacade roleFacade;

    private final ISysDeptFacade deptFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysUserBiz(ISysUserFacade userFacade, ISysRoleFacade roleFacade, ISysDeptFacade deptFacade) {
        this.userFacade = userFacade;
        this.roleFacade = roleFacade;
        this.deptFacade = deptFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 获取用户列表：按原顺序执行 selectUserList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysUserBO> list(SysUserBO user, PageDomain pageDomain) {
        List<SysUserBO> list = userFacade.queryUserList(user, pageDomain);
        return list;
    }

    /**
     * 按查询条件与数据权限读取用户导出数据，返回业务列表，由 Web 层编码并输出 Excel。
     *
     * @param user user 请求参数或领域数据
     * @return 当前过滤条件和数据权限范围内的导出数据，由 Web 层编码为 Excel
     */
    public List<SysUserBO> export(SysUserBO user) {
        return userFacade.queryUserList(user);
    }

    /**
     * importData：按原顺序执行 getUsername、importUser、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param userList      Web 层解析并转换后的用户导入数据
     * @param updateSupport updateSupport 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     * @throws Exception 原处理过程声明的失败
     */
    public String importData(List<SysUserBO> userList, boolean updateSupport) throws Exception {
        String operName = SecurityUtils.getUsername();
        String message = userFacade.importUser(userList, updateSupport, operName);
        return message;
    }

    /**
     * 校验用户数据范围后读取用户详情、已分配角色及可选角色；查询失败沿用原异常语义。
     *
     * @param userId userId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> getInfo(Long userId) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (StringUtils.isNotNull(userId)) {
            userFacade.checkUserDataScope(userId);
            SysUserBO sysUser = userFacade.queryUserById(userId);
            result.put("data", sysUser);
            result.put("roleIds", sysUser.getRoles().stream().map(SysRoleBO::getRoleId).collect(Collectors.toList()));
        }
        List<SysRoleBO> roles = roleFacade.queryRoleAll();
        result.put("roles", SecurityUtils.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return result;
    }

    /**
     * 新增用户：按原顺序执行 checkDeptDataScope、getDeptId、checkRoleDataScope、getRoleIds、checkUserNameUnique、getUserName、isNotEmpty 等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysUserBO user) {
        deptFacade.checkDeptDataScope(user.getDeptId());
        roleFacade.checkRoleDataScope(user.getRoleIds());
        if (!userFacade.checkUserNameUnique(user)) {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userFacade.checkPhoneUnique(user)) {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userFacade.checkEmailUnique(user)) {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return userFacade.createUser(user);
    }

    /**
     * 修改用户：按原顺序执行 checkUserAllowed、checkUserDataScope、getUserId、checkDeptDataScope、getDeptId、checkRoleDataScope、getRoleIds、checkUserNameUnique 等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int edit(SysUserBO user) {
        userFacade.checkUserAllowed(user);
        userFacade.checkUserDataScope(user.getUserId());
        deptFacade.checkDeptDataScope(user.getDeptId());
        roleFacade.checkRoleDataScope(user.getRoleIds());
        if (!userFacade.checkUserNameUnique(user)) {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userFacade.checkPhoneUnique(user)) {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userFacade.checkEmailUnique(user)) {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return userFacade.modifyUser(user);
    }

    /**
     * 删除用户：按原顺序执行 contains、getUserId、deleteUserByIds 等步骤，并保留原判断、返回及异常语义。
     *
     * @param userIds userIds 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int remove(Long[] userIds) {
        if (ArrayUtils.contains(userIds, SecurityUtils.getUserId())) {
            throw new ServiceException("当前用户不能删除");
        }
        return userFacade.removeUserByIds(userIds);
    }

    /**
     * 重置密码：按原顺序执行 checkUserAllowed、checkUserDataScope、getUserId、setPassword、encryptPassword、getPassword、setUpdateBy、getUsername 等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int resetPwd(SysUserBO user) {
        userFacade.checkUserAllowed(user);
        userFacade.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return userFacade.resetPwd(user);
    }

    /**
     * 状态修改：按原顺序执行 checkUserAllowed、checkUserDataScope、getUserId、setUpdateBy、getUsername、updateUserStatus 等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int changeStatus(SysUserBO user) {
        userFacade.checkUserAllowed(user);
        userFacade.checkUserDataScope(user.getUserId());
        user.setUpdateBy(SecurityUtils.getUsername());
        return userFacade.modifyUserStatus(user);
    }

    /**
     * 根据用户编号获取授权角色：按原顺序执行 selectUserById、selectRolesByUserId、put、isAdmin、stream、filter、collect 等步骤，并保留原判断、返回及异常语义。
     *
     * @param userId userId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> authRole(Long userId) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        SysUserBO user = userFacade.queryUserById(userId);
        List<SysRoleBO> roles = roleFacade.queryRolesByUserId(userId);
        result.put("user", user);
        result.put("roles", SecurityUtils.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return result;
    }

    /**
     * 用户授权角色：按原顺序执行 checkUserDataScope、checkRoleDataScope、insertUserAuth、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param userId  userId 请求参数或领域数据
     * @param roleIds roleIds 请求参数或领域数据
     */
    public void insertAuthRole(Long userId, Long[] roleIds) {
        userFacade.checkUserDataScope(userId);
        roleFacade.checkRoleDataScope(roleIds);
        userFacade.createUserAuth(userId, roleIds);
    }

    /**
     * 获取部门树列表：按原顺序执行 selectDeptTreeList 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dept dept 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<TreeSelect> deptTree(SysDeptBO dept) {
        return deptFacade.queryDeptTreeList(dept);
    }
}
