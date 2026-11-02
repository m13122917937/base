package com.ruoyi.web.form.system;

/**
 * 用户和角色关联 sys_user_role
 *
 * @author ruoyi
 */
public class SysUserRoleForm {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 读取并返回当前对象的 UserId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 将参数 userId 写入当前对象的 UserId 属性，不执行持久化。
     *
     * @param userId userId 请求参数或领域数据
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 读取并返回当前对象的 RoleId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * 将参数 roleId 写入当前对象的 RoleId 属性，不执行持久化。
     *
     * @param roleId roleId 请求参数或领域数据
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}
