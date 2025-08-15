package com.ruoyi.system.domain.email;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 邮箱账号对象 email_account
 * 
 * @author ruoyi
 * @date 2023-01-01
 */
public class EmailAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 账号ID */
    private Long accountId;

    /** 账号名称 */
    @Excel(name = "账号名称")
    private String accountName;

    /** 邮箱地址 */
    @Excel(name = "邮箱地址")
    private String emailAddress;

    /** 邮箱密码(加密) */
    private String password;

    /** SMTP服务器 */
    @Excel(name = "SMTP服务器")
    private String smtpHost;

    /** SMTP端口 */
    @Excel(name = "SMTP端口")
    private Integer smtpPort;

    /** 是否启用SSL(0否 1是) */
    @Excel(name = "是否启用SSL", readConverterExp = "0=否,1=是")
    private String smtpSsl;

    /** 每日发送限制 */
    @Excel(name = "每日发送限制")
    private Integer dailyLimit;

    /** 今日已发送数量 */
    @Excel(name = "今日已发送数量")
    private Integer usedCount;

    /** 最后发送时间 */
    @Excel(name = "最后发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String lastSendTime;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

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
    public void setEmailAddress(String emailAddress) 
    {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() 
    {
        return emailAddress;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setSmtpHost(String smtpHost) 
    {
        this.smtpHost = smtpHost;
    }

    public String getSmtpHost() 
    {
        return smtpHost;
    }
    public void setSmtpPort(Integer smtpPort) 
    {
        this.smtpPort = smtpPort;
    }

    public Integer getSmtpPort() 
    {
        return smtpPort;
    }
    public void setSmtpSsl(String smtpSsl) 
    {
        this.smtpSsl = smtpSsl;
    }

    public String getSmtpSsl() 
    {
        return smtpSsl;
    }
    public void setDailyLimit(Integer dailyLimit) 
    {
        this.dailyLimit = dailyLimit;
    }

    public Integer getDailyLimit() 
    {
        return dailyLimit;
    }
    public void setUsedCount(Integer usedCount) 
    {
        this.usedCount = usedCount;
    }

    public Integer getUsedCount() 
    {
        return usedCount;
    }
    public void setLastSendTime(String lastSendTime) 
    {
        this.lastSendTime = lastSendTime;
    }

    public String getLastSendTime() 
    {
        return lastSendTime;
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
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("emailAddress", getEmailAddress())
            .append("password", getPassword())
            .append("smtpHost", getSmtpHost())
            .append("smtpPort", getSmtpPort())
            .append("smtpSsl", getSmtpSsl())
            .append("dailyLimit", getDailyLimit())
            .append("usedCount", getUsedCount())
            .append("lastSendTime", getLastSendTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

