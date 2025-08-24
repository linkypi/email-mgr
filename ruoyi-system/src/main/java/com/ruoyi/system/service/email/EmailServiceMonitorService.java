package com.ruoyi.system.service.email;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailServiceMonitor;
import com.ruoyi.system.domain.email.EmailServiceMonitorLog;
import com.ruoyi.system.mapper.email.EmailServiceMonitorLogMapper;
import com.ruoyi.system.mapper.email.EmailServiceMonitorMapper;
import com.ruoyi.system.utils.EmailServiceMonitorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.*;

/**
 * 邮件服务监控服务
 * 实现实时监控IMAP和SMTP服务状态，支持邮件发送、送达、已读、回复等事件的实时跟踪
 */
@Service
public class EmailServiceMonitorService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceMonitorService.class);
    
    @Autowired
    private EmailServiceMonitorMapper monitorMapper;
    
    @Autowired
    private EmailServiceMonitorLogMapper logMapper;
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private ImapService imapService;
    
    @Autowired
    private SmtpService smtpService;
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    // 全局监控状态
    private volatile boolean globalMonitorRunning = false;
    
    // 账户监控状态管理
    private final Map<Long, AccountMonitorState> accountMonitorStates = new ConcurrentHashMap<>();
    
    // 线程池
    private ScheduledExecutorService monitorScheduler;
    private ExecutorService eventProcessor;
    
    /**
     * 账户监控状态
     */
    private static class AccountMonitorState {
        volatile boolean imapMonitoring = false;
        volatile boolean smtpMonitoring = false;
        volatile String imapStatus = "STOPPED";
        volatile String smtpStatus = "STOPPED";
        volatile String imapErrorMessage = null;
        volatile String smtpErrorMessage = null;
        volatile Date imapErrorTime = null;
        volatile Date smtpErrorTime = null;
        volatile Date imapLastCheckTime = null;
        volatile Date smtpLastCheckTime = null;
        
        // IMAP实时监控相关
        volatile Store imapStore = null;
        volatile Folder imapFolder = null;
        volatile Thread imapIdleThread = null;
        volatile boolean imapIdleRunning = false;
        
        // SMTP实时监控相关
        volatile Session smtpSession = null;
        volatile Transport smtpTransport = null;
        volatile Thread smtpMonitorThread = null;
        volatile boolean smtpMonitorRunning = false;
    }
    
    @PostConstruct
    public void init() {
        logger.info("邮件服务监控服务初始化开始");
        try {
            // 初始化线程池
            initThreadPools();
            // 自动启动全局监控，确保启用的账号能够自动开始监控
            startGlobalMonitor();
            logger.info("邮件服务监控服务初始化完成，全局监控已自动启动");
        } catch (Exception e) {
            logger.error("邮件服务监控服务初始化失败", e);
            // 不要抛出异常，避免阻止Spring容器启动
        }
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("邮件服务监控服务销毁");
        stopGlobalMonitor();
        shutdownThreadPools();
    }
    
    /**
     * 初始化线程池
     */
    private void initThreadPools() {
        logger.info("开始初始化线程池...");
        
        // 强制重新创建监控调度线程池
        if (monitorScheduler != null) {
            logger.info("强制关闭旧的监控调度线程池");
            try {
                monitorScheduler.shutdownNow();
                // 等待一段时间确保线程池完全关闭
                Thread.sleep(2000); // 增加等待时间到2秒
                
                // 再次检查线程池状态
                if (!monitorScheduler.isTerminated()) {
                    logger.warn("线程池未完全终止，强制等待");
                    Thread.sleep(3000); // 再等待3秒
                }
            } catch (Exception e) {
                logger.warn("关闭旧的监控调度线程池时发生异常", e);
            }
            // 确保线程池引用被清空
            monitorScheduler = null;
        }
        
        // 创建新的监控调度线程池
        monitorScheduler = Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r, "Monitor-Scheduler-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
        logger.info("监控调度线程池创建成功");
        
        // 强制重新创建事件处理线程池
        if (eventProcessor != null) {
            logger.info("强制关闭旧的事件处理线程池");
            try {
                eventProcessor.shutdownNow();
                // 等待一段时间确保线程池完全关闭
                Thread.sleep(2000); // 增加等待时间到2秒
                
                // 再次检查线程池状态
                if (!eventProcessor.isTerminated()) {
                    logger.warn("事件处理线程池未完全终止，强制等待");
                    Thread.sleep(3000); // 再等待3秒
                }
            } catch (Exception e) {
                logger.warn("关闭旧的事件处理线程池时发生异常", e);
            }
            // 确保线程池引用被清空
            eventProcessor = null;
        }
        
        // 创建新的事件处理线程池
        eventProcessor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "Event-Processor-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
        logger.info("事件处理线程池创建成功");
        
        // 验证线程池状态
        if (monitorScheduler == null || monitorScheduler.isShutdown() || monitorScheduler.isTerminated()) {
            throw new RuntimeException("监控调度线程池创建失败或状态异常");
        }
        
        if (eventProcessor == null || eventProcessor.isShutdown() || eventProcessor.isTerminated()) {
            throw new RuntimeException("事件处理线程池创建失败或状态异常");
        }
        
        logger.info("线程池初始化完成，状态验证通过");
    }
    
    /**
     * 关闭线程池
     */
    private void shutdownThreadPools() {
        if (monitorScheduler != null && !monitorScheduler.isShutdown()) {
            try {
                monitorScheduler.shutdown();
                if (!monitorScheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    monitorScheduler.shutdownNow();
                }
                logger.info("监控调度线程池已关闭");
            } catch (InterruptedException e) {
                logger.warn("关闭监控调度线程池时被中断", e);
                monitorScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (eventProcessor != null && !eventProcessor.isShutdown()) {
            try {
                eventProcessor.shutdown();
                if (!eventProcessor.awaitTermination(10, TimeUnit.SECONDS)) {
                    eventProcessor.shutdownNow();
                }
                logger.info("事件处理线程池已关闭");
            } catch (InterruptedException e) {
                logger.warn("关闭事件处理线程池时被中断", e);
                eventProcessor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 启动全局监控
     */
    public void startGlobalMonitor() {
        logger.info("开始启动全局监控...");
        
        if (globalMonitorRunning) {
            logger.warn("全局监控已在运行中");
            return;
        }
        
        // 强制重新初始化线程池
        logger.info("强制重新初始化线程池...");
        initThreadPools();
        
        // 严格检查线程池状态
        if (monitorScheduler == null) {
            logger.error("监控调度线程池为null，无法启动全局监控");
            throw new RuntimeException("邮件监听服务启动失败: 监控调度线程池未初始化");
        }
        
        if (monitorScheduler.isShutdown()) {
            logger.error("监控调度线程池已关闭，无法启动全局监控");
            throw new RuntimeException("邮件监听服务启动失败: 监控调度线程池已关闭");
        }
        
        if (monitorScheduler.isTerminated()) {
            logger.error("监控调度线程池已终止，无法启动全局监控");
            throw new RuntimeException("邮件监听服务启动失败: 监控调度线程池已终止");
        }
        
        // 额外检查：确保线程池可以接受新任务
        try {
            // 尝试提交一个简单的测试任务来验证线程池状态
            monitorScheduler.submit(() -> {
                logger.debug("线程池状态测试任务执行成功");
            });
            logger.info("线程池状态验证通过，可以接受新任务");
        } catch (RejectedExecutionException e) {
            logger.error("线程池拒绝执行测试任务，状态异常");
            throw new RuntimeException("邮件监听服务启动失败: 线程池状态异常，无法接受新任务", e);
        }
        
        logger.info("线程池状态检查通过，开始启动监控任务");
        
        globalMonitorRunning = true;
        logger.info("启动全局邮件服务监控");
        
        try {
            // 立即启动所有现有账号的监控
            logger.info("启动所有现有账号的监控...");
            List<EmailAccount> existingAccounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            logger.info("找到 {} 个邮箱账号", existingAccounts.size());
            
            for (EmailAccount account : existingAccounts) {
                logger.info("检查账号: {} (ID: {}), 状态: {}, 邮件跟踪: {}", 
                    account.getEmailAddress(), 
                    account.getAccountId(),
                    account.getStatus(), 
                    account.getTrackingEnabled());
                
                // 只监控启用发送邮件的账号
                if ("0".equals(account.getStatus())) {
                    try {
                        logger.info("符合监控条件，启动账号 {} 的监控", account.getEmailAddress());
                        startAccountMonitor(account.getAccountId());
                        logger.info("已启动账号 {} 的监控（基于发送邮件启用状态）", account.getEmailAddress());
                    } catch (Exception e) {
                        logger.error("启动账号 {} 监控失败", account.getEmailAddress(), e);
                    }
                } else {
                    logger.info("跳过账号 {} 的监控启动：发送邮件状态={}（已禁用）", 
                        account.getEmailAddress(), 
                        account.getStatus());
                }
            }
            
            // 检查新增账户并启动监控
            logger.info("调度监控任务到线程池...");
            monitorScheduler.scheduleAtFixedRate(() -> {
                try {
                    checkNewAccounts();
                } catch (Exception e) {
                    logger.error("检查新增邮箱账号失败", e);
                }
            }, 1, 1, TimeUnit.MINUTES);
            
            logger.info("监控任务调度成功");
            recordOperation(null, "GLOBAL", "SUCCESS", "全局监控启动成功");
        } catch (RejectedExecutionException e) {
            logger.error("启动全局监控失败：线程池拒绝执行任务", e);
            logger.error("线程池状态: shutdown={}, terminated={}", 
                monitorScheduler.isShutdown(), monitorScheduler.isTerminated());
            globalMonitorRunning = false;
            throw new RuntimeException("邮件监听服务启动失败: 线程池拒绝执行任务，请检查线程池状态", e);
        } catch (Exception e) {
            logger.error("启动全局监控失败", e);
            globalMonitorRunning = false;
            throw new RuntimeException("邮件监听服务启动失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 停止全局监控
     */
    public void stopGlobalMonitor() {
        if (!globalMonitorRunning) {
            logger.warn("全局监控未在运行");
            return;
        }
        
        logger.info("开始停止全局邮件服务监控");
        globalMonitorRunning = false;
        
        // 获取所有账户ID的副本，避免并发修改异常
        Set<Long> accountIds = new HashSet<>(accountMonitorStates.keySet());
        
        // 停止所有账户监控
        for (Long accountId : accountIds) {
            try {
                stopAccountMonitor(accountId);
            } catch (Exception e) {
                logger.error("停止账户 {} 监控时发生异常", accountId, e);
            }
        }
        
        // 等待一段时间确保所有线程都已停止
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.warn("等待监控停止时被中断", e);
            Thread.currentThread().interrupt();
        }
        
        // 清除所有监控状态
        accountMonitorStates.clear();
        
        // 更新数据库中所有账号的监控状态为停止
        try {
            List<EmailServiceMonitor> allMonitors = monitorMapper.selectEmailServiceMonitorList(new EmailServiceMonitor());
            for (EmailServiceMonitor monitor : allMonitors) {
                monitor.setMonitorStatus("0"); // 停止
                monitor.setImapStatus("0"); // 停止
                monitor.setSmtpStatus("0"); // 停止
                monitor.setUpdateBy("system");
                monitor.setUpdateTime(new Date());
                monitorMapper.updateEmailServiceMonitor(monitor);
            }
            logger.info("已更新数据库中所有账号的监控状态为停止");
        } catch (Exception e) {
            logger.error("更新数据库监控状态失败", e);
        }
        
        logger.info("全局邮件服务监控已停止");
        recordOperation(null, "GLOBAL", "SUCCESS", "全局监控停止成功");
    }
    
    /**
     * 启动账户监控
     */
    public void startAccountMonitor(Long accountId) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        if (state != null && (state.imapMonitoring || state.smtpMonitoring)) {
            logger.warn("账户 {} 监控已在运行中", accountId);
            return;
        }
        
        EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
        if (account == null) {
            logger.error("账户 {} 不存在", accountId);
            return;
        }
        
        // 检查账号是否启用
        if (!"0".equals(account.getStatus())) {
            logger.warn("账户 {} 未启用，跳过监控启动", account.getEmailAddress());
            return;
        }
        
        AccountMonitorState newState = new AccountMonitorState();
        accountMonitorStates.put(accountId, newState);
        
        // 启动IMAP监控
        startImapMonitoring(accountId, account, newState);
        
        // 启动SMTP监控
        startSmtpMonitoring(accountId, account, newState);
        
        // 更新数据库状态
        updateMonitorStatus(accountId, "RUNNING");
        
        recordOperation(accountId, "ACCOUNT", "SUCCESS", 
            String.format("账户 %s 监控启动成功", account.getEmailAddress()));
    }
    
    /**
     * 停止账户监控
     */
    public void stopAccountMonitor(Long accountId) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        if (state == null) {
            logger.warn("账户 {} 监控未在运行", accountId);
            return;
        }
        
        // 停止IMAP监控
        stopImapMonitoring(accountId, state);
        
        // 停止SMTP监控
        stopSmtpMonitoring(accountId, state);
        
        // 移除状态
        accountMonitorStates.remove(accountId);
        
        // 更新数据库状态
        updateMonitorStatus(accountId, "STOPPED");
        
        EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
        String emailAddress = account != null ? account.getEmailAddress() : accountId.toString();
        recordOperation(accountId, "ACCOUNT", "SUCCESS", 
            String.format("账户 %s 监控停止成功", emailAddress));
    }
    
    /**
     * 重启账户监控
     */
    public void restartAccountMonitor(Long accountId) {
        logger.info("重启账户 {} 监控", accountId);
        stopAccountMonitor(accountId);
        
        // 等待一段时间后重新启动
        monitorScheduler.schedule(() -> {
            try {
                startAccountMonitor(accountId);
            } catch (Exception e) {
                logger.error("重启账户 {} 监控失败", accountId, e);
                recordOperation(accountId, "ACCOUNT", "FAILED", e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);
    }
    
    /**
     * 启动IMAP实时监控
     */
    private void startImapMonitoring(Long accountId, EmailAccount account, AccountMonitorState state) {
        if (state.imapMonitoring) {
            return;
        }
        
        state.imapMonitoring = true;
        state.imapIdleRunning = true;
        state.imapStatus = "2"; // 连接中状态
        
        // 更新状态为连接中
        updateImapStatus(accountId, "2", null);
        
        // 在独立线程中启动IMAP监控
        state.imapIdleThread = new Thread(() -> {
            try {
                performImapMonitoring(accountId, account, state);
            } catch (Exception e) {
                logger.error("账户 {} IMAP监控异常", accountId, e);
                updateImapStatus(accountId, "10", e.getMessage()); // 使用数字状态码
            }
        }, "IMAP-Monitor-" + accountId);
        
        state.imapIdleThread.setDaemon(true);
        state.imapIdleThread.start();
    }
    
    /**
     * 执行IMAP实时监控
     */
    private void performImapMonitoring(Long accountId, EmailAccount account, AccountMonitorState state) {
        // 首先测试连接
        if (!testImapConnectionBeforeMonitoring(account)) {
            logger.error("IMAP连接测试失败，跳过监控: {}", account.getEmailAddress());
            updateImapStatus(accountId, "4", "连接测试失败，请检查网络或服务器配置");
            return;
        }
        int retryCount = 0;
        final int maxRetries = 3;
        final long retryDelay = 30000; // 30秒重试间隔
        
        while (state.imapIdleRunning && !Thread.currentThread().isInterrupted()) {
            try {
                // 建立IMAP连接
                Properties props = imapService.createImapProperties(account);
                Session session = Session.getInstance(props);
                session.setDebug(false);
                
                logger.info("尝试建立IMAP连接: {}:{}", account.getImapHost(), account.getImapPort());
                
                Store store = session.getStore("imap");
                store.connect(account.getImapHost(), account.getImapPort(), 
                    account.getImapUsername(), account.getImapPassword());
                
                state.imapStore = store;
                state.imapStatus = "CONNECTED";
                state.imapLastCheckTime = new Date();
                state.imapErrorMessage = null;
                state.imapErrorTime = null;
                retryCount = 0; // 连接成功，重置重试计数
                
                updateImapStatus(accountId, "3", null); // 使用数字状态码
                
                // 打开收件箱
                Folder folder = store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                state.imapFolder = folder;
                
                // 添加消息监听器
                folder.addMessageCountListener(new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent evt) {
                        Message[] messages = evt.getMessages();
                        eventProcessor.submit(() -> {
                            try {
                                processNewMessages(accountId, account, messages);
                            } catch (Exception e) {
                                logger.error("处理新邮件失败", e);
                            }
                        });
                    }
                });
                
                logger.info("IMAP连接成功，开始监听: {}", account.getEmailAddress());
                
                // 启动IDLE监听
                while (state.imapIdleRunning && !Thread.currentThread().isInterrupted()) {
                    try {
                        // 检查连接是否仍然有效
                        if (!store.isConnected()) {
                            logger.warn("IMAP连接已断开，尝试重新连接: {}", account.getEmailAddress());
                            break; // 跳出内层循环，重新建立连接
                        }
                        
                        // 使用IDLE命令进行实时监听
                        if (folder.hasNewMessages()) {
                            // 处理新邮件
                            Message[] newMessages = folder.getMessages();
                            processNewMessages(accountId, account, newMessages);
                        }
                        
                        // 等待新消息通知，使用更短的检查间隔
                        Thread.sleep(3000); // 3秒检查一次
                        
                        // 更新最后检查时间
                        state.imapLastCheckTime = new Date();
                        
                    } catch (MessagingException e) {
                        if (state.imapIdleRunning) {
                            logger.warn("IMAP IDLE异常，尝试重新连接: {}", e.getMessage());
                            // 使用工具类分析异常状态
                            String errorStatus = EmailServiceMonitorUtils.determineStatusFromException(e);
                            String errorMessage = EmailServiceMonitorUtils.getErrorMessage(e);
                            updateImapStatus(accountId, errorStatus, errorMessage);
                            break; // 跳出内层循环，重新建立连接
                        }
                    } catch (InterruptedException e) {
                        logger.info("IMAP监控线程被中断，停止监听: {}", account.getEmailAddress());
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error("IMAP监听过程中发生未知异常: {}", e.getMessage(), e);
                        break; // 跳出内层循环，重新建立连接
                    }
                }
                
            } catch (Exception e) {
                retryCount++;
                logger.error("IMAP监控连接失败 (重试 {}/{}): {}", retryCount, maxRetries, e.getMessage());
                
                // 使用工具类分析异常状态
                String errorStatus = EmailServiceMonitorUtils.determineStatusFromException(e);
                String errorMessage = EmailServiceMonitorUtils.getErrorMessage(e);
                updateImapStatus(accountId, errorStatus, errorMessage);
                
                // 根据重试次数决定是否继续重试
                if (state.imapIdleRunning && retryCount < maxRetries) {
                    try {
                        long delay = retryDelay * retryCount; // 递增重试延迟
                        logger.info("IMAP连接失败，{}秒后进行第{}次重试: {}", delay/1000, retryCount + 1, account.getEmailAddress());
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        logger.info("IMAP监控线程被中断，停止重试: {}", account.getEmailAddress());
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else if (retryCount >= maxRetries) {
                    logger.error("IMAP连接重试次数已达上限，停止重试: {}", account.getEmailAddress());
                    // 更新状态为连接失败
                    updateImapStatus(accountId, "4", "连接重试次数已达上限，请检查网络或服务器状态");
                    break;
                }
            } finally {
                // 清理资源
                cleanupImapResources(state);
            }
        }
    }
    
    /**
     * 处理新收到的邮件
     */
    private void processNewMessages(Long accountId, EmailAccount account, Message[] messages) {
        for (Message message : messages) {
            try {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    
                    // 解析邮件信息
                    String subject = mimeMessage.getSubject();
                    String from = extractEmailAddress(mimeMessage.getFrom());
                    String to = extractEmailAddress(mimeMessage.getRecipients(Message.RecipientType.TO));
                    String messageId = mimeMessage.getMessageID();
                    
                    // 检查是否是回复邮件
                    if (isReplyMessage(mimeMessage)) {
                        // 更新原始邮件的回复状态
                        updateEmailReplyStatus(messageId, accountId);
                    }
                    
                    // 检查是否是送达状态通知
                    if (isDeliveryStatusNotification(mimeMessage)) {
                        processDeliveryStatusNotification(mimeMessage, accountId);
                    }
                    
                    logger.info("处理新邮件: {} - {}", subject, messageId);
                    
                }
            } catch (Exception e) {
                logger.error("处理邮件失败", e);
            }
        }
    }
    
    /**
     * 启动SMTP实时监控
     */
    private void startSmtpMonitoring(Long accountId, EmailAccount account, AccountMonitorState state) {
        if (state.smtpMonitoring) {
            return;
        }
        
        state.smtpMonitoring = true;
        state.smtpMonitorRunning = true;
        state.smtpStatus = "2"; // 连接中状态
        
        // 更新状态为连接中
        updateSmtpStatus(accountId, "2", null);
        
        // 在独立线程中启动SMTP监控
        state.smtpMonitorThread = new Thread(() -> {
            try {
                performSmtpMonitoring(accountId, account, state);
            } catch (Exception e) {
                logger.error("账户 {} SMTP监控异常", accountId, e);
                updateSmtpStatus(accountId, "10", e.getMessage()); // 使用数字状态码
            }
        }, "SMTP-Monitor-" + accountId);
        
        state.smtpMonitorThread.setDaemon(true);
        state.smtpMonitorThread.start();
    }
    
    /**
     * 执行SMTP实时监控
     */
    private void performSmtpMonitoring(Long accountId, EmailAccount account, AccountMonitorState state) {
        while (state.smtpMonitorRunning && !Thread.currentThread().isInterrupted()) {
            try {
                // 建立SMTP连接
                Properties props = smtpService.createSmtpProperties(account);
                Session session = Session.getInstance(props);
                session.setDebug(false);
                
                Transport transport = session.getTransport("smtp");
                transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                    account.getEmailAddress(), account.getPassword());
                
                state.smtpSession = session;
                state.smtpTransport = transport;
                state.smtpStatus = "CONNECTED";
                state.smtpLastCheckTime = new Date();
                state.smtpErrorMessage = null;
                state.smtpErrorTime = null;
                
                updateSmtpStatus(accountId, "3", null); // 使用数字状态码
                
                logger.info("SMTP连接成功，开始监控: {}", account.getEmailAddress());
                
                // SMTP连接保持活跃状态，等待发送任务
                while (state.smtpMonitorRunning && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(30000); // 每30秒检查一次连接状态
                    
                    // 检查连接是否仍然有效
                    if (!transport.isConnected()) {
                        logger.warn("SMTP连接断开，尝试重新连接");
                        try {
                            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                                account.getEmailAddress(), account.getPassword());
                            logger.info("SMTP重新连接成功: {}", account.getEmailAddress());
                        } catch (Exception e) {
                            logger.error("SMTP重新连接失败", e);
                            // 使用工具类分析异常状态
                            String errorStatus = EmailServiceMonitorUtils.determineStatusFromException(e);
                            String errorMessage = EmailServiceMonitorUtils.getErrorMessage(e);
                            updateSmtpStatus(accountId, errorStatus, errorMessage);
                            break; // 跳出内层循环，重新建立连接
                        }
                    }
                    
                    state.smtpLastCheckTime = new Date();
                }
                
            } catch (Exception e) {
                logger.error("SMTP监控连接失败", e);
                // 使用工具类分析异常状态
                String errorStatus = EmailServiceMonitorUtils.determineStatusFromException(e);
                String errorMessage = EmailServiceMonitorUtils.getErrorMessage(e);
                updateSmtpStatus(accountId, errorStatus, errorMessage);
                
                // 等待一段时间后重试
                if (state.smtpMonitorRunning) {
                    try {
                        logger.info("SMTP连接失败，10秒后重试: {}", account.getEmailAddress());
                        Thread.sleep(10000); // 10秒后重试
                    } catch (InterruptedException ie) {
                        logger.info("SMTP监控线程被中断，停止重试: {}", account.getEmailAddress());
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } finally {
                // 清理资源
                cleanupSmtpResources(state);
            }
        }
    }
    
    /**
     * 停止IMAP监控
     */
    private void stopImapMonitoring(Long accountId, AccountMonitorState state) {
        if (!state.imapMonitoring) {
            return;
        }
        
        logger.info("停止账户 {} 的IMAP监控", accountId);
        
        // 先设置停止标志
        state.imapMonitoring = false;
        state.imapIdleRunning = false;
        
        // 中断线程
        if (state.imapIdleThread != null && state.imapIdleThread.isAlive()) {
            try {
                state.imapIdleThread.interrupt();
                // 等待线程结束，最多等待5秒
                state.imapIdleThread.join(5000);
            } catch (InterruptedException e) {
                logger.warn("等待IMAP监控线程结束时被中断", e);
                Thread.currentThread().interrupt();
            }
        }
        
        // 清理资源
        cleanupImapResources(state);
        
        // 更新状态
        updateImapStatus(accountId, "0", null); // 使用数字状态码
        
        logger.info("账户 {} 的IMAP监控已停止", accountId);
    }
    
    /**
     * 停止SMTP监控
     */
    private void stopSmtpMonitoring(Long accountId, AccountMonitorState state) {
        if (!state.smtpMonitoring) {
            return;
        }
        
        logger.info("停止账户 {} 的SMTP监控", accountId);
        
        // 先设置停止标志
        state.smtpMonitoring = false;
        state.smtpMonitorRunning = false;
        
        // 中断线程
        if (state.smtpMonitorThread != null && state.smtpMonitorThread.isAlive()) {
            try {
                state.smtpMonitorThread.interrupt();
                // 等待线程结束，最多等待5秒
                state.smtpMonitorThread.join(5000);
            } catch (InterruptedException e) {
                logger.warn("等待SMTP监控线程结束时被中断", e);
                Thread.currentThread().interrupt();
            }
        }
        
        // 清理资源
        cleanupSmtpResources(state);
        
        // 更新状态
        updateSmtpStatus(accountId, "0", null); // 使用数字状态码
        
        logger.info("账户 {} 的SMTP监控已停止", accountId);
    }
    
    /**
     * 监控前测试IMAP连接
     */
    private boolean testImapConnectionBeforeMonitoring(EmailAccount account) {
        try {
            logger.info("开始IMAP连接测试: {}", account.getEmailAddress());
            
            Properties props = imapService.createImapProperties(account);
            Session session = Session.getInstance(props);
            session.setDebug(false);
            
            Store store = session.getStore("imap");
            store.connect(account.getImapHost(), account.getImapPort(), 
                account.getImapUsername(), account.getImapPassword());
            
            // 测试获取文件夹
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            int messageCount = folder.getMessageCount();
            logger.info("IMAP连接测试成功，收件箱共有 {} 封邮件: {}", messageCount, account.getEmailAddress());
            
            folder.close(false);
            store.close();
            
            return true;
            
        } catch (Exception e) {
            logger.error("IMAP连接测试失败: {} - {}", account.getEmailAddress(), e.getMessage());
            return false;
        }
    }

    /**
     * 清理IMAP资源
     */
    private void cleanupImapResources(AccountMonitorState state) {
        try {
            if (state.imapFolder != null) {
                try {
                    if (state.imapFolder.isOpen()) {
                        state.imapFolder.close(false);
                    }
                } catch (IllegalStateException e) {
                    // 文件夹已经关闭，忽略此异常
                    logger.debug("IMAP文件夹已经关闭: {}", e.getMessage());
                } catch (Exception e) {
                    logger.warn("关闭IMAP文件夹失败", e);
                }
            }
        } catch (Exception e) {
            logger.warn("清理IMAP文件夹时发生异常", e);
        }
        
        try {
            if (state.imapStore != null) {
                try {
                    if (state.imapStore.isConnected()) {
                        state.imapStore.close();
                    }
                } catch (Exception e) {
                    logger.warn("关闭IMAP连接失败", e);
                }
            }
        } catch (Exception e) {
            logger.warn("清理IMAP连接时发生异常", e);
        }
        
        state.imapFolder = null;
        state.imapStore = null;
    }
    
    /**
     * 清理SMTP资源
     */
    private void cleanupSmtpResources(AccountMonitorState state) {
        try {
            if (state.smtpTransport != null) {
                try {
                    if (state.smtpTransport.isConnected()) {
                        state.smtpTransport.close();
                    }
                } catch (Exception e) {
                    logger.warn("关闭SMTP连接失败", e);
                }
            }
        } catch (Exception e) {
            logger.warn("清理SMTP连接时发生异常", e);
        }
        
        state.smtpTransport = null;
        state.smtpSession = null;
    }
    
    /**
     * 检查新增账户
     */
    private void checkNewAccounts() {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            
            for (EmailAccount account : accounts) {
                // 检查启用发送邮件且未在监控中的账号
                if ("0".equals(account.getStatus()) &&
                    !accountMonitorStates.containsKey(account.getAccountId())) {
                    logger.info("发现新的启用发送邮件账号: {}", account.getEmailAddress());
                    // 立即启动监控
                    startAccountMonitor(account.getAccountId());
                }
            }
        } catch (Exception e) {
            logger.error("检查新增账户失败", e);
        }
    }
    
    /**
     * 测试IMAP服务
     */
    public Map<String, Object> testImapService(Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
            if (account == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "账户不存在");
                return result;
            }
            
            ImapService.ImapTestResult result = imapService.testImapConnection(account);
            
            if (result.isSuccess()) {
                updateImapStatus(accountId, "TEST_SUCCESS", null);
                recordOperation(accountId, "IMAP", "SUCCESS", "IMAP连接测试成功");
            } else {
                updateImapStatus(accountId, "TEST_FAILED", result.getErrorMessage());
                recordOperation(accountId, "IMAP", "FAILED", result.getErrorMessage());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getErrorMessage());
            response.put("connectionTime", result.getConnectionTime());
            return response;
            
        } catch (Exception e) {
            logger.error("测试IMAP服务失败", e);
            updateImapStatus(accountId, "TEST_FAILED", e.getMessage());
            recordOperation(accountId, "IMAP", "FAILED", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }
    
    /**
     * 测试SMTP服务
     */
    public Map<String, Object> testSmtpService(Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
            if (account == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "账户不存在");
                return result;
            }
            
            SmtpService.SmtpTestResult result = smtpService.testSmtpConnection(account);
            
            if (result.isSuccess()) {
                updateSmtpStatus(accountId, "TEST_SUCCESS", null);
                recordOperation(accountId, "SMTP", "SUCCESS", "SMTP连接测试成功");
            } else {
                updateSmtpStatus(accountId, "TEST_FAILED", result.getErrorMessage());
                recordOperation(accountId, "SMTP", "FAILED", result.getErrorMessage());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getErrorMessage());
            response.put("connectionTime", result.getConnectionTime());
            return response;
            
        } catch (Exception e) {
            logger.error("测试SMTP服务失败", e);
            updateSmtpStatus(accountId, "TEST_FAILED", e.getMessage());
            recordOperation(accountId, "SMTP", "FAILED", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }
    
    /**
     * 更新IMAP状态
     */
    private void updateImapStatus(Long accountId, String status, String errorMessage) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        if (state != null) {
            state.imapStatus = status;
            state.imapErrorMessage = errorMessage;
            state.imapErrorTime = errorMessage != null ? new Date() : null;
            state.imapLastCheckTime = new Date();
        }
        
        // 更新数据库
        EmailServiceMonitor monitor = getOrCreateMonitor(accountId);
        monitor.setImapStatus(status);
        monitor.setImapErrorMessage(errorMessage);
        monitor.setImapErrorTime(errorMessage != null ? new Date() : null);
        monitor.setImapLastCheckTime(new Date());
        monitor.setUpdateBy("system");
        monitor.setUpdateTime(new Date());
        
        if (monitor.getId() == null) {
            monitorMapper.insertEmailServiceMonitor(monitor);
        } else {
            monitorMapper.updateEmailServiceMonitor(monitor);
        }
    }
    
    /**
     * 更新SMTP状态
     */
    private void updateSmtpStatus(Long accountId, String status, String errorMessage) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        if (state != null) {
            state.smtpStatus = status;
            state.smtpErrorMessage = errorMessage;
            state.smtpErrorTime = errorMessage != null ? new Date() : null;
            state.smtpLastCheckTime = new Date();
        }
        
        // 更新数据库
        EmailServiceMonitor monitor = getOrCreateMonitor(accountId);
        monitor.setSmtpStatus(status);
        monitor.setSmtpErrorMessage(errorMessage);
        monitor.setSmtpErrorTime(errorMessage != null ? new Date() : null);
        monitor.setSmtpLastCheckTime(new Date());
        monitor.setUpdateBy("system");
        monitor.setUpdateTime(new Date());
        
        if (monitor.getId() == null) {
            monitorMapper.insertEmailServiceMonitor(monitor);
        } else {
            monitorMapper.updateEmailServiceMonitor(monitor);
        }
    }
    
    /**
     * 更新监控状态
     */
    private void updateMonitorStatus(Long accountId, String status) {
        EmailServiceMonitor monitor = getOrCreateMonitor(accountId);
        // 根据服务类型设置相应的状态
        if ("IMAP".equals(status)) {
            monitor.setImapStatus("running");
            monitor.setImapLastCheckTime(new Date());
        } else if ("SMTP".equals(status)) {
            monitor.setSmtpStatus("running");
            monitor.setSmtpLastCheckTime(new Date());
        }
        monitor.setUpdateBy("system");
        monitor.setUpdateTime(new Date());
        
        if (monitor.getId() == null) {
            monitorMapper.insertEmailServiceMonitor(monitor);
        } else {
            monitorMapper.updateEmailServiceMonitor(monitor);
        }
    }
    
    /**
     * 更新服务状态 - 供EmailListener调用
     */
    public void updateServiceStatus(Long accountId, String serviceType, String status, String errorMessage) {
        if ("smtp".equals(serviceType)) {
            updateSmtpStatus(accountId, status, errorMessage);
        } else if ("imap".equals(serviceType)) {
            updateImapStatus(accountId, status, errorMessage);
        }
        
        // 记录操作日志
        recordOperation(accountId, serviceType.toUpperCase(), status, 
                       errorMessage != null ? errorMessage : "服务状态更新: " + status);
        
        // 如果是错误状态，发送管理员通知
        if ("error".equals(status) && errorMessage != null) {
            sendAdminNotification(accountId, serviceType.toUpperCase(), errorMessage);
        }
    }
    
    /**
     * 获取或创建监控记录
     */
    private EmailServiceMonitor getOrCreateMonitor(Long accountId) {
        EmailServiceMonitor monitor = monitorMapper.selectEmailServiceMonitorByAccountId(accountId);
        if (monitor == null) {
            EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
            monitor = new EmailServiceMonitor();
            monitor.setAccountId(accountId);
            monitor.setEmailAddress(account != null ? account.getEmailAddress() : "");
            monitor.setImapStatus("stopped");
            monitor.setSmtpStatus("stopped");
            monitor.setMonitorEnabled(1);
            monitor.setCreateBy("system");
            monitor.setCreateTime(new Date());
        }
        return monitor;
    }
    
    /**
     * 记录操作日志
     */
    private void recordOperation(Long accountId, String serviceType, String status, String message) {
        try {
            EmailServiceMonitorLog log = new EmailServiceMonitorLog();
            log.setAccountId(accountId);
            log.setServiceType(serviceType);
            log.setStatus(status);
            log.setMessage(message);
            log.setCheckTime(new Date());
            log.setCreateBy("system");
            log.setCreateTime(new Date());
            
            logMapper.insertEmailServiceMonitorLog(log);
        } catch (Exception e) {
            logger.error("记录操作日志失败", e);
        }
    }
    
    /**
     * 发送错误通知
     */
    private void sendErrorNotification(Long accountId, String serviceType, String errorMessage) {
        logger.warn("服务异常通知 - 账户: {}, 服务: {}, 错误: {}", accountId, serviceType, errorMessage);
        sendAdminNotification(accountId, serviceType, errorMessage);
    }
    
    /**
     * 发送管理员通知
     */
    private void sendAdminNotification(Long accountId, String serviceType, String errorMessage) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
            String emailAddress = account != null ? account.getEmailAddress() : "账户ID: " + accountId;
            
            String subject = String.format("邮件服务监控告警 - %s 服务异常", serviceType);
            String content = String.format(
                "尊敬的管理员：\n\n" +
                "邮箱账号 %s 的 %s 服务出现异常，请及时处理。\n\n" +
                "异常详情：\n" +
                "- 邮箱账号：%s\n" +
                "- 服务类型：%s\n" +
                "- 异常信息：%s\n" +
                "- 异常时间：%s\n\n" +
                "请登录邮件管理系统查看详细信息并处理相关问题。\n\n" +
                "邮件管理系统\n%s",
                emailAddress, serviceType, 
                emailAddress, serviceType, errorMessage,
                DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date()),
                DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date())
            );
            
            // 异步发送通知邮件
            eventProcessor.submit(() -> {
                try {
                    sendNotificationEmail(subject, content, accountId);
                } catch (Exception e) {
                    logger.error("发送管理员通知邮件失败", e);
                }
            });
            
            // 记录通知日志
            recordOperation(accountId, "NOTIFICATION", "SENT", 
                           String.format("已发送 %s 服务异常通知", serviceType));
            
        } catch (Exception e) {
            logger.error("处理管理员通知失败", e);
        }
    }
    
    /**
     * 发送通知邮件
     */
    private void sendNotificationEmail(String subject, String content, Long accountId) {
        try {
            // 获取管理员邮箱配置 - 这里需要配置管理员邮箱地址
            String adminEmail = getAdminEmail();
            if (adminEmail == null || adminEmail.isEmpty()) {
                logger.warn("未配置管理员邮箱地址，无法发送通知邮件");
                return;
            }
            
            // 获取可用的发送邮箱账号
            EmailAccount senderAccount = getAvailableSenderAccount(accountId);
            if (senderAccount == null) {
                logger.warn("没有可用的发送邮箱账号，无法发送通知邮件");
                return;
            }
            
            // 发送邮件
            SmtpService.SmtpSendResult result = smtpService.sendEmail(
                senderAccount, adminEmail, subject, content, null);
                
            if (result.isSuccess()) {
                logger.info("管理员通知邮件发送成功");
                recordOperation(accountId, "NOTIFICATION", "SUCCESS", "管理员通知邮件发送成功");
            } else {
                logger.error("管理员通知邮件发送失败: {}", result.getErrorMessage());
                recordOperation(accountId, "NOTIFICATION", "FAILED", result.getErrorMessage());
            }
            
        } catch (Exception e) {
            logger.error("发送通知邮件异常", e);
            recordOperation(accountId, "NOTIFICATION", "FAILED", e.getMessage());
        }
    }
    
    /**
     * 获取管理员邮箱地址
     */
    private String getAdminEmail() {
        // TODO: 从系统配置中获取管理员邮箱地址
        // 可以从数据库配置表或配置文件中读取
        return "admin@example.com"; // 临时硬编码，需要改为可配置
    }
    
    /**
     * 获取可用的发送邮箱账号
     */
    private EmailAccount getAvailableSenderAccount(Long excludeAccountId) {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            
            // 过滤出可用的账号（排除异常账号）
            for (EmailAccount account : accounts) {
                if (!account.getAccountId().equals(excludeAccountId) && "1".equals(account.getStatus())) {
                    // 测试账号是否可用
                    SmtpService.SmtpTestResult testResult = smtpService.testSmtpConnection(account);
                    if (testResult.isSuccess()) {
                        return account;
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            logger.error("获取可用发送邮箱账号失败", e);
            return null;
        }
    }
    
    // 辅助方法
    private String extractEmailAddress(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "";
        }
        return addresses[0].toString();
    }
    
    private boolean isReplyMessage(Message message) throws MessagingException {
        String subject = message.getSubject();
        return subject != null && (subject.startsWith("Re:") || subject.startsWith("回复:"));
    }
    
    private boolean isDeliveryStatusNotification(Message message) throws MessagingException {
        String subject = message.getSubject();
        return subject != null && subject.contains("Delivery Status Notification");
    }
    
    private void updateEmailReplyStatus(String messageId, Long accountId) {
        try {
            emailTrackRecordService.recordEmailReplied(messageId);
        } catch (Exception e) {
            logger.error("更新邮件回复状态失败", e);
        }
    }
    
    private void processDeliveryStatusNotification(Message message, Long accountId) {
        try {
            // 解析DSN并更新邮件状态
            // TODO: 实现DSN解析逻辑
            logger.info("处理送达状态通知");
        } catch (Exception e) {
            logger.error("处理送达状态通知失败", e);
        }
    }
    
    // 查询方法
    public List<EmailServiceMonitor> getMonitorList(EmailServiceMonitor monitor) {
        List<EmailServiceMonitor> monitors = monitorMapper.selectEmailServiceMonitorList(monitor);
        
        // 过滤掉禁用的账号，只显示启用的账号
        monitors.removeIf(monitorItem -> {
            try {
                EmailAccount account = emailAccountService.selectEmailAccountByAccountId(monitorItem.getAccountId());
                return account == null || !"0".equals(account.getStatus());
            } catch (Exception e) {
                logger.error("检查账号状态时发生错误: {}", monitorItem.getAccountId(), e);
                return true; // 出错时也过滤掉
            }
        });
        
        // 更新实时监控状态
        for (EmailServiceMonitor monitorItem : monitors) {
            Long accountId = monitorItem.getAccountId();
            
            // 如果全局监控未运行，所有账号状态都应该是停止
            if (!globalMonitorRunning) {
                monitorItem.setMonitorStatus("0"); // 停止
                monitorItem.setImapStatus("0"); // 停止
                monitorItem.setSmtpStatus("0"); // 停止
                continue;
            }
            
            // 检查监控状态
            AccountMonitorState state = accountMonitorStates.get(accountId);
            if (state != null) {
                // 更新监控状态
                if (state.imapMonitoring || state.smtpMonitoring) {
                    monitorItem.setMonitorStatus("1"); // 运行中
                    monitorItem.setLastMonitorTime(new Date());
                } else {
                    monitorItem.setMonitorStatus("0"); // 停止
                }
                
                // 更新IMAP状态（如果监控正在运行且状态不是错误状态）
                if (state.imapMonitoring) {
                    if (!isErrorStatus(state.imapStatus)) {
                        monitorItem.setImapStatus(state.imapStatus);
                    }
                    if (state.imapLastCheckTime != null) {
                        monitorItem.setImapLastCheckTime(state.imapLastCheckTime);
                    }
                } else {
                    // 如果IMAP监控未运行，设置为停止
                    monitorItem.setImapStatus("0");
                }
                
                // 更新SMTP状态（如果监控正在运行且状态不是错误状态）
                if (state.smtpMonitoring) {
                    if (!isErrorStatus(state.smtpStatus)) {
                        monitorItem.setSmtpStatus(state.smtpStatus);
                    }
                    if (state.smtpLastCheckTime != null) {
                        monitorItem.setSmtpLastCheckTime(state.smtpLastCheckTime);
                    }
                } else {
                    // 如果SMTP监控未运行，设置为停止
                    monitorItem.setSmtpStatus("0");
                }
            } else {
                // 如果没有监控状态，设置为停止
                monitorItem.setMonitorStatus("0");
                monitorItem.setImapStatus("0"); // 停止
                monitorItem.setSmtpStatus("0"); // 停止
            }
        }
        
        return monitors;
    }
    
    /**
     * 判断是否为错误状态
     */
    private boolean isErrorStatus(String status) {
        if (status == null) return false;
        // 错误状态码：4-10
        return status.matches("[4-9]|10");
    }
    
    /**
     * 检查账号是否正在监控
     */
    public boolean isAccountMonitoring(Long accountId) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        return state != null && (state.imapMonitoring || state.smtpMonitoring);
    }
    
    public List<EmailServiceMonitorLog> getMonitorLogList(EmailServiceMonitorLog log) {
        return logMapper.selectEmailServiceMonitorLogList(log);
    }
    
    public List<EmailServiceMonitorLog> getAccountMonitorLogs(Long accountId) {
        return logMapper.selectEmailServiceMonitorLogByAccountId(accountId);
    }
    
    public Map<String, Object> getMonitorStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取实时监控列表（包含最新的状态）
        List<EmailServiceMonitor> monitors = getMonitorList(new EmailServiceMonitor());
        long totalAccounts = monitors.size();
        
        // 统计运行中的账号（IMAP或SMTP状态为运行中）
        long runningAccounts = monitors.stream()
            .filter(m -> "1".equals(m.getImapStatus()) || "2".equals(m.getImapStatus()) || "3".equals(m.getImapStatus()) ||
                        "1".equals(m.getSmtpStatus()) || "2".equals(m.getSmtpStatus()) || "3".equals(m.getSmtpStatus()))
            .count();
        
        // 统计错误账号（状态码4-10表示各种错误）
        long errorAccounts = monitors.stream()
            .filter(m -> {
                String imapStatus = m.getImapStatus();
                String smtpStatus = m.getSmtpStatus();
                return (imapStatus != null && imapStatus.matches("[4-9]|10")) ||
                       (smtpStatus != null && smtpStatus.matches("[4-9]|10"));
            })
            .count();
        
        // 统计连接中的账号
        long connectingAccounts = monitors.stream()
            .filter(m -> "2".equals(m.getImapStatus()) || "2".equals(m.getSmtpStatus()))
            .count();
        
        // 统计已连接的账号
        long connectedAccounts = monitors.stream()
            .filter(m -> "3".equals(m.getImapStatus()) || "3".equals(m.getSmtpStatus()))
            .count();
        
        stats.put("totalAccounts", totalAccounts);
        stats.put("runningAccounts", runningAccounts);
        stats.put("errorAccounts", errorAccounts);
        stats.put("connectingAccounts", connectingAccounts);
        stats.put("connectedAccounts", connectedAccounts);
        stats.put("globalMonitorRunning", globalMonitorRunning);
        
        return stats;
    }
    
    public boolean isGlobalMonitorRunning() {
        return globalMonitorRunning;
    }
    
    /**
     * 获取线程池状态信息（用于调试）
     */
    public Map<String, Object> getThreadPoolStatus() {
        Map<String, Object> status = new HashMap<>();
        
        if (monitorScheduler != null) {
            status.put("monitorSchedulerNull", false);
            status.put("monitorSchedulerShutdown", monitorScheduler.isShutdown());
            status.put("monitorSchedulerTerminated", monitorScheduler.isTerminated());
        } else {
            status.put("monitorSchedulerNull", true);
            status.put("monitorSchedulerShutdown", false);
            status.put("monitorSchedulerTerminated", false);
        }
        
        if (eventProcessor != null) {
            status.put("eventProcessorNull", false);
            status.put("eventProcessorShutdown", eventProcessor.isShutdown());
            status.put("eventProcessorTerminated", eventProcessor.isTerminated());
        } else {
            status.put("eventProcessorNull", true);
            status.put("eventProcessorShutdown", false);
            status.put("eventProcessorTerminated", false);
        }
        
        status.put("globalMonitorRunning", globalMonitorRunning);
        
        return status;
    }
    
    /**
     * 获取账号状态信息（用于调试）
     */
    public Map<String, Object> getAccountStatusInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            info.put("totalAccounts", accounts.size());
            
            long enabledAccounts = accounts.stream().filter(a -> "0".equals(a.getStatus())).count();
            long disabledAccounts = accounts.stream().filter(a -> "1".equals(a.getStatus())).count();
            long shouldMonitorAccounts = accounts.stream()
                .filter(a -> "0".equals(a.getStatus()))
                .count();
            
            info.put("enabledAccounts", enabledAccounts);
            info.put("disabledAccounts", disabledAccounts);
            info.put("shouldMonitorAccounts", shouldMonitorAccounts);
            info.put("monitoringAccounts", accountMonitorStates.size());
            
            // 详细账号信息
            List<Map<String, Object>> accountDetails = new ArrayList<>();
            for (EmailAccount account : accounts) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("accountId", account.getAccountId());
                detail.put("emailAddress", account.getEmailAddress());
                detail.put("status", account.getStatus());
                detail.put("statusDesc", "0".equals(account.getStatus()) ? "启用发送邮件" : "禁用发送邮件");
                detail.put("isMonitoring", accountMonitorStates.containsKey(account.getAccountId()));
                accountDetails.add(detail);
            }
            info.put("accountDetails", accountDetails);
            
        } catch (Exception e) {
            logger.error("获取账号状态信息失败", e);
            info.put("error", e.getMessage());
        }
        
        return info;
    }
}
