package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 公告已读记录表 sys_notice_read
 *
 * @author ruoyi
 */
@TableName(value = "sys_notice_read", autoResultMap = true)
public class SysNoticeRead {
    /**
     * 主键
     */
    @TableId(value = "read_id", type = IdType.AUTO)
    private Long readId;

    /**
     * 公告ID
     */
    @TableField(value = "notice_id", insertStrategy = FieldStrategy.NOT_NULL)
    private Long noticeId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id", insertStrategy = FieldStrategy.NOT_NULL)
    private Long userId;

    /**
     * 阅读时间
     */
    @TableField(value = "read_time", insertStrategy = FieldStrategy.NOT_NULL)
    private Date readTime;

    public Long getReadId() {
        return readId;
    }

    public void setReadId(Long readId) {
        this.readId = readId;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("readId", getReadId())
                .append("noticeId", getNoticeId())
                .append("userId", getUserId())
                .append("readTime", getReadTime())
                .toString();
    }
}
