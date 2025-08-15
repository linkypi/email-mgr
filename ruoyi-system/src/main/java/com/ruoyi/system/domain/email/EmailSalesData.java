package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售数据对象 email_sales_data
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailSalesData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 销售数据ID */
    private Long salesId;

    /** 联系人ID */
    @Excel(name = "联系人ID")
    private Long contactId;

    /** 销售金额 */
    @Excel(name = "销售金额")
    private BigDecimal salesAmount;

    /** 销售日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "销售日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date salesDate;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 销售状态 */
    @Excel(name = "销售状态")
    private String salesStatus;

    /** 联系人姓名 */
    private String contactName;

    /** 联系人邮箱 */
    private String contactEmail;

    public void setSalesId(Long salesId) 
    {
        this.salesId = salesId;
    }

    public Long getSalesId() 
    {
        return salesId;
    }

    public void setContactId(Long contactId) 
    {
        this.contactId = contactId;
    }

    public Long getContactId() 
    {
        return contactId;
    }

    public void setSalesAmount(BigDecimal salesAmount) 
    {
        this.salesAmount = salesAmount;
    }

    public BigDecimal getSalesAmount() 
    {
        return salesAmount;
    }

    public void setSalesDate(Date salesDate) 
    {
        this.salesDate = salesDate;
    }

    public Date getSalesDate() 
    {
        return salesDate;
    }

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setSalesStatus(String salesStatus) 
    {
        this.salesStatus = salesStatus;
    }

    public String getSalesStatus() 
    {
        return salesStatus;
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
            .append("salesId", getSalesId())
            .append("contactId", getContactId())
            .append("salesAmount", getSalesAmount())
            .append("salesDate", getSalesDate())
            .append("productName", getProductName())
            .append("salesStatus", getSalesStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("deleted", 0)
            .toString();
    }
}
