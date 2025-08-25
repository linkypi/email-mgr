package com.ruoyi.system.service.email.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.domain.email.EmailTaskExecution;
import com.ruoyi.system.mapper.email.EmailTaskExecutionMapper;
import com.ruoyi.system.service.email.IEmailTaskExecutionService;

/**
 * 邮件任务执行Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailTaskExecutionServiceImpl implements IEmailTaskExecutionService
{
    @Autowired
    private EmailTaskExecutionMapper emailTaskExecutionMapper;

    /**
     * 查询邮件任务执行
     * 
     * @param executionId 邮件任务执行主键
     * @return 邮件任务执行
     */
    @Override
    public EmailTaskExecution selectEmailTaskExecutionByExecutionId(Long executionId)
    {
        return emailTaskExecutionMapper.selectEmailTaskExecutionByExecutionId(executionId);
    }

    /**
     * 查询邮件任务执行列表
     * 
     * @param emailTaskExecution 邮件任务执行
     * @return 邮件任务执行
     */
    @Override
    public List<EmailTaskExecution> selectEmailTaskExecutionList(EmailTaskExecution emailTaskExecution)
    {
        return emailTaskExecutionMapper.selectEmailTaskExecutionList(emailTaskExecution);
    }

    /**
     * 新增邮件任务执行
     * 
     * @param emailTaskExecution 邮件任务执行
     * @return 结果
     */
    @Override
    public int insertEmailTaskExecution(EmailTaskExecution emailTaskExecution)
    {
        return emailTaskExecutionMapper.insertEmailTaskExecution(emailTaskExecution);
    }

    /**
     * 修改邮件任务执行
     * 
     * @param emailTaskExecution 邮件任务执行
     * @return 结果
     */
    @Override
    public int updateEmailTaskExecution(EmailTaskExecution emailTaskExecution)
    {
        return emailTaskExecutionMapper.updateEmailTaskExecution(emailTaskExecution);
    }

    /**
     * 批量删除邮件任务执行
     * 
     * @param executionIds 需要删除的邮件任务执行主键
     * @return 结果
     */
    @Override
    public int deleteEmailTaskExecutionByExecutionIds(Long[] executionIds)
    {
        return emailTaskExecutionMapper.deleteEmailTaskExecutionByExecutionIds(executionIds);
    }

    /**
     * 删除邮件任务执行信息
     * 
     * @param executionId 邮件任务执行主键
     * @return 结果
     */
    @Override
    public int deleteEmailTaskExecutionByExecutionId(Long executionId)
    {
        return emailTaskExecutionMapper.deleteEmailTaskExecutionByExecutionId(executionId);
    }

    /**
     * 根据任务ID查询最新的执行记录
     * 
     * @param taskId 任务ID
     * @return 邮件任务执行
     */
    @Override
    public EmailTaskExecution selectLatestExecutionByTaskId(Long taskId)
    {
        return emailTaskExecutionMapper.selectLatestExecutionByTaskId(taskId);
    }

    /**
     * 根据任务ID查询所有执行记录
     * 
     * @param taskId 任务ID
     * @return 邮件任务执行集合
     */
    @Override
    public List<EmailTaskExecution> selectExecutionsByTaskId(Long taskId)
    {
        return emailTaskExecutionMapper.selectExecutionsByTaskId(taskId);
    }
}

