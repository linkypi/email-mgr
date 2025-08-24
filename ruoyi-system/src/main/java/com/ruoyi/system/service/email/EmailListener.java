package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailServiceMonitor;
import com.ruoyi.system.mapper.email.EmailAccountMapper;
import com.ruoyi.system.service.email.EmailServiceMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    
    private final Map<Long, AccountConnectionInfo> activeConnections = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
    private volatile boolean isRunning = false;
    
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
}