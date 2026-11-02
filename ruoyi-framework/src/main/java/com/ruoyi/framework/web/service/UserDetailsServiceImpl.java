package com.ruoyi.framework.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ISysUserService userService;
    private final SysPermissionService permissionService;

    /**
     * 注入用户查询和权限服务；密码比较由 AuthenticationManager 的认证提供器执行。
     */
    public UserDetailsServiceImpl(ISysUserService userService, SysPermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    /**
     * 查询用户并拒绝不存在、已删除或停用的账号，再构造带权限的认证主体。
     * 本方法不统计错误次数；返回后由 Spring Security 使用 BCrypt 校验输入密码。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("用户不存在/密码错误");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException("对不起，您的账号已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("用户已封禁，请联系管理员");
        }

        return createLoginUser(user);
    }

    /**
     * 将已通过账号状态检查的用户和菜单权限组成认证主体，不签发令牌或修改缓存。
     */
    public UserDetails createLoginUser(SysUser user) {
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
