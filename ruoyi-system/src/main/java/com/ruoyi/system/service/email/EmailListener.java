package com.ruoyi.system.service.email;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailStatistics;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.domain.email.EmailPersonal;
import com.ruoyi.system.domain.email.EmailSendTask;
import com.ruoyi.system.mapper.email.EmailAccountMapper;
import com.ruoyi.system.service.ISysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
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
    
    @Autowired
    private EmailTrackingDiagnostic emailTrackingDiagnostic;
    
    @Autowired
    private ISysConfigService sysConfigService;
    
    @Autowired
    private EmailReadTrackingService emailReadTrackingService;

    @Autowired
    private IEmailSendTaskService emailSendTaskService;

    @Autowired
    private ISysUserService sysUserService;
    
    @Value("${ruoyi.email.tracking.base-url:http://localhost:28081}")
    private String defaultTrackingBaseUrl;
    
    @Value("${ruoyi.email.monitor.interval:30}")
    private int defaultMonitorInterval;
    
    @Value("${ruoyi.email.dsn.check.interval:15}")
    private int defaultDsnCheckInterval;
    
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
        int monitorInterval = getMonitorInterval();
        scheduledExecutor.scheduleWithFixedDelay(this::checkAllConnections, monitorInterval, monitorInterval, TimeUnit.SECONDS);
        scheduledExecutor.scheduleWithFixedDelay(this::loadEmailAccounts, 5, 5, TimeUnit.MINUTES);
        
        logger.info("邮件监听服务启动成功，当前监控 {} 个邮箱账号，监控间隔: {} 秒", activeConnections.size(), monitorInterval);
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
                    // 减少启动时的连接建立日志输出
        if (logger.isDebugEnabled()) {
            logger.debug("为启用发送邮件的账号 {} 建立连接", account.getEmailAddress());
        }
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
        // 减少启动时的连接建立日志输出
        if (logger.isDebugEnabled()) {
            logger.debug("为邮箱账号 {} 建立连接", account.getEmailAddress());
        }
        
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
            
            // 减少SMTP连接成功的日志输出
        if (logger.isTraceEnabled()) {
            logger.trace("SMTP连接建立成功: {}", account.getEmailAddress());
        }
            
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
            
            // 减少IMAP连接成功的日志输出
        if (logger.isTraceEnabled()) {
            logger.trace("IMAP连接建立成功: {}", account.getEmailAddress());
        }
            
            // 启动邮件跟踪监控（只监控发送方邮箱）
            if (account.getTrackingEnabled() != null && "1".equals(account.getTrackingEnabled())) {
                startEmailTracking(account);
                // 减少邮件跟踪启动的日志输出
                if (logger.isDebugEnabled()) {
                    logger.debug("启动发送方邮箱的邮件跟踪监控: {}", account.getEmailAddress());
                }
            } else {
                logger.debug("跳过非发送方邮箱的邮件跟踪监控: {}", account.getEmailAddress());
            }
            
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
                statistics.setStatDate(new Date()); // 设置统计日期
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
     * 邮件发送结果
     */
    public static class EmailSendResult {
        private String messageId;
        private boolean success;
        private String errorMessage;
        
        public EmailSendResult(String messageId, boolean success, String errorMessage) {
            this.messageId = messageId;
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public String getMessageId() { return messageId; }
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        
        // 静态工厂方法
        public static EmailSendResult success(String messageId) {
            return new EmailSendResult(messageId, true, null);
        }
        
        public static EmailSendResult failure(String messageId, String errorMessage) {
            return new EmailSendResult(messageId, false, errorMessage);
        }
        
        public static EmailSendResult failure(String errorMessage) {
            return new EmailSendResult(null, false, errorMessage);
        }
    }
    
    /**
     * 发送带跟踪的邮件
     */
    public EmailSendResult sendEmailWithTracking(EmailAccount account, String to, String subject, 
                                               String content, Long taskId) {
        return sendEmailWithTracking(account, to, subject, content, taskId, null);
    }

    /**
     * 发送带跟踪的邮件（带用户ID参数）
     */
    public EmailSendResult sendEmailWithTracking(EmailAccount account, String to, String subject, 
                                               String content, Long taskId, Long userId) {
        String messageId = null;
        try {
            // 检查数据库连接是否可用
            if (!isDatabaseConnectionAvailable()) {
                logger.error("数据库连接不可用，无法发送带跟踪的邮件: {} -> {}", account.getEmailAddress(), to);
                return EmailSendResult.failure("数据库连接不可用");
            }
            
            // 记录数据库连接状态
            logger.info("数据库连接正常，准备发送邮件: {} -> {}, TaskID: {}", account.getEmailAddress(), to, taskId);
            
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
            
            // 设置个人邮件管理相关字段
            Long finalUserId = userId;
            try {
                // 如果传入了用户ID，直接使用
                if (finalUserId != null) {
                    logger.debug("使用传入的用户ID: UserID={}", finalUserId);
                } else {
                    // 尝试从任务中获取用户ID
                    if (taskId != null) {
                        try {
                            // 通过任务ID查询任务的创建者ID
                            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
                            if (task != null && task.getCreateBy() != null) {
                                // 根据createBy字段获取用户ID
                                finalUserId = getUserIdByUsername(task.getCreateBy());
                                logger.debug("从任务获取创建者信息: TaskID={}, CreateBy={}, UserID={}", taskId, task.getCreateBy(), finalUserId);
                            } else {
                                logger.warn("任务不存在或创建者为空: TaskID={}", taskId);
                            }
                        } catch (Exception taskException) {
                            logger.warn("查询任务信息失败: TaskID={}, 错误: {}", taskId, taskException.getMessage());
                        }
                    }
                    
                    // 如果无法从任务获取用户ID，尝试其他方法
                    if (finalUserId == null) {
                        try {
                            // 尝试从当前安全上下文获取用户ID
                            finalUserId = SecurityUtils.getUserId();
                            if (finalUserId != null) {
                                logger.debug("从安全上下文获取用户ID: UserID={}", finalUserId);
                            }
                        } catch (Exception e) {
                            logger.warn("从安全上下文获取用户ID失败: {}", e.getMessage());
                        }
                    }
                    
                    // 如果仍然无法获取用户ID，使用默认值
                    if (finalUserId == null) {
                        finalUserId = 1L; // 默认使用admin用户ID
                        logger.warn("无法获取用户ID，使用默认值: UserID={}", finalUserId);
                    }
                }
                
                trackRecord.setUserId(finalUserId);
                logger.info("设置邮件跟踪记录用户ID: UserID={}, TaskID={}", finalUserId, taskId);
                
            } catch (Exception e) {
                // 发生异常时使用默认用户ID
                finalUserId = 1L;
                trackRecord.setUserId(finalUserId);
                logger.error("设置用户ID时发生异常，使用默认值: UserID={}, 错误: {}", finalUserId, e.getMessage());
            }
            trackRecord.setFolderType("sent"); // 设置为发件箱
            trackRecord.setIsRead(1); // 发送的邮件默认为已读
            trackRecord.setIsStarred(0); // 默认不星标
            trackRecord.setIsImportant(0); // 默认不重要
            
            // 生成跟踪URL
            String trackingPixelUrl = generateTrackingPixelUrl(messageId);
            String trackingLinkUrl = generateTrackingLinkUrl(messageId);
            trackRecord.setTrackingPixelUrl(trackingPixelUrl);
            trackRecord.setTrackingLinkUrl(trackingLinkUrl);
            
            // 启动增强版已读跟踪
            try {
                emailReadTrackingService.startTrackingEmailRead(messageId, to, account.getEmailAddress());
                logger.info("启动增强版邮件已读跟踪: MessageID={}, 收件人={}", messageId, to);
            } catch (Exception e) {
                logger.warn("启动增强版已读跟踪失败: MessageID={}", messageId, e);
            }
            
            logger.debug("准备插入邮件跟踪记录: MessageID={}, TaskID={}", messageId, taskId);
            
            // 保存跟踪记录
            logger.info("准备插入邮件跟踪记录: MessageID={}, TaskID={}, 收件人={}, 发件人={}", 
                       messageId, taskId, to, account.getEmailAddress());
            
            int insertResult = emailTrackRecordService.insertEmailTrackRecord(trackRecord);
            if (insertResult <= 0) {
                logger.error("插入邮件跟踪记录失败: MessageID={}, TaskID={}, 插入结果={}", messageId, taskId, insertResult);
                return EmailSendResult.failure(messageId, "插入邮件跟踪记录失败");
            }
            logger.info("邮件跟踪记录插入成功: MessageID={}, TaskID={}, 插入结果={}", messageId, taskId, insertResult);
            
            // 验证记录是否真的插入成功
            EmailTrackRecord verifyRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
            if (verifyRecord == null) {
                logger.error("验证失败：邮件跟踪记录插入后无法查询到: MessageID={}, TaskID={}", messageId, taskId);
                return EmailSendResult.failure(messageId, "邮件跟踪记录插入后验证失败");
            }
            logger.info("邮件跟踪记录验证成功: MessageID={}, TaskID={}, 记录ID={}", messageId, taskId, verifyRecord.getId());
            
            // 更新状态为发送中
            emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SENDING.name());
            logger.debug("邮件状态更新为发送中: MessageID={}", messageId);
            
            // 发送邮件
            SmtpService.SmtpSendResult result = smtpService.sendEmail(account, to, subject, 
                addTrackingToContent(content, trackingPixelUrl, trackingLinkUrl), messageId);
            
            if (result.isSuccess()) {
                // 更新状态为发送成功
                emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_SUCCESS.name());
                logger.info("邮件发送成功: {} -> {}, MessageID: {}", account.getEmailAddress(), to, messageId);
                
                // 验证记录是否真的存在
                EmailTrackRecord finalRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
                if (finalRecord != null) {
                    logger.info("邮件发送成功并验证记录存在: MessageID={}, 记录ID={}, 状态={}", 
                               messageId, finalRecord.getId(), finalRecord.getStatus());
                } else {
                    logger.error("邮件发送成功但记录验证失败: MessageID={}", messageId);
                }
                
                // 注意：送达状态将通过DSN（Delivery Status Notification）来更新
                // 系统会监听IMAP收件箱中的DSN邮件，并自动更新邮件状态
                // 如果Gmail等现代邮件服务不发送DSN，可以考虑使用其他方式检测送达状态
                
                // 启动延迟送达状态检查（作为DSN的备用机制）
                scheduleDeliveryStatusCheck(messageId, account, to);
                
                return EmailSendResult.success(messageId);
            } else {
                // 更新状态为发送失败
                emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_FAILED.name());
                logger.error("邮件发送失败: {} -> {}, MessageID: {}, 错误: {}", 
                           account.getEmailAddress(), to, messageId, result.getErrorMessage());
                
                // 验证失败记录是否真的存在
                EmailTrackRecord failedRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
                if (failedRecord != null) {
                    logger.info("邮件发送失败但记录存在: MessageID={}, 记录ID={}, 状态={}", 
                               messageId, failedRecord.getId(), failedRecord.getStatus());
                } else {
                    logger.error("邮件发送失败且记录验证失败: MessageID={}", messageId);
                }
                
                return EmailSendResult.failure(messageId, result.getErrorMessage());
            }
            
        } catch (Exception e) {
            logger.error("发送带跟踪的邮件失败: {} -> {}, MessageID: {}, TaskID: {}", 
                        account.getEmailAddress(), to, messageId, taskId, e);
            
            // 如果跟踪记录已创建但发送失败，尝试更新状态
            if (messageId != null) {
                try {
                    emailTrackRecordService.updateEmailStatus(messageId, EmailStatus.SEND_FAILED.name());
                    logger.info("已更新失败邮件的跟踪记录状态: MessageID={}", messageId);
                    return EmailSendResult.failure(messageId, "发送邮件时发生异常: " + e.getMessage());
                } catch (Exception updateException) {
                    logger.error("更新失败邮件跟踪记录状态时发生异常: MessageID={}", messageId, updateException);
                    return EmailSendResult.failure(messageId, "发送邮件时发生异常: " + e.getMessage());
                }
            }
            
            return EmailSendResult.failure("发送邮件时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 创建收件箱记录
     */
    private void createInboxRecord(MimeMessage mimeMessage, EmailAccount account) {
        try {
            String messageId = mimeMessage.getMessageID();
            String subject = decodeMimeText(mimeMessage.getSubject());
            String from = extractEmailAddress(mimeMessage.getFrom());
            String to = extractEmailAddress(mimeMessage.getRecipients(Message.RecipientType.TO));
            
            // 检查是否已经存在该邮件的记录
            EmailTrackRecord existingRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
            if (existingRecord != null) {
                logger.debug("邮件记录已存在，跳过创建: {}", messageId);
                return;
            }
            
            // 创建收件箱记录
            EmailTrackRecord inboxRecord = new EmailTrackRecord();
            inboxRecord.setMessageId(messageId);
            inboxRecord.setSubject(subject);
            inboxRecord.setSender(from);
            inboxRecord.setRecipient(to);
            inboxRecord.setContent(extractEmailContent(mimeMessage));
            inboxRecord.setStatus("RECEIVED");
            // inboxRecord.setReceiveTime(new Date()); // 暂时注释，等数据库字段添加后再启用
            inboxRecord.setAccountId(account.getAccountId());
            inboxRecord.setCreateBy("system");
            inboxRecord.setCreateTime(new Date());
            
            // 设置个人邮件管理相关字段
            // 根据收件人邮箱地址查找对应的用户ID
            Long userId = getUserIdByEmailAddress(to);
            inboxRecord.setUserId(userId);
            inboxRecord.setFolderType("inbox"); // 设置为收件箱
            inboxRecord.setIsRead(0); // 收件箱邮件默认为未读
            inboxRecord.setIsStarred(0); // 默认不星标
            inboxRecord.setIsImportant(0); // 默认不重要
            
            // 保存收件箱记录
            int insertResult = emailTrackRecordService.insertEmailTrackRecord(inboxRecord);
            if (insertResult > 0) {
                logger.info("收件箱记录创建成功: {} - {}", subject, messageId);
            } else {
                logger.warn("收件箱记录创建失败: {}", messageId);
            }
            
        } catch (Exception e) {
            logger.error("创建收件箱记录失败", e);
        }
    }
    
    /**
     * 提取邮件内容
     */
    private String extractEmailContent(MimeMessage mimeMessage) {
        try {
            Object content = mimeMessage.getContent();
            if (content instanceof String) {
                return (String) content;
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                StringBuilder contentBuilder = new StringBuilder();
                for (int i = 0; i < multipart.getCount(); i++) {
                    Part bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getContentType().startsWith("text/")) {
                        contentBuilder.append(bodyPart.getContent().toString());
                    }
                }
                return contentBuilder.toString();
            }
            return "邮件内容解析失败";
        } catch (Exception e) {
            logger.error("提取邮件内容失败", e);
            return "邮件内容提取失败";
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
                    logger.info("邮件被识别为回复邮件，开始处理: {}", messageId);
                    
            // 提取原始邮件的Message-ID
            String originalMessageId = extractOriginalMessageIdFromReply(mimeMessage);
            if (originalMessageId != null) {
                logger.info("准备更新原始邮件的回复状态: {} -> {}", originalMessageId, messageId);
                
                // 更新原始邮件的回复状态
                int updateResult = emailTrackRecordService.recordEmailReplied(originalMessageId);
                if (updateResult > 0) {
                    logger.info("✓ 成功更新邮件回复状态: {} -> {} (影响行数: {})", originalMessageId, messageId, updateResult);
                } else {
                    logger.warn("✗ 直接匹配失败，尝试模糊匹配: {} -> {}", originalMessageId, messageId);
                    
                    // 如果直接匹配失败，尝试模糊匹配
                    updateResult = emailTrackRecordService.recordEmailRepliedByFuzzyMatch(originalMessageId, mimeMessage.getSubject());
                    if (updateResult > 0) {
                        logger.info("✓ 模糊匹配成功更新邮件回复状态: {} -> {} (影响行数: {})", originalMessageId, messageId, updateResult);
                    } else {
                        logger.warn("✗ 模糊匹配也失败: {} -> {}", originalMessageId, messageId);
                    }
                }
                
                // 回复邮件不创建收件箱记录，只更新原始邮件的回复状态
                // 回复邮件应该在发件箱中显示
            } else {
                logger.warn("✗ 回复邮件中未找到原始Message-ID: {}，将作为普通邮件处理", messageId);
                // 如果无法确定是回复邮件，则作为普通邮件处理
                createInboxRecord(mimeMessage, account);
            }
                } else {
                    logger.debug("邮件被识别为非回复邮件，创建收件箱记录: {}", messageId);
                    // 非回复邮件，创建收件箱记录
                    createInboxRecord(mimeMessage, account);
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
                    
                    // 首先处理邮件回复检测（这是关键！）
                    processIncomingMessage(mimeMessage, account);
                    
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
            if (message instanceof MimeMessage) {
                MimeMessage mimeMessage = (MimeMessage) message;
                
                // 检查是否是DSN邮件
                if (isDSNEmail(mimeMessage)) {
                    logger.info("检测到DSN邮件，开始解析: {}", mimeMessage.getSubject());
                    
                    // 解析DSN内容
                    String originalMessageId = extractOriginalMessageIdFromDSN(mimeMessage);
                    String deliveryStatus = extractDeliveryStatusFromDSN(mimeMessage);
                    String recipientEmail = extractRecipientFromDSN(mimeMessage);
                    
                    logger.info("DSN解析结果 - 原始MessageID: {}, 状态: {}, 收件人: {}", 
                        originalMessageId, deliveryStatus, recipientEmail);
                    
                    // 增强DSN状态解析
                    if (deliveryStatus == null || deliveryStatus.trim().isEmpty()) {
                        deliveryStatus = extractDeliveryStatusFromContent(mimeMessage);
                        logger.info("从邮件内容重新解析DSN状态: {}", deliveryStatus);
                    }
                    
                    if (originalMessageId != null) {
                        // 查找对应的跟踪记录
                        EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(originalMessageId);
                        if (trackRecord != null) {
                            // 根据DSN状态更新邮件状态
                            String newStatus = null;
                            switch (deliveryStatus.toLowerCase()) {
                                case "delivered":
                                case "success":
                                case "2.0.0":
                                    newStatus = "DELIVERED";
                                    break;
                                case "failed":
                                case "bounced":
                                case "rejected":
                                case "5.0.0":
                                case "5.1.1":
                                case "5.2.1":
                                    newStatus = "BOUNCED";
                                    break;
                                case "deferred":
                                case "delayed":
                                case "4.0.0":
                                case "4.2.2":
                                    newStatus = "DEFERRED";
                                    break;
                                default:
                                    logger.warn("未知的DSN状态: {} for message: {}", deliveryStatus, originalMessageId);
                                    return;
                            }
                            
                            // 更新邮件状态
                            if (newStatus != null) {
                                emailTrackRecordService.updateEmailStatus(originalMessageId, newStatus);
                                logger.info("DSN邮件处理完成: {} -> {} (任务ID: {})", 
                                           originalMessageId, newStatus, trackRecord.getTaskId());
                                
                                // 更新任务统计
                                updateTaskStatistics(trackRecord.getTaskId());
                            }
                        } else {
                            logger.debug("未找到对应的邮件跟踪记录: {}", originalMessageId);
                        }
                    } else {
                        logger.warn("无法从DSN中提取原始MessageID");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理送达状态通知失败", e);
        }
    }
    
    /**
     * 检查是否是DSN邮件
     */
    private boolean isDSNEmail(MimeMessage message) {
        try {
            String subject = message.getSubject();
            String contentType = message.getContentType();
            Address[] fromHeaders = message.getFrom();
            
            // 检查主题
            boolean subjectMatch = subject != null && (
                subject.toLowerCase().contains("delivery status notification") ||
                subject.toLowerCase().contains("mail delivery failure") ||
                subject.toLowerCase().contains("undelivered mail") ||
                subject.toLowerCase().contains("mail system error") ||
                subject.toLowerCase().contains("delivery failure") ||
                subject.toLowerCase().contains("returned mail")
            );
            
            // 检查内容类型
            boolean contentTypeMatch = contentType != null && (
                contentType.toLowerCase().contains("multipart/report") ||
                contentType.toLowerCase().contains("message/delivery-status")
            );
            
            // 检查发件人（DSN邮件通常来自邮件系统）
            boolean fromMatch = false;
            if (fromHeaders != null && fromHeaders.length > 0) {
                String from = fromHeaders[0].toString().toLowerCase();
                fromMatch = from.contains("mailer-daemon") || 
                           from.contains("postmaster") ||
                           from.contains("mail delivery subsystem") ||
                           from.contains("mail system") ||
                           from.contains("noreply") ||
                           from.contains("no-reply");
            }
            
            return subjectMatch || contentTypeMatch || fromMatch;
        } catch (Exception e) {
            logger.warn("检查DSN邮件时发生异常", e);
            return false;
        }
    }
    
    /**
     * 从回复邮件中提取原始Message-ID
     */
    private String extractOriginalMessageIdFromReply(MimeMessage message) {
        try {
            String currentMessageId = message.getMessageID();
            logger.info("开始从回复邮件提取原始Message-ID: 当前邮件ID={}", currentMessageId);
            
            // 方式1: 检查In-Reply-To头
            String[] inReplyToHeaders = message.getHeader("In-Reply-To");
            logger.info("In-Reply-To头检查: {}", inReplyToHeaders != null ? String.join(", ", inReplyToHeaders) : "null");
            
            if (inReplyToHeaders != null && inReplyToHeaders.length > 0) {
                String originalMessageId = extractMessageIdFromHeader(inReplyToHeaders[0]);
                if (originalMessageId != null) {
                    logger.info("✓ 从In-Reply-To头提取到原始Message-ID: {} -> {}", currentMessageId, originalMessageId);
                    return originalMessageId;
                } else {
                    logger.warn("In-Reply-To头存在但无法提取Message-ID: {}", inReplyToHeaders[0]);
                }
            }
            
            // 方式2: 检查References头
            String[] referencesHeaders = message.getHeader("References");
            logger.info("References头检查: {}", referencesHeaders != null ? String.join(", ", referencesHeaders) : "null");
            
            if (referencesHeaders != null && referencesHeaders.length > 0) {
                String references = referencesHeaders[0];
                // References头可能包含多个Message-ID，取最后一个
                String[] messageIds = references.split("\\s+");
                logger.info("References头解析结果: 包含{}个Message-ID", messageIds.length);
                
                if (messageIds.length > 0) {
                    String originalMessageId = extractMessageIdFromHeader(messageIds[messageIds.length - 1]);
                    if (originalMessageId != null) {
                        logger.info("✓ 从References头提取到原始Message-ID: {} -> {}", currentMessageId, originalMessageId);
                        return originalMessageId;
                    } else {
                        logger.warn("References头存在但无法提取Message-ID: {}", messageIds[messageIds.length - 1]);
                    }
                }
            }
            
            // 方式3: 从主题中提取（如果主题包含Message-ID）
            String subject = message.getSubject();
            logger.info("主题检查: {}", subject);
            
            if (subject != null) {
                String originalMessageId = extractMessageIdFromSubject(subject);
                if (originalMessageId != null) {
                    logger.info("✓ 从主题中提取到原始Message-ID: {} -> {}", currentMessageId, originalMessageId);
                    return originalMessageId;
                } else {
                    logger.debug("主题中未找到Message-ID");
                }
            }
            
            logger.warn("✗ 无法从回复邮件中提取原始Message-ID: {}", currentMessageId);
            return null;
        } catch (Exception e) {
            try {
                logger.error("提取原始Message-ID时发生异常: MessageID={}", message.getMessageID(), e);
            } catch (Exception ex) {
                logger.error("提取原始Message-ID时发生异常: 无法获取MessageID", e);
            }
            return null;
        }
    }
    
    /**
     * 从主题中提取Message-ID
     */
    private String extractMessageIdFromSubject(String subject) {
        if (subject == null) {
            return null;
        }
        
        // 尝试从主题中提取Message-ID格式的字符串
        java.util.regex.Pattern messageIdPattern = java.util.regex.Pattern.compile("<([^>]+@[^>]+)>");
        java.util.regex.Matcher matcher = messageIdPattern.matcher(subject);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * 从DSN邮件中提取原始Message-ID
     */
    private String extractOriginalMessageIdFromDSN(MimeMessage message) {
        try {
            // 首先尝试从邮件头中获取
            String[] originalMessageIdHeaders = message.getHeader("Original-Message-ID");
            if (originalMessageIdHeaders != null && originalMessageIdHeaders.length > 0) {
                return extractMessageIdFromHeader(originalMessageIdHeaders[0]);
            }
            
            // 从邮件内容中提取
            String content = extractMessageContent(message);
            if (content != null) {
                // 使用正则表达式匹配Message-ID
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "(?:Original-Message-ID|Message-ID):\\s*<([^>]+)>", 
                    java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
            
            return null;
        } catch (Exception e) {
            logger.warn("提取原始Message-ID失败", e);
            return null;
        }
    }
    
    /**
     * 从DSN邮件中提取送达状态
     */
    private String extractDeliveryStatusFromDSN(MimeMessage message) {
        try {
            String content = extractMessageContent(message);
            if (content != null) {
                // 使用正则表达式匹配状态
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "(?:Status|Diagnostic-Code|Action):\\s*(\\d+\\.\\d+\\.\\d+|[^\\n\\r]+)", 
                    java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                }
            }
            return null;
        } catch (Exception e) {
            logger.warn("提取送达状态失败", e);
            return null;
        }
    }
    
    /**
     * 从邮件内容中提取DSN状态
     */
    private String extractDeliveryStatusFromContent(MimeMessage message) {
        try {
            Object content = message.getContent();
            String contentText = null;
            
            if (content instanceof String) {
                contentText = (String) content;
            } else if (content instanceof javax.mail.Multipart) {
                javax.mail.Multipart multipart = (javax.mail.Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    javax.mail.BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getContentType().toLowerCase().contains("text/plain")) {
                        Object partContent = bodyPart.getContent();
                        if (partContent instanceof String) {
                            contentText = (String) partContent;
                            break;
                        }
                    }
                }
            }
            
            if (contentText != null) {
                // 查找常见的DSN状态关键词
                String[] deliveredKeywords = {"delivered", "success", "2.0.0", "delivery successful", "邮件已送达"};
                String[] failedKeywords = {"failed", "bounced", "rejected", "5.0.0", "5.1.1", "5.2.1", "delivery failed", "邮件发送失败"};
                String[] deferredKeywords = {"deferred", "delayed", "4.0.0", "4.2.2", "temporarily unavailable", "暂时不可用"};
                
                String lowerContent = contentText.toLowerCase();
                
                for (String keyword : deliveredKeywords) {
                    if (lowerContent.contains(keyword.toLowerCase())) {
                        return "DELIVERED";
                    }
                }
                
                for (String keyword : failedKeywords) {
                    if (lowerContent.contains(keyword.toLowerCase())) {
                        return "BOUNCED";
                    }
                }
                
                for (String keyword : deferredKeywords) {
                    if (lowerContent.contains(keyword.toLowerCase())) {
                        return "DEFERRED";
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            logger.debug("从邮件内容提取DSN状态失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从DSN邮件中提取收件人
     */
    private String extractRecipientFromDSN(MimeMessage message) {
        try {
            String content = extractMessageContent(message);
            if (content != null) {
                // 使用正则表达式匹配收件人
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "(?:Final-Recipient|Original-Recipient):\\s*(?:rfc822;\\s*)?([^\\s\\n\\r]+)", 
                    java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                }
            }
            return null;
        } catch (Exception e) {
            logger.warn("提取收件人失败", e);
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
        
        // 提取<...>中的内容
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<([^>]+)>");
        java.util.regex.Matcher matcher = pattern.matcher(header);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return header.trim();
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
        try {
            String subject = message.getSubject();
            String messageId = null;
            if (message instanceof MimeMessage) {
                messageId = ((MimeMessage) message).getMessageID();
            }
            
            // 方式1: 检查主题是否以Re:或回复:开头
            boolean isReplyBySubject = subject != null && (
                subject.toLowerCase().startsWith("re:") || 
                subject.toLowerCase().startsWith("回复:") ||
                subject.toLowerCase().startsWith("re：") ||
                subject.toLowerCase().startsWith("回复：")
            );
            
            // 方式2: 检查In-Reply-To头
            boolean isReplyByHeader = false;
            String[] inReplyToHeaders = message.getHeader("In-Reply-To");
            if (inReplyToHeaders != null && inReplyToHeaders.length > 0) {
                isReplyByHeader = true;
            }
            
            // 方式3: 检查References头
            boolean isReplyByReferences = false;
            String[] referencesHeaders = message.getHeader("References");
            if (referencesHeaders != null && referencesHeaders.length > 0) {
                isReplyByReferences = true;
            }
            
            boolean isReply = isReplyBySubject || isReplyByHeader || isReplyByReferences;
            
            // 增加详细的调试日志
            logger.info("检查邮件是否为回复: MessageID={}, 主题={}, 检测结果={}", 
                       messageId, subject, isReply);
            logger.info("回复检测详情: 主题检测={}, In-Reply-To检测={}, References检测={}", 
                       isReplyBySubject, isReplyByHeader, isReplyByReferences);
            
            if (inReplyToHeaders != null && inReplyToHeaders.length > 0) {
                logger.info("In-Reply-To头内容: {}", String.join(", ", inReplyToHeaders));
            }
            
            if (referencesHeaders != null && referencesHeaders.length > 0) {
                logger.info("References头内容: {}", String.join(", ", referencesHeaders));
            }
            
            if (isReply) {
                logger.info("✓ 检测到回复邮件: {} - 主题: {} - 检测方式: 主题={}, In-Reply-To={}, References={}", 
                           messageId, subject, isReplyBySubject, isReplyByHeader, isReplyByReferences);
            } else {
                logger.debug("✗ 非回复邮件: {} - 主题: {}", messageId, subject);
            }
            
            return isReply;
        } catch (Exception e) {
            logger.error("检查回复邮件时发生异常: MessageID={}", 
                        message instanceof MimeMessage ? ((MimeMessage) message).getMessageID() : "unknown", e);
            return false;
        }
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
     * 根据用户名获取用户ID
     */
    private Long getUserIdByUsername(String username) {
        try {
            SysUser user = sysUserService.selectUserByUserName(username);
            if (user != null) {
                return user.getUserId();
            }
        } catch (Exception e) {
            logger.warn("根据用户名获取用户ID失败: username={}, 错误: {}", username, e.getMessage());
        }
        // 如果获取失败，返回当前登录用户ID
        return SecurityUtils.getUserId();
    }

    /**
     * 根据邮箱地址获取用户ID
     */
    private Long getUserIdByEmailAddress(String emailAddress) {
        try {
            // 尝试从用户表中查找
            // 这里可以根据实际业务逻辑调整，比如根据邮箱前缀匹配用户名
            String username = extractUsernameFromEmail(emailAddress);
            if (username != null) {
                SysUser user = sysUserService.selectUserByUserName(username);
                if (user != null) {
                    return user.getUserId();
                }
            }
            
            // 也可以尝试根据邮箱地址直接查找用户
            // 这里可以根据实际业务逻辑调整，暂时注释掉，因为ISysUserService没有selectUserByEmail方法
            // SysUser userByEmail = sysUserService.selectUserByEmail(emailAddress);
            // if (userByEmail != null) {
            //     return userByEmail.getUserId();
            // }
        } catch (Exception e) {
            logger.warn("根据邮箱地址获取用户ID失败: email={}, 错误: {}", emailAddress, e.getMessage());
        }
        
        // 如果都获取失败，返回默认用户ID（通常是admin用户）
        return 1L;
    }

    /**
     * 从邮箱地址中提取用户名
     */
    private String extractUsernameFromEmail(String emailAddress) {
        if (emailAddress == null || !emailAddress.contains("@")) {
            return null;
        }
        return emailAddress.substring(0, emailAddress.indexOf("@"));
    }
    
    /**
     * 生成跟踪像素URL
     */
    private String generateTrackingPixelUrl(String messageId) {
        String baseUrl = getTrackingBaseUrl();
        // 使用增强版跟踪接口，支持多种检测方式
        return baseUrl + "/email/read-tracking/open?msgid=" + messageId + "&method=pixel";
    }
    
    /**
     * 生成跟踪链接URL
     */
    private String generateTrackingLinkUrl(String messageId) {
        String baseUrl = getTrackingBaseUrl();
        return baseUrl + "/email/tracking/click?msgid=" + messageId;
    }
    
    /**
     * 获取跟踪服务的基础URL
     * 优先从数据库配置获取，如果获取不到再从yml配置文件获取
     */
    private String getTrackingBaseUrl() {
        try {
            // 首先尝试从数据库配置中获取
            String baseUrl = sysConfigService.selectConfigByKey("email.tracking.base.url");
            if (baseUrl != null && !baseUrl.trim().isEmpty()) {
                // 确保URL不以斜杠结尾
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                logger.debug("从数据库配置获取邮件跟踪基础URL: {}", baseUrl);
                return baseUrl;
            }
        } catch (Exception e) {
            logger.warn("从数据库配置获取邮件跟踪基础URL失败，将使用yml配置", e);
        }
        
        // 如果数据库配置获取失败，使用yml配置文件中的默认值
        String baseUrl = defaultTrackingBaseUrl;
        if (baseUrl != null && baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        logger.debug("使用yml配置的邮件跟踪基础URL: {}", baseUrl);
        return baseUrl;
    }
    
    /**
     * 获取邮件监控间隔（秒）
     * 优先从数据库配置获取，如果获取不到再从yml配置文件获取
     */
    private int getMonitorInterval() {
        try {
            // 首先尝试从数据库配置中获取
            String intervalStr = sysConfigService.selectConfigByKey("email.monitor.interval");
            if (intervalStr != null && !intervalStr.trim().isEmpty()) {
                int interval = Integer.parseInt(intervalStr.trim());
                if (interval > 0) {
                    logger.debug("从数据库配置获取邮件监控间隔: {} 秒", interval);
                    return interval;
                }
            }
        } catch (Exception e) {
            logger.warn("从数据库配置获取邮件监控间隔失败，将使用yml配置", e);
        }
        
        // 如果数据库配置获取失败，使用yml配置文件中的默认值
        int interval = defaultMonitorInterval;
        logger.debug("使用yml配置的邮件监控间隔: {} 秒", interval);
        return interval;
    }
    
    /**
     * 获取DSN检查间隔（秒）
     * 优先从数据库配置获取，如果获取不到再从yml配置文件获取
     */
    private int getDsnCheckInterval() {
        try {
            // 首先尝试从数据库配置中获取
            String intervalStr = sysConfigService.selectConfigByKey("email.dsn.check.interval");
            if (intervalStr != null && !intervalStr.trim().isEmpty()) {
                int interval = Integer.parseInt(intervalStr.trim());
                if (interval > 0) {
                    logger.debug("从数据库配置获取DSN检查间隔: {} 秒", interval);
                    return interval;
                }
            }
        } catch (Exception e) {
            logger.warn("从数据库配置获取DSN检查间隔失败，将使用yml配置", e);
        }
        
        // 如果数据库配置获取失败，使用yml配置文件中的默认值
        int interval = defaultDsnCheckInterval;
        logger.debug("使用yml配置的DSN检查间隔: {} 秒", interval);
        return interval;
    }
    
    /**
     * 在邮件内容中添加跟踪
     */
    private String addTrackingToContent(String content, String pixelUrl, String linkUrl) {
        if (content == null || content.trim().isEmpty()) {
            return content;
        }
        
        // 检查内容是否已经是HTML格式
        boolean isHtml = content.toLowerCase().contains("<html") || 
                        content.toLowerCase().contains("<body") || 
                        content.toLowerCase().contains("<p>") ||
                        content.toLowerCase().contains("<div");
        
        if (isHtml) {
            // 如果是HTML内容，在</body>标签前添加跟踪像素
            String trackingPixel = "<img src=\"" + pixelUrl + "\" width=\"1\" height=\"1\" style=\"display:none;\">";
            
            if (content.toLowerCase().contains("</body>")) {
                // 在</body>前添加跟踪像素
                return content.replaceAll("(?i)</body>", trackingPixel + "\n</body>");
            } else if (content.toLowerCase().contains("</html>")) {
                // 如果没有</body>但有</html>，在</html>前添加
                return content.replaceAll("(?i)</html>", trackingPixel + "\n</html>");
            } else {
                // 如果都没有，在内容末尾添加
                return content + "\n" + trackingPixel;
            }
        } else {
            // 如果是纯文本内容，转换为HTML格式
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<!DOCTYPE html>\n");
            htmlContent.append("<html>\n");
            htmlContent.append("<head>\n");
            htmlContent.append("<meta charset=\"UTF-8\">\n");
            htmlContent.append("</head>\n");
            htmlContent.append("<body>\n");
            
            // 将换行符转换为HTML段落
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    htmlContent.append("<br>\n");
                } else {
                    htmlContent.append("<p>").append(escapeHtml(line)).append("</p>\n");
                }
            }
            
            // 添加跟踪像素
            htmlContent.append("<img src=\"").append(pixelUrl).append("\" width=\"1\" height=\"1\" style=\"display:none;\">\n");
            htmlContent.append("</body>\n");
            htmlContent.append("</html>");
            
            return htmlContent.toString();
        }
    }
    
    /**
     * HTML转义
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
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
                public void onDeliveryConfirmed(EmailAccount account, String messageId, Message sentMessage) {
                    handleDeliveryConfirmed(account, messageId, sentMessage);
                }
                
                @Override
                public void onBounceReceived(EmailAccount account, ImapService.BounceInfo bounceInfo) {
                    handleBounceReceived(account, bounceInfo);
                }
                
                @Override
                public void onTrackingError(EmailAccount account, Exception e) {
                    logger.error("邮件跟踪监控错误: {} - {}", account.getEmailAddress(), e.getMessage());
                }
            };
            
            imapService.startEmailTracking(account, callback);
            // 减少邮件跟踪启动的日志输出
        if (logger.isDebugEnabled()) {
            logger.debug("启动邮件跟踪监控: {}", account.getEmailAddress());
        }
            
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
            String status = dsnInfo.getStatus();
            
            if (originalMessageId == null || originalMessageId.trim().isEmpty()) {
                logger.warn("DSN邮件中未找到原始Message-ID: {}", dsnInfo.getDsnMessageId());
                return;
            }
            
            // 查找对应的跟踪记录
            EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(originalMessageId);
            if (trackRecord == null) {
                logger.debug("未找到对应的邮件跟踪记录: {}", originalMessageId);
                return;
            }
            
            // 根据DSN状态更新邮件状态
            String newStatus = null;
            switch (status.toLowerCase()) {
                case "delivered":
                case "success":
                    newStatus = "DELIVERED";
                    break;
                case "failed":
                case "bounced":
                case "rejected":
                    newStatus = "BOUNCED";
                    break;
                case "deferred":
                case "delayed":
                    newStatus = "DEFERRED";
                    break;
                default:
                    logger.warn("未知的DSN状态: {} for message: {}", status, originalMessageId);
                    return;
            }
            
            // 更新邮件状态
            if (newStatus != null) {
                emailTrackRecordService.updateEmailStatus(originalMessageId, newStatus);
                logger.info("DSN邮件处理完成: {} -> {} (任务ID: {})", 
                           originalMessageId, newStatus, trackRecord.getTaskId());
                
                // 更新任务统计
                updateTaskStatistics(trackRecord.getTaskId());
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
            
            if (originalMessageId == null || originalMessageId.trim().isEmpty()) {
                logger.warn("回复邮件中未找到原始Message-ID");
                return;
            }
            
            // 查找对应的跟踪记录
            EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(originalMessageId);
            if (trackRecord == null) {
                logger.debug("未找到对应的邮件跟踪记录: {}", originalMessageId);
                return;
            }
            
            // 更新邮件状态为已回复
            emailTrackRecordService.recordEmailReplied(originalMessageId);
            logger.info("回复邮件处理完成: {} (任务ID: {})", originalMessageId, trackRecord.getTaskId());
            
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
     * 处理送达确认
     */
    private void handleDeliveryConfirmed(EmailAccount account, String messageId, Message sentMessage) {
        try {
            logger.info("收到送达确认: 账户={}, 邮件ID={}", account.getEmailAddress(), messageId);
            
            // 更新邮件送达状态
            emailTrackRecordService.recordEmailDelivered(messageId);
            
            // 查找对应的跟踪记录并更新任务统计
            EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
            if (trackRecord != null) {
                updateTaskStatistics(trackRecord.getTaskId());
                logger.info("通过IMAP已发送文件夹确认邮件送达: MessageID={}, 收件人={}", 
                    messageId, trackRecord.getRecipient());
            }
            
        } catch (Exception e) {
            logger.error("处理送达确认失败: MessageID={}", messageId, e);
        }
    }
    
    /**
     * 处理退信
     */
    private void handleBounceReceived(EmailAccount account, ImapService.BounceInfo bounceInfo) {
        try {
            logger.info("收到退信: 账户={}, 原始邮件ID={}, 退信原因={}", 
                       account.getEmailAddress(), bounceInfo.getOriginalMessageId(), bounceInfo.getBounceReason());
            
            // 更新邮件状态为退信
            if (bounceInfo.getOriginalMessageId() != null) {
                emailTrackRecordService.recordEmailBounced(bounceInfo.getOriginalMessageId(), bounceInfo.getBounceReason());
                
                // 查找对应的跟踪记录并更新任务统计
                EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(bounceInfo.getOriginalMessageId());
                if (trackRecord != null) {
                    updateTaskStatistics(trackRecord.getTaskId());
                    logger.info("通过退信邮件确认邮件发送失败: MessageID={}, 收件人={}, 原因={}", 
                        bounceInfo.getOriginalMessageId(), trackRecord.getRecipient(), bounceInfo.getBounceReason());
                }
            }
            
        } catch (Exception e) {
            logger.error("处理退信失败: MessageID={}", bounceInfo.getOriginalMessageId(), e);
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
            
            // 如果邮件被标记为重要（flagged），可以记录为重要邮件
            if (isFlagged && trackRecord.getStatus().equals("DELIVERED")) {
                // 可以添加一个新的状态或字段来记录重要邮件
                logger.debug("邮件被标记为重要: {} (任务ID: {})", messageId, trackRecord.getTaskId());
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
    
    /**
     * 测试邮件跟踪功能
     */
    public void testEmailTracking(Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                logger.error("未找到邮箱账号: {}", accountId);
                return;
            }
            
            logger.info("开始测试邮件跟踪功能: {}", account.getEmailAddress());
            imapService.testEmailTracking(account);
            
        } catch (Exception e) {
            logger.error("测试邮件跟踪功能失败: accountId={}", accountId, e);
        }
    }
    
    /**
     * 强制扫描指定邮件的状态
     */
    public void forceScanEmailStatus(Long accountId, String messageId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                logger.error("未找到邮箱账号: {}", accountId);
                return;
            }
            
            logger.info("强制扫描邮件状态: accountId={}, messageId={}", accountId, messageId);
            imapService.forceScanEmailStatus(account, messageId);
            
        } catch (Exception e) {
            logger.error("强制扫描邮件状态失败: accountId={}, messageId={}", accountId, messageId, e);
        }
    }
    
    /**
     * 诊断邮件跟踪问题
     */
    public void diagnoseEmailTracking(Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                logger.error("未找到邮箱账号: {}", accountId);
                return;
            }
            
            logger.info("开始诊断邮件跟踪问题: accountId={}, email={}", accountId, account.getEmailAddress());
            
            // 使用新的诊断服务
            emailTrackingDiagnostic.performFullDiagnostic();
            
        } catch (Exception e) {
            logger.error("诊断邮件跟踪问题失败: accountId={}", accountId, e);
        }
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
     * 安排延迟送达状态检查（作为DSN的备用机制）
     * 由于Gmail等现代邮件服务可能不发送DSN通知，我们使用延迟检查作为备用
     */
    private void scheduleDeliveryStatusCheck(String messageId, EmailAccount account, String recipient) {
        try {
            // 延迟5分钟后检查送达状态
            scheduledExecutor.schedule(() -> {
                try {
                    checkDeliveryStatusWithoutDSN(messageId, account, recipient);
                } catch (Exception e) {
                    logger.error("延迟送达状态检查失败: MessageID={}", messageId, e);
                }
            }, 1, TimeUnit.MINUTES);
            
            logger.debug("已安排延迟送达状态检查: MessageID={}, 5分钟后执行", messageId);
            
        } catch (Exception e) {
            logger.error("安排延迟送达状态检查失败: MessageID={}", messageId, e);
        }
    }
    
    /**
     * 不使用DSN检查送达状态（备用机制）
     * 通过检查邮件跟踪记录的状态来判断是否送达
     */
    private void checkDeliveryStatusWithoutDSN(String messageId, EmailAccount account, String recipient) {
        try {
            // 检查邮件跟踪记录
            EmailTrackRecord trackRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
            if (trackRecord == null) {
                logger.debug("未找到邮件跟踪记录: MessageID={}", messageId);
                return;
            }
            
            // 如果状态仍然是SEND_SUCCESS且没有送达时间，则假设已送达
            if ("SEND_SUCCESS".equals(trackRecord.getStatus()) && trackRecord.getDeliveredTime() == null) {
                // 更新为已送达状态
                emailTrackRecordService.recordEmailDelivered(messageId);
                logger.info("通过备用机制更新邮件送达状态: MessageID={}, 收件人={}", messageId, recipient);
                
                // 更新任务统计
                if (trackRecord.getTaskId() != null) {
                    updateTaskStatistics(trackRecord.getTaskId());
                }
            } else {
                logger.debug("邮件状态无需更新: MessageID={}, 当前状态={}, 送达时间={}", 
                           messageId, trackRecord.getStatus(), trackRecord.getDeliveredTime());
            }
            
        } catch (Exception e) {
            logger.error("检查送达状态失败: MessageID={}", messageId, e);
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
            List<EmailTrackRecord> testList = emailTrackRecordService.selectEmailTrackRecordList(new EmailTrackRecord());
            logger.debug("数据库连接检查成功，查询到 {} 条记录", testList != null ? testList.size() : 0);
            return true;
        } catch (Exception e) {
            logger.error("数据库连接检查失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 手动检测回复邮件
     * 用于在IMAP连接失败时作为备用机制
     */
    public void manualReplyDetection(Long taskId) {
        try {
            logger.info("开始手动检测回复邮件，任务ID: {}", taskId);
            
            // 检查数据库连接
            if (!isDatabaseConnectionAvailable()) {
                logger.error("数据库连接不可用，无法进行手动回复检测");
                return;
            }
            
            // 获取任务的所有邮件跟踪记录
            List<EmailTrackRecord> trackRecords = emailTrackRecordService.selectEmailTrackRecordByTaskId(taskId);
            if (trackRecords.isEmpty()) {
                logger.warn("任务 {} 没有找到邮件跟踪记录", taskId);
                return;
            }
            
            logger.info("找到 {} 条邮件跟踪记录，开始检测回复状态", trackRecords.size());
            
            // 获取所有发件人账户
            List<EmailAccount> senderAccounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            int detectedReplies = 0;
            
            for (EmailAccount account : senderAccounts) {
                if (account.getTrackingEnabled() != null && "1".equals(account.getTrackingEnabled())) {
                    try {
                        // 尝试连接IMAP并检测回复
                        Store store = imapService.createPersistentConnectionWithRetry(account, 2);
                        if (store != null && store.isConnected()) {
                            Folder folder = imapService.openInboxFolder(store);
                            if (folder != null && folder.isOpen()) {
                                int replyCount = scanRepliesForTask(folder, trackRecords, account);
                                detectedReplies += replyCount;
                                logger.info("账户 {} 检测到 {} 个回复", account.getEmailAddress(), replyCount);
                                
                                folder.close(false);
                            }
                            store.close();
                        } else {
                            logger.warn("无法连接到账户 {} 进行回复检测", account.getEmailAddress());
                        }
                    } catch (Exception e) {
                        logger.warn("检测账户 {} 的回复邮件时发生异常: {}", account.getEmailAddress(), e.getMessage());
                    }
                }
            }
            
            logger.info("手动回复检测完成，任务ID: {}，共检测到 {} 个回复", taskId, detectedReplies);
            
        } catch (Exception e) {
            logger.error("手动回复检测失败，任务ID: {}", taskId, e);
        }
    }
    
    /**
     * 扫描指定任务的回复邮件
     */
    private int scanRepliesForTask(Folder folder, List<EmailTrackRecord> trackRecords, EmailAccount account) {
        int replyCount = 0;
        try {
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                return 0;
            }
            
            // 检查最近200封邮件
            int startIndex = Math.max(1, messageCount - 200);
            Message[] messages = folder.getMessages(startIndex, messageCount);
            
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    
                    // 检查是否是回复邮件
                    if (isReplyMessage(mimeMessage)) {
                        String originalMessageId = extractOriginalMessageIdFromReply(mimeMessage);
                        if (originalMessageId != null) {
                            // 检查是否是我们发送的邮件
                            for (EmailTrackRecord record : trackRecords) {
                                if (originalMessageId.equals(record.getMessageId()) && 
                                    record.getRepliedTime() == null) {
                                    
                                    // 更新回复时间
                                    record.setRepliedTime(new Date());
                                    record.setStatus("REPLIED");
                                    record.setUpdateTime(new Date());
                                    emailTrackRecordService.updateEmailTrackRecord(record);
                                    
                                    logger.info("检测到回复邮件: {} -> {} (任务ID: {})", 
                                               originalMessageId, mimeMessage.getMessageID(), record.getTaskId());
                                    replyCount++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描回复邮件失败: {}", account.getEmailAddress(), e);
        }
        
        return replyCount;
    }

    /**
     * 测试邮件跟踪记录插入功能
     * 用于调试和验证数据库连接
     */
    public boolean testEmailTrackRecordInsertion(Long taskId) {
        try {
            logger.info("开始测试邮件跟踪记录插入功能，任务ID: {}", taskId);
            
            // 检查数据库连接
            if (!isDatabaseConnectionAvailable()) {
                logger.error("数据库连接不可用，无法测试邮件跟踪记录插入");
                return false;
            }
            
            // 创建测试记录
            EmailTrackRecord testRecord = new EmailTrackRecord();
            testRecord.setTaskId(taskId);
            testRecord.setMessageId("TEST-" + System.currentTimeMillis());
            testRecord.setSubject("测试邮件主题");
            testRecord.setRecipient("test@example.com");
            testRecord.setSender("test@example.com");
            testRecord.setContent("测试邮件内容");
            testRecord.setStatus(EmailStatus.PENDING.name());
            testRecord.setSentTime(new Date());
            testRecord.setAccountId(1L);
            testRecord.setCreateBy("system");
            testRecord.setCreateTime(new Date());
            try {
                testRecord.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                logger.warn("测试方法中无法获取当前用户ID，使用默认值: {}", e.getMessage());
                testRecord.setUserId(1L); // 使用默认用户ID
            }
            testRecord.setFolderType("sent");
            testRecord.setIsRead(1);
            testRecord.setIsStarred(0);
            testRecord.setIsImportant(0);
            
            // 插入测试记录
            int insertResult = emailTrackRecordService.insertEmailTrackRecord(testRecord);
            logger.info("测试记录插入结果: {}", insertResult);
            
            if (insertResult > 0) {
                // 验证记录是否真的插入成功
                EmailTrackRecord verifyRecord = emailTrackRecordService.selectEmailTrackRecordByMessageId(testRecord.getMessageId());
                if (verifyRecord != null) {
                    logger.info("测试记录验证成功: MessageID={}, 记录ID={}", testRecord.getMessageId(), verifyRecord.getId());
                    
                    // 删除测试记录
                    emailTrackRecordService.deleteEmailTrackRecordById(verifyRecord.getId());
                    logger.info("测试记录已删除: 记录ID={}", verifyRecord.getId());
                    
                    return true;
                } else {
                    logger.error("测试记录验证失败: MessageID={}", testRecord.getMessageId());
                    return false;
                }
            } else {
                logger.error("测试记录插入失败: 插入结果={}", insertResult);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("测试邮件跟踪记录插入功能时发生异常", e);
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