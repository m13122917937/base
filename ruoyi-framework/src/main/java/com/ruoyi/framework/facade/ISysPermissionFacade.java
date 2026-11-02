package com.ruoyi.framework.facade;

import java.util.Set;

import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysPermissionFacade {
    /**
     * getRolePermission：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    Set<String> getRolePermission(SysUserBO user);

    /**
     * 计算用户菜单权限并刷新入参 BO 的逐角色 permissions，供后续 DataScope 匹配及会话缓存使用。
     * 不修改原认证缓存主体；调用方按原令牌刷新流程持久化会话。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    Set<String> getMenuPermission(SysUserBO user);
}
