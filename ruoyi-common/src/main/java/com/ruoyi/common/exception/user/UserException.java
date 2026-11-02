package com.ruoyi.common.exception.user;

import com.ruoyi.common.exception.base.BaseException;

/**
 * 用户信息异常类
 *
 * @author ruoyi
 */
public class UserException extends BaseException {
    private static final long serialVersionUID = 1L;

    /**
     * 保存可直接展示的业务消息，不解析国际化资源键。
     */
    public UserException(String message) {
        super("user", message);
    }
}
