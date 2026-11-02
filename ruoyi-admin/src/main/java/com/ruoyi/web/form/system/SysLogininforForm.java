package com.ruoyi.web.form.system;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author ruoyi
 */
public class SysLogininforForm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Excel(name = "序号", cellType = ColumnType.NUMERIC)
    private Long infoId;

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    private String status;

    /**
     * 登录IP地址
     */
    @Excel(name = "登录地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @Excel(name = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @Excel(name = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Excel(name = "操作系统")
    private String os;

    /**
     * 提示消息
     */
    @Excel(name = "提示消息")
    private String msg;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "访问时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    /**
     * 读取并返回当前对象的 InfoId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getInfoId() {
        return infoId;
    }

    /**
     * 将参数 infoId 写入当前对象的 InfoId 属性，不执行持久化。
     *
     * @param infoId infoId 请求参数或领域数据
     */
    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    /**
     * 读取并返回当前对象的 UserName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 将参数 userName 写入当前对象的 UserName 属性，不执行持久化。
     *
     * @param userName userName 请求参数或领域数据
     */
    public void setUserName(String userName) {
        this.userName = userName;
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

    /**
     * 读取并返回当前对象的 Ipaddr 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getIpaddr() {
        return ipaddr;
    }

    /**
     * 将参数 ipaddr 写入当前对象的 Ipaddr 属性，不执行持久化。
     *
     * @param ipaddr ipaddr 请求参数或领域数据
     */
    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    /**
     * 读取并返回当前对象的 LoginLocation 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getLoginLocation() {
        return loginLocation;
    }

    /**
     * 将参数 loginLocation 写入当前对象的 LoginLocation 属性，不执行持久化。
     *
     * @param loginLocation loginLocation 请求参数或领域数据
     */
    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    /**
     * 读取并返回当前对象的 Browser 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * 将参数 browser 写入当前对象的 Browser 属性，不执行持久化。
     *
     * @param browser browser 请求参数或领域数据
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * 读取并返回当前对象的 Os 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getOs() {
        return os;
    }

    /**
     * 将参数 os 写入当前对象的 Os 属性，不执行持久化。
     *
     * @param os os 请求参数或领域数据
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * 读取并返回当前对象的 Msg 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 将参数 msg 写入当前对象的 Msg 属性，不执行持久化。
     *
     * @param msg msg 请求参数或领域数据
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 读取并返回当前对象的 LoginTime 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 将参数 loginTime 写入当前对象的 LoginTime 属性，不执行持久化。
     *
     * @param loginTime loginTime 请求参数或领域数据
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
