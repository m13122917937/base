package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
@TableName(value = "sys_logininfor", autoResultMap = true, excludeProperty = {"searchValue", "createBy", "createTime", "updateBy", "updateTime", "remark", "params"})
public class SysLogininfor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Excel(name = "序号", cellType = ColumnType.NUMERIC)
    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    @TableField(value = "user_name", insertStrategy = FieldStrategy.ALWAYS)
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    @TableField(value = "status", insertStrategy = FieldStrategy.ALWAYS)
    private String status;

    /**
     * 登录IP地址
     */
    @Excel(name = "登录地址")
    @TableField(value = "ipaddr", insertStrategy = FieldStrategy.ALWAYS)
    private String ipaddr;

    /**
     * 登录地点
     */
    @Excel(name = "登录地点")
    @TableField(value = "login_location", insertStrategy = FieldStrategy.ALWAYS)
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @Excel(name = "浏览器")
    @TableField(value = "browser", insertStrategy = FieldStrategy.ALWAYS)
    private String browser;

    /**
     * 操作系统
     */
    @Excel(name = "操作系统")
    @TableField(value = "os", insertStrategy = FieldStrategy.ALWAYS)
    private String os;

    /**
     * 提示消息
     */
    @Excel(name = "提示消息")
    @TableField(value = "msg", insertStrategy = FieldStrategy.ALWAYS)
    private String msg;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "访问时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "login_time", insertStrategy = FieldStrategy.NOT_NULL)
    private Date loginTime;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
