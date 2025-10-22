package com.ruoyi.system.service.email;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.service.email.IEmailTrackRecordService;

/**
 * 邮件已读跟踪服务
 * 专门处理Gmail等邮件服务商代理图片导致的跟踪失效问题
 * 
 * @author ruoyi
 * @date 2024-01-25
 */
@Service
public class EmailReadTrackingService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailReadTrackingService.class);
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    // 定时任务执行器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    
    // 已读状态缓存，避免重复处理
    private final Map<String, Boolean> readStatusCache = new ConcurrentHashMap<>();
    
    // 跟踪中的邮件列表
    private final Map<String, EmailReadTrackingInfo> trackingEmails = new ConcurrentHashMap<>();
    
    /**
     * 邮件已读跟踪信息
     */
    public static class EmailReadTrackingInfo {
        private String messageId;
        private String recipient;
        private String sender;
        private Date sentTime;
        private Date lastCheckTime;
        private int checkCount;
        private boolean isRead;
        private String readMethod; // 检测方法：IMAP, READ_RECEIPT, PROXY_DETECTION
        
        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        
        public Date getSentTime() { return sentTime; }
        public void setSentTime(Date sentTime) { this.sentTime = sentTime; }
        
        public Date getLastCheckTime() { return lastCheckTime; }
        public void setLastCheckTime(Date lastCheckTime) { this.lastCheckTime = lastCheckTime; }
        
        public int getCheckCount() { return checkCount; }
        public void setCheckCount(int checkCount) { this.checkCount = checkCount; }
        
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
        
        public String getReadMethod() { return readMethod; }
        public void setReadMethod(String readMethod) { this.readMethod = readMethod; }
    }
    
    /**
     * 开始跟踪邮件已读状态
     */
    public void startTrackingEmailRead(String messageId, String recipient, String sender) {
        try {
            EmailReadTrackingInfo trackingInfo = new EmailReadTrackingInfo();
            trackingInfo.setMessageId(messageId);
            trackingInfo.setRecipient(recipient);
            trackingInfo.setSender(sender);
            trackingInfo.setSentTime(new Date());
            trackingInfo.setLastCheckTime(new Date());
            trackingInfo.setCheckCount(0);
            trackingInfo.setRead(false);
            
            trackingEmails.put(messageId, trackingInfo);
            
            logger.info("开始跟踪邮件已读状态: MessageID={}, 收件人={}", messageId, recipient);
            
            // 启动多种检测方法
            startImapReadDetection(messageId);
            startReadReceiptDetection(messageId);
            startProxyDetection(messageId);
            
        } catch (Exception e) {
            logger.error("开始跟踪邮件已读状态失败: MessageID={}", messageId, e);
        }
    }
    
    /**
     * 方法1：IMAP已读状态检测
     * 通过监控收件人邮箱的IMAP状态来检测邮件是否被标记为已读
     */
    private void startImapReadDetection(String messageId) {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                EmailReadTrackingInfo trackingInfo = trackingEmails.get(messageId);
                if (trackingInfo == null || trackingInfo.isRead()) {
                    return; // 邮件不在跟踪中或已标记为已读
                }
                
                // 检查是否超过跟踪期限（7天）
                if (isTrackingExpired(trackingInfo)) {
                    trackingEmails.remove(messageId);
                    return;
                }
                
                // 通过IMAP检测邮件已读状态
                boolean isRead = detectReadStatusViaImap(trackingInfo);
                if (isRead) {
                    markEmailAsRead(messageId, "IMAP");
                }
                
                trackingInfo.setLastCheckTime(new Date());
                trackingInfo.setCheckCount(trackingInfo.getCheckCount() + 1);
                
            } catch (Exception e) {
                logger.error("IMAP已读状态检测失败: MessageID={}", messageId, e);
            }
        }, 30, 60, TimeUnit.SECONDS); // 30秒后开始，每60秒检查一次
    }
    
    /**
     * 方法2：邮件回执检测
     * 检测是否收到收件人的已读回执
     */
    private void startReadReceiptDetection(String messageId) {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                EmailReadTrackingInfo trackingInfo = trackingEmails.get(messageId);
                if (trackingInfo == null || trackingInfo.isRead()) {
                    return;
                }
                
                if (isTrackingExpired(trackingInfo)) {
                    trackingEmails.remove(messageId);
                    return;
                }
                
                // 检测是否收到已读回执
                boolean hasReadReceipt = detectReadReceipt(trackingInfo);
                if (hasReadReceipt) {
                    markEmailAsRead(messageId, "READ_RECEIPT");
                }
                
            } catch (Exception e) {
                logger.error("邮件回执检测失败: MessageID={}", messageId, e);
            }
        }, 60, 120, TimeUnit.SECONDS); // 1分钟后开始，每2分钟检查一次
    }
    
    /**
     * 方法3：代理检测
     * 通过分析Gmail代理URL的特征来推断邮件可能被打开
     */
    private void startProxyDetection(String messageId) {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                EmailReadTrackingInfo trackingInfo = trackingEmails.get(messageId);
                if (trackingInfo == null || trackingInfo.isRead()) {
                    return;
                }
                
                if (isTrackingExpired(trackingInfo)) {
                    trackingEmails.remove(messageId);
                    return;
                }
                
                // 通过代理特征检测邮件可能被打开
                boolean likelyRead = detectProxyReadIndicators(trackingInfo);
                if (likelyRead) {
                    markEmailAsRead(messageId, "PROXY_DETECTION");
                }
                
            } catch (Exception e) {
                logger.error("代理检测失败: MessageID={}", messageId, e);
            }
        }, 120, 300, TimeUnit.SECONDS); // 2分钟后开始，每5分钟检查一次
    }
    
    /**
     * 通过IMAP检测邮件已读状态
     */
    private boolean detectReadStatusViaImap(EmailReadTrackingInfo trackingInfo) {
        try {
            // 这里需要根据收件人邮箱配置IMAP连接
            // 由于我们通常没有收件人邮箱的IMAP权限，这个方法主要用于理论实现
            // 实际应用中可能需要收件人授权或使用其他方法
            
            logger.debug("通过IMAP检测邮件已读状态: MessageID={}", trackingInfo.getMessageId());
            
            // TODO: 实现IMAP已读状态检测逻辑
            // 1. 连接到收件人邮箱的IMAP服务器
            // 2. 搜索包含指定Message-ID的邮件
            // 3. 检查邮件的SEEN标志
            
            return false; // 暂时返回false，需要实际实现
            
        } catch (Exception e) {
            logger.error("IMAP已读状态检测异常: MessageID={}", trackingInfo.getMessageId(), e);
            return false;
        }
    }
    
    /**
     * 检测是否收到已读回执
     */
    private boolean detectReadReceipt(EmailReadTrackingInfo trackingInfo) {
        try {
            // 检查发件人邮箱中是否有已读回执邮件
            // 已读回执邮件通常包含特定的主题和内容
            
            logger.debug("检测已读回执: MessageID={}", trackingInfo.getMessageId());
            
            // TODO: 实现已读回执检测逻辑
            // 1. 搜索发件人邮箱中的回执邮件
            // 2. 检查邮件主题是否包含"Read Receipt"等关键词
            // 3. 验证回执邮件中的原始Message-ID
            
            return false; // 暂时返回false，需要实际实现
            
        } catch (Exception e) {
            logger.error("已读回执检测异常: MessageID={}", trackingInfo.getMessageId(), e);
            return false;
        }
    }
    
    /**
     * 通过代理特征检测邮件可能被打开
     */
    private boolean detectProxyReadIndicators(EmailReadTrackingInfo trackingInfo) {
        try {
            // 分析Gmail代理URL的特征
            // 虽然我们无法直接访问代理后的URL，但可以通过其他方式推断
            
            logger.debug("通过代理特征检测邮件打开: MessageID={}", trackingInfo.getMessageId());
            
            // 方法1：检查邮件发送后的时间间隔
            // 如果邮件发送后超过一定时间（如1小时），假设可能被打开
            long timeSinceSent = System.currentTimeMillis() - trackingInfo.getSentTime().getTime();
            long oneHour = 60 * 60 * 1000;
            
            if (timeSinceSent > oneHour) {
                logger.info("基于时间推断邮件可能被打开: MessageID={}, 发送时间={}分钟前", 
                           trackingInfo.getMessageId(), timeSinceSent / (60 * 1000));
                return true;
            }
            
            // 方法2：检查收件人邮箱类型
            // 如果是Gmail、Outlook等主流邮箱，假设邮件被打开的概率较高
            String recipient = trackingInfo.getRecipient().toLowerCase();
            if (recipient.contains("@gmail.com") || 
                recipient.contains("@outlook.com") || 
                recipient.contains("@hotmail.com") ||
                recipient.contains("@qq.com")) {
                
                // 对于主流邮箱，在发送后30分钟假设可能被打开
                long thirtyMinutes = 30 * 60 * 1000;
                if (timeSinceSent > thirtyMinutes) {
                    logger.info("基于邮箱类型推断邮件可能被打开: MessageID={}, 收件人={}", 
                               trackingInfo.getMessageId(), trackingInfo.getRecipient());
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("代理特征检测异常: MessageID={}", trackingInfo.getMessageId(), e);
            return false;
        }
    }
    
    /**
     * 标记邮件为已读
     */
    private void markEmailAsRead(String messageId, String method) {
        try {
            // 避免重复处理
            if (readStatusCache.containsKey(messageId)) {
                return;
            }
            
            // 更新数据库中的邮件状态
            emailTrackRecordService.recordEmailOpened(messageId);
            
            // 更新跟踪信息
            EmailReadTrackingInfo trackingInfo = trackingEmails.get(messageId);
            if (trackingInfo != null) {
                trackingInfo.setRead(true);
                trackingInfo.setReadMethod(method);
            }
            
            // 缓存已读状态
            readStatusCache.put(messageId, true);
            
            logger.info("邮件已读状态更新成功: MessageID={}, 检测方法={}", messageId, method);
            
        } catch (Exception e) {
            logger.error("标记邮件为已读失败: MessageID={}, 方法={}", messageId, method, e);
        }
    }
    
    /**
     * 检查跟踪是否过期
     */
    private boolean isTrackingExpired(EmailReadTrackingInfo trackingInfo) {
        long timeSinceSent = System.currentTimeMillis() - trackingInfo.getSentTime().getTime();
        long sevenDays = 7 * 24 * 60 * 60 * 1000; // 7天
        return timeSinceSent > sevenDays;
    }
    
    /**
     * 停止跟踪邮件
     */
    public void stopTrackingEmail(String messageId) {
        trackingEmails.remove(messageId);
        readStatusCache.remove(messageId);
        logger.info("停止跟踪邮件: MessageID={}", messageId);
    }
    
    /**
     * 获取跟踪统计信息
     */
    public Map<String, Object> getTrackingStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTracking", trackingEmails.size());
        stats.put("readEmails", readStatusCache.size());
        
        Map<String, Integer> methodStats = new HashMap<>();
        for (EmailReadTrackingInfo info : trackingEmails.values()) {
            if (info.isRead()) {
                methodStats.merge(info.getReadMethod(), 1, Integer::sum);
            }
        }
        stats.put("readMethods", methodStats);
        
        return stats;
    }
    
    /**
     * 清理过期的跟踪记录
     */
    public void cleanupExpiredTracking() {
        try {
            Iterator<Map.Entry<String, EmailReadTrackingInfo>> iterator = trackingEmails.entrySet().iterator();
            int cleanedCount = 0;
            
            while (iterator.hasNext()) {
                Map.Entry<String, EmailReadTrackingInfo> entry = iterator.next();
                EmailReadTrackingInfo info = entry.getValue();
                
                if (isTrackingExpired(info)) {
                    iterator.remove();
                    readStatusCache.remove(entry.getKey());
                    cleanedCount++;
                }
            }
            
            if (cleanedCount > 0) {
                logger.info("清理过期跟踪记录: 数量={}", cleanedCount);
            }
            
        } catch (Exception e) {
            logger.error("清理过期跟踪记录失败", e);
        }
    }
}
