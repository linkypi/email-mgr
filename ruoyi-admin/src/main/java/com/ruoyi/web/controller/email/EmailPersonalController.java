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
import com.ruoyi.system.domain.email.EmailPersonal;
import com.ruoyi.system.service.email.IEmailPersonalService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 个人邮件Controller
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
@RestController
@RequestMapping("/email/personal")
public class EmailPersonalController extends BaseController
{
    @Autowired
    private IEmailPersonalService emailPersonalService;

    /**
     * 查询个人邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailPersonal emailPersonal)
    {
        startPage();
        List<EmailPersonal> list = emailPersonalService.selectEmailPersonalList(emailPersonal);
        return getDataTable(list);
    }

    /**
     * 查询收件箱列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:inbox:list')")
    @GetMapping("/inbox/list")
    public TableDataInfo inboxList(EmailPersonal emailPersonal)
    {
        startPage();
        List<EmailPersonal> list = emailPersonalService.selectInboxList(emailPersonal);
        return getDataTable(list);
    }

    /**
     * 查询发件箱列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:sent:list')")
    @GetMapping("/sent/list")
    public TableDataInfo sentList(EmailPersonal emailPersonal)
    {
        startPage();
        List<EmailPersonal> list = emailPersonalService.selectSentList(emailPersonal);
        return getDataTable(list);
    }

    /**
     * 查询星标邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:starred:list')")
    @GetMapping("/starred/list")
    public TableDataInfo starredList(EmailPersonal emailPersonal)
    {
        startPage();
        List<EmailPersonal> list = emailPersonalService.selectStarredList(emailPersonal);
        return getDataTable(list);
    }

    /**
     * 查询已删除邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:deleted:list')")
    @GetMapping("/deleted/list")
    public TableDataInfo deletedList(EmailPersonal emailPersonal)
    {
        startPage();
        List<EmailPersonal> list = emailPersonalService.selectDeletedList(emailPersonal);
        return getDataTable(list);
    }

    /**
     * 导出个人邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:export')")
    @Log(title = "个人邮件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailPersonal emailPersonal)
    {
        List<EmailPersonal> list = emailPersonalService.selectEmailPersonalList(emailPersonal);
        ExcelUtil<EmailPersonal> util = new ExcelUtil<EmailPersonal>(EmailPersonal.class);
        util.exportExcel(response, list, "个人邮件数据");
    }

    /**
     * 获取个人邮件详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:personal:query')")
    @GetMapping(value = "/{emailId}")
    public AjaxResult getInfo(@PathVariable("emailId") Long emailId)
    {
        return success(emailPersonalService.selectEmailPersonalByEmailId(emailId));
    }

    /**
     * 新增个人邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:add')")
    @Log(title = "个人邮件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailPersonal emailPersonal)
    {
        return toAjax(emailPersonalService.insertEmailPersonal(emailPersonal));
    }

    /**
     * 修改个人邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:edit')")
    @Log(title = "个人邮件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailPersonal emailPersonal)
    {
        return toAjax(emailPersonalService.updateEmailPersonal(emailPersonal));
    }

    /**
     * 删除个人邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:remove')")
    @Log(title = "个人邮件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{emailIds}")
    public AjaxResult remove(@PathVariable Long[] emailIds)
    {
        return toAjax(emailPersonalService.deleteEmailPersonalByEmailIds(emailIds));
    }

    /**
     * 标记邮件为已读
     */
    @PreAuthorize("@ss.hasPermi('email:personal:edit')")
    @Log(title = "标记邮件已读", businessType = BusinessType.UPDATE)
    @PutMapping("/read/{emailId}")
    public AjaxResult markAsRead(@PathVariable Long emailId)
    {
        return toAjax(emailPersonalService.markAsRead(emailId));
    }

    /**
     * 标记邮件为星标
     */
    @PreAuthorize("@ss.hasPermi('email:personal:edit')")
    @Log(title = "标记邮件星标", businessType = BusinessType.UPDATE)
    @PutMapping("/star/{emailId}")
    public AjaxResult markAsStarred(@PathVariable Long emailId)
    {
        return toAjax(emailPersonalService.markAsStarred(emailId));
    }

    /**
     * 标记邮件为重要
     */
    @PreAuthorize("@ss.hasPermi('email:personal:edit')")
    @Log(title = "标记邮件重要", businessType = BusinessType.UPDATE)
    @PutMapping("/important/{emailId}")
    public AjaxResult markAsImportant(@PathVariable Long emailId)
    {
        return toAjax(emailPersonalService.markAsImportant(emailId));
    }

    /**
     * 恢复已删除邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:deleted:restore')")
    @Log(title = "恢复已删除邮件", businessType = BusinessType.UPDATE)
    @PutMapping("/restore/{emailId}")
    public AjaxResult restoreEmail(@PathVariable Long emailId)
    {
        return toAjax(emailPersonalService.restoreEmail(emailId));
    }

    /**
     * 彻底删除邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:deleted:delete')")
    @Log(title = "彻底删除邮件", businessType = BusinessType.DELETE)
    @DeleteMapping("/permanent/{emailId}")
    public AjaxResult deletePermanently(@PathVariable Long emailId)
    {
        return toAjax(emailPersonalService.deletePermanently(emailId));
    }

    /**
     * 获取未读邮件数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:query')")
    @GetMapping("/unread/count")
    public AjaxResult getUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalService.getUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取收件箱未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:inbox:query')")
    @GetMapping("/inbox/unread/count")
    public AjaxResult getInboxUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalService.getInboxUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取发件箱未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:sent:query')")
    @GetMapping("/sent/unread/count")
    public AjaxResult getSentUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalService.getSentUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取星标邮件未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:starred:query')")
    @GetMapping("/starred/unread/count")
    public AjaxResult getStarredUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalService.getStarredUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取已删除邮件未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:deleted:query')")
    @GetMapping("/deleted/unread/count")
    public AjaxResult getDeletedUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalService.getDeletedUnreadCount(userId);
        return success(count);
    }
}


