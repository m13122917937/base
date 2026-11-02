package com.ruoyi.web.vo.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 参数配置表 sys_config
 *
 * @author ruoyi
 */
public class SysConfigVO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 参数主键
     */
    @Excel(name = "参数主键", cellType = ColumnType.NUMERIC)
    private Long configId;

    /**
     * 参数名称
     */
    @Excel(name = "参数名称")
    private String configName;

    /**
     * 参数键名
     */
    @Excel(name = "参数键名")
    private String configKey;

    /**
     * 参数键值
     */
    @Excel(name = "参数键值")
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    @Excel(name = "系统内置", readConverterExp = "Y=是,N=否")
    private String configType;

    /**
     * 读取并返回当前对象的 ConfigId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getConfigId() {
        return configId;
    }

    /**
     * 将参数 configId 写入当前对象的 ConfigId 属性，不执行持久化。
     *
     * @param configId configId 请求参数或领域数据
     */
    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    /**
     * 读取并返回当前对象的 ConfigName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符")
    public String getConfigName() {
        return configName;
    }

    /**
     * 将参数 configName 写入当前对象的 ConfigName 属性，不执行持久化。
     *
     * @param configName configName 请求参数或领域数据
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * 读取并返回当前对象的 ConfigKey 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "参数键名长度不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符")
    public String getConfigKey() {
        return configKey;
    }

    /**
     * 将参数 configKey 写入当前对象的 ConfigKey 属性，不执行持久化。
     *
     * @param configKey configKey 请求参数或领域数据
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * 读取并返回当前对象的 ConfigValue 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "参数键值不能为空")
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符")
    public String getConfigValue() {
        return configValue;
    }

    /**
     * 将参数 configValue 写入当前对象的 ConfigValue 属性，不执行持久化。
     *
     * @param configValue configValue 请求参数或领域数据
     */
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    /**
     * 读取并返回当前对象的 ConfigType 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getConfigType() {
        return configType;
    }

    /**
     * 将参数 configType 写入当前对象的 ConfigType 属性，不执行持久化。
     *
     * @param configType configType 请求参数或领域数据
     */
    public void setConfigType(String configType) {
        this.configType = configType;
    }

}
