package com.ruoyi.framework.facade.impl;

import java.util.Set;

import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.framework.facade.ISysPermissionFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysPermissionFacade implements ISysPermissionFacade {
    private final SysPermissionService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysPermissionFacade(SysPermissionService service) {
        this.service = service;
    }

    /**
     * getRolePermission：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public Set<String> getRolePermission(SysUserBO user) {
        return service.getRolePermission(BoundaryConvert.INSTANCE.toSysUser(user));
    }

    /**
     * 先将用户 BO 转为认证内部对象，再按角色计算菜单权限，并把 Service 填充的逐角色权限写回 BO。
     * 该回写供后续会话缓存和 DataScope 匹配使用，不直接修改当前缓存主体或写数据库。
     *
     * @param user user 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public Set<String> getMenuPermission(SysUserBO user) {
        SysUser principal = BoundaryConvert.INSTANCE.toSysUser(user);
        Set<String> permissions = service.getMenuPermission(principal);
        user.setRoles(BoundaryConvert.INSTANCE.toSysUserBO(principal).getRoles());
        return permissions;
    }
}
