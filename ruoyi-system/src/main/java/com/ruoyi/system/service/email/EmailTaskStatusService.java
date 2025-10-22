package com.ruoyi.system.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 邮件任务状态管理服务
 * 统一管理邮件发送任务的状态定义和转换
 * 
 * @author ruoyi
 * @date 2024-01-25
 */
@Service
public class EmailTaskStatusService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailTaskStatusService.class);
    
    // 任务状态常量
    public static final String STATUS_PENDING = "0";      // 待执行
    public static final String STATUS_RUNNING = "1";      // 执行中
    public static final String STATUS_COMPLETED = "2";    // 已完成
    public static final String STATUS_PAUSED = "3";       // 暂停
    public static final String STATUS_FAILED = "4";       // 失败
    public static final String STATUS_DELAYED = "5";      // 等待中（延迟执行）
    
    // 状态描述
    public static final String STATUS_PENDING_DESC = "待执行";
    public static final String STATUS_RUNNING_DESC = "执行中";
    public static final String STATUS_COMPLETED_DESC = "已完成";
    public static final String STATUS_PAUSED_DESC = "暂停";
    public static final String STATUS_FAILED_DESC = "失败";
    public static final String STATUS_DELAYED_DESC = "等待中（延迟执行）";
    
    /**
     * 获取状态描述
     * 
     * @param status 状态码
     * @return 状态描述
     */
    public String getStatusDescription(String status) {
        if (status == null) {
            return "未知状态";
        }
        
        switch (status) {
            case STATUS_PENDING:
                return STATUS_PENDING_DESC;
            case STATUS_RUNNING:
                return STATUS_RUNNING_DESC;
            case STATUS_COMPLETED:
                return STATUS_COMPLETED_DESC;
            case STATUS_PAUSED:
                return STATUS_PAUSED_DESC;
            case STATUS_FAILED:
                return STATUS_FAILED_DESC;
            case STATUS_DELAYED:
                return STATUS_DELAYED_DESC;
            default:
                return "未知状态";
        }
    }
    
    /**
     * 检查状态是否有效
     * 
     * @param status 状态码
     * @return 是否有效
     */
    public boolean isValidStatus(String status) {
        if (status == null) {
            return false;
        }
        
        return STATUS_PENDING.equals(status) ||
               STATUS_RUNNING.equals(status) ||
               STATUS_COMPLETED.equals(status) ||
               STATUS_PAUSED.equals(status) ||
               STATUS_FAILED.equals(status) ||
               STATUS_DELAYED.equals(status);
    }
    
    /**
     * 检查是否可以启动任务
     * 
     * @param currentStatus 当前状态
     * @return 是否可以启动
     */
    public boolean canStartTask(String currentStatus) {
        return STATUS_PENDING.equals(currentStatus) || 
               STATUS_PAUSED.equals(currentStatus) ||
               STATUS_DELAYED.equals(currentStatus);
    }
    
    /**
     * 检查是否可以暂停任务
     * 
     * @param currentStatus 当前状态
     * @return 是否可以暂停
     */
    public boolean canPauseTask(String currentStatus) {
        return STATUS_RUNNING.equals(currentStatus);
    }
    
    /**
     * 检查是否可以取消任务
     * 
     * @param currentStatus 当前状态
     * @return 是否可以取消
     */
    public boolean canCancelTask(String currentStatus) {
        return STATUS_PENDING.equals(currentStatus) || 
               STATUS_RUNNING.equals(currentStatus) ||
               STATUS_PAUSED.equals(currentStatus) ||
               STATUS_DELAYED.equals(currentStatus);
    }
    
    /**
     * 检查任务是否已完成（包括成功和失败）
     * 
     * @param status 状态码
     * @return 是否已完成
     */
    public boolean isTaskFinished(String status) {
        return STATUS_COMPLETED.equals(status) || STATUS_FAILED.equals(status);
    }
    
    /**
     * 检查任务是否正在执行
     * 
     * @param status 状态码
     * @return 是否正在执行
     */
    public boolean isTaskRunning(String status) {
        return STATUS_RUNNING.equals(status);
    }
    
    /**
     * 检查任务是否等待执行
     * 
     * @param status 状态码
     * @return 是否等待执行
     */
    public boolean isTaskWaiting(String status) {
        return STATUS_PENDING.equals(status) || STATUS_DELAYED.equals(status);
    }
    
    /**
     * 记录状态变更日志
     * 
     * @param taskId 任务ID
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     * @param reason 变更原因
     */
    public void logStatusChange(Long taskId, String oldStatus, String newStatus, String reason) {
        logger.info("任务状态变更: taskId={}, {} -> {}, 原因: {}", 
            taskId, getStatusDescription(oldStatus), getStatusDescription(newStatus), reason);
    }
}

