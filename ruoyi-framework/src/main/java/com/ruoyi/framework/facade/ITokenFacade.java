package com.ruoyi.framework.facade;

import com.ruoyi.system.model.bo.LoginSessionBO;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ITokenFacade {
    /**
     * 以原 LoginUser 类型更新令牌缓存，并将刷新后的时间同步回会话 BO；原 TTL 与空操作语义不变。
     *
     * @param loginUser loginUser 领域参数
     */
    void setLoginUser(LoginSessionBO loginUser);

    /**
     * 刷新现有令牌缓存 TTL 并同步会话 BO 的时间；失败沿用原异常，不提前修改 BO。
     *
     * @param loginUser loginUser 领域参数
     */
    void refreshToken(LoginSessionBO loginUser);

    /**
     * 重新计算指定角色关联的在线会话权限并刷新原缓存，不向调用方暴露内部 Service。
     *
     * @param roleId roleId 领域参数
     */
    void refreshPermissionByRoleId(Long roleId);
}
