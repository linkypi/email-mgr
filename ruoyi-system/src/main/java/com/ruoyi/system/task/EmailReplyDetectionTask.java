package com.ruoyi.system.task;

import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.ISysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 邮件回复检测定时任务
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Component
public class EmailReplyDetectionTask 
{
    private static final Logger log = LoggerFactory.getLogger(EmailReplyDetectionTask.class);
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private EmailListener emailListener;
    
    @Autowired
    private ISysConfigService configService;
    
    // 定时任务执行器
    private ScheduledExecutorService scheduledExecutor;
    private volatile boolean running = false;
    
    @PostConstruct
    public void init() {
        scheduledExecutor = Executors.newScheduledThreadPool(2);
        startScheduledTasks();
    }
    
    @PreDestroy
    public void destroy() {
        running = false;
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
    }
    
    /**
     * 启动定时任务
     */
    private void startScheduledTasks() {
        running = true;
        
        // 启动常规检测任务
        startRegularDetectionTask();
        
        // 启动全量检测任务
        startFullDetectionTask();
    }
    
    /**
     * 启动常规检测任务
     */
    private void startRegularDetectionTask() {
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            if (!running) return;
            
            try {
                detectReplyEmails();
            } catch (Exception e) {
                log.error("常规回复检测任务执行失败", e);
            }
        }, 0, getRegularDetectionInterval(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * 启动全量检测任务
     */
    private void startFullDetectionTask() {
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            if (!running) return;
            
            try {
                fullReplyDetection();
            } catch (Exception e) {
                log.error("全量回复检测任务执行失败", e);
            }
        }, 0, getFullDetectionInterval(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * 获取常规检测间隔
     */
    private long getRegularDetectionInterval() {
        try {
            String interval = configService.selectConfigByKey("email.reply.detection.interval");
            if (interval != null && !interval.trim().isEmpty()) {
                return Long.parseLong(interval);
            }
        } catch (Exception e) {
            log.warn("获取常规检测间隔配置失败，使用默认值", e);
        }
        return 10000; // 默认10秒
    }
    
    /**
     * 获取全量检测间隔
     */
    private long getFullDetectionInterval() {
        try {
            String interval = configService.selectConfigByKey("email.reply.full.detection.interval");
            if (interval != null && !interval.trim().isEmpty()) {
                return Long.parseLong(interval);
            }
        } catch (Exception e) {
            log.warn("获取全量检测间隔配置失败，使用默认值", e);
        }
        return 600000; // 默认10分钟
    }
    
    /**
     * 重新启动定时任务（当配置更改时调用）
     */
    public void restartScheduledTasks() {
        log.info("重新启动邮件回复检测定时任务");
        
        // 停止当前任务
        running = false;
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
        
        // 重新初始化
        scheduledExecutor = Executors.newScheduledThreadPool(2);
        startScheduledTasks();
        
        log.info("邮件回复检测定时任务已重新启动");
    }
    
    /**
     * 定时检测回复邮件
     * 检测间隔通过配置参数控制，默认30秒
     */
    public void detectReplyEmails() 
    {
        try 
        {
            // 检查是否启用回复检测
            String enabled = configService.selectConfigByKey("email.reply.detection.enabled");
            if (!"true".equals(enabled)) {
                log.debug("邮件回复检测功能已禁用");
                return;
            }
            
            log.debug("开始执行邮件回复检测任务");
            
            // 获取所有启用了跟踪的邮箱账户
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            int totalDetectedReplies = 0;
            
            for (EmailAccount account : accounts) {
                // 只处理启用了跟踪的账户
                if (account.getTrackingEnabled() != null && "1".equals(account.getTrackingEnabled())) {
                    try {
                        log.debug("检测账户 {} 的回复邮件", account.getEmailAddress());
                        
                        // 调用EmailListener的回复检测方法
                        int detectedReplies = emailListener.detectReplyEmailsForAccount(account.getAccountId());
                        totalDetectedReplies += detectedReplies;
                        
                        if (detectedReplies > 0) {
                            log.info("账户 {} 检测到 {} 个回复邮件", account.getEmailAddress(), detectedReplies);
                        }
                        
                    } catch (Exception e) {
                        log.error("检测账户 {} 的回复邮件失败: {}", account.getEmailAddress(), e.getMessage());
                    }
                }
            }
            
            if (totalDetectedReplies > 0) {
                log.info("邮件回复检测任务完成，共检测到 {} 个回复", totalDetectedReplies);
            } else {
                log.debug("邮件回复检测任务完成，未检测到新回复");
            }
            
        } 
        catch (Exception e) 
        {
            log.error("邮件回复检测任务执行失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 定时执行全量回复检测（更全面的检测）
     * 检测间隔通过配置参数控制，默认1小时
     */
    public void fullReplyDetection() 
    {
        try 
        {
            // 检查是否启用全量回复检测
            String enabled = configService.selectConfigByKey("email.reply.full.detection.enabled");
            if (!"true".equals(enabled)) {
                log.debug("全量邮件回复检测功能已禁用");
                return;
            }
            
            log.info("开始执行全量邮件回复检测任务");
            
            // 获取所有启用了跟踪的邮箱账户
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            int totalDetectedReplies = 0;
            
            for (EmailAccount account : accounts) {
                // 只处理启用了跟踪的账户
                if (account.getTrackingEnabled() != null && "1".equals(account.getTrackingEnabled())) {
                    try {
                        log.info("全量检测账户 {} 的回复邮件", account.getEmailAddress());
                        
                        // 调用EmailListener的全量回复检测方法
                        int detectedReplies = emailListener.performFullReplyDetection(account.getAccountId());
                        totalDetectedReplies += detectedReplies;
                        
                        if (detectedReplies > 0) {
                            log.info("账户 {} 全量检测到 {} 个回复邮件", account.getEmailAddress(), detectedReplies);
                        }
                        
                    } catch (Exception e) {
                        log.error("全量检测账户 {} 的回复邮件失败: {}", account.getEmailAddress(), e.getMessage());
                    }
                }
            }
            
            log.info("全量邮件回复检测任务完成，共检测到 {} 个回复", totalDetectedReplies);
            
        } 
        catch (Exception e) 
        {
            log.error("全量邮件回复检测任务执行失败: {}", e.getMessage(), e);
        }
    }
}
