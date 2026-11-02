package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.facade.ISysUserFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysUserFacade implements ISysUserFacade {
    private final ISysUserService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysUserFacade(ISysUserService service) {
        this.service = service;
    }

    /**
     * selectUserList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysUserBO> queryUserList(SysUserBO user) {
        return BoundaryPages.map(service.selectUserList(BoundaryConvert.INSTANCE.toSysUser(user)), BoundaryConvert.INSTANCE::toSysUserBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysUserBO> queryUserList(SysUserBO user, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectUserList(BoundaryConvert.INSTANCE.toSysUser(user), pageDomain), BoundaryConvert.INSTANCE::toSysUserBOList);
    }

    /**
     * selectAllocatedList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysUserBO> queryAllocatedList(SysUserBO user) {
        return BoundaryPages.map(service.selectAllocatedList(BoundaryConvert.INSTANCE.toSysUser(user)), BoundaryConvert.INSTANCE::toSysUserBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysUserBO> queryAllocatedList(SysUserBO user, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectAllocatedList(BoundaryConvert.INSTANCE.toSysUser(user), pageDomain), BoundaryConvert.INSTANCE::toSysUserBOList);
    }

    /**
     * selectUnallocatedList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysUserBO> queryUnallocatedList(SysUserBO user) {
        return BoundaryPages.map(service.selectUnallocatedList(BoundaryConvert.INSTANCE.toSysUser(user)), BoundaryConvert.INSTANCE::toSysUserBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysUserBO> queryUnallocatedList(SysUserBO user, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectUnallocatedList(BoundaryConvert.INSTANCE.toSysUser(user), pageDomain), BoundaryConvert.INSTANCE::toSysUserBOList);
    }

    /**
     * selectUserByUserName：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userName userName 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysUserBO queryUserByUserName(String userName) {
        return BoundaryConvert.INSTANCE.toSysUserBO(service.selectUserByUserName(userName));
    }

    /**
     * selectUserById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysUserBO queryUserById(Long userId) {
        return BoundaryConvert.INSTANCE.toSysUserBO(service.selectUserById(userId));
    }

    /**
     * selectUserRoleGroup：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userName userName 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public String queryUserRoleGroup(String userName) {
        return service.selectUserRoleGroup(userName);
    }

    /**
     * checkUserNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkUserNameUnique(SysUserBO user) {
        return service.checkUserNameUnique(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * checkPhoneUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkPhoneUnique(SysUserBO user) {
        return service.checkPhoneUnique(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * checkEmailUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkEmailUnique(SysUserBO user) {
        return service.checkEmailUnique(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * checkUserAllowed：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     */
    @Override
    public void checkUserAllowed(SysUserBO user) {
        service.checkUserAllowed(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * checkUserDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     */
    @Override
    public void checkUserDataScope(Long userId) {
        service.checkUserDataScope(userId);
    }

    /**
     * insertUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createUser(SysUserBO user) {
        return service.insertUser(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * updateUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyUser(SysUserBO user) {
        return service.updateUser(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * insertUserAuth：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId  userId 领域参数
     * @param roleIds roleIds 领域参数
     */
    @Override
    public void createUserAuth(Long userId, Long[] roleIds) {
        service.insertUserAuth(userId, roleIds);
    }

    /**
     * updateUserStatus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyUserStatus(SysUserBO user) {
        return service.updateUserStatus(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * updateUserProfile：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyUserProfile(SysUserBO user) {
        return service.updateUserProfile(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * resetPwd：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int resetPwd(SysUserBO user) {
        return service.resetPwd(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * resetUserPwd：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId   userId 领域参数
     * @param password password 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int resetUserPwd(Long userId, String password) {
        return service.resetUserPwd(userId, password);
    }

    /**
     * deleteUserByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userIds userIds 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeUserByIds(Long[] userIds) {
        return service.deleteUserByIds(userIds);
    }

    /**
     * importUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userList        userList 领域参数
     * @param isUpdateSupport isUpdateSupport 领域参数
     * @param operName        operName 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public String importUser(List<SysUserBO> userList, Boolean isUpdateSupport, String operName) {
        return service.importUser(BoundaryConvert.INSTANCE.toSysUserList(userList), isUpdateSupport, operName);
    }
}
