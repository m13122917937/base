package com.ruoyi.common.core.domain.model;

/**
 * 账号密码登录参数，不包含验证码。
 */
public class LoginBody {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 读取用户名，不执行验证或外部调用。
     */
    public String getUsername() {
        return username;
    }

    /**
     * 保存用户名，不执行持久化或输出日志。
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 读取密码，不执行验证或外部调用。
     */
    public String getPassword() {
        return password;
    }

    /**
     * 保存密码，不执行持久化或输出日志。
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
