package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 邮件模板对象 email_template
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long templateId;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String templateName;

    /** 模板类型(1普通 2营销 3通知) */
    @Excel(name = "模板类型", readConverterExp = "1=普通,2=营销,3=通知")
    private String templateType;

    /** 邮件主题 */
    @Excel(name = "邮件主题")
    private String subject;

    /** 邮件内容 */
    private String content;

    /** 模板变量 */
    private String variables;

    /** 使用次数 */
    @Excel(name = "使用次数")
    private Integer useCount;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }
    public void setTemplateName(String templateName) 
    {
        this.templateName = templateName;
    }

    public String getTemplateName() 
    {
        return templateName;
    }
    public void setTemplateType(String templateType) 
    {
        this.templateType = templateType;
    }

    public String getTemplateType() 
    {
        return templateType;
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
    public void setVariables(String variables) 
    {
        this.variables = variables;
    }

    public String getVariables() 
    {
        return variables;
    }
    public void setUseCount(Integer useCount) 
    {
        this.useCount = useCount;
    }

    public Integer getUseCount() 
    {
        return useCount;
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
            .append("templateId", getTemplateId())
            .append("templateName", getTemplateName())
            .append("templateType", getTemplateType())
            .append("subject", getSubject())
            .append("content", getContent())
            .append("variables", getVariables())
            .append("useCount", getUseCount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}



