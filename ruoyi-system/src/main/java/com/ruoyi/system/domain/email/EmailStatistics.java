package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 邮件统计对象 email_statistics
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailStatistics extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 统计ID */
    private Long statisticsId;

    /** 统计日期 */
    @Excel(name = "统计日期", width = 30, dateFormat = "yyyy-MM-dd")
    private String statisticsDate;

    /** 邮箱账号ID */
    @Excel(name = "邮箱账号ID")
    private Long accountId;

    /** 邮箱账号名称 */
    @Excel(name = "邮箱账号名称")
    private String accountName;

    /** 发送数量 */
    @Excel(name = "发送数量")
    private Integer totalSent;

    /** 送达数量 */
    @Excel(name = "送达数量")
    private Integer delivered;

    /** 打开数量 */
    @Excel(name = "打开数量")
    private Integer opened;

    /** 回复数量 */
    @Excel(name = "回复数量")
    private Integer replied;

    /** 送达率 */
    @Excel(name = "送达率")
    private Double deliveryRate;

    /** 打开率 */
    @Excel(name = "打开率")
    private Double openRate;

    /** 回复率 */
    @Excel(name = "回复率")
    private Double replyRate;

    /** 统计类型(1日报 2周报 3月报 4年报) */
    @Excel(name = "统计类型", readConverterExp = "1=日报,2=周报,3=月报,4=年报")
    private String statisticsType;

    public void setStatisticsId(Long statisticsId) 
    {
        this.statisticsId = statisticsId;
    }

    public Long getStatisticsId() 
    {
        return statisticsId;
    }
    public void setStatisticsDate(String statisticsDate) 
    {
        this.statisticsDate = statisticsDate;
    }

    public String getStatisticsDate() 
    {
        return statisticsDate;
    }
    public void setAccountId(Long accountId) 
    {
        this.accountId = accountId;
    }

    public Long getAccountId() 
    {
        return accountId;
    }
    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }
    public void setTotalSent(Integer totalSent) 
    {
        this.totalSent = totalSent;
    }

    public Integer getTotalSent() 
    {
        return totalSent;
    }
    public void setDelivered(Integer delivered) 
    {
        this.delivered = delivered;
    }

    public Integer getDelivered() 
    {
        return delivered;
    }
    public void setOpened(Integer opened) 
    {
        this.opened = opened;
    }

    public Integer getOpened() 
    {
        return opened;
    }
    public void setReplied(Integer replied) 
    {
        this.replied = replied;
    }

    public Integer getReplied() 
    {
        return replied;
    }
    public void setDeliveryRate(Double deliveryRate) 
    {
        this.deliveryRate = deliveryRate;
    }

    public Double getDeliveryRate() 
    {
        return deliveryRate;
    }
    public void setOpenRate(Double openRate) 
    {
        this.openRate = openRate;
    }

    public Double getOpenRate() 
    {
        return openRate;
    }
    public void setReplyRate(Double replyRate) 
    {
        this.replyRate = replyRate;
    }

    public Double getReplyRate() 
    {
        return replyRate;
    }
    public void setStatisticsType(String statisticsType) 
    {
        this.statisticsType = statisticsType;
    }

    public String getStatisticsType() 
    {
        return statisticsType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("statisticsId", getStatisticsId())
            .append("statisticsDate", getStatisticsDate())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("totalSent", getTotalSent())
            .append("delivered", getDelivered())
            .append("opened", getOpened())
            .append("replied", getReplied())
            .append("deliveryRate", getDeliveryRate())
            .append("openRate", getOpenRate())
            .append("replyRate", getReplyRate())
            .append("statisticsType", getStatisticsType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
