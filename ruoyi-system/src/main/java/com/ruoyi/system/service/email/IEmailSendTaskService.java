package com.ruoyi.system.service.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailSendTask;

/**
 * 批量发送任务Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailSendTaskService 
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
     * 批量删除批量发送任务
     * 
     * @param taskIds 需要删除的批量发送任务主键集合
     * @return 结果
     */
    public int deleteEmailSendTaskByTaskIds(Long[] taskIds);

    /**
     * 删除批量发送任务信息
     * 
     * @param taskId 批量发送任务主键
     * @return 结果
     */
    public int deleteEmailSendTaskByTaskId(Long taskId);

    /**
     * 启动发送任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    public int startSendTask(Long taskId);

    /**
     * 暂停发送任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    public int pauseSendTask(Long taskId);

    /**
     * 取消发送任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    public int cancelSendTask(Long taskId);

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
    public int updateTaskProgress(Long taskId, Integer sentCount, Integer deliveredCount, Integer openedCount, Integer repliedCount);
}
