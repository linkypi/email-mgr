package com.ruoyi.system.domain.email;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 联系人销售数据对象 email_sales_data
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
public class EmailContactSalesData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 销售数据ID */
    private Long salesId;

    /** 联系人ID列表 */
    @Excel(name = "联系人ID列表")
    private String contactIds;

    /** 联系人姓名列表 */
    @Excel(name = "联系人姓名列表")
    private String contactNames;

    /** 联系人邮箱列表 */
    @Excel(name = "联系人邮箱列表")
    private String contactEmails;

    /** 联系人企业列表 */
    @Excel(name = "联系人企业列表")
    private String contactCompanies;

    /** 状态(已寄样、已发布、未发布、确定不发) */
    @Excel(name = "状态", readConverterExp = "已寄样=已寄样,已发布=已发布,未发布=未发布,确定不发=确定不发")
    private String status;

    /** 带货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "带货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date salesDate;

    /** 带货型号 */
    @Excel(name = "带货型号")
    private String productModel;

    /** 带货单量 */
    @Excel(name = "带货单量")
    private Long salesQuantity;

    /** 播放次数 */
    @Excel(name = "播放次数")
    private Long playCount;

    /** 转化率(%) */
    @Excel(name = "转化率(%)")
    private BigDecimal conversionRate;

    /** 折扣类型 */
    @Excel(name = "折扣类型")
    private String discountType;

    /** 折扣比例(%) */
    @Excel(name = "折扣比例(%)")
    private BigDecimal discountRate;

    /** 来源渠道 */
    @Excel(name = "来源渠道")
    private String sourceChannel;

    /** 删除标志(0代表存在 2代表删除) */
    private String deleted;

    public void setSalesId(Long salesId) 
    {
        this.salesId = salesId;
    }

    public Long getSalesId() 
    {
        return salesId;
    }
    public void setContactIds(String contactIds) 
    {
        this.contactIds = contactIds;
    }

    public String getContactIds() 
    {
        return contactIds;
    }
    public void setContactNames(String contactNames) 
    {
        this.contactNames = contactNames;
    }

    public String getContactNames() 
    {
        return contactNames;
    }
    public void setContactEmails(String contactEmails) 
    {
        this.contactEmails = contactEmails;
    }

    public String getContactEmails() 
    {
        return contactEmails;
    }
    public void setContactCompanies(String contactCompanies) 
    {
        this.contactCompanies = contactCompanies;
    }

    public String getContactCompanies() 
    {
        return contactCompanies;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setSalesDate(Date salesDate) 
    {
        this.salesDate = salesDate;
    }

    public Date getSalesDate() 
    {
        return salesDate;
    }
    public void setProductModel(String productModel) 
    {
        this.productModel = productModel;
    }

    public String getProductModel() 
    {
        return productModel;
    }
    public void setSalesQuantity(Long salesQuantity) 
    {
        this.salesQuantity = salesQuantity;
    }

    public Long getSalesQuantity() 
    {
        return salesQuantity;
    }
    public void setPlayCount(Long playCount) 
    {
        this.playCount = playCount;
    }

    public Long getPlayCount() 
    {
        return playCount;
    }
    public void setConversionRate(BigDecimal conversionRate) 
    {
        this.conversionRate = conversionRate;
    }

    public BigDecimal getConversionRate() 
    {
        return conversionRate;
    }
    public void setDiscountType(String discountType) 
    {
        this.discountType = discountType;
    }

    public String getDiscountType() 
    {
        return discountType;
    }
    public void setDiscountRate(BigDecimal discountRate) 
    {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountRate() 
    {
        return discountRate;
    }
    public void setSourceChannel(String sourceChannel) 
    {
        this.sourceChannel = sourceChannel;
    }

    public String getSourceChannel() 
    {
        return sourceChannel;
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
            .append("salesId", getSalesId())
            .append("contactIds", getContactIds())
            .append("contactNames", getContactNames())
            .append("contactEmails", getContactEmails())
            .append("contactCompanies", getContactCompanies())
            .append("status", getStatus())
            .append("salesDate", getSalesDate())
            .append("productModel", getProductModel())
            .append("salesQuantity", getSalesQuantity())
            .append("playCount", getPlayCount())
            .append("conversionRate", getConversionRate())
            .append("discountType", getDiscountType())
            .append("discountRate", getDiscountRate())
            .append("sourceChannel", getSourceChannel())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("deleted", getDeleted())
            .toString();
    }
}
