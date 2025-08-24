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
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        
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
            emailAccountService.updateEmailAccount(account);
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
}
