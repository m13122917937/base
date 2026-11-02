package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.mybatis.PersistenceChecks;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.PageDomain;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 用户 业务层处理
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    protected Validator validator;

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    /**
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> userMapper.selectUserList(user));
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> userMapper.selectAllocatedList(user));
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param user 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> userMapper.selectUnallocatedList(user));
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 使用绑定参数查询未删除的同账号用户，排除当前 ID；存在其他记录时返回不唯一，查询异常向上传递。
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user) {
        return !exists(new QueryWrapper<SysUser>().eq("user_name", user.getUserName()).eq("del_flag", "0")
                .ne(user.getUserId() != null, "user_id", user.getUserId()));
    }

    /**
     * 使用绑定参数查询未删除的同手机号用户，排除当前 ID；存在其他记录时返回不唯一，查询异常向上传递。
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkPhoneUnique(SysUser user) {
        return !exists(new QueryWrapper<SysUser>().eq("phonenumber", user.getPhonenumber()).eq("del_flag", "0")
                .ne(user.getUserId() != null, "user_id", user.getUserId()));
    }

    /**
     * 使用绑定参数查询未删除的同邮箱用户，排除当前 ID；存在其他记录时返回不唯一，查询异常向上传递。
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkEmailUnique(SysUser user) {
        return !exists(new QueryWrapper<SysUser>().eq("email", user.getEmail()).eq("del_flag", "0")
                .ne(user.getUserId() != null, "user_id", user.getUserId()));
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (!SecurityUtils.isAdmin()) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtils.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 按原字段策略调用通用 save，回填自增主键；创建/日志时间使用服务端当前时间。
     * 在同一事务内新增用户并保存角色关联；任一步失败整体回滚。
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = insertRecord(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 按原字段策略调用通用 save，回填自增主键；创建/日志时间使用服务端当前时间。
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        return insertRecord(user) > 0;
    }

    /**
     * 按原接口允许的列调用通用 update，保留空值策略及数据库更新时间，主键为空拒绝执行。
     * 在同一事务内重建用户角色关联并更新允许修改的用户字段；主键校验或持久化失败整体回滚。
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("user_id", PersistenceChecks.id(userId)));
        // 新增用户与角色管理
        insertUserRole(user);
        return updateRecord(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("user_id", PersistenceChecks.id(userId)));
        insertUserRole(userId, roleIds);
    }

    /**
     * 使用受控 UpdateWrapper 和通用更新方法，仅修改明确列，并绑定主键或非空主键集合。
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return baseMapper.update(null, new UpdateWrapper<SysUser>().eq("user_id", PersistenceChecks.id(user.getUserId())).set("status", user.getStatus()).setSql("update_time = CURRENT_TIMESTAMP"));
    }

    /**
     * 按原接口允许的列调用通用 update，保留空值策略及数据库更新时间，主键为空拒绝执行。
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return updateRecord(user);
    }

    /**
     * 使用受控 UpdateWrapper 和通用更新方法，仅修改明确列，并绑定主键或非空主键集合。
     * 更新用户登录信息（IP和登录时间）
     *
     * @param userId    用户ID
     * @param loginIp   登录IP地址
     * @param loginDate 登录时间
     * @return 结果
     */
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate) {
        baseMapper.update(null, new UpdateWrapper<SysUser>().eq("user_id", PersistenceChecks.id(userId)).set("login_ip", loginIp).set("login_date", loginDate));
    }

    /**
     * 使用受控 UpdateWrapper 和通用更新方法，仅修改明确列，并绑定主键或非空主键集合。
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return baseMapper.update(null, new UpdateWrapper<SysUser>().eq("user_id", PersistenceChecks.id(user.getUserId())).set("password", user.getPassword()).setSql("pwd_update_date = CURRENT_TIMESTAMP, update_time = CURRENT_TIMESTAMP"));
    }

    /**
     * 使用受控 UpdateWrapper 和通用更新方法，仅修改明确列，并绑定主键或非空主键集合。
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(Long userId, String password) {
        return baseMapper.update(null, new UpdateWrapper<SysUser>().eq("user_id", PersistenceChecks.id(userId)).set("password", password).setSql("pwd_update_date = CURRENT_TIMESTAMP, update_time = CURRENT_TIMESTAMP"));
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 使用受控 UpdateWrapper 和通用更新方法，仅修改明确列，并绑定主键或非空主键集合。
     * 在同一事务内删除角色关联并将用户标记为已删除；主键校验或持久化失败整体回滚。
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("user_id", PersistenceChecks.id(userId)));
        return baseMapper.update(null, new UpdateWrapper<SysUser>().set("del_flag", "2").eq("user_id", PersistenceChecks.id(userId)));
    }

    /**
     * 使用受控 UpdateWrapper 和通用更新方法，仅修改明确列，并绑定主键或非空主键集合。
     * 逐个校验用户操作限制与数据范围，再在同一事务内删除角色关联并逻辑删除用户；任一步失败整体回滚。
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        PersistenceChecks.ids(userIds);
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.delete(new QueryWrapper<SysUserRole>().in("user_id", PersistenceChecks.ids(userIds)));
        return baseMapper.update(null, new UpdateWrapper<SysUser>().set("del_flag", "2").in("user_id", PersistenceChecks.ids(userIds)));
    }

    /**
     * 按原接口允许的列调用通用 update，保留空值策略及数据库更新时间，主键为空拒绝执行。
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, user);
                    deptService.checkDeptDataScope(user.getDeptId());
                    String password = configService.selectConfigByKey("sys.user.initPassword");
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    insertRecord(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, user);
                    checkUserAllowed(u);
                    checkUserDataScope(u.getUserId());
                    deptService.checkDeptDataScope(user.getDeptId());
                    user.setUserId(u.getUserId());
                    user.setDeptId(u.getDeptId());
                    user.setUpdateBy(operName);
                    updateRecord(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /** 校验主键后使用通用 update，仅写原接口允许的列；保留空值/空串策略，返回影响行数。 */
    private int updateRecord(SysUser value) {
        return update(new UpdateWrapper<SysUser>().eq("user_id", PersistenceChecks.id(value.getUserId()))
                .set(value.getDeptId() != null && value.getDeptId() != 0, "dept_id", value.getDeptId())
                .set(value.getNickName() != null && !value.getNickName().isEmpty(), "nick_name", value.getNickName())
                .set(value.getEmail() != null, "email", value.getEmail())
                .set(value.getPhonenumber() != null, "phonenumber", value.getPhonenumber())
                .set(value.getSex() != null && !value.getSex().isEmpty(), "sex", value.getSex())
                .set(value.getAvatar() != null && !value.getAvatar().isEmpty(), "avatar", value.getAvatar())
                .set(value.getPassword() != null && !value.getPassword().isEmpty(), "password", value.getPassword())
                .set(value.getStatus() != null && !value.getStatus().isEmpty(), "status", value.getStatus())
                .set(value.getLoginIp() != null && !value.getLoginIp().isEmpty(), "login_ip", value.getLoginIp())
                .set(value.getLoginDate() != null, "login_date", value.getLoginDate())
                .set(value.getUpdateBy() != null && !value.getUpdateBy().isEmpty(), "update_by", value.getUpdateBy())
                .set(value.getRemark() != null, "remark", value.getRemark())
                .setSql("update_time = CURRENT_TIMESTAMP")) ? 1 : 0;
    }

    /** 显式设置新增时间为服务端当前时间，再按字段插入策略调用通用 save，返回单行写入结果。 */
    private int insertRecord(SysUser value) {
        value.setCreateTime(new Date());
        if (value.getDeptId() != null && value.getDeptId() == 0) {
            value.setDeptId(null);
        }
        if (value.getUserId() != null && value.getUserId() == 0) {
            value.setUserId(null);
        }
        return save(value) ? 1 : 0;
    }
}
