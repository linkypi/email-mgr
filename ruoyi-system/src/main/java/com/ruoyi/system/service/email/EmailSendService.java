package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.domain.email.EmailSendTask;
import com.ruoyi.system.domain.email.EmailTemplate;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.IEmailContactService;
import com.ruoyi.system.service.email.IEmailSendTaskService;
import com.ruoyi.system.service.email.IEmailTemplateService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private EmailServiceMonitorService emailServiceMonitorService;

    @Autowired
    private EmailSchedulerService emailSchedulerService;

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
        try {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task == null) {
                logger.error("任务不存在: {}", taskId);
                throw new RuntimeException("Task not found: " + taskId);
            }

            // 更新任务状态为发送中
            task.setStatus("1");
            emailSendTaskService.updateEmailSendTask(task);

            // 获取发件人账户
            EmailAccount account;
            if (task.getAccountId() != null) {
                account = emailAccountService.selectEmailAccountByAccountId(task.getAccountId());
            } else {
                account = null;
            }
            if (account == null) {
                logger.error("发件人账户未找到，任务ID: {}, 账户ID: {}", taskId, task.getAccountId());
                throw new RuntimeException("Sender account not found. TaskId: " + taskId + ", AccountId: " + task.getAccountId());
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

            // 初始化任务统计
            task.setTotalCount(recipients.size());
            task.setSentCount(0);
            task.setDeliveredCount(0);
            task.setOpenedCount(0);
            task.setRepliedCount(0);
            emailSendTaskService.updateEmailSendTask(task);

            // 创建邮件会话
            Session session = createMailSession(account);

            // 使用线程池异步发送邮件
            CompletableFuture<Void> sendingTask = CompletableFuture.runAsync(() -> {
                for (EmailContact recipient : recipients) {
                    // 检查任务状态，如果被暂停或取消则停止发送
                    EmailSendTask currentTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                    if (currentTask == null || !"1".equals(currentTask.getStatus())) {
                        logger.info("任务状态已改变，停止发送, 任务名称：{}, 任务ID：{}", task.getTaskName(), taskId);
                        break;
                    }
                    logger.info("准备发送邮件，发件箱：{}, 收件人: {}",account.getEmailAddress(), recipient.getEmail());
                    try {
                        // 发送邮件
                        sendEmail(session, account, template, recipient, task);
                        
                        // 更新发送计数
                        synchronized (this) {
                            EmailSendTask latestTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                            latestTask.setSentCount(latestTask.getSentCount() + 1);
                            emailSendTaskService.updateEmailSendTask(latestTask);
                        }

                        logger.debug("邮件发送成功: {} -> {}", account.getEmailAddress(), recipient.getEmail());

                        // 发送间隔
                        if (task.getSendInterval() != null && task.getSendInterval() > 0) {
                            Thread.sleep(task.getSendInterval() * 1000L);
                        }
                    } catch (Exception e) {
                        logger.error("发送邮件失败: {} -> {}, 错误: {}", account.getEmailAddress(), recipient.getEmail(), e.getMessage());
                    }
                }
            }, executorService);

            // 等待发送完成（不阻塞主线程池）
            sendingTask.whenComplete((result, throwable) -> {
                try {
                    EmailSendTask finalTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                    if (finalTask != null) {
                        if (throwable != null) {
                            logger.error("邮件发送任务异常: {}", throwable.getMessage());
                            finalTask.setStatus("4"); // 失败
                        } else {
                            logger.info("邮件发送任务完成: {}", taskId);
                            finalTask.setStatus("2"); // 完成
                        }
                        emailSendTaskService.updateEmailSendTask(finalTask);
                    }
                } catch (Exception e) {
                    logger.error("更新任务状态失败: {}", e.getMessage());
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
     */
    private String replacePlaceholders(String text, EmailContact recipient) {
        if (text == null) return null;
        
        return text
                .replace("${name}", recipient.getName() != null ? recipient.getName() : "")
                .replace("${email}", recipient.getEmail() != null ? recipient.getEmail() : "")
                .replace("${company}", recipient.getCompany() != null ? recipient.getCompany() : "")
                .replace("{{name}}", recipient.getName() != null ? recipient.getName() : "")
                .replace("{{email}}", recipient.getEmail() != null ? recipient.getEmail() : "")
                .replace("{{company}}", recipient.getCompany() != null ? recipient.getCompany() : "");
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
        try {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task == null) {
                logger.error("任务不存在: {}", taskId);
                throw new RuntimeException("Task not found: " + taskId);
            }

            // 更新任务状态为发送中
            task.setStatus("1");
            emailSendTaskService.updateEmailSendTask(task);

            // 获取发件人账户
            EmailAccount account;
            if (task.getAccountId() != null) {
                account = emailAccountService.selectEmailAccountWithDecryptedPassword(task.getAccountId());
            } else {
                account = null;
            }
            if (account == null) {
                logger.error("发件人账户未找到，任务ID: {}, 账户ID: {}", taskId, task.getAccountId());
                throw new RuntimeException("Sender account not found. TaskId: " + taskId + ", AccountId: " + task.getAccountId());
            }

            // 检查账户是否有长连接且连接健康
            boolean hasLongConnection = emailServiceMonitorService.isAccountMonitoring(account.getAccountId());
            boolean isConnectionHealthy = emailServiceMonitorService.isSmtpConnectionHealthy(account.getAccountId());
            int healthScore = emailServiceMonitorService.getSmtpHealthScore(account.getAccountId());
            
            if (hasLongConnection && isConnectionHealthy) {
                logger.info("使用健康的长连接发送邮件，账户: {}, 健康度: {}", account.getEmailAddress(), healthScore);
                sendWithLongConnection(task, account);
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
            } catch (Exception ex) {
                logger.error("更新任务状态失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 使用长连接发送邮件
     * 直接使用EmailServiceMonitorService中的长连接
     */
    private void sendWithLongConnection(EmailSendTask task, EmailAccount account) throws Exception {
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

        logger.info("准备使用长连接发送邮件，收件人数量: {}", recipients.size());

        // 初始化任务统计
        task.setTotalCount(recipients.size());
        task.setSentCount(0);
        task.setDeliveredCount(0);
        task.setOpenedCount(0);
        task.setRepliedCount(0);
        emailSendTaskService.updateEmailSendTask(task);

        // 获取或创建长连接
        Transport transport = emailServiceMonitorService.getSmtpLongConnection(account.getAccountId());
        Session session = emailServiceMonitorService.getSmtpSession(account.getAccountId());
        
        // 如果没有长连接，尝试创建
        if (transport == null || !transport.isConnected()) {
            logger.info("长连接不存在或已断开，尝试创建新的长连接");
            transport = emailServiceMonitorService.createSmtpLongConnection(account.getAccountId());
            session = emailServiceMonitorService.getSmtpSession(account.getAccountId());
        }
        
        // 如果仍然无法获取长连接，尝试多次重连
        int maxRetryAttempts = 3;
        int retryAttempt = 0;
        
        while ((transport == null || session == null || !transport.isConnected()) && retryAttempt < maxRetryAttempts) {
            retryAttempt++;
            logger.warn("长连接不可用 (尝试 {}/{}), 等待2秒后重新创建连接", retryAttempt, maxRetryAttempts);
            
            try {
                Thread.sleep(2000); // 等待2秒
                transport = emailServiceMonitorService.createSmtpLongConnection(account.getAccountId());
                session = emailServiceMonitorService.getSmtpSession(account.getAccountId());
                
                if (transport != null && transport.isConnected()) {
                    logger.info("第{}次重连成功，长连接已恢复", retryAttempt);
                    break;
                }
            } catch (Exception e) {
                logger.warn("第{}次重连失败: {}", retryAttempt, e.getMessage());
            }
        }
        
        // 如果多次重连后仍然无法获取长连接，回退到普通连接
        if (transport == null || session == null || !transport.isConnected()) {
            logger.warn("经过{}次重连尝试后无法恢复长连接，回退到普通连接发送", maxRetryAttempts);
            throw new RuntimeException("长连接不可用，请使用普通发送方式");
        }
        
        logger.info("使用现有长连接发送邮件: {}:{}", account.getSmtpHost(), account.getSmtpPort());
        
        // 使用线程池异步发送邮件
        final Transport finalTransport = transport;
        final Session finalSession = session;
        CompletableFuture<Void> sendingTask = CompletableFuture.runAsync(() -> {
            for (EmailContact recipient : recipients) {
                // 检查任务状态，如果被暂停或取消则停止发送
                EmailSendTask currentTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                if (currentTask == null || !"1".equals(currentTask.getStatus())) {
                    logger.info("任务状态已改变，停止发送, 任务名称：{}, 任务ID：{}", task.getTaskName(), task.getTaskId());
                    break;
                }
                
                logger.info("准备使用长连接发送邮件，发件箱：{}, 收件人: {}", account.getEmailAddress(), recipient.getEmail());
                try {
                    // 增强的连接检测和重连机制
                    Transport currentTransport = finalTransport;
                    Session currentSession = finalSession;
                    
                    // 首先进行连接状态检测
                    if (!isTransportHealthy(currentTransport)) {
                        int healthScore = emailServiceMonitorService.getSmtpHealthScore(account.getAccountId());
                        logger.warn("长连接健康检查失败 (健康度评分: {})，尝试重新创建连接", healthScore);
                        
                        // 尝试重新创建连接，最多3次
                        int reconnectAttempts = 3;
                        boolean reconnectSuccess = false;
                        
                        for (int i = 1; i <= reconnectAttempts; i++) {
                            try {
                                logger.info("第{}次尝试重新创建SMTP长连接", i);
                                Transport newTransport = emailServiceMonitorService.createSmtpLongConnection(account.getAccountId());
                                Session newSession = emailServiceMonitorService.getSmtpSession(account.getAccountId());
                                
                                if (newTransport != null && newTransport.isConnected() && isTransportHealthy(newTransport)) {
                                    currentTransport = newTransport;
                                    currentSession = newSession;
                                    reconnectSuccess = true;
                                    logger.info("第{}次重连成功，连接已恢复", i);
                                    break;
                                } else {
                                    logger.warn("第{}次重连失败，连接不健康", i);
                                }
                            } catch (Exception reconnectEx) {
                                logger.warn("第{}次重连过程中出现异常: {}", i, reconnectEx.getMessage());
                            }
                            
                            // 如果不是最后一次尝试，等待一下再重试
                            if (i < reconnectAttempts) {
                                try {
                                    Thread.sleep(1000 * i); // 递增等待时间
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        }
                        
                        if (!reconnectSuccess) {
                            logger.error("经过{}次尝试无法重新创建长连接，跳过当前邮件发送", reconnectAttempts);
                            continue;
                        }
                    }
                    
                    // 使用经过健康检查的长连接发送邮件
                    sendEmailWithTransport(currentSession, currentTransport, account, template, recipient, task);
                    
                    // 更新发送计数
                    synchronized (this) {
                        EmailSendTask latestTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                        latestTask.setSentCount(latestTask.getSentCount() + 1);
                        emailSendTaskService.updateEmailSendTask(latestTask);
                    }

                    logger.debug("长连接邮件发送成功: {} -> {}", account.getEmailAddress(), recipient.getEmail());

                    // 发送间隔
                    if (task.getSendInterval() != null && task.getSendInterval() > 0) {
                        Thread.sleep(task.getSendInterval() * 1000L);
                    }
                } catch (Exception e) {
                    logger.error("长连接发送邮件失败: {} -> {}, 错误: {}", account.getEmailAddress(), recipient.getEmail(), e.getMessage());
                    
                    // 如果是连接错误，尝试重新创建长连接
                    if (e.getMessage().contains("connection") || e.getMessage().contains("socket")) {
                        logger.info("连接错误，尝试重新创建长连接");
                        try {
                            Transport newTransport = emailServiceMonitorService.createSmtpLongConnection(account.getAccountId());
                            if (newTransport != null && newTransport.isConnected()) {
                                logger.info("长连接重新创建成功");
                            }
                        } catch (Exception reconnectEx) {
                            logger.error("重新创建长连接失败: {}", reconnectEx.getMessage());
                        }
                    }
                }
            }
        }, executorService);

        // 等待发送完成（注意：不关闭长连接，因为它由EmailServiceMonitorService管理）
        sendingTask.whenComplete((result, throwable) -> {
            try {
                EmailSendTask finalTask = emailSendTaskService.selectEmailSendTaskByTaskId(task.getTaskId());
                if (finalTask != null) {
                    if (throwable != null) {
                        logger.error("长连接邮件发送任务异常: {}", throwable.getMessage());
                        finalTask.setStatus("4"); // 失败
                    } else {
                        logger.info("长连接邮件发送任务完成: {}", task.getTaskId());
                        finalTask.setStatus("2"); // 完成
                    }
                    emailSendTaskService.updateEmailSendTask(finalTask);
                }
            } catch (Exception e) {
                logger.error("更新长连接任务状态失败: {}", e.getMessage());
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
}
