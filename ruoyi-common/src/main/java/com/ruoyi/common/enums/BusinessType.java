package com.ruoyi.common.enums;

/**
 * 业务操作类型
 *
 * @author ruoyi
 */
public enum BusinessType {
    /**
     * 其它
     */
    OTHER(0),

    /**
     * 新增
     */
    INSERT(1),

    /**
     * 修改
     */
    UPDATE(2),

    /**
     * 删除
     */
    DELETE(3),

    /**
     * 授权
     */
    GRANT(4),

    /**
     * 导出
     */
    EXPORT(5),

    /**
     * 导入
     */
    IMPORT(6),

    /**
     * 强退
     */
    FORCE(7),

    /**
     * 清空数据
     */
    CLEAN(9);

    private final int code;

    /**
     * 保存既有操作日志编码；删除操作类型后不重排数据库中的历史编码。
     *
     * @param code 持久化日志及操作字典使用的固定编码
     */
    BusinessType(int code) {
        this.code = code;
    }

    /**
     * 返回固定日志编码，不依赖枚举声明顺序，不访问数据库。
     *
     * @return 与既有操作字典一致的编码
     */
    public int getCode() {
        return code;
    }
}
