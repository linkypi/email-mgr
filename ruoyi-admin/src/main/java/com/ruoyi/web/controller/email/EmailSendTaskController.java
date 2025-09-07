package com.ruoyi.web.controller.email;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailSendTask;
import com.ruoyi.system.domain.email.EmailTemplate;
import com.ruoyi.system.domain.email.EmailTaskExecution;
import com.ruoyi.system.service.email.IEmailSendTaskService;
import com.ruoyi.system.service.email.IEmailTemplateService;
import com.ruoyi.system.service.email.IEmailTaskExecutionService;
import com.ruoyi.system.service.email.EmailSendService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.email.EmailSchedulerService;

import java.util.HashMap;
import java.util.Map;

import static com.ruoyi.common.utils.ip.IpUtils.getIpAddr;

/**
 * 邮件发送任务Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/task")
public class EmailSendTaskController extends BaseController
{
    @Autowired
    private IEmailSendTaskService emailSendTaskService;

    @Autowired
    private EmailSendService emailSendService;

    @Autowired
    private EmailSchedulerService emailSchedulerService;

    @Autowired
    private IEmailTemplateService emailTemplateService;

    @Autowired
    private IEmailTaskExecutionService emailTaskExecutionService;

    /**
     * 查询邮件发送任务列表
     */
    @PreAuthorize("@ss.hasPermi('email:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailSendTask emailSendTask)
    {
        startPage();
        List<EmailSendTask> list = emailSendTaskService.selectEmailSendTaskList(emailSendTask);
        return getDataTable(list);
    }

    /**
     * 导出邮件发送任务列表
     */
    @PreAuthorize("@ss.hasPermi('email:task:export')")
    @Log(title = "邮件发送任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailSendTask emailSendTask)
    {
        List<EmailSendTask> list = emailSendTaskService.selectEmailSendTaskList(emailSendTask);
        ExcelUtil<EmailSendTask> util = new ExcelUtil<EmailSendTask>(EmailSendTask.class);
        util.exportExcel(response, list, "邮件发送任务数据");
    }

    /**
     * 获取邮件发送任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return success(emailSendTaskService.selectEmailSendTaskByTaskId(taskId));
    }

    /**
     * 新增邮件发送任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:add')")
    @Log(title = "邮件发送任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailSendTask emailSendTask)
    {
        return toAjax(emailSendTaskService.insertEmailSendTask(emailSendTask));
    }

    /**
     * 修改邮件发送任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:edit')")
    @Log(title = "邮件发送任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailSendTask emailSendTask)
    {
        return toAjax(emailSendTaskService.updateEmailSendTask(emailSendTask));
    }

    /**
     * 删除邮件发送任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:remove')")
    @Log(title = "邮件发送任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(emailSendTaskService.deleteEmailSendTaskByTaskIds(taskIds));
    }

    /**
     * 启动发送任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:start')")
    @Log(title = "启动发送任务", businessType = BusinessType.UPDATE)
    @PostMapping("/start/{taskId}")
    public AjaxResult startTask(@PathVariable("taskId") Long taskId)
    {
        try {
            emailSendService.startSendTask(taskId);
            return success("任务已启动");
        } catch (Exception e) {
            return error("任务启动失败：" + e.getMessage());
        }
    }

    /**
     * 暂停发送任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:pause')")
    @Log(title = "暂停发送任务", businessType = BusinessType.UPDATE)
    @PostMapping("/pause/{taskId}")
    public AjaxResult pauseTask(@PathVariable("taskId") Long taskId)
    {
        try {
            emailSendService.pauseSendTask(taskId);
            return success("任务已暂停");
        } catch (Exception e) {
            return error("任务暂停失败：" + e.getMessage());
        }
    }

    /**
     * 创建并启动邮件发送任务（用于批量发送页面）
     */
    @PreAuthorize("@ss.hasPermi('email:batch:send:create')")
    @Log(title = "创建邮件发送任务", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult createSendTask(@RequestBody EmailSendTask emailSendTask)
    {
        try {
            // 防重复提交检查：检查是否存在相同任务名称的未完成任务
            EmailSendTask existingTask = emailSendTaskService.selectEmailSendTaskByTaskName(emailSendTask.getTaskName());
            if (existingTask != null && !"completed".equals(existingTask.getStatus()) && !"failed".equals(existingTask.getStatus())) {
                return error("任务名称已存在，请使用不同的任务名称");
            }
            
            // 设置创建者信息
            emailSendTask.setCreateBy(getUsername());
            emailSendTask.setCreateTime(new java.util.Date());
            
            // 如果使用模板发送，需要从模板获取subject和content
            if (emailSendTask.getTemplateId() != null) {
                EmailTemplate template = emailTemplateService.selectEmailTemplateByTemplateId(emailSendTask.getTemplateId());
                if (template != null) {
                    // 如果前端没有提供subject和content，使用模板的默认值
                    if (emailSendTask.getSubject() == null || emailSendTask.getSubject().trim().isEmpty()) {
                        emailSendTask.setSubject(template.getSubject());
                    }
                    if (emailSendTask.getContent() == null || emailSendTask.getContent().trim().isEmpty()) {
                        emailSendTask.setContent(template.getContent());
                    }
                    
                }
            }
            
            // 确保subject和content不为空
            if (emailSendTask.getSubject() == null || emailSendTask.getSubject().trim().isEmpty()) {
                return error("邮件主题不能为空");
            }
            if (emailSendTask.getContent() == null || emailSendTask.getContent().trim().isEmpty()) {
                return error("邮件内容不能为空");
            }
            
            // 插入任务记录
            int result = emailSendTaskService.insertEmailSendTask(emailSendTask);
            if (result > 0) {
                // 根据sendMode参数决定执行方式
                if ("immediate".equals(emailSendTask.getSendMode())) {
                    // 立即发送，使用长连接
                    emailSendService.startSendTaskWithLongConnection(emailSendTask.getTaskId());
                    return AjaxResult.success("发送任务已创建并开始执行", emailSendTask);
                } else {
                    // 定时发送，添加到调度器
                    if (emailSendTask.getStartTime() == null) {
                        return error("定时发送任务必须指定发送时间");
                    }
                    emailSchedulerService.scheduleEmailTask(emailSendTask);
                    return AjaxResult.success("定时发送任务已创建并加入调度队列", emailSendTask);
                }
            } else {
                return error("创建发送任务失败");
            }
        } catch (Exception e) {
            logger.error("创建发送任务失败", e);
            return error("创建发送任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取任务统计信息
     */
    @GetMapping("/statistics/{taskId}")
    public AjaxResult getTaskStatistics(@PathVariable("taskId") Long taskId)
    {
        EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
        if (task != null) {
            return success(task);
        }
        return error("任务不存在");
    }

    /**
     * 重新执行任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:restart')")
    @Log(title = "重新执行邮件任务", businessType = BusinessType.UPDATE)
    @PostMapping("/restart/{taskId}")
    public AjaxResult restartTask(@PathVariable("taskId") Long taskId)
    {
        try
        {
            // 检查任务状态
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task == null)
            {
                return error("任务不存在");
            }
            
            if ("1".equals(task.getStatus())) // 执行中
            {
                return error("任务正在执行中，无法重新执行");
            }
            
            // 创建新的执行记录
            EmailTaskExecution execution = new EmailTaskExecution();
            execution.setTaskId(taskId);
            execution.setExecutionStatus("0"); // 未开始
            execution.setExecutionUser(getUsername());
            execution.setExecutionIp(getIpAddr());
            execution.setTotalCount(task.getTotalCount());
            
            emailTaskExecutionService.insertEmailTaskExecution(execution);
            
            return success("任务重新执行已启动");
        }
        catch (Exception e)
        {
            return error("重新执行任务失败: " + e.getMessage());
        }
    }

    /**
     * 复制任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:copy')")
    @Log(title = "复制邮件任务", businessType = BusinessType.INSERT)
    @PostMapping("/copy/{taskId}")
    public AjaxResult copyTask(@PathVariable("taskId") Long taskId)
    {
        try
        {
            EmailSendTask originalTask = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (originalTask == null)
            {
                return error("原任务不存在");
            }
            
            // 创建新任务
            EmailSendTask newTask = new EmailSendTask();
            newTask.setTaskName(originalTask.getTaskName() + "_副本");
            newTask.setSubject(originalTask.getSubject());
            newTask.setContent(originalTask.getContent());
            newTask.setAccountId(originalTask.getAccountId());
            newTask.setTemplateId(originalTask.getTemplateId());
            newTask.setRecipientType(originalTask.getRecipientType());
            newTask.setRecipientIds(originalTask.getRecipientIds());
            newTask.setSendMode(originalTask.getSendMode());
            newTask.setStartTime(originalTask.getStartTime());
            newTask.setStatus("0"); // 未开始
            newTask.setCreateBy(getUsername());
            newTask.setCreateTime(new java.util.Date());
            
            emailSendTaskService.insertEmailSendTask(newTask);
            
            return success("任务复制成功");
        }
        catch (Exception e)
        {
            return error("复制任务失败: " + e.getMessage());
        }
    }

    /**
     * 停止任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:stop')")
    @Log(title = "停止邮件任务", businessType = BusinessType.UPDATE)
    @PostMapping("/stop/{taskId}")
    public AjaxResult stopTask(@PathVariable("taskId") Long taskId)
    {
        try
        {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task == null)
            {
                return error("任务不存在");
            }
            
            if (!"1".equals(task.getStatus()) && !"0".equals(task.getStatus())) // 不是执行中或等待中
            {
                return error("任务不在执行中或等待中，无法停止");
            }
            
            // 如果是等待中的定时任务，从调度队列中移除
            if ("0".equals(task.getStatus())) {
                emailSchedulerService.cancelScheduledTask(taskId);
            }
            
            // 更新任务状态为中断
            task.setStatus("4");
            emailSendTaskService.updateEmailSendTask(task);
            
            return success("任务已停止");
        }
        catch (Exception e)
        {
            return error("停止任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消定时任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:cancel')")
    @Log(title = "取消定时邮件任务", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel-schedule/{taskId}")
    public AjaxResult cancelSchedule(@PathVariable("taskId") Long taskId)
    {
        try
        {
            EmailSendTask task = emailSendTaskService.selectEmailSendTaskByTaskId(taskId);
            if (task == null)
            {
                return error("任务不存在");
            }
            
            if (!"0".equals(task.getStatus())) // 不是等待状态
            {
                return error("只能取消等待中的定时任务");
            }
            
            // 从调度队列中移除
            emailSchedulerService.cancelScheduledTask(taskId);
            
            // 更新任务状态为已取消
            task.setStatus("5"); // 假设5表示已取消
            emailSendTaskService.updateEmailSendTask(task);
            
            return success("定时任务已取消");
        }
        catch (Exception e)
        {
            return error("取消定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务执行记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:task:execution')")
    @GetMapping("/execution/list")
    public TableDataInfo getExecutionList(EmailTaskExecution emailTaskExecution)
    {
        startPage();
        List<EmailTaskExecution> list = emailTaskExecutionService.selectEmailTaskExecutionList(emailTaskExecution);
        return getDataTable(list);
    }

    /**
     * 获取指定任务的执行记录
     */
    @PreAuthorize("@ss.hasPermi('email:task:execution')")
    @GetMapping("/execution/{taskId}")
    public AjaxResult getTaskExecutions(@PathVariable("taskId") Long taskId)
    {
        try
        {
            List<EmailTaskExecution> executions = emailTaskExecutionService.selectExecutionsByTaskId(taskId);
            return success(executions);
        }
        catch (Exception e)
        {
            return error("获取执行记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取邮件调度器状态
     */
    @PreAuthorize("@ss.hasPermi('email:task:scheduler')")
    @GetMapping("/scheduler/status")
    public AjaxResult getSchedulerStatus()
    {
        try
        {
            Map<String, Object> status = new HashMap<>();
            status.put("running", emailSchedulerService.isRunning());
            status.put("pendingTaskCount", emailSchedulerService.getPendingTaskCount());
            status.put("processingTaskCount", emailSchedulerService.getProcessingTaskCount());
            status.put("totalScheduledTaskCount", emailSchedulerService.getTotalScheduledTaskCount());
            
            return success(status);
        }
        catch (Exception e)
        {
            return error("获取调度器状态失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期的处理任务
     */
    @PreAuthorize("@ss.hasPermi('email:task:cleanup')")
    @PostMapping("/scheduler/cleanup")
    public AjaxResult cleanupExpiredTasks()
    {
        try
        {
            emailSchedulerService.cleanupExpiredProcessingTasks();
            return success("过期任务清理完成");
        }
        catch (Exception e)
        {
            return error("清理过期任务失败: " + e.getMessage());
        }
    }
}
