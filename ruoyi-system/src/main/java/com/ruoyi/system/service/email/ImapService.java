package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * IMAP服务
 * 提供IMAP连接测试、邮件获取和实时监控功能
 */
@Service
public class ImapService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImapService.class);
    
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
}
