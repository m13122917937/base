package com.ruoyi.web.vo.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 字典数据表 sys_dict_data
 *
 * @author ruoyi
 */
public class SysDictDataVO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @Excel(name = "字典编码", cellType = ColumnType.NUMERIC)
    private Long dictCode;

    /**
     * 字典排序
     */
    @Excel(name = "字典排序", cellType = ColumnType.NUMERIC)
    private Long dictSort;

    /**
     * 字典标签
     */
    @Excel(name = "字典标签")
    private String dictLabel;

    /**
     * 字典键值
     */
    @Excel(name = "字典键值")
    private String dictValue;

    /**
     * 字典类型
     */
    @Excel(name = "字典类型")
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;

    /**
     * 表格字典样式
     */
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @Excel(name = "是否默认", readConverterExp = "Y=是,N=否")
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 读取并返回当前对象的 DictCode 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getDictCode() {
        return dictCode;
    }

    /**
     * 将参数 dictCode 写入当前对象的 DictCode 属性，不执行持久化。
     *
     * @param dictCode dictCode 请求参数或领域数据
     */
    public void setDictCode(Long dictCode) {
        this.dictCode = dictCode;
    }

    /**
     * 读取并返回当前对象的 DictSort 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getDictSort() {
        return dictSort;
    }

    /**
     * 将参数 dictSort 写入当前对象的 DictSort 属性，不执行持久化。
     *
     * @param dictSort dictSort 请求参数或领域数据
     */
    public void setDictSort(Long dictSort) {
        this.dictSort = dictSort;
    }

    /**
     * 读取并返回当前对象的 DictLabel 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(min = 0, max = 100, message = "字典标签长度不能超过100个字符")
    public String getDictLabel() {
        return dictLabel;
    }

    /**
     * 将参数 dictLabel 写入当前对象的 DictLabel 属性，不执行持久化。
     *
     * @param dictLabel dictLabel 请求参数或领域数据
     */
    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    /**
     * 读取并返回当前对象的 DictValue 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "字典键值不能为空")
    @Size(min = 0, max = 100, message = "字典键值长度不能超过100个字符")
    public String getDictValue() {
        return dictValue;
    }

    /**
     * 将参数 dictValue 写入当前对象的 DictValue 属性，不执行持久化。
     *
     * @param dictValue dictValue 请求参数或领域数据
     */
    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    /**
     * 读取并返回当前对象的 DictType 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(min = 0, max = 100, message = "字典类型长度不能超过100个字符")
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
     * 读取并返回当前对象的 CssClass 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Size(min = 0, max = 100, message = "样式属性长度不能超过100个字符")
    public String getCssClass() {
        return cssClass;
    }

    /**
     * 将参数 cssClass 写入当前对象的 CssClass 属性，不执行持久化。
     *
     * @param cssClass cssClass 请求参数或领域数据
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * 读取并返回当前对象的 ListClass 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getListClass() {
        return listClass;
    }

    /**
     * 将参数 listClass 写入当前对象的 ListClass 属性，不执行持久化。
     *
     * @param listClass listClass 请求参数或领域数据
     */
    public void setListClass(String listClass) {
        this.listClass = listClass;
    }

    /**
     * 读取并返回当前对象的 Default 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean getDefault() {
        return UserConstants.YES.equals(this.isDefault);
    }

    /**
     * 读取并返回当前对象的 IsDefault 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getIsDefault() {
        return isDefault;
    }

    /**
     * 将参数 isDefault 写入当前对象的 IsDefault 属性，不执行持久化。
     *
     * @param isDefault isDefault 请求参数或领域数据
     */
    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
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
