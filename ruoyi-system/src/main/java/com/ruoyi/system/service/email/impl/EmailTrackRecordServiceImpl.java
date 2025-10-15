package com.ruoyi.system.service.email.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.service.email.EmailSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailTrackRecordMapper;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.IEmailTrackRecordService;

/**
 * 邮件跟踪记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailTrackRecordServiceImpl implements IEmailTrackRecordService 
{
    @Autowired
    private EmailTrackRecordMapper emailTrackRecordMapper;

    private static final Logger logger = LoggerFactory.getLogger(EmailTrackRecordServiceImpl.class);

    /**
     * 查询邮件跟踪记录
     * 
     * @param id 邮件跟踪记录主键
     * @return 邮件跟踪记录
     */
    @Override
    public EmailTrackRecord selectEmailTrackRecordById(Long id)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordById(id);
    }

    /**
     * 根据Message-ID查询邮件跟踪记录
     * 
     * @param messageId 邮件Message-ID
     * @return 邮件跟踪记录
     */
    @Override
    public EmailTrackRecord selectEmailTrackRecordByMessageId(String messageId)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordByMessageId(messageId);
    }

    /**
     * 查询邮件跟踪记录列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录
     */
    @Override
    public List<EmailTrackRecord> selectEmailTrackRecordList(EmailTrackRecord emailTrackRecord)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordList(emailTrackRecord);
    }

    /**
     * 根据任务ID查询邮件跟踪记录列表
     * 
     * @param taskId 任务ID
     * @return 邮件跟踪记录集合
     */
    @Override
    public List<EmailTrackRecord> selectEmailTrackRecordByTaskId(Long taskId)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordByTaskId(taskId);
    }

    /**
     * 根据邮箱账号ID查询邮件跟踪记录列表
     * 
     * @param accountId 邮箱账号ID
     * @return 邮件跟踪记录集合
     */
    @Override
    public List<EmailTrackRecord> selectEmailTrackRecordByAccountId(Long accountId)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordByAccountId(accountId);
    }

    /**
     * 根据状态查询邮件跟踪记录列表
     * 
     * @param status 邮件状态
     * @return 邮件跟踪记录集合
     */
    @Override
    public List<EmailTrackRecord> selectEmailTrackRecordByStatus(String status)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordByStatus(status);
    }

    /**
     * 新增邮件跟踪记录
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 结果
     */
    @Override
    public int insertEmailTrackRecord(EmailTrackRecord emailTrackRecord)
    {
        emailTrackRecord.setCreateTime(new Date());
        emailTrackRecord.setDeleted("0");
        return emailTrackRecordMapper.insertEmailTrackRecord(emailTrackRecord);
    }

    /**
     * 修改邮件跟踪记录
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 结果
     */
    @Override
    public int updateEmailTrackRecord(EmailTrackRecord emailTrackRecord)
    {
        emailTrackRecord.setUpdateTime(new Date());
        return emailTrackRecordMapper.updateEmailTrackRecord(emailTrackRecord);
    }

    /**
     * 批量删除邮件跟踪记录
     * 
     * @param ids 需要删除的邮件跟踪记录主键
     * @return 结果
     */
    @Override
    public int deleteEmailTrackRecordByIds(Long[] ids)
    {
        return emailTrackRecordMapper.deleteEmailTrackRecordByIds(ids);
    }

    /**
     * 删除邮件跟踪记录信息
     * 
     * @param id 邮件跟踪记录主键
     * @return 结果
     */
    @Override
    public int deleteEmailTrackRecordById(Long id)
    {
        return emailTrackRecordMapper.deleteEmailTrackRecordById(id);
    }

    /**
     * 根据Message-ID删除邮件跟踪记录
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int deleteEmailTrackRecordByMessageId(String messageId)
    {
        return emailTrackRecordMapper.deleteEmailTrackRecordByMessageId(messageId);
    }

    /**
     * 根据任务ID删除邮件跟踪记录
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int deleteEmailTrackRecordByTaskId(Long taskId)
    {
        return emailTrackRecordMapper.deleteEmailTrackRecordByTaskId(taskId);
    }

    /**
     * 统计任务邮件状态
     * 
     * @param taskId 任务ID
     * @return 统计结果
     */
    @Override
    public List<EmailTrackRecord> selectEmailTrackRecordStatsByTaskId(Long taskId)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordStatsByTaskId(taskId);
    }

    /**
     * 统计邮箱账号邮件状态
     * 
     * @param accountId 邮箱账号ID
     * @return 统计结果
     */
    @Override
    public List<EmailTrackRecord> selectEmailTrackRecordStatsByAccountId(Long accountId)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordStatsByAccountId(accountId);
    }

    /**
     * 更新邮件状态
     * 
     * @param messageId 邮件Message-ID
     * @param status 新状态
     * @return 结果
     */
    @Override
    public int updateEmailStatus(String messageId, String status)
    {
        logger.info("开始更新邮件状态: MessageID={}, 新状态={}", messageId, status);
        
        EmailTrackRecord record = selectEmailTrackRecordByMessageId(messageId);
        if (record != null) {
            String oldStatus = record.getStatus();
            record.setStatus(status);
            record.setUpdateTime(new Date());
            
            logger.info("找到邮件跟踪记录: ID={}, 原状态={}, 新状态={}", record.getId(), oldStatus, status);
            
            // 根据状态设置相应的时间字段
            switch (status) {
                case "SEND_SUCCESS":
                    record.setSentTime(new Date());
                    break;
                case "DELIVERED":
                    record.setDeliveredTime(new Date());
                    break;
                case "OPENED":
                    // 如果送达时间为空，先设置送达时间（逻辑上邮件必须先送达才能被打开）
                    if (record.getDeliveredTime() == null) {
                        record.setDeliveredTime(new Date());
                        logger.info("邮件打开时自动设置送达时间: {}", messageId);
                    }
                    record.setOpenedTime(new Date());
                    break;
                case "REPLIED":
                    record.setRepliedTime(new Date());
                    logger.info("设置邮件回复时间: MessageID={}, 回复时间={}", messageId, record.getRepliedTime());
                    break;
                case "CLICKED":
                    // 如果送达时间为空，先设置送达时间（逻辑上邮件必须先送达才能被点击）
                    if (record.getDeliveredTime() == null) {
                        record.setDeliveredTime(new Date());
                        logger.info("邮件点击时自动设置送达时间: {}", messageId);
                    }
                    // 如果打开时间为空，先设置打开时间（逻辑上邮件必须先被打开才能被点击）
                    if (record.getOpenedTime() == null) {
                        record.setOpenedTime(new Date());
                        logger.info("邮件点击时自动设置打开时间: {}", messageId);
                    }
                    record.setClickedTime(new Date());
                    break;
            }
            
            int result = updateEmailTrackRecord(record);
            logger.info("邮件状态更新完成: MessageID={}, 状态={}, 影响行数={}", messageId, status, result);
            return result;
        } else {
            logger.warn("未找到邮件跟踪记录: MessageID={}", messageId);
        }
        return 0;
    }

    /**
     * 记录邮件打开事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailOpened(String messageId)
    {
        EmailTrackRecord record = selectEmailTrackRecordByMessageId(messageId);
        if (record != null) {
            // 如果送达时间为空，先设置送达时间（逻辑上邮件必须先送达才能被打开）
            if (record.getDeliveredTime() == null) {
                record.setDeliveredTime(new Date());
                record.setStatus("DELIVERED");
                logger.info("邮件打开时自动设置送达时间: {}", messageId);
            }
            
            // 设置打开时间
            record.setOpenedTime(new Date());
            record.setStatus("OPENED");
            record.setUpdateTime(new Date());
            
            return updateEmailTrackRecord(record);
        }
        return 0;
    }

    /**
     * 记录邮件点击事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailClicked(String messageId)
    {
        EmailTrackRecord record = selectEmailTrackRecordByMessageId(messageId);
        if (record != null) {
            // 如果送达时间为空，先设置送达时间（逻辑上邮件必须先送达才能被点击）
            if (record.getDeliveredTime() == null) {
                record.setDeliveredTime(new Date());
                logger.info("邮件点击时自动设置送达时间: {}", messageId);
            }
            
            // 如果打开时间为空，先设置打开时间（逻辑上邮件必须先被打开才能被点击）
            if (record.getOpenedTime() == null) {
                record.setOpenedTime(new Date());
                logger.info("邮件点击时自动设置打开时间: {}", messageId);
            }
            
            // 设置点击时间
            record.setClickedTime(new Date());
            record.setStatus("CLICKED");
            record.setUpdateTime(new Date());
            
            return updateEmailTrackRecord(record);
        }
        return 0;
    }

    /**
     * 记录邮件回复事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailReplied(String messageId)
    {
        return updateEmailStatus(messageId, "REPLIED");
    }

    /**
     * 通过模糊匹配记录邮件回复事件
     * 
     * @param originalMessageId 原始邮件Message-ID
     * @param replySubject 回复邮件主题
     * @return 结果
     */
    @Override
    public int recordEmailRepliedByFuzzyMatch(String originalMessageId, String replySubject)
    {
        logger.info("开始模糊匹配回复邮件: 原始Message-ID={}, 回复主题={}", originalMessageId, replySubject);
        
        try {
            // 从回复主题中提取原始主题（去掉"Re:"前缀）
            String originalSubject = extractOriginalSubjectFromReply(replySubject);
            if (originalSubject == null || originalSubject.trim().isEmpty()) {
                logger.warn("无法从回复主题中提取原始主题: {}", replySubject);
                return 0;
            }
            
            logger.info("提取的原始主题: {}", originalSubject);
            
            // 查找匹配的邮件记录
            List<EmailTrackRecord> candidates = emailTrackRecordMapper.selectEmailTrackRecordBySubjectFuzzy(originalSubject);
            if (candidates == null || candidates.isEmpty()) {
                logger.warn("未找到匹配的邮件记录: 主题={}", originalSubject);
                return 0;
            }
            
            logger.info("找到{}个候选邮件记录", candidates.size());
            
            // 选择最匹配的记录（优先选择未回复的）
            EmailTrackRecord bestMatch = null;
            for (EmailTrackRecord record : candidates) {
                if (record.getRepliedTime() == null) {
                    bestMatch = record;
                    break;
                }
            }
            
            // 如果没有未回复的，选择第一个
            if (bestMatch == null) {
                bestMatch = candidates.get(0);
            }
            
            logger.info("选择匹配记录: ID={}, Message-ID={}, 主题={}", 
                       bestMatch.getId(), bestMatch.getMessageId(), bestMatch.getSubject());
            
            // 更新回复状态
            return updateEmailStatus(bestMatch.getMessageId(), "REPLIED");
            
        } catch (Exception e) {
            logger.error("模糊匹配回复邮件失败: 原始Message-ID={}, 回复主题={}", originalMessageId, replySubject, e);
            return 0;
        }
    }
    
    /**
     * 从回复主题中提取原始主题
     * 
     * @param replySubject 回复主题
     * @return 原始主题
     */
    private String extractOriginalSubjectFromReply(String replySubject) {
        if (replySubject == null || replySubject.trim().isEmpty()) {
            return null;
        }
        
        String subject = replySubject.trim();
        
        // 去掉常见的回复前缀
        String[] replyPrefixes = {
            "Re:", "RE:", "re:", "回复:", "回复：", "Re：", "RE：", "re：",
            "Fwd:", "FWD:", "fwd:", "转发:", "转发：", "Fwd：", "FWD：", "fwd："
        };
        
        for (String prefix : replyPrefixes) {
            if (subject.startsWith(prefix)) {
                subject = subject.substring(prefix.length()).trim();
                break;
            }
        }
        
        return subject.isEmpty() ? null : subject;
    }

    /**
     * 记录邮件送达事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailDelivered(String messageId)
    {
        return updateEmailStatus(messageId, "DELIVERED");
    }

    /**
     * 记录邮件退信事件
     * 
     * @param messageId 邮件Message-ID
     * @param bounceReason 退信原因
     * @return 结果
     */
    @Override
    public int recordEmailBounced(String messageId, String bounceReason)
    {
        try {
            EmailTrackRecord record = selectEmailTrackRecordByMessageId(messageId);
            if (record != null) {
                record.setStatus("BOUNCED");
                record.setUpdateTime(new Date());
                // 可以在error_logs字段中记录退信原因
                if (bounceReason != null && !bounceReason.isEmpty()) {
                    record.setErrorLogs("退信原因: " + bounceReason);
                }
                return emailTrackRecordMapper.updateEmailTrackRecord(record);
            }
            return 0;
        } catch (Exception e) {
            logger.error("记录邮件退信失败: {}", messageId, e);
            return 0;
        }
    }

    @Override
    public long countTotalEmails()
    {
        return emailTrackRecordMapper.countTotalEmails();
    }

    @Override
    public long countTodaySentEmails(String date)
    {
        return emailTrackRecordMapper.countTodaySentEmails(date);
    }

    @Override
    public long countRepliedEmails()
    {
        return emailTrackRecordMapper.countRepliedEmails();
    }

    @Override
    public long countTodayOpenedEmails(String date)
    {
        return emailTrackRecordMapper.countTodayOpenedEmails(date);
    }

    @Override
    public long countTotalOpenedEmails()
    {
        return emailTrackRecordMapper.countTotalOpenedEmails();
    }

    @Override
    public Map<String, Long> getSentEmailsByDateRange(String startDate, String endDate)
    {
        List<Map<String, Object>> results = emailTrackRecordMapper.getSentEmailsByDateRange(startDate, endDate);
        Map<String, Long> resultMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            String date = (String) result.get("date");
            Long count = ((Number) result.get("count")).longValue();
            resultMap.put(date, count);
        }
        return resultMap;
    }

    @Override
    public Map<String, Long> getDeliveredEmailsByDateRange(String startDate, String endDate)
    {
        List<Map<String, Object>> results = emailTrackRecordMapper.getDeliveredEmailsByDateRange(startDate, endDate);
        Map<String, Long> resultMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            String date = (String) result.get("date");
            Long count = ((Number) result.get("count")).longValue();
            resultMap.put(date, count);
        }
        return resultMap;
    }

    @Override
    public long countTodayDeliveredEmails(String date)
    {
        return emailTrackRecordMapper.countTodayDeliveredEmails(date);
    }

    @Override
    public long countTodayClickedEmails(String date)
    {
        return emailTrackRecordMapper.countTodayClickedEmails(date);
    }

    @Override
    public List<EmailTrackRecord> getRecentEmails(int limit)
    {
        return emailTrackRecordMapper.getRecentEmails(limit);
    }

    @Override
    public List<EmailTrackRecord> getRecentReplies(int limit)
    {
        return emailTrackRecordMapper.getRecentReplies(limit);
    }

    @Override
    public List<Map<String, Object>> getDetailedStatistics(String startDate, String endDate, Long accountId)
    {
        return emailTrackRecordMapper.getDetailedStatistics(startDate, endDate, accountId);
    }
}
