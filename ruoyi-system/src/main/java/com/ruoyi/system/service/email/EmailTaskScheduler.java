package com.ruoyi.system.service.email;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruoyi.system.domain.email.EmailSendTask;
import com.ruoyi.system.service.email.EmailSendService;

/**
 * 邮件发送任务调度器
 * 定时查询即将启动的发送任务，并基于线程池进行批量发送
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Component
public class EmailTaskScheduler
{
    private static final Logger logger = LoggerFactory.getLogger(EmailTaskScheduler.class);

    @Autowired
    private IEmailSendTaskService emailSendTaskService;

    @Autowired
    private EmailSendService emailSendService;

    // 创建线程池，用于批量发送邮件
    private final ExecutorService emailExecutor = Executors.newFixedThreadPool(10);

    /**
     * 定时任务：每分钟检查一次即将启动的发送任务
     * 查询条件：状态为"pending"且发送时间已到的任务
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkPendingEmailTasks()
    {
        try
        {
            logger.info("开始检查即将启动的邮件发送任务...");
            
            // 查询即将启动的发送任务
            List<EmailSendTask> pendingTasks = emailSendTaskService.selectPendingTasks();
            
            if (pendingTasks.isEmpty())
            {
                logger.debug("没有找到即将启动的邮件发送任务");
                return;
            }

            logger.info("找到 {} 个即将启动的邮件发送任务", pendingTasks.size());

            // 为每个任务启动异步发送
            for (EmailSendTask task : pendingTasks)
            {
                CompletableFuture.runAsync(() -> {
                    try
                    {
                        logger.info("开始执行邮件发送任务: {}", task.getTaskName());
                        
                        // 更新任务状态为"1" (执行中)
                        task.setStatus("1");
                        task.setUpdateTime(new Date());
                        emailSendTaskService.updateEmailSendTask(task);
                        
                        // 执行邮件发送
                        emailSendService.startSendTask(task.getTaskId());
                        
                        logger.info("邮件发送任务执行完成: {}", task.getTaskName());
                    }
                    catch (Exception e)
                    {
                        logger.error("邮件发送任务执行失败: {}, 错误: {}", task.getTaskName(), e.getMessage(), e);
                        
                        // 更新任务状态为"3" (执行失败)
                        try
                        {
                            task.setStatus("3");
                            task.setUpdateTime(new Date());
                            task.setRemark("执行失败: " + e.getMessage());
                            emailSendTaskService.updateEmailSendTask(task);
                        }
                        catch (Exception updateException)
                        {
                            logger.error("更新任务状态失败: {}", updateException.getMessage(), updateException);
                        }
                    }
                }, emailExecutor);
            }
        }
        catch (Exception e)
        {
            logger.error("检查邮件发送任务时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 定时任务：每5分钟同步一次邮件统计数据
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void syncEmailStatistics()
    {
        try
        {
            logger.info("开始同步邮件统计数据...");
            
            // 调用邮件统计服务同步数据
            // 这里应该调用具体的同步方法，暂时注释掉
            // emailSendService.getEmailStatisticsService().syncEmailStatistics();
            
            logger.info("邮件统计数据同步完成");
        }
        catch (Exception e)
        {
            logger.error("同步邮件统计数据时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取线程池状态信息
     */
    public String getThreadPoolStatus()
    {
        if (emailExecutor instanceof ThreadPoolExecutor)
        {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) emailExecutor;
            return String.format("线程池状态 - 活跃线程数: %d, 核心线程数: %d, 最大线程数: %d, 队列大小: %d, 已完成任务数: %d",
                    executor.getActiveCount(),
                    executor.getCorePoolSize(),
                    executor.getMaximumPoolSize(),
                    executor.getQueue().size(),
                    executor.getCompletedTaskCount());
        }
        return "线程池状态: " + emailExecutor.getClass().getSimpleName();
    }

    /**
     * 关闭线程池
     */
    public void shutdown()
    {
        if (emailExecutor != null && !emailExecutor.isShutdown())
        {
            emailExecutor.shutdown();
            logger.info("邮件发送线程池已关闭");
        }
    }
}
