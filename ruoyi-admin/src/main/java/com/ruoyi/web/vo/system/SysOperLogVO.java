package com.ruoyi.web.vo.system;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 操作日志记录表 oper_log
 *
 * @author ruoyi
 */
public class SysOperLogVO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @Excel(name = "操作序号", cellType = ColumnType.NUMERIC)
    private Long operId;

    /**
     * 操作模块
     */
    @Excel(name = "操作模块")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @Excel(name = "业务类型", readConverterExp = "0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据")
    private Integer businessType;

    /**
     * 业务类型数组
     */
    private Integer[] businessTypes;

    /**
     * 请求方法
     */
    @Excel(name = "请求方法")
    private String method;

    /**
     * 请求方式
     */
    @Excel(name = "请求方式")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @Excel(name = "操作类别", readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @Excel(name = "操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称")
    private String deptName;

    /**
     * 请求url
     */
    @Excel(name = "请求地址")
    private String operUrl;

    /**
     * 操作地址
     */
    @Excel(name = "操作地址")
    private String operIp;

    /**
     * 操作地点
     */
    @Excel(name = "操作地点")
    private String operLocation;

    /**
     * 请求参数
     */
    @Excel(name = "请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @Excel(name = "返回参数")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private Integer status;

    /**
     * 错误消息
     */
    @Excel(name = "错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    /**
     * 消耗时间
     */
    @Excel(name = "消耗时间", suffix = "毫秒")
    private Long costTime;

    /**
     * 读取并返回当前对象的 OperId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getOperId() {
        return operId;
    }

    /**
     * 将参数 operId 写入当前对象的 OperId 属性，不执行持久化。
     *
     * @param operId operId 请求参数或领域数据
     */
    public void setOperId(Long operId) {
        this.operId = operId;
    }

    /**
     * 读取并返回当前对象的 Title 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getTitle() {
        return title;
    }

    /**
     * 将参数 title 写入当前对象的 Title 属性，不执行持久化。
     *
     * @param title title 请求参数或领域数据
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 读取并返回当前对象的 BusinessType 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Integer getBusinessType() {
        return businessType;
    }

    /**
     * 将参数 businessType 写入当前对象的 BusinessType 属性，不执行持久化。
     *
     * @param businessType businessType 请求参数或领域数据
     */
    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    /**
     * 读取并返回当前对象的 BusinessTypes 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Integer[] getBusinessTypes() {
        return businessTypes;
    }

    /**
     * 将参数 businessTypes 写入当前对象的 BusinessTypes 属性，不执行持久化。
     *
     * @param businessTypes businessTypes 请求参数或领域数据
     */
    public void setBusinessTypes(Integer[] businessTypes) {
        this.businessTypes = businessTypes;
    }

    /**
     * 读取并返回当前对象的 Method 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getMethod() {
        return method;
    }

    /**
     * 将参数 method 写入当前对象的 Method 属性，不执行持久化。
     *
     * @param method method 请求参数或领域数据
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 读取并返回当前对象的 RequestMethod 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * 将参数 requestMethod 写入当前对象的 RequestMethod 属性，不执行持久化。
     *
     * @param requestMethod requestMethod 请求参数或领域数据
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * 读取并返回当前对象的 OperatorType 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Integer getOperatorType() {
        return operatorType;
    }

    /**
     * 将参数 operatorType 写入当前对象的 OperatorType 属性，不执行持久化。
     *
     * @param operatorType operatorType 请求参数或领域数据
     */
    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    /**
     * 读取并返回当前对象的 OperName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getOperName() {
        return operName;
    }

    /**
     * 将参数 operName 写入当前对象的 OperName 属性，不执行持久化。
     *
     * @param operName operName 请求参数或领域数据
     */
    public void setOperName(String operName) {
        this.operName = operName;
    }

    /**
     * 读取并返回当前对象的 DeptName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * 将参数 deptName 写入当前对象的 DeptName 属性，不执行持久化。
     *
     * @param deptName deptName 请求参数或领域数据
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * 读取并返回当前对象的 OperUrl 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getOperUrl() {
        return operUrl;
    }

    /**
     * 将参数 operUrl 写入当前对象的 OperUrl 属性，不执行持久化。
     *
     * @param operUrl operUrl 请求参数或领域数据
     */
    public void setOperUrl(String operUrl) {
        this.operUrl = operUrl;
    }

    /**
     * 读取并返回当前对象的 OperIp 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getOperIp() {
        return operIp;
    }

    /**
     * 将参数 operIp 写入当前对象的 OperIp 属性，不执行持久化。
     *
     * @param operIp operIp 请求参数或领域数据
     */
    public void setOperIp(String operIp) {
        this.operIp = operIp;
    }

    /**
     * 读取并返回当前对象的 OperLocation 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getOperLocation() {
        return operLocation;
    }

    /**
     * 将参数 operLocation 写入当前对象的 OperLocation 属性，不执行持久化。
     *
     * @param operLocation operLocation 请求参数或领域数据
     */
    public void setOperLocation(String operLocation) {
        this.operLocation = operLocation;
    }

    /**
     * 读取并返回当前对象的 OperParam 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getOperParam() {
        return operParam;
    }

    /**
     * 将参数 operParam 写入当前对象的 OperParam 属性，不执行持久化。
     *
     * @param operParam operParam 请求参数或领域数据
     */
    public void setOperParam(String operParam) {
        this.operParam = operParam;
    }

    /**
     * 读取并返回当前对象的 JsonResult 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getJsonResult() {
        return jsonResult;
    }

    /**
     * 将参数 jsonResult 写入当前对象的 JsonResult 属性，不执行持久化。
     *
     * @param jsonResult jsonResult 请求参数或领域数据
     */
    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    /**
     * 读取并返回当前对象的 Status 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 将参数 status 写入当前对象的 Status 属性，不执行持久化。
     *
     * @param status status 请求参数或领域数据
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 读取并返回当前对象的 ErrorMsg 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 将参数 errorMsg 写入当前对象的 ErrorMsg 属性，不执行持久化。
     *
     * @param errorMsg errorMsg 请求参数或领域数据
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 读取并返回当前对象的 OperTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Date getOperTime() {
        return operTime;
    }

    /**
     * 将参数 operTime 写入当前对象的 OperTime 属性，不执行持久化。
     *
     * @param operTime operTime 请求参数或领域数据
     */
    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    /**
     * 读取并返回当前对象的 CostTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getCostTime() {
        return costTime;
    }

    /**
     * 将参数 costTime 写入当前对象的 CostTime 属性，不执行持久化。
     *
     * @param costTime costTime 请求参数或领域数据
     */
    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }
}
