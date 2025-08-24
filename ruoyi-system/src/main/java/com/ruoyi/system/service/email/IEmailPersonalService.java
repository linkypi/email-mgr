package com.ruoyi.system.service.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailPersonal;

/**
 * 个人邮件Service接口
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
public interface IEmailPersonalService 
{
    /**
     * 查询个人邮件
     * 
     * @param emailId 个人邮件主键
     * @return 个人邮件
     */
    public EmailPersonal selectEmailPersonalByEmailId(Long emailId);

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
     * 批量删除个人邮件
     * 
     * @param emailIds 需要删除的个人邮件主键集合
     * @return 结果
     */
    public int deleteEmailPersonalByEmailIds(Long[] emailIds);

    /**
     * 删除个人邮件信息
     * 
     * @param emailId 个人邮件主键
     * @return 结果
     */
    public int deleteEmailPersonalByEmailId(Long emailId);

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
}
