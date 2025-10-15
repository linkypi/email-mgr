package com.ruoyi.system.mapper.email;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.email.EmailPersonal;

/**
 * 个人邮件Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
public interface EmailPersonalMapper 
{
    /**
     * 查询个人邮件
     * 
     * @param emailId 个人邮件主键
     * @return 个人邮件
     */
    public EmailPersonal selectEmailPersonalByEmailId(Long emailId);

    /**
     * 根据Message-ID查询个人邮件
     * 
     * @param messageId 邮件Message-ID
     * @return 个人邮件
     */
    public EmailPersonal selectEmailPersonalByMessageId(String messageId);

    /**
     * 查询个人邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    public List<EmailPersonal> selectEmailPersonalList(EmailPersonal emailPersonal);

    /**
     * 查询收件箱列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    public List<EmailPersonal> selectInboxList(EmailPersonal emailPersonal);

    /**
     * 查询发件箱列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    public List<EmailPersonal> selectSentList(EmailPersonal emailPersonal);

    /**
     * 查询星标邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    public List<EmailPersonal> selectStarredList(EmailPersonal emailPersonal);

    /**
     * 查询已删除邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    public List<EmailPersonal> selectDeletedList(EmailPersonal emailPersonal);

    /**
     * 新增个人邮件
     * 
     * @param emailPersonal 个人邮件
     * @return 结果
     */
    public int insertEmailPersonal(EmailPersonal emailPersonal);

    /**
     * 修改个人邮件
     * 
     * @param emailPersonal 个人邮件
     * @return 结果
     */
    public int updateEmailPersonal(EmailPersonal emailPersonal);

    /**
     * 删除个人邮件
     * 
     * @param emailId 个人邮件主键
     * @return 结果
     */
    public int deleteEmailPersonalByEmailId(Long emailId);

    /**
     * 批量删除个人邮件
     * 
     * @param emailIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailPersonalByEmailIds(Long[] emailIds);

    /**
     * 标记邮件为已读
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    public int markAsRead(Long emailId);

    /**
     * 标记邮件为星标
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    public int markAsStarred(Long emailId);

    /**
     * 标记邮件为重要
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    public int markAsImportant(Long emailId);

    /**
     * 恢复已删除邮件
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    public int restoreEmail(Long emailId);

    /**
     * 彻底删除邮件
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    public int deletePermanently(Long emailId);

    /**
     * 获取未读邮件数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    public int getUnreadCount(Long userId);

    /**
     * 获取收件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    public int getInboxUnreadCount(Long userId);

    /**
     * 获取发件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    public int getSentUnreadCount(Long userId);

    /**
     * 获取星标邮件未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    public int getStarredUnreadCount(Long userId);

    /**
     * 获取已删除邮件未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    public int getDeletedUnreadCount(Long userId);

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
    public long countTodaySentEmails(@Param("date") String date);

    /**
     * 统计回复邮件数
     * 
     * @return 回复邮件数
     */
    public long countRepliedEmails();

    /**
     * 统计今日接收邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日接收邮件数
     */
    public long countTodayReceivedEmails(@Param("date") String date);

    /**
     * 获取最近邮件记录
     * 
     * @param limit 限制数量
     * @return 最近邮件记录
     */
    public List<EmailPersonal> getRecentEmails(@Param("limit") int limit);

    /**
     * 获取最近回复记录
     * 
     * @param limit 限制数量
     * @return 最近回复记录
     */
    public List<EmailPersonal> getRecentReplies(@Param("limit") int limit);

    /**
     * 统计今日回复邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日回复邮件数
     */
    public long countTodayRepliedEmails(@Param("date") String date);

    /**
     * 统计总接收邮件数
     * 
     * @return 总接收邮件数
     */
    public long countTotalReceivedEmails();

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
