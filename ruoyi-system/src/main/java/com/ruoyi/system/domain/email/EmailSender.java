package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.List;

/**
 * 发件人信息对象 email_sender
 * 
 * @author ruoyi
 * @date 2025-01-01
 */
public class EmailSender extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 发件人ID */
    private Long senderId;

    /** 发件人姓名 */
    @Excel(name = "发件人姓名")
    private String senderName;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String company;

    /** 部门 */
    @Excel(name = "部门")
    private String department;

    /** 职位 */
    @Excel(name = "职位")
    private String position;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 发件人描述 */
    @Excel(name = "发件人描述")
    private String description;

    /** 等级(1重要 2普通 3一般) */
    @Excel(name = "等级", readConverterExp = "1=重要,2=普通,3=一般")
    private String level;

    /** 标签(逗号分隔) */
    @Excel(name = "标签")
    private String tags;

    /** 关联邮箱账号总数 */
    @Excel(name = "关联邮箱账号总数")
    private Integer totalAccounts;

    /** 活跃邮箱账号数 */
    @Excel(name = "活跃邮箱账号数")
    private Integer activeAccounts;

    /** 总发送邮件数 */
    @Excel(name = "总发送邮件数")
    private Integer totalSent;

    /** 总回复邮件数 */
    @Excel(name = "总回复邮件数")
    private Integer totalReplied;

    /** 回复率 */
    @Excel(name = "回复率")
    private Double replyRate;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String deleted;

    /** 关联的邮箱账号列表 */
    private List<EmailAccount> emailAccounts;

    /** 当天剩余发送数量（用于选项显示） */
    private Integer dailyRemainingCount;

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
    }
    public void setSenderName(String senderName) 
    {
        this.senderName = senderName;
    }

    public String getSenderName() 
    {
        return senderName;
    }
    public void setCompany(String company) 
    {
        this.company = company;
    }

    public String getCompany() 
    {
        return company;
    }
    public void setDepartment(String department) 
    {
        this.department = department;
    }

    public String getDepartment() 
    {
        return department;
    }
    public void setPosition(String position) 
    {
        this.position = position;
    }

    public String getPosition() 
    {
        return position;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }
    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }
    public void setTotalAccounts(Integer totalAccounts) 
    {
        this.totalAccounts = totalAccounts;
    }

    public Integer getTotalAccounts() 
    {
        return totalAccounts;
    }
    public void setActiveAccounts(Integer activeAccounts) 
    {
        this.activeAccounts = activeAccounts;
    }

    public Integer getActiveAccounts() 
    {
        return activeAccounts;
    }
    public void setTotalSent(Integer totalSent) 
    {
        this.totalSent = totalSent;
    }

    public Integer getTotalSent() 
    {
        return totalSent;
    }
    public void setTotalReplied(Integer totalReplied) 
    {
        this.totalReplied = totalReplied;
    }

    public Integer getTotalReplied() 
    {
        return totalReplied;
    }
    public void setReplyRate(Double replyRate) 
    {
        this.replyRate = replyRate;
    }

    public Double getReplyRate() 
    {
        return replyRate;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDeleted(String deleted) 
    {
        this.deleted = deleted;
    }

    public String getDeleted() 
    {
        return deleted;
    }

    public List<EmailAccount> getEmailAccounts() 
    {
        return emailAccounts;
    }

    public void setEmailAccounts(List<EmailAccount> emailAccounts) 
    {
        this.emailAccounts = emailAccounts;
    }

    public Integer getDailyRemainingCount() 
    {
        return dailyRemainingCount;
    }

    public void setDailyRemainingCount(Integer dailyRemainingCount) 
    {
        this.dailyRemainingCount = dailyRemainingCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("senderId", getSenderId())
            .append("senderName", getSenderName())
            .append("company", getCompany())
            .append("department", getDepartment())
            .append("position", getPosition())
            .append("phone", getPhone())
            .append("address", getAddress())
            .append("description", getDescription())
            .append("level", getLevel())
            .append("tags", getTags())
            .append("totalAccounts", getTotalAccounts())
            .append("activeAccounts", getActiveAccounts())
            .append("totalSent", getTotalSent())
            .append("totalReplied", getTotalReplied())
            .append("replyRate", getReplyRate())
            .append("status", getStatus())
            .append("deleted", getDeleted())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

