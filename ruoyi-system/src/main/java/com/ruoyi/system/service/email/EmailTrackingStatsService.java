package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.IEmailTrackRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 邮件跟踪统计服务
 * 用于首页统计显示
 */
@Service
public class EmailTrackingStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailTrackingStatsService.class);
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    /**
     * 获取邮件发送统计概览
     */
    public Map<String, Object> getSendStatsOverview() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 查询各种状态的邮件数量
            EmailTrackRecord queryRecord = new EmailTrackRecord();
            
            // 总发送量
            List<EmailTrackRecord> allRecords = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            stats.put("totalSent", allRecords.size());
            
            // 发送成功
            queryRecord.setStatus("SEND_SUCCESS");
            List<EmailTrackRecord> successRecords = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            stats.put("totalDelivered", successRecords.size());
            
            // 发送失败
            queryRecord.setStatus("SEND_FAILED");
            List<EmailTrackRecord> failedRecords = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            stats.put("totalFailed", failedRecords.size());
            
            // 已打开
            List<EmailTrackRecord> openedRecords = new ArrayList<>();
            for (EmailTrackRecord record : allRecords) {
                if (record.getOpenedTime() != null) {
                    openedRecords.add(record);
                }
            }
            stats.put("totalOpened", openedRecords.size());
            
            // 已回复
            List<EmailTrackRecord> repliedRecords = new ArrayList<>();
            for (EmailTrackRecord record : allRecords) {
                if (record.getRepliedTime() != null) {
                    repliedRecords.add(record);
                }
            }
            stats.put("totalReplied", repliedRecords.size());
            
            // 计算比率
            int totalSent = allRecords.size();
            if (totalSent > 0) {
                stats.put("deliveryRate", String.format("%.2f%%", (double) successRecords.size() / totalSent * 100));
                stats.put("openRate", String.format("%.2f%%", (double) openedRecords.size() / totalSent * 100));
                stats.put("replyRate", String.format("%.2f%%", (double) repliedRecords.size() / totalSent * 100));
            } else {
                stats.put("deliveryRate", "0.00%");
                stats.put("openRate", "0.00%");
                stats.put("replyRate", "0.00%");
            }
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取邮件发送统计概览失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 获取指定账号的邮件统计
     */
    public Map<String, Object> getAccountStats(Long accountId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            EmailTrackRecord queryRecord = new EmailTrackRecord();
            queryRecord.setAccountId(accountId);
            
            List<EmailTrackRecord> allRecords = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            stats.put("totalSent", allRecords.size());
            
            // 按状态统计
            Map<String, Integer> statusStats = new HashMap<>();
            for (EmailTrackRecord record : allRecords) {
                String status = record.getStatus();
                statusStats.put(status, statusStats.getOrDefault(status, 0) + 1);
            }
            stats.put("statusStats", statusStats);
            
            // 已打开和已回复统计
            int openedCount = 0;
            int repliedCount = 0;
            for (EmailTrackRecord record : allRecords) {
                if (record.getOpenedTime() != null) {
                    openedCount++;
                }
                if (record.getRepliedTime() != null) {
                    repliedCount++;
                }
            }
            stats.put("openedCount", openedCount);
            stats.put("repliedCount", repliedCount);
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取账号邮件统计失败: {}", accountId, e);
            return new HashMap<>();
        }
    }
    
    /**
     * 获取邮件发送趋势数据
     */
    public Map<String, Object> getSendTrends(int days) {
        try {
            Map<String, Object> trends = new HashMap<>();
            
            // 计算日期范围
            Calendar calendar = Calendar.getInstance();
            Date endDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, -days);
            Date startDate = calendar.getTime();
            
            // 查询指定日期范围内的邮件
            EmailTrackRecord queryRecord = new EmailTrackRecord();
            // 这里需要根据实际需求设置日期查询条件
            
            List<EmailTrackRecord> records = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            
            // 按日期分组统计
            Map<String, Map<String, Integer>> dailyStats = new HashMap<>();
            for (EmailTrackRecord record : records) {
                Date sentDate = record.getSentTime();
                if (sentDate != null && sentDate.after(startDate) && sentDate.before(endDate)) {
                    String dateKey = new java.text.SimpleDateFormat("yyyy-MM-dd").format(sentDate);
                    dailyStats.computeIfAbsent(dateKey, k -> new HashMap<>());
                    
                    Map<String, Integer> dayStats = dailyStats.get(dateKey);
                    dayStats.put("sent", dayStats.getOrDefault("sent", 0) + 1);
                    
                    if ("SEND_SUCCESS".equals(record.getStatus())) {
                        dayStats.put("delivered", dayStats.getOrDefault("delivered", 0) + 1);
                    }
                    
                    if (record.getOpenedTime() != null) {
                        dayStats.put("opened", dayStats.getOrDefault("opened", 0) + 1);
                    }
                    
                    if (record.getRepliedTime() != null) {
                        dayStats.put("replied", dayStats.getOrDefault("replied", 0) + 1);
                    }
                }
            }
            
            trends.put("dailyStats", dailyStats);
            return trends;
            
        } catch (Exception e) {
            logger.error("获取邮件发送趋势失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 获取邮件状态分布
     */
    public Map<String, Object> getStatusDistribution() {
        try {
            Map<String, Object> distribution = new HashMap<>();
            
            EmailTrackRecord queryRecord = new EmailTrackRecord();
            List<EmailTrackRecord> allRecords = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            
            Map<String, Integer> statusCount = new HashMap<>();
            for (EmailTrackRecord record : allRecords) {
                String status = record.getStatus();
                statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
            }
            
            distribution.put("statusDistribution", statusCount);
            distribution.put("totalCount", allRecords.size());
            
            return distribution;
            
        } catch (Exception e) {
            logger.error("获取邮件状态分布失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 获取最近发送的邮件列表
     */
    public List<Map<String, Object>> getRecentEmails(int limit) {
        try {
            EmailTrackRecord queryRecord = new EmailTrackRecord();
            List<EmailTrackRecord> records = emailTrackRecordService.selectEmailTrackRecordList(queryRecord);
            
            // 按发送时间排序，取最近的
            records.sort((a, b) -> {
                if (a.getSentTime() == null && b.getSentTime() == null) return 0;
                if (a.getSentTime() == null) return 1;
                if (b.getSentTime() == null) return -1;
                return b.getSentTime().compareTo(a.getSentTime());
            });
            
            List<Map<String, Object>> recentEmails = new ArrayList<>();
            int count = Math.min(limit, records.size());
            for (int i = 0; i < count; i++) {
                EmailTrackRecord record = records.get(i);
                Map<String, Object> emailInfo = new HashMap<>();
                emailInfo.put("messageId", record.getMessageId());
                emailInfo.put("subject", record.getSubject());
                emailInfo.put("recipient", record.getRecipient());
                emailInfo.put("status", record.getStatus());
                emailInfo.put("sentTime", record.getSentTime());
                emailInfo.put("openedTime", record.getOpenedTime());
                emailInfo.put("repliedTime", record.getRepliedTime());
                recentEmails.add(emailInfo);
            }
            
            return recentEmails;
            
        } catch (Exception e) {
            logger.error("获取最近发送的邮件列表失败", e);
            return new ArrayList<>();
        }
    }
}
