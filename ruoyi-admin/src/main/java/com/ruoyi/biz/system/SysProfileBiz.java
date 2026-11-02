package com.ruoyi.biz.system;

import java.util.LinkedHashMap;

import com.ruoyi.common.exception.ServiceException;

import java.util.Map;

import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.model.bo.LoginSessionBO;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.framework.facade.ITokenFacade;
import com.ruoyi.framework.facade.ILoginSessionFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysProfileController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysProfileBiz {

    private final ISysUserFacade userFacade;

    private final ITokenFacade tokenFacade;

    private final ILoginSessionFacade sessionFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysProfileBiz(ISysUserFacade userFacade, ITokenFacade tokenFacade, ILoginSessionFacade sessionFacade) {
        this.userFacade = userFacade;
        this.tokenFacade = tokenFacade;
        this.sessionFacade = sessionFacade;
    }

    /**
     * 读取当前登录用户及角色名称，返回个人信息；登录失效或查询失败沿用原异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> profile() {
        LoginSessionBO loginUser = sessionFacade.currentSession();
        SysUserBO user = loginUser.getUser();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("data", user);
        result.put("roleGroup", userFacade.queryUserRoleGroup(loginUser.getUsername()));
        return result;
    }

    /**
     * 修改用户：按原顺序执行 getLoginUser、getUser、setNickName、getNickName、setEmail、getEmail、setPhonenumber、getPhonenumber 等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     */
    public void updateProfile(SysUserBO user) {
        LoginSessionBO loginUser = sessionFacade.currentSession();
        SysUserBO currentUser = loginUser.getUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userFacade.checkPhoneUnique(currentUser)) {
            throw new ServiceException("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userFacade.checkEmailUnique(currentUser)) {
            throw new ServiceException("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userFacade.modifyUserProfile(currentUser) > 0) {
            // 更新缓存用户信息
            tokenFacade.setLoginUser(loginUser);
            return;
        }
        throw new ServiceException("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码：按原顺序执行 get、getLoginUser、getUserId、selectUserById、getPassword、matchesPassword、encryptPassword 等步骤，并保留原判断、返回及异常语义。
     *
     * @param params params 请求参数或领域数据
     */
    public void updatePwd(Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        LoginSessionBO loginUser = sessionFacade.currentSession();
        Long userId = loginUser.getUserId();
        SysUserBO user = userFacade.queryUserById(userId);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            throw new ServiceException("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            throw new ServiceException("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userFacade.resetUserPwd(userId, newPassword) > 0) {
            // 更新缓存用户密码&密码最后更新时间
            loginUser.getUser().setPwdUpdateDate(DateUtils.getNowDate());
            loginUser.getUser().setPassword(newPassword);
            tokenFacade.setLoginUser(loginUser);
            return;
        }
        throw new ServiceException("修改密码异常，请联系管理员");
    }

}
