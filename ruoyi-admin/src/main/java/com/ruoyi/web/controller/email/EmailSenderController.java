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
import com.ruoyi.system.domain.email.EmailSender;
import com.ruoyi.system.service.email.IEmailSenderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 发件人信息Controller
 * 
 * @author ruoyi
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/email/sender")
public class EmailSenderController extends BaseController
{
    @Autowired
    private IEmailSenderService emailSenderService;

    /**
     * 查询发件人信息列表
     */
    @PreAuthorize("@ss.hasPermi('email:sender:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailSender emailSender)
    {
        startPage();
        List<EmailSender> list = emailSenderService.selectEmailSenderList(emailSender);
        return getDataTable(list);
    }

    /**
     * 导出发件人信息列表
     */
    @PreAuthorize("@ss.hasPermi('email:sender:export')")
    @Log(title = "发件人信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailSender emailSender)
    {
        List<EmailSender> list = emailSenderService.selectEmailSenderList(emailSender);
        ExcelUtil<EmailSender> util = new ExcelUtil<EmailSender>(EmailSender.class);
        util.exportExcel(response, list, "发件人信息数据");
    }

    /**
     * 获取发件人信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:sender:query')")
    @GetMapping(value = "/{senderId}")
    public AjaxResult getInfo(@PathVariable("senderId") Long senderId)
    {
        return success(emailSenderService.selectEmailSenderBySenderId(senderId));
    }

    /**
     * 获取发件人信息及其关联的邮箱账号
     */
    @PreAuthorize("@ss.hasPermi('email:sender:query')")
    @GetMapping(value = "/{senderId}/accounts")
    public AjaxResult getInfoWithAccounts(@PathVariable("senderId") Long senderId)
    {
        return success(emailSenderService.selectEmailSenderWithAccounts(senderId));
    }

    /**
     * 新增发件人信息
     */
    @PreAuthorize("@ss.hasPermi('email:sender:add')")
    @Log(title = "发件人信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailSender emailSender)
    {
        return toAjax(emailSenderService.insertEmailSender(emailSender));
    }

    /**
     * 修改发件人信息
     */
    @PreAuthorize("@ss.hasPermi('email:sender:edit')")
    @Log(title = "发件人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailSender emailSender)
    {
        return toAjax(emailSenderService.updateEmailSender(emailSender));
    }

    /**
     * 删除发件人信息
     */
    @PreAuthorize("@ss.hasPermi('email:sender:remove')")
    @Log(title = "发件人信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{senderIds}")
    public AjaxResult remove(@PathVariable Long[] senderIds)
    {
        return toAjax(emailSenderService.deleteEmailSenderBySenderIds(senderIds));
    }

    /**
     * 批量更新发件人状态
     */
    @PreAuthorize("@ss.hasPermi('email:sender:edit')")
    @Log(title = "发件人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult updateStatus(@RequestBody EmailSender emailSender)
    {
        return toAjax(emailSenderService.batchUpdateSenderStatus(emailSender));
    }

    /**
     * 获取发件人选项列表（用于下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('email:sender:list')")
    @GetMapping("/options")
    public AjaxResult getOptions()
    {
        List<EmailSender> list = emailSenderService.selectEmailSenderOptions();
        return success(list);
    }
}







