package com.ruoyi.biz.system;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.system.model.bo.SysMenuBO;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.framework.model.bo.LoginParam;
import com.ruoyi.system.model.bo.LoginSessionBO;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.facade.ISysLoginFacade;
import com.ruoyi.system.facade.ISysMenuFacade;
import com.ruoyi.framework.facade.ISysPermissionFacade;
import com.ruoyi.framework.facade.ITokenFacade;
import com.ruoyi.system.facade.ISysConfigFacade;
import com.ruoyi.framework.facade.ILoginSessionFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysLoginController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysLoginBiz {

    private final ISysLoginFacade loginFacade;

    private final ISysMenuFacade menuFacade;

    private final ISysPermissionFacade permissionFacade;

    private final ITokenFacade tokenFacade;

    private final ISysConfigFacade configFacade;

    private final ILoginSessionFacade sessionFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysLoginBiz(ISysLoginFacade loginFacade, ISysMenuFacade menuFacade, ISysPermissionFacade permissionFacade, ITokenFacade tokenFacade, ISysConfigFacade configFacade, ILoginSessionFacade sessionFacade) {
        this.loginFacade = loginFacade;
        this.menuFacade = menuFacade;
        this.permissionFacade = permissionFacade;
        this.tokenFacade = tokenFacade;
        this.configFacade = configFacade;
        this.sessionFacade = sessionFacade;
    }

    /**
     * 登录方法：按原顺序执行 login、getUsername、getPassword、put 等步骤，并保留原判断、返回及异常语义。
     *
     * @param loginBody loginBody 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> login(LoginParam loginBody) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        // 生成令牌
        String token = loginFacade.login(loginBody.getUsername(), loginBody.getPassword());
        result.put(Constants.TOKEN, token);
        return result;
    }

    /**
     * 获取用户信息：按原顺序执行 getLoginUser、getUser、getRolePermission、getMenuPermission、getPermissions、equals、setPermissions、refreshToken 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> getInfo() {
        LoginSessionBO loginUser = sessionFacade.currentSession();
        SysUserBO user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionFacade.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionFacade.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions)) {
            loginUser.setPermissions(permissions);
            tokenFacade.refreshToken(loginUser);
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("user", user);
        result.put("roles", roles);
        result.put("permissions", permissions);
        result.put("pwdChrtype", getSysAccountChrtype());
        result.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        result.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return result;
    }

    /**
     * 获取路由信息：按原顺序执行 getUserId、selectMenuTreeByUserId、buildMenus 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<RouterBO> getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenuBO> menus = menuFacade.queryMenuTreeByUserId(userId);
        return menuFacade.buildMenus(menus);
    }

    /**
     * getSysAccountChrtype：按原顺序执行 toStr、selectConfigByKey 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public String getSysAccountChrtype() {
        return Convert.toStr(configFacade.queryConfigByKey("sys.account.chrtype"), "0");
    }

    /**
     * initPasswordIsModify：按原顺序执行 toInt、selectConfigByKey 等步骤，并保留原判断、返回及异常语义。
     *
     * @param pwdUpdateDate pwdUpdateDate 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public boolean initPasswordIsModify(Date pwdUpdateDate) {
        Integer initPasswordModify = Convert.toInt(configFacade.queryConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    /**
     * passwordIsExpiration：按原顺序执行 toInt、selectConfigByKey、isNull、getNowDate、differentDaysByMillisecond 等步骤，并保留原判断、返回及异常语义。
     *
     * @param pwdUpdateDate pwdUpdateDate 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public boolean passwordIsExpiration(Date pwdUpdateDate) {
        Integer passwordValidateDays = Convert.toInt(configFacade.queryConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (StringUtils.isNull(pwdUpdateDate)) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
