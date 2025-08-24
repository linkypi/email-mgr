package com.ruoyi.system.service.email;

import java.util.List;
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
     * 记录邮件送达事件
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    public int recordEmailDelivered(String messageId);
}
