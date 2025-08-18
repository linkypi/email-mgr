package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailSendTask;

/**
 * 批量发送任务Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailSendTaskMapper 
{
    /**
     * 查询批量发送任务
     * 
     * @param taskId 批量发送任务主键
     * @return 批量发送任务
     */
    public EmailSendTask selectEmailSendTaskByTaskId(Long taskId);

    /**
     * 查询批量发送任务列表
     * 
     * @param emailSendTask 批量发送任务
     * @return 批量发送任务集合
     */
    public List<EmailSendTask> selectEmailSendTaskList(EmailSendTask emailSendTask);

    /**
     * 新增批量发送任务
     * 
     * @param emailSendTask 批量发送任务
     * @return 结果
     */
    public int insertEmailSendTask(EmailSendTask emailSendTask);

    /**
     * 修改批量发送任务
     * 
     * @param emailSendTask 批量发送任务
     * @return 结果
     */
    public int updateEmailSendTask(EmailSendTask emailSendTask);

    /**
     * 删除批量发送任务
     * 
     * @param taskId 批量发送任务主键
     * @return 结果
     */
    public int deleteEmailSendTaskByTaskId(Long taskId);

    /**
     * 批量删除批量发送任务
     * 
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailSendTaskByTaskIds(Long[] taskIds);

    /**
     * 更新任务进度
     * 
     * @param emailSendTask 批量发送任务
     * @return 结果
     */
    public int updateTaskProgress(EmailSendTask emailSendTask);

    /**
     * 获取待发送的任务列表
     * 
     * @return 任务列表
     */
    public List<EmailSendTask> selectPendingTasks();
}
