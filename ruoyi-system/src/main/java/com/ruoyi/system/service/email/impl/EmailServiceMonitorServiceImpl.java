package com.ruoyi.system.service.email.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.ruoyi.system.service.email.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailServiceMonitorMapper;
import com.ruoyi.system.mapper.email.EmailServiceMonitorLogMapper;
import com.ruoyi.system.domain.email.EmailServiceMonitor;
import com.ruoyi.system.domain.email.EmailServiceMonitorLog;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.enums.EmailServiceStatus;
import com.ruoyi.system.utils.EmailServiceMonitorUtils;

/**
 * 邮件服务监控Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailServiceMonitorServiceImpl implements IEmailServiceMonitorService
{
    private static final Logger log = LoggerFactory.getLogger(EmailServiceMonitorServiceImpl.class);
    
    @Autowired
    private EmailServiceMonitorMapper emailServiceMonitorMapper;
    
    @Autowired
    private EmailServiceMonitorLogMapper emailServiceMonitorLogMapper;
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private EmailListener emailListener;
    
    // 存储正在监控的账号
    private final Map<Long, ScheduledExecutorService> imapMonitors = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledExecutorService> smtpMonitors = new ConcurrentHashMap<>();

    /**
     * 查询邮件服务监控
     * 
     * @param id 邮件服务监控主键
     * @return 邮件服务监控
     */
    @Override
    public EmailServiceMonitor selectEmailServiceMonitorById(Long id)
    {
        return emailServiceMonitorMapper.selectEmailServiceMonitorById(id);
    }

    /**
     * 查询邮件服务监控列表
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 邮件服务监控
     */
    @Override
    public List<EmailServiceMonitor> selectEmailServiceMonitorList(EmailServiceMonitor emailServiceMonitor)
    {
        return emailServiceMonitorMapper.selectEmailServiceMonitorList(emailServiceMonitor);
    }

    /**
     * 新增邮件服务监控
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 结果
     */
    @Override
    public int insertEmailServiceMonitor(EmailServiceMonitor emailServiceMonitor)
    {
        emailServiceMonitor.setCreateTime(DateUtils.getNowDate());
        return emailServiceMonitorMapper.insertEmailServiceMonitor(emailServiceMonitor);
    }

    /**
     * 修改邮件服务监控
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 结果
     */
    public int updateEmailServiceMonitor(EmailServiceMonitor emailServiceMonitor)
    {
        emailServiceMonitor.setUpdateTime(DateUtils.getNowDate());
        return emailServiceMonitorMapper.updateEmailServiceMonitor(emailServiceMonitor);
    }

    /**
     * 批量删除邮件服务监控
     * 
     * @param ids 需要删除的邮件服务监控主键
     * @return 结果
     */
    @Override
    public int deleteEmailServiceMonitorByIds(Long[] ids)
    {
        return emailServiceMonitorMapper.deleteEmailServiceMonitorByIds(ids);
    }

    /**
     * 删除邮件服务监控信息
     * 
     * @param id 邮件服务监控主键
     * @return 结果
     */
    @Override
    public int deleteEmailServiceMonitorById(Long id)
    {
        return emailServiceMonitorMapper.deleteEmailServiceMonitorById(id);
    }
    
    /**
     * 根据账号ID查询监控状态
     * 
     * @param accountId 账号ID
     * @return 监控状态
     */
    @Override
    public EmailServiceMonitor selectEmailServiceMonitorByAccountId(Long accountId)
    {
        return emailServiceMonitorMapper.selectEmailServiceMonitorByAccountId(accountId);
    }
    
    /**
     * 启动IMAP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    @Override
    public Map<String, Object> startImapMonitor(Long accountId)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查是否已经在监控
            if (imapMonitors.containsKey(accountId)) {
                result.put("success", false);
                result.put("message", "IMAP服务已在监控中");
                return result;
            }
            
            // 获取邮箱账号信息
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                result.put("success", false);
                result.put("message", "邮箱账号不存在");
                return result;
            }
            
            // 创建监控任务
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    testImapService(accountId);
                } catch (Exception e) {
                    log.error("IMAP监控任务执行失败: {}", e.getMessage(), e);
                }
            }, 0, 30, TimeUnit.SECONDS); // 每30秒检查一次
            
            imapMonitors.put(accountId, executor);
            
            // 更新监控状态
            EmailServiceMonitor monitor = getOrCreateMonitor(accountId, account.getEmailAddress());
            monitor.setImapStatus(EmailServiceStatus.CONNECTING.getCode());
            monitor.setImapLastCheckTime(DateUtils.getNowDate());
            monitor.setImapErrorMessage(null);
            monitor.setImapErrorTime(null);
            // 设置监控启用标志
            monitor.setMonitorEnabled(1);
            updateEmailServiceMonitor(monitor);
            
            result.put("success", true);
            result.put("message", "IMAP服务监控启动成功");
            
        } catch (Exception e) {
            log.error("启动IMAP监控失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "启动IMAP监控失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 停止IMAP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    @Override
    public Map<String, Object> stopImapMonitor(Long accountId)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ScheduledExecutorService executor = imapMonitors.remove(accountId);
            if (executor != null) {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
            
            // 更新监控状态
            EmailServiceMonitor monitor = selectEmailServiceMonitorByAccountId(accountId);
            if (monitor != null) {
                monitor.setImapStatus(EmailServiceStatus.STOPPED.getCode());
                monitor.setImapLastCheckTime(DateUtils.getNowDate());
                // 设置监控禁用标志，防止自动重启
                monitor.setMonitorEnabled(0);
                updateEmailServiceMonitor(monitor);
            }
            
            result.put("success", true);
            result.put("message", "IMAP服务监控停止成功");
            
        } catch (Exception e) {
            log.error("停止IMAP监控失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "停止IMAP监控失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 启动SMTP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    @Override
    public Map<String, Object> startSmtpMonitor(Long accountId)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查是否已经在监控
            if (smtpMonitors.containsKey(accountId)) {
                result.put("success", false);
                result.put("message", "SMTP服务已在监控中");
                return result;
            }
            
            // 获取邮箱账号信息
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                result.put("success", false);
                result.put("message", "邮箱账号不存在");
                return result;
            }
            
            // 创建监控任务
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    testSmtpService(accountId);
                } catch (Exception e) {
                    log.error("SMTP监控任务执行失败: {}", e.getMessage(), e);
                }
            }, 0, 60, TimeUnit.SECONDS); // 每60秒检查一次
            
            smtpMonitors.put(accountId, executor);
            
            // 更新监控状态
            EmailServiceMonitor monitor = getOrCreateMonitor(accountId, account.getEmailAddress());
            monitor.setSmtpStatus(EmailServiceStatus.CONNECTING.getCode());
            monitor.setSmtpLastCheckTime(DateUtils.getNowDate());
            monitor.setSmtpErrorMessage(null);
            monitor.setSmtpErrorTime(null);
            // 设置监控启用标志
            monitor.setMonitorEnabled(1);
            updateEmailServiceMonitor(monitor);
            
            result.put("success", true);
            result.put("message", "SMTP服务监控启动成功");
            
        } catch (Exception e) {
            log.error("启动SMTP监控失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "启动SMTP监控失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 停止SMTP服务监控
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    @Override
    public Map<String, Object> stopSmtpMonitor(Long accountId)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ScheduledExecutorService executor = smtpMonitors.remove(accountId);
            if (executor != null) {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
            
            // 更新监控状态
            EmailServiceMonitor monitor = selectEmailServiceMonitorByAccountId(accountId);
            if (monitor != null) {
                monitor.setSmtpStatus(EmailServiceStatus.STOPPED.getCode());
                monitor.setSmtpLastCheckTime(DateUtils.getNowDate());
                // 设置监控禁用标志，防止自动重启
                monitor.setMonitorEnabled(0);
                updateEmailServiceMonitor(monitor);
            }
            
            result.put("success", true);
            result.put("message", "SMTP服务监控停止成功");
            
        } catch (Exception e) {
            log.error("停止SMTP监控失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "停止SMTP监控失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 测试IMAP服务
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    @Override
    public Map<String, Object> testImapService(Long accountId)
    {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
            if (account == null) {
                result.put("success", false);
                result.put("message", "邮箱账号不存在");
                return result;
            }
            
            // 使用EmailListener测试IMAP连接
            boolean success = emailListener.isAccountConnected(account.getAccountId());
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 记录日志
            EmailServiceMonitorLog log = new EmailServiceMonitorLog();
            log.setAccountId(accountId);
            log.setEmailAddress(account.getEmailAddress());
            log.setServiceType("IMAP");
            log.setStatus(success ? "success" : "error");
            log.setMessage(success ? "IMAP服务连接正常" : "IMAP服务连接失败");
            log.setCheckTime(DateUtils.getNowDate());
            log.setResponseTime((int) responseTime);
            log.setCreateBy(SecurityUtils.getUsername());
            insertEmailServiceMonitorLog(log);
            
            // 更新监控状态
            EmailServiceMonitor monitor = getOrCreateMonitor(accountId, account.getEmailAddress());
            if (success) {
                monitor.setImapStatus(EmailServiceStatus.RUNNING.getCode());
                monitor.setImapErrorMessage(null);
                monitor.setImapErrorTime(null);
            } else {
                // 使用工具类分析错误状态
                String errorStatus = EmailServiceStatus.SERVICE_ERROR.getCode();
                String errorMessage = "IMAP服务连接失败";
                monitor.setImapStatus(errorStatus);
                monitor.setImapErrorMessage(errorMessage);
                monitor.setImapErrorTime(DateUtils.getNowDate());
            }
            monitor.setImapLastCheckTime(DateUtils.getNowDate());
            updateEmailServiceMonitor(monitor);
            
            result.put("success", success);
            result.put("message", success ? "IMAP服务连接正常" : "IMAP服务连接失败");
            result.put("responseTime", responseTime);
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("测试IMAP服务失败: {}", e.getMessage(), e);
            
            // 记录错误日志
            try {
                EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
                if (account != null) {
                    // 使用工具类分析异常状态
                    String errorStatus = EmailServiceMonitorUtils.determineStatusFromException(e);
                    String errorMessage = EmailServiceMonitorUtils.getErrorMessage(e);
                    String detailedMessage = EmailServiceMonitorUtils.getDetailedErrorMessage(e);
                    
                    EmailServiceMonitorLog log = new EmailServiceMonitorLog();
                    log.setAccountId(accountId);
                    log.setEmailAddress(account.getEmailAddress());
                    log.setServiceType("IMAP");
                    log.setStatus("error");
                    log.setMessage(errorMessage);
                    log.setCheckTime(DateUtils.getNowDate());
                    log.setResponseTime((int) responseTime);
                    log.setCreateBy(SecurityUtils.getUsername());
                    insertEmailServiceMonitorLog(log);
                    
                    // 更新监控状态
                    EmailServiceMonitor monitor = getOrCreateMonitor(accountId, account.getEmailAddress());
                    monitor.setImapStatus(errorStatus);
                    monitor.setImapErrorMessage(detailedMessage);
                    monitor.setImapErrorTime(DateUtils.getNowDate());
                    monitor.setImapLastCheckTime(DateUtils.getNowDate());
                    updateEmailServiceMonitor(monitor);
                }
            } catch (Exception ex) {
                log.error("记录IMAP测试日志失败: {}", ex.getMessage(), ex);
            }
            
            result.put("success", false);
            result.put("message", "IMAP服务测试异常: " + e.getMessage());
            result.put("responseTime", responseTime);
        }
        
        return result;
    }
    
    /**
     * 测试SMTP服务
     * 
     * @param accountId 账号ID
     * @return 结果
     */
    @Override
    public Map<String, Object> testSmtpService(Long accountId)
    {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
            if (account == null) {
                result.put("success", false);
                result.put("message", "邮箱账号不存在");
                return result;
            }
            
            // 使用EmailListener测试SMTP连接
            boolean success = emailListener.isAccountConnected(account.getAccountId());
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 记录日志
            EmailServiceMonitorLog log = new EmailServiceMonitorLog();
            log.setAccountId(accountId);
            log.setEmailAddress(account.getEmailAddress());
            log.setServiceType("SMTP");
            log.setStatus(success ? "success" : "error");
            log.setMessage(success ? "SMTP服务连接正常" : "SMTP服务连接失败");
            log.setCheckTime(DateUtils.getNowDate());
            log.setResponseTime((int) responseTime);
            log.setCreateBy(SecurityUtils.getUsername());
            insertEmailServiceMonitorLog(log);
            
            // 更新监控状态
            EmailServiceMonitor monitor = getOrCreateMonitor(accountId, account.getEmailAddress());
            if (success) {
                monitor.setSmtpStatus(EmailServiceStatus.RUNNING.getCode());
                monitor.setSmtpErrorMessage(null);
                monitor.setSmtpErrorTime(null);
            } else {
                // 使用工具类分析错误状态
                String errorStatus = EmailServiceStatus.SERVICE_ERROR.getCode();
                String errorMessage = "SMTP服务连接失败";
                monitor.setSmtpStatus(errorStatus);
                monitor.setSmtpErrorMessage(errorMessage);
                monitor.setSmtpErrorTime(DateUtils.getNowDate());
            }
            monitor.setSmtpLastCheckTime(DateUtils.getNowDate());
            updateEmailServiceMonitor(monitor);
            
            result.put("success", success);
            result.put("message", success ? "SMTP服务连接正常" : "SMTP服务连接失败");
            result.put("responseTime", responseTime);
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("测试SMTP服务失败: {}", e.getMessage(), e);
            
            // 记录错误日志
            try {
                EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
                if (account != null) {
                    // 使用工具类分析异常状态
                    String errorStatus = EmailServiceMonitorUtils.determineStatusFromException(e);
                    String errorMessage = EmailServiceMonitorUtils.getErrorMessage(e);
                    String detailedMessage = EmailServiceMonitorUtils.getDetailedErrorMessage(e);
                    
                    EmailServiceMonitorLog log = new EmailServiceMonitorLog();
                    log.setAccountId(accountId);
                    log.setEmailAddress(account.getEmailAddress());
                    log.setServiceType("SMTP");
                    log.setStatus("error");
                    log.setMessage(errorMessage);
                    log.setCheckTime(DateUtils.getNowDate());
                    log.setResponseTime((int) responseTime);
                    log.setCreateBy(SecurityUtils.getUsername());
                    insertEmailServiceMonitorLog(log);
                    
                    // 更新监控状态
                    EmailServiceMonitor monitor = getOrCreateMonitor(accountId, account.getEmailAddress());
                    monitor.setSmtpStatus(errorStatus);
                    monitor.setSmtpErrorMessage(detailedMessage);
                    monitor.setSmtpErrorTime(DateUtils.getNowDate());
                    monitor.setSmtpLastCheckTime(DateUtils.getNowDate());
                    updateEmailServiceMonitor(monitor);
                }
            } catch (Exception ex) {
                log.error("记录SMTP测试日志失败: {}", ex.getMessage(), ex);
            }
            
            result.put("success", false);
            result.put("message", "SMTP服务测试异常: " + e.getMessage());
            result.put("responseTime", responseTime);
        }
        
        return result;
    }
    
    /**
     * 获取监控统计信息
     * 
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getMonitorStats()
    {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<EmailServiceMonitor> monitors = selectEmailServiceMonitorList(new EmailServiceMonitor());
            
            int totalAccounts = monitors.size();
            int runningImap = 0;
            int connectingImap = 0;
            int connectedImap = 0;
            int errorImap = 0;
            int runningSmtp = 0;
            int connectingSmtp = 0;
            int connectedSmtp = 0;
            int errorSmtp = 0;
            
            for (EmailServiceMonitor monitor : monitors) {
                // IMAP状态统计
                if (EmailServiceStatus.RUNNING.getCode().equals(monitor.getImapStatus())) {
                    runningImap++;
                } else if (EmailServiceStatus.CONNECTING.getCode().equals(monitor.getImapStatus())) {
                    connectingImap++;
                } else if (EmailServiceStatus.CONNECTED.getCode().equals(monitor.getImapStatus())) {
                    connectedImap++;
                } else if (EmailServiceStatus.isErrorStatus(monitor.getImapStatus())) {
                    errorImap++;
                }
                
                // SMTP状态统计
                if (EmailServiceStatus.RUNNING.getCode().equals(monitor.getSmtpStatus())) {
                    runningSmtp++;
                } else if (EmailServiceStatus.CONNECTING.getCode().equals(monitor.getSmtpStatus())) {
                    connectingSmtp++;
                } else if (EmailServiceStatus.CONNECTED.getCode().equals(monitor.getSmtpStatus())) {
                    connectedSmtp++;
                } else if (EmailServiceStatus.isErrorStatus(monitor.getSmtpStatus())) {
                    errorSmtp++;
                }
            }
            
            stats.put("totalAccounts", totalAccounts);
            stats.put("runningImap", runningImap);
            stats.put("connectingImap", connectingImap);
            stats.put("connectedImap", connectedImap);
            stats.put("errorImap", errorImap);
            stats.put("runningSmtp", runningSmtp);
            stats.put("connectingSmtp", connectingSmtp);
            stats.put("connectedSmtp", connectedSmtp);
            stats.put("errorSmtp", errorSmtp);
            stats.put("activeImapMonitors", imapMonitors.size());
            stats.put("activeSmtpMonitors", smtpMonitors.size());
            
        } catch (Exception e) {
            log.error("获取监控统计信息失败: {}", e.getMessage(), e);
        }
        
        return stats;
    }
    
    /**
     * 查询监控日志列表
     * 
     * @param emailServiceMonitorLog 监控日志
     * @return 监控日志集合
     */
    @Override
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogList(EmailServiceMonitorLog emailServiceMonitorLog)
    {
        return emailServiceMonitorLogMapper.selectEmailServiceMonitorLogList(emailServiceMonitorLog);
    }
    
    /**
     * 根据账号ID查询监控日志
     * 
     * @param accountId 账号ID
     * @return 监控日志列表
     */
    @Override
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogByAccountId(Long accountId)
    {
        return emailServiceMonitorLogMapper.selectEmailServiceMonitorLogByAccountId(accountId);
    }
    
    /**
     * 新增监控日志
     * 
     * @param emailServiceMonitorLog 监控日志
     * @return 结果
     */
    @Override
    public int insertEmailServiceMonitorLog(EmailServiceMonitorLog emailServiceMonitorLog)
    {
        emailServiceMonitorLog.setCreateTime(DateUtils.getNowDate());
        return emailServiceMonitorLogMapper.insertEmailServiceMonitorLog(emailServiceMonitorLog);
    }
    
    /**
     * 清理历史日志
     * 
     * @param days 保留天数
     * @return 结果
     */
    @Override
    public int cleanHistoryLogs(int days)
    {
        return emailServiceMonitorLogMapper.cleanHistoryLogs(days);
    }
    
    /**
     * 更新服务状态
     * 
     * @param accountId 账号ID
     * @param serviceType 服务类型 (imap/smtp)
     * @param status 状态
     * @param errorMessage 错误信息
     */
    @Override
    public void updateServiceStatus(Long accountId, String serviceType, String status, String errorMessage)
    {
        try {
            EmailServiceMonitor monitor = selectEmailServiceMonitorByAccountId(accountId);
            if (monitor == null) {
                // 如果监控记录不存在，创建一个新的
                EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
                if (account != null) {
                    monitor = new EmailServiceMonitor();
                    monitor.setAccountId(accountId);
                    monitor.setEmailAddress(account.getEmailAddress());
                    monitor.setImapStatus("stopped");
                    monitor.setSmtpStatus("stopped");
                    monitor.setMonitorEnabled(1);
                    monitor.setCreateBy(SecurityUtils.getUsername());
                    insertEmailServiceMonitor(monitor);
                } else {
                    log.error("更新服务状态失败：账号不存在，accountId: {}", accountId);
                    return;
                }
            }
            
            if ("imap".equalsIgnoreCase(serviceType)) {
                monitor.setImapStatus(status);
                monitor.setImapLastCheckTime(DateUtils.getNowDate());
                if (errorMessage != null) {
                    monitor.setImapErrorMessage(errorMessage);
                    monitor.setImapErrorTime(DateUtils.getNowDate());
                } else {
                    monitor.setImapErrorMessage(null);
                    monitor.setImapErrorTime(null);
                }
            } else if ("smtp".equalsIgnoreCase(serviceType)) {
                monitor.setSmtpStatus(status);
                monitor.setSmtpLastCheckTime(DateUtils.getNowDate());
                if (errorMessage != null) {
                    monitor.setSmtpErrorMessage(errorMessage);
                    monitor.setSmtpErrorTime(DateUtils.getNowDate());
                } else {
                    monitor.setSmtpErrorMessage(null);
                    monitor.setSmtpErrorTime(null);
                }
            }
            
            updateEmailServiceMonitor(monitor);
            
        } catch (Exception e) {
            log.error("更新服务状态失败: accountId={}, serviceType={}, status={}, error: {}", 
                     accountId, serviceType, status, e.getMessage(), e);
        }
    }
    
    /**
     * 获取或创建监控记录
     * 
     * @param accountId 账号ID
     * @param emailAddress 邮箱地址
     * @return 监控记录
     */
    private EmailServiceMonitor getOrCreateMonitor(Long accountId, String emailAddress)
    {
        EmailServiceMonitor monitor = selectEmailServiceMonitorByAccountId(accountId);
        if (monitor == null) {
            monitor = new EmailServiceMonitor();
            monitor.setAccountId(accountId);
            monitor.setEmailAddress(emailAddress);
            monitor.setImapStatus("stopped");
            monitor.setSmtpStatus("stopped");
            monitor.setMonitorEnabled(1);
            monitor.setCreateBy(SecurityUtils.getUsername());
            insertEmailServiceMonitor(monitor);
        }
        return monitor;
    }
}
