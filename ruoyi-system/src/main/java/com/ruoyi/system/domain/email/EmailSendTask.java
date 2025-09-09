package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 邮件发送任务对象 email_send_task
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailSendTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 模板ID */
    @Excel(name = "模板ID")
    private Long templateId;

    /** 发件人账号ID */
    @Excel(name = "发件人账号ID")
    private Long accountId;

    /** 发件人ID */
    @Excel(name = "发件人ID")
    private Long senderId;

    /** 邮件主题 */
    @Excel(name = "邮件主题")
    private String subject;

    /** 邮件内容 */
    @Excel(name = "邮件内容")
    private String content;

    /** 收件人类型(all全部 group群组 tag标签 manual手动) */
    @Excel(name = "收件人类型", readConverterExp = "all=全部,group=群组,tag=标签,manual=手动")
    private String recipientType;

    /** 收件人ID列表(JSON格式) */
    @Excel(name = "收件人ID列表")
    private String recipientIds;

    /** 群组ID列表(逗号分隔) */
    @Excel(name = "群组ID列表")
    private String groupIds;

    /** 标签ID列表(逗号分隔) */
    @Excel(name = "标签ID列表")
    private String tagIds;

    /** 联系人ID列表(逗号分隔) */
    @Excel(name = "联系人ID列表")
    private String contactIds;

    /** 发件账号ID列表(JSON格式) */
    @Excel(name = "发件账号ID列表")
    private String accountIds;

    /** 发送间隔(秒) */
    @Excel(name = "发送间隔(秒)")
    private Integer sendInterval;

    /** 开始发送时间 */
    @Excel(name = "开始发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束发送时间 */
    @Excel(name = "结束发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 总发送数量 */
    @Excel(name = "总发送数量")
    private Integer totalCount;

    /** 已发送数量 */
    @Excel(name = "已发送数量")
    private Integer sentCount;

    /** 送达数量 */
    @Excel(name = "送达数量")
    private Integer deliveredCount;

    /** 打开数量 */
    @Excel(name = "打开数量")
    private Integer openedCount;

    /** 回复数量 */
    @Excel(name = "回复数量")
    private Integer repliedCount;

    /** 发送模式(immediate立即发送 scheduled定时发送) */
    @Excel(name = "发送模式", readConverterExp = "immediate=立即发送,scheduled=定时发送")
    private String sendMode;

    /** 状态(0待发送 1发送中 2已完成 3已暂停 4已取消) */
    @Excel(name = "状态", readConverterExp = "0=待发送,1=发送中,2=已完成,3=已暂停,4=已取消")
    private String status;

    /** 模板变量(JSON格式) */
    @Excel(name = "模板变量")
    private String templateVariables;

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setAccountId(Long accountId) 
    {
        this.accountId = accountId;
    }

    public Long getAccountId() 
    {
        return accountId;
    }

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
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

    public void setRecipientType(String recipientType) 
    {
        this.recipientType = recipientType;
    }

    public String getRecipientType() 
    {
        return recipientType;
    }

    public void setRecipientIds(String recipientIds) 
    {
        this.recipientIds = recipientIds;
    }

    public String getRecipientIds() 
    {
        return recipientIds;
    }

    public void setGroupIds(String groupIds) 
    {
        this.groupIds = groupIds;
    }

    public String getGroupIds() 
    {
        return groupIds;
    }

    public void setTagIds(String tagIds) 
    {
        this.tagIds = tagIds;
    }

    public String getTagIds() 
    {
        return tagIds;
    }

    public void setContactIds(String contactIds) 
    {
        this.contactIds = contactIds;
    }

    public String getContactIds() 
    {
        return contactIds;
    }

    public void setAccountIds(String accountIds) 
    {
        this.accountIds = accountIds;
    }

    public String getAccountIds() 
    {
        return accountIds;
    }

    public void setSendInterval(Integer sendInterval) 
    {
        this.sendInterval = sendInterval;
    }

    public Integer getSendInterval() 
    {
        return sendInterval;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setTotalCount(Integer totalCount) 
    {
        this.totalCount = totalCount;
    }

    public Integer getTotalCount() 
    {
        return totalCount;
    }

    public void setSentCount(Integer sentCount) 
    {
        this.sentCount = sentCount;
    }

    public Integer getSentCount() 
    {
        return sentCount;
    }

    public void setDeliveredCount(Integer deliveredCount) 
    {
        this.deliveredCount = deliveredCount;
    }

    public Integer getDeliveredCount() 
    {
        return deliveredCount;
    }

    public void setOpenedCount(Integer openedCount) 
    {
        this.openedCount = openedCount;
    }

    public Integer getOpenedCount() 
    {
        return openedCount;
    }

    public void setRepliedCount(Integer repliedCount) 
    {
        this.repliedCount = repliedCount;
    }

    public Integer getRepliedCount() 
    {
        return repliedCount;
    }

    public void setSendMode(String sendMode) 
    {
        this.sendMode = sendMode;
    }

    public String getSendMode() 
    {
        return sendMode;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setTemplateVariables(String templateVariables) 
    {
        this.templateVariables = templateVariables;
    }

    public String getTemplateVariables() 
    {
        return templateVariables;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("taskName", getTaskName())
            .append("templateId", getTemplateId())
            .append("accountId", getAccountId())
            .append("senderId", getSenderId())
            .append("subject", getSubject())
            .append("content", getContent())
            .append("recipientType", getRecipientType())
            .append("recipientIds", getRecipientIds())
            .append("groupIds", getGroupIds())
            .append("tagIds", getTagIds())
            .append("contactIds", getContactIds())
            .append("accountIds", getAccountIds())
            .append("sendInterval", getSendInterval())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("totalCount", getTotalCount())
            .append("sentCount", getSentCount())
            .append("deliveredCount", getDeliveredCount())
            .append("openedCount", getOpenedCount())
            .append("repliedCount", getRepliedCount())
            .append("sendMode", getSendMode())
            .append("status", getStatus())
            .append("templateVariables", getTemplateVariables())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
