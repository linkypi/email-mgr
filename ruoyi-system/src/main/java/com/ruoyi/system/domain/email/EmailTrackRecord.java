package com.ruoyi.system.domain.email;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 邮件跟踪记录对象 email_track_record
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailTrackRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 邮件Message-ID */
    @Excel(name = "邮件Message-ID")
    private String messageId;

    /** 邮件主题 */
    @Excel(name = "邮件主题")
    private String subject;

    /** 收件人 */
    @Excel(name = "收件人")
    private String recipient;

    /** 发件人 */
    @Excel(name = "发件人")
    private String sender;

    /** 邮件内容 */
    @Excel(name = "邮件内容")
    private String content;

    /** 邮件状态 */
    @Excel(name = "邮件状态")
    private String status;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sentTime;

    /** 送达时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "送达时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date deliveredTime;

    /** 打开时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "打开时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date openedTime;

    /** 回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "回复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date repliedTime;

    /** 点击时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "点击时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date clickedTime;

    /** 重试次数 */
    @Excel(name = "重试次数")
    private Integer retryCount;

    /** 错误日志 */
    @Excel(name = "错误日志")
    private String errorLogs;

    /** 邮箱账号ID */
    @Excel(name = "邮箱账号ID")
    private Long accountId;

    /** 跟踪像素URL */
    @Excel(name = "跟踪像素URL")
    private String trackingPixelUrl;

    /** 跟踪链接URL */
    @Excel(name = "跟踪链接URL")
    private String trackingLinkUrl;

    /** 删除标志（0代表存在 2代表删除） */
    private String deleted;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setMessageId(String messageId) 
    {
        this.messageId = messageId;
    }

    public String getMessageId() 
    {
        return messageId;
    }

    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }

    public void setRecipient(String recipient) 
    {
        this.recipient = recipient;
    }

    public String getRecipient() 
    {
        return recipient;
    }

    public void setSender(String sender) 
    {
        this.sender = sender;
    }

    public String getSender() 
    {
        return sender;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setSentTime(Date sentTime) 
    {
        this.sentTime = sentTime;
    }

    public Date getSentTime() 
    {
        return sentTime;
    }

    public void setDeliveredTime(Date deliveredTime) 
    {
        this.deliveredTime = deliveredTime;
    }

    public Date getDeliveredTime() 
    {
        return deliveredTime;
    }

    public void setOpenedTime(Date openedTime) 
    {
        this.openedTime = openedTime;
    }

    public Date getOpenedTime() 
    {
        return openedTime;
    }

    public void setRepliedTime(Date repliedTime) 
    {
        this.repliedTime = repliedTime;
    }

    public Date getRepliedTime() 
    {
        return repliedTime;
    }

    public void setClickedTime(Date clickedTime) 
    {
        this.clickedTime = clickedTime;
    }

    public Date getClickedTime() 
    {
        return clickedTime;
    }

    public void setRetryCount(Integer retryCount) 
    {
        this.retryCount = retryCount;
    }

    public Integer getRetryCount() 
    {
        return retryCount;
    }

    public void setErrorLogs(String errorLogs) 
    {
        this.errorLogs = errorLogs;
    }

    public String getErrorLogs() 
    {
        return errorLogs;
    }

    public void setAccountId(Long accountId) 
    {
        this.accountId = accountId;
    }

    public Long getAccountId() 
    {
        return accountId;
    }

    public void setTrackingPixelUrl(String trackingPixelUrl) 
    {
        this.trackingPixelUrl = trackingPixelUrl;
    }

    public String getTrackingPixelUrl() 
    {
        return trackingPixelUrl;
    }

    public void setTrackingLinkUrl(String trackingLinkUrl) 
    {
        this.trackingLinkUrl = trackingLinkUrl;
    }

    public String getTrackingLinkUrl() 
    {
        return trackingLinkUrl;
    }

    public void setDeleted(String deleted) 
    {
        this.deleted = deleted;
    }

    public String getDeleted() 
    {
        return deleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskId", getTaskId())
            .append("messageId", getMessageId())
            .append("subject", getSubject())
            .append("recipient", getRecipient())
            .append("sender", getSender())
            .append("content", getContent())
            .append("status", getStatus())
            .append("sentTime", getSentTime())
            .append("deliveredTime", getDeliveredTime())
            .append("openedTime", getOpenedTime())
            .append("repliedTime", getRepliedTime())
            .append("clickedTime", getClickedTime())
            .append("retryCount", getRetryCount())
            .append("errorLogs", getErrorLogs())
            .append("accountId", getAccountId())
            .append("trackingPixelUrl", getTrackingPixelUrl())
            .append("trackingLinkUrl", getTrackingLinkUrl())
            .append("deleted", getDeleted())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
