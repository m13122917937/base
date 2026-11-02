package com.ruoyi.common.exception.user;

/**
 * 用户不存在异常类
 *
 * @author ruoyi
 */
public class UserNotExistsException extends UserException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建固定中文提示的用户异常，不依赖语言配置或 Spring 容器。
     */
    public UserNotExistsException() {
        super("用户不存在/密码错误");
    }
}
