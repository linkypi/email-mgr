package com.ruoyi.system.service.email.impl;

import java.util.Date;
import java.util.List;
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
        EmailTrackRecord record = selectEmailTrackRecordByMessageId(messageId);
        if (record != null) {
            record.setStatus(status);
            record.setUpdateTime(new Date());
            
            // 根据状态设置相应的时间字段
            switch (status) {
                case "SEND_SUCCESS":
                    record.setSentTime(new Date());
                    break;
                case "DELIVERED":
                    record.setDeliveredTime(new Date());
                    break;
                case "OPENED":
                    record.setOpenedTime(new Date());
                    break;
                case "REPLIED":
                    record.setRepliedTime(new Date());
                    break;
                case "CLICKED":
                    record.setClickedTime(new Date());
                    break;
            }
            
            return updateEmailTrackRecord(record);
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
        return updateEmailStatus(messageId, "OPENED");
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
        return updateEmailStatus(messageId, "CLICKED");
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
}
