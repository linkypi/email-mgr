package com.ruoyi.system.domain.email;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 邮件任务执行对象 email_task_execution
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailTaskExecution extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 执行ID */
    private Long executionId;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 执行状态(0未开始 1执行中 2已完成 3执行失败 4执行中断) */
    @Excel(name = "执行状态", readConverterExp = "0=未开始,1=执行中,2=已完成,3=执行失败,4=执行中断")
    private String executionStatus;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 执行人 */
    @Excel(name = "执行人")
    private String executionUser;

    /** 执行IP */
    @Excel(name = "执行IP")
    private String executionIp;

    /** 总发送数量 */
    @Excel(name = "总发送数量")
    private Integer totalCount;

    /** 已发送数量 */
    @Excel(name = "已发送数量")
    private Integer sentCount;

    /** 成功数量 */
    @Excel(name = "成功数量")
    private Integer successCount;

    /** 失败数量 */
    @Excel(name = "失败数量")
    private Integer failedCount;

    /** 错误信息 */
    private String errorMessage;

    /** 执行日志 */
    private String executionLog;

    /** 任务名称（关联查询） */
    private String taskName;

    /** 任务状态（关联查询） */
    private String taskStatus;

    public void setExecutionId(Long executionId) 
    {
        this.executionId = executionId;
    }

    public Long getExecutionId() 
    {
        return executionId;
    }
    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }
    public void setExecutionStatus(String executionStatus) 
    {
        this.executionStatus = executionStatus;
    }

    public String getExecutionStatus() 
    {
        return executionStatus;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setExecutionUser(String executionUser) 
    {
        this.executionUser = executionUser;
    }

    public String getExecutionUser() 
    {
        return executionUser;
    }
    public void setExecutionIp(String executionIp) 
    {
        this.executionIp = executionIp;
    }

    public String getExecutionIp() 
    {
        return executionIp;
    }
    public void setTotalCount(Integer totalCount) 
    {
        this.totalCount = totalCount;
    }

    public Integer getTotalCount() 
    {
        return totalCount;
    }
    public void setSentCount(Integer sentCount) 
    {
        this.sentCount = sentCount;
    }

    public Integer getSentCount() 
    {
        return sentCount;
    }
    public void setSuccessCount(Integer successCount) 
    {
        this.successCount = successCount;
    }

    public Integer getSuccessCount() 
    {
        return successCount;
    }
    public void setFailedCount(Integer failedCount) 
    {
        this.failedCount = failedCount;
    }

    public Integer getFailedCount() 
    {
        return failedCount;
    }
    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }
    public void setExecutionLog(String executionLog) 
    {
        this.executionLog = executionLog;
    }

    public String getExecutionLog() 
    {
        return executionLog;
    }

    public String getTaskName() 
    {
        return taskName;
    }

    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskStatus() 
    {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) 
    {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("executionId", getExecutionId())
            .append("taskId", getTaskId())
            .append("executionStatus", getExecutionStatus())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("executionUser", getExecutionUser())
            .append("executionIp", getExecutionIp())
            .append("totalCount", getTotalCount())
            .append("sentCount", getSentCount())
            .append("successCount", getSuccessCount())
            .append("failedCount", getFailedCount())
            .append("errorMessage", getErrorMessage())
            .append("executionLog", getExecutionLog())
            .append("taskName", getTaskName())
            .append("taskStatus", getTaskStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

