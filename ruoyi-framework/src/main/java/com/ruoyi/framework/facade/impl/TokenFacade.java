package com.ruoyi.framework.facade.impl;

import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.LoginSessionBO;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.facade.ITokenFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class TokenFacade implements ITokenFacade {
    private final TokenService service;
    private final SysPermissionService permissionService;

    /**
     * 接收现有令牌及权限服务，建立身份基础设施边界，不执行查询或写入。
     *
     * @param service           本领域业务服务
     * @param permissionService 原逐角色权限计算能力
     */
    public TokenFacade(TokenService service, SysPermissionService permissionService) {
        this.service = service;
        this.permissionService = permissionService;
    }

    /**
     * 将会话 BO 转回原 LoginUser 并交给令牌服务写入现有缓存，再同步刷新后的登录时间和到期时间。
     * 保留原缓存类型、令牌键和 TTL 行为，不修改 Redis 序列化格式；无令牌时沿用原空操作语义。
     *
     * @param loginUser loginUser 领域参数
     */
    @Override
    public void setLoginUser(LoginSessionBO loginUser) {
        LoginUser session = BoundaryConvert.INSTANCE.toLoginUser(loginUser);
        service.setLoginUser(session);
        if (session != null) {
            loginUser.setLoginTime(session.getLoginTime());
            loginUser.setExpireTime(session.getExpireTime());
        }
    }

    /**
     * 将 BO 转回原认证对象，调用令牌服务更新缓存 TTL，再将新的登录与到期时间同步到调用方会话 BO。
     * 不发起数据库事务，缓存写入失败沿用原异常且不提前回写 BO 时间。
     *
     * @param loginUser loginUser 领域参数
     */
    @Override
    public void refreshToken(LoginSessionBO loginUser) {
        LoginUser session = BoundaryConvert.INSTANCE.toLoginUser(loginUser);
        service.refreshToken(session);
        loginUser.setLoginTime(session.getLoginTime());
        loginUser.setExpireTime(session.getExpireTime());
    }

    /**
     * 调用原令牌服务按角色筛选在线会话、重新计算权限并刷新缓存，不把权限 Service 暴露给 Biz。
     *
     * @param roleId roleId 领域参数
     */
    @Override
    public void refreshPermissionByRoleId(Long roleId) {
        service.refreshPermissionByRoleId(roleId, permissionService);
    }
}
