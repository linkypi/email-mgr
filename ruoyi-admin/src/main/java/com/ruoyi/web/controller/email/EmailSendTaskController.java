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
import com.ruoyi.system.service.email.IEmailSendTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

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
            int result = emailSendTaskService.startSendTask(taskId);
            return success(result > 0 ? "任务启动成功" : "任务启动失败");
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
            int result = emailSendTaskService.pauseSendTask(taskId);
            return success(result > 0 ? "任务暂停成功" : "任务暂停失败");
        } catch (Exception e) {
            return error("任务暂停失败：" + e.getMessage());
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
}
