package com.ruoyi.system.model.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.system.model.bo.SysUserBO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * 登录用户身份权限
 *
 * @author ruoyi
 */
public class LoginSessionBO implements UserDetails {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 用户信息
     */
    private SysUserBO user;

    /**
     * 接收 默认初始化参数，初始化当前对象的依赖或属性；不执行持久化。
     */
    public LoginSessionBO() {
    }

    /**
     * 接收 user, permissions，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param user        user 请求参数或领域数据
     * @param permissions permissions 请求参数或领域数据
     */
    public LoginSessionBO(SysUserBO user, Set<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    /**
     * 接收 userId, deptId, user, permissions，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param userId      userId 请求参数或领域数据
     * @param deptId      deptId 请求参数或领域数据
     * @param user        user 请求参数或领域数据
     * @param permissions permissions 请求参数或领域数据
     */
    public LoginSessionBO(Long userId, Long deptId, SysUserBO user, Set<String> permissions) {
        this.userId = userId;
        this.deptId = deptId;
        this.user = user;
        this.permissions = permissions;
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
     * 读取并返回当前对象的 Token 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getToken() {
        return token;
    }

    /**
     * 将参数 token 写入当前对象的 Token 属性，不执行持久化。
     *
     * @param token token 请求参数或领域数据
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 读取并返回当前对象的 Password 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @JsonIgnore
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 读取并返回当前对象的 Username 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /**
     * isAccountNonExpired：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * isAccountNonLocked：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * isCredentialsNonExpired：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * isEnabled：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 读取并返回当前对象的 LoginTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getLoginTime() {
        return loginTime;
    }

    /**
     * 将参数 loginTime 写入当前对象的 LoginTime 属性，不执行持久化。
     *
     * @param loginTime loginTime 请求参数或领域数据
     */
    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 读取并返回当前对象的 Ipaddr 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getIpaddr() {
        return ipaddr;
    }

    /**
     * 将参数 ipaddr 写入当前对象的 Ipaddr 属性，不执行持久化。
     *
     * @param ipaddr ipaddr 请求参数或领域数据
     */
    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    /**
     * 读取并返回当前对象的 LoginLocation 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getLoginLocation() {
        return loginLocation;
    }

    /**
     * 将参数 loginLocation 写入当前对象的 LoginLocation 属性，不执行持久化。
     *
     * @param loginLocation loginLocation 请求参数或领域数据
     */
    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    /**
     * 读取并返回当前对象的 Browser 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * 将参数 browser 写入当前对象的 Browser 属性，不执行持久化。
     *
     * @param browser browser 请求参数或领域数据
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * 读取并返回当前对象的 Os 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getOs() {
        return os;
    }

    /**
     * 将参数 os 写入当前对象的 Os 属性，不执行持久化。
     *
     * @param os os 请求参数或领域数据
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * 读取并返回当前对象的 ExpireTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getExpireTime() {
        return expireTime;
    }

    /**
     * 将参数 expireTime 写入当前对象的 ExpireTime 属性，不执行持久化。
     *
     * @param expireTime expireTime 请求参数或领域数据
     */
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
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

    /**
     * 读取并返回当前对象的 User 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public SysUserBO getUser() {
        return user;
    }

    /**
     * 将参数 user 写入当前对象的 User 属性，不执行持久化。
     *
     * @param user user 请求参数或领域数据
     */
    public void setUser(SysUserBO user) {
        this.user = user;
    }

    /**
     * 沿用原认证主体的 UserDetails 契约返回 null，细粒度权限由 permissions 字段表达。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
