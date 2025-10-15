package com.ruoyi.system.service.email;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.email.EmailTrackRecord;

/**
 * 邮件跟踪记录Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailTrackRecordService 
{
    /**
     * 查询邮件跟踪记录
     * 
     * @param id 邮件跟踪记录主键
     * @return 邮件跟踪记录
     */
    public EmailTrackRecord selectEmailTrackRecordById(Long id);

    /**
     * 根据Message-ID查询邮件跟踪记录
     * 
     * @param messageId 邮件Message-ID
     * @return 邮件跟踪记录
     */
    public EmailTrackRecord selectEmailTrackRecordByMessageId(String messageId);

    /**
     * 查询邮件跟踪记录列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectEmailTrackRecordList(EmailTrackRecord emailTrackRecord);

    /**
     * 根据任务ID查询邮件跟踪记录列表
     * 
     * @param taskId 任务ID
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectEmailTrackRecordByTaskId(Long taskId);

    /**
     * 根据邮箱账号ID查询邮件跟踪记录列表
     * 
     * @param accountId 邮箱账号ID
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectEmailTrackRecordByAccountId(Long accountId);

    /**
     * 根据状态查询邮件跟踪记录列表
     * 
     * @param status 邮件状态
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectEmailTrackRecordByStatus(String status);

    /**
     * 新增邮件跟踪记录
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 结果
     */
    public int insertEmailTrackRecord(EmailTrackRecord emailTrackRecord);

    /**
     * 修改邮件跟踪记录
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 结果
     */
    public int updateEmailTrackRecord(EmailTrackRecord emailTrackRecord);

    /**
     * 批量删除邮件跟踪记录
     * 
     * @param ids 需要删除的邮件跟踪记录主键集合
     * @return 结果
     */
    public int deleteEmailTrackRecordByIds(Long[] ids);

    /**
     * 删除邮件跟踪记录信息
     * 
     * @param id 邮件跟踪记录主键
     * @return 结果
     */
    public int deleteEmailTrackRecordById(Long id);

    /**
     * 根据Message-ID删除邮件跟踪记录
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    public int deleteEmailTrackRecordByMessageId(String messageId);

    /**
     * 根据任务ID删除邮件跟踪记录
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    public int deleteEmailTrackRecordByTaskId(Long taskId);

    /**
     * 统计任务邮件状态
     * 
     * @param taskId 任务ID
     * @return 统计结果
     */
    public List<EmailTrackRecord> selectEmailTrackRecordStatsByTaskId(Long taskId);

    /**
     * 统计邮箱账号邮件状态
     * 
     * @param accountId 邮箱账号ID
     * @return 统计结果
     */
    public List<EmailTrackRecord> selectEmailTrackRecordStatsByAccountId(Long accountId);

    /**
     * 更新邮件状态
     * 
     * @param messageId 邮件Message-ID
     * @param status 新状态
     * @return 结果
     */
    public int updateEmailStatus(String messageId, String status);

    /**
     * 记录邮件打开事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    public int recordEmailOpened(String messageId);

    /**
     * 记录邮件点击事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    public int recordEmailClicked(String messageId);

    /**
     * 记录邮件回复事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    public int recordEmailReplied(String messageId);

    /**
     * 通过模糊匹配记录邮件回复事件
     * 
     * @param originalMessageId 原始邮件Message-ID
     * @param replySubject 回复邮件主题
     * @return 结果
     */
    public int recordEmailRepliedByFuzzyMatch(String originalMessageId, String replySubject);

    /**
     * 记录邮件送达事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    public int recordEmailDelivered(String messageId);

    /**
     * 记录邮件退信事件
     * 
     * @param messageId 邮件Message-ID
     * @param bounceReason 退信原因
     * @return 结果
     */
    public int recordEmailBounced(String messageId, String bounceReason);

    /**
     * 统计总邮件数
     * 
     * @return 总邮件数
     */
    public long countTotalEmails();

    /**
     * 统计今日发送邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日发送邮件数
     */
    public long countTodaySentEmails(String date);

    /**
     * 统计回复邮件数
     * 
     * @return 回复邮件数
     */
    public long countRepliedEmails();

    /**
     * 统计今日打开邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日打开邮件数
     */
    public long countTodayOpenedEmails(String date);

    /**
     * 统计总打开邮件数
     * 
     * @return 总打开邮件数
     */
    public long countTotalOpenedEmails();

    /**
     * 批量查询指定日期范围内的发送邮件数
     * 
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate 结束日期 (yyyy-MM-dd)
     * @return 日期到数量的映射
     */
    public Map<String, Long> getSentEmailsByDateRange(String startDate, String endDate);

    /**
     * 批量查询指定日期范围内的送达邮件数
     * 
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate 结束日期 (yyyy-MM-dd)
     * @return 日期到数量的映射
     */
    public Map<String, Long> getDeliveredEmailsByDateRange(String startDate, String endDate);

    /**
     * 统计今日送达邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日送达邮件数
     */
    public long countTodayDeliveredEmails(String date);

    /**
     * 统计今日点击邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日点击邮件数
     */
    public long countTodayClickedEmails(String date);

    /**
     * 获取最近邮件记录
     * 
     * @param limit 限制数量
     * @return 最近邮件记录
     */
    public List<EmailTrackRecord> getRecentEmails(int limit);

    /**
     * 获取最近回复记录
     * 
     * @param limit 限制数量
     * @return 最近回复记录
     */
    public List<EmailTrackRecord> getRecentReplies(int limit);

    /**
     * 获取详细统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账号ID（可选）
     * @return 详细统计数据
     */
    public List<Map<String, Object>> getDetailedStatistics(String startDate, String endDate, Long accountId);
}

