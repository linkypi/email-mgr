package com.ruoyi.system.service.email;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailPersonal;
import com.ruoyi.system.domain.email.EmailServiceMonitor;
import com.ruoyi.system.domain.email.EmailServiceMonitorLog;
import com.ruoyi.system.mapper.email.EmailServiceMonitorLogMapper;
import com.ruoyi.system.mapper.email.EmailServiceMonitorMapper;
import com.ruoyi.system.utils.EmailServiceMonitorUtils;
import com.ruoyi.system.service.ISysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.FolderAdapter;
import javax.mail.event.FolderEvent;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.Flags;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import java.util.Properties;
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
    private EmailListener emailListener;
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    @Autowired
    private IEmailPersonalService emailPersonalService;
    
    @Autowired
    private ISysConfigService sysConfigService;
    
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
        
        // SMTP连接健康度评分 (0-100, 100为最健康)
        volatile int smtpHealthScore = 100;
        volatile long smtpLastSuccessTime = System.currentTimeMillis();
        volatile int smtpConsecutiveFailures = 0;
        
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
     * 负责监控邮件接收和状态变化：
     * - 新邮件接收
     * - 邮件已读/未读状态
     * - 邮件已回复/未回复状态
     * - 邮件星标状态
     * - 邮件删除状态
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
     * 监控邮件接收和状态变化，包括新邮件、已读/未读、已回复/未回复等
     */
    private void performImapMonitoring(Long accountId, EmailAccount account, AccountMonitorState state) {
        // 首先测试连接
        if (!testImapConnectionBeforeMonitoring(account)) {
            logger.error("IMAP连接测试失败，跳过监控: {}", account.getEmailAddress());
            updateImapStatus(accountId, "4", "连接测试失败，请检查网络或服务器配置");
            return;
        }
        
        int retryCount = 0;
        final int maxRetries = Integer.MAX_VALUE; // 无限重试，确保服务持续运行
        final long retryDelay = getImapRetryDelay(); // 从配置获取重试间隔
        final long heartbeatInterval = getImapHeartbeatInterval(); // 从配置获取心跳间隔
        long lastHeartbeatTime = 0;
        long lastDSNScanTime = 0;
        
        while (state.imapIdleRunning && !Thread.currentThread().isInterrupted()) {
            try {
                // 建立IMAP长连接（只在需要时建立）
                if (state.imapStore == null || !state.imapStore.isConnected()) {
                    logger.info("建立IMAP长连接: {}:{}", account.getImapHost(), account.getImapPort());
                    
                    Properties props = createImapProperties(account);
                    Session session = Session.getInstance(props);
                    session.setDebug(false);
                    
                    Store store = session.getStore("imap");
                    store.connect(account.getImapHost(), account.getImapPort(), 
                        account.getImapUsername(), account.getImapPassword());
                    
                    state.imapStore = store;
                    state.imapStatus = "CONNECTED";
                    state.imapLastCheckTime = new Date();
                    state.imapErrorMessage = null;
                    state.imapErrorTime = null;
                    retryCount = 0; // 连接成功，重置重试计数
                    lastHeartbeatTime = System.currentTimeMillis();
                    
                    updateImapStatus(accountId, "3", null); // 使用数字状态码
                    
                    // 打开收件箱
                    Folder folder = store.getFolder("INBOX");
                    folder.open(Folder.READ_WRITE);
                    state.imapFolder = folder;
                    
                    // 添加消息监听器，处理邮件变化事件
                    folder.addMessageCountListener(new MessageCountAdapter() {
                        @Override
                        public void messagesAdded(MessageCountEvent evt) {
                            Message[] messages = evt.getMessages();
                            eventProcessor.submit(() -> {
                                try {
                                    logger.info("检测到新邮件，账户: {}, 数量: {}", account.getEmailAddress(), messages.length);
                                    // 详细记录每封邮件的信息用于DSN调试
                                    for (Message msg : messages) {
                                        if (msg instanceof MimeMessage) {
                                            MimeMessage mimeMsg = (MimeMessage) msg;
                                            try {
                                                String subject = mimeMsg.getSubject();
                                                String contentType = mimeMsg.getContentType();
                                                Address[] from = mimeMsg.getFrom();
                                                String fromStr = from != null && from.length > 0 ? from[0].toString() : "unknown";
                                                
                                                logger.info("新邮件详情 - 主题: {}, 发件人: {}, 内容类型: {}", subject, fromStr, contentType);
                                                
                                                // 特别标记可能的DSN邮件
                                                if (isPotentialDSN(subject, fromStr, contentType)) {
                                                    logger.warn("⚠️ 检测到疑似DSN邮件 - 主题: {}, 发件人: {}", subject, fromStr);
                                                }
                                            } catch (Exception ex) {
                                                logger.error("分析邮件信息时出错", ex);
                                            }
                                        }
                                    }
                                    
                                    processNewMessages(accountId, account, messages);
                                } catch (Exception e) {
                                    logger.error("处理新邮件失败", e);
                                }
                            });
                        }
                        
                        @Override
                        public void messagesRemoved(MessageCountEvent evt) {
                            // 处理邮件删除事件
                            eventProcessor.submit(() -> {
                                try {
                                    logger.info("检测到邮件删除，账户: {}", account.getEmailAddress());
                                    // 处理邮件删除逻辑
                                    processDeletedMessages(accountId, account, evt.getMessages());
                                } catch (Exception e) {
                                    logger.error("处理邮件删除失败", e);
                                }
                            });
                        }
                    });
                    
                    // 添加文件夹监听器，处理邮件状态变化
                    folder.addFolderListener(new FolderAdapter() {
                        @Override
                        public void folderRenamed(FolderEvent e) {
                            logger.debug("文件夹重命名: {}", e.getFolder().getName());
                        }
                        
                        @Override
                        public void folderDeleted(FolderEvent e) {
                            logger.debug("文件夹删除: {}", e.getFolder().getName());
                        }
                    });
                    
                    logger.info("IMAP长连接建立成功，开始监听邮件接收和状态变化: {}", account.getEmailAddress());
                }
                
                // 记录上次检查的邮件数量
                int lastMessageCount = state.imapFolder.getMessageCount();
                
                // 保持长连接并监听
                while (state.imapIdleRunning && !Thread.currentThread().isInterrupted()) {
                    try {
                        // 心跳检测
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastHeartbeatTime > heartbeatInterval) {
                            if (!performHeartbeatCheck(state)) {
                                logger.warn("IMAP心跳检测失败，连接可能已断开: {}", account.getEmailAddress());
                                break; // 跳出内层循环，重新建立连接
                            }
                            lastHeartbeatTime = currentTime;
                        }
                        
                        // 使用轮询方式检查邮件变化，因为JavaMail的Folder类没有idle()方法
                        // 通过比较邮件数量来检测变化
                        int currentMessageCount = state.imapFolder.getMessageCount();
                        
                        // 如果邮件数量发生变化，说明有新邮件
                        if (currentMessageCount != lastMessageCount) {
                            logger.debug("检测到邮件数量变化: {} -> {}, 账户: {}", 
                                lastMessageCount, currentMessageCount, account.getEmailAddress());
                            
                            // 检查是否有新的DSN邮件
                            checkForNewDSNEmails(accountId, account, state.imapFolder, lastMessageCount, currentMessageCount);
                            
                            // 更新最后检查时间
                            state.imapLastCheckTime = new Date();
                            lastMessageCount = currentMessageCount;
                        }
                        
                        // 定期扫描其他文件夹中的DSN邮件（每5分钟）
                        if (System.currentTimeMillis() - lastDSNScanTime > 300000) { // 5分钟
                            scanAllFoldersForDSN(accountId, account, state.imapStore);
                            lastDSNScanTime = System.currentTimeMillis();
                        }
                        
                        // 短暂等待后继续检查
                        Thread.sleep(5000); // 5秒检查一次
                        
                    } catch (MessagingException e) {
                        if (state.imapIdleRunning) {
                            logger.warn("IMAP监听异常，连接可能已断开: {}", e.getMessage());
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
                
                // 清理当前连接资源
                cleanupImapResources(state);
                
                // 无限重试，确保服务持续运行
                if (state.imapIdleRunning) {
                    try {
                        long delay = retryDelay * Math.min(retryCount, 10); // 递增重试延迟，但最大不超过10次
                        logger.info("IMAP连接失败，{}秒后进行第{}次重试: {}", delay/1000, retryCount + 1, account.getEmailAddress());
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        logger.info("IMAP监控线程被中断，停止重试: {}", account.getEmailAddress());
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
    
        /**
     * 处理新收到的邮件
     */
    private void processNewMessages(Long accountId, EmailAccount account, Message[] messages) {
        try {
            // 调用EmailListener处理新邮件
            emailListener.processNewMessages(accountId, account, messages);
            
            // 处理邮件状态变化（通过事件驱动，而不是轮询）
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String messageId = mimeMessage.getMessageID();
                    
                    // 检查是否为送达状态通知（DSN）
                    if (isDeliveryStatusNotification(mimeMessage)) {
                        logger.info("检测到送达状态通知，账户: {}, MessageID: {}", account.getEmailAddress(), messageId);
                        // 异步处理送达状态通知
                        eventProcessor.submit(() -> {
                            try {
                                processDeliveryStatusNotification(accountId, account, mimeMessage);
                            } catch (Exception e) {
                                logger.error("处理送达状态通知失败: {}", messageId, e);
                            }
                        });
                    } else if (messageId != null) {
                        // 异步处理普通邮件状态变化
                        eventProcessor.submit(() -> {
                            try {
                                processEmailStatusChange(accountId, messageId, mimeMessage);
                            } catch (Exception e) {
                                logger.error("处理邮件状态变化失败: {}", messageId, e);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理新邮件失败", e);
        }
    }
    
    /**
     * 快速判断是否为潜在的DSN邮件（用于调试）
     */
    private boolean isPotentialDSN(String subject, String from, String contentType) {
        if (subject != null) {
            String lowerSubject = subject.toLowerCase();
            if (lowerSubject.contains("delivery") || 
                lowerSubject.contains("bounce") ||
                lowerSubject.contains("returned") ||
                lowerSubject.contains("undelivered") ||
                lowerSubject.contains("failure") ||
                lowerSubject.contains("receipt")) {
                return true;
            }
        }
        
        if (from != null) {
            String lowerFrom = from.toLowerCase();
            if (lowerFrom.contains("mailer-daemon") ||
                lowerFrom.contains("postmaster") ||
                lowerFrom.contains("noreply") ||
                lowerFrom.contains("bounce")) {
                return true;
            }
        }
        
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (lowerContentType.contains("multipart/report") ||
                lowerContentType.contains("message/delivery-status")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 判断是否为送达状态通知（DSN）
     */
    private boolean isDeliveryStatusNotification(MimeMessage message) {
        try {
            // 检查Content-Type是否为multipart/report
            String contentType = message.getContentType();
            if (contentType != null && contentType.toLowerCase().contains("multipart/report")) {
                return true;
            }
            
            // 检查主题是否包含送达状态相关关键词
            String subject = message.getSubject();
            if (subject != null) {
                String lowerSubject = subject.toLowerCase();
                if (lowerSubject.contains("delivery status") || 
                    lowerSubject.contains("mail delivery") ||
                    lowerSubject.contains("undelivered") ||
                    lowerSubject.contains("delivery failure") ||
                    lowerSubject.contains("delivery notification") ||
                    lowerSubject.contains("bounce") ||
                    lowerSubject.contains("returned mail") ||
                    lowerSubject.contains("delivery receipt") ||
                    lowerSubject.contains("read receipt")) {
                    return true;
                }
            }
            
            // 检查发件人是否为邮件服务器守护程序
            Address[] from = message.getFrom();
            if (from != null && from.length > 0) {
                String fromAddress = from[0].toString().toLowerCase();
                if (fromAddress.contains("mailer-daemon") ||
                    fromAddress.contains("postmaster") ||
                    fromAddress.contains("mail-daemon") ||
                    fromAddress.contains("noreply") ||
                    fromAddress.contains("no-reply") ||
                    fromAddress.contains("bounce")) {
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            logger.warn("判断送达状态通知时发生异常: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 处理送达状态通知
     */
    private void processDeliveryStatusNotification(Long accountId, EmailAccount account, MimeMessage dsnMessage) {
        try {
            logger.info("开始处理送达状态通知，账户: {}", account.getEmailAddress());
            
            // 解析DSN内容
            String originalMessageId = extractOriginalMessageIdFromDSN(dsnMessage);
            String deliveryStatus = extractDeliveryStatus(dsnMessage);
            String recipientEmail = extractRecipientFromDSN(dsnMessage);
            
            logger.info("DSN解析结果 - 原始MessageID: {}, 状态: {}, 收件人: {}", 
                originalMessageId, deliveryStatus, recipientEmail);
            
            if (originalMessageId != null) {
                // 查找对应的个人邮件记录
                EmailPersonal personalEmail = emailPersonalService.selectEmailPersonalByMessageId(originalMessageId);
                
                if (personalEmail != null) {
                    // 更新送达状态
                    updateEmailDeliveryStatus(personalEmail, deliveryStatus, recipientEmail);
                    logger.info("已更新邮件送达状态: {} -> {}", originalMessageId, deliveryStatus);
                } else {
                    // 可能是通过我们的系统发送的邮件，尝试通过其他方式查找
                    updateDeliveryStatusByReference(originalMessageId, deliveryStatus, recipientEmail);
                }
            } else {
                logger.warn("无法从DSN中提取原始MessageID");
            }
            
        } catch (Exception e) {
            logger.error("处理送达状态通知失败", e);
        }
    }
    
    /**
     * 从DSN消息中提取原始邮件的Message-ID
     */
    private String extractOriginalMessageIdFromDSN(MimeMessage dsnMessage) {
        try {
            // 方法1: 检查Headers中的Original-Message-ID
            String[] originalIds = dsnMessage.getHeader("Original-Message-ID");
            if (originalIds != null && originalIds.length > 0) {
                return originalIds[0];
            }
            
            // 方法2: 尝试从现有方法提取
            String messageId = extractOriginalMessageId(dsnMessage);
            if (messageId != null) {
                return messageId;
            }
            
            // 方法3: 解析邮件内容中的Message-ID引用
            Object content = dsnMessage.getContent();
            if (content instanceof String) {
                return extractMessageIdFromContent((String) content);
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getContentType().toLowerCase().contains("text")) {
                        String partContent = (String) bodyPart.getContent();
                        String extractedId = extractMessageIdFromContent(partContent);
                        if (extractedId != null) {
                            return extractedId;
                        }
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            logger.error("提取原始MessageID失败", e);
            return null;
        }
    }
    
    /**
     * 检查新的DSN邮件
     */
    private void checkForNewDSNEmails(Long accountId, EmailAccount account, Folder folder, int oldCount, int newCount) {
        try {
            if (newCount > oldCount) {
                // 获取新邮件
                Message[] newMessages = folder.getMessages(oldCount + 1, newCount);
                logger.info("检查 {} 封新邮件中的DSN，账户: {}", newMessages.length, account.getEmailAddress());
                
                for (Message message : newMessages) {
                    if (message instanceof MimeMessage) {
                        MimeMessage mimeMessage = (MimeMessage) message;
                        if (isDeliveryStatusNotification(mimeMessage)) {
                            logger.info("在INBOX中发现DSN邮件: {}", mimeMessage.getSubject());
                            processDeliveryStatusNotification(accountId, account, mimeMessage);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("检查新DSN邮件失败", e);
        }
    }
    
    /**
     * 扫描所有文件夹寻找DSN邮件
     */
    private void scanAllFoldersForDSN(Long accountId, EmailAccount account, Store store) {
        try {
            logger.debug("开始扫描所有文件夹寻找DSN邮件，账户: {}", account.getEmailAddress());
            
            // 获取所有文件夹
            Folder[] folders = store.getDefaultFolder().list("*");
            String[] targetFolders = {"INBOX", "Sent", "垃圾邮件", "Junk", "Spam", "已发送", "Sent Messages"};
            
            for (String folderName : targetFolders) {
                try {
                    Folder folder = store.getFolder(folderName);
                    if (folder.exists()) {
                        scanFolderForDSN(accountId, account, folder, folderName);
                    }
                } catch (Exception e) {
                    // 某些文件夹可能不存在或无法访问，继续检查其他文件夹
                    logger.debug("扫描文件夹 {} 失败: {}", folderName, e.getMessage());
                }
            }
            
            // 额外检查其他可能的文件夹
            for (Folder folder : folders) {
                String name = folder.getName().toLowerCase();
                if (name.contains("bounce") || name.contains("delivery") || name.contains("return")) {
                    try {
                        scanFolderForDSN(accountId, account, folder, folder.getName());
                    } catch (Exception e) {
                        logger.debug("扫描文件夹 {} 失败: {}", folder.getName(), e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("扫描所有文件夹失败", e);
        }
    }
    
    /**
     * 扫描指定文件夹中的DSN邮件
     */
    private void scanFolderForDSN(Long accountId, EmailAccount account, Folder folder, String folderName) {
        try {
            if (!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }
            
            // 获取最近3天的邮件 (DSN通常很快就会收到)
            long threeDaysAgo = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);
            Date searchDate = new Date(threeDaysAgo);
            
            // 搜索最近的邮件
            Message[] messages = folder.search(new ReceivedDateTerm(ComparisonTerm.GT, searchDate));
            
            int dsnCount = 0;
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    if (isDeliveryStatusNotification(mimeMessage)) {
                        logger.info("在文件夹 {} 中发现DSN邮件: {}", folderName, mimeMessage.getSubject());
                        processDeliveryStatusNotification(accountId, account, mimeMessage);
                        dsnCount++;
                    }
                }
            }
            
            if (dsnCount > 0) {
                logger.info("在文件夹 {} 中找到 {} 封DSN邮件", folderName, dsnCount);
            }
            
        } catch (Exception e) {
            logger.debug("扫描文件夹 {} 中的DSN邮件失败: {}", folderName, e.getMessage());
        } finally {
            try {
                if (folder.isOpen()) {
                    folder.close(false);
                }
            } catch (Exception e) {
                // 忽略关闭错误
            }
        }
    }
    
    /**
     * 从内容中提取Message-ID
     */
    private String extractMessageIdFromContent(String content) {
        if (content == null) return null;
        
        // 使用正则表达式匹配Message-ID
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "Message-ID:\\s*(<[^>]+>)", 
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = pattern.matcher(content);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * 提取送达状态
     */
    private String extractDeliveryStatus(MimeMessage dsnMessage) {
        try {
            String subject = dsnMessage.getSubject();
            if (subject != null) {
                String lowerSubject = subject.toLowerCase();
                if (lowerSubject.contains("delivery failure") || 
                    lowerSubject.contains("undelivered") ||
                    lowerSubject.contains("bounce")) {
                    return "failed";
                } else if (lowerSubject.contains("delivered") ||
                          lowerSubject.contains("delivery receipt")) {
                    return "delivered";
                } else if (lowerSubject.contains("read receipt")) {
                    return "read";
                }
            }
            
            // 检查内容中的状态信息
            Object content = dsnMessage.getContent();
            if (content instanceof String) {
                String contentStr = ((String) content).toLowerCase();
                if (contentStr.contains("550") || contentStr.contains("permanent failure")) {
                    return "failed";
                } else if (contentStr.contains("250") || contentStr.contains("delivered")) {
                    return "delivered";
                }
            }
            
            return "unknown";
            
        } catch (Exception e) {
            logger.warn("提取送达状态失败: {}", e.getMessage());
            return "unknown";
        }
    }
    
    /**
     * 从DSN中提取收件人邮箱
     */
    private String extractRecipientFromDSN(MimeMessage dsnMessage) {
        try {
            Object content = dsnMessage.getContent();
            if (content instanceof String) {
                String contentStr = (String) content;
                // 使用正则表达式匹配邮箱地址
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
                );
                java.util.regex.Matcher matcher = pattern.matcher(contentStr);
                
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
            
            return null;
            
        } catch (Exception e) {
            logger.warn("提取收件人邮箱失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 更新邮件送达状态
     */
    private void updateEmailDeliveryStatus(EmailPersonal personalEmail, String deliveryStatus, String recipientEmail) {
        try {
            switch (deliveryStatus) {
                case "delivered":
                    personalEmail.setIsDelivered(1);
                    personalEmail.setStatus("delivered");
                    break;
                case "failed":
                    personalEmail.setIsDelivered(0);
                    personalEmail.setStatus("failed");
                    break;
                case "read":
                    personalEmail.setIsDelivered(1);
                    personalEmail.setStatus("read");
                    break;
                default:
                    personalEmail.setStatus("unknown");
                    break;
            }
            
            personalEmail.setUpdateTime(new Date());
            emailPersonalService.updateEmailPersonal(personalEmail);
            
            logger.info("邮件送达状态已更新: MessageID={}, Status={}, Recipient={}", 
                personalEmail.getMessageId(), deliveryStatus, recipientEmail);
                
        } catch (Exception e) {
            logger.error("更新邮件送达状态失败", e);
        }
    }
    
    /**
     * 通过其他方式更新送达状态（当找不到对应的个人邮件记录时）
     */
    private void updateDeliveryStatusByReference(String originalMessageId, String deliveryStatus, String recipientEmail) {
        try {
            // 这里可以添加其他查找逻辑，比如：
            // 1. 通过发送任务记录查找
            // 2. 通过邮件模板或内容匹配
            // 3. 记录到专门的送达状态表中
            
            logger.info("记录未匹配的送达状态通知: MessageID={}, Status={}, Recipient={}", 
                originalMessageId, deliveryStatus, recipientEmail);
                
        } catch (Exception e) {
            logger.error("记录送达状态通知失败", e);
        }
    }

    /**
     * 处理已删除的邮件
     */
    private void processDeletedMessages(Long accountId, EmailAccount account, Message[] messages) {
        try {
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String messageId = mimeMessage.getMessageID();
                    
                    if (messageId != null) {
                        // 查询个人邮件表中的记录
                        EmailPersonal personalEmail = emailPersonalService.selectEmailPersonalByMessageId(messageId);
                        
                        if (personalEmail != null) {
                            // 更新邮件类型为已删除
                            personalEmail.setEmailType("deleted");
                            personalEmail.setUpdateTime(new Date());
                            emailPersonalService.updateEmailPersonal(personalEmail);
                            
                            logger.info("邮件已标记为删除: {}", messageId);
                        } else {
                            // 如果邮件记录不存在，创建新的删除记录
                            try {
                                EmailPersonal deletedEmail = createEmailPersonalFromMessage(mimeMessage, account, "deleted");
                                emailPersonalService.insertEmailPersonal(deletedEmail);
                                
                                logger.info("创建删除邮件记录: {}", messageId);
                            } catch (Exception e) {
                                logger.error("创建删除邮件记录失败: {}", messageId, e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理删除邮件失败", e);
        }
    }
    
    /**
     * 从邮件消息创建个人邮件记录
     */
    private EmailPersonal createEmailPersonalFromMessage(MimeMessage message, EmailAccount account, String emailType) {
        try {
            EmailPersonal personalEmail = new EmailPersonal();
            
            // 设置基本信息
            personalEmail.setMessageId(message.getMessageID());
            personalEmail.setFromAddress(extractEmailAddress(message.getFrom()));
            personalEmail.setToAddress(extractEmailAddress(message.getRecipients(Message.RecipientType.TO)));
            personalEmail.setSubject(message.getSubject());
            personalEmail.setEmailType(emailType);
            personalEmail.setStatus("read"); // 删除的邮件默认为已读
            personalEmail.setIsStarred(0);
            personalEmail.setIsImportant(0);
            personalEmail.setIsReplied(0);
            personalEmail.setIsDelivered(1); // 删除的邮件默认为已送达
            
            // 设置时间
            Date receivedDate = message.getReceivedDate();
            if (receivedDate != null) {
                personalEmail.setReceiveTime(receivedDate);
            } else {
                personalEmail.setReceiveTime(new Date());
            }
            
            Date sentDate = message.getSentDate();
            if (sentDate != null) {
                personalEmail.setSendTime(sentDate);
            }
            
            // 提取邮件内容（使用EmailListener的方法）
            String content = emailListener.extractMessageContent(message);
            personalEmail.setContent(content);
            
            // 提取HTML内容（使用EmailListener的方法）
            String htmlContent = emailListener.extractHtmlContent(message);
            personalEmail.setHtmlContent(htmlContent);
            
            // 计算附件数量（使用EmailListener的方法）
            int attachmentCount = emailListener.countAttachments(message);
            personalEmail.setAttachmentCount(attachmentCount);
            
            // 设置创建时间
            personalEmail.setCreateTime(new Date());
            personalEmail.setUpdateTime(new Date());
            
            return personalEmail;
            
        } catch (Exception e) {
            logger.error("创建邮件记录失败", e);
            throw new RuntimeException("创建邮件记录失败", e);
        }
    }
    
    /**
     * 处理单个邮件的状态变化（事件驱动）
     */
    private void processEmailStatusChange(Long accountId, String messageId, MimeMessage message) {
        try {
            // 查询个人邮件表中的记录
            EmailPersonal personalEmail = emailPersonalService.selectEmailPersonalByMessageId(messageId);
            if (personalEmail != null) {
                // 检查邮件已读状态
                boolean isSeen = message.isSet(Flags.Flag.SEEN);
                String currentStatus = personalEmail.getStatus();
                
                if (isSeen && "unread".equals(currentStatus)) {
                    personalEmail.setStatus("read");
                    personalEmail.setUpdateTime(new Date());
                    emailPersonalService.updateEmailPersonal(personalEmail);
                    logger.debug("邮件已读状态更新: {}", messageId);
                }
                
                // 检查邮件星标状态
                boolean isFlagged = message.isSet(Flags.Flag.FLAGGED);
                int currentStarred = personalEmail.getIsStarred();
                
                if (isFlagged && currentStarred == 0) {
                    personalEmail.setIsStarred(1);
                    personalEmail.setUpdateTime(new Date());
                    emailPersonalService.updateEmailPersonal(personalEmail);
                    logger.debug("邮件星标状态更新: {}", messageId);
                } else if (!isFlagged && currentStarred == 1) {
                    personalEmail.setIsStarred(0);
                    personalEmail.setUpdateTime(new Date());
                    emailPersonalService.updateEmailPersonal(personalEmail);
                    logger.debug("邮件取消星标状态更新: {}", messageId);
                }
                
                // 检查邮件删除状态
                boolean isDeleted = message.isSet(Flags.Flag.DELETED);
                if (isDeleted) {
                    personalEmail.setEmailType("deleted");
                    personalEmail.setUpdateTime(new Date());
                    emailPersonalService.updateEmailPersonal(personalEmail);
                    logger.debug("邮件删除状态更新: {}", messageId);
                }
                
                // 检查邮件回复状态
                String subject = message.getSubject();
                if (subject != null && (subject.startsWith("Re:") || subject.startsWith("回复:"))) {
                    String originalMessageId = extractOriginalMessageId(message);
                    if (originalMessageId != null) {
                        // 更新邮件跟踪记录
                        emailTrackRecordService.recordEmailReplied(originalMessageId);
                        
                        // 更新原始邮件的回复状态
                        EmailPersonal originalEmail = emailPersonalService.selectEmailPersonalByMessageId(originalMessageId);
                        if (originalEmail != null) {
                            originalEmail.setIsReplied(1);
                            originalEmail.setUpdateTime(new Date());
                            emailPersonalService.updateEmailPersonal(originalEmail);
                            logger.debug("邮件回复状态更新: {}", originalMessageId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理邮件状态变化失败: {}", messageId, e);
        }
    }
    
    /**
     * 启动SMTP实时监控
     * 负责监控邮件发送状态：
     * - 邮件发送成功/失败状态
     * - 发送状态通知（DSN - Delivery Status Notification）
     * - 邮件送达确认
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
     * 监控邮件发送状态，包括发送成功、失败、DSN通知等
     */
    private void performSmtpMonitoring(Long accountId, EmailAccount account, AccountMonitorState state) {
        int retryCount = 0;
        final int maxRetries = Integer.MAX_VALUE; // 无限重试，确保服务持续运行
        final long retryDelay = getSmtpRetryDelay(); // 从配置获取重试间隔
        final long heartbeatInterval = getSmtpHeartbeatInterval(); // 从配置获取心跳间隔
        long lastHeartbeatTime = 0;
        
        while (state.smtpMonitorRunning && !Thread.currentThread().isInterrupted()) {
            try {
                // 建立SMTP长连接（用于实际发送邮件）
                if (state.smtpTransport == null || !state.smtpTransport.isConnected()) {
                    logger.info("需要建立SMTP长连接: {}:{}, 原因: Transport={}, Connected={}", 
                        account.getSmtpHost(), account.getSmtpPort(), 
                        state.smtpTransport != null ? "exists" : "null",
                        state.smtpTransport != null ? state.smtpTransport.isConnected() : "N/A");
                    
                    // 创建SMTP Session和Transport
                    Properties props = createSmtpProperties(account);
                    Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(account.getEmailAddress(), account.getPassword());
                        }
                    });
                    
                    Transport transport = session.getTransport("smtp");
                    transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                        account.getEmailAddress(), account.getPassword());
                    
                    state.smtpSession = session;
                    state.smtpTransport = transport;
                    state.smtpStatus = "CONNECTED";
                    state.smtpLastCheckTime = new Date();
                    state.smtpErrorMessage = null;
                    state.smtpErrorTime = null;
                    retryCount = 0; // 连接成功，重置重试计数
                    lastHeartbeatTime = System.currentTimeMillis();
                    
                    updateSmtpStatus(accountId, "3", null); // 使用数字状态码
                    
                    logger.info("SMTP长连接建立成功，可用于邮件发送: {}", account.getEmailAddress());
                }
                
                // 监控SMTP连接状态和维护长连接
                while (state.smtpMonitorRunning && !Thread.currentThread().isInterrupted()) {
                    try {
                        // SMTP心跳检测
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastHeartbeatTime > heartbeatInterval) {
                            if (!performSmtpHeartbeatCheck(state)) {
                                logger.warn("SMTP心跳检测失败，连接可能已断开: {} (健康度评分: {} -> {})", 
                                           account.getEmailAddress(), 
                                           state.smtpHealthScore,
                                           Math.max(0, state.smtpHealthScore - Math.min(30, (state.smtpConsecutiveFailures + 1) * 10)));
                                updateSmtpHealthScore(state, false); // 更新健康度评分
                                break; // 跳出内层循环，重新建立连接
                            } else {
                                updateSmtpHealthScore(state, true); // 更新健康度评分
                                logger.debug("SMTP心跳检测成功: {} (健康度评分: {})", account.getEmailAddress(), state.smtpHealthScore);
                            }
                            lastHeartbeatTime = currentTime;
                        }
                        
                        // 更新最后检查时间
                        state.smtpLastCheckTime = new Date();
                        
                        // 使用配置的心跳间隔进行SMTP连接状态检查
                        Thread.sleep(heartbeatInterval);
                        
                    } catch (Exception e) {
                        logger.warn("SMTP监控过程中发生异常: {}", e.getMessage());
                        // 检查SMTP连接是否断开
                        if (state.smtpTransport != null && !state.smtpTransport.isConnected()) {
                            logger.warn("SMTP连接断开，尝试重新连接");
                            break; // 跳出内层循环，重新建立连接
                        }
                        Thread.sleep(5000); // 短暂等待后继续
                    }
                }
                
            } catch (Exception e) {
                retryCount++;
                logger.error("SMTP监控连接失败 (重试 {}/{}): {}", retryCount, maxRetries, e.getMessage());
                
                // 使用工具类分析异常状态
                String errorStatus = EmailServiceMonitorUtils.determineStatusFromException(e);
                String errorMessage = EmailServiceMonitorUtils.getErrorMessage(e);
                updateSmtpStatus(accountId, errorStatus, errorMessage);
                
                // 清理当前连接资源
                cleanupSmtpResources(state);
                
                // 无限重试，确保服务持续运行，但使用智能退避策略
                if (state.smtpMonitorRunning) {
                    try {
                        // 实现指数退避算法，避免频繁重连给服务器造成压力
                        // 对于QQ邮箱等容易断开连接的服务商，使用渐进式重试策略
                        long baseDelay = retryDelay;
                        long backoffDelay;
                        
                        if (retryCount <= 3) {
                            // 前3次重试使用较短间隔(10秒)
                            backoffDelay = 10000L;
                        } else if (retryCount <= 10) {
                            // 4-10次重试使用中等间隔(30-60秒)
                            backoffDelay = 30000L + (retryCount - 3) * 5000L;
                        } else if (retryCount <= 20) {
                            // 11-20次重试使用较长间隔(2-5分钟)
                            backoffDelay = 120000L + (retryCount - 10) * 20000L;
                        } else {
                            // 超过20次重试使用固定长间隔(10分钟)，避免持续骚扰服务器
                            backoffDelay = 600000L;
                        }
                        
                        // 最大重试间隔限制为10分钟
                        backoffDelay = Math.min(backoffDelay, 600000L);
                        
                        logger.info("SMTP监控连接失败，{}秒后进行第{}次重试: {} (使用智能退避策略)", 
                                   backoffDelay/1000, retryCount + 1, account.getEmailAddress());
                        Thread.sleep(backoffDelay);
                    } catch (InterruptedException ie) {
                        logger.info("SMTP监控线程被中断，停止重试: {}", account.getEmailAddress());
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
    

    
    /**
     * 检查发送状态通知（DSN）
     */
    private void checkDeliveryStatusNotifications(Long accountId, EmailAccount account, Folder inbox) throws MessagingException {
        // 查找DSN邮件
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        
        for (Message message : messages) {
            if (message instanceof MimeMessage) {
                MimeMessage mimeMessage = (MimeMessage) message;
                String subject = mimeMessage.getSubject();
                
                if (subject != null && subject.contains("Delivery Status Notification")) {
                    // 处理DSN邮件
                    processDeliveryStatusNotification(mimeMessage, account);
                    
                    // 标记为已读
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            }
        }
    }
    
    /**
     * 处理发送状态通知
     */
    private void processDeliveryStatusNotification(MimeMessage message, EmailAccount account) {
        try {
            // 从DSN邮件中提取原始Message-ID
            String originalMessageId = extractOriginalMessageIdFromDSN(message);
            
            if (originalMessageId != null) {
                // 更新邮件跟踪记录
                emailTrackRecordService.recordEmailDelivered(originalMessageId);
                
                // 更新个人邮件记录
                EmailPersonal personalEmail = emailPersonalService.selectEmailPersonalByMessageId(originalMessageId);
                if (personalEmail != null) {
                    personalEmail.setIsDelivered(1);
                    personalEmail.setUpdateTime(new Date());
                    emailPersonalService.updateEmailPersonal(personalEmail);
                }
                
                logger.info("处理发送状态通知: {}", originalMessageId);
            }
        } catch (Exception e) {
            logger.error("处理发送状态通知失败", e);
        }
    }
    
    
    /**
     * 提取原始Message-ID
     */
    private String extractOriginalMessageId(MimeMessage message) {
        try {
            // 从邮件头中提取
            String[] headers = message.getHeader("In-Reply-To");
            if (headers != null && headers.length > 0) {
                return headers[0].replaceAll("[<>]", "");
            }
            
            // 从References头中提取
            headers = message.getHeader("References");
            if (headers != null && headers.length > 0) {
                String references = headers[0];
                String[] refs = references.split("\\s+");
                if (refs.length > 0) {
                    return refs[0].replaceAll("[<>]", "");
                }
            }
            
            return null;
        } catch (Exception e) {
            logger.error("提取原始Message-ID失败", e);
            return null;
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
            
            Properties props = createImapProperties(account);
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
     * 执行IMAP心跳检测
     */
    private boolean performHeartbeatCheck(AccountMonitorState state) {
        try {
            if (state.imapStore == null || !state.imapStore.isConnected()) {
                return false;
            }
            
            if (state.imapFolder == null || !state.imapFolder.isOpen()) {
                return false;
            }
            
            // 执行一个简单的操作来检测连接是否有效
            state.imapFolder.getMessageCount();
            
            logger.debug("IMAP心跳检测成功");
            return true;
            
        } catch (Exception e) {
            logger.warn("IMAP心跳检测失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 更新SMTP连接健康度评分
     * @param state 账户监控状态
     * @param success 心跳检测是否成功
     */
    private void updateSmtpHealthScore(AccountMonitorState state, boolean success) {
        long currentTime = System.currentTimeMillis();
        
        if (success) {
            // 成功时提升健康度评分
            state.smtpConsecutiveFailures = 0;
            state.smtpLastSuccessTime = currentTime;
            
            // 根据连续成功情况逐步恢复健康度
            if (state.smtpHealthScore < 100) {
                state.smtpHealthScore = Math.min(100, state.smtpHealthScore + 10);
            }
            
            logger.debug("SMTP连接健康度评分更新: {} (连续成功)", state.smtpHealthScore);
        } else {
            // 失败时降低健康度评分
            state.smtpConsecutiveFailures++;
            
            // 根据连续失败次数降低健康度评分
            int penalty = Math.min(30, state.smtpConsecutiveFailures * 10);
            state.smtpHealthScore = Math.max(0, state.smtpHealthScore - penalty);
            
            logger.debug("SMTP连接健康度评分更新: {} (连续失败{}次)", 
                        state.smtpHealthScore, state.smtpConsecutiveFailures);
        }
    }
    
    /**
     * 获取SMTP连接健康度评分
     * @param accountId 账户ID
     * @return 健康度评分 (0-100)，-1表示未找到状态
     */
    public int getSmtpHealthScore(Long accountId) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        if (state != null) {
            return state.smtpHealthScore;
        }
        return -1;
    }
    
    /**
     * 检查SMTP连接是否健康
     * @param accountId 账户ID
     * @return true如果连接健康度评分>=60
     */
    public boolean isSmtpConnectionHealthy(Long accountId) {
        int score = getSmtpHealthScore(accountId);
        return score >= 60; // 60分以上视为健康
    }
    
    /**
     * 判断SMTP响应消息是否表示成功
     * 支持各种SMTP服务商的响应格式
     * @param responseMessage 响应消息
     * @return true如果是成功响应
     */
    private boolean isSmtpSuccessResponse(String responseMessage) {
        if (responseMessage == null || responseMessage.trim().isEmpty()) {
            return false;
        }
        
        String msg = responseMessage.trim();
        
        // 标准SMTP成功响应码：2xx
        if (msg.matches("^2\\d{2}\\s.*")) {
            return true;
        }
        
        // QQ邮箱格式：250 OK from xxx to xxx
        if (msg.contains("250") && msg.contains("OK")) {
            return true;
        }
        
        // 网易邮箱等格式：250 OK
        if (msg.matches(".*250.*OK.*")) {
            return true;
        }
        
        // Gmail等格式：250 2.0.0 OK
        if (msg.matches(".*250.*2\\.\\d+\\.\\d+.*OK.*")) {
            return true;
        }
        
        // 其他常见成功响应
        if (msg.matches(".*(220|221|225|250|251|252).*")) {
            // 进一步检查是否包含错误关键词
            String lowerMsg = msg.toLowerCase();
            if (lowerMsg.contains("error") || lowerMsg.contains("failed") || 
                lowerMsg.contains("timeout") || lowerMsg.contains("refused")) {
                return false;
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * 执行SMTP监控心跳检测
     */
    private boolean performSmtpHeartbeatCheck(AccountMonitorState state) {
        try {
            // 检查SMTP连接
            if (state.smtpTransport == null) {
                logger.warn("SMTP心跳检测失败: Transport为null");
                return false;
            }
            
            if (!state.smtpTransport.isConnected()) {
                logger.warn("SMTP心跳检测失败: Transport未连接");
                return false;
            }
            
            // 使用真实的SMTP命令来验证连接有效性并保持连接活跃
            try {
                // 方法1: 尝试发送NOOP命令（如果Transport支持SMTPTransport类型）
                if (state.smtpTransport instanceof com.sun.mail.smtp.SMTPTransport) {
                    com.sun.mail.smtp.SMTPTransport smtpTransport = (com.sun.mail.smtp.SMTPTransport) state.smtpTransport;
                    
                    try {
                        // 发送NOOP命令来保持连接活跃并测试连接有效性
                        // NOOP命令不会对服务器产生副作用，但能验证连接状态
                        smtpTransport.issueCommand("NOOP", 250);
                        logger.debug("SMTP心跳检测成功: NOOP命令响应正常，连接保持活跃");
                        return true;
                    } catch (Exception noopEx) {
                        // 分析异常信息，QQ邮箱和其他SMTP服务商的成功响应可能被当作异常抛出
                        String errorMsg = noopEx.getMessage();
                        if (isSmtpSuccessResponse(errorMsg)) {
                            logger.debug("SMTP心跳检测成功: NOOP命令返回成功响应: {}", 
                                       errorMsg != null ? errorMsg.replaceAll("[\\r\\n]", " ") : "null");
                            return true;
                        }
                        
                        logger.warn("SMTP NOOP命令失败，连接可能已断开: {} (响应解析失败，非250 OK格式)", errorMsg);
                        
                        // 尝试备用的连接检测方法
                        try {
                            // 发送RSET命令作为备用心跳检测
                            smtpTransport.issueCommand("RSET", 250);
                            logger.debug("SMTP备用心跳检测成功: RSET命令响应正常");
                            return true;
                        } catch (Exception rsetEx) {
                            String rsetErrorMsg = rsetEx.getMessage();
                            // 同样检查RSET的成功响应
                            if (isSmtpSuccessResponse(rsetErrorMsg)) {
                                logger.debug("SMTP备用心跳检测成功: RSET命令返回成功响应: {}", 
                                           rsetErrorMsg != null ? rsetErrorMsg.replaceAll("[\\r\\n]", " ") : "null");
                                return true;
                            }
                            logger.warn("SMTP备用心跳检测也失败: {}", rsetErrorMsg);
                            return false;
                        }
                    }
                } else {
                    // 方法2: 对于其他类型的Transport，尝试检查连接属性
                    try {
                        String protocol = state.smtpTransport.getURLName().getProtocol();
                        String host = state.smtpTransport.getURLName().getHost();
                        
                        // 尝试获取更多属性来验证连接仍然有效
                        if (protocol != null && host != null) {
                            logger.debug("SMTP心跳检测成功: protocol={}, host={}", protocol, host);
                            
                            // 尝试更深入的连接验证
                            try {
                                state.smtpTransport.toString(); // 简单的操作来触发可能的连接检查
                                return true;
                            } catch (Exception toStringEx) {
                                logger.warn("SMTP深度连接检查失败: {}", toStringEx.getMessage());
                                return false;
                            }
                        } else {
                            logger.warn("SMTP连接属性异常: protocol={}, host={}", protocol, host);
                            return false;
                        }
                    } catch (Exception propEx) {
                        logger.warn("SMTP连接属性检查失败: {}", propEx.getMessage());
                        return false;
                    }
                }

            } catch (Exception verifyEx) {
                logger.warn("SMTP连接验证失败: {}", verifyEx.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            logger.warn("SMTP心跳检测异常: {}", e.getMessage());
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
            
            // 使用EmailListener进行IMAP连接测试
            boolean testResult = emailListener.isAccountConnected(account.getAccountId());
            if (testResult) {
                updateImapStatus(accountId, "TEST_SUCCESS", null);
                return new HashMap<String, Object>() {{
                    put("success", true);
                    put("message", "IMAP连接测试成功");
                }};
            } else {
                updateImapStatus(accountId, "TEST_FAILED", "连接失败");
                return new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "IMAP连接测试失败");
                }};
            }
            
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
            
            // 使用EmailListener进行SMTP连接测试
            boolean testResult = emailListener.isAccountConnected(account.getAccountId());
            if (testResult) {
                updateSmtpStatus(accountId, "TEST_SUCCESS", null);
                return new HashMap<String, Object>() {{
                    put("success", true);
                    put("message", "SMTP连接测试成功");
                }};
            } else {
                updateSmtpStatus(accountId, "TEST_FAILED", "连接失败");
                return new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "SMTP连接测试失败");
                }};
            }
            
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
            
            // 使用EmailListener发送邮件
            String messageId = emailListener.sendEmailWithTracking(senderAccount, adminEmail, subject, content, null);
                
            if (messageId != null) {
                logger.info("管理员通知邮件发送成功，Message-ID: {}", messageId);
                recordOperation(accountId, "NOTIFICATION", "SUCCESS", "管理员通知邮件发送成功");
            } else {
                logger.error("管理员通知邮件发送失败");
                recordOperation(accountId, "NOTIFICATION", "FAILED", "管理员通知邮件发送失败");
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
                    if (emailListener.isAccountConnected(account.getAccountId())) {
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
    
    /**
     * 获取IMAP重试延迟时间（毫秒）
     */
    private long getImapRetryDelay() {
        try {
            String configValue = sysConfigService.selectConfigByKey("email.imap.retry.delay");
            if (configValue != null && !configValue.isEmpty()) {
                long delay = Convert.toLong(configValue, 5000L); // 默认5秒
                // 限制在5-30秒范围内
                return Math.max(5000L, Math.min(delay, 30000L));
            }
        } catch (Exception e) {
            logger.warn("获取IMAP重试延迟配置失败，使用默认值", e);
        }
        return 5000L; // 默认5秒
    }
    
    /**
     * 获取IMAP心跳检测间隔（毫秒）
     */
    private long getImapHeartbeatInterval() {
        try {
            String configValue = sysConfigService.selectConfigByKey("email.imap.heartbeat.interval");
            if (configValue != null && !configValue.isEmpty()) {
                long interval = Convert.toLong(configValue, 10000L); // 默认10秒
                // 限制在5-30秒范围内
                return Math.max(5000L, Math.min(interval, 30000L));
            }
        } catch (Exception e) {
            logger.warn("获取IMAP心跳检测间隔配置失败，使用默认值", e);
        }
        return 10000L; // 默认10秒
    }
    
    /**
     * 获取SMTP重试延迟时间（毫秒）
     */
    private long getSmtpRetryDelay() {
        try {
            String configValue = sysConfigService.selectConfigByKey("email.smtp.retry.delay");
            if (configValue != null && !configValue.isEmpty()) {
                long delay = Convert.toLong(configValue, 5000L); // 默认5秒
                // 限制在5-30秒范围内
                return Math.max(5000L, Math.min(delay, 30000L));
            }
        } catch (Exception e) {
            logger.warn("获取SMTP重试延迟配置失败，使用默认值", e);
        }
        return 5000L; // 默认5秒
    }
    
    /**
     * 获取SMTP心跳检测间隔（毫秒）
     */
    private long getSmtpHeartbeatInterval() {
        try {
            String configValue = sysConfigService.selectConfigByKey("email.smtp.heartbeat.interval");
            if (configValue != null && !configValue.isEmpty()) {
                long interval = Convert.toLong(configValue, 15000L); // 默认15秒
                // 限制在3-20秒范围内，适应QQ邮箱等服务商的短超时特性
                return Math.max(3000L, Math.min(interval, 20000L));
            }
        } catch (Exception e) {
            logger.warn("获取SMTP心跳检测间隔配置失败，使用默认值", e);
        }
        return 15000L; // 默认15秒，适应QQ邮箱等服务商的超时特性
    }
    
    /**
     * 获取账户的SMTP长连接Transport
     * 供EmailSendService使用
     */
    public Transport getSmtpLongConnection(Long accountId) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        
        // 如果账户未在监控中，先启动监控
        if (state == null) {
            logger.info("账户 {} 未在监控中，尝试启动SMTP监控", accountId);
            try {
                EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
                if (account == null) {
                    logger.error("账户 {} 不存在，无法启动SMTP监控", accountId);
                    return null;
                }
                
                state = new AccountMonitorState();
                accountMonitorStates.put(accountId, state);
                startSmtpMonitoring(accountId, account, state);
                
                // 给监控一些时间来建立连接
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.error("启动账户 {} 的SMTP监控失败", accountId, e);
                return null;
            }
        }
        
        // 检查连接状态
        if (state != null && state.smtpTransport != null && state.smtpTransport.isConnected()) {
            logger.debug("获取到账户 {} 的SMTP长连接", accountId);
            return state.smtpTransport;
        }
        
        // 如果连接不存在或已断开，尝试创建
        logger.info("账户 {} 的SMTP长连接不可用，尝试创建新连接", accountId);
        return createSmtpLongConnection(accountId);
    }
    
    /**
     * 获取账户的SMTP Session
     * 供EmailSendService使用
     */
    public Session getSmtpSession(Long accountId) {
        AccountMonitorState state = accountMonitorStates.get(accountId);
        if (state != null && state.smtpSession != null) {
            return state.smtpSession;
        }
        
        // 如果Session不存在，尝试创建连接（这会同时创建Session）
        Transport transport = getSmtpLongConnection(accountId);
        if (transport != null) {
            state = accountMonitorStates.get(accountId);
            return state != null ? state.smtpSession : null;
        }
        
        return null;
    }
    
    /**
     * 为账户创建SMTP长连接（如果不存在）
     * 供EmailSendService使用
     */
    public synchronized Transport createSmtpLongConnection(Long accountId) {
        try {
            AccountMonitorState state = accountMonitorStates.get(accountId);
            if (state == null) {
                logger.info("账户 {} 未在监控中，创建监控状态并建立SMTP长连接", accountId);
                // 创建监控状态
                state = new AccountMonitorState();
                accountMonitorStates.put(accountId, state);
            }
            
            // 检查现有连接是否可用
            if (state.smtpTransport != null && state.smtpTransport.isConnected()) {
                return state.smtpTransport;
            }
            
            // 获取账户信息
            EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
            if (account == null) {
                logger.error("账户 {} 不存在，无法创建SMTP长连接", accountId);
                return null;
            }
            
            // 创建SMTP连接
            Properties props = createSmtpProperties(account);
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account.getEmailAddress(), account.getPassword());
                }
            });
            
            Transport transport = session.getTransport("smtp");
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            
            // 保存连接到状态中
            state.smtpSession = session;
            state.smtpTransport = transport;
            
            logger.info("为账户 {} 创建SMTP长连接成功: {}:{}", 
                accountId, account.getSmtpHost(), account.getSmtpPort());
            
            return transport;
            
        } catch (Exception e) {
            logger.error("为账户 {} 创建SMTP长连接失败: {}", accountId, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 创建SMTP连接属性
     */
    private Properties createSmtpProperties(EmailAccount account) {
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
        
        // 设置超时时间 - 针对长连接优化，增加超时时间
        props.put("mail.smtp.timeout", "120000"); // 120秒读取超时（增加）
        props.put("mail.smtp.connectiontimeout", "60000"); // 60秒连接超时（增加）
        props.put("mail.smtp.writetimeout", "120000"); // 120秒写入超时（增加）
        
        // 信任所有主机（用于开发环境）
        props.put("mail.smtp.ssl.trust", "*");
        
        // 连接保持和优化
        props.put("mail.smtp.keepalive", "true");
        props.put("mail.smtp.userset", "true"); // 启用连接复用
        
        // 认证设置
        props.put("mail.smtp.auth.plain.disable", "false");
        props.put("mail.smtp.auth.login.disable", "false");
        
        // 其他优化设置
        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.allow8bitmime", "true");
        props.put("mail.smtp.sendpartial", "true");
        
        // QQ邮箱特殊设置
        if (account.getSmtpHost().contains("qq.com")) {
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.quitwait", "false"); // QQ邮箱优化
        }
        
        // Gmail特殊设置
        if (account.getSmtpHost().contains("gmail.com")) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        }
        
        return props;
    }
    
    /**
     * 创建IMAP连接属性
     */
    private Properties createImapProperties(EmailAccount account) {
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
            props.setProperty("mail.imap.ssl.enable", "true");
            props.setProperty("mail.imap.ssl.trust", "*");
            props.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
        } else if (port == 143) {
            // 非SSL端口
            props.setProperty("mail.imap.ssl.enable", "false");
            props.setProperty("mail.imap.starttls.enable", "true");
            props.setProperty("mail.imap.starttls.required", "true");
        } else {
            // 其他端口，默认启用SSL
            props.setProperty("mail.imap.ssl.enable", "true");
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
}
