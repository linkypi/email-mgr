package com.ruoyi.system.service.email;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.email.EmailServiceMonitor;
import com.ruoyi.system.domain.email.EmailServiceMonitorLog;

/**
 * 邮件服务监控Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailServiceMonitorService 
{
    /**
     * 查询邮件服务监控
     * 
     * @param id 邮件服务监控主键
     * @return 邮件服务监控
     */
    public EmailServiceMonitor selectEmailServiceMonitorById(Long id);

    /**
     * 查询邮件服务监控列表
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 邮件服务监控集合
     */
    public List<EmailServiceMonitor> selectEmailServiceMonitorList(EmailServiceMonitor emailServiceMonitor);

    /**
     * 新增邮件服务监控
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 结果
     */
    public int insertEmailServiceMonitor(EmailServiceMonitor emailServiceMonitor);

    /**
     * 修改邮件服务监控
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 结果
     */
    public int updateEmailServiceMonitor(EmailServiceMonitor emailServiceMonitor);

    /**
     * 批量删除邮件服务监控
     * 
     * @param ids 需要删除的邮件服务监控主键集合
     * @return 结果
     */
    public int deleteEmailServiceMonitorByIds(Long[] ids);

    /**
     * 删除邮件服务监控信息
     * 
     * @param id 邮件服务监控主键
     * @return 结果
     */
    public int deleteEmailServiceMonitorById(Long id);
    
    /**
     * 根据账号ID查询监控状态
     * 
     * @param accountId 账号ID
     * @return 监控状态
     */
    public EmailServiceMonitor selectEmailServiceMonitorByAccountId(Long accountId);
    
    /**
     * 启动IMAP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    public Map<String, Object> startImapMonitor(Long accountId);
    
    /**
     * 停止IMAP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    public Map<String, Object> stopImapMonitor(Long accountId);
    
    /**
     * 启动SMTP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    public Map<String, Object> startSmtpMonitor(Long accountId);
    
    /**
     * 停止SMTP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    public Map<String, Object> stopSmtpMonitor(Long accountId);
    
    /**
     * 测试IMAP服务
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    public Map<String, Object> testImapService(Long accountId);
    
    /**
     * 测试SMTP服务
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    public Map<String, Object> testSmtpService(Long accountId);
    
    /**
     * 获取监控统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Object> getMonitorStats();
    
    /**
     * 查询监控日志列表
     * 
     * @param emailServiceMonitorLog 监控日志
     * @return 监控日志集合
     */
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogList(EmailServiceMonitorLog emailServiceMonitorLog);
    
    /**
     * 根据账号ID查询监控日志
     * 
     * @param accountId 账号ID
     * @return 监控日志列表
     */
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogByAccountId(Long accountId);
    
    /**
     * 新增监控日志
     * 
     * @param emailServiceMonitorLog 监控日志
     * @return 结果
     */
    public int insertEmailServiceMonitorLog(EmailServiceMonitorLog emailServiceMonitorLog);
    
    /**
     * 清理历史日志
     * 
     * @param days 保留天数
     * @return 结果
     */
    public int cleanHistoryLogs(int days);
    
    /**
     * 更新服务状态
     * 
     * @param accountId 账号ID
     * @param serviceType 服务类型 (imap/smtp)
     * @param status 状态
     * @param errorMessage 错误信息
     */
    public void updateServiceStatus(Long accountId, String serviceType, String status, String errorMessage);
}
