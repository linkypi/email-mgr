package com.ruoyi.system.domain.email;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Long statId;

    /** 统计日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "统计日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date statDate;

    /** 统计类型(1日统计 2月统计 3年统计) */
    @Excel(name = "统计类型", readConverterExp = "1=日统计,2=月统计,3=年统计")
    private String statType;

    /** 总发送数 */
    @Excel(name = "总发送数")
    private Integer totalSent;

    /** 总送达数 */
    @Excel(name = "总送达数")
    private Integer totalDelivered;

    /** 总打开数 */
    @Excel(name = "总打开数")
    private Integer totalOpened;

    /** 总回复数 */
    @Excel(name = "总回复数")
    private Integer totalReplied;

    /** 送达率 */
    @Excel(name = "送达率")
    private BigDecimal deliveryRate;

    /** 打开率 */
    @Excel(name = "打开率")
    private BigDecimal openRate;

    /** 回复率 */
    @Excel(name = "回复率")
    private BigDecimal replyRate;

    /** 邮件Message-ID */
    private String messageId;

    /** 邮件状态 */
    private String status;

    /** 送达时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveredTime;

    /** 打开时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openedTime;

    /** 回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date repliedTime;

    /** 点击时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date clickedTime;

    /** 邮箱账号ID */
    private Long accountId;

    public void setStatId(Long statId) 
    {
        this.statId = statId;
    }

    public Long getStatId() 
    {
        return statId;
    }
    public void setStatDate(Date statDate) 
    {
        this.statDate = statDate;
    }

    public Date getStatDate() 
    {
        return statDate;
    }
    public void setStatType(String statType) 
    {
        this.statType = statType;
    }

    public String getStatType() 
    {
        return statType;
    }
    public void setTotalSent(Integer totalSent) 
    {
        this.totalSent = totalSent;
    }

    public Integer getTotalSent() 
    {
        return totalSent;
    }
    public void setTotalDelivered(Integer totalDelivered) 
    {
        this.totalDelivered = totalDelivered;
    }

    public Integer getTotalDelivered() 
    {
        return totalDelivered;
    }
    public void setTotalOpened(Integer totalOpened) 
    {
        this.totalOpened = totalOpened;
    }

    public Integer getTotalOpened() 
    {
        return totalOpened;
    }
    public void setTotalReplied(Integer totalReplied) 
    {
        this.totalReplied = totalReplied;
    }

    public Integer getTotalReplied() 
    {
        return totalReplied;
    }
    public void setDeliveryRate(BigDecimal deliveryRate) 
    {
        this.deliveryRate = deliveryRate;
    }

    public BigDecimal getDeliveryRate() 
    {
        return deliveryRate;
    }
    public void setOpenRate(BigDecimal openRate) 
    {
        this.openRate = openRate;
    }

    public BigDecimal getOpenRate() 
    {
        return openRate;
    }
    public void setReplyRate(BigDecimal replyRate) 
    {
        this.replyRate = replyRate;
    }

    public BigDecimal getReplyRate() 
    {
        return replyRate;
    }

    public String getMessageId() 
    {
        return messageId;
    }

    public void setMessageId(String messageId) 
    {
        this.messageId = messageId;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public Date getDeliveredTime() 
    {
        return deliveredTime;
    }

    public void setDeliveredTime(Date deliveredTime) 
    {
        this.deliveredTime = deliveredTime;
    }

    public Date getOpenedTime() 
    {
        return openedTime;
    }

    public void setOpenedTime(Date openedTime) 
    {
        this.openedTime = openedTime;
    }

    public Date getRepliedTime() 
    {
        return repliedTime;
    }

    public void setRepliedTime(Date repliedTime) 
    {
        this.repliedTime = repliedTime;
    }

    public Date getClickedTime() 
    {
        return clickedTime;
    }

    public void setClickedTime(Date clickedTime) 
    {
        this.clickedTime = clickedTime;
    }

    public Long getAccountId() 
    {
        return accountId;
    }

    public void setAccountId(Long accountId) 
    {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("statId", getStatId())
            .append("accountId", getAccountId())
            .append("statDate", getStatDate())
            .append("statType", getStatType())
            .append("totalSent", getTotalSent())
            .append("totalDelivered", getTotalDelivered())
            .append("totalOpened", getTotalOpened())
            .append("totalReplied", getTotalReplied())
            .append("deliveryRate", getDeliveryRate())
            .append("openRate", getOpenRate())
            .append("replyRate", getReplyRate())
            .append("messageId", getMessageId())
            .append("status", getStatus())
            .append("deliveredTime", getDeliveredTime())
            .append("openedTime", getOpenedTime())
            .append("repliedTime", getRepliedTime())
            .append("clickedTime", getClickedTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
