package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 个人邮件对象 email_personal
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailPersonal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 邮件ID */
    private Long emailId;

    /** 邮件类型(1收件 2发件) */
    @Excel(name = "邮件类型", readConverterExp = "1=收件,2=发件")
    private String emailType;

    /** 发件人地址 */
    @Excel(name = "发件人地址")
    private String fromAddress;

    /** 收件人地址 */
    @Excel(name = "收件人地址")
    private String toAddress;

    /** 抄送地址 */
    private String ccAddress;

    /** 密送地址 */
    private String bccAddress;

    /** 邮件主题 */
    @Excel(name = "邮件主题")
    private String subject;

    /** 邮件内容 */
    private String content;

    /** 附件列表(JSON格式) */
    private String attachments;

    /** 是否已读(0未读 1已读) */
    @Excel(name = "是否已读", readConverterExp = "0=未读,1=已读")
    private String isRead;

    /** 是否星标(0否 1是) */
    @Excel(name = "是否星标", readConverterExp = "0=否,1=是")
    private String isStarred;

    /** 是否重要(0否 1是) */
    @Excel(name = "是否重要", readConverterExp = "0=否,1=是")
    private String isImportant;

    /** 标签(逗号分隔) */
    private String tags;

    /** 发送时间 */
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String sendTime;

    /** 接收时间 */
    @Excel(name = "接收时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String receiveTime;

    /** 状态(0正常 1已删除) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=已删除")
    private String status;

    public void setEmailId(Long emailId) 
    {
        this.emailId = emailId;
    }

    public Long getEmailId() 
    {
        return emailId;
    }
    public void setEmailType(String emailType) 
    {
        this.emailType = emailType;
    }

    public String getEmailType() 
    {
        return emailType;
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
    public void setCcAddress(String ccAddress) 
    {
        this.ccAddress = ccAddress;
    }

    public String getCcAddress() 
    {
        return ccAddress;
    }
    public void setBccAddress(String bccAddress) 
    {
        this.bccAddress = bccAddress;
    }

    public String getBccAddress() 
    {
        return bccAddress;
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
    public void setAttachments(String attachments) 
    {
        this.attachments = attachments;
    }

    public String getAttachments() 
    {
        return attachments;
    }
    public void setIsRead(String isRead) 
    {
        this.isRead = isRead;
    }

    public String getIsRead() 
    {
        return isRead;
    }
    public void setIsStarred(String isStarred) 
    {
        this.isStarred = isStarred;
    }

    public String getIsStarred() 
    {
        return isStarred;
    }
    public void setIsImportant(String isImportant) 
    {
        this.isImportant = isImportant;
    }

    public String getIsImportant() 
    {
        return isImportant;
    }
    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }
    public void setSendTime(String sendTime) 
    {
        this.sendTime = sendTime;
    }

    public String getSendTime() 
    {
        return sendTime;
    }
    public void setReceiveTime(String receiveTime) 
    {
        this.receiveTime = receiveTime;
    }

    public String getReceiveTime() 
    {
        return receiveTime;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("emailId", getEmailId())
            .append("emailType", getEmailType())
            .append("fromAddress", getFromAddress())
            .append("toAddress", getToAddress())
            .append("ccAddress", getCcAddress())
            .append("bccAddress", getBccAddress())
            .append("subject", getSubject())
            .append("content", getContent())
            .append("attachments", getAttachments())
            .append("isRead", getIsRead())
            .append("isStarred", getIsStarred())
            .append("isImportant", getIsImportant())
            .append("tags", getTags())
            .append("sendTime", getSendTime())
            .append("receiveTime", getReceiveTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
