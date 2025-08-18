package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailSendTaskMapper;
import com.ruoyi.system.domain.email.EmailSendTask;
import com.ruoyi.system.service.email.IEmailSendTaskService;

/**
 * 批量发送任务Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailSendTaskServiceImpl implements IEmailSendTaskService 
{
    @Autowired
    private EmailSendTaskMapper emailSendTaskMapper;

    /**
     * 查询批量发送任务
     * 
     * @param taskId 批量发送任务主键
     * @return 批量发送任务
     */
    @Override
    public EmailSendTask selectEmailSendTaskByTaskId(Long taskId)
    {
        return emailSendTaskMapper.selectEmailSendTaskByTaskId(taskId);
    }

    /**
     * 查询批量发送任务列表
     * 
     * @param emailSendTask 批量发送任务
     * @return 批量发送任务
     */
    @Override
    public List<EmailSendTask> selectEmailSendTaskList(EmailSendTask emailSendTask)
    {
        return emailSendTaskMapper.selectEmailSendTaskList(emailSendTask);
    }

    /**
     * 新增批量发送任务
     * 
     * @param emailSendTask 批量发送任务
     * @return 结果
     */
    @Override
    public int insertEmailSendTask(EmailSendTask emailSendTask)
    {
        return emailSendTaskMapper.insertEmailSendTask(emailSendTask);
    }

    /**
     * 修改批量发送任务
     * 
     * @param emailSendTask 批量发送任务
     * @return 结果
     */
    @Override
    public int updateEmailSendTask(EmailSendTask emailSendTask)
    {
        return emailSendTaskMapper.updateEmailSendTask(emailSendTask);
    }

    /**
     * 批量删除批量发送任务
     * 
     * @param taskIds 需要删除的批量发送任务主键
     * @return 结果
     */
    @Override
    public int deleteEmailSendTaskByTaskIds(Long[] taskIds)
    {
        return emailSendTaskMapper.deleteEmailSendTaskByTaskIds(taskIds);
    }

    /**
     * 删除批量发送任务信息
     * 
     * @param taskId 批量发送任务主键
     * @return 结果
     */
    @Override
    public int deleteEmailSendTaskByTaskId(Long taskId)
    {
        return emailSendTaskMapper.deleteEmailSendTaskByTaskId(taskId);
    }

    /**
     * 启动发送任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int startSendTask(Long taskId)
    {
        EmailSendTask emailSendTask = new EmailSendTask();
        emailSendTask.setTaskId(taskId);
        emailSendTask.setStatus("1"); // 发送中
        return emailSendTaskMapper.updateEmailSendTask(emailSendTask);
    }

    /**
     * 暂停发送任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int pauseSendTask(Long taskId)
    {
        EmailSendTask emailSendTask = new EmailSendTask();
        emailSendTask.setTaskId(taskId);
        emailSendTask.setStatus("3"); // 已暂停
        return emailSendTaskMapper.updateEmailSendTask(emailSendTask);
    }

    /**
     * 取消发送任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int cancelSendTask(Long taskId)
    {
        EmailSendTask emailSendTask = new EmailSendTask();
        emailSendTask.setTaskId(taskId);
        emailSendTask.setStatus("4"); // 已取消
        return emailSendTaskMapper.updateEmailSendTask(emailSendTask);
    }

    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param sentCount 已发送数量
     * @param deliveredCount 送达数量
     * @param openedCount 打开数量
     * @param repliedCount 回复数量
     * @return 结果
     */
    @Override
    public int updateTaskProgress(Long taskId, Integer sentCount, Integer deliveredCount, Integer openedCount, Integer repliedCount)
    {
        EmailSendTask emailSendTask = new EmailSendTask();
        emailSendTask.setTaskId(taskId);
        emailSendTask.setSentCount(sentCount);
        emailSendTask.setDeliveredCount(deliveredCount);
        emailSendTask.setOpenedCount(openedCount);
        emailSendTask.setRepliedCount(repliedCount);
        
        // 如果已发送数量等于总数量，则标记为已完成
        EmailSendTask task = selectEmailSendTaskByTaskId(taskId);
        if (task != null && sentCount != null && task.getTotalCount() != null && sentCount >= task.getTotalCount()) {
            emailSendTask.setStatus("2"); // 已完成
        }
        
        return emailSendTaskMapper.updateEmailSendTask(emailSendTask);
    }
}
