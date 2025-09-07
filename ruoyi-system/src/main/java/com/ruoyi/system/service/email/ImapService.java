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
    
    // DSN邮件模式匹配
    private static final Pattern DSN_PATTERN = Pattern.compile(
        "Message-ID:\\s*<([^>]+)>", Pattern.CASE_INSENSITIVE);
    private static final Pattern DSN_STATUS_PATTERN = Pattern.compile(
        "Status:\\s*(\\d+\\.\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DSN_RECIPIENT_PATTERN = Pattern.compile(
        "Final-Recipient:\\s*rfc822;\\s*([^\\s]+)", Pattern.CASE_INSENSITIVE);
    
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
        
        // 启动监控任务
        trackingExecutor.scheduleWithFixedDelay(() -> {
            try {
                if (!serviceShutdown && monitor.isRunning()) {
                    performEmailTracking(monitor);
                }
            } catch (Exception e) {
                if (!serviceShutdown) {
                    logger.error("邮件跟踪监控异常: {}", account.getEmailAddress(), e);
                }
            }
        }, 0, 30, TimeUnit.SECONDS); // 每30秒检查一次
        
        logger.info("启动邮件跟踪监控: {}", account.getEmailAddress());
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
            // 建立IMAP连接
            store = createPersistentConnection(account);
            folder = openInboxFolder(store);
            
            // 扫描DSN邮件
            scanDSNEmails(folder, monitor);
            
            // 扫描回复邮件
            scanReplyEmails(folder, monitor);
            
            // 扫描邮件状态变化
            scanEmailStatusChanges(folder, monitor);
            
            monitor.updateLastCheckTime();
            
        } catch (Exception e) {
            if (!serviceShutdown) {
                logger.error("邮件跟踪监控失败: {} - {}", account.getEmailAddress(), e.getMessage());
                monitor.getCallback().onTrackingError(account, e);
            }
        } finally {
            // 关闭连接
            if (folder != null) {
                try {
                    folder.close(false);
                } catch (Exception e) {
                    if (!serviceShutdown) {
                        logger.warn("关闭文件夹失败", e);
                    }
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) {
                    if (!serviceShutdown) {
                        logger.warn("关闭存储失败", e);
                    }
                }
            }
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
            
            int startIndex = Math.max(1, messageCount - 100); // 检查最近100封邮件
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    
                    // 检查是否是DSN邮件
                    if (isDSNEmail(mimeMessage)) {
                        DSNInfo dsnInfo = parseDSNEmail(mimeMessage);
                        if (dsnInfo != null) {
                            logger.info("检测到DSN邮件: {} - 状态: {}", 
                                       dsnInfo.getOriginalMessageId(), dsnInfo.getStatus());
                            monitor.getCallback().onDSNReceived(monitor.getAccount(), dsnInfo);
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
            
            int startIndex = Math.max(1, messageCount - 100);
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    
                    // 检查是否是回复邮件
                    String inReplyTo = mimeMessage.getHeader("In-Reply-To", null);
                    if (inReplyTo != null && !inReplyTo.trim().isEmpty()) {
                        String originalMessageId = extractMessageIdFromHeader(inReplyTo);
                        if (originalMessageId != null) {
                            logger.info("检测到回复邮件: {} -> {}", 
                                       originalMessageId, mimeMessage.getMessageID());
                            monitor.getCallback().onReplyReceived(monitor.getAccount(), 
                                                                originalMessageId, mimeMessage);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描回复邮件失败", e);
        }
    }
    
    /**
     * 扫描邮件状态变化
     */
    private void scanEmailStatusChanges(Folder folder, EmailTrackingMonitor monitor) {
        try {
            // 这里可以检查邮件的已读状态、星标状态等变化
            // 用于统计邮件打开情况
            
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                return;
            }
            
            int startIndex = Math.max(1, messageCount - 50);
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String messageId = mimeMessage.getMessageID();
                    
                    if (messageId != null) {
                        // 检查邮件状态
                        boolean isRead = message.isSet(Flags.Flag.SEEN);
                        boolean isFlagged = message.isSet(Flags.Flag.FLAGGED);
                        
                        // 通知状态变化
                        monitor.getCallback().onEmailStatusChanged(monitor.getAccount(), 
                                                                  messageId, isRead, isFlagged);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描邮件状态变化失败", e);
        }
    }
    
    /**
     * 检查是否是DSN邮件
     */
    private boolean isDSNEmail(MimeMessage message) {
        try {
            String subject = message.getSubject();
            String contentType = message.getContentType();
            
            return (subject != null && subject.toLowerCase().contains("delivery status notification")) ||
                   (contentType != null && contentType.toLowerCase().contains("multipart/report"));
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
                // 提取原始Message-ID
                Matcher messageIdMatcher = DSN_PATTERN.matcher(content);
                if (messageIdMatcher.find()) {
                    dsnInfo.setOriginalMessageId(messageIdMatcher.group(1));
                }
                
                // 提取状态
                Matcher statusMatcher = DSN_STATUS_PATTERN.matcher(content);
                if (statusMatcher.find()) {
                    dsnInfo.setStatus(statusMatcher.group(1));
                }
                
                // 提取收件人
                Matcher recipientMatcher = DSN_RECIPIENT_PATTERN.matcher(content);
                if (recipientMatcher.find()) {
                    dsnInfo.setRecipient(recipientMatcher.group(1));
                }
            }
            
            return dsnInfo;
            
        } catch (Exception e) {
            logger.error("解析DSN邮件失败", e);
            return null;
        }
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
                
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        contentBuilder.append(bodyPart.getContent());
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
        
        public void incrementStatusChangeCount() {
            statusChangeCount++;
            lastUpdateTime = new Date();
        }
        
        // Getters and setters
        public int getDsnCount() { return dsnCount; }
        public void setDsnCount(int dsnCount) { this.dsnCount = dsnCount; }
        
        public int getReplyCount() { return replyCount; }
        public void setReplyCount(int replyCount) { this.replyCount = replyCount; }
        
        public int getStatusChangeCount() { return statusChangeCount; }
        public void setStatusChangeCount(int statusChangeCount) { this.statusChangeCount = statusChangeCount; }
        
        public Date getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(Date lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    }
    
    /**
     * 邮件跟踪回调接口
     */
    public interface EmailTrackingCallback {
        void onDSNReceived(EmailAccount account, DSNInfo dsnInfo);
        void onReplyReceived(EmailAccount account, String originalMessageId, MimeMessage replyMessage);
        void onEmailStatusChanged(EmailAccount account, String messageId, boolean isRead, boolean isFlagged);
        void onTrackingError(EmailAccount account, Exception e);
    }
}
