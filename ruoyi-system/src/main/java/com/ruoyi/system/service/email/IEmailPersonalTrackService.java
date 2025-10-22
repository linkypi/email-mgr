package com.ruoyi.system.service.email;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.email.EmailTrackRecord;

/**
 * 个人邮件跟踪Service接口
 * 基于email_track_record表实现个人邮件管理功能
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
public interface IEmailPersonalTrackService 
{
    /**
     * 查询发件箱列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectSentList(EmailTrackRecord emailTrackRecord);

    /**
     * 查询收件箱列表（回复邮件）
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    public List<EmailTrackRecord> selectInboxList(EmailTrackRecord emailTrackRecord);

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
     * 根据ID查询邮件跟踪记录
     * 
     * @param id 邮件跟踪记录主键
     * @return 邮件跟踪记录
     */
    public EmailTrackRecord selectEmailTrackRecordById(Long id);

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
     * 彻底删除邮件
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    public int deletePermanently(Long[] ids);

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
     * 获取已删除邮件未读数量
     * 
     * @param userId 用户ID
     * @return 已删除未读数量
     */
    public int getDeletedUnreadCount(Long userId);

    /**
     * 获取星标邮件未读数量
     * 
     * @param userId 用户ID
     * @return 星标未读数量
     */
    public int getStarredUnreadCount(Long userId);

    /**
     * 获取邮件统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    public Map<String, Object> getEmailStatistics(Long userId);

    /**
     * 发送邮件
     * 
     * @param emailData 邮件信息
     * @return 结果
     */
    public int sendEmail(EmailTrackRecord emailData);

    /**
     * 发送回复邮件
     * 
     * @param replyEmail 回复邮件信息
     * @return 结果
     */
    public int sendReply(EmailTrackRecord replyEmail);

    /**
     * 获取邮件详情（用于回复和转发）
     * 
     * @param emailId 邮件ID
     * @return 邮件详情
     */
    public EmailTrackRecord getEmailDetail(Long emailId);

    /**
     * 发送转发邮件
     * 
     * @param forwardEmail 转发邮件信息
     * @return 结果
     */
    public int sendForward(EmailTrackRecord forwardEmail);

    /**
     * 修改邮件跟踪记录
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 结果
     */
    public int updateEmailTrackRecord(EmailTrackRecord emailTrackRecord);
}

