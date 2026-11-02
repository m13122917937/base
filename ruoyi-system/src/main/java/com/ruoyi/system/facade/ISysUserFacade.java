package com.ruoyi.system.facade;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysUserFacade {
    /**
     * selectUserList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysUserBO> queryUserList(SysUserBO user);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysUserBO> queryUserList(SysUserBO user, PageDomain pageDomain);

    /**
     * selectAllocatedList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysUserBO> queryAllocatedList(SysUserBO user);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysUserBO> queryAllocatedList(SysUserBO user, PageDomain pageDomain);

    /**
     * selectUnallocatedList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysUserBO> queryUnallocatedList(SysUserBO user);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysUserBO> queryUnallocatedList(SysUserBO user, PageDomain pageDomain);

    /**
     * selectUserByUserName：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userName userName 领域参数
     * @return 原领域能力的处理结果
     */
    SysUserBO queryUserByUserName(String userName);

    /**
     * selectUserById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    SysUserBO queryUserById(Long userId);

    /**
     * selectUserRoleGroup：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userName userName 领域参数
     * @return 原领域能力的处理结果
     */
    String queryUserRoleGroup(String userName);

    /**
     * checkUserNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkUserNameUnique(SysUserBO user);

    /**
     * checkPhoneUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkPhoneUnique(SysUserBO user);

    /**
     * checkEmailUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkEmailUnique(SysUserBO user);

    /**
     * checkUserAllowed：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     */
    void checkUserAllowed(SysUserBO user);

    /**
     * checkUserDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     */
    void checkUserDataScope(Long userId);

    /**
     * insertUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    int createUser(SysUserBO user);

    /**
     * updateUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyUser(SysUserBO user);

    /**
     * insertUserAuth：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId  userId 领域参数
     * @param roleIds roleIds 领域参数
     */
    void createUserAuth(Long userId, Long[] roleIds);

    /**
     * updateUserStatus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyUserStatus(SysUserBO user);

    /**
     * updateUserProfile：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyUserProfile(SysUserBO user);

    /**
     * resetPwd：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    int resetPwd(SysUserBO user);

    /**
     * resetUserPwd：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId   userId 领域参数
     * @param password password 领域参数
     * @return 原领域能力的处理结果
     */
    int resetUserPwd(Long userId, String password);

    /**
     * deleteUserByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userIds userIds 领域参数
     * @return 原领域能力的处理结果
     */
    int removeUserByIds(Long[] userIds);

    /**
     * importUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userList        userList 领域参数
     * @param isUpdateSupport isUpdateSupport 领域参数
     * @param operName        operName 领域参数
     * @return 原领域能力的处理结果
     */
    String importUser(List<SysUserBO> userList, Boolean isUpdateSupport, String operName);
}
