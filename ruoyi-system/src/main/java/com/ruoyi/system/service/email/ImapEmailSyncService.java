package com.ruoyi.system.service.email;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailStatistics;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.IEmailStatisticsService;
import com.ruoyi.system.service.email.IEmailTrackRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * IMAP邮件同步服务
 * 负责邮件同步、发送和跟踪功能，与实时监控服务集成
 */
@Service
public class ImapEmailSyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImapEmailSyncService.class);
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private IEmailStatisticsService emailStatisticsService;
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    @Autowired
    private SmtpService smtpService;
    
    @Autowired
    private ImapService imapService;
    
    // 线程池用于异步处理
    private final ScheduledExecutorService syncExecutor = Executors.newScheduledThreadPool(3);
    
    /**
     * 邮件状态枚举
     */
    public enum EmailStatus {
        PENDING("待发送"),
        SENDING("发送中"),
        SEND_SUCCESS("发送成功"),
        SEND_FAILED("发送失败"),
        DELIVERED("已送达"),
        OPENED("已打开"),
        REPLIED("已回复"),
        CLICKED("已点击");
        
        private final String description;
        
        EmailStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 同步邮件统计信息
     */
    public void syncEmailStatistics(EmailAccount account) {
        try {
            logger.info("开始同步邮件统计信息: {}", account.getEmailAddress());
            
            // 获取配置参数
            int syncDays = getSyncDays();
            int batchSize = getBatchSize();
            int maxMessages = getMaxMessages();
            
            // 计算同步开始时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -syncDays);
            Date syncStartDate = calendar.getTime();
            
            // 使用ImapService获取邮件
            ImapService.ImapMessageListResult result = imapService.getMessageList(account, maxMessages);
            
            if (!result.isSuccess()) {
                logger.error("获取邮件列表失败: {}", result.getErrorMessage());
                return;
            }
            
            try {
                Message[] messages = result.getMessages();
                logger.info("获取到 {} 封邮件，开始处理", messages.length);
                
                int processedCount = 0;
                for (Message message : messages) {
                    try {
                        if (message instanceof MimeMessage) {
                            MimeMessage mimeMessage = (MimeMessage) message;
                            
                            // 检查邮件日期
                            Date sentDate = mimeMessage.getSentDate();
                            if (sentDate != null && sentDate.before(syncStartDate)) {
                                continue; // 跳过过期的邮件
                            }
                            
                            // 处理邮件
                            processEmailMessage(mimeMessage, account);
                            processedCount++;
                            
                            // 批量处理控制
                            if (processedCount % batchSize == 0) {
                                logger.info("已处理 {} 封邮件", processedCount);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("处理邮件失败", e);
                    }
                }
                
                logger.info("邮件统计同步完成，共处理 {} 封邮件", processedCount);
                
                // 更新账户同步信息
                updateAccountSyncInfo(account, processedCount);
                
            } finally {
                // 确保资源被正确关闭
                result.closeResources();
            }
            
        } catch (Exception e) {
            logger.error("同步邮件统计信息失败", e);
        }
    }
    
    /**
     * 创建邮件统计记录
     */
    public void createEmailStatistics(Message message, EmailAccount account) {
        try {
            if (message instanceof MimeMessage) {
                MimeMessage mimeMessage = (MimeMessage) message;
                
                // 解析邮件信息
                String messageId = mimeMessage.getMessageID();
                String subject = decodeMimeText(mimeMessage.getSubject());
                String from = extractEmailAddress(mimeMessage.getFrom());
                String to = extractEmailAddress(mimeMessage.getRecipients(Message.RecipientType.TO));
                Date sentDate = mimeMessage.getSentDate();
                
                // 检查是否已存在
                EmailStatistics existingStats = emailStatisticsService.selectEmailStatisticsByMessageId(messageId);
                if (existingStats != null) {
                    logger.debug("邮件统计记录已存在: {}", messageId);
                    return;
                }
                
                // 创建新的统计记录
                EmailStatistics statistics = new EmailStatistics();
                statistics.setAccountId(account.getAccountId());
                statistics.setMessageId(messageId);
                // 注意：EmailStatistics可能没有这些字段，需要根据实际实体类调整
                // statistics.setSubject(subject);
                // statistics.setSender(from);
                // statistics.setRecipient(to);
                // statistics.setSentTime(sentDate);
                statistics.setStatus(EmailStatus.SEND_SUCCESS.name());
                statistics.setCreateBy("system");
                statistics.setCreateTime(new Date());
                
                emailStatisticsService.insertEmailStatistics(statistics);
                logger.debug("创建邮件统计记录: {}", messageId);
            }
        } catch (Exception e) {
            logger.error("创建邮件统计记录失败", e);
        }
    }
    
    /**
     * 发送带跟踪的邮件
     */
    public String sendEmailWithTracking(EmailAccount account, String to, String subject, 
                                      String content, Long taskId) {
        try {
            // 生成唯一的Message-ID
            String messageId = generateMessageId();
            
            // 创建跟踪记录
            EmailTrackRecord trackRecord = new EmailTrackRecord();
            trackRecord.setTaskId(taskId);
            trackRecord.setMessageId(messageId);
            trackRecord.setSubject(subject);
            trackRecord.setRecipient(to);
            trackRecord.setSender(account.getEmailAddress());
            trackRecord.setContent(content);
            trackRecord.setStatus(EmailStatus.PENDING.name());
            trackRecord.setSentTime(new Date());
            trackRecord.setAccountId(account.getAccountId());
            trackRecord.setCreateBy("system");
            trackRecord.setCreateTime(new Date());
            
            // 生成跟踪URL
            String trackingPixelUrl = generateTrackingPixelUrl(messageId);
            String trackingLinkUrl = generateTrackingLinkUrl(messageId);
            trackRecord.setTrackingPixelUrl(trackingPixelUrl);
            trackRecord.setTrackingLinkUrl(trackingLinkUrl);
            
            // 保存跟踪记录
            emailTrackRecordService.insertEmailTrackRecord(trackRecord);
            
            // 更新状态为发送中
            emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SENDING.name());
            
            // 发送邮件
            SmtpService.SmtpSendResult result = smtpService.sendEmail(account, to, subject, 
                addTrackingToContent(content, trackingPixelUrl, trackingLinkUrl), messageId);
            
            if (result.isSuccess()) {
                // 更新状态为发送成功
                emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_SUCCESS.name());
                emailTrackRecordService.recordEmailDelivered(messageId);
                logger.info("邮件发送成功: {} -> {}", account.getEmailAddress(), to);
            } else {
                // 更新状态为发送失败
                emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_FAILED.name());
                logger.error("邮件发送失败: {} -> {}, 错误: {}", account.getEmailAddress(), to, result.getErrorMessage());
            }
            
            return messageId;
            
        } catch (Exception e) {
            logger.error("发送带跟踪的邮件失败", e);
            return null;
        }
    }
    
    /**
     * 启动IMAP监听器
     */
    public void startImapListener(EmailAccount account) {
        logger.info("启动IMAP监听器: {}", account.getEmailAddress());
        
        // 注意：实际的IMAP监听现在由EmailServiceMonitorService负责
        // 这里只是记录启动事件
        try {
            // 更新账户最后同步时间
            account.setLastSyncTime(DateUtils.getTime());
            emailAccountService.updateEmailAccount(account);
            
            logger.info("IMAP监听器启动成功: {}", account.getEmailAddress());
        } catch (Exception e) {
            logger.error("启动IMAP监听器失败: {}", account.getEmailAddress(), e);
        }
    }
    
    /**
     * 处理收到的邮件
     */
    public void processIncomingMessage(Message message, EmailAccount account) {
        try {
            if (message instanceof MimeMessage) {
                MimeMessage mimeMessage = (MimeMessage) message;
                
                // 解析邮件信息
                String messageId = mimeMessage.getMessageID();
                String subject = decodeMimeText(mimeMessage.getSubject());
                String from = extractEmailAddress(mimeMessage.getFrom());
                String to = extractEmailAddress(mimeMessage.getRecipients(Message.RecipientType.TO));
                
                logger.info("处理收到的邮件: {} - {}", subject, messageId);
                
                // 检查是否是回复邮件
                if (isReplyMessage(mimeMessage)) {
                    // 更新原始邮件的回复状态
                    emailTrackRecordService.recordEmailReplied(messageId);
                    logger.info("检测到邮件回复: {}", messageId);
                }
                
                // 检查是否是送达状态通知
                if (isDeliveryStatusNotification(mimeMessage)) {
                    processDeliveryStatusNotification(mimeMessage, account);
                }
                
                // 创建邮件统计记录
                createEmailStatistics(message, account);
                
            }
        } catch (Exception e) {
            logger.error("处理收到的邮件失败", e);
        }
    }
    
    /**
     * 解析送达状态通知
     */
    private void processDeliveryStatusNotification(Message message, EmailAccount account) {
        try {
            // TODO: 实现DSN解析逻辑
            // 这里需要解析DSN邮件内容，提取原始Message-ID和送达状态
            logger.info("处理送达状态通知");
        } catch (Exception e) {
            logger.error("处理送达状态通知失败", e);
        }
    }
    
    /**
     * 提取原始Message-ID
     */
    private String extractOriginalMessageId(Message message) {
        try {
            // 从邮件头或内容中提取原始Message-ID
            String[] headers = message.getHeader("X-Original-Message-ID");
            if (headers != null && headers.length > 0) {
                return headers[0];
            }
            
            // 从邮件内容中提取
            // TODO: 实现从邮件内容中提取Message-ID的逻辑
            
            return null;
        } catch (Exception e) {
            logger.error("提取原始Message-ID失败", e);
            return null;
        }
    }
    
    /**
     * 更新邮件状态
     */
    public void updateEmailStatus(String messageId, EmailStatus status) {
        try {
            emailTrackRecordService.updateEmailStatus(messageId, status.name());
            logger.debug("更新邮件状态: {} -> {}", messageId, status.name());
        } catch (Exception e) {
            logger.error("更新邮件状态失败: {}", messageId, e);
        }
    }
    
    /**
     * 记录邮件已打开
     */
    public void recordEmailOpened(String messageId) {
        try {
            emailTrackRecordService.recordEmailOpened(messageId);
            logger.info("记录邮件已打开: {}", messageId);
        } catch (Exception e) {
            logger.error("记录邮件已打开失败: {}", messageId, e);
        }
    }
    
    /**
     * 记录邮件已点击
     */
    public void recordEmailClicked(String messageId) {
        try {
            emailTrackRecordService.recordEmailClicked(messageId);
            logger.info("记录邮件已点击: {}", messageId);
        } catch (Exception e) {
            logger.error("记录邮件已点击失败: {}", messageId, e);
        }
    }
    
    /**
     * 获取跟踪记录
     */
    public EmailTrackRecord getTrackRecord(String messageId) {
        try {
            return emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
        } catch (Exception e) {
            logger.error("获取跟踪记录失败: {}", messageId, e);
            return null;
        }
    }
    
    /**
     * 停止服务
     */
    public void stop() {
        try {
            syncExecutor.shutdown();
            if (!syncExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                syncExecutor.shutdownNow();
            }
            logger.info("IMAP邮件同步服务已停止");
        } catch (Exception e) {
            logger.error("停止IMAP邮件同步服务失败", e);
        }
    }
    
    // 辅助方法
    
    /**
     * 处理邮件消息
     */
    private void processEmailMessage(MimeMessage message, EmailAccount account) {
        try {
            // 创建邮件统计记录
            createEmailStatistics(message, account);
            
            // 检查是否需要更新跟踪记录
            String messageId = message.getMessageID();
            if (messageId != null) {
                EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
                if (trackRecord != null) {
                    // 更新送达时间
                    if (trackRecord.getDeliveredTime() == null) {
                        emailTrackRecordService.recordEmailDelivered(messageId);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("处理邮件消息失败", e);
        }
    }
    
    /**
     * 解码MIME文本
     */
    private String decodeMimeText(String text) {
        if (text == null) {
            return "";
        }
        try {
            return MimeUtility.decodeText(text);
        } catch (UnsupportedEncodingException e) {
            logger.warn("MIME解码失败: {}", text, e);
            return text;
        }
    }
    
    /**
     * 解码MIME地址
     */
    private String decodeMimeAddress(String address) {
        if (address == null) {
            return "";
        }
        try {
            return MimeUtility.decodeText(address);
        } catch (UnsupportedEncodingException e) {
            logger.warn("MIME地址解码失败: {}", address, e);
            return address;
        }
    }
    
    /**
     * 提取邮件地址
     */
    private String extractEmailAddress(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "";
        }
        String address = addresses[0].toString();
        return decodeMimeAddress(address);
    }
    
    /**
     * 检查是否是回复邮件
     */
    private boolean isReplyMessage(Message message) throws MessagingException {
        String subject = message.getSubject();
        return subject != null && (subject.startsWith("Re:") || subject.startsWith("回复:"));
    }
    
    /**
     * 检查是否是送达状态通知
     */
    private boolean isDeliveryStatusNotification(Message message) throws MessagingException {
        String subject = message.getSubject();
        return subject != null && subject.contains("Delivery Status Notification");
    }
    
    /**
     * 生成Message-ID
     */
    private String generateMessageId() {
        return "<" + UUID.randomUUID().toString() + "@" + System.currentTimeMillis() + ">";
    }
    
    /**
     * 生成跟踪像素URL
     */
    private String generateTrackingPixelUrl(String messageId) {
        return "/api/email/tracking/open?mid=" + messageId;
    }
    
    /**
     * 生成跟踪链接URL
     */
    private String generateTrackingLinkUrl(String messageId) {
        return "/api/email/tracking/click?mid=" + messageId;
    }
    
    /**
     * 在邮件内容中添加跟踪
     */
    private String addTrackingToContent(String content, String pixelUrl, String linkUrl) {
        StringBuilder trackedContent = new StringBuilder(content);
        
        // 添加跟踪像素
        trackedContent.append("\n\n<img src=\"").append(pixelUrl).append("\" width=\"1\" height=\"1\" style=\"display:none;\">");
        
        // 添加跟踪链接（这里只是示例，实际应用中需要替换邮件中的链接）
        // TODO: 实现链接替换逻辑
        
        return trackedContent.toString();
    }
    
    /**
     * 获取同步天数配置
     */
    private int getSyncDays() {
        // TODO: 从sys_config表获取配置
        return 7; // 默认7天
    }
    
    /**
     * 获取批处理大小配置
     */
    private int getBatchSize() {
        // TODO: 从sys_config表获取配置
        return 100; // 默认100
    }
    
    /**
     * 获取最大邮件数量配置
     */
    private int getMaxMessages() {
        // TODO: 从sys_config表获取配置
        return 1000; // 默认1000
    }
    
    /**
     * 更新账户同步信息
     */
    private void updateAccountSyncInfo(EmailAccount account, int syncCount) {
        try {
            // 只更新最后同步时间，同步计数现在由EmailServiceMonitor表管理
            account.setLastSyncTime(DateUtils.getTime());
            emailAccountService.updateEmailAccount(account);
            
            logger.debug("更新账户 {} 同步时间: {}", account.getEmailAddress(), account.getLastSyncTime());
        } catch (Exception e) {
            logger.error("更新账户同步信息失败", e);
        }
    }
    
    /**
     * 批量同步所有邮箱账号的邮件统计数据
     */
    public void syncAllEmailAccounts(List<EmailAccount> emailAccounts) {
        logger.info("开始批量同步 {} 个邮箱账号的邮件统计数据", emailAccounts.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (EmailAccount account : emailAccounts) {
            try {
                logger.info("开始同步邮箱账号: {}", account.getEmailAddress());
                syncEmailStatistics(account);
                successCount++;
                logger.info("邮箱账号 {} 同步完成", account.getEmailAddress());
            } catch (Exception e) {
                failCount++;
                logger.error("邮箱账号 {} 同步失败: {}", account.getEmailAddress(), e.getMessage(), e);
            }
        }
        
        logger.info("批量同步完成，成功: {} 个，失败: {} 个", successCount, failCount);
    }
}
