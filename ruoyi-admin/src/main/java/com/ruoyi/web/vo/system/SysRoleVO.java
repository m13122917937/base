package com.ruoyi.web.vo.system;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 角色表 sys_role
 *
 * @author ruoyi
 */
public class SysRoleVO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Excel(name = "角色序号", cellType = ColumnType.NUMERIC)
    private Long roleId;

    /**
     * 角色名称
     */
    @Excel(name = "角色名称")
    private String roleName;

    /**
     * 角色权限
     */
    @Excel(name = "角色权限")
    private String roleKey;

    /**
     * 角色排序
     */
    @Excel(name = "角色排序")
    private Integer roleSort;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）
     */
    @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限")
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    private boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）
     */
    private boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @Excel(name = "角色状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    private boolean flag = false;

    /**
     * 菜单组
     */
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    private Long[] deptIds;

    /**
     * 角色菜单权限
     */
    private Set<String> permissions;

    /**
     * 接收 默认初始化参数，初始化当前对象的依赖或属性；不执行持久化。
     */
    public SysRoleVO() {

    }

    /**
     * 接收 roleId，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param roleId roleId 请求参数或领域数据
     */
    public SysRoleVO(Long roleId) {
        this.roleId = roleId;
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

    /**
     * isAdmin：按原顺序执行 isAdmin 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    /**
     * isAdmin：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @param roleId roleId 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

    /**
     * 读取并返回当前对象的 RoleName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
    public String getRoleName() {
        return roleName;
    }

    /**
     * 将参数 roleName 写入当前对象的 RoleName 属性，不执行持久化。
     *
     * @param roleName roleName 请求参数或领域数据
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 读取并返回当前对象的 RoleKey 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "权限字符不能为空")
    @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符")
    public String getRoleKey() {
        return roleKey;
    }

    /**
     * 将参数 roleKey 写入当前对象的 RoleKey 属性，不执行持久化。
     *
     * @param roleKey roleKey 请求参数或领域数据
     */
    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    /**
     * 读取并返回当前对象的 RoleSort 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotNull(message = "显示顺序不能为空")
    public Integer getRoleSort() {
        return roleSort;
    }

    /**
     * 将参数 roleSort 写入当前对象的 RoleSort 属性，不执行持久化。
     *
     * @param roleSort roleSort 请求参数或领域数据
     */
    public void setRoleSort(Integer roleSort) {
        this.roleSort = roleSort;
    }

    /**
     * 读取并返回当前对象的 DataScope 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getDataScope() {
        return dataScope;
    }

    /**
     * 将参数 dataScope 写入当前对象的 DataScope 属性，不执行持久化。
     *
     * @param dataScope dataScope 请求参数或领域数据
     */
    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    /**
     * isMenuCheckStrictly：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean isMenuCheckStrictly() {
        return menuCheckStrictly;
    }

    /**
     * 将参数 menuCheckStrictly 写入当前对象的 MenuCheckStrictly 属性，不执行持久化。
     *
     * @param menuCheckStrictly menuCheckStrictly 请求参数或领域数据
     */
    public void setMenuCheckStrictly(boolean menuCheckStrictly) {
        this.menuCheckStrictly = menuCheckStrictly;
    }

    /**
     * isDeptCheckStrictly：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean isDeptCheckStrictly() {
        return deptCheckStrictly;
    }

    /**
     * 将参数 deptCheckStrictly 写入当前对象的 DeptCheckStrictly 属性，不执行持久化。
     *
     * @param deptCheckStrictly deptCheckStrictly 请求参数或领域数据
     */
    public void setDeptCheckStrictly(boolean deptCheckStrictly) {
        this.deptCheckStrictly = deptCheckStrictly;
    }

    /**
     * 读取并返回当前对象的 Status 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getStatus() {
        return status;
    }

    /**
     * 将参数 status 写入当前对象的 Status 属性，不执行持久化。
     *
     * @param status status 请求参数或领域数据
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 读取并返回当前对象的 DelFlag 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 将参数 delFlag 写入当前对象的 DelFlag 属性，不执行持久化。
     *
     * @param delFlag delFlag 请求参数或领域数据
     */
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * isFlag：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * 将参数 flag 写入当前对象的 Flag 属性，不执行持久化。
     *
     * @param flag flag 请求参数或领域数据
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * 读取并返回当前对象的 MenuIds 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long[] getMenuIds() {
        return menuIds;
    }

    /**
     * 将参数 menuIds 写入当前对象的 MenuIds 属性，不执行持久化。
     *
     * @param menuIds menuIds 请求参数或领域数据
     */
    public void setMenuIds(Long[] menuIds) {
        this.menuIds = menuIds;
    }

    /**
     * 读取并返回当前对象的 DeptIds 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long[] getDeptIds() {
        return deptIds;
    }

    /**
     * 将参数 deptIds 写入当前对象的 DeptIds 属性，不执行持久化。
     *
     * @param deptIds deptIds 请求参数或领域数据
     */
    public void setDeptIds(Long[] deptIds) {
        this.deptIds = deptIds;
    }

    /**
     * 读取并返回当前对象的 Permissions 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * 将参数 permissions 写入当前对象的 Permissions 属性，不执行持久化。
     *
     * @param permissions permissions 请求参数或领域数据
     */
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

}
