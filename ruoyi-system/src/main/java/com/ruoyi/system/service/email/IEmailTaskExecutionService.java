package com.ruoyi.system.service.email;

import java.util.List;

import com.ruoyi.system.domain.email.EmailTaskExecution;

/**
 * 邮件任务执行服务接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailTaskExecutionService
{
    /**
     * 查询邮件任务执行
     * 
     * @param executionId 邮件任务执行主键
     * @return 邮件任务执行
     */
    public EmailTaskExecution selectEmailTaskExecutionByExecutionId(Long executionId);

    /**
     * 查询邮件任务执行列表
     * 
     * @param emailTaskExecution 邮件任务执行
     * @return 邮件任务执行集合
     */
    public List<EmailTaskExecution> selectEmailTaskExecutionList(EmailTaskExecution emailTaskExecution);

    /**
     * 新增邮件任务执行
     * 
     * @param emailTaskExecution 邮件任务执行
     * @return 结果
     */
    public int insertEmailTaskExecution(EmailTaskExecution emailTaskExecution);

    /**
     * 修改邮件任务执行
     * 
     * @param emailTaskExecution 邮件任务执行
     * @return 结果
     */
    public int updateEmailTaskExecution(EmailTaskExecution emailTaskExecution);

    /**
     * 批量删除邮件任务执行
     * 
     * @param executionIds 需要删除的邮件任务执行主键集合
     * @return 结果
     */
    public int deleteEmailTaskExecutionByExecutionIds(Long[] executionIds);

    /**
     * 删除邮件任务执行信息
     * 
     * @param executionId 邮件任务执行主键
     * @return 结果
     */
    public int deleteEmailTaskExecutionByExecutionId(Long executionId);

    /**
     * 根据任务ID查询最新的执行记录
     * 
     * @param taskId 任务ID
     * @return 邮件任务执行
     */
    public EmailTaskExecution selectLatestExecutionByTaskId(Long taskId);

    /**
     * 根据任务ID查询所有执行记录
     * 
     * @param taskId 任务ID
     * @return 邮件任务执行集合
     */
    public List<EmailTaskExecution> selectExecutionsByTaskId(Long taskId);
}
