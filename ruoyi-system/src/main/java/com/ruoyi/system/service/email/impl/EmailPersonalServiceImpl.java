package com.ruoyi.system.service.email.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailPersonalMapper;
import com.ruoyi.system.domain.email.EmailPersonal;
import com.ruoyi.system.service.email.IEmailPersonalService;

/**
 * 个人邮件Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
@Service
public class EmailPersonalServiceImpl implements IEmailPersonalService 
{
    @Autowired
    private EmailPersonalMapper emailPersonalMapper;

    /**
     * 查询个人邮件
     * 
     * @param emailId 个人邮件主键
     * @return 个人邮件
     */
    @Override
    public EmailPersonal selectEmailPersonalByEmailId(Long emailId)
    {
        return emailPersonalMapper.selectEmailPersonalByEmailId(emailId);
    }

    /**
     * 根据Message-ID查询个人邮件
     * 
     * @param messageId 邮件Message-ID
     * @return 个人邮件
     */
    @Override
    public EmailPersonal selectEmailPersonalByMessageId(String messageId)
    {
        return emailPersonalMapper.selectEmailPersonalByMessageId(messageId);
    }

    /**
     * 查询个人邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件
     */
    @Override
    public List<EmailPersonal> selectEmailPersonalList(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.selectEmailPersonalList(emailPersonal);
    }

    /**
     * 查询收件箱列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    @Override
    public List<EmailPersonal> selectInboxList(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.selectInboxList(emailPersonal);
    }

    /**
     * 查询发件箱列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    @Override
    public List<EmailPersonal> selectSentList(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.selectSentList(emailPersonal);
    }

    /**
     * 查询星标邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    @Override
    public List<EmailPersonal> selectStarredList(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.selectStarredList(emailPersonal);
    }

    /**
     * 查询已删除邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    @Override
    public List<EmailPersonal> selectDeletedList(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.selectDeletedList(emailPersonal);
    }

    /**
     * 新增个人邮件
     * 
     * @param emailPersonal 个人邮件
     * @return 结果
     */
    @Override
    public int insertEmailPersonal(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.insertEmailPersonal(emailPersonal);
    }

    /**
     * 修改个人邮件
     * 
     * @param emailPersonal 个人邮件
     * @return 结果
     */
    @Override
    public int updateEmailPersonal(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.updateEmailPersonal(emailPersonal);
    }

    /**
     * 批量删除个人邮件
     * 
     * @param emailIds 需要删除的个人邮件主键
     * @return 结果
     */
    @Override
    public int deleteEmailPersonalByEmailIds(Long[] emailIds)
    {
        return emailPersonalMapper.deleteEmailPersonalByEmailIds(emailIds);
    }

    /**
     * 删除个人邮件信息
     * 
     * @param emailId 个人邮件主键
     * @return 结果
     */
    @Override
    public int deleteEmailPersonalByEmailId(Long emailId)
    {
        return emailPersonalMapper.deleteEmailPersonalByEmailId(emailId);
    }

    /**
     * 标记邮件为已读
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    @Override
    public int markAsRead(Long emailId)
    {
        return emailPersonalMapper.markAsRead(emailId);
    }

    /**
     * 标记邮件为星标
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    @Override
    public int markAsStarred(Long emailId)
    {
        return emailPersonalMapper.markAsStarred(emailId);
    }

    /**
     * 标记邮件为重要
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    @Override
    public int markAsImportant(Long emailId)
    {
        return emailPersonalMapper.markAsImportant(emailId);
    }

    /**
     * 恢复已删除邮件
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    @Override
    public int restoreEmail(Long emailId)
    {
        return emailPersonalMapper.restoreEmail(emailId);
    }

    /**
     * 彻底删除邮件
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    @Override
    public int deletePermanently(Long emailId)
    {
        return emailPersonalMapper.deletePermanently(emailId);
    }

    /**
     * 获取未读邮件数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    @Override
    public int getUnreadCount(Long userId)
    {
        return emailPersonalMapper.getUnreadCount(userId);
    }

    /**
     * 获取收件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    @Override
    public int getInboxUnreadCount(Long userId)
    {
        return emailPersonalMapper.getInboxUnreadCount(userId);
    }

    /**
     * 获取发件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    @Override
    public int getSentUnreadCount(Long userId)
    {
        return emailPersonalMapper.getSentUnreadCount(userId);
    }

    /**
     * 获取星标邮件未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    @Override
    public int getStarredUnreadCount(Long userId)
    {
        return emailPersonalMapper.getStarredUnreadCount(userId);
    }

    /**
     * 获取已删除邮件未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    @Override
    public int getDeletedUnreadCount(Long userId)
    {
        return emailPersonalMapper.getDeletedUnreadCount(userId);
    }

    /**
     * 统计总邮件数
     * 
     * @return 总邮件数
     */
    @Override
    public long countTotalEmails()
    {
        return emailPersonalMapper.countTotalEmails();
    }

    /**
     * 统计今日发送邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日发送邮件数
     */
    @Override
    public long countTodaySentEmails(String date)
    {
        return emailPersonalMapper.countTodaySentEmails(date);
    }

    /**
     * 统计回复邮件数
     * 
     * @return 回复邮件数
     */
    @Override
    public long countRepliedEmails()
    {
        return emailPersonalMapper.countRepliedEmails();
    }

    /**
     * 统计今日接收邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日接收邮件数
     */
    @Override
    public long countTodayReceivedEmails(String date)
    {
        return emailPersonalMapper.countTodayReceivedEmails(date);
    }

    /**
     * 获取最近邮件记录
     * 
     * @param limit 限制数量
     * @return 最近邮件记录
     */
    @Override
    public List<EmailPersonal> getRecentEmails(int limit)
    {
        return emailPersonalMapper.getRecentEmails(limit);
    }

    /**
     * 获取最近回复记录
     * 
     * @param limit 限制数量
     * @return 最近回复记录
     */
    @Override
    public List<EmailPersonal> getRecentReplies(int limit)
    {
        return emailPersonalMapper.getRecentReplies(limit);
    }

    /**
     * 统计今日回复邮件数
     * 
     * @param date 日期字符串 (yyyy-MM-dd)
     * @return 今日回复邮件数
     */
    @Override
    public long countTodayRepliedEmails(String date)
    {
        return emailPersonalMapper.countTodayRepliedEmails(date);
    }

    /**
     * 统计总接收邮件数
     * 
     * @return 总接收邮件数
     */
    @Override
    public long countTotalReceivedEmails()
    {
        return emailPersonalMapper.countTotalReceivedEmails();
    }

    /**
     * 获取详细统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账号ID（可选）
     * @return 详细统计数据
     */
    @Override
    public List<Map<String, Object>> getDetailedStatistics(String startDate, String endDate, Long accountId)
    {
        return emailPersonalMapper.getDetailedStatistics(startDate, endDate, accountId);
    }
}
