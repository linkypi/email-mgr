package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 发送回复标签对象 email_send_reply_tag
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailSendReplyTag extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    private Long tagId;

    /** 联系人ID */
    @Excel(name = "联系人ID")
    private Long contactId;

    /** 标签名称 */
    @Excel(name = "标签名称")
    private String tagName;

    /** 标签类型 */
    @Excel(name = "标签类型")
    private String tagType;

    /** 发送邮件数量 */
    @Excel(name = "发送邮件数量")
    private Integer sendCount;

    /** 回复邮件数量 */
    @Excel(name = "回复邮件数量")
    private Integer replyCount;

    /** 打开邮件数量 */
    @Excel(name = "打开邮件数量")
    private Integer openCount;

    /** 最后发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastSendTime;

    /** 最后回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后回复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastReplyTime;

    /** 联系人姓名 */
    private String contactName;

    /** 联系人邮箱 */
    private String contactEmail;

    public void setTagId(Long tagId) 
    {
        this.tagId = tagId;
    }

    public Long getTagId() 
    {
        return tagId;
    }

    public void setContactId(Long contactId) 
    {
        this.contactId = contactId;
    }

    public Long getContactId() 
    {
        return contactId;
    }

    public void setTagName(String tagName) 
    {
        this.tagName = tagName;
    }

    public String getTagName() 
    {
        return tagName;
    }

    public void setTagType(String tagType) 
    {
        this.tagType = tagType;
    }

    public String getTagType() 
    {
        return tagType;
    }

    public void setSendCount(Integer sendCount) 
    {
        this.sendCount = sendCount;
    }

    public Integer getSendCount() 
    {
        return sendCount;
    }

    public void setReplyCount(Integer replyCount) 
    {
        this.replyCount = replyCount;
    }

    public Integer getReplyCount() 
    {
        return replyCount;
    }

    public void setOpenCount(Integer openCount) 
    {
        this.openCount = openCount;
    }

    public Integer getOpenCount() 
    {
        return openCount;
    }

    public void setLastSendTime(Date lastSendTime) 
    {
        this.lastSendTime = lastSendTime;
    }

    public Date getLastSendTime() 
    {
        return lastSendTime;
    }

    public void setLastReplyTime(Date lastReplyTime) 
    {
        this.lastReplyTime = lastReplyTime;
    }

    public Date getLastReplyTime() 
    {
        return lastReplyTime;
    }

    public String getContactName() 
    {
        return contactName;
    }

    public void setContactName(String contactName) 
    {
        this.contactName = contactName;
    }

    public String getContactEmail() 
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) 
    {
        this.contactEmail = contactEmail;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("tagId", getTagId())
            .append("contactId", getContactId())
            .append("tagName", getTagName())
            .append("tagType", getTagType())
            .append("sendCount", getSendCount())
            .append("replyCount", getReplyCount())
            .append("openCount", getOpenCount())
            .append("lastSendTime", getLastSendTime())
            .append("lastReplyTime", getLastReplyTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("deleted", 0)
            .toString();
    }
}
