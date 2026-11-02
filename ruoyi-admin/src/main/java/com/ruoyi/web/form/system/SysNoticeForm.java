package com.ruoyi.web.form.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.xss.Xss;

/**
 * 通知公告表 sys_notice
 *
 * @author ruoyi
 */
public class SysNoticeForm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    private String status;

    /**
     * 是否已读
     */
    @JsonProperty("isRead")
    private boolean isRead;

    /**
     * 读取并返回当前对象的 NoticeId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getNoticeId() {
        return noticeId;
    }

    /**
     * 将参数 noticeId 写入当前对象的 NoticeId 属性，不执行持久化。
     *
     * @param noticeId noticeId 请求参数或领域数据
     */
    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    /**
     * 将参数 noticeTitle 写入当前对象的 NoticeTitle 属性，不执行持久化。
     *
     * @param noticeTitle noticeTitle 请求参数或领域数据
     */
    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    /**
     * 读取并返回当前对象的 NoticeTitle 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    public String getNoticeTitle() {
        return noticeTitle;
    }

    /**
     * 将参数 noticeType 写入当前对象的 NoticeType 属性，不执行持久化。
     *
     * @param noticeType noticeType 请求参数或领域数据
     */
    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    /**
     * 读取并返回当前对象的 NoticeType 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getNoticeType() {
        return noticeType;
    }

    /**
     * 将参数 noticeContent 写入当前对象的 NoticeContent 属性，不执行持久化。
     *
     * @param noticeContent noticeContent 请求参数或领域数据
     */
    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    /**
     * 读取并返回当前对象的 NoticeContent 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getNoticeContent() {
        return noticeContent;
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
     * 读取并返回当前对象的 Status 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getStatus() {
        return status;
    }

    /**
     * 读取并返回当前对象的 IsRead 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean getIsRead() {
        return isRead;
    }

    /**
     * 将参数 isRead 写入当前对象的 IsRead 属性，不执行持久化。
     *
     * @param isRead isRead 请求参数或领域数据
     */
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

}
