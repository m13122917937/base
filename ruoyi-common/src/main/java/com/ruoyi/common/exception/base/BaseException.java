package com.ruoyi.common.exception.base;

/**
 * 固定中文消息的业务基础异常，不解析语言、资源键或国际化参数。
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String module;

    /**
     * 保存异常所属模块及可直接展示的消息，不访问 Spring 或外部资源。
     */
    public BaseException(String module, String message) {
        super(message);
        this.module = module;
    }

    /**
     * 保存无模块的业务消息，不执行国际化查找。
     */
    public BaseException(String message) {
        this(null, message);
    }

    /**
     * 返回异常所属模块；未指定模块时返回 null。
     */
    public String getModule() {
        return module;
    }
}
