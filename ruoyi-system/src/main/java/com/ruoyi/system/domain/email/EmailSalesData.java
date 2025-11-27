package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 邮件销售数据对象 email_sales_data
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailSalesData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 销售数据ID */
    private Long salesId;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 用户邮箱 */
    @Excel(name = "用户邮箱")
    private String userEmail;

    /** 用户姓名 */
    @Excel(name = "用户姓名")
    private String userName;

    /** 带货日期 */
    @Excel(name = "带货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date salesDate;

    /** 带货型号 */
    @Excel(name = "带货型号")
    private String productModel;

    /** 带货单量 */
    @Excel(name = "带货单量")
    private Integer salesQuantity;

    /** 播放次数 */
    @Excel(name = "播放次数")
    private Long playCount;

    /** 转化率 */
    @Excel(name = "转化率")
    private BigDecimal conversionRate;

    /** 折扣类型 */
    @Excel(name = "折扣类型", readConverterExp = "满减=满减,折扣=折扣,优惠券=优惠券,无折扣=无折扣")
    private String discountType;

    /** 折扣比例 */
    @Excel(name = "折扣比例")
    private BigDecimal discountRatio;

    /** 来源渠道 */
    @Excel(name = "来源渠道")
    private String sourceChannel;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 删除标志（0代表存在 2代表删除） */
    private String deleted;

    /** 联系人姓名列表（用于前端显示，不映射到数据库） */
    private List<String> contactNames;

    /** 联系人邮箱列表（用于前端显示，不映射到数据库） */
    private List<String> contactEmails;

    /** 联系人ID（用于查询，不映射到数据库） */
    private Long contactId;

    /** 联系人ID列表（用于插入/更新关联关系，不映射到数据库） */
    private List<Long> contactIds;

    public void setSalesId(Long salesId) 
    {
        this.salesId = salesId;
    }

    public Long getSalesId() 
    {
        return salesId;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setUserEmail(String userEmail) 
    {
        this.userEmail = userEmail;
    }

    public String getUserEmail() 
    {
        return userEmail;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
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
    public void setSalesQuantity(Integer salesQuantity) 
    {
        this.salesQuantity = salesQuantity;
    }

    public Integer getSalesQuantity() 
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
    public void setDiscountRatio(BigDecimal discountRatio) 
    {
        this.discountRatio = discountRatio;
    }

    public BigDecimal getDiscountRatio() 
    {
        return discountRatio;
    }
    public void setSourceChannel(String sourceChannel) 
    {
        this.sourceChannel = sourceChannel;
    }

    public String getSourceChannel() 
    {
        return sourceChannel;
    }
    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }
    public void setDeleted(String deleted) 
    {
        this.deleted = deleted;
    }

    public String getDeleted() 
    {
        return deleted;
    }
    public void setContactNames(List<String> contactNames) 
    {
        this.contactNames = contactNames;
    }

    public List<String> getContactNames() 
    {
        return contactNames;
    }
    public void setContactEmails(List<String> contactEmails) 
    {
        this.contactEmails = contactEmails;
    }

    public List<String> getContactEmails() 
    {
        return contactEmails;
    }
    public void setContactId(Long contactId) 
    {
        this.contactId = contactId;
    }

    public Long getContactId() 
    {
        return contactId;
    }
    public void setContactIds(List<Long> contactIds) 
    {
        this.contactIds = contactIds;
    }

    public List<Long> getContactIds() 
    {
        return contactIds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("salesId", getSalesId())
            .append("status", getStatus())
            .append("userEmail", getUserEmail())
            .append("userName", getUserName())
            .append("salesDate", getSalesDate())
            .append("productModel", getProductModel())
            .append("salesQuantity", getSalesQuantity())
            .append("playCount", getPlayCount())
            .append("conversionRate", getConversionRate())
            .append("discountType", getDiscountType())
            .append("discountRatio", getDiscountRatio())
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