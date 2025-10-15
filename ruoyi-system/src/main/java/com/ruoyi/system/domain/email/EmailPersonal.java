package com.ruoyi.system.domain.email;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 个人邮件对象 email_personal
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
public class EmailPersonal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 邮件ID */
    private Long emailId;

    /** 邮件Message-ID */
    @Excel(name = "邮件Message-ID")
    private String messageId;

    /** 发件人邮箱 */
    @Excel(name = "发件人邮箱")
    private String fromAddress;

    /** 收件人邮箱 */
    @Excel(name = "收件人邮箱")
    private String toAddress;

    /** 邮件主题 */
    @Excel(name = "邮件主题")
    private String subject;

    /** 邮件内容 */
    @Excel(name = "邮件内容")
    private String content;

    /** HTML内容 */
    private String htmlContent;

    /** 邮件状态(unread=未读,read=已读,starred=星标,deleted=已删除) */
    @Excel(name = "邮件状态", readConverterExp = "unread=未读,read=已读,starred=星标,deleted=已删除")
    private String status;

    /** 是否星标(0=否,1=是) */
    @Excel(name = "是否星标", readConverterExp = "0=否,1=是")
    private Integer isStarred;

    /** 是否重要(0=否,1=是) */
    @Excel(name = "是否重要", readConverterExp = "0=否,1=是")
    private Integer isImportant;

    /** 接收时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "接收时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /** 邮件类型(inbox=收件箱,sent=发件箱,starred=星标,deleted=已删除) */
    @Excel(name = "邮件类型", readConverterExp = "inbox=收件箱,sent=发件箱,starred=星标,deleted=已删除")
    private String emailType;

    /** 附件数量 */
    @Excel(name = "附件数量")
    private Integer attachmentCount;

    /** 是否已回复：0=否，1=是 */
    @Excel(name = "是否已回复", readConverterExp = "0=否,1=是")
    private Integer isReplied;

    /** 是否已送达：0=否，1=是 */
    @Excel(name = "是否已送达", readConverterExp = "0=否,1=是")
    private Integer isDelivered;

    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "删除时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    public void setEmailId(Long emailId) 
    {
        this.emailId = emailId;
    }

    public Long getEmailId() 
    {
        return emailId;
    }
    public void setMessageId(String messageId) 
    {
        this.messageId = messageId;
    }

    public String getMessageId() 
    {
        return messageId;
    }
    public void setFromAddress(String fromAddress) 
    {
        this.fromAddress = fromAddress;
    }

    public String getFromAddress() 
    {
        return fromAddress;
    }
    public void setToAddress(String toAddress) 
    {
        this.toAddress = toAddress;
    }

    public String getToAddress() 
    {
        return toAddress;
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
    public void setHtmlContent(String htmlContent) 
    {
        this.htmlContent = htmlContent;
    }

    public String getHtmlContent() 
    {
        return htmlContent;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
    public void setReceiveTime(Date receiveTime) 
    {
        this.receiveTime = receiveTime;
    }

    public Date getReceiveTime() 
    {
        return receiveTime;
    }
    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }
    public void setEmailType(String emailType) 
    {
        this.emailType = emailType;
    }

    public String getEmailType() 
    {
        return emailType;
    }
    public void setAttachmentCount(Integer attachmentCount) 
    {
        this.attachmentCount = attachmentCount;
    }

    public Integer getAttachmentCount() 
    {
        return attachmentCount;
    }
    public void setIsReplied(Integer isReplied) 
    {
        this.isReplied = isReplied;
    }

    public Integer getIsReplied() 
    {
        return isReplied;
    }
    public void setIsDelivered(Integer isDelivered) 
    {
        this.isDelivered = isDelivered;
    }

    public Integer getIsDelivered() 
    {
        return isDelivered;
    }
    public void setDeleteTime(Date deleteTime) 
    {
        this.deleteTime = deleteTime;
    }

    public Date getDeleteTime() 
    {
        return deleteTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("emailId", getEmailId())
            .append("messageId", getMessageId())
            .append("fromAddress", getFromAddress())
            .append("toAddress", getToAddress())
            .append("subject", getSubject())
            .append("content", getContent())
            .append("htmlContent", getHtmlContent())
            .append("status", getStatus())
            .append("isStarred", getIsStarred())
            .append("isImportant", getIsImportant())
            .append("receiveTime", getReceiveTime())
            .append("sendTime", getSendTime())
            .append("emailType", getEmailType())
            .append("attachmentCount", getAttachmentCount())
            .append("isReplied", getIsReplied())
            .append("isDelivered", getIsDelivered())
            .append("deleteTime", getDeleteTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}