package com.ruoyi.system.task;

import com.ruoyi.system.service.email.EmailSendLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 邮件发送限制重置定时任务
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Component
public class EmailSendLimitResetTask 
{
    private static final Logger log = LoggerFactory.getLogger(EmailSendLimitResetTask.class);
    
    @Autowired
    private EmailSendLimitService emailSendLimitService;
    
    /**
     * 每日凌晨0点重置发送计数
     * 使用 cron 表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailySendCount() 
    {
        try 
        {
            log.info("开始执行每日发送计数重置任务");
            int result = emailSendLimitService.resetDailySendCount();
            log.info("每日发送计数重置任务完成，影响行数: {}", result);
        } 
        catch (Exception e) 
        {
            log.error("每日发送计数重置任务执行失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每小时检查并重置过期的每日计数
     * 使用 cron 表达式：每小时的0分执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkAndResetExpiredCounts() 
    {
        try 
        {
            log.debug("开始检查过期的每日发送计数");
            // 这里可以添加额外的检查逻辑，比如检查跨天的计数重置
            log.debug("过期每日发送计数检查完成");
        } 
        catch (Exception e) 
        {
            log.error("检查过期每日发送计数失败: {}", e.getMessage(), e);
        }
    }
}
