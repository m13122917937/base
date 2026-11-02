package com.ruoyi.common.exception.user;

/**
 * 用户密码不正确或不符合规范异常类
 *
 * @author ruoyi
 */
public class UserPasswordNotMatchException extends UserException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建固定中文提示的用户异常，不依赖语言配置或 Spring 容器。
     */
    public UserPasswordNotMatchException() {
        super("用户不存在/密码错误");
    }
}
