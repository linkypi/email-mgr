package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 联系人对象 email_contact
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

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 企业 */
    @Excel(name = "企业")
    private String company;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 年龄 */
    @Excel(name = "年龄")
    private Integer age;

    /** 性别 */
    @Excel(name = "性别", readConverterExp = "0=男,1=女")
    private String gender;

    /** 社交媒体账号 */
    @Excel(name = "社交媒体账号")
    private String socialMedia;

    /** 粉丝数 */
    @Excel(name = "粉丝数")
    private Integer followersCount;

    /** 群组ID */
    @Excel(name = "群组")
    private Long groupId;

    /** 等级 */
    @Excel(name = "等级")
    private String level;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 群组名称 */
    private String groupName;

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

    public void setFollowersCount(Integer followersCount) 
    {
        this.followersCount = followersCount;
    }

    public Integer getFollowersCount() 
    {
        return followersCount;
    }

    public void setGroupId(Long groupId) 
    {
        this.groupId = groupId;
    }

    public Long getGroupId() 
    {
        return groupId;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public String getGroupName() 
    {
        return groupName;
    }

    public void setGroupName(String groupName) 
    {
        this.groupName = groupName;
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
            .append("followersCount", getFollowersCount())
            .append("groupId", getGroupId())
            .append("level", getLevel())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("deleted", 0)
            .toString();
    }
}

