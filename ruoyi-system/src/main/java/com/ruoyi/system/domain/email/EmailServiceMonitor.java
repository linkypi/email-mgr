package com.ruoyi.system.domain.email;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 邮件服务监控对象 email_service_monitor
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailServiceMonitor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 邮箱账号ID */
    @Excel(name = "邮箱账号ID")
    private Long accountId;

    /** 邮箱地址 */
    @Excel(name = "邮箱地址")
    private String emailAddress;

    /** IMAP服务状态：running/stopped/error */
    @Excel(name = "IMAP服务状态", readConverterExp = "running=运行中,stopped=已停止,error=异常")
    private String imapStatus;

    /** SMTP服务状态：running/stopped/error */
    @Excel(name = "SMTP服务状态", readConverterExp = "running=运行中,stopped=已停止,error=异常")
    private String smtpStatus;

    /** IMAP最后检查时间 */
    @Excel(name = "IMAP最后检查时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date imapLastCheckTime;

    /** SMTP最后检查时间 */
    @Excel(name = "SMTP最后检查时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date smtpLastCheckTime;

    /** IMAP错误信息 */
    @Excel(name = "IMAP错误信息")
    private String imapErrorMessage;

    /** SMTP错误信息 */
    @Excel(name = "SMTP错误信息")
    private String smtpErrorMessage;

    /** IMAP错误时间 */
    @Excel(name = "IMAP错误时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date imapErrorTime;

    /** SMTP错误时间 */
    @Excel(name = "SMTP错误时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date smtpErrorTime;

    /** 是否启用监控：0-禁用，1-启用 */
    @Excel(name = "监控状态", readConverterExp = "0=禁用,1=启用")
    private Integer monitorEnabled;

    /** 监控状态：0-停止，1-运行中 */
    @Excel(name = "监控状态", readConverterExp = "0=停止,1=运行中")
    private String monitorStatus;

    /** 最后监控时间 */
    @Excel(name = "最后监控时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastMonitorTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAccountId(Long accountId) 
    {
        this.accountId = accountId;
    }

    public Long getAccountId() 
    {
        return accountId;
    }
    public void setEmailAddress(String emailAddress) 
    {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() 
    {
        return emailAddress;
    }
    public void setImapStatus(String imapStatus) 
    {
        this.imapStatus = imapStatus;
    }

    public String getImapStatus() 
    {
        return imapStatus;
    }
    public void setSmtpStatus(String smtpStatus) 
    {
        this.smtpStatus = smtpStatus;
    }

    public String getSmtpStatus() 
    {
        return smtpStatus;
    }
    public void setImapLastCheckTime(Date imapLastCheckTime) 
    {
        this.imapLastCheckTime = imapLastCheckTime;
    }

    public Date getImapLastCheckTime() 
    {
        return imapLastCheckTime;
    }
    public void setSmtpLastCheckTime(Date smtpLastCheckTime) 
    {
        this.smtpLastCheckTime = smtpLastCheckTime;
    }

    public Date getSmtpLastCheckTime() 
    {
        return smtpLastCheckTime;
    }
    public void setImapErrorMessage(String imapErrorMessage) 
    {
        this.imapErrorMessage = imapErrorMessage;
    }

    public String getImapErrorMessage() 
    {
        return imapErrorMessage;
    }
    public void setSmtpErrorMessage(String smtpErrorMessage) 
    {
        this.smtpErrorMessage = smtpErrorMessage;
    }

    public String getSmtpErrorMessage() 
    {
        return smtpErrorMessage;
    }
    public void setImapErrorTime(Date imapErrorTime) 
    {
        this.imapErrorTime = imapErrorTime;
    }

    public Date getImapErrorTime() 
    {
        return imapErrorTime;
    }
    public void setSmtpErrorTime(Date smtpErrorTime) 
    {
        this.smtpErrorTime = smtpErrorTime;
    }

    public Date getSmtpErrorTime() 
    {
        return smtpErrorTime;
    }
    public void setMonitorEnabled(Integer monitorEnabled) 
    {
        this.monitorEnabled = monitorEnabled;
    }

    public Integer getMonitorEnabled() 
    {
        return monitorEnabled;
    }
    public void setMonitorStatus(String monitorStatus) 
    {
        this.monitorStatus = monitorStatus;
    }

    public String getMonitorStatus() 
    {
        return monitorStatus;
    }
    public void setLastMonitorTime(Date lastMonitorTime) 
    {
        this.lastMonitorTime = lastMonitorTime;
    }

    public Date getLastMonitorTime() 
    {
        return lastMonitorTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("accountId", getAccountId())
            .append("emailAddress", getEmailAddress())
            .append("imapStatus", getImapStatus())
            .append("smtpStatus", getSmtpStatus())
            .append("imapLastCheckTime", getImapLastCheckTime())
            .append("smtpLastCheckTime", getSmtpLastCheckTime())
            .append("imapErrorMessage", getImapErrorMessage())
            .append("smtpErrorMessage", getSmtpErrorMessage())
            .append("imapErrorTime", getImapErrorTime())
            .append("smtpErrorTime", getSmtpErrorTime())
            .append("monitorEnabled", getMonitorEnabled())
            .append("monitorStatus", getMonitorStatus())
            .append("lastMonitorTime", getLastMonitorTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
