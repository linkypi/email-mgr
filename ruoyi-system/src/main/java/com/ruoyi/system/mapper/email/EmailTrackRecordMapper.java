package com.ruoyi.system.mapper.email;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.email.EmailTrackRecord;

/**
 * 邮件跟踪记录Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailTrackRecordMapper 
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
     * 根据主题模糊查询邮件跟踪记录
     * 
     * @param subject 邮件主题
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectEmailTrackRecordBySubjectFuzzy(String subject);

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
     * 删除邮件跟踪记录
     * 
     * @param id 邮件跟踪记录主键
     * @return 结果
     */
    public int deleteEmailTrackRecordById(Long id);

    /**
     * 批量删除邮件跟踪记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailTrackRecordByIds(Long[] ids);

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
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期到数量的映射
     */
    public List<Map<String, Object>> getSentEmailsByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 批量查询指定日期范围内的送达邮件数
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期到数量的映射
     */
    public List<Map<String, Object>> getDeliveredEmailsByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

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

    // ==================== 个人邮件管理相关方法 ====================

    /**
     * 查询个人邮件跟踪记录列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectPersonalTrackList(EmailTrackRecord emailTrackRecord);

    /**
     * 查询星标邮件列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectStarredList(EmailTrackRecord emailTrackRecord);

    /**
     * 查询已删除邮件列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectDeletedList(EmailTrackRecord emailTrackRecord);

    /**
     * 标记邮件为星标
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int markAsStarred(Long[] ids);

    /**
     * 取消星标
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int unmarkAsStarred(Long[] ids);

    /**
     * 标记邮件为已读
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int markAsRead(Long[] ids);

    /**
     * 标记邮件为未读
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int markAsUnread(Long[] ids);

    /**
     * 标记邮件为重要
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int markAsImportant(Long[] ids);

    /**
     * 取消重要标记
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int unmarkAsImportant(Long[] ids);

    /**
     * 移动到已删除
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int moveToDeleted(Long[] ids);

    /**
     * 从已删除恢复
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int restoreFromDeleted(Long[] ids);

    /**
     * 获取发件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    public int getSentUnreadCount(Long userId);

    /**
     * 获取发件箱总数量
     * 
     * @param userId 用户ID
     * @return 总数量
     */
    public int getSentTotalCount(Long userId);

    /**
     * 获取收件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    public int getInboxUnreadCount(Long userId);

    /**
     * 获取星标邮件数量
     * 
     * @param userId 用户ID
     * @return 星标数量
     */
    public int getStarredCount(Long userId);

    /**
     * 获取已删除邮件数量
     * 
     * @param userId 用户ID
     * @return 已删除数量
     */
    public int getDeletedCount(Long userId);

    /**
     * 获取个人邮件统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    public Map<String, Object> getPersonalEmailStatistics(Long userId);

    /**
     * 获取详细统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账号ID（可选）
     * @return 详细统计数据
     */
    public List<Map<String, Object>> getDetailedStatistics(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("accountId") Long accountId);
}

