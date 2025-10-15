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

    /** 接收时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "接收时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

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

    /** 是否星标(0=否,1=是) */
    @Excel(name = "是否星标", readConverterExp = "0=否,1=是")
    private Integer isStarred;

    /** 是否重要(0=否,1=是) */
    @Excel(name = "是否重要", readConverterExp = "0=否,1=是")
    private Integer isImportant;

    /** 是否已读(0=否,1=是) */
    @Excel(name = "是否已读", readConverterExp = "0=否,1=是")
    private Integer isRead;

    /** 文件夹类型(sent=发件箱,inbox=收件箱,starred=星标,deleted=已删除) */
    @Excel(name = "文件夹类型", readConverterExp = "sent=发件箱,inbox=收件箱,starred=星标,deleted=已删除")
    private String folderType;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 阅读时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "阅读时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    /** 星标时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "星标时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date starredTime;

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

    public void setReceiveTime(Date receiveTime) 
    {
        this.receiveTime = receiveTime;
    }

    public Date getReceiveTime() 
    {
        return receiveTime;
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

    public void setIsStarred(Integer isStarred) 
    {
        this.isStarred = isStarred;
    }

    public Integer getIsStarred() 
    {
        return isStarred;
    }

    public void setIsImportant(Integer isImportant) 
    {
        this.isImportant = isImportant;
    }

    public Integer getIsImportant() 
    {
        return isImportant;
    }

    public void setIsRead(Integer isRead) 
    {
        this.isRead = isRead;
    }

    public Integer getIsRead() 
    {
        return isRead;
    }

    public void setFolderType(String folderType) 
    {
        this.folderType = folderType;
    }

    public String getFolderType() 
    {
        return folderType;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setReadTime(Date readTime) 
    {
        this.readTime = readTime;
    }

    public Date getReadTime() 
    {
        return readTime;
    }

    public void setStarredTime(Date starredTime) 
    {
        this.starredTime = starredTime;
    }

    public Date getStarredTime() 
    {
        return starredTime;
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
            .append("receiveTime", getReceiveTime())
            .append("clickedTime", getClickedTime())
            .append("retryCount", getRetryCount())
            .append("errorLogs", getErrorLogs())
            .append("accountId", getAccountId())
            .append("trackingPixelUrl", getTrackingPixelUrl())
            .append("trackingLinkUrl", getTrackingLinkUrl())
            .append("deleted", getDeleted())
            .append("isStarred", getIsStarred())
            .append("isImportant", getIsImportant())
            .append("isRead", getIsRead())
            .append("folderType", getFolderType())
            .append("userId", getUserId())
            .append("readTime", getReadTime())
            .append("starredTime", getStarredTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
