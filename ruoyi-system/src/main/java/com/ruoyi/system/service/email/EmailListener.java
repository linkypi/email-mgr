package com.ruoyi.system.service.email;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailStatistics;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.domain.email.EmailPersonal;
import com.ruoyi.system.mapper.email.EmailAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.Calendar;
import java.util.Date;

/**
 * 邮件监听服务
 * 为每个邮箱账号建立持久化连接并进行监控
 */
@Service
public class EmailListener {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);
    
    @Autowired
    private EmailAccountMapper emailAccountMapper;
    
    @Autowired
    private SmtpService smtpService;
    
    @Autowired
    private ImapService imapService;
    
    @Autowired
    private IEmailServiceMonitorService emailServiceMonitorService;
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private IEmailStatisticsService emailStatisticsService;
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    @Autowired
    private IEmailPersonalService emailPersonalService;
    
    private final Map<Long, AccountConnectionInfo> activeConnections = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
    private volatile boolean isRunning = false;
    
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
     * 账号连接信息
     */
    private static class AccountConnectionInfo {
        private EmailAccount account;
        private Transport smtpTransport;
        private Store imapStore;
        private Folder imapFolder;
        private volatile boolean smtpConnected = false;
        private volatile boolean imapConnected = false;
        private volatile String smtpError;
        private volatile String imapError;
        
        public AccountConnectionInfo(EmailAccount account) {
            this.account = account;
        }
        
        // Getters and setters
        public EmailAccount getAccount() { return account; }
        public Transport getSmtpTransport() { return smtpTransport; }
        public void setSmtpTransport(Transport smtpTransport) { this.smtpTransport = smtpTransport; }
        public Store getImapStore() { return imapStore; }
        public void setImapStore(Store imapStore) { this.imapStore = imapStore; }
        public Folder getImapFolder() { return imapFolder; }
        public void setImapFolder(Folder imapFolder) { this.imapFolder = imapFolder; }
        public boolean isSmtpConnected() { return smtpConnected; }
        public void setSmtpConnected(boolean smtpConnected) { this.smtpConnected = smtpConnected; }
        public boolean isImapConnected() { return imapConnected; }
        public void setImapConnected(boolean imapConnected) { this.imapConnected = imapConnected; }
        public String getSmtpError() { return smtpError; }
        public void setSmtpError(String smtpError) { this.smtpError = smtpError; }
        public String getImapError() { return imapError; }
        public void setImapError(String imapError) { this.imapError = imapError; }
    }
    
    /**
     * 启动监听服务
     */
    @PostConstruct
    public void startListener() {
        if (isRunning) {
            logger.info("邮件监听服务已经在运行中");
            return;
        }
        
        logger.info("启动邮件监听服务");
        isRunning = true;
        
        // 加载所有启用的邮箱账号
        loadEmailAccounts();
        
        // 启动定期检查任务
        scheduledExecutor.scheduleWithFixedDelay(this::checkAllConnections, 30, 30, TimeUnit.SECONDS);
        scheduledExecutor.scheduleWithFixedDelay(this::loadEmailAccounts, 5, 5, TimeUnit.MINUTES);
        
        logger.info("邮件监听服务启动成功，当前监控 {} 个邮箱账号", activeConnections.size());
    }
    
    /**
     * 停止监听服务
     */
    @PreDestroy
    public void stopListener() {
        if (!isRunning) {
            return;
        }
        
        logger.info("停止邮件监听服务");
        isRunning = false;
        
        // 关闭所有连接
        activeConnections.values().forEach(this::closeAccountConnections);
        activeConnections.clear();
        
        // 关闭线程池
        scheduledExecutor.shutdown();
        try {
            if (!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("邮件监听服务已停止");
    }
    
    /**
     * 重启监听服务
     */
    public void restartListener() {
        logger.info("重启邮件监听服务");
        stopListener();
        startListener();
    }
    
    /**
     * 加载邮箱账号
     */
    private void loadEmailAccounts() {
        try {
            List<EmailAccount> accounts = emailAccountMapper.selectEmailAccountList(new EmailAccount());
            
            for (EmailAccount account : accounts) {
                if (!"0".equals(account.getStatus())) {
                    // 如果账号被禁用发送邮件，移除现有连接
                    logger.info("邮箱账号 {} 已禁用发送邮件，移除现有连接", account.getEmailAddress());
                    removeAccountConnection(account.getAccountId());
                    continue;
                }
                
                // 只处理启用发送邮件的账号
                if (!activeConnections.containsKey(account.getAccountId())) {
                    // 新增账号连接
                    logger.info("为启用发送邮件的账号 {} 建立连接", account.getEmailAddress());
                    addAccountConnection(account);
                }
            }
            
            // 移除已删除的账号连接
            activeConnections.entrySet().removeIf(entry -> {
                Long accountId = entry.getKey();
                boolean exists = accounts.stream().anyMatch(account -> account.getAccountId().equals(accountId));
                if (!exists) {
                    closeAccountConnections(entry.getValue());
                    return true;
                }
                return false;
            });
            
        } catch (Exception e) {
            logger.error("加载邮箱账号失败", e);
        }
    }
    
    /**
     * 添加账号连接
     */
    public void addAccountConnection(EmailAccount account) {
        logger.info("为邮箱账号 {} 建立连接", account.getEmailAddress());
        
        AccountConnectionInfo connectionInfo = new AccountConnectionInfo(account);
        activeConnections.put(account.getAccountId(), connectionInfo);
        
        // 异步建立连接
        scheduledExecutor.submit(() -> {
            establishSmtpConnection(connectionInfo);
            establishImapConnection(connectionInfo);
        });
    }
    
    /**
     * 移除账号连接
     */
    public void removeAccountConnection(Long accountId) {
        AccountConnectionInfo connectionInfo = activeConnections.remove(accountId);
        if (connectionInfo != null) {
            logger.info("移除邮箱账号 {} 的连接", connectionInfo.getAccount().getEmailAddress());
            closeAccountConnections(connectionInfo);
        }
    }
    
    /**
     * 建立SMTP连接
     */
    private void establishSmtpConnection(AccountConnectionInfo connectionInfo) {
        EmailAccount account = connectionInfo.getAccount();
        
        try {
            Transport transport = smtpService.createPersistentConnection(account);
            connectionInfo.setSmtpTransport(transport);
            connectionInfo.setSmtpConnected(true);
            connectionInfo.setSmtpError(null);
            
            logger.debug("SMTP连接建立成功: {}", account.getEmailAddress());
            
            // 更新监控状态
            updateServiceMonitorStatus(account.getAccountId(), "smtp", "running", null);
            
        } catch (Exception e) {
            connectionInfo.setSmtpConnected(false);
            connectionInfo.setSmtpError(e.getMessage());
            
            logger.error("建立SMTP连接失败: {} - {}", account.getEmailAddress(), e.getMessage());
            
            // 更新监控状态
            updateServiceMonitorStatus(account.getAccountId(), "smtp", "error", e.getMessage());
            
            // 发送管理员通知
            sendAdminNotification(account, "SMTP", e.getMessage());
        }
    }
    
    /**
     * 建立IMAP连接
     */
    private void establishImapConnection(AccountConnectionInfo connectionInfo) {
        EmailAccount account = connectionInfo.getAccount();
        
        try {
            Store store = imapService.createPersistentConnection(account);
            Folder folder = imapService.openInboxFolder(store);
            
            connectionInfo.setImapStore(store);
            connectionInfo.setImapFolder(folder);
            connectionInfo.setImapConnected(true);
            connectionInfo.setImapError(null);
            
            logger.debug("IMAP连接建立成功: {}", account.getEmailAddress());
            
            // 启动邮件跟踪监控
            startEmailTracking(account);
            
            // 更新监控状态
            updateServiceMonitorStatus(account.getAccountId(), "imap", "running", null);
            
        } catch (Exception e) {
            connectionInfo.setImapConnected(false);
            connectionInfo.setImapError(e.getMessage());
            
            logger.error("建立IMAP连接失败: {} - {}", account.getEmailAddress(), e.getMessage());
            
            // 更新监控状态
            updateServiceMonitorStatus(account.getAccountId(), "imap", "error", e.getMessage());
            
            // 发送管理员通知
            sendAdminNotification(account, "IMAP", e.getMessage());
        }
    }
    
    /**
     * 检查所有连接状态
     */
    private void checkAllConnections() {
        for (AccountConnectionInfo connectionInfo : activeConnections.values()) {
            checkAccountConnections(connectionInfo);
        }
    }
    
    /**
     * 检查账号连接状态
     */
    private void checkAccountConnections(AccountConnectionInfo connectionInfo) {
        EmailAccount account = connectionInfo.getAccount();
        
        // 检查SMTP连接
        if (connectionInfo.isSmtpConnected()) {
            boolean smtpAlive = smtpService.isConnectionAlive(connectionInfo.getSmtpTransport());
            if (!smtpAlive) {
                logger.warn("SMTP连接断开: {}", account.getEmailAddress());
                connectionInfo.setSmtpConnected(false);
                updateServiceMonitorStatus(account.getAccountId(), "smtp", "error", "连接断开");
                
                // 尝试重连
                reconnectSmtp(connectionInfo);
            } else {
                updateServiceMonitorStatus(account.getAccountId(), "smtp", "running", null);
            }
        }
        
        // 检查IMAP连接
        if (connectionInfo.isImapConnected()) {
            boolean imapAlive = isImapConnectionAlive(connectionInfo);
            if (!imapAlive) {
                logger.warn("IMAP连接断开: {}", account.getEmailAddress());
                connectionInfo.setImapConnected(false);
                updateServiceMonitorStatus(account.getAccountId(), "imap", "error", "连接断开");
                
                // 尝试重连
                reconnectImap(connectionInfo);
            } else {
                updateServiceMonitorStatus(account.getAccountId(), "imap", "running", null);
            }
        }
    }
    
    /**
     * 检查IMAP连接是否存活
     */
    private boolean isImapConnectionAlive(AccountConnectionInfo connectionInfo) {
        try {
            Store store = connectionInfo.getImapStore();
            Folder folder = connectionInfo.getImapFolder();
            
            return store != null && store.isConnected() && 
                   folder != null && folder.isOpen();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 重连SMTP
     */
    private void reconnectSmtp(AccountConnectionInfo connectionInfo) {
        scheduledExecutor.submit(() -> {
            try {
                Transport transport = connectionInfo.getSmtpTransport();
                if (smtpService.reconnect(transport, connectionInfo.getAccount())) {
                    connectionInfo.setSmtpConnected(true);
                    connectionInfo.setSmtpError(null);
                    logger.info("SMTP重连成功: {}", connectionInfo.getAccount().getEmailAddress());
                    updateServiceMonitorStatus(connectionInfo.getAccount().getAccountId(), "smtp", "running", null);
                } else {
                    establishSmtpConnection(connectionInfo);
                }
            } catch (Exception e) {
                logger.error("SMTP重连失败: {} - {}", connectionInfo.getAccount().getEmailAddress(), e.getMessage());
            }
        });
    }
    
    /**
     * 重连IMAP
     */
    private void reconnectImap(AccountConnectionInfo connectionInfo) {
        scheduledExecutor.submit(() -> {
            try {
                // 关闭旧连接
                closeImapConnection(connectionInfo);
                
                // 建立新连接
                establishImapConnection(connectionInfo);
            } catch (Exception e) {
                logger.error("IMAP重连失败: {} - {}", connectionInfo.getAccount().getEmailAddress(), e.getMessage());
            }
        });
    }
    
    /**
     * 更新服务监控状态
     */
    private void updateServiceMonitorStatus(Long accountId, String serviceType, String status, String errorMessage) {
        try {
            emailServiceMonitorService.updateServiceStatus(accountId, serviceType, status, errorMessage);
        } catch (Exception e) {
            logger.error("更新服务监控状态失败: {} - {}", accountId, e.getMessage());
        }
    }
    
    /**
     * 发送管理员通知
     */
    private void sendAdminNotification(EmailAccount account, String serviceType, String errorMessage) {
        // TODO: 实现管理员通知逻辑
        logger.warn("邮箱账号 {} 的 {} 服务异常，需要管理员关注: {}", 
                   account.getEmailAddress(), serviceType, errorMessage);
    }
    
    /**
     * 关闭账号连接
     */
    private void closeAccountConnections(AccountConnectionInfo connectionInfo) {
        try {
            // 关闭SMTP连接
            if (connectionInfo.getSmtpTransport() != null) {
                smtpService.closeConnection(connectionInfo.getSmtpTransport());
            }
            
            // 关闭IMAP连接
            closeImapConnection(connectionInfo);
            
            // 更新监控状态
            EmailAccount account = connectionInfo.getAccount();
            updateServiceMonitorStatus(account.getAccountId(), "smtp", "stopped", null);
            updateServiceMonitorStatus(account.getAccountId(), "imap", "stopped", null);
            
        } catch (Exception e) {
            logger.error("关闭连接失败", e);
        }
    }
    
    /**
     * 关闭IMAP连接
     */
    private void closeImapConnection(AccountConnectionInfo connectionInfo) {
        try {
            // 停止邮件跟踪监控
            stopEmailTracking(connectionInfo.getAccount());
            
            Folder folder = connectionInfo.getImapFolder();
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            
            Store store = connectionInfo.getImapStore();
            if (store != null && store.isConnected()) {
                store.close();
            }
            
            connectionInfo.setImapFolder(null);
            connectionInfo.setImapStore(null);
            connectionInfo.setImapConnected(false);
            
        } catch (Exception e) {
            logger.warn("关闭IMAP连接失败", e);
        }
    }
    
    /**
     * 手动启动指定账号的服务
     */
    public boolean startAccountService(Long accountId, String serviceType) {
        AccountConnectionInfo connectionInfo = activeConnections.get(accountId);
        if (connectionInfo == null) {
            EmailAccount account = emailAccountMapper.selectEmailAccountByAccountId(accountId);
            if (account != null) {
                addAccountConnection(account);
                connectionInfo = activeConnections.get(accountId);
            }
        }
        
        if (connectionInfo == null) {
            return false;
        }
        
        if ("smtp".equals(serviceType)) {
            establishSmtpConnection(connectionInfo);
            return connectionInfo.isSmtpConnected();
        } else if ("imap".equals(serviceType)) {
            establishImapConnection(connectionInfo);
            return connectionInfo.isImapConnected();
        }
        
        return false;
    }
    
    /**
     * 手动停止指定账号的服务
     */
    public boolean stopAccountService(Long accountId, String serviceType) {
        AccountConnectionInfo connectionInfo = activeConnections.get(accountId);
        if (connectionInfo == null) {
            return false;
        }
        
        if ("smtp".equals(serviceType)) {
            smtpService.closeConnection(connectionInfo.getSmtpTransport());
            connectionInfo.setSmtpConnected(false);
            updateServiceMonitorStatus(accountId, "smtp", "stopped", null);
            return true;
        } else if ("imap".equals(serviceType)) {
            closeImapConnection(connectionInfo);
            updateServiceMonitorStatus(accountId, "imap", "stopped", null);
            return true;
        }
        
        return false;
    }
    
    /**
     * 手动重启指定账号的服务
     */
    public boolean restartAccountService(Long accountId, String serviceType) {
        stopAccountService(accountId, serviceType);
        return startAccountService(accountId, serviceType);
    }
    
    /**
     * 获取连接状态
     */
    public Map<String, Object> getConnectionStatus(Long accountId) {
        AccountConnectionInfo connectionInfo = activeConnections.get(accountId);
        Map<String, Object> status = new ConcurrentHashMap<>();
        
        if (connectionInfo != null) {
            status.put("smtpConnected", connectionInfo.isSmtpConnected());
            status.put("imapConnected", connectionInfo.isImapConnected());
            status.put("smtpError", connectionInfo.getSmtpError());
            status.put("imapError", connectionInfo.getImapError());
        } else {
            status.put("smtpConnected", false);
            status.put("imapConnected", false);
            status.put("smtpError", "未连接");
            status.put("imapError", "未连接");
        }
        
        return status;
    }
    
    /**
     * 获取所有连接状态
     */
    public Map<Long, Map<String, Object>> getAllConnectionStatus() {
        Map<Long, Map<String, Object>> allStatus = new ConcurrentHashMap<>();
        
        for (Map.Entry<Long, AccountConnectionInfo> entry : activeConnections.entrySet()) {
            allStatus.put(entry.getKey(), getConnectionStatus(entry.getKey()));
        }
        
        return allStatus;
    }
    
    /**
     * 检查账号是否已连接
     */
    public boolean isAccountConnected(Long accountId) {
        AccountConnectionInfo connectionInfo = activeConnections.get(accountId);
        return connectionInfo != null && (connectionInfo.isSmtpConnected() || connectionInfo.isImapConnected());
    }
    
    /**
     * 检查监听服务是否运行中
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * 获取活跃连接数量
     */
    public int getActiveConnectionCount() {
        return activeConnections.size();
    }
    
    // ==================== 从 ImapEmailSyncService 迁移的功能 ====================
    
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
        String messageId = null;
        try {
            // 检查数据库连接是否可用
            if (!isDatabaseConnectionAvailable()) {
                logger.error("数据库连接不可用，无法发送带跟踪的邮件: {} -> {}", account.getEmailAddress(), to);
                return null;
            }
            
            // 生成唯一的Message-ID
            messageId = generateMessageId();
            logger.info("开始发送带跟踪的邮件: {} -> {}, MessageID: {}, TaskID: {}", 
                       account.getEmailAddress(), to, messageId, taskId);
            
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
            
            logger.debug("准备插入邮件跟踪记录: MessageID={}, TaskID={}", messageId, taskId);
            
            // 保存跟踪记录
            int insertResult = emailTrackRecordService.insertEmailTrackRecord(trackRecord);
            if (insertResult <= 0) {
                logger.error("插入邮件跟踪记录失败: MessageID={}, TaskID={}", messageId, taskId);
                return null;
            }
            logger.info("邮件跟踪记录插入成功: MessageID={}, TaskID={}", messageId, taskId);
            
            // 更新状态为发送中
            emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SENDING.name());
            logger.debug("邮件状态更新为发送中: MessageID={}", messageId);
            
            // 发送邮件
            SmtpService.SmtpSendResult result = smtpService.sendEmail(account, to, subject, 
                addTrackingToContent(content, trackingPixelUrl, trackingLinkUrl), messageId);
            
            if (result.isSuccess()) {
                // 更新状态为发送成功
                emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_SUCCESS.name());
                emailTrackRecordService.recordEmailDelivered(messageId);
                logger.info("邮件发送成功: {} -> {}, MessageID: {}", account.getEmailAddress(), to, messageId);
            } else {
                // 更新状态为发送失败
                emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_FAILED.name());
                logger.error("邮件发送失败: {} -> {}, MessageID: {}, 错误: {}", 
                           account.getEmailAddress(), to, messageId, result.getErrorMessage());
            }
            
            return messageId;
            
        } catch (Exception e) {
            logger.error("发送带跟踪的邮件失败: {} -> {}, MessageID: {}, TaskID: {}", 
                        account.getEmailAddress(), to, messageId, taskId, e);
            
            // 如果跟踪记录已创建但发送失败，尝试更新状态
            if (messageId != null) {
                try {
                    emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_FAILED.name());
                    logger.info("已更新失败邮件的跟踪记录状态: MessageID={}", messageId);
                } catch (Exception updateException) {
                    logger.error("更新失败邮件跟踪记录状态时发生异常: MessageID={}", messageId, updateException);
                }
            }
            
            return null;
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
    
    /**
     * 处理新邮件并同步到个人邮件表
     */
    public void processNewMessages(Long accountId, EmailAccount account, Message[] messages) {
        logger.info("处理 {} 封新邮件", messages.length);
        
        for (Message message : messages) {
            try {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    
                    // 同步到个人邮件表
                    syncToPersonalEmail(mimeMessage, account);
                    
                    // 处理邮件跟踪记录
                    processEmailTracking(mimeMessage, account);
                    
                }
            } catch (Exception e) {
                logger.error("处理新邮件失败", e);
            }
        }
    }
    
    // ==================== 辅助方法 ====================
    
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
            emailAccountService.updateEmailAccountStatistics(account);
            
            logger.debug("更新账户 {} 同步时间: {}", account.getEmailAddress(), account.getLastSyncTime());
        } catch (Exception e) {
            logger.error("更新账户同步信息失败", e);
        }
    }
    
    /**
     * 同步邮件到个人邮件表
     */
    private void syncToPersonalEmail(MimeMessage message, EmailAccount account) {
        try {
            // 提取邮件信息
            String messageId = message.getMessageID();
            String subject = decodeMimeText(message.getSubject());
            String from = extractEmailAddress(message.getFrom());
            String to = extractEmailAddress(message.getRecipients(Message.RecipientType.TO));
            String cc = extractEmailAddress(message.getRecipients(Message.RecipientType.CC));
            String bcc = extractEmailAddress(message.getRecipients(Message.RecipientType.BCC));
            Date sentDate = message.getSentDate();
            Date receivedDate = message.getReceivedDate();
            
            // 检查是否已存在该邮件
            EmailPersonal existingEmail = emailPersonalService.selectEmailPersonalByMessageId(messageId);
            if (existingEmail != null) {
                return; // 邮件已存在，跳过处理
            }
            
            // 获取邮件内容
            String content = extractMessageContent(message);
            String htmlContent = extractHtmlContent(message);
            
            // 判断邮件类型（收件箱或发件箱）
            String emailType = determineEmailType(from, account.getEmailAddress());
            
            // 判断邮件状态
            String status = determineEmailStatus(message);
            
            // 判断是否星标
            boolean isStarred = message.isSet(Flags.Flag.FLAGGED);
            
            // 判断是否重要
            boolean isImportant = message.isSet(Flags.Flag.SEEN) ? false : true; // 未读邮件标记为重要
            
            // 创建个人邮件记录
            EmailPersonal personalEmail = new EmailPersonal();
            personalEmail.setFromAddress(from);
            personalEmail.setToAddress(to);
            personalEmail.setSubject(subject);
            personalEmail.setContent(content);
            personalEmail.setHtmlContent(htmlContent);
            personalEmail.setStatus(status);
            personalEmail.setIsStarred(isStarred ? 1 : 0);
            personalEmail.setIsImportant(isImportant ? 1 : 0);
            personalEmail.setReceiveTime(receivedDate);
            personalEmail.setSendTime(sentDate);
            personalEmail.setEmailType(emailType);
            personalEmail.setAttachmentCount(countAttachments(message));
            personalEmail.setCreateBy(SecurityUtils.getUsername());
            personalEmail.setCreateTime(new Date());
            
            emailPersonalService.insertEmailPersonal(personalEmail);
            
            logger.info("邮件已同步到个人邮件表: {}", subject);
            
        } catch (Exception e) {
            logger.error("同步邮件到个人邮件表失败", e);
        }
    }
    
    /**
     * 判断邮件类型
     */
    private String determineEmailType(String from, String accountEmail) {
        if (from != null && from.contains(accountEmail)) {
            return "sent"; // 发件箱
        } else {
            return "inbox"; // 收件箱
        }
    }
    
    /**
     * 判断邮件状态
     */
    private String determineEmailStatus(Message message) {
        try {
            if (message.isSet(Flags.Flag.SEEN)) {
                return "read"; // 已读
            } else {
                return "unread"; // 未读
            }
        } catch (Exception e) {
            return "unread"; // 默认未读
        }
    }
    
    /**
     * 提取邮件内容
     */
    public String extractMessageContent(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                return (String) content;
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
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
        return "";
    }
    
    /**
     * 提取HTML内容
     */
    public String extractHtmlContent(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/html")) {
                        return (String) bodyPart.getContent();
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("提取HTML内容失败", e);
        }
        return "";
    }
    
    /**
     * 统计附件数量
     */
    public int countAttachments(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                int attachmentCount = 0;
                
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                        attachmentCount++;
                    }
                }
                
                return attachmentCount;
            }
        } catch (Exception e) {
            logger.warn("统计附件数量失败", e);
        }
        return 0;
    }
    
    /**
     * 处理邮件跟踪记录
     */
    private void processEmailTracking(MimeMessage message, EmailAccount account) {
        try {
            String messageId = message.getMessageID();
            String subject = decodeMimeText(message.getSubject());
            String from = extractEmailAddress(message.getFrom());
            String to = extractEmailAddress(message.getRecipients(Message.RecipientType.TO));
            Date sentDate = message.getSentDate();
            
            // 检查是否已存在该邮件跟踪记录
            if (emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId) != null) {
                return; // 邮件已存在，跳过处理
            }
            
            // 创建邮件跟踪记录
            EmailTrackRecord trackRecord = new EmailTrackRecord();
            trackRecord.setMessageId(messageId);
            trackRecord.setSubject(subject);
            trackRecord.setRecipient(to);
            trackRecord.setSender(from);
            trackRecord.setSentTime(sentDate);
            trackRecord.setStatus("RECEIVED");
            trackRecord.setAccountId(account.getAccountId());
            trackRecord.setCreateBy(SecurityUtils.getUsername());
            
            emailTrackRecordService.insertEmailTrackRecord(trackRecord);
            
        } catch (Exception e) {
            logger.error("处理邮件跟踪记录失败", e);
        }
    }
    
    // ==================== 邮件跟踪监控功能 ====================
    
    /**
     * 启动邮件跟踪监控
     */
    private void startEmailTracking(EmailAccount account) {
        try {
            ImapService.EmailTrackingCallback callback = new ImapService.EmailTrackingCallback() {
                @Override
                public void onDSNReceived(EmailAccount account, ImapService.DSNInfo dsnInfo) {
                    handleDSNReceived(account, dsnInfo);
                }
                
                @Override
                public void onReplyReceived(EmailAccount account, String originalMessageId, MimeMessage replyMessage) {
                    handleReplyReceived(account, originalMessageId, replyMessage);
                }
                
                @Override
                public void onEmailStatusChanged(EmailAccount account, String messageId, boolean isRead, boolean isFlagged) {
                    handleEmailStatusChanged(account, messageId, isRead, isFlagged);
                }
                
                @Override
                public void onTrackingError(EmailAccount account, Exception e) {
                    logger.error("邮件跟踪监控错误: {} - {}", account.getEmailAddress(), e.getMessage());
                }
            };
            
            imapService.startEmailTracking(account, callback);
            logger.info("启动邮件跟踪监控: {}", account.getEmailAddress());
            
        } catch (Exception e) {
            logger.error("启动邮件跟踪监控失败: {} - {}", account.getEmailAddress(), e.getMessage());
        }
    }
    
    /**
     * 停止邮件跟踪监控
     */
    private void stopEmailTracking(EmailAccount account) {
        try {
            imapService.stopEmailTracking(account.getAccountId());
            logger.info("停止邮件跟踪监控: {}", account.getEmailAddress());
        } catch (Exception e) {
            logger.error("停止邮件跟踪监控失败: {} - {}", account.getEmailAddress(), e.getMessage());
        }
    }
    
    /**
     * 处理DSN邮件
     */
    private void handleDSNReceived(EmailAccount account, ImapService.DSNInfo dsnInfo) {
        try {
            // 检查数据库连接是否可用
            if (!isDatabaseConnectionAvailable()) {
                logger.warn("数据库连接不可用，跳过DSN邮件处理: {}", dsnInfo.getOriginalMessageId());
                return;
            }
            
            String originalMessageId = dsnInfo.getOriginalMessageId();
            if (originalMessageId == null) {
                logger.warn("DSN邮件中未找到原始Message-ID");
                return;
            }
            
            // 查找对应的跟踪记录
            EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(originalMessageId);
            if (trackRecord == null) {
                logger.warn("未找到对应的邮件跟踪记录: {}", originalMessageId);
                return;
            }
            
            // 根据DSN状态更新邮件状态
            String status = dsnInfo.getStatus();
            if (status != null) {
                if (status.startsWith("2.")) {
                    // 2.x.x 表示成功
                    emailTrackRecordService.recordEmailDelivered(originalMessageId);
                    logger.info("邮件送达确认: {} (任务ID: {})", originalMessageId, trackRecord.getTaskId());
                    
                    // 更新任务统计
                    updateTaskStatistics(trackRecord.getTaskId());
                } else if (status.startsWith("4.")) {
                    // 4.x.x 表示临时失败
                    emailTrackRecordService.updateEmailStatus(originalMessageId, "DELIVERY_FAILED_TEMP");
                    logger.warn("邮件临时投递失败: {} (任务ID: {})", originalMessageId, trackRecord.getTaskId());
                } else if (status.startsWith("5.")) {
                    // 5.x.x 表示永久失败
                    emailTrackRecordService.updateEmailStatus(originalMessageId, "DELIVERY_FAILED_PERM");
                    logger.error("邮件永久投递失败: {} (任务ID: {})", originalMessageId, trackRecord.getTaskId());
                }
            }
            
        } catch (Exception e) {
            // 检查是否是数据库连接问题
            if (isDataSourceClosedException(e)) {
                logger.warn("数据库连接已关闭，跳过DSN邮件处理: {}", dsnInfo.getOriginalMessageId());
                return;
            }
            logger.error("处理DSN邮件失败", e);
        }
    }
    
    /**
     * 处理回复邮件
     */
    private void handleReplyReceived(EmailAccount account, String originalMessageId, MimeMessage replyMessage) {
        try {
            // 检查数据库连接是否可用
            if (!isDatabaseConnectionAvailable()) {
                logger.warn("数据库连接不可用，跳过回复邮件处理: {}", originalMessageId);
                return;
            }
            
            // 查找对应的跟踪记录
            EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(originalMessageId);
            if (trackRecord == null) {
                logger.warn("未找到对应的邮件跟踪记录: {}", originalMessageId);
                return;
            }
            
            // 更新回复状态
            emailTrackRecordService.recordEmailReplied(originalMessageId);
            logger.info("邮件回复确认: {} (任务ID: {})", originalMessageId, trackRecord.getTaskId());
            
            // 更新任务统计
            updateTaskStatistics(trackRecord.getTaskId());
            
        } catch (Exception e) {
            // 检查是否是数据库连接问题
            if (isDataSourceClosedException(e)) {
                logger.warn("数据库连接已关闭，跳过回复邮件处理: {}", originalMessageId);
                return;
            }
            logger.error("处理回复邮件失败", e);
        }
    }
    
    /**
     * 处理邮件状态变化
     */
    private void handleEmailStatusChanged(EmailAccount account, String messageId, boolean isRead, boolean isFlagged) {
        try {
            // 检查数据库连接是否可用
            if (!isDatabaseConnectionAvailable()) {
                logger.warn("数据库连接不可用，跳过邮件状态变化处理: {}", messageId);
                return;
            }
            
            // 查找对应的跟踪记录
            EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
            if (trackRecord == null) {
                // 可能是收到的邮件，不是我们发送的
                return;
            }
            
            // 如果邮件被标记为已读，记录为已打开
            if (isRead && trackRecord.getOpenedTime() == null) {
                emailTrackRecordService.recordEmailOpened(messageId);
                logger.info("邮件打开确认: {} (任务ID: {})", messageId, trackRecord.getTaskId());
                
                // 更新任务统计
                updateTaskStatistics(trackRecord.getTaskId());
            }
            
        } catch (Exception e) {
            // 检查是否是数据库连接问题
            if (isDataSourceClosedException(e)) {
                logger.warn("数据库连接已关闭，跳过邮件状态变化处理: {}", messageId);
                return;
            }
            logger.error("处理邮件状态变化失败", e);
        }
    }
    
    /**
     * 获取邮件跟踪统计
     */
    public ImapService.EmailTrackingStats getEmailTrackingStats(Long accountId) {
        return imapService.getEmailTrackingStats(accountId);
    }
    
    /**
     * 获取所有邮件跟踪统计
     */
    public Map<Long, ImapService.EmailTrackingStats> getAllEmailTrackingStats() {
        return imapService.getAllEmailTrackingStats();
    }
    
    // ==================== 任务相关邮件跟踪功能 ====================
    
    /**
     * 根据任务ID获取邮件跟踪记录
     */
    public List<EmailTrackRecord> getEmailTrackRecordsByTaskId(Long taskId) {
        try {
            if (!isDatabaseConnectionAvailable()) {
                logger.warn("数据库连接不可用，无法获取任务邮件跟踪记录: {}", taskId);
                return new ArrayList<>();
            }
            
            return emailTrackRecordService.selectEmailTrackRecordByTaskId(taskId);
        } catch (Exception e) {
            if (isDataSourceClosedException(e)) {
                logger.warn("数据库连接已关闭，无法获取任务邮件跟踪记录: {}", taskId);
                return new ArrayList<>();
            }
            logger.error("获取任务邮件跟踪记录失败: {}", taskId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 根据任务ID获取邮件统计
     */
    public Map<String, Object> getTaskEmailStatistics(Long taskId) {
        try {
            if (!isDatabaseConnectionAvailable()) {
                logger.warn("数据库连接不可用，无法获取任务邮件统计: {}", taskId);
                return new HashMap<>();
            }
            
            List<EmailTrackRecord> records = emailTrackRecordService.selectEmailTrackRecordByTaskId(taskId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSent", records.size());
            
            int deliveredCount = 0;
            int openedCount = 0;
            int repliedCount = 0;
            int failedCount = 0;
            
            for (EmailTrackRecord record : records) {
                if (record.getDeliveredTime() != null) {
                    deliveredCount++;
                }
                if (record.getOpenedTime() != null) {
                    openedCount++;
                }
                if (record.getRepliedTime() != null) {
                    repliedCount++;
                }
                if ("SEND_FAILED".equals(record.getStatus()) || 
                    "DELIVERY_FAILED_TEMP".equals(record.getStatus()) ||
                    "DELIVERY_FAILED_PERM".equals(record.getStatus())) {
                    failedCount++;
                }
            }
            
            stats.put("deliveredCount", deliveredCount);
            stats.put("openedCount", openedCount);
            stats.put("repliedCount", repliedCount);
            stats.put("failedCount", failedCount);
            
            if (records.size() > 0) {
                stats.put("deliveryRate", String.format("%.2f%%", (double) deliveredCount / records.size() * 100));
                stats.put("openRate", String.format("%.2f%%", (double) openedCount / records.size() * 100));
                stats.put("replyRate", String.format("%.2f%%", (double) repliedCount / records.size() * 100));
            } else {
                stats.put("deliveryRate", "0.00%");
                stats.put("openRate", "0.00%");
                stats.put("replyRate", "0.00%");
            }
            
            return stats;
            
        } catch (Exception e) {
            if (isDataSourceClosedException(e)) {
                logger.warn("数据库连接已关闭，无法获取任务邮件统计: {}", taskId);
                return new HashMap<>();
            }
            logger.error("获取任务邮件统计失败: {}", taskId, e);
            return new HashMap<>();
        }
    }
    
    /**
     * 更新任务统计
     */
    private void updateTaskStatistics(Long taskId) {
        try {
            if (taskId == null) {
                return;
            }
            
            // 这里可以添加更新任务统计的逻辑
            // 比如更新 email_send_task 表中的统计字段
            logger.debug("更新任务统计: {}", taskId);
            
        } catch (Exception e) {
            logger.error("更新任务统计失败: {}", taskId, e);
        }
    }
    
    /**
     * 根据任务ID批量更新邮件状态
     */
    public int updateEmailStatusByTaskId(Long taskId, String status) {
        try {
            if (!isDatabaseConnectionAvailable()) {
                logger.warn("数据库连接不可用，无法批量更新任务邮件状态: {}", taskId);
                return 0;
            }
            
            List<EmailTrackRecord> records = emailTrackRecordService.selectEmailTrackRecordByTaskId(taskId);
            int updateCount = 0;
            
            for (EmailTrackRecord record : records) {
                if (emailTrackRecordService.updateEmailStatus(record.getMessageId(), status) > 0) {
                    updateCount++;
                }
            }
            
            logger.info("批量更新任务邮件状态完成: 任务ID={}, 状态={}, 更新数量={}", taskId, status, updateCount);
            return updateCount;
            
        } catch (Exception e) {
            if (isDataSourceClosedException(e)) {
                logger.warn("数据库连接已关闭，无法批量更新任务邮件状态: {}", taskId);
                return 0;
            }
            logger.error("批量更新任务邮件状态失败: {}", taskId, e);
            return 0;
        }
    }
    
    // ==================== 数据库连接检查辅助方法 ====================
    
    /**
     * 检查数据库连接是否可用
     */
    private boolean isDatabaseConnectionAvailable() {
        try {
            // 尝试执行一个简单的查询来检查数据库连接
            emailTrackRecordService.selectEmailTrackRecordList(new EmailTrackRecord());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查是否是数据源关闭异常
     */
    private boolean isDataSourceClosedException(Exception e) {
        if (e == null) {
            return false;
        }
        
        // 检查异常链中是否包含数据源关闭异常
        Throwable cause = e;
        while (cause != null) {
            String message = cause.getMessage();
            if (message != null) {
                // 检查常见的数据库连接关闭异常消息
                if (message.contains("dataSource already closed") ||
                    message.contains("DataSourceClosedException") ||
                    message.contains("Failed to obtain JDBC Connection") ||
                    message.contains("Connection is not available")) {
                    return true;
                }
            }
            
            // 检查异常类名
            String className = cause.getClass().getSimpleName();
            if (className.contains("DataSourceClosedException") ||
                className.contains("CannotGetJdbcConnectionException")) {
                return true;
            }
            
            cause = cause.getCause();
        }
        
        return false;
    }
}