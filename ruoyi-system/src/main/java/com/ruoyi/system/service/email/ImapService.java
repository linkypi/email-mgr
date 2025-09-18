package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.SearchTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.ComparisonTerm;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * IMAP服务
 * 提供IMAP连接测试、邮件获取和实时监控功能
 */
@Service
public class ImapService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImapService.class);
    
    // 邮件跟踪监控相关
    private final Map<Long, EmailTrackingMonitor> trackingMonitors = new ConcurrentHashMap<>();
    private final ScheduledExecutorService trackingExecutor = Executors.newScheduledThreadPool(5);
    private volatile boolean serviceShutdown = false;
    
    // DSN邮件模式匹配 - 增强版本
    private static final Pattern DSN_ORIGINAL_MESSAGE_ID_PATTERN = Pattern.compile(
        "(?:Original-Message-ID|Message-ID):\\s*<([^>]+)>", Pattern.CASE_INSENSITIVE);
    private static final Pattern DSN_STATUS_PATTERN = Pattern.compile(
        "(?:Status|Diagnostic-Code):\\s*(\\d+\\.\\d+\\.\\d+|[^\\n\\r]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DSN_RECIPIENT_PATTERN = Pattern.compile(
        "(?:Final-Recipient|Original-Recipient):\\s*(?:rfc822;\\s*)?([^\\s\\n\\r]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DSN_ACTION_PATTERN = Pattern.compile(
        "Action:\\s*([^\\n\\r]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DSN_REASON_PATTERN = Pattern.compile(
        "(?:Reason|Diagnostic-Code):\\s*([^\\n\\r]+)", Pattern.CASE_INSENSITIVE);
    
    // 回复邮件模式匹配
    private static final Pattern REPLY_PATTERN = Pattern.compile(
        "In-Reply-To:\\s*<([^>]+)>", Pattern.CASE_INSENSITIVE);
    
    /**
     * 测试IMAP连接
     */
    public ImapTestResult testImapConnection(EmailAccount account) {
        long startTime = System.currentTimeMillis();
        
        try {
            Properties props = createImapProperties(account);
            Session session = Session.getInstance(props);
            session.setDebug(false);
            
            Store store = session.getStore("imap");
            store.connect(account.getImapHost(), account.getImapPort(), 
                account.getImapUsername(), account.getImapPassword());
            
            // 测试获取文件夹
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            long connectionTime = System.currentTimeMillis() - startTime;
            
            // 获取邮件数量
            int messageCount = folder.getMessageCount();
            
            folder.close(false);
            store.close();
            
            return new ImapTestResult(true, "连接成功，收件箱共有 " + messageCount + " 封邮件", 
                null, connectionTime);
            
        } catch (Exception e) {
            long connectionTime = System.currentTimeMillis() - startTime;
            logger.error("IMAP连接测试失败: {}", e.getMessage(), e);
            return new ImapTestResult(false, "连接失败: " + e.getMessage(), 
                e.getMessage(), connectionTime);
        }
    }
    
    /**
     * 获取邮件列表
     */
    public ImapMessageListResult getMessageList(EmailAccount account, int maxMessages) {
        Store store = null;
        Folder folder = null;
        
        try {
            Properties props = createImapProperties(account);
            Session session = Session.getInstance(props);
            session.setDebug(false);
            
            store = session.getStore("imap");
            store.connect(account.getImapHost(), account.getImapPort(), 
                account.getImapUsername(), account.getImapPassword());
            
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            int totalMessages = folder.getMessageCount();
            int startIndex = Math.max(1, totalMessages - maxMessages + 1);
            
            Message[] messages = folder.getMessages(startIndex, totalMessages);
            
            // 预加载邮件内容，避免FolderClosedException
            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            fetchProfile.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fetchProfile);
            
            ImapMessageListResult result = new ImapMessageListResult();
            result.setSuccess(true);
            result.setMessages(messages);
            result.setTotalCount(totalMessages);
            result.setRetrievedCount(messages.length);
            result.setStore(store);
            result.setFolder(folder);
            
            return result;
            
        } catch (Exception e) {
            logger.error("获取邮件列表失败: {}", e.getMessage(), e);
            
            // 确保资源被正确关闭
            if (folder != null) {
                try {
                    folder.close(false);
                } catch (Exception ex) {
                    logger.warn("关闭文件夹失败: {}", ex.getMessage());
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception ex) {
                    logger.warn("关闭存储失败: {}", ex.getMessage());
                }
            }
            
            ImapMessageListResult result = new ImapMessageListResult();
            result.setSuccess(false);
            result.setErrorMessage("获取邮件列表失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 创建IMAP连接属性
     */
    public Properties createImapProperties(EmailAccount account) {
        Properties props = new Properties();
        
        // 基本连接设置
        props.setProperty("mail.imap.host", account.getImapHost());
        props.setProperty("mail.imap.port", String.valueOf(account.getImapPort()));
        props.setProperty("mail.imap.username", account.getImapUsername());
        props.setProperty("mail.imap.password", account.getImapPassword());
        
        // SSL/TLS设置 - 根据端口自动判断
        int port = account.getImapPort();
        if (port == 993) {
            // SSL端口
            props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.imap.socketFactory.port", String.valueOf(port));
            props.setProperty("mail.imap.socketFactory.fallback", "false");
            props.setProperty("mail.imap.ssl.enable", "true");
            props.setProperty("mail.imap.ssl.trust", "*");
        } else if (port == 143) {
            // STARTTLS端口
            props.setProperty("mail.imap.starttls.enable", "true");
            props.setProperty("mail.imap.starttls.required", "true");
            props.setProperty("mail.imap.ssl.trust", "*");
        } else if ("1".equals(account.getImapSsl())) {
            // 手动指定SSL
            props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.imap.socketFactory.port", String.valueOf(port));
            props.setProperty("mail.imap.socketFactory.fallback", "false");
            props.setProperty("mail.imap.ssl.enable", "true");
            props.setProperty("mail.imap.ssl.trust", "*");
        } else {
            // 默认STARTTLS
            props.setProperty("mail.imap.starttls.enable", "true");
            props.setProperty("mail.imap.starttls.required", "true");
            props.setProperty("mail.imap.ssl.trust", "*");
        }
        
        // 连接超时设置 - 针对监控场景优化
        props.setProperty("mail.imap.connectiontimeout", "30000"); // 30秒连接超时
        props.setProperty("mail.imap.timeout", "60000"); // 60秒读取超时
        props.setProperty("mail.imap.writetimeout", "30000"); // 30秒写入超时
        
        // IDLE支持设置
        props.setProperty("mail.imap.idle.enable", "true");
        props.setProperty("mail.imap.idle.timeout", "600000"); // 10分钟IDLE超时
        
        // 连接池和重试设置
        props.setProperty("mail.imap.connectionpoolsize", "5");
        props.setProperty("mail.imap.connectionpooltimeout", "300000"); // 5分钟连接池超时
        
        // 其他优化设置
        props.setProperty("mail.imap.peek", "true"); // 不标记邮件为已读
        props.setProperty("mail.imap.allowreadonlyselect", "true");
        props.setProperty("mail.imap.auth.plain.disable", "false");
        props.setProperty("mail.imap.auth.login.disable", "false");
        
        return props;
    }
    
    /**
     * 创建持久IMAP连接（用于实时监控）
     */
    public Store createPersistentConnection(EmailAccount account) throws MessagingException {
        Properties props = createImapProperties(account);
        Session session = Session.getInstance(props);
        session.setDebug(false);
        
        Store store = session.getStore("imap");
        store.connect(account.getImapHost(), account.getImapPort(), 
            account.getImapUsername(), account.getImapPassword());
        
        return store;
    }
    
    /**
     * 打开收件箱文件夹
     */
    public Folder openInboxFolder(Store store) throws MessagingException {
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        return folder;
    }
    
    /**
     * 添加消息监听器
     */
    public void addMessageListener(Folder folder, MessageListener listener) {
        folder.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent evt) {
                Message[] messages = evt.getMessages();
                listener.onNewMessages(messages);
            }
        });
    }
    
    /**
     * 启动IDLE监听
     */
    public void startIdleListening(Folder folder, IdleListener listener) {
        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 检查是否有新邮件
                        if (folder.hasNewMessages()) {
                            Message[] newMessages = folder.getMessages();
                            listener.onNewMessages(newMessages);
                        }
                        
                        // 等待一段时间后再次检查
                        Thread.sleep(5000); // 5秒检查一次
                        
                        // 更新最后活动时间
                        listener.onIdleUpdate(new Date());
                        
                    } catch (MessagingException e) {
                        if (!Thread.currentThread().isInterrupted()) {
                            logger.warn("IMAP监听异常，尝试重新连接: {}", e.getMessage());
                            listener.onIdleError(e);
                            Thread.sleep(5000); // 等待5秒后重试
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("IMAP监听被中断");
            } catch (Exception e) {
                logger.error("IMAP监听异常", e);
                listener.onIdleError(e);
            }
        }, "IMAP-IDLE-Listener").start();
    }
    
    /**
     * 解析邮件信息
     */
    public EmailMessageInfo parseMessageInfo(Message message) {
        try {
            if (message instanceof MimeMessage) {
                MimeMessage mimeMessage = (MimeMessage) message;
                
                EmailMessageInfo info = new EmailMessageInfo();
                info.setMessageId(mimeMessage.getMessageID());
                info.setSubject(mimeMessage.getSubject());
                info.setFrom(extractEmailAddress(mimeMessage.getFrom()));
                info.setTo(extractEmailAddress(mimeMessage.getRecipients(Message.RecipientType.TO)));
                info.setCc(extractEmailAddress(mimeMessage.getRecipients(Message.RecipientType.CC)));
                info.setBcc(extractEmailAddress(mimeMessage.getRecipients(Message.RecipientType.BCC)));
                info.setSentDate(mimeMessage.getSentDate());
                info.setReceivedDate(mimeMessage.getReceivedDate());
                info.setSize(mimeMessage.getSize());
                info.setFlags(mimeMessage.getFlags());
                
                return info;
            }
        } catch (Exception e) {
            logger.error("解析邮件信息失败", e);
        }
        
        return null;
    }
    
    /**
     * 提取邮件地址
     */
    private String extractEmailAddress(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "";
        }
        return addresses[0].toString();
    }
    
    /**
     * IMAP测试结果
     */
    public static class ImapTestResult {
        private boolean success;
        private String message;
        private String errorMessage;
        private long connectionTime;
        
        public ImapTestResult(boolean success, String message, String errorMessage, long connectionTime) {
            this.success = success;
            this.message = message;
            this.errorMessage = errorMessage;
            this.connectionTime = connectionTime;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public long getConnectionTime() { return connectionTime; }
        public void setConnectionTime(long connectionTime) { this.connectionTime = connectionTime; }
    }
    
    /**
     * IMAP邮件列表结果
     */
    public static class ImapMessageListResult {
        private boolean success;
        private Message[] messages;
        private int totalCount;
        private int retrievedCount;
        private String errorMessage;
        private Store store;
        private Folder folder;
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Message[] getMessages() { return messages; }
        public void setMessages(Message[] messages) { this.messages = messages; }
        
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public int getRetrievedCount() { return retrievedCount; }
        public void setRetrievedCount(int retrievedCount) { this.retrievedCount = retrievedCount; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public Store getStore() { return store; }
        public void setStore(Store store) { this.store = store; }
        
        public Folder getFolder() { return folder; }
        public void setFolder(Folder folder) { this.folder = folder; }
        
        /**
         * 关闭资源
         */
        public void closeResources() {
            if (folder != null) {
                try {
                    folder.close(false);
                } catch (Exception e) {
                    // 忽略关闭错误
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) {
                    // 忽略关闭错误
                }
            }
        }
    }
    
    /**
     * 邮件信息
     */
    public static class EmailMessageInfo {
        private String messageId;
        private String subject;
        private String from;
        private String to;
        private String cc;
        private String bcc;
        private Date sentDate;
        private Date receivedDate;
        private int size;
        private Flags flags;
        
        // Getters and setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        
        public String getCc() { return cc; }
        public void setCc(String cc) { this.cc = cc; }
        
        public String getBcc() { return bcc; }
        public void setBcc(String bcc) { this.bcc = bcc; }
        
        public Date getSentDate() { return sentDate; }
        public void setSentDate(Date sentDate) { this.sentDate = sentDate; }
        
        public Date getReceivedDate() { return receivedDate; }
        public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }
        
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        
        public Flags getFlags() { return flags; }
        public void setFlags(Flags flags) { this.flags = flags; }
    }
    
    /**
     * 消息监听器接口
     */
    public interface MessageListener {
        void onNewMessages(Message[] messages);
    }
    
    /**
     * IDLE监听器接口
     */
    public interface IdleListener {
        void onNewMessages(Message[] messages);
        void onIdleUpdate(Date lastUpdate);
        void onIdleError(Exception e);
    }
    
    // ==================== 邮件跟踪监控功能 ====================
    
    /**
     * 启动邮件跟踪监控
     */
    public void startEmailTracking(EmailAccount account, EmailTrackingCallback callback) {
        if (serviceShutdown) {
            logger.warn("服务正在关闭，无法启动邮件跟踪监控: {}", account.getEmailAddress());
            return;
        }
        
        Long accountId = account.getAccountId();
        
        if (trackingMonitors.containsKey(accountId)) {
            logger.info("邮件跟踪监控已启动: {}", account.getEmailAddress());
            return;
        }
        
        EmailTrackingMonitor monitor = new EmailTrackingMonitor(account, callback);
        trackingMonitors.put(accountId, monitor);
        
        // 启动监控任务 - 使用智能扫描频率
        trackingExecutor.scheduleWithFixedDelay(() -> {
            try {
                if (!serviceShutdown && monitor.isRunning()) {
                    performEmailTracking(monitor);
                    
                    // 定期清理过期缓存
                    if (System.currentTimeMillis() % (5 * 60 * 1000) < 30000) { // 每5分钟清理一次
                        monitor.cleanupExpiredCache();
                    }
                }
            } catch (Exception e) {
                if (!serviceShutdown) {
                    logger.error("邮件跟踪监控异常: {}", account.getEmailAddress(), e);
                }
            }
        }, 0, getTrackingInterval(monitor), TimeUnit.SECONDS);
        
        logger.info("启动邮件跟踪监控: {}", account.getEmailAddress());
    }
    
    /**
     * 获取智能扫描间隔
     */
    private int getTrackingInterval(EmailTrackingMonitor monitor) {
        // 根据监控统计动态调整扫描频率
        ImapService.EmailTrackingStats stats = monitor.getStats();
        
        // 如果最近有活动，增加扫描频率
        if (stats.getDsnCount() > 0 || stats.getReplyCount() > 0 || stats.getStatusChangeCount() > 0) {
            return 15; // 15秒扫描一次
        }
        
        // 默认30秒扫描一次
        return 30;
    }
    
    /**
     * 停止邮件跟踪监控
     */
    public void stopEmailTracking(Long accountId) {
        EmailTrackingMonitor monitor = trackingMonitors.remove(accountId);
        if (monitor != null) {
            monitor.stop();
            logger.info("停止邮件跟踪监控: {}", monitor.getAccount().getEmailAddress());
        }
    }
    
    /**
     * 执行邮件跟踪监控
     */
    private void performEmailTracking(EmailTrackingMonitor monitor) {
        if (!monitor.isRunning() || serviceShutdown) {
            return;
        }
        
        EmailAccount account = monitor.getAccount();
        Store store = null;
        Folder folder = null;
        
        try {
            logger.debug("开始执行邮件跟踪监控: {}", account.getEmailAddress());
            
            // 建立IMAP连接
            store = createPersistentConnection(account);
            if (store == null || !store.isConnected()) {
                logger.warn("IMAP连接失败或已断开: {}", account.getEmailAddress());
                return;
            }
            
            folder = openInboxFolder(store);
            if (folder == null || !folder.isOpen()) {
                logger.warn("无法打开收件箱: {}", account.getEmailAddress());
                return;
            }
            
            logger.debug("IMAP连接成功，开始扫描邮件: {}", account.getEmailAddress());
            
            // 扫描DSN邮件
            scanDSNEmails(folder, monitor);
            
            // 扫描回复邮件
            scanReplyEmails(folder, monitor);
            
            // 扫描邮件状态变化
            scanEmailStatusChanges(folder, monitor);
            
            // 扫描已发送文件夹，检测邮件送达状态
            scanSentFolderForDeliveryConfirmation(store, monitor);
            
            // 扫描退信邮件
            scanBounceEmails(folder, monitor);
            
            monitor.updateLastCheckTime();
            
            logger.debug("邮件跟踪监控完成: {}", account.getEmailAddress());
            
        } catch (Exception e) {
            if (!serviceShutdown) {
                logger.error("邮件跟踪监控失败: {} - {}", account.getEmailAddress(), e.getMessage(), e);
                try {
                    monitor.getCallback().onTrackingError(account, e);
                } catch (Exception callbackException) {
                    logger.error("触发跟踪错误回调失败", callbackException);
                }
            }
        } finally {
            // 保持连接打开，不关闭
            // 这样可以避免频繁建立连接的开销
            if (folder != null && folder.isOpen()) {
                try {
                    // 只关闭文件夹，保持存储连接
                    folder.close(false);
                } catch (Exception e) {
                    if (!serviceShutdown) {
                        logger.warn("关闭文件夹失败", e);
                    }
                }
            }
            // 不关闭store连接，保持持久连接
        }
    }
    
    /**
     * 扫描DSN邮件
     */
    private void scanDSNEmails(Folder folder, EmailTrackingMonitor monitor) {
        try {
            // 获取最近的邮件
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                return;
            }
            
            // 检查最近200封邮件，提高检测覆盖率
            int startIndex = Math.max(1, messageCount - 200);
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    
                    // 检查是否是DSN邮件
                    if (isDSNEmail(mimeMessage)) {
                        DSNInfo dsnInfo = parseDSNEmail(mimeMessage);
                        if (dsnInfo != null) {
                            // 检查是否已经处理过这个DSN邮件
                            if (!monitor.isDSNProcessed(dsnInfo.getDsnMessageId())) {
                                logger.info("检测到DSN邮件: {} - 状态: {} - 原始邮件: {}", 
                                           dsnInfo.getDsnMessageId(), dsnInfo.getStatus(), 
                                           dsnInfo.getOriginalMessageId());
                                monitor.getCallback().onDSNReceived(monitor.getAccount(), dsnInfo);
                                monitor.markDSNProcessed(dsnInfo.getDsnMessageId());
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描DSN邮件失败", e);
        }
    }
    
    /**
     * 扫描回复邮件
     */
    private void scanReplyEmails(Folder folder, EmailTrackingMonitor monitor) {
        try {
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                return;
            }
            
            int startIndex = Math.max(1, messageCount - 200);
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String messageId = mimeMessage.getMessageID();
                    
                    // 检查是否已经处理过这个回复邮件
                    if (monitor.isReplyProcessed(messageId)) {
                        continue;
                    }
                    
                    // 检查是否是回复邮件 - 多种方式检测
                    String originalMessageId = null;
                    String detectionMethod = "";
                    
                    // 方式1: 检查In-Reply-To头
                    String inReplyTo = mimeMessage.getHeader("In-Reply-To", null);
                    if (inReplyTo != null && !inReplyTo.trim().isEmpty()) {
                        originalMessageId = extractMessageIdFromHeader(inReplyTo);
                        detectionMethod = "In-Reply-To";
                    }
                    
                    // 方式2: 检查References头
                    if (originalMessageId == null) {
                        String references = mimeMessage.getHeader("References", null);
                        if (references != null && !references.trim().isEmpty()) {
                            originalMessageId = extractMessageIdFromHeader(references);
                            detectionMethod = "References";
                        }
                    }
                    
                    // 方式3: 检查主题是否包含Re:或回复标识
                    if (originalMessageId == null) {
                        String subject = mimeMessage.getSubject();
                        if (subject != null && (subject.toLowerCase().startsWith("re:") || 
                                               subject.toLowerCase().startsWith("回复:") ||
                                               subject.toLowerCase().contains("reply"))) {
                            // 尝试从主题中提取原始邮件ID（如果主题包含）
                            originalMessageId = extractMessageIdFromSubject(subject);
                            detectionMethod = "Subject";
                        }
                    }
                    
                    if (originalMessageId != null) {
                        logger.info("检测到回复邮件: {} -> {} (检测方式: {})", 
                                   originalMessageId, messageId, detectionMethod);
                        monitor.getCallback().onReplyReceived(monitor.getAccount(), 
                                                            originalMessageId, mimeMessage);
                        monitor.markReplyProcessed(messageId);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描回复邮件失败", e);
        }
    }
    
    /**
     * 扫描已发送文件夹，检测邮件送达状态
     */
    private void scanSentFolderForDeliveryConfirmation(Store store, EmailTrackingMonitor monitor) {
        if (store == null || !store.isConnected()) {
            return;
        }
        
        EmailAccount account = monitor.getAccount();
        Folder sentFolder = null;
        
        try {
            // 尝试打开已发送文件夹
            String[] sentFolderNames = {"Sent", "Sent Messages", "已发送", "已发送邮件", "Sent Items"};
            for (String folderName : sentFolderNames) {
                try {
                    sentFolder = store.getFolder(folderName);
                    if (sentFolder.exists()) {
                        break;
                    }
                } catch (Exception e) {
                    // 继续尝试下一个文件夹名
                }
            }
            
            if (sentFolder == null || !sentFolder.exists()) {
                logger.debug("未找到已发送文件夹: {}", account.getEmailAddress());
                return;
            }
            
            if (!sentFolder.isOpen()) {
                sentFolder.open(Folder.READ_ONLY);
            }
            
            logger.debug("开始扫描已发送文件夹: {}, 邮件数量: {}", 
                sentFolder.getName(), sentFolder.getMessageCount());
            
            // 获取最近24小时内的邮件
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = calendar.getTime();
            
            Message[] messages = sentFolder.search(
                new ReceivedDateTerm(ComparisonTerm.GE, yesterday)
            );
            
            for (Message message : messages) {
                try {
                    processSentMessageForDeliveryConfirmation(message, monitor);
                } catch (Exception e) {
                    logger.warn("处理已发送邮件失败: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.warn("扫描已发送文件夹失败: {} - {}", account.getEmailAddress(), e.getMessage());
        } finally {
            if (sentFolder != null && sentFolder.isOpen()) {
                try {
                    sentFolder.close(false);
                } catch (Exception e) {
                    logger.warn("关闭已发送文件夹失败: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * 处理已发送邮件，确认送达状态
     */
    private void processSentMessageForDeliveryConfirmation(Message message, EmailTrackingMonitor monitor) {
        try {
            String messageId = getMessageId(message);
            if (messageId == null) {
                return;
            }
            
            // 检查是否是我们发送的邮件
            if (!isOurSentMessage(message, monitor.getAccount())) {
                return;
            }
            
            logger.debug("发现我们发送的邮件在已发送文件夹中: MessageID={}", messageId);
            
            // 通知回调：邮件已送达
            if (monitor.getCallback() != null) {
                monitor.getCallback().onDeliveryConfirmed(monitor.getAccount(), messageId, message);
            }
            
        } catch (Exception e) {
            logger.warn("处理已发送邮件确认失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取邮件的MessageID
     */
    private String getMessageId(Message message) {
        try {
            String[] messageIds = message.getHeader("Message-ID");
            if (messageIds != null && messageIds.length > 0) {
                return messageIds[0];
            }
            return null;
        } catch (Exception e) {
            logger.warn("获取MessageID失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从邮件内容中提取MessageID
     */
    private String extractMessageIdFromContent(String content) {
        if (content == null) return null;
        
        // 多种正则表达式模式尝试匹配Message-ID
        String[] patterns = {
            "Message-ID:\\s*(<[^>]+>)",
            "Message-ID:\\s*([^\\s]+)",
            "Message-ID\\s*:\\s*(<[^>]+>)",
            "Message-ID\\s*:\\s*([^\\s]+)"
        };
        
        for (String pattern : patterns) {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        return null;
    }
    
    /**
     * 检查是否是我们发送的邮件
     */
    private boolean isOurSentMessage(Message message, EmailAccount account) {
        try {
            // 检查发件人是否匹配
            Address[] fromAddresses = message.getFrom();
            if (fromAddresses != null && fromAddresses.length > 0) {
                String fromEmail = fromAddresses[0].toString();
                if (fromEmail.contains(account.getEmailAddress())) {
                    return true;
                }
            }
            
            // 检查Message-ID是否包含我们的域名
            String messageId = getMessageId(message);
            if (messageId != null && messageId.contains(account.getEmailAddress().split("@")[1])) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.warn("检查邮件归属失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 扫描退信邮件
     */
    private void scanBounceEmails(Folder folder, EmailTrackingMonitor monitor) {
        try {
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                return;
            }
            
            // 检查最近100封邮件中的退信
            int startIndex = Math.max(1, messageCount - 100);
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    
                    // 检查是否是退信邮件
                    if (isBounceEmail(mimeMessage)) {
                        BounceInfo bounceInfo = parseBounceEmail(mimeMessage);
                        if (bounceInfo != null) {
                            // 检查是否已经处理过这个退信邮件
                            if (!monitor.isBounceProcessed(bounceInfo.getBounceMessageId())) {
                                logger.info("检测到退信邮件: {} - 原始邮件: {} - 原因: {}", 
                                           bounceInfo.getBounceMessageId(), bounceInfo.getOriginalMessageId(), 
                                           bounceInfo.getBounceReason());
                                monitor.getCallback().onBounceReceived(monitor.getAccount(), bounceInfo);
                                monitor.markBounceProcessed(bounceInfo.getBounceMessageId());
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描退信邮件失败", e);
        }
    }
    
    /**
     * 检查是否是退信邮件
     */
    private boolean isBounceEmail(MimeMessage message) {
        try {
            String subject = message.getSubject();
            if (subject == null) {
                return false;
            }
            
            String lowerSubject = subject.toLowerCase();
            
            // 检查发件人是否为邮件服务器
            Address[] fromAddresses = message.getFrom();
            if (fromAddresses != null && fromAddresses.length > 0) {
                String fromEmail = fromAddresses[0].toString().toLowerCase();
                if (fromEmail.contains("mailer-daemon") || 
                    fromEmail.contains("postmaster") ||
                    fromEmail.contains("noreply") ||
                    fromEmail.contains("no-reply") ||
                    fromEmail.contains("bounce")) {
                    return true;
                }
            }
            
            // 检查主题关键词
            String[] bounceKeywords = {
                "undelivered", "undeliverable", "delivery failure", "delivery failed",
                "mail delivery failed", "returned mail", "mail undeliverable",
                "message not delivered", "delivery status notification",
                "退信", "邮件发送失败", "邮件无法送达", "邮件被退回"
            };
            
            for (String keyword : bounceKeywords) {
                if (lowerSubject.contains(keyword)) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.warn("检查退信邮件失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 解析退信邮件
     */
    private BounceInfo parseBounceEmail(MimeMessage message) {
        try {
            String messageId = getMessageId(message);
            String subject = message.getSubject();
            
            // 尝试从主题中提取原始邮件信息
            String originalMessageId = extractOriginalMessageIdFromBounce(message);
            String bounceReason = extractBounceReason(message);
            
            return new BounceInfo(messageId, originalMessageId, bounceReason, new Date());
            
        } catch (Exception e) {
            logger.warn("解析退信邮件失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从退信邮件中提取原始MessageID
     */
    private String extractOriginalMessageIdFromBounce(MimeMessage message) {
        try {
            // 方法1: 检查Headers
            String[] originalIds = message.getHeader("Original-Message-ID");
            if (originalIds != null && originalIds.length > 0) {
                return originalIds[0];
            }
            
            // 方法2: 从邮件内容中提取
            Object content = message.getContent();
            if (content instanceof String) {
                return extractMessageIdFromContent((String) content);
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getContent() instanceof String) {
                        String messageId = extractMessageIdFromContent((String) bodyPart.getContent());
                        if (messageId != null) {
                            return messageId;
                        }
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            logger.warn("从退信邮件提取原始MessageID失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从退信邮件中提取退信原因
     */
    private String extractBounceReason(MimeMessage message) {
        try {
            Object content = message.getContent();
            String contentStr = "";
            
            if (content instanceof String) {
                contentStr = (String) content;
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getContent() instanceof String) {
                        contentStr += (String) bodyPart.getContent();
                    }
                }
            }
            
            // 提取退信原因
            String[] reasonPatterns = {
                "Diagnostic-Code:", "Status:", "Reason:", "Error:", "原因:", "错误:"
            };
            
            for (String pattern : reasonPatterns) {
                int index = contentStr.indexOf(pattern);
                if (index != -1) {
                    String reason = contentStr.substring(index + pattern.length()).trim();
                    // 取第一行作为原因
                    int lineEnd = reason.indexOf('\n');
                    if (lineEnd != -1) {
                        reason = reason.substring(0, lineEnd).trim();
                    }
                    if (reason.length() > 0 && reason.length() < 200) {
                        return reason;
                    }
                }
            }
            
            return "未知退信原因";
        } catch (Exception e) {
            logger.warn("提取退信原因失败: {}", e.getMessage());
            return "解析失败";
        }
    }

    /**
     * 扫描邮件状态变化
     * 注意：对于发送的邮件，送达状态主要通过DSN邮件检测，而不是通过IMAP状态标志
     * 这个方法主要用于检测我们发送的邮件是否被标记为已读、已回复等状态
     */
    private void scanEmailStatusChanges(Folder folder, EmailTrackingMonitor monitor) {
        try {
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                logger.debug("收件箱为空，跳过状态扫描");
                return;
            }
            
            // 扩大扫描范围，检查最近500封邮件
            int startIndex = Math.max(1, messageCount - 500);
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            logger.debug("开始扫描邮件状态变化: 账号={}, 扫描范围={}-{}, 邮件数量={}", 
                        monitor.getAccount().getEmailAddress(), startIndex, messageCount, messages.length);
            
            int statusChangeCount = 0;
            int processedCount = 0;
            
            int ourEmailCount = 0;
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String messageId = mimeMessage.getMessageID();
                    
                    if (messageId != null) {
                        processedCount++;
                        
                        // 检查是否是我们发送的邮件
                        // 方法1：检查发件人是否是我们
                        String from = extractEmailAddress(mimeMessage.getFrom());
                        boolean isOurEmail = monitor.getAccount().getEmailAddress().equalsIgnoreCase(from);
                        
                        if (isOurEmail) {
                            ourEmailCount++;
                            
                            // 检查邮件状态
                            boolean isRead = message.isSet(Flags.Flag.SEEN);
                            boolean isFlagged = message.isSet(Flags.Flag.FLAGGED);
                            boolean isAnswered = message.isSet(Flags.Flag.ANSWERED);
                            boolean isDeleted = message.isSet(Flags.Flag.DELETED);
                            
                            // 获取邮件的完整状态信息
                            EmailStatusInfo statusInfo = new EmailStatusInfo();
                            statusInfo.setMessageId(messageId);
                            statusInfo.setRead(isRead);
                            statusInfo.setFlagged(isFlagged);
                            statusInfo.setAnswered(isAnswered);
                            statusInfo.setDeleted(isDeleted);
                            statusInfo.setReceivedDate(message.getReceivedDate());
                            statusInfo.setSentDate(message.getSentDate());
                            
                            // 检查状态是否发生变化
                            if (monitor.hasStatusChanged(messageId, statusInfo)) {
                                statusChangeCount++;
                                logger.info("检测到我们发送的邮件状态变化: {} - 已读:{}, 标记:{}, 已回复:{}, 已删除:{}", 
                                           messageId, isRead, isFlagged, isAnswered, isDeleted);
                                
                                // 触发回调
                                try {
                                    monitor.getCallback().onEmailStatusChanged(monitor.getAccount(), 
                                                                              messageId, isRead, isFlagged);
                                    logger.info("邮件状态变化回调已触发: MessageID={}", messageId);
                                } catch (Exception callbackException) {
                                    logger.error("触发邮件状态变化回调失败: MessageID={}", messageId, callbackException);
                                }
                                
                                monitor.updateStatusCache(messageId, statusInfo);
                            }
                        } else {
                            // 记录非我们发送的邮件，但不处理状态变化
                            logger.debug("跳过非我们发送的邮件: {} (发件人: {})", messageId, from);
                        }
                    }
                }
            }
            
            logger.debug("邮件状态扫描完成: 账号={}, 总邮件数={}, 我们的邮件数={}, 状态变化数={}", 
                        monitor.getAccount().getEmailAddress(), processedCount, ourEmailCount, statusChangeCount);
            
        } catch (Exception e) {
            logger.error("扫描邮件状态变化失败: 账号={}", monitor.getAccount().getEmailAddress(), e);
        }
    }
    
    /**
     * 从主题中提取邮件ID
     */
    private String extractMessageIdFromSubject(String subject) {
        if (subject == null) {
            return null;
        }
        
        // 尝试从主题中提取Message-ID格式的字符串
        Pattern messageIdPattern = Pattern.compile("<([^>]+@[^>]+)>");
        Matcher matcher = messageIdPattern.matcher(subject);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * 检查是否是DSN邮件
     */
    private boolean isDSNEmail(MimeMessage message) {
        try {
            String subject = message.getSubject();
            String contentType = message.getContentType();
            String[] fromHeaders = message.getHeader("From");
            
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
            
            // 检查发件人（DSN邮件通常来自邮件系统）
            boolean fromMatch = false;
            if (fromHeaders != null && fromHeaders.length > 0) {
                String from = fromHeaders[0].toLowerCase();
                fromMatch = from.contains("mailer-daemon") || 
                           from.contains("postmaster") ||
                           from.contains("mail delivery subsystem") ||
                           from.contains("mail system");
            }
            
            return subjectMatch || contentTypeMatch || fromMatch;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 解析DSN邮件
     */
    private DSNInfo parseDSNEmail(MimeMessage message) {
        try {
            DSNInfo dsnInfo = new DSNInfo();
            
            // 解析邮件头
            String[] messageIdHeaders = message.getHeader("Message-ID");
            if (messageIdHeaders != null && messageIdHeaders.length > 0) {
                dsnInfo.setDsnMessageId(messageIdHeaders[0]);
            }
            
            // 解析邮件内容
            String content = extractMessageContent(message);
            if (content != null) {
                logger.debug("DSN邮件内容: {}", content);
                
                // 提取原始Message-ID - 多种方式尝试
                String originalMessageId = extractOriginalMessageId(content, message);
                if (originalMessageId != null) {
                    dsnInfo.setOriginalMessageId(originalMessageId);
                }
                
                // 提取状态 - 多种方式尝试
                String status = extractDSNStatus(content);
                if (status != null) {
                    dsnInfo.setStatus(status);
                }
                
                // 提取收件人 - 多种方式尝试
                String recipient = extractDSNRecipient(content);
                if (recipient != null) {
                    dsnInfo.setRecipient(recipient);
                }
            }
            
            // 如果从内容中无法提取到原始Message-ID，尝试从邮件头中获取
            if (dsnInfo.getOriginalMessageId() == null) {
                String[] originalMessageIdHeaders = message.getHeader("Original-Message-ID");
                if (originalMessageIdHeaders != null && originalMessageIdHeaders.length > 0) {
                    dsnInfo.setOriginalMessageId(extractMessageIdFromHeader(originalMessageIdHeaders[0]));
                }
            }
            
            logger.debug("解析DSN邮件结果: originalMessageId={}, status={}, recipient={}", 
                        dsnInfo.getOriginalMessageId(), dsnInfo.getStatus(), dsnInfo.getRecipient());
            
            return dsnInfo;
            
        } catch (Exception e) {
            logger.error("解析DSN邮件失败", e);
            return null;
        }
    }
    
    /**
     * 提取原始Message-ID
     */
    private String extractOriginalMessageId(String content, MimeMessage message) {
        // 方式1: 从内容中提取
        Matcher messageIdMatcher = DSN_ORIGINAL_MESSAGE_ID_PATTERN.matcher(content);
        if (messageIdMatcher.find()) {
            return messageIdMatcher.group(1);
        }
        
        // 方式2: 尝试从邮件头中提取
        try {
            String[] originalMessageIdHeaders = message.getHeader("Original-Message-ID");
            if (originalMessageIdHeaders != null && originalMessageIdHeaders.length > 0) {
                return extractMessageIdFromHeader(originalMessageIdHeaders[0]);
            }
        } catch (Exception e) {
            logger.debug("从邮件头提取Original-Message-ID失败", e);
        }
        
        // 方式3: 尝试从References头中提取
        try {
            String[] referencesHeaders = message.getHeader("References");
            if (referencesHeaders != null && referencesHeaders.length > 0) {
                String references = referencesHeaders[0];
                // References头可能包含多个Message-ID，取最后一个
                String[] messageIds = references.split("\\s+");
                if (messageIds.length > 0) {
                    return extractMessageIdFromHeader(messageIds[messageIds.length - 1]);
                }
            }
        } catch (Exception e) {
            logger.debug("从References头提取Message-ID失败", e);
        }
        
        return null;
    }
    
    /**
     * 提取DSN状态
     */
    private String extractDSNStatus(String content) {
        // 方式1: 使用正则表达式提取
        Matcher statusMatcher = DSN_STATUS_PATTERN.matcher(content);
        if (statusMatcher.find()) {
            String status = statusMatcher.group(1).trim();
            // 标准化状态
            return normalizeDSNStatus(status);
        }
        
        // 方式2: 根据内容中的关键词判断状态
        String lowerContent = content.toLowerCase();
        if (lowerContent.contains("delivered") || lowerContent.contains("success")) {
            return "delivered";
        } else if (lowerContent.contains("failed") || lowerContent.contains("bounced")) {
            return "failed";
        } else if (lowerContent.contains("deferred") || lowerContent.contains("delayed")) {
            return "deferred";
        } else if (lowerContent.contains("rejected")) {
            return "rejected";
        }
        
        return null;
    }
    
    /**
     * 提取DSN收件人
     */
    private String extractDSNRecipient(String content) {
        // 方式1: 使用正则表达式提取
        Matcher recipientMatcher = DSN_RECIPIENT_PATTERN.matcher(content);
        if (recipientMatcher.find()) {
            return recipientMatcher.group(1).trim();
        }
        
        // 方式2: 尝试从To头中提取
        Pattern toPattern = Pattern.compile("To:\\s*([^\\n\\r]+)", Pattern.CASE_INSENSITIVE);
        Matcher toMatcher = toPattern.matcher(content);
        if (toMatcher.find()) {
            String to = toMatcher.group(1).trim();
            // 提取邮箱地址
            Pattern emailPattern = Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
            Matcher emailMatcher = emailPattern.matcher(to);
            if (emailMatcher.find()) {
                return emailMatcher.group(1);
            }
        }
        
        return null;
    }
    
    /**
     * 标准化DSN状态
     */
    private String normalizeDSNStatus(String status) {
        if (status == null) {
            return null;
        }
        
        String lowerStatus = status.toLowerCase();
        
        // 根据状态码判断
        if (status.matches("\\d+\\.\\d+\\.\\d+")) {
            if (status.startsWith("2.")) {
                return "delivered";
            } else if (status.startsWith("4.")) {
                return "deferred";
            } else if (status.startsWith("5.")) {
                return "failed";
            }
        }
        
        // 根据关键词判断
        if (lowerStatus.contains("delivered") || lowerStatus.contains("success")) {
            return "delivered";
        } else if (lowerStatus.contains("failed") || lowerStatus.contains("bounced")) {
            return "failed";
        } else if (lowerStatus.contains("deferred") || lowerStatus.contains("delayed")) {
            return "deferred";
        } else if (lowerStatus.contains("rejected")) {
            return "rejected";
        }
        
        return status;
    }
    
    /**
     * 从邮件头中提取Message-ID
     */
    private String extractMessageIdFromHeader(String header) {
        if (header == null) {
            return null;
        }
        
        // 移除尖括号
        String messageId = header.trim();
        if (messageId.startsWith("<") && messageId.endsWith(">")) {
            messageId = messageId.substring(1, messageId.length() - 1);
        }
        
        return messageId;
    }
    
    /**
     * 提取邮件内容
     */
    private String extractMessageContent(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                return (String) content;
            } else if (content instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) content;
                StringBuilder contentBuilder = new StringBuilder();
                
                // 遍历所有部分，优先提取DSN相关信息
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    
                    // 优先处理message/delivery-status类型（DSN标准格式）
                    if (bodyPart.isMimeType("message/delivery-status")) {
                        String deliveryStatus = extractBodyPartContent(bodyPart);
                        if (deliveryStatus != null) {
                            contentBuilder.append(deliveryStatus).append("\n");
                        }
                    }
                    // 处理text/plain类型
                    else if (bodyPart.isMimeType("text/plain")) {
                        String textContent = extractBodyPartContent(bodyPart);
                        if (textContent != null) {
                            contentBuilder.append(textContent).append("\n");
                        }
                    }
                    // 处理text/html类型（某些DSN邮件可能使用HTML格式）
                    else if (bodyPart.isMimeType("text/html")) {
                        String htmlContent = extractBodyPartContent(bodyPart);
                        if (htmlContent != null) {
                            // 简单去除HTML标签
                            String plainText = htmlContent.replaceAll("<[^>]+>", " ");
                            contentBuilder.append(plainText).append("\n");
                        }
                    }
                }
                
                return contentBuilder.toString();
            }
        } catch (Exception e) {
            logger.warn("提取邮件内容失败", e);
        }
        
        return null;
    }
    
    /**
     * 提取BodyPart内容
     */
    private String extractBodyPartContent(BodyPart bodyPart) {
        try {
            Object content = bodyPart.getContent();
            if (content instanceof String) {
                return (String) content;
            } else if (content instanceof MimeMultipart) {
                // 递归处理嵌套的multipart
                MimeMultipart nestedMultipart = (MimeMultipart) content;
                StringBuilder nestedContent = new StringBuilder();
                
                for (int i = 0; i < nestedMultipart.getCount(); i++) {
                    BodyPart nestedPart = nestedMultipart.getBodyPart(i);
                    String nestedText = extractBodyPartContent(nestedPart);
                    if (nestedText != null) {
                        nestedContent.append(nestedText).append("\n");
                    }
                }
                
                return nestedContent.toString();
            }
            
            return null;
        } catch (Exception e) {
            logger.debug("提取BodyPart内容失败", e);
            return null;
        }
    }
    
    /**
     * 获取邮件跟踪统计
     */
    public EmailTrackingStats getEmailTrackingStats(Long accountId) {
        EmailTrackingMonitor monitor = trackingMonitors.get(accountId);
        if (monitor != null) {
            return monitor.getStats();
        }
        return new EmailTrackingStats();
    }
    
    /**
     * 获取所有邮件跟踪统计
     */
    public Map<Long, EmailTrackingStats> getAllEmailTrackingStats() {
        Map<Long, EmailTrackingStats> allStats = new HashMap<>();
        for (Map.Entry<Long, EmailTrackingMonitor> entry : trackingMonitors.entrySet()) {
            allStats.put(entry.getKey(), entry.getValue().getStats());
        }
        return allStats;
    }
    
    /**
     * 测试DSN解析功能
     */
    public void testDSNParsing(String dsnContent) {
        logger.info("测试DSN解析功能");
        logger.info("DSN内容: {}", dsnContent);
        
        // 测试原始Message-ID提取
        Matcher messageIdMatcher = DSN_ORIGINAL_MESSAGE_ID_PATTERN.matcher(dsnContent);
        if (messageIdMatcher.find()) {
            logger.info("提取到原始Message-ID: {}", messageIdMatcher.group(1));
        } else {
            logger.warn("未找到原始Message-ID");
        }
        
        // 测试状态提取
        Matcher statusMatcher = DSN_STATUS_PATTERN.matcher(dsnContent);
        if (statusMatcher.find()) {
            String status = statusMatcher.group(1).trim();
            String normalizedStatus = normalizeDSNStatus(status);
            logger.info("提取到状态: {} -> {}", status, normalizedStatus);
        } else {
            logger.warn("未找到状态信息");
        }
        
        // 测试收件人提取
        Matcher recipientMatcher = DSN_RECIPIENT_PATTERN.matcher(dsnContent);
        if (recipientMatcher.find()) {
            logger.info("提取到收件人: {}", recipientMatcher.group(1).trim());
        } else {
            logger.warn("未找到收件人信息");
        }
    }
    
    /**
     * 测试邮件跟踪功能
     */
    public void testEmailTracking(EmailAccount account) {
        logger.info("开始测试邮件跟踪功能: {}", account.getEmailAddress());
        
        EmailTrackingMonitor monitor = trackingMonitors.get(account.getAccountId());
        if (monitor == null) {
            logger.warn("未找到邮件跟踪监控器: {}", account.getEmailAddress());
            return;
        }
        
        logger.info("监控器状态: 运行中={}, 最后检查时间={}", 
                   monitor.isRunning(), monitor.getLastCheckTime());
        
        // 手动执行一次跟踪监控
        try {
            performEmailTracking(monitor);
            logger.info("手动执行邮件跟踪监控完成");
        } catch (Exception e) {
            logger.error("手动执行邮件跟踪监控失败", e);
        }
        
        // 输出统计信息
        EmailTrackingStats stats = monitor.getStats();
        logger.info("跟踪统计: DSN={}, 回复={}, 状态变化={}, 最后更新时间={}", 
                   stats.getDsnCount(), stats.getReplyCount(), 
                   stats.getStatusChangeCount(), stats.getLastUpdateTime());
    }
    
    /**
     * 强制扫描指定邮件的状态
     */
    public void forceScanEmailStatus(EmailAccount account, String messageId) {
        logger.info("强制扫描邮件状态: 账号={}, MessageID={}", account.getEmailAddress(), messageId);
        
        EmailTrackingMonitor monitor = trackingMonitors.get(account.getAccountId());
        if (monitor == null) {
            logger.warn("未找到邮件跟踪监控器: {}", account.getEmailAddress());
            return;
        }
        
        Store store = null;
        Folder folder = null;
        
        try {
            store = createPersistentConnection(account);
            folder = openInboxFolder(store);
            
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                logger.warn("收件箱为空");
                return;
            }
            
            logger.info("收件箱邮件总数: {}", messageCount);
            
            // 扫描所有邮件查找指定的Message-ID
            Message[] messages = folder.getMessages(1, messageCount);
            boolean found = false;
            
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String msgId = mimeMessage.getMessageID();
                    
                    // 记录前10封邮件的Message-ID用于调试
                    if (i < 10) {
                        logger.debug("邮件 {}: MessageID={}", i + 1, msgId);
                    }
                    
                    if (messageId.equals(msgId)) {
                        found = true;
                        logger.info("找到指定邮件: MessageID={}, 位置={}", messageId, i + 1);
                        
                        // 检查邮件状态
                        boolean isRead = message.isSet(Flags.Flag.SEEN);
                        boolean isFlagged = message.isSet(Flags.Flag.FLAGGED);
                        boolean isAnswered = message.isSet(Flags.Flag.ANSWERED);
                        boolean isDeleted = message.isSet(Flags.Flag.DELETED);
                        
                        logger.info("邮件状态: 已读={}, 标记={}, 已回复={}, 已删除={}", 
                                   isRead, isFlagged, isAnswered, isDeleted);
                        
                        // 强制触发回调
                        try {
                            monitor.getCallback().onEmailStatusChanged(account, messageId, isRead, isFlagged);
                            logger.info("强制触发状态变化回调成功");
                        } catch (Exception e) {
                            logger.error("强制触发状态变化回调失败", e);
                        }
                        
                        break;
                    }
                }
            }
            
            if (!found) {
                logger.warn("未找到指定Message-ID的邮件: {}", messageId);
                logger.info("请检查Message-ID是否正确，或者邮件是否在收件箱中");
            }
            
        } catch (Exception e) {
            logger.error("强制扫描邮件状态失败", e);
        } finally {
            if (folder != null) {
                try {
                    folder.close(false);
                } catch (Exception e) {
                    logger.warn("关闭文件夹失败", e);
                }
            }
        }
    }
    
    /**
     * 诊断邮件跟踪问题
     */
    public void diagnoseEmailTracking(EmailAccount account) {
        logger.info("开始诊断邮件跟踪问题: {}", account.getEmailAddress());
        
        // 1. 检查监控器状态
        EmailTrackingMonitor monitor = trackingMonitors.get(account.getAccountId());
        if (monitor == null) {
            logger.error("❌ 邮件跟踪监控器未启动: {}", account.getEmailAddress());
            return;
        } else {
            logger.info("✅ 邮件跟踪监控器已启动: {}", account.getEmailAddress());
        }
        
        // 2. 检查监控器运行状态
        if (!monitor.isRunning()) {
            logger.error("❌ 邮件跟踪监控器未运行: {}", account.getEmailAddress());
        } else {
            logger.info("✅ 邮件跟踪监控器正在运行: {}", account.getEmailAddress());
        }
        
        // 3. 检查IMAP连接
        Store store = null;
        Folder folder = null;
        
        try {
            store = createPersistentConnection(account);
            if (store == null || !store.isConnected()) {
                logger.error("❌ IMAP连接失败: {}", account.getEmailAddress());
                return;
            } else {
                logger.info("✅ IMAP连接成功: {}", account.getEmailAddress());
            }
            
            folder = openInboxFolder(store);
            if (folder == null || !folder.isOpen()) {
                logger.error("❌ 无法打开收件箱: {}", account.getEmailAddress());
                return;
            } else {
                logger.info("✅ 收件箱打开成功: {}", account.getEmailAddress());
            }
            
            // 4. 检查收件箱邮件数量
            int messageCount = folder.getMessageCount();
            logger.info("📧 收件箱邮件总数: {}", messageCount);
            
            if (messageCount == 0) {
                logger.warn("⚠️ 收件箱为空，无法进行邮件跟踪");
                return;
            }
            
            // 5. 检查最近邮件的Message-ID
            int scanCount = Math.min(10, messageCount);
            int startIndex = Math.max(1, messageCount - scanCount + 1);
            Message[] recentMessages = folder.getMessages(startIndex, messageCount);
            
            logger.info("🔍 检查最近 {} 封邮件的Message-ID:", scanCount);
            for (int i = 0; i < recentMessages.length; i++) {
                Message message = recentMessages[i];
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String msgId = mimeMessage.getMessageID();
                    boolean isRead = message.isSet(Flags.Flag.SEEN);
                    boolean isFlagged = message.isSet(Flags.Flag.FLAGGED);
                    
                    logger.info("  邮件 {}: MessageID={}, 已读={}, 标记={}", 
                               startIndex + i, msgId, isRead, isFlagged);
                }
            }
            
            // 6. 检查状态缓存
            logger.info("📊 状态缓存大小: {}", monitor.statusCache.size());
            logger.info("📊 已处理DSN数量: {}", monitor.processedDSNs.size());
            logger.info("📊 已处理回复数量: {}", monitor.processedReplies.size());
            
            // 7. 输出统计信息
            EmailTrackingStats stats = monitor.getStats();
            logger.info("📈 跟踪统计: DSN={}, 回复={}, 状态变化={}", 
                       stats.getDsnCount(), stats.getReplyCount(), stats.getStatusChangeCount());
            
        } catch (Exception e) {
            logger.error("❌ 诊断过程中发生错误", e);
        } finally {
            if (folder != null) {
                try {
                    folder.close(false);
                } catch (Exception e) {
                    logger.warn("关闭文件夹失败", e);
                }
            }
        }
    }
    
    /**
     * 优雅关闭服务
     */
    @PreDestroy
    public void shutdown() {
        logger.info("开始关闭ImapService邮件跟踪监控服务...");
        serviceShutdown = true;
        
        // 停止所有监控任务
        for (EmailTrackingMonitor monitor : trackingMonitors.values()) {
            monitor.stop();
        }
        trackingMonitors.clear();
        
        // 关闭线程池
        trackingExecutor.shutdown();
        try {
            if (!trackingExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("邮件跟踪监控线程池未能在10秒内正常关闭，强制关闭");
                trackingExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warn("等待邮件跟踪监控线程池关闭时被中断");
            trackingExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("ImapService邮件跟踪监控服务已关闭");
    }
    
    // ==================== 内部类定义 ====================
    
    /**
     * 邮件跟踪监控器
     */
    private static class EmailTrackingMonitor {
        private final EmailAccount account;
        private final EmailTrackingCallback callback;
        private final EmailTrackingStats stats;
        private volatile boolean running = true;
        private volatile Date lastCheckTime;
        
        // 去重缓存
        private final Set<String> processedDSNs = ConcurrentHashMap.newKeySet();
        private final Set<String> processedReplies = ConcurrentHashMap.newKeySet();
        private final Set<String> processedBounces = ConcurrentHashMap.newKeySet();
        private final Map<String, EmailStatusInfo> statusCache = new ConcurrentHashMap<>();
        
        public EmailTrackingMonitor(EmailAccount account, EmailTrackingCallback callback) {
            this.account = account;
            this.callback = callback;
            this.stats = new EmailTrackingStats();
            this.lastCheckTime = new Date();
        }
        
        public void stop() {
            running = false;
        }
        
        public boolean isRunning() {
            return running;
        }
        
        public void updateLastCheckTime() {
            lastCheckTime = new Date();
        }
        
        // DSN处理相关方法
        public boolean isDSNProcessed(String dsnMessageId) {
            return processedDSNs.contains(dsnMessageId);
        }
        
        public void markDSNProcessed(String dsnMessageId) {
            processedDSNs.add(dsnMessageId);
            stats.incrementDsnCount();
        }
        
        // 回复处理相关方法
        public boolean isReplyProcessed(String replyMessageId) {
            return processedReplies.contains(replyMessageId);
        }
        
        public void markReplyProcessed(String replyMessageId) {
            processedReplies.add(replyMessageId);
            stats.incrementReplyCount();
        }
        
        // 退信处理相关方法
        public boolean isBounceProcessed(String bounceMessageId) {
            return processedBounces.contains(bounceMessageId);
        }
        
        public void markBounceProcessed(String bounceMessageId) {
            processedBounces.add(bounceMessageId);
            stats.incrementBounceCount();
        }
        
        // 状态变化检测相关方法
        public boolean hasStatusChanged(String messageId, EmailStatusInfo newStatus) {
            EmailStatusInfo oldStatus = statusCache.get(messageId);
            if (oldStatus == null) {
                logger.debug("新邮件首次检测: MessageID={}, 状态={}", messageId, newStatus);
                return true; // 新邮件，认为有变化
            }
            
            boolean hasChanged = !oldStatus.equals(newStatus);
            if (hasChanged) {
                logger.debug("检测到状态变化: MessageID={}, 旧状态={}, 新状态={}", 
                           messageId, oldStatus, newStatus);
            }
            
            return hasChanged;
        }
        
        public void updateStatusCache(String messageId, EmailStatusInfo statusInfo) {
            statusCache.put(messageId, statusInfo);
            stats.incrementStatusChangeCount();
        }
        
        // 清理过期缓存
        public void cleanupExpiredCache() {
            Date expireTime = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 24小时前
            
            // 清理过期的状态缓存
            statusCache.entrySet().removeIf(entry -> {
                EmailStatusInfo status = entry.getValue();
                return status.getReceivedDate() != null && status.getReceivedDate().before(expireTime);
            });
            
            // 清理过期的DSN和回复记录（保留最近1000条）
            if (processedDSNs.size() > 1000) {
                processedDSNs.clear();
            }
            if (processedReplies.size() > 1000) {
                processedReplies.clear();
            }
        }
        
        // Getters
        public EmailAccount getAccount() { return account; }
        public EmailTrackingCallback getCallback() { return callback; }
        public EmailTrackingStats getStats() { return stats; }
        public Date getLastCheckTime() { return lastCheckTime; }
    }
    
    /**
     * DSN信息
     */
    public static class DSNInfo {
        private String dsnMessageId;
        private String originalMessageId;
        private String status;
        private String recipient;
        private Date receivedTime;
        
        public DSNInfo() {
            this.receivedTime = new Date();
        }
        
        // Getters and setters
        public String getDsnMessageId() { return dsnMessageId; }
        public void setDsnMessageId(String dsnMessageId) { this.dsnMessageId = dsnMessageId; }
        
        public String getOriginalMessageId() { return originalMessageId; }
        public void setOriginalMessageId(String originalMessageId) { this.originalMessageId = originalMessageId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        
        public Date getReceivedTime() { return receivedTime; }
        public void setReceivedTime(Date receivedTime) { this.receivedTime = receivedTime; }
    }
    
    /**
     * 邮件跟踪统计
     */
    public static class EmailTrackingStats {
        private int dsnCount = 0;
        private int replyCount = 0;
        private int bounceCount = 0;
        private int statusChangeCount = 0;
        private Date lastUpdateTime = new Date();
        
        public void incrementDsnCount() {
            dsnCount++;
            lastUpdateTime = new Date();
        }
        
        public void incrementReplyCount() {
            replyCount++;
            lastUpdateTime = new Date();
        }
        
        public void incrementBounceCount() {
            bounceCount++;
            lastUpdateTime = new Date();
        }
        
        public void incrementStatusChangeCount() {
            statusChangeCount++;
            lastUpdateTime = new Date();
        }
        
        // Getters and setters
        public int getDsnCount() { return dsnCount; }
        public void setDsnCount(int dsnCount) { this.dsnCount = dsnCount; }
        
        public int getReplyCount() { return replyCount; }
        public void setReplyCount(int replyCount) { this.replyCount = replyCount; }
        
        public int getBounceCount() { return bounceCount; }
        public void setBounceCount(int bounceCount) { this.bounceCount = bounceCount; }
        
        public int getStatusChangeCount() { return statusChangeCount; }
        public void setStatusChangeCount(int statusChangeCount) { this.statusChangeCount = statusChangeCount; }
        
        public Date getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(Date lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    }
    
    /**
     * 邮件状态信息
     */
    public static class EmailStatusInfo {
        private String messageId;
        private boolean read;
        private boolean flagged;
        private boolean answered;
        private boolean deleted;
        private Date receivedDate;
        private Date sentDate;
        
        // Getters and setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
        
        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
        
        public boolean isAnswered() { return answered; }
        public void setAnswered(boolean answered) { this.answered = answered; }
        
        public boolean isDeleted() { return deleted; }
        public void setDeleted(boolean deleted) { this.deleted = deleted; }
        
        public Date getReceivedDate() { return receivedDate; }
        public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }
        
        public Date getSentDate() { return sentDate; }
        public void setSentDate(Date sentDate) { this.sentDate = sentDate; }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EmailStatusInfo that = (EmailStatusInfo) obj;
            return read == that.read && flagged == that.flagged && 
                   answered == that.answered && deleted == that.deleted &&
                   Objects.equals(messageId, that.messageId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(messageId, read, flagged, answered, deleted);
        }
    }
    
    /**
     * 退信信息
     */
    public static class BounceInfo {
        private String bounceMessageId;
        private String originalMessageId;
        private String bounceReason;
        private Date receivedTime;
        
        public BounceInfo(String bounceMessageId, String originalMessageId, String bounceReason, Date receivedTime) {
            this.bounceMessageId = bounceMessageId;
            this.originalMessageId = originalMessageId;
            this.bounceReason = bounceReason;
            this.receivedTime = receivedTime;
        }
        
        // Getters
        public String getBounceMessageId() { return bounceMessageId; }
        public String getOriginalMessageId() { return originalMessageId; }
        public String getBounceReason() { return bounceReason; }
        public Date getReceivedTime() { return receivedTime; }
        
        // Setters
        public void setBounceMessageId(String bounceMessageId) { this.bounceMessageId = bounceMessageId; }
        public void setOriginalMessageId(String originalMessageId) { this.originalMessageId = originalMessageId; }
        public void setBounceReason(String bounceReason) { this.bounceReason = bounceReason; }
        public void setReceivedTime(Date receivedTime) { this.receivedTime = receivedTime; }
        
        @Override
        public String toString() {
            return "BounceInfo{" +
                    "bounceMessageId='" + bounceMessageId + '\'' +
                    ", originalMessageId='" + originalMessageId + '\'' +
                    ", bounceReason='" + bounceReason + '\'' +
                    ", receivedTime=" + receivedTime +
                    '}';
        }
    }
    
    /**
     * 邮件跟踪回调接口
     */
    public interface EmailTrackingCallback {
        void onDSNReceived(EmailAccount account, DSNInfo dsnInfo);
        void onReplyReceived(EmailAccount account, String originalMessageId, MimeMessage replyMessage);
        void onEmailStatusChanged(EmailAccount account, String messageId, boolean isRead, boolean isFlagged);
        void onDeliveryConfirmed(EmailAccount account, String messageId, Message sentMessage);
        void onBounceReceived(EmailAccount account, BounceInfo bounceInfo);
        void onTrackingError(EmailAccount account, Exception e);
    }
}
