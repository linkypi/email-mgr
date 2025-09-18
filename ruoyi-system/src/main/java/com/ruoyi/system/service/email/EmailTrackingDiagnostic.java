package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * 邮件跟踪诊断服务
 * 用于诊断邮件跟踪功能是否正常工作
 */
@Service
public class EmailTrackingDiagnostic {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailTrackingDiagnostic.class);
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private ImapService imapService;
    
    /**
     * 检查最近的邮件跟踪记录
     */
    public void checkRecentTrackingRecords() {
        try {
            logger.info("=== 检查最近的邮件跟踪记录 ===");
            
            // 获取最近的跟踪记录
            EmailTrackRecord queryRecord = new EmailTrackRecord();
            List<EmailTrackRecord> recentRecords = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            if (recentRecords.size() > 10) {
                recentRecords = recentRecords.subList(0, 10);
            }
            
            if (recentRecords.isEmpty()) {
                logger.warn("没有找到最近的邮件跟踪记录");
                return;
            }
            
            logger.info("找到 {} 条最近的邮件跟踪记录:", recentRecords.size());
            for (EmailTrackRecord record : recentRecords) {
                logger.info("MessageID: {}, 状态: {}, 发送时间: {}, 送达时间: {}, 打开时间: {}", 
                           record.getMessageId(), 
                           record.getStatus(),
                           record.getSentTime(),
                           record.getDeliveredTime(),
                           record.getOpenedTime());
            }
            
        } catch (Exception e) {
            logger.error("检查邮件跟踪记录失败", e);
        }
    }
    
    /**
     * 检查IMAP连接状态
     */
    public void checkImapConnectionStatus() {
        try {
            logger.info("=== 检查IMAP连接状态 ===");
            
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            
            for (EmailAccount account : accounts) {
                if (!"0".equals(account.getStatus())) {
                    continue; // 跳过未启用的账户
                }
                
                try {
                    Store store = imapService.createPersistentConnection(account);
                    if (store != null && store.isConnected()) {
                        logger.info("账户 {} IMAP连接正常", account.getEmailAddress());
                        
                        // 检查收件箱
                        Folder folder = store.getFolder("INBOX");
                        if (folder.exists()) {
                            folder.open(Folder.READ_ONLY);
                            int messageCount = folder.getMessageCount();
                            logger.info("账户 {} 收件箱有 {} 封邮件", account.getEmailAddress(), messageCount);
                            folder.close(false);
                        }
                    } else {
                        logger.warn("账户 {} IMAP连接失败", account.getEmailAddress());
                    }
                } catch (Exception e) {
                    logger.error("检查账户 {} IMAP连接时出错", account.getEmailAddress(), e);
                }
            }
            
        } catch (Exception e) {
            logger.error("检查IMAP连接状态失败", e);
        }
    }
    
    /**
     * 扫描收件箱中的DSN邮件
     */
    public void scanInboxForDSNEmails() {
        try {
            logger.info("=== 扫描收件箱中的DSN邮件 ===");
            
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            
            for (EmailAccount account : accounts) {
                if (!"0".equals(account.getStatus())) {
                    continue; // 跳过未启用的账户
                }
                
                try {
                    Store store = imapService.createPersistentConnection(account);
                    if (store != null && store.isConnected()) {
                        Folder folder = store.getFolder("INBOX");
                        if (folder.exists()) {
                            folder.open(Folder.READ_ONLY);
                            
                            int messageCount = folder.getMessageCount();
                            if (messageCount > 0) {
                                // 检查最近的50封邮件
                                int startIndex = Math.max(1, messageCount - 50);
                                Message[] messages = folder.getMessages(startIndex, messageCount);
                                
                                int dsnCount = 0;
                                for (Message message : messages) {
                                    if (message instanceof MimeMessage) {
                                        MimeMessage mimeMessage = (MimeMessage) message;
                                        if (isDSNEmail(mimeMessage)) {
                                            dsnCount++;
                                            logger.info("发现DSN邮件: 主题={}, 发件人={}", 
                                                      mimeMessage.getSubject(), 
                                                      mimeMessage.getFrom()[0]);
                                        }
                                    }
                                }
                                
                                logger.info("账户 {} 收件箱中最近50封邮件中有 {} 封DSN邮件", 
                                           account.getEmailAddress(), dsnCount);
                            }
                            
                            folder.close(false);
                        }
                    }
                } catch (Exception e) {
                    logger.error("扫描账户 {} 收件箱时出错", account.getEmailAddress(), e);
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描收件箱DSN邮件失败", e);
        }
    }
    
    /**
     * 检查是否是DSN邮件
     */
    private boolean isDSNEmail(MimeMessage message) {
        try {
            String subject = message.getSubject();
            String contentType = message.getContentType();
            
            // 检查主题
            boolean subjectMatch = subject != null && (
                subject.toLowerCase().contains("delivery status notification") ||
                subject.toLowerCase().contains("mail delivery failure") ||
                subject.toLowerCase().contains("undelivered mail") ||
                subject.toLowerCase().contains("mail system error") ||
                subject.toLowerCase().contains("delivery failure")
            );
            
            // 检查内容类型
            boolean contentTypeMatch = contentType != null && (
                contentType.toLowerCase().contains("multipart/report") ||
                contentType.toLowerCase().contains("message/delivery-status")
            );
            
            return subjectMatch || contentTypeMatch;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查邮件跟踪监控状态
     */
    public void checkEmailTrackingMonitorStatus() {
        try {
            logger.info("=== 检查邮件跟踪监控状态 ===");
            
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            
            for (EmailAccount account : accounts) {
                if (!"0".equals(account.getStatus())) {
                    continue; // 跳过未启用的账户
                }
                
                // 检查是否有活跃的跟踪监控
                // 注意：这里需要根据实际的ImapService实现来调整
                logger.info("账户 {} 邮件跟踪监控状态: 需要检查ImapService实现", 
                           account.getEmailAddress());
            }
            
        } catch (Exception e) {
            logger.error("检查邮件跟踪监控状态失败", e);
        }
    }
    
    /**
     * 执行完整的诊断
     */
    public void performFullDiagnostic() {
        logger.info("开始执行邮件跟踪功能完整诊断...");
        
        checkRecentTrackingRecords();
        checkImapConnectionStatus();
        scanInboxForDSNEmails();
        checkEmailTrackingMonitorStatus();
        
        logger.info("邮件跟踪功能诊断完成");
    }
}