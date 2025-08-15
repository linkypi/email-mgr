package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 邮件发送记录对象 email_send_record
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailSendRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 联系人ID */
    @Excel(name = "联系人ID")
    private Long contactId;

    /** 发送账号ID */
    @Excel(name = "发送账号ID")
    private Long accountId;

    /** 收件人邮箱 */
    @Excel(name = "收件人邮箱")
    private String emailAddress;

    /** 邮件主题 */
    @Excel(name = "邮件主题")
    private String subject;

    /** 邮件内容 */
    private String content;

    /** 发送时间 */
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String sendTime;

    /** 送达时间 */
    @Excel(name = "送达时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String deliveredTime;

    /** 打开时间 */
    @Excel(name = "打开时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String openedTime;

    /** 回复时间 */
    @Excel(name = "回复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String repliedTime;

    /** 发送状态 */
    @Excel(name = "发送状态")
    private String sendStatus;

    /** 是否送达 */
    @Excel(name = "是否送达", readConverterExp = "0=否,1=是")
    private String isDelivered;

    /** 是否打开 */
    @Excel(name = "是否打开", readConverterExp = "0=否,1=是")
    private String isOpened;

    /** 是否回复 */
    @Excel(name = "是否回复", readConverterExp = "0=否,1=是")
    private String isReplied;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setContactId(Long contactId) 
    {
        this.contactId = contactId;
    }

    public Long getContactId() 
    {
        return contactId;
    }

    public void setAccountId(Long accountId) 
    {
        this.accountId = accountId;
    }

    public Long getAccountId() 
    {
        return accountId;
    }

    public void setEmailAddress(String emailAddress) 
    {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() 
    {
        return emailAddress;
    }

    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setSendTime(String sendTime) 
    {
        this.sendTime = sendTime;
    }

    public String getSendTime() 
    {
        return sendTime;
    }

    public void setDeliveredTime(String deliveredTime) 
    {
        this.deliveredTime = deliveredTime;
    }

    public String getDeliveredTime() 
    {
        return deliveredTime;
    }

    public void setOpenedTime(String openedTime) 
    {
        this.openedTime = openedTime;
    }

    public String getOpenedTime() 
    {
        return openedTime;
    }

    public void setRepliedTime(String repliedTime) 
    {
        this.repliedTime = repliedTime;
    }

    public String getRepliedTime() 
    {
        return repliedTime;
    }

    public void setSendStatus(String sendStatus) 
    {
        this.sendStatus = sendStatus;
    }

    public String getSendStatus() 
    {
        return sendStatus;
    }

    public void setIsDelivered(String isDelivered) 
    {
        this.isDelivered = isDelivered;
    }

    public String getIsDelivered() 
    {
        return isDelivered;
    }

    public void setIsOpened(String isOpened) 
    {
        this.isOpened = isOpened;
    }

    public String getIsOpened() 
    {
        return isOpened;
    }

    public void setIsReplied(String isReplied) 
    {
        this.isReplied = isReplied;
    }

    public String getIsReplied() 
    {
        return isReplied;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("taskId", getTaskId())
            .append("contactId", getContactId())
            .append("accountId", getAccountId())
            .append("emailAddress", getEmailAddress())
            .append("subject", getSubject())
            .append("content", getContent())
            .append("sendTime", getSendTime())
            .append("deliveredTime", getDeliveredTime())
            .append("openedTime", getOpenedTime())
            .append("repliedTime", getRepliedTime())
            .append("sendStatus", getSendStatus())
            .append("isDelivered", getIsDelivered())
            .append("isOpened", getIsOpened())
            .append("isReplied", getIsReplied())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("deleted", 0)
            .toString();
    }
}

