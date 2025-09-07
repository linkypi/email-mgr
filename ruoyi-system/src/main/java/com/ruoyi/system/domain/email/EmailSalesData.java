package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

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

    public void setSalesId(Long salesId) 
    {
        this.salesId = salesId;
    }

    public Long getSalesId() 
    {
        return salesId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("salesId", getSalesId())
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
            .toString();
    }
}