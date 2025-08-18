package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailPersonal;

/**
 * 个人邮件Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
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
     * 查询个人邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    public List<EmailPersonal> selectEmailPersonalList(EmailPersonal emailPersonal);

    /**
     * 查询收件箱邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件集合
     */
    public List<EmailPersonal> selectInboxList(EmailPersonal emailPersonal);

    /**
     * 查询发件箱邮件列表
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
     * 获取未读邮件数量
     * 
     * @return 未读邮件数量
     */
    public int getUnreadCount();
}
