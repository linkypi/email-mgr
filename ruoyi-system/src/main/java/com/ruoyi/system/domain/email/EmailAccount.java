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
    @Excel(name = "邮箱密码", targetAttr = "password", type = Excel.Type.EXPORT)
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

    /** IMAP服务器 */
    @Excel(name = "IMAP服务器")
    private String imapHost;

    /** IMAP端口 */
    @Excel(name = "IMAP端口")
    private Integer imapPort;

    /** IMAP是否启用SSL(0否 1是) */
    @Excel(name = "IMAP是否启用SSL", readConverterExp = "0=否,1=是")
    private String imapSsl;

    /** IMAP用户名(通常与邮箱地址相同) */
    private String imapUsername;

    /** IMAP密码(加密，通常与SMTP密码相同) */
    @Excel(name = "IMAP密码", targetAttr = "imapPassword", type = Excel.Type.EXPORT)
    private String imapPassword;

    /** Webhook回调地址 */
    private String webhookUrl;

    /** Webhook密钥 */
    private String webhookSecret;

    /** 是否启用邮件跟踪(0否 1是) */
    @Excel(name = "是否启用邮件跟踪", readConverterExp = "0=否,1=是")
    private String trackingEnabled;

    /** 每日发送限制 */
    @Excel(name = "每日发送限制")
    private Integer dailyLimit;

    /** 今日已发送数量 */
    @Excel(name = "今日已发送数量")
    private Integer usedCount;

    /** 最后发送时间 */
    @Excel(name = "最后发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String lastSendTime;

    /** 最后同步时间 */
    @Excel(name = "最后同步时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String lastSyncTime;

    /** 状态(0启用 1禁用) */
    @Excel(name = "启用状态", readConverterExp = "0=启用,1=禁用")
    private String status;

    /** 备注 */
    private String remark;

    /** 批量操作时的账号ID数组 */
    private Long[] accountIds;

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
    public void setImapHost(String imapHost) 
    {
        this.imapHost = imapHost;
    }

    public String getImapHost() 
    {
        return imapHost;
    }
    public void setImapPort(Integer imapPort) 
    {
        this.imapPort = imapPort;
    }

    public Integer getImapPort() 
    {
        return imapPort;
    }
    public void setImapSsl(String imapSsl) 
    {
        this.imapSsl = imapSsl;
    }

    public String getImapSsl() 
    {
        return imapSsl;
    }
    public void setImapUsername(String imapUsername) 
    {
        this.imapUsername = imapUsername;
    }

    public String getImapUsername() 
    {
        return imapUsername;
    }
    public void setImapPassword(String imapPassword) 
    {
        this.imapPassword = imapPassword;
    }

    public String getImapPassword() 
    {
        return imapPassword;
    }
    public void setWebhookUrl(String webhookUrl) 
    {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookUrl() 
    {
        return webhookUrl;
    }
    public void setWebhookSecret(String webhookSecret) 
    {
        this.webhookSecret = webhookSecret;
    }

    public String getWebhookSecret() 
    {
        return webhookSecret;
    }
    public void setTrackingEnabled(String trackingEnabled) 
    {
        this.trackingEnabled = trackingEnabled;
    }

    public String getTrackingEnabled() 
    {
        return trackingEnabled;
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
    public void setLastSyncTime(String lastSyncTime) 
    {
        this.lastSyncTime = lastSyncTime;
    }

    public String getLastSyncTime() 
    {
        return lastSyncTime;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

        public String getStatus() 
    {
        return status;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setAccountIds(Long[] accountIds)
    {
        this.accountIds = accountIds;
    }

    public Long[] getAccountIds()
    {
        return accountIds;
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
            .append("imapHost", getImapHost())
            .append("imapPort", getImapPort())
            .append("imapSsl", getImapSsl())
            .append("imapUsername", getImapUsername())
            .append("imapPassword", getImapPassword())
            .append("webhookUrl", getWebhookUrl())
            .append("webhookSecret", getWebhookSecret())
            .append("trackingEnabled", getTrackingEnabled())
            .append("dailyLimit", getDailyLimit())
            .append("usedCount", getUsedCount())
            .append("lastSendTime", getLastSendTime())
            .append("lastSyncTime", getLastSyncTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

