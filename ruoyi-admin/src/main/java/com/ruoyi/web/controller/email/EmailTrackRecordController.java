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
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.IEmailTrackRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮件跟踪记录Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/track-record")
public class EmailTrackRecordController extends BaseController
{
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;

    /**
     * 查询邮件跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailTrackRecordService.selectEmailTrackRecordList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 导出邮件跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:export')")
    @Log(title = "邮件跟踪记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailTrackRecord emailTrackRecord)
    {
        List<EmailTrackRecord> list = emailTrackRecordService.selectEmailTrackRecordList(emailTrackRecord);
        ExcelUtil<EmailTrackRecord> util = new ExcelUtil<EmailTrackRecord>(EmailTrackRecord.class);
        util.exportExcel(response, list, "邮件跟踪记录数据");
    }

    /**
     * 获取邮件跟踪记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(emailTrackRecordService.selectEmailTrackRecordById(id));
    }

    /**
     * 根据Message-ID获取邮件跟踪记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:query')")
    @GetMapping(value = "/message/{messageId}")
    public AjaxResult getInfoByMessageId(@PathVariable("messageId") String messageId)
    {
        return success(emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId));
    }

    /**
     * 根据任务ID获取邮件跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:query')")
    @GetMapping(value = "/task/{taskId}")
    public AjaxResult getListByTaskId(@PathVariable("taskId") Long taskId)
    {
        List<EmailTrackRecord> list = emailTrackRecordService.selectEmailTrackRecordByTaskId(taskId);
        return success(list);
    }

    /**
     * 根据邮箱账号ID获取邮件跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:query')")
    @GetMapping(value = "/account/{accountId}")
    public AjaxResult getListByAccountId(@PathVariable("accountId") Long accountId)
    {
        List<EmailTrackRecord> list = emailTrackRecordService.selectEmailTrackRecordByAccountId(accountId);
        return success(list);
    }

    /**
     * 根据状态获取邮件跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:query')")
    @GetMapping(value = "/status/{status}")
    public AjaxResult getListByStatus(@PathVariable("status") String status)
    {
        List<EmailTrackRecord> list = emailTrackRecordService.selectEmailTrackRecordByStatus(status);
        return success(list);
    }

    /**
     * 新增邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:add')")
    @Log(title = "邮件跟踪记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailTrackRecord emailTrackRecord)
    {
        return toAjax(emailTrackRecordService.insertEmailTrackRecord(emailTrackRecord));
    }

    /**
     * 修改邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:edit')")
    @Log(title = "邮件跟踪记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailTrackRecord emailTrackRecord)
    {
        return toAjax(emailTrackRecordService.updateEmailTrackRecord(emailTrackRecord));
    }

    /**
     * 删除邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:remove')")
    @Log(title = "邮件跟踪记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(emailTrackRecordService.deleteEmailTrackRecordByIds(ids));
    }

    /**
     * 根据Message-ID删除邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:remove')")
    @Log(title = "邮件跟踪记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/message/{messageId}")
    public AjaxResult removeByMessageId(@PathVariable String messageId)
    {
        return toAjax(emailTrackRecordService.deleteEmailTrackRecordByMessageId(messageId));
    }

    /**
     * 根据任务ID删除邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:remove')")
    @Log(title = "邮件跟踪记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/task/{taskId}")
    public AjaxResult removeByTaskId(@PathVariable Long taskId)
    {
        return toAjax(emailTrackRecordService.deleteEmailTrackRecordByTaskId(taskId));
    }

    /**
     * 获取任务邮件状态统计
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:query')")
    @GetMapping(value = "/stats/task/{taskId}")
    public AjaxResult getTaskStats(@PathVariable("taskId") Long taskId)
    {
        List<EmailTrackRecord> stats = emailTrackRecordService.selectEmailTrackRecordStatsByTaskId(taskId);
        return success(stats);
    }

    /**
     * 获取邮箱账号邮件状态统计
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:query')")
    @GetMapping(value = "/stats/account/{accountId}")
    public AjaxResult getAccountStats(@PathVariable("accountId") Long accountId)
    {
        List<EmailTrackRecord> stats = emailTrackRecordService.selectEmailTrackRecordStatsByAccountId(accountId);
        return success(stats);
    }

    /**
     * 更新邮件状态
     */
    @PreAuthorize("@ss.hasPermi('email:track-record:edit')")
    @Log(title = "邮件跟踪记录", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{messageId}/{status}")
    public AjaxResult updateStatus(@PathVariable("messageId") String messageId, @PathVariable("status") String status)
    {
        return toAjax(emailTrackRecordService.updateEmailStatus(messageId, status));
    }
}
