package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.domain.email.EmailSendTask;
import com.ruoyi.system.domain.email.EmailTemplate;
import com.ruoyi.system.domain.email.EmailTaskExecution;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.IEmailContactService;
import com.ruoyi.system.service.email.IEmailSendTaskService;
import com.ruoyi.system.service.email.IEmailTemplateService;
import com.ruoyi.system.service.email.IEmailTaskExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;

@Service
public class EmailSendService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendService.class);

    @Autowired
    private IEmailSendTaskService emailSendTaskService;

    @Autowired
    private IEmailAccountService emailAccountService;

    @Autowired
    private IEmailTemplateService emailTemplateService;

    @Autowired
    private IEmailContactService emailContactService;

    @Autowired
    private IEmailStatisticsService emailStatisticsService;

    @Autowired
    private IEmailTaskExecutionService emailTaskExecutionService;

    @Autowired
    private EmailServiceMonitorService emailServiceMonitorService;

    @Autowired
    private EmailSchedulerService emailSchedulerService;

    @Autowired
    private EmailSendLimitService emailSendLimitService;

    @Autowired
    private EmailListener emailListener;
    
    @Autowired
    private EmailTaskStatusService emailTaskStatusService;

    // 使用全局线程池，避免每次创建新的
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PreDestroy
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 获取邮件统计服务
     * 
     * @return 邮件统计服务
     */
    public IEmailStatisticsService getEmailStatisticsService() {
        return emailStatisticsService;
    }

    @Async
    public void startSendTask(Long taskId) {
        logger.info("开始执行邮件发送任务: {}", taskId);
        EmailTaskExecution execution = null;
        try {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task == null) {
                logger.error("任务不存在: {}", taskId);
                throw new RuntimeException("Task not found: " + taskId);
            }

            // 获取当前用户ID，用于后续的邮件跟踪记录
            Long currentUserId = SecurityUtils.getUserId();
            logger.info("获取当前用户ID: {}", currentUserId);
            
            // 检查是否有未完成的执行记录，如果有则先完成它们
            List<EmailTaskExecution> existingExecutions = emailTaskExecutionService.selectExecutionsByTaskId(taskId);
            for (EmailTaskExecution existingExecution : existingExecutions) {
                if ("1".equals(existingExecution.getExecutionStatus())) { // 执行中
                    logger.info("发现未完成的执行记录，ID: {}，将其标记为已完成", existingExecution.getExecutionId());
                    existingExecution.setExecutionStatus("2"); // 已完成
                    existingExecution.setEndTime(new Date());
                    existingExecution.setUpdateTime(new Date());
                    emailTaskExecutionService.updateEmailTaskExecution(existingExecution);
                }
            }
            
            // 创建任务执行记录
            execution = new EmailTaskExecution();
            execution.setTaskId(taskId);
            execution.setExecutionStatus("1"); // 执行中
            execution.setExecutionUser(SecurityUtils.getUsername());
            execution.setExecutionIp(IpUtils.getIpAddr());
            execution.setStartTime(new Date());
            execution.setCreateBy(SecurityUtils.getUsername());
            execution.setCreateTime(new Date());
            
            // 初始化计数字段，避免null值导致的计数问题
            execution.setTotalCount(0);
            execution.setSentCount(0);
            execution.setSuccessCount(0);
            execution.setFailedCount(0);
            
            emailTaskExecutionService.insertEmailTaskExecution(execution);
            logger.info("创建任务执行记录成功，执行ID: {}", execution.getExecutionId());
            
            // 创建final引用用于lambda表达式
            final EmailTaskExecution finalExecution = execution;
            final Long finalUserId = currentUserId;

            // 更新任务状态为发送中
            task.setStatus("1");
            emailSendTaskService.updateEmailSendTask(task);

            // 获取发件人账户列表
            List<EmailAccount> senderAccounts = getSenderAccounts(task);
            if (senderAccounts.isEmpty()) {
                logger.error("没有可用的发件人账户，任务ID: {}", taskId);
                throw new RuntimeException("No available sender accounts found for task: " + taskId);
            }
            
            logger.info("找到 {} 个可用的发件人账户", senderAccounts.size());
            
            // 检查所有发件邮箱是否都达到每日发送上限
            boolean allAccountsReachedLimit = true;
            for (EmailAccount account : senderAccounts) {
                if (emailSendLimitService.canSendToday(account.getAccountId())) {
                    allAccountsReachedLimit = false;
                    break;
                }
            }
            
            if (allAccountsReachedLimit) {
                logger.warn("所有发件邮箱都已达到每日发送上限，将任务延迟到第二天凌晨0点30分执行，任务ID: {}", taskId);
                delayTaskToNextDay(task);
                return;
            }

            // 获取邮件模板（如果使用模板）
            EmailTemplate template;
            if (task.getTemplateId() != null) {
                template = emailTemplateService.selectEmailTemplateByTemplateId(task.getTemplateId());
                if (template == null) {
                    logger.warn("邮件模板未找到，将使用任务中的直接内容，模板ID: {}", task.getTemplateId());
                }
            } else {
                template = null;
            }

            // 获取收件人列表
            List<EmailContact> recipients = getRecipients(task);
            if (recipients.isEmpty()) {
                logger.error("未找到收件人，任务ID: {}", taskId);
                throw new RuntimeException("No recipients found for task: " + taskId);
            }

            logger.info("准备发送邮件，收件人数量: {}", recipients.size());

            // 初始化执行日志
            StringBuilder executionLog = new StringBuilder();
            executionLog.append("=== 邮件发送任务开始 ===\n");
            executionLog.append("任务ID: ").append(taskId).append("\n");
            executionLog.append("开始时间: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
            executionLog.append("执行人: ").append(SecurityUtils.getUsername()).append("\n");
            executionLog.append("收件人数量: ").append(recipients.size()).append("\n");
            executionLog.append("发件人账户数量: ").append(senderAccounts.size()).append("\n");
            executionLog.append("\n");
            finalExecution.setExecutionLog(executionLog.toString());
            finalExecution.setUpdateTime(new Date());
            emailTaskExecutionService.updateEmailTaskExecution(finalExecution);

            // 初始化任务统计
            task.setTotalCount(recipients.size());
            task.setSentCount(0);
            task.setDeliveredCount(0);
            task.setOpenedCount(0);
            task.setRepliedCount(0);
            emailSendTaskService.updateEmailSendTask(task);

            // 使用线程池异步发送邮件
            CompletableFuture<Void> sendingTask = CompletableFuture.runAsync(() -> {
                int senderIndex = 0;
                int successCount = 0;
                int failedCount = 0;
                
                try {
                    for (EmailContact recipient : recipients) {
                        // 检查任务状态，如果被暂停或取消则停止发送
                        EmailSendTask currentTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                        if (currentTask == null || !"1".equals(currentTask.getStatus())) {
                            logger.info("任务状态已改变，停止发送, 任务名称：{}, 任务ID：{}", task.getTaskName(), taskId);
                            break;
                        }
                    
                    // 获取当前可用的发件邮箱（每次重新检查可用性）
                    EmailAccount currentSender = getCurrentAvailableSender(senderAccounts, senderIndex);
                    if (currentSender == null) {
                        int remainingCount = recipients.size() - recipients.indexOf(recipient);
                        logger.warn("所有发件邮箱都已达到每日发送上限，剩余 {} 封邮件未发送", remainingCount);
                        
                        // 将剩余邮件延迟到第二天凌晨0点30分执行
                        delayRemainingEmailsToNextDay(currentTask, remainingCount);
                        
                        // 更新当前任务状态为已完成
                        currentTask.setStatus("2"); // 已完成
                        currentTask.setRemark("部分邮件已发送，剩余邮件已延迟到第二天执行");
                        emailSendTaskService.updateEmailSendTask(currentTask);
                        break;
                    }
                    
                    logger.info("准备发送邮件，发件箱：{}, 收件人: {}", currentSender.getEmailAddress(), recipient.getEmail());
                    
                    try {
                        // 准备邮件内容
                        String subject = getEmailSubject(template, task, recipient);
                        String content = getEmailContent(template, task, recipient);
                        
                        // 使用 EmailListener 发送带跟踪的邮件
                        logger.info("开始发送邮件: {} -> {}, 任务ID: {}", currentSender.getEmailAddress(), recipient.getEmail(), taskId);
                        
                        EmailListener.EmailSendResult emailSendResult = emailListener.sendEmailWithTracking(
                                currentSender,
                                recipient.getEmail(),
                                subject,
                                content,
                                taskId,
                                finalUserId
                        );

                        if (emailSendResult.isSuccess()) {
                            successCount++;
                            // 更新发送计数
                            synchronized (this) {
                                EmailSendTask latestTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                                latestTask.setSentCount(latestTask.getSentCount() + 1);
                                emailSendTaskService.updateEmailSendTask(latestTask);
                            }
                            
                            // 更新邮箱发送计数
                            emailSendLimitService.updateSendCount(currentSender.getAccountId());
                            
                            logger.info("邮件发送成功: {} -> {}, MessageID: {}", 
                                currentSender.getEmailAddress(), recipient.getEmail(), emailSendResult.getMessageId());
                            
                            // 实时更新执行记录统计
                            if (finalExecution != null) {
                                String logMsg = String.format("[%s] 邮件发送成功: %s -> %s, MessageID: %s\n", 
                                    new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()),
                                    currentSender.getEmailAddress(), recipient.getEmail(), emailSendResult.getMessageId());
                                finalExecution.setExecutionLog(finalExecution.getExecutionLog() + logMsg);
                                
                                // 实时更新统计字段
                                finalExecution.setTotalCount(recipients.size());
                                finalExecution.setSentCount(successCount + failedCount);
                                finalExecution.setSuccessCount(successCount);
                                finalExecution.setFailedCount(failedCount);
                                
                                finalExecution.setUpdateTime(new Date());
                                emailTaskExecutionService.updateEmailTaskExecution(finalExecution);
                                logger.debug("执行记录统计已更新: 总数={}, 已发送={}, 成功={}, 失败={}", 
                                           finalExecution.getTotalCount(), finalExecution.getSentCount(), 
                                           finalExecution.getSuccessCount(), finalExecution.getFailedCount());
                            }
                        } else {
                            failedCount++;
                            String errorMsg = String.format("邮件发送失败: %s -> %s, 错误信息: %s", 
                                currentSender.getEmailAddress(), recipient.getEmail(), emailSendResult.getErrorMessage());
                            logger.error(errorMsg);
                            
                            // 实时更新执行记录统计（失败情况）
                            if (finalExecution != null) {
                                String logMsg = String.format("[%s] 邮件发送失败: %s -> %s, 错误: %s\n", 
                                    new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()),
                                    currentSender.getEmailAddress(), recipient.getEmail(), emailSendResult.getErrorMessage());
                                finalExecution.setExecutionLog(finalExecution.getExecutionLog() + logMsg);
                                
                                // 实时更新统计字段
                                finalExecution.setTotalCount(recipients.size());
                                finalExecution.setSentCount(successCount + failedCount);
                                finalExecution.setSuccessCount(successCount);
                                finalExecution.setFailedCount(failedCount);
                                
                                finalExecution.setUpdateTime(new Date());
                                emailTaskExecutionService.updateEmailTaskExecution(finalExecution);
                                logger.debug("执行记录统计已更新（失败）: 总数={}, 已发送={}, 成功={}, 失败={}", 
                                           finalExecution.getTotalCount(), finalExecution.getSentCount(), 
                                           finalExecution.getSuccessCount(), finalExecution.getFailedCount());
                            }
                            
                            // 记录错误信息到执行记录
                            if (finalExecution != null) {
                                String currentErrorMsg = finalExecution.getErrorMessage();
                                if (currentErrorMsg == null || currentErrorMsg.isEmpty()) {
                                    finalExecution.setErrorMessage(errorMsg);
                                } else {
                                    finalExecution.setErrorMessage(currentErrorMsg + "; " + errorMsg);
                                }
                                
                                // 记录失败日志到执行记录
                                String logMsg = String.format("[%s] 邮件发送失败: %s -> %s, 错误: %s\n", 
                                    new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()),
                                    currentSender.getEmailAddress(), recipient.getEmail(), emailSendResult.getErrorMessage());
                                finalExecution.setExecutionLog(finalExecution.getExecutionLog() + logMsg);
                                finalExecution.setUpdateTime(new Date());
                                emailTaskExecutionService.updateEmailTaskExecution(finalExecution);
                            }
                            
                            // 即使发送失败，跟踪记录也应该已经创建，记录失败状态
                            if (emailSendResult.getMessageId() != null) {
                                logger.info("邮件发送失败但跟踪记录已创建: MessageID={}", emailSendResult.getMessageId());
                            }
                        }

                        // 切换到下一个发件邮箱
                        senderIndex = (senderIndex + 1) % senderAccounts.size();
                        
                        // 发送间隔控制（除了最后一封邮件）
                        if (recipients.indexOf(recipient) < recipients.size() - 1) {
                            int interval = getSendInterval(currentSender.getAccountId());
                            if (interval > 0) {
                                logger.debug("等待 {} 秒后发送下一封邮件", interval);
                                Thread.sleep(interval * 1000L);
                            }
                        }
                        
                    } catch (Exception e) {
                        failedCount++;
                        String errorMsg = String.format("发送邮件失败: %s -> %s, 错误: %s", 
                            currentSender.getEmailAddress(), recipient.getEmail(), e.getMessage());
                        logger.error(errorMsg);
                        
                        // 记录错误信息到执行记录
                        if (finalExecution != null) {
                            String currentErrorMsg = finalExecution.getErrorMessage();
                            if (currentErrorMsg == null || currentErrorMsg.isEmpty()) {
                                finalExecution.setErrorMessage(errorMsg);
                            } else {
                                finalExecution.setErrorMessage(currentErrorMsg + "; " + errorMsg);
                            }
                            
                            // 记录异常日志到执行记录
                            String logMsg = String.format("[%s] 邮件发送异常: %s -> %s, 错误: %s\n", 
                                new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()),
                                currentSender.getEmailAddress(), recipient.getEmail(), e.getMessage());
                            finalExecution.setExecutionLog(finalExecution.getExecutionLog() + logMsg);
                            finalExecution.setUpdateTime(new Date());
                            emailTaskExecutionService.updateEmailTaskExecution(finalExecution);
                        }
                    }
                }
                
                // 任务完成，更新执行记录状态
                try {
                    EmailSendTask finalTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                    if (finalTask != null) {
                        logger.info("邮件发送任务完成: {}, 成功: {}, 失败: {}", taskId, successCount, failedCount);
                        
                        // 更新任务执行记录状态
                        if (finalExecution != null) {
                            // 根据失败情况判断执行状态
                            String executionStatus;
                            String taskStatus;
                            if (failedCount > 0 && successCount == 0) {
                                // 全部失败
                                executionStatus = "3"; // 执行失败
                                taskStatus = "3"; // 任务失败
                            } else if (failedCount > 0) {
                                // 部分失败
                                executionStatus = "2"; // 已完成（部分成功）
                                taskStatus = "2"; // 任务完成
                            } else {
                                // 全部成功
                                executionStatus = "2"; // 已完成
                                taskStatus = "2"; // 任务完成
                            }
                            
                            // 更新任务状态
                            finalTask.setStatus(taskStatus);
                            emailSendTaskService.updateEmailSendTask(finalTask);
                            
                            finalExecution.setExecutionStatus(executionStatus);
                            finalExecution.setEndTime(new Date());
                            finalExecution.setTotalCount(recipients.size());
                            finalExecution.setSentCount(successCount + failedCount);
                            finalExecution.setSuccessCount(successCount);
                            finalExecution.setFailedCount(failedCount);
                            
                            // 添加任务完成日志
                            String completionLog = String.format("\n=== 邮件发送任务完成 ===\n");
                            completionLog += String.format("结束时间: %s\n", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            completionLog += String.format("总收件人: %d\n", recipients.size());
                            completionLog += String.format("成功发送: %d\n", successCount);
                            completionLog += String.format("发送失败: %d\n", failedCount);
                            completionLog += String.format("成功率: %.2f%%\n", recipients.size() > 0 ? (successCount * 100.0 / recipients.size()) : 0);
                            finalExecution.setExecutionLog(finalExecution.getExecutionLog() + completionLog);
                            
                            finalExecution.setUpdateTime(new Date());
                            emailTaskExecutionService.updateEmailTaskExecution(finalExecution);
                            logger.info("任务执行记录已更新为完成状态，执行ID: {}", finalExecution.getExecutionId());
                        } else {
                            // 如果没有执行记录，根据失败情况设置任务状态
                            if (failedCount > 0 && successCount == 0) {
                                finalTask.setStatus("3"); // 任务失败
                            } else {
                                finalTask.setStatus("2"); // 任务完成
                            }
                            emailSendTaskService.updateEmailSendTask(finalTask);
                        }
                    }
                } catch (Exception e) {
                    logger.error("更新任务完成状态失败: {}", e.getMessage(), e);
                }
                
                } catch (Exception e) {
                    logger.error("邮件发送任务执行异常: {}", e.getMessage(), e);
                    try {
                        EmailSendTask finalTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                        if (finalTask != null) {
                            finalTask.setStatus("4"); // 失败
                            emailSendTaskService.updateEmailSendTask(finalTask);
                        }
                        
                        // 更新任务执行记录状态为失败
                        if (finalExecution != null) {
                            finalExecution.setExecutionStatus("3"); // 执行失败
                            finalExecution.setEndTime(new Date());
                            finalExecution.setErrorMessage(e.getMessage());
                            // 设置失败数量为总数（因为任务执行异常，所有邮件都未发送成功）
                            finalExecution.setFailedCount(finalExecution.getTotalCount() != null ? finalExecution.getTotalCount() : 0);
                            finalExecution.setSuccessCount(0);
                            finalExecution.setSentCount(0);
                            finalExecution.setUpdateTime(new Date());
                            emailTaskExecutionService.updateEmailTaskExecution(finalExecution);
                        }
                    } catch (Exception ex) {
                        logger.error("更新任务失败状态失败: {}", ex.getMessage());
                    }
                }
            }, executorService);

            // 等待发送完成（不阻塞主线程池）
            sendingTask.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("邮件发送任务异步执行异常: {}", throwable.getMessage(), throwable);
                } else {
                    logger.info("邮件发送任务异步执行完成: {}", taskId);
                }
            });

        } catch (Exception e) {
            logger.error("邮件发送任务执行失败: {}", e.getMessage(), e);
            try {
                EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                if (task != null) {
                    task.setStatus("4"); // 失败
                    emailSendTaskService.updateEmailSendTask(task);
                }
                
                // 更新任务执行记录状态为失败
                if (execution != null) {
                    execution.setExecutionStatus("3"); // 执行失败
                    execution.setEndTime(new Date());
                    execution.setErrorMessage(e.getMessage());
                    // 设置失败数量为总数（因为任务执行异常，所有邮件都未发送成功）
                    execution.setFailedCount(execution.getTotalCount() != null ? execution.getTotalCount() : 0);
                    execution.setSuccessCount(0);
                    execution.setSentCount(0);
                    execution.setUpdateTime(new Date());
                    emailTaskExecutionService.updateEmailTaskExecution(execution);
                }
            } catch (Exception ex) {
                logger.error("更新任务状态失败: {}", ex.getMessage());
            }
        }
    }

    private List<EmailContact> getRecipients(EmailSendTask task) {
        logger.info("获取收件人列表，类型: {}", task.getRecipientType());
        
        try {
            String recipientType = task.getRecipientType();
            if ("all".equals(recipientType)) {
                // 获取所有联系人
                EmailContact query = new EmailContact();
                query.setDeleted("0"); // 只获取未删除的联系人
                return emailContactService.selectEmailContactList(query);
            } else if ("group".equals(recipientType)) {
                // 根据群组ID获取联系人
                if (task.getGroupIds() != null && !task.getGroupIds().trim().isEmpty()) {
                    String[] groupIds = task.getGroupIds().split(",");
                    return emailContactService.selectContactsByGroupIds(Arrays.asList(groupIds));
                }
            } else if ("tag".equals(recipientType)) {
                // 根据标签ID获取联系人
                if (task.getTagIds() != null && !task.getTagIds().trim().isEmpty()) {
                    String[] tagIds = task.getTagIds().split(",");
                    return emailContactService.selectContactsByTagIds(Arrays.asList(tagIds));
                }
            } else if ("manual".equals(recipientType)) {
                // 根据手动选择的联系人ID获取联系人
                if (task.getContactIds() != null && !task.getContactIds().trim().isEmpty()) {
                    String[] contactIds = task.getContactIds().split(",");
                    return emailContactService.selectContactsByIds(Arrays.asList(contactIds));
                }
            }
            
            logger.warn("未能根据收件人类型获取到联系人: {}", recipientType);
            return Arrays.asList(); // 返回空列表
        } catch (Exception e) {
            logger.error("获取收件人列表失败: {}", e.getMessage(), e);
            return Arrays.asList(); // 返回空列表
        }
    }

    private Session createMailSession(EmailAccount account) {
        Properties props = new Properties();
        props.put("mail.smtp.host", account.getSmtpHost());
        props.put("mail.smtp.port", account.getSmtpPort());
        props.put("mail.smtp.auth", "true");
        
        // 根据端口判断使用SSL还是STARTTLS
        int port = account.getSmtpPort();
        if (port == 465) {
            // SSL连接
            props.put("mail.smtp.socketFactory.port", account.getSmtpPort());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        } else if (port == 587) {
            // STARTTLS连接
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        } else if (port == 25) {
            // 普通连接
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        // 设置超时时间
        props.put("mail.smtp.timeout", "60000");
        props.put("mail.smtp.connectiontimeout", "60000");
        
        // 信任所有主机（用于开发环境）
        props.put("mail.smtp.ssl.trust", "*");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.getEmailAddress(), account.getPassword());
            }
        });
    }

    private void sendEmail(Session session, EmailAccount account, EmailTemplate template, 
                          EmailContact recipient, EmailSendTask task) throws Exception {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(account.getEmailAddress()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient.getEmail()));
        
        String subject;
        String content;
        
        if (template != null) {
            // 使用模板
            subject = template.getSubject();
            content = template.getContent();
        } else {
            // 使用任务中的直接内容
            subject = task.getSubject();
            content = task.getContent();
        }
        
        // 替换占位符
        if (subject != null) {
            subject = replacePlaceholders(subject, recipient);
        }
        if (content != null) {
            content = replacePlaceholders(content, recipient);
        }
        
        message.setSubject(subject != null ? subject : "");
        
        // 请求送达状态通知（DSN）
        message.setHeader("Return-Receipt-To", account.getEmailAddress());
        message.setHeader("Disposition-Notification-To", account.getEmailAddress());
        message.setHeader("X-Confirm-Reading-To", account.getEmailAddress());
        
        // 设置邮件内容
        if (content != null) {
            if (content.contains("<") && content.contains(">")) {
                // 如果内容包含HTML标签，使用HTML格式发送
                message.setContent(content, "text/html; charset=UTF-8");
                logger.debug("使用HTML格式发送邮件");
            } else {
                // 如果内容不包含HTML标签，使用纯文本格式发送
                message.setText(content);
                logger.debug("使用纯文本格式发送邮件");
            }
        }
        
        try {
            Transport.send(message);
            logger.debug("邮件发送成功: {} -> {}, 主题: {}", account.getEmailAddress(), recipient.getEmail(), subject);
        } catch (Exception e) {
            logger.error("邮件发送失败: {} -> {}, 错误: {}", account.getEmailAddress(), recipient.getEmail(), e.getMessage());
            throw new Exception("邮件发送失败: " + account.getEmailAddress() + " -> " + recipient.getEmail() + ", 错误: " + e.getMessage());
        }

        // 更新账户使用统计
        try {
            account.setUsedCount(account.getUsedCount() + 1);
            account.setLastSendTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            emailAccountService.updateEmailAccountStatistics(account);
        } catch (Exception e) {
            logger.warn("更新账户统计失败: {}", e.getMessage());
        }

        // 更新任务投递统计
        try {
            synchronized (this) {
                EmailSendTask latestTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                if (latestTask != null) {
                    latestTask.setDeliveredCount(latestTask.getDeliveredCount() + 1);
                    emailSendTaskService.updateEmailSendTask(latestTask);
                }
            }
        } catch (Exception e) {
            logger.warn("更新任务统计失败: {}", e.getMessage());
        }
    }
    
    /**
     * 替换邮件内容中的占位符
     * 支持 {name}, {email}, {company} 等格式的占位符
     */
    private String replacePlaceholders(String text, EmailContact recipient) {
        if (text == null || recipient == null) return text;
        
        // 基本信息占位符
        text = text.replace("{name}", recipient.getName() != null ? recipient.getName() : "");
        text = text.replace("{email}", recipient.getEmail() != null ? recipient.getEmail() : "");
        text = text.replace("{company}", recipient.getCompany() != null ? recipient.getCompany() : "");
        text = text.replace("{address}", recipient.getAddress() != null ? recipient.getAddress() : "");
        
        // 个人属性占位符
        text = text.replace("{age}", recipient.getAge() != null ? recipient.getAge().toString() : "");
        text = text.replace("{gender}", recipient.getGender() != null ? recipient.getGender() : "");
        text = text.replace("{level}", recipient.getLevel() != null ? recipient.getLevel() : "");
        text = text.replace("{groupName}", recipient.getGroupName() != null ? recipient.getGroupName() : "");
        
        // 统计信息占位符
        text = text.replace("{sendCount}", recipient.getSendCount() != null ? recipient.getSendCount().toString() : "0");
        text = text.replace("{replyCount}", recipient.getReplyCount() != null ? recipient.getReplyCount().toString() : "0");
        text = text.replace("{openCount}", recipient.getOpenCount() != null ? recipient.getOpenCount().toString() : "0");
        text = text.replace("{replyRate}", recipient.getReplyRate() != null ? String.format("%.1f%%", recipient.getReplyRate()) : "0%");
        
        // 其他占位符
        text = text.replace("{tags}", recipient.getTags() != null ? recipient.getTags() : "");
        text = text.replace("{socialMedia}", recipient.getSocialMedia() != null ? recipient.getSocialMedia() : "");
        text = text.replace("{followers}", recipient.getFollowers() != null ? recipient.getFollowers().toString() : "0");
        
        // 兼容旧的占位符格式
        text = text.replace("${name}", recipient.getName() != null ? recipient.getName() : "");
        text = text.replace("${email}", recipient.getEmail() != null ? recipient.getEmail() : "");
        text = text.replace("${company}", recipient.getCompany() != null ? recipient.getCompany() : "");
        text = text.replace("{{name}}", recipient.getName() != null ? recipient.getName() : "");
        text = text.replace("{{email}}", recipient.getEmail() != null ? recipient.getEmail() : "");
        text = text.replace("{{company}}", recipient.getCompany() != null ? recipient.getCompany() : "");
        
        logger.debug("占位符替换完成，收件人: {}, 替换后内容长度: {}", recipient.getEmail(), text.length());
        
        return text;
    }

    public void pauseSendTask(Long taskId) {
        try {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task != null) {
                task.setStatus("3");
                emailSendTaskService.updateEmailSendTask(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSendTask(Long taskId) {
        try {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task != null) {
                task.setStatus("4");
                emailSendTaskService.updateEmailSendTask(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用长连接发送邮件任务
     * 优先使用EmailServiceMonitorService中的accountMonitorStates长连接
     */
    public void startSendTaskWithLongConnection(Long taskId) {
        logger.info("使用长连接开始执行邮件发送任务: {}", taskId);
        EmailTaskExecution execution = null;
        try {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task == null) {
                logger.error("任务不存在: {}", taskId);
                throw new RuntimeException("Task not found: " + taskId);
            }

            // 获取当前用户ID，用于后续的邮件跟踪记录
            Long currentUserId = SecurityUtils.getUserId();
            logger.info("获取当前用户ID: {}", currentUserId);

            // 检查是否有未完成的执行记录，如果有则先完成它们
            List<EmailTaskExecution> existingExecutions = emailTaskExecutionService.selectExecutionsByTaskId(taskId);
            for (EmailTaskExecution existingExecution : existingExecutions) {
                if ("1".equals(existingExecution.getExecutionStatus())) { // 执行中
                    logger.info("发现未完成的执行记录，ID: {}，将其标记为已完成", existingExecution.getExecutionId());
                    existingExecution.setExecutionStatus("2"); // 已完成
                    existingExecution.setEndTime(new Date());
                    existingExecution.setUpdateTime(new Date());
                    emailTaskExecutionService.updateEmailTaskExecution(existingExecution);
                }
            }

            // 创建任务执行记录
            execution = new EmailTaskExecution();
            execution.setTaskId(taskId);
            execution.setExecutionStatus("1"); // 执行中
            execution.setExecutionUser(SecurityUtils.getUsername());
            execution.setExecutionIp(IpUtils.getIpAddr());
            execution.setStartTime(new Date());
            execution.setCreateBy(SecurityUtils.getUsername());
            execution.setCreateTime(new Date());
            
            // 初始化计数字段，避免null值导致的计数问题
            execution.setTotalCount(0);
            execution.setSentCount(0);
            execution.setSuccessCount(0);
            execution.setFailedCount(0);
            
            emailTaskExecutionService.insertEmailTaskExecution(execution);
            logger.info("创建长连接任务执行记录成功，执行ID: {}", execution.getExecutionId());

            // 更新任务状态为发送中
            task.setStatus("1");
            emailSendTaskService.updateEmailSendTask(task);

            // 获取发件人账户列表
            List<EmailAccount> senderAccounts = getSenderAccounts(task);
            if (senderAccounts.isEmpty()) {
                logger.error("没有可用的发件人账户，任务ID: {}", taskId);
                throw new RuntimeException("No available sender accounts found for task: " + taskId);
            }
            
            // 选择第一个可用的账户进行长连接发送
            EmailAccount account = senderAccounts.get(0);
            
            // 检查账户是否还有发送配额
            if (!emailSendLimitService.canSendToday(account.getAccountId())) {
                logger.warn("长连接账户已达到每日发送上限，回退到普通发送方式进行账号轮换，账户: {}", account.getEmailAddress());
                // 回退到普通发送方式，支持账号轮换
                startSendTask(taskId);
                return;
            }

            // 检查账户是否有长连接且连接健康
            boolean hasLongConnection = emailServiceMonitorService.isAccountMonitoring(account.getAccountId());
            boolean isConnectionHealthy = emailServiceMonitorService.isSmtpConnectionHealthy(account.getAccountId());
            int healthScore = emailServiceMonitorService.getSmtpHealthScore(account.getAccountId());
            
            if (hasLongConnection && isConnectionHealthy) {
                logger.info("使用健康的长连接发送邮件，账户: {}, 健康度: {}", account.getEmailAddress(), healthScore);
                sendWithLongConnection(task, account, currentUserId, execution);
            } else if (hasLongConnection && !isConnectionHealthy) {
                logger.warn("长连接存在但不健康 (健康度: {})，使用普通连接发送邮件，账户: {}", healthScore, account.getEmailAddress());
                // 回退到普通发送方式
                startSendTask(taskId);
            } else {
                logger.info("长连接不可用，使用普通连接发送邮件，账户: {}", account.getEmailAddress());
                // 回退到普通发送方式
                startSendTask(taskId);
            }

        } catch (Exception e) {
            logger.error("长连接邮件发送任务执行失败: {}", e.getMessage(), e);
            try {
                EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                if (task != null) {
                    task.setStatus("4"); // 失败
                    emailSendTaskService.updateEmailSendTask(task);
                }
                
                // 更新任务执行记录状态为失败
                if (execution != null) {
                    execution.setExecutionStatus("3"); // 执行失败
                    execution.setEndTime(new Date());
                    execution.setErrorMessage(e.getMessage());
                    // 设置失败数量为总数（因为任务执行异常，所有邮件都未发送成功）
                    execution.setFailedCount(execution.getTotalCount() != null ? execution.getTotalCount() : 0);
                    execution.setSuccessCount(0);
                    execution.setSentCount(0);
                    execution.setUpdateTime(new Date());
                    emailTaskExecutionService.updateEmailTaskExecution(execution);
                }
            } catch (Exception ex) {
                logger.error("更新任务状态失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 使用长连接发送邮件
     * 现在使用 sendEmailWithTracking 来确保邮件追踪功能
     */
    private void sendWithLongConnection(EmailSendTask task, EmailAccount account, Long userId, EmailTaskExecution execution) throws Exception {
        // 获取邮件模板（如果使用模板）
        EmailTemplate template;
        if (task.getTemplateId() != null) {
            template = emailTemplateService.selectEmailTemplateByTemplateId(task.getTemplateId());
            if (template == null) {
                logger.warn("邮件模板未找到，将使用任务中的直接内容，模板ID: {}", task.getTemplateId());
            }
        } else {
            template = null;
        }

        // 获取收件人列表
        List<EmailContact> recipients = getRecipients(task);
        if (recipients.isEmpty()) {
            logger.error("未找到收件人，任务ID: {}", task.getTaskId());
            throw new RuntimeException("No recipients found for task: " + task.getTaskId());
        }

        logger.info("准备使用长连接发送邮件（带追踪功能），收件人数量: {}", recipients.size());

        // 初始化长连接执行日志
        if (execution != null) {
            StringBuilder executionLog = new StringBuilder();
            executionLog.append("=== 长连接邮件发送任务开始 ===\n");
            executionLog.append("任务ID: ").append(task.getTaskId()).append("\n");
            executionLog.append("开始时间: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
            executionLog.append("执行人: ").append(SecurityUtils.getUsername()).append("\n");
            executionLog.append("收件人数量: ").append(recipients.size()).append("\n");
            executionLog.append("发件人账户: ").append(account.getEmailAddress()).append("\n");
            executionLog.append("\n");
            execution.setExecutionLog(executionLog.toString());
            execution.setUpdateTime(new Date());
            emailTaskExecutionService.updateEmailTaskExecution(execution);
        }

        // 初始化任务统计
        task.setTotalCount(recipients.size());
        task.setSentCount(0);
        task.setDeliveredCount(0);
        task.setOpenedCount(0);
        task.setRepliedCount(0);
        emailSendTaskService.updateEmailSendTask(task);

        // 使用异步方式发送邮件，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                for (EmailContact recipient : recipients) {
                    // 检查任务状态，如果被暂停或取消则停止发送
                    EmailSendTask currentTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                    if (currentTask == null || !"1".equals(currentTask.getStatus())) {
                        logger.info("任务状态已改变，停止发送, 任务名称：{}, 任务ID：{}", task.getTaskName(), task.getTaskId());
                        break;
                    }
                    
                    // 检查当前账号是否还有发送配额
                    if (!emailSendLimitService.canSendToday(account.getAccountId())) {
                        int remainingCount = recipients.size() - recipients.indexOf(recipient);
                        logger.warn("长连接账号已达到每日发送上限，剩余 {} 封邮件未发送", remainingCount);
                        
                        // 将剩余邮件延迟到第二天凌晨0点30分执行
                        delayRemainingEmailsToNextDay(currentTask, remainingCount);
                        
                        // 更新当前任务状态为已完成
                        currentTask.setStatus("2"); // 已完成
                        currentTask.setRemark("部分邮件已发送，剩余邮件已延迟到第二天执行");
                        emailSendTaskService.updateEmailSendTask(currentTask);
                        break;
                    }
                    
                    // 准备邮件内容
                    String subject = getEmailSubject(template, task, recipient);
                    String content = getEmailContent(template, task, recipient);
                    
                    // 使用 EmailListener 发送带跟踪的邮件（长连接方式）
                    logger.info("开始长连接发送邮件: {} -> {}, 任务ID: {}", account.getEmailAddress(), recipient.getEmail(), task.getTaskId());
                    
                    EmailListener.EmailSendResult emailSendResult = emailListener.sendEmailWithTracking(
                            account,
                            recipient.getEmail(),
                            subject,
                            content,
                            task.getTaskId(),
                            userId
                    );

                    if (emailSendResult.isSuccess()) {
                        // 更新发送计数
                        synchronized (this) {
                            EmailSendTask latestTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                            latestTask.setSentCount(latestTask.getSentCount() + 1);
                            emailSendTaskService.updateEmailSendTask(latestTask);
                        }
                        
                        // 更新邮箱发送计数
                        emailSendLimitService.updateSendCount(account.getAccountId());
                        
                        logger.debug("长连接邮件发送成功: {} -> {}, MessageID: {}", 
                            account.getEmailAddress(), recipient.getEmail(), emailSendResult.getMessageId());
                        
                        // 实时更新执行记录统计
                        if (execution != null) {
                            String logMsg = String.format("[%s] 长连接邮件发送成功: %s -> %s, MessageID: %s\n", 
                                new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()),
                                account.getEmailAddress(), recipient.getEmail(), emailSendResult.getMessageId());
                            execution.setExecutionLog(execution.getExecutionLog() + logMsg);
                            
                            // 实时更新统计字段
                            execution.setTotalCount(recipients.size());
                            execution.setSentCount(execution.getSentCount() + 1);
                            execution.setSuccessCount(execution.getSuccessCount() + 1);
                            
                            execution.setUpdateTime(new Date());
                            emailTaskExecutionService.updateEmailTaskExecution(execution);
                            logger.debug("长连接执行记录统计已更新: 总数={}, 已发送={}, 成功={}, 失败={}", 
                                       execution.getTotalCount(), execution.getSentCount(), 
                                       execution.getSuccessCount(), execution.getFailedCount());
                        }
                    } else {
                        String errorMsg = String.format("长连接邮件发送失败: %s -> %s, 错误: %s", 
                            account.getEmailAddress(), recipient.getEmail(), 
                            emailSendResult.getErrorMessage() != null ? emailSendResult.getErrorMessage() : "未知错误");
                        logger.error(errorMsg);
                        
                        // 实时更新执行记录统计（失败情况）
                        if (execution != null) {
                            String currentErrorMsg = execution.getErrorMessage();
                            if (currentErrorMsg == null || currentErrorMsg.isEmpty()) {
                                execution.setErrorMessage(errorMsg);
                            } else {
                                execution.setErrorMessage(currentErrorMsg + "; " + errorMsg);
                            }
                            
                            // 记录失败日志到执行记录
                            String logMsg = String.format("[%s] 长连接邮件发送失败: %s -> %s, 错误: %s\n", 
                                new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()),
                                account.getEmailAddress(), recipient.getEmail(), 
                                emailSendResult.getErrorMessage() != null ? emailSendResult.getErrorMessage() : "未知错误");
                            execution.setExecutionLog(execution.getExecutionLog() + logMsg);
                            
                            // 实时更新统计字段
                            execution.setTotalCount(recipients.size());
                            execution.setSentCount(execution.getSentCount() + 1);
                            execution.setFailedCount(execution.getFailedCount() + 1);
                            
                            execution.setUpdateTime(new Date());
                            emailTaskExecutionService.updateEmailTaskExecution(execution);
                            logger.debug("长连接执行记录统计已更新（失败）: 总数={}, 已发送={}, 成功={}, 失败={}", 
                                       execution.getTotalCount(), execution.getSentCount(), 
                                       execution.getSuccessCount(), execution.getFailedCount());
                        }
                    }

                    // 发送间隔
                    if (task.getSendInterval() != null && task.getSendInterval() > 0) {
                        Thread.sleep(task.getSendInterval() * 1000L);
                    }
                }
                
                // 发送完成，更新任务状态和执行记录
                EmailSendTask finalTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                if (finalTask != null) {
                    logger.info("长连接邮件发送任务完成: {}", task.getTaskId());
                    
                    // 更新任务执行记录状态
                    if (execution != null) {
                        // 根据失败情况判断执行状态
                        String executionStatus;
                        String taskStatus;
                        if (execution.getFailedCount() > 0 && execution.getSuccessCount() == 0) {
                            // 全部失败
                            executionStatus = "3"; // 执行失败
                            taskStatus = "3"; // 任务失败
                        } else if (execution.getFailedCount() > 0) {
                            // 部分失败
                            executionStatus = "2"; // 已完成（部分成功）
                            taskStatus = "2"; // 任务完成
                        } else {
                            // 全部成功
                            executionStatus = "2"; // 已完成
                            taskStatus = "2"; // 任务完成
                        }
                        
                        // 更新任务状态
                        finalTask.setStatus(taskStatus);
                        emailSendTaskService.updateEmailSendTask(finalTask);
                        
                        execution.setExecutionStatus(executionStatus);
                        execution.setEndTime(new Date());
                        
                        // 添加任务完成日志
                        String completionLog = String.format("\n=== 长连接邮件发送任务完成 ===\n");
                        completionLog += String.format("结束时间: %s\n", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        completionLog += String.format("总收件人: %d\n", recipients.size());
                        completionLog += String.format("成功发送: %d\n", execution.getSuccessCount());
                        completionLog += String.format("发送失败: %d\n", execution.getFailedCount());
                        completionLog += String.format("成功率: %.2f%%\n", recipients.size() > 0 ? (execution.getSuccessCount() * 100.0 / recipients.size()) : 0);
                        execution.setExecutionLog(execution.getExecutionLog() + completionLog);
                        
                        execution.setUpdateTime(new Date());
                        emailTaskExecutionService.updateEmailTaskExecution(execution);
                        logger.info("长连接任务执行记录已更新为完成状态，执行ID: {}", execution.getExecutionId());
                    } else {
                        // 如果没有执行记录，设置为完成状态
                        finalTask.setStatus("2"); // 任务完成
                        emailSendTaskService.updateEmailSendTask(finalTask);
                    }
                }
                
            } catch (Exception e) {
                logger.error("长连接邮件发送任务异常: {}", e.getMessage(), e);
                try {
                    EmailSendTask finalTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                    if (finalTask != null) {
                        finalTask.setStatus("4"); // 失败
                        emailSendTaskService.updateEmailSendTask(finalTask);
                    }
                    
                    // 更新任务执行记录状态为失败
                    if (execution != null) {
                        execution.setExecutionStatus("3"); // 执行失败
                        execution.setEndTime(new Date());
                        execution.setErrorMessage(e.getMessage());
                        // 设置失败数量为总数（因为任务执行异常，所有邮件都未发送成功）
                        execution.setFailedCount(execution.getTotalCount() != null ? execution.getTotalCount() : 0);
                        execution.setSuccessCount(0);
                        execution.setSentCount(0);
                        execution.setUpdateTime(new Date());
                        emailTaskExecutionService.updateEmailTaskExecution(execution);
                        logger.info("长连接任务执行记录已更新为失败状态，执行ID: {}", execution.getExecutionId());
                    }
                } catch (Exception ex) {
                    logger.error("更新长连接任务状态失败: {}", ex.getMessage());
                }
            }
        });
    }

    /**
     * 使用指定的Transport发送邮件
     */
    private void sendEmailWithTransport(Session session, Transport transport, EmailAccount account, 
                                      EmailTemplate template, EmailContact recipient, EmailSendTask task) throws Exception {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(account.getEmailAddress()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient.getEmail()));
        
        String subject;
        String content;
        
        if (template != null) {
            // 使用模板
            subject = template.getSubject();
            content = template.getContent();
        } else {
            // 使用任务中的直接内容
            subject = task.getSubject();
            content = task.getContent();
        }
        
        // 替换占位符
        if (subject != null) {
            subject = replacePlaceholders(subject, recipient);
        }
        if (content != null) {
            content = replacePlaceholders(content, recipient);
        }
        
        message.setSubject(subject != null ? subject : "");
        
        // 请求送达状态通知（DSN）
        message.setHeader("Return-Receipt-To", account.getEmailAddress());
        message.setHeader("Disposition-Notification-To", account.getEmailAddress());
        message.setHeader("X-Confirm-Reading-To", account.getEmailAddress());
        
        // 设置邮件内容
        if (content != null) {
            if (content.contains("<") && content.contains(">")) {
                // 如果内容包含HTML标签，使用HTML格式发送
                message.setContent(content, "text/html; charset=UTF-8");
                logger.debug("使用HTML格式发送邮件");
            } else {
                // 如果内容不包含HTML标签，使用纯文本格式发送
                message.setText(content);
                logger.debug("使用纯文本格式发送邮件");
            }
        }
        
        try {
            // 使用指定的Transport发送
            transport.sendMessage(message, message.getAllRecipients());
            logger.debug("长连接邮件发送成功: {} -> {}, 主题: {}", account.getEmailAddress(), recipient.getEmail(), subject);
        } catch (Exception e) {
            if(e instanceof IllegalStateException){
                IllegalStateException illegalStateException = (IllegalStateException) e;
                if(illegalStateException.getMessage().contains("Not connected")){
                    // 重连后再试，并设置重试次数
                    logger.info("长连接已断开，尝试重新创建");
                    int retryCount = 3;
                    while (retryCount > 0) {
                        Transport newTransport = emailServiceMonitorService.createSmtpLongConnection(account.getAccountId());
                        if (newTransport != null && newTransport.isConnected()) {
                            logger.info("长连接重新创建成功");
                            // 递归调用发送方法
                            sendEmailWithTransport(session, newTransport, account, template, recipient, task);
                            return;
                        }
                        retryCount--;
                        logger.info("重新创建长连接失败，剩余重试次数: {}", retryCount);
                        Thread.sleep(2000); // 等待2秒后重试
                    }
                    if (retryCount == 0) {
                        logger.error("重新创建长连接失败");
                    }
                }
            }
            logger.error("长连接邮件发送失败: {} -> {}, 错误: {}", account.getEmailAddress(), recipient.getEmail(), e.getMessage());
            throw new Exception("长连接邮件发送失败: " + account.getEmailAddress() + " -> " + recipient.getEmail() + ", 错误: " + e.getMessage());
        }

        // 更新账户使用统计
        try {
            account.setUsedCount(account.getUsedCount() + 1);
            account.setLastSendTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            emailAccountService.updateEmailAccountStatistics(account);
        } catch (Exception e) {
            logger.warn("更新账户统计失败: {}", e.getMessage());
        }

        // 更新任务投递统计
        try {
            synchronized (this) {
                EmailSendTask latestTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                if (latestTask != null) {
                    latestTask.setDeliveredCount(latestTask.getDeliveredCount() + 1);
                    emailSendTaskService.updateEmailSendTask(latestTask);
                }
            }
        } catch (Exception e) {
            logger.warn("更新任务统计失败: {}", e.getMessage());
        }
    }
    
    /**
     * 检测SMTP Transport的健康状态
     * 不仅检查isConnected()，还尝试验证连接的实际可用性
     */
    private boolean isTransportHealthy(Transport transport) {
        if (transport == null) {
            logger.debug("Transport健康检查失败: Transport为null");
            return false;
        }
        
        if (!transport.isConnected()) {
            logger.debug("Transport健康检查失败: 连接已断开");
            return false;
        }
        
        // 进行更深入的连接健康检查
        try {
            // 尝试获取连接的基本属性来验证连接仍然有效
            String protocol = transport.getURLName().getProtocol();
            String host = transport.getURLName().getHost();
            
            if (protocol == null || host == null) {
                logger.debug("Transport健康检查失败: 连接属性异常 protocol={}, host={}", protocol, host);
                return false;
            }
            
            // 如果是SMTPTransport类型，可以进行更详细的检查
            if (transport instanceof com.sun.mail.smtp.SMTPTransport) {
                com.sun.mail.smtp.SMTPTransport smtpTransport = (com.sun.mail.smtp.SMTPTransport) transport;
                try {
                    // 尝试发送NOOP命令来真正验证连接
                    smtpTransport.issueCommand("NOOP", 250);
                    logger.debug("Transport健康检查成功: NOOP命令响应正常");
                    return true;
                } catch (Exception e) {
                    logger.debug("Transport健康检查失败: NOOP命令失败 - {}", e.getMessage());
                    return false;
                }
            }
            
            logger.debug("Transport健康检查成功: 基本连接正常");
            return true;
            
        } catch (Exception e) {
            logger.debug("Transport健康检查异常: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取发件人账户列表
     * 
     * @param task 发送任务
     * @return 可用的发件人账户列表
     */
    private List<EmailAccount> getSenderAccounts(EmailSendTask task) {
        List<EmailAccount> accounts = new ArrayList<>();
        
        // 优先使用 senderIds（多个发件人ID）
        if (task.getSenderIds() != null && !task.getSenderIds().trim().isEmpty()) {
            try {
                String[] senderIdArray = task.getSenderIds().split(",");
                for (String senderIdStr : senderIdArray) {
                    if (senderIdStr != null && !senderIdStr.trim().isEmpty()) {
                        try {
                            Long senderId = Long.parseLong(senderIdStr.trim());
                            List<EmailAccount> senderAccounts = emailAccountService.selectEmailAccountBySenderId(senderId);
                            for (EmailAccount account : senderAccounts) {
                                if (account != null && "0".equals(account.getStatus()) && emailSendLimitService.canSendToday(account.getAccountId())) {
                                    accounts.add(account);
                                    logger.debug("添加发件人邮箱账号: {} ({})", account.getAccountName(), account.getEmailAddress());
                                } else {
                                    logger.debug("发件人邮箱账号不可用: {} (ID: {}, 状态: {}, 今日可发送: {})", 
                                        account != null ? account.getAccountName() : "未知", 
                                        account != null ? account.getAccountId() : "未知",
                                        account != null ? account.getStatus() : "未知",
                                        account != null ? emailSendLimitService.canSendToday(account.getAccountId()) : false);
                                }
                            }
                        } catch (NumberFormatException e) {
                            logger.warn("无效的发件人ID格式: {}", senderIdStr);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("获取多个发件人邮箱账号失败: {}", e.getMessage());
            }
        }
        // 回退到单个 senderId（兼容旧版本）
        else if (task.getSenderId() != null) {
            try {
                List<EmailAccount> senderAccounts = emailAccountService.selectEmailAccountBySenderId(task.getSenderId());
                for (EmailAccount account : senderAccounts) {
                    if (account != null && "0".equals(account.getStatus()) && emailSendLimitService.canSendToday(account.getAccountId())) {
                        accounts.add(account);
                        logger.debug("添加发件人邮箱账号: {} ({})", account.getAccountName(), account.getEmailAddress());
                    } else {
                        logger.debug("发件人邮箱账号不可用: {} (ID: {}, 状态: {}, 今日可发送: {})", 
                            account != null ? account.getAccountName() : "未知", 
                            account != null ? account.getAccountId() : "未知",
                            account != null ? account.getStatus() : "未知",
                            account != null ? emailSendLimitService.canSendToday(account.getAccountId()) : false);
                    }
                }
            } catch (Exception e) {
                logger.error("获取发件人邮箱账号失败: {}", e.getMessage());
            }
        }
        
        // 如果没有发件人ID，回退到 accountIds（多邮箱支持）
        if (accounts.isEmpty() && task.getAccountIds() != null && !task.getAccountIds().trim().isEmpty()) {
            try {
                // 解析 accountIds（假设是逗号分隔的ID列表）
                String[] accountIdArray = task.getAccountIds().split(",");
                for (String accountIdStr : accountIdArray) {
                    try {
                        Long accountId = Long.parseLong(accountIdStr.trim());
                        EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
                        if (account != null && emailSendLimitService.canSendToday(accountId)) {
                            accounts.add(account);
                            logger.debug("添加可用发件箱: {} ({})", account.getAccountName(), account.getEmailAddress());
                        } else {
                            logger.debug("发件箱不可用: {} (ID: {})", 
                                account != null ? account.getAccountName() : "未知", accountId);
                        }
                    } catch (NumberFormatException e) {
                        logger.warn("无效的账户ID: {}", accountIdStr);
                    }
                }
            } catch (Exception e) {
                logger.error("解析账户ID列表失败: {}", e.getMessage());
            }
        }
        
        // 如果还是没有，回退到单个账户
        if (accounts.isEmpty() && task.getAccountId() != null) {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(task.getAccountId());
            if (account != null && emailSendLimitService.canSendToday(task.getAccountId())) {
                accounts.add(account);
                logger.debug("添加单个发件箱: {} ({})", account.getAccountName(), account.getEmailAddress());
            } else {
                logger.debug("单个发件箱不可用: {} (ID: {})", 
                    account != null ? account.getAccountName() : "未知", task.getAccountId());
            }
        }
        
        return accounts;
    }
    
    /**
     * 获取当前可用的发件邮箱
     * 
     * @param availableSenders 可用发件邮箱列表
     * @param index 当前索引
     * @return 可用的发件邮箱
     */
    private EmailAccount getCurrentAvailableSender(List<EmailAccount> availableSenders, int index) {
        // 过滤掉已达到每日上限的发件邮箱
        List<EmailAccount> stillAvailable = new ArrayList<>();
        for (EmailAccount sender : availableSenders) {
            if (emailSendLimitService.canSendToday(sender.getAccountId())) {
                stillAvailable.add(sender);
            } else {
                logger.debug("邮箱账号已达到每日发送上限: {} ({})", sender.getAccountName(), sender.getEmailAddress());
            }
        }
        
        if (stillAvailable.isEmpty()) {
            logger.warn("所有邮箱账号都已达到每日发送上限，无法继续发送");
            return null;
        }
        
        // 轮换使用
        EmailAccount selectedAccount = stillAvailable.get(index % stillAvailable.size());
        logger.debug("选择邮箱账号: {} ({})，剩余可用账号数: {}", 
            selectedAccount.getAccountName(), selectedAccount.getEmailAddress(), stillAvailable.size());
        return selectedAccount;
    }
    
    /**
     * 获取发送间隔时间（秒）
     * 
     * @param accountId 邮箱账户ID
     * @return 发送间隔时间（秒）
     */
    private int getSendInterval(Long accountId) {
        return emailSendLimitService.getRandomSendInterval(accountId);
    }
    
    /**
     * 获取邮件主题
     * 
     * @param template 邮件模板
     * @param task 发送任务
     * @param recipient 收件人
     * @return 邮件主题
     */
    private String getEmailSubject(EmailTemplate template, EmailSendTask task, EmailContact recipient) {
        String subject;
        
        if (template != null && template.getSubject() != null) {
            subject = template.getSubject();
        } else if (task.getSubject() != null) {
            subject = task.getSubject();
        } else {
            subject = "";
        }
        
        // 替换占位符
        if (subject != null && recipient != null) {
            subject = replacePlaceholders(subject, recipient);
        }
        
        return subject;
    }
    
    /**
     * 获取邮件内容
     * 
     * @param template 邮件模板
     * @param task 发送任务
     * @param recipient 收件人
     * @return 邮件内容
     */
    private String getEmailContent(EmailTemplate template, EmailSendTask task, EmailContact recipient) {
        String content;
        
        if (template != null && template.getContent() != null) {
            content = template.getContent();
        } else if (task.getContent() != null) {
            content = task.getContent();
        } else {
            content = "";
        }
        
        // 替换占位符
        if (content != null && recipient != null) {
            content = replacePlaceholders(content, recipient);
        }
        
        return content;
    }
    
    /**
     * 将超出部分延迟到第二天凌晨0点30分执行
     * 
     * @param task 发送任务
     * @param remainingCount 剩余邮件数量
     */
    private void delayRemainingEmailsToNextDay(EmailSendTask task, int remainingCount) {
        try {
            if (remainingCount <= 0) {
                logger.info("没有剩余邮件需要延迟，任务ID: {}", task.getTaskId());
                return;
            }
            
            // 计算第二天凌晨0点30分的时间
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1); // 加一天
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0); // 设置为0点
            calendar.set(java.util.Calendar.MINUTE, 30); // 设置为30分
            calendar.set(java.util.Calendar.SECOND, 0); // 设置为0秒
            calendar.set(java.util.Calendar.MILLISECOND, 0); // 设置为0毫秒
            
            Date nextDayTime = calendar.getTime();
            
            // 创建新的延迟任务，包含剩余的邮件
            EmailSendTask delayedTask = new EmailSendTask();
            delayedTask.setTaskName(task.getTaskName() + "_延迟_" + new java.text.SimpleDateFormat("MMdd").format(new Date()));
            delayedTask.setTemplateId(task.getTemplateId());
            delayedTask.setSenderId(task.getSenderId());
            delayedTask.setSubject(task.getSubject());
            delayedTask.setContent(task.getContent());
            delayedTask.setRecipientType(task.getRecipientType());
            delayedTask.setRecipientIds(task.getRecipientIds());
            delayedTask.setGroupIds(task.getGroupIds());
            delayedTask.setTagIds(task.getTagIds());
            delayedTask.setContactIds(task.getContactIds());
            delayedTask.setAccountIds(task.getAccountIds());
            delayedTask.setSendMode("scheduled"); // 定时发送
            delayedTask.setSendInterval(task.getSendInterval());
            delayedTask.setStartTime(nextDayTime);
            delayedTask.setTotalCount(remainingCount);
            delayedTask.setSentCount(0);
            delayedTask.setDeliveredCount(0);
            delayedTask.setOpenedCount(0);
            delayedTask.setRepliedCount(0);
            delayedTask.setStatus("0"); // 待发送
            delayedTask.setRemark("因所有发件邮箱达到每日发送上限，剩余" + remainingCount + "封邮件已自动延迟到第二天凌晨0点30分执行");
            delayedTask.setCreateBy(task.getCreateBy());
            delayedTask.setCreateTime(new Date());
            delayedTask.setUpdateTime(new Date());
            
            // 插入新的延迟任务
            emailSendTaskService.insertEmailSendTask(delayedTask);
            
            // 添加到调度器
            emailSchedulerService.scheduleEmailTask(delayedTask);
            
            logger.info("剩余邮件已延迟到第二天凌晨0点30分执行: 原任务ID={}, 延迟任务ID={}, 剩余邮件数={}, 延迟时间={}", 
                task.getTaskId(), delayedTask.getTaskId(), remainingCount, 
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextDayTime));
                
        } catch (Exception e) {
            logger.error("延迟剩余邮件到第二天执行失败: taskId={}, remainingCount={}", task.getTaskId(), remainingCount, e);
        }
    }
    
    /**
     * 将整个任务延迟到第二天凌晨0点30分执行（用于发送前检查）
     * 
     * @param task 发送任务
     */
    private void delayTaskToNextDay(EmailSendTask task) {
        try {
            // 计算第二天凌晨0点30分的时间
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1); // 加一天
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0); // 设置为0点
            calendar.set(java.util.Calendar.MINUTE, 30); // 设置为30分
            calendar.set(java.util.Calendar.SECOND, 0); // 设置为0秒
            calendar.set(java.util.Calendar.MILLISECOND, 0); // 设置为0毫秒
            
            Date nextDayTime = calendar.getTime();
            
            // 更新任务状态为待发送，并设置新的发送时间
            task.setStatus("0"); // 待发送
            task.setSendMode("scheduled"); // 改为定时发送
            task.setStartTime(nextDayTime);
            task.setUpdateTime(new Date());
            task.setRemark("因所有发件邮箱达到每日发送上限，已自动延迟到第二天凌晨0点30分执行");
            
            // 更新数据库
            emailSendTaskService.updateEmailSendTask(task);
            
            // 添加到调度器
            emailSchedulerService.scheduleEmailTask(task);
            
            logger.info("任务已延迟到第二天凌晨0点30分执行: taskId={}, delayTime={}", 
                task.getTaskId(), new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextDayTime));
                
        } catch (Exception e) {
            logger.error("延迟任务到第二天执行失败: taskId={}", task.getTaskId(), e);
            // 如果延迟失败，将任务状态设置为失败
            try {
                task.setStatus("4"); // 失败
                task.setRemark("延迟执行失败: " + e.getMessage());
                emailSendTaskService.updateEmailSendTask(task);
            } catch (Exception ex) {
                logger.error("更新任务状态失败: taskId={}", task.getTaskId(), ex);
            }
        }
    }
    
    /**
     * 手动检测回复邮件
     * 
     * @param taskId 任务ID
     */
    public void manualReplyDetection(Long taskId) {
        try {
            logger.info("开始手动检测任务 {} 的回复邮件", taskId);
            
            // 调用EmailListener的手动回复检测方法
            emailListener.manualReplyDetection(taskId);
            
            logger.info("手动回复检测完成，任务ID: {}", taskId);
            
        } catch (Exception e) {
            logger.error("手动回复检测失败，任务ID: {}", taskId, e);
            throw e;
        }
    }
}
