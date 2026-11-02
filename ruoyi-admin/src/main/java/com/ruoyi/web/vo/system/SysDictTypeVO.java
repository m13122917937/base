package com.ruoyi.web.vo.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 字典类型表 sys_dict_type
 *
 * @author ruoyi
 */
public class SysDictTypeVO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @Excel(name = "字典主键", cellType = ColumnType.NUMERIC)
    private Long dictId;

    /**
     * 字典名称
     */
    @Excel(name = "字典名称")
    private String dictName;

    /**
     * 字典类型
     */
    @Excel(name = "字典类型")
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 读取并返回当前对象的 DictId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getDictId() {
        return dictId;
    }

    /**
     * 将参数 dictId 写入当前对象的 DictId 属性，不执行持久化。
     *
     * @param dictId dictId 请求参数或领域数据
     */
    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    /**
     * 读取并返回当前对象的 DictName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(min = 0, max = 100, message = "字典类型名称长度不能超过100个字符")
    public String getDictName() {
        return dictName;
    }

    /**
     * 将参数 dictName 写入当前对象的 DictName 属性，不执行持久化。
     *
     * @param dictName dictName 请求参数或领域数据
     */
    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    /**
     * 读取并返回当前对象的 DictType 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(min = 0, max = 100, message = "字典类型类型长度不能超过100个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）")
    public String getDictType() {
        return dictType;
    }

    /**
     * 将参数 dictType 写入当前对象的 DictType 属性，不执行持久化。
     *
     * @param dictType dictType 请求参数或领域数据
     */
    public void setDictType(String dictType) {
        this.dictType = dictType;
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

}
