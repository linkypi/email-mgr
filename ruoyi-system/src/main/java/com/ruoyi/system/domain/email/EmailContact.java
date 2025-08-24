package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 邮件联系人对象 email_contact
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailContact extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 联系人ID */
    private Long contactId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 邮箱地址 */
    @Excel(name = "邮箱地址")
    private String email;

    /** 企业名称 */
    @Excel(name = "企业名称")
    private String company;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 年龄 */
    @Excel(name = "年龄")
    private Integer age;

    /** 性别 */
    @Excel(name = "性别", readConverterExp = "0=未知,1=男,2=女")
    private String gender;

    /** 社交媒体 */
    @Excel(name = "社交媒体")
    private String socialMedia;

    /** 粉丝数 */
    @Excel(name = "粉丝数")
    private Integer followers;

    /** 等级 */
    @Excel(name = "等级", readConverterExp = "1=重要,2=普通,3=一般")
    private String level;

    /** 群组ID */
    private Long groupId;

    /** 群组名称 */
    @Excel(name = "群组名称")
    private String groupName;

    /** 标签 */
    @Excel(name = "标签")
    private String tags;

    /** 发送次数 */
    @Excel(name = "发送次数")
    private Integer sendCount;

    /** 回复次数 */
    @Excel(name = "回复次数")
    private Integer replyCount;

    /** 打开次数 */
    @Excel(name = "打开次数")
    private Integer openCount;

    /** 回复率 */
    @Excel(name = "回复率")
    private Double replyRate;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String deleted;

    public void setContactId(Long contactId) 
    {
        this.contactId = contactId;
    }

    public Long getContactId() 
    {
        return contactId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setCompany(String company) 
    {
        this.company = company;
    }

    public String getCompany() 
    {
        return company;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setAge(Integer age) 
    {
        this.age = age;
    }

    public Integer getAge() 
    {
        return age;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }

    public void setSocialMedia(String socialMedia) 
    {
        this.socialMedia = socialMedia;
    }

    public String getSocialMedia() 
    {
        return socialMedia;
    }

    public void setFollowers(Integer followers) 
    {
        this.followers = followers;
    }

    public Integer getFollowers() 
    {
        return followers;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setGroupId(Long groupId) 
    {
        this.groupId = groupId;
    }

    public Long getGroupId() 
    {
        return groupId;
    }

    public void setGroupName(String groupName) 
    {
        this.groupName = groupName;
    }

    public String getGroupName() 
    {
        return groupName;
    }

    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("contactId", getContactId())
            .append("name", getName())
            .append("email", getEmail())
            .append("company", getCompany())
            .append("address", getAddress())
            .append("age", getAge())
            .append("gender", getGender())
            .append("socialMedia", getSocialMedia())
            .append("followers", getFollowers())
            .append("level", getLevel())
            .append("groupId", getGroupId())
            .append("groupName", getGroupName())
            .append("tags", getTags())
            .append("sendCount", getSendCount())
            .append("replyCount", getReplyCount())
            .append("openCount", getOpenCount())
            .append("replyRate", getReplyRate())
            .append("status", getStatus())
            .append("deleted", getDeleted())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

