package com.ruoyi.common.exception.user;

/**
 * 黑名单IP异常类
 *
 * @author ruoyi
 */
public class BlackListException extends UserException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建固定中文提示的用户异常，不依赖语言配置或 Spring 容器。
     */
    public BlackListException() {
        super("很遗憾，访问IP已被列入系统黑名单");
    }
}
