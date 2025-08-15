package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 联系人群组对象 email_contact_group
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailContactGroup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 群组ID */
    private Long groupId;

    /** 群组名称 */
    @Excel(name = "群组名称")
    private String groupName;

    /** 群组描述 */
    @Excel(name = "群组描述")
    private String description;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 联系人数量 */
    private Integer contactCount;

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

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public Integer getContactCount() 
    {
        return contactCount;
    }

    public void setContactCount(Integer contactCount) 
    {
        this.contactCount = contactCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("groupId", getGroupId())
            .append("groupName", getGroupName())
            .append("description", getDescription())
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
