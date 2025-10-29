package com.ruoyi.system.domain.email;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 邮件服务监控日志对象 email_service_monitor_log
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailServiceMonitorLog extends BaseEntity
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

    /** 服务类型：IMAP/SMTP */
    @Excel(name = "服务类型", readConverterExp = "IMAP=IMAP,SMTP=SMTP")
    private String serviceType;

    /** 状态：success/error */
    @Excel(name = "状态", readConverterExp = "success=成功,error=失败")
    private String status;

    /** 状态信息 */
    @Excel(name = "状态信息")
    private String message;

    /** 具体错误日志 */
    @Excel(name = "具体错误日志")
    private String errorLog;

    /** 检查时间 */
    @Excel(name = "检查时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    /** 响应时间(毫秒) */
    @Excel(name = "响应时间(毫秒)")
    private Integer responseTime;

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
    public void setServiceType(String serviceType) 
    {
        this.serviceType = serviceType;
    }

    public String getServiceType() 
    {
        return serviceType;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setMessage(String message) 
    {
        this.message = message;
    }

    public String getMessage() 
    {
        return message;
    }
    public void setErrorLog(String errorLog) 
    {
        this.errorLog = errorLog;
    }

    public String getErrorLog() 
    {
        return errorLog;
    }
    public void setCheckTime(Date checkTime) 
    {
        this.checkTime = checkTime;
    }

    public Date getCheckTime() 
    {
        return checkTime;
    }
    public void setResponseTime(Integer responseTime) 
    {
        this.responseTime = responseTime;
    }

    public Integer getResponseTime() 
    {
        return responseTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("accountId", getAccountId())
            .append("emailAddress", getEmailAddress())
            .append("serviceType", getServiceType())
            .append("status", getStatus())
            .append("message", getMessage())
            .append("errorLog", getErrorLog())
            .append("checkTime", getCheckTime())
            .append("responseTime", getResponseTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
