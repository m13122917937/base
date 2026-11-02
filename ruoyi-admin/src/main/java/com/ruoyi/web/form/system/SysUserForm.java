package com.ruoyi.web.form.system;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.annotation.Excels;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.xss.Xss;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
public class SysUserForm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Excel(name = "用户序号", type = Type.EXPORT, cellType = ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    /**
     * 部门ID
     */
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /**
     * 用户账号
     */
    @Excel(name = "登录名称")
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户名称")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Excel(name = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码", cellType = ColumnType.TEXT)
    private String phonenumber;

    /**
     * 用户性别
     */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号状态（0正常 1停用）
     */
    @Excel(name = "账号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登录IP
     */
    @Excel(name = "最后登录IP", type = Type.EXPORT)
    private String loginIp;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    /**
     * 密码最后更新时间
     */
    private Date pwdUpdateDate;

    /**
     * 部门对象
     */
    @Excels({
            @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT),
            @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    private SysDeptForm dept;

    /**
     * 角色对象
     */
    private List<SysRoleForm> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 接收 默认初始化参数，初始化当前对象的依赖或属性；不执行持久化。
     */
    public SysUserForm() {

    }

    /**
     * 接收 userId，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param userId userId 请求参数或领域数据
     */
    public SysUserForm(Long userId) {
        this.userId = userId;
    }

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
     * isAdmin：按原顺序执行 isAdmin 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean isAdmin() {
        return SecurityUtils.isAdmin(this.userId);
    }

    /**
     * 读取并返回当前对象的 DeptId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getDeptId() {
        return deptId;
    }

    /**
     * 将参数 deptId 写入当前对象的 DeptId 属性，不执行持久化。
     *
     * @param deptId deptId 请求参数或领域数据
     */
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    /**
     * 读取并返回当前对象的 NickName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName() {
        return nickName;
    }

    /**
     * 将参数 nickName 写入当前对象的 NickName 属性，不执行持久化。
     *
     * @param nickName nickName 请求参数或领域数据
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 读取并返回当前对象的 UserName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    /**
     * 将参数 userName 写入当前对象的 UserName 属性，不执行持久化。
     *
     * @param userName userName 请求参数或领域数据
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 读取并返回当前对象的 Email 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    /**
     * 将参数 email 写入当前对象的 Email 属性，不执行持久化。
     *
     * @param email email 请求参数或领域数据
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 读取并返回当前对象的 Phonenumber 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * 将参数 phonenumber 写入当前对象的 Phonenumber 属性，不执行持久化。
     *
     * @param phonenumber phonenumber 请求参数或领域数据
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * 读取并返回当前对象的 Sex 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getSex() {
        return sex;
    }

    /**
     * 将参数 sex 写入当前对象的 Sex 属性，不执行持久化。
     *
     * @param sex sex 请求参数或领域数据
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 读取并返回当前对象的 Avatar 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 将参数 avatar 写入当前对象的 Avatar 属性，不执行持久化。
     *
     * @param avatar avatar 请求参数或领域数据
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 读取并返回当前对象的 Password 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    /**
     * 将参数 password 写入当前对象的 Password 属性，不执行持久化。
     *
     * @param password password 请求参数或领域数据
     */
    public void setPassword(String password) {
        this.password = password;
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
     * 读取并返回当前对象的 LoginIp 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getLoginIp() {
        return loginIp;
    }

    /**
     * 将参数 loginIp 写入当前对象的 LoginIp 属性，不执行持久化。
     *
     * @param loginIp loginIp 请求参数或领域数据
     */
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    /**
     * 读取并返回当前对象的 LoginDate 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Date getLoginDate() {
        return loginDate;
    }

    /**
     * 将参数 loginDate 写入当前对象的 LoginDate 属性，不执行持久化。
     *
     * @param loginDate loginDate 请求参数或领域数据
     */
    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    /**
     * 读取并返回当前对象的 PwdUpdateDate 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Date getPwdUpdateDate() {
        return pwdUpdateDate;
    }

    /**
     * 将参数 pwdUpdateDate 写入当前对象的 PwdUpdateDate 属性，不执行持久化。
     *
     * @param pwdUpdateDate pwdUpdateDate 请求参数或领域数据
     */
    public void setPwdUpdateDate(Date pwdUpdateDate) {
        this.pwdUpdateDate = pwdUpdateDate;
    }

    /**
     * 读取并返回当前对象的 Dept 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public SysDeptForm getDept() {
        return dept;
    }

    /**
     * 将参数 dept 写入当前对象的 Dept 属性，不执行持久化。
     *
     * @param dept dept 请求参数或领域数据
     */
    public void setDept(SysDeptForm dept) {
        this.dept = dept;
    }

    /**
     * 读取并返回当前对象的 Roles 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public List<SysRoleForm> getRoles() {
        return roles;
    }

    /**
     * 将参数 roles 写入当前对象的 Roles 属性，不执行持久化。
     *
     * @param roles roles 请求参数或领域数据
     */
    public void setRoles(List<SysRoleForm> roles) {
        this.roles = roles;
    }

    /**
     * 读取并返回当前对象的 RoleIds 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long[] getRoleIds() {
        return roleIds;
    }

    /**
     * 将参数 roleIds 写入当前对象的 RoleIds 属性，不执行持久化。
     *
     * @param roleIds roleIds 请求参数或领域数据
     */
    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
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
