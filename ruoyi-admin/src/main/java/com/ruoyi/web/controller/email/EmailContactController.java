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
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.service.email.IEmailContactService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 邮件联系人Controller
 * 
 * @author ruoyi
 * @date 2023-12-01
 */
@RestController
@RequestMapping("/email/contact")
public class EmailContactController extends BaseController
{
    @Autowired
    private IEmailContactService emailContactService;

    /**
     * 查询邮件联系人列表
     */
    @PreAuthorize("@ss.hasPermi('email:contact:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailContact emailContact)
    {
        startPage();
        List<EmailContact> list = emailContactService.selectEmailContactList(emailContact);
        return getDataTable(list);
    }

    /**
     * 导出邮件联系人列表
     */
    @PreAuthorize("@ss.hasPermi('email:contact:export')")
    @Log(title = "邮件联系人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailContact emailContact)
    {
        List<EmailContact> list = emailContactService.selectEmailContactList(emailContact);
        ExcelUtil<EmailContact> util = new ExcelUtil<EmailContact>(EmailContact.class);
        util.exportExcel(response, list, "邮件联系人数据");
    }

    /**
     * 获取邮件联系人详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:contact:query')")
    @GetMapping(value = "/{contactId}")
    public AjaxResult getInfo(@PathVariable("contactId") Long contactId)
    {
        return success(emailContactService.selectEmailContactByContactId(contactId));
    }

    /**
     * 新增邮件联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:add')")
    @Log(title = "邮件联系人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailContact emailContact)
    {
        return toAjax(emailContactService.insertEmailContact(emailContact));
    }

    /**
     * 修改邮件联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "邮件联系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailContact emailContact)
    {
        return toAjax(emailContactService.updateEmailContact(emailContact));
    }

    /**
     * 删除邮件联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:remove')")
    @Log(title = "邮件联系人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contactIds}")
    public AjaxResult remove(@PathVariable Long[] contactIds)
    {
        return toAjax(emailContactService.deleteEmailContactByContactIds(contactIds));
    }

    /**
     * 导入联系人数据
     */
    @PreAuthorize("@ss.hasPermi('email:contact:import')")
    @Log(title = "邮件联系人", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, Boolean isUpdateSupport) throws Exception
    {
        String operName = getUsername();
        String message = emailContactService.importContact(file, isUpdateSupport, operName);
        return success(message);
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<EmailContact> util = new ExcelUtil<EmailContact>(EmailContact.class);
        util.importTemplateExcel(response, "邮件联系人数据");
    }

    /**
     * 根据群组ID查询联系人列表
     */
    @GetMapping("/listByGroup/{groupId}")
    public AjaxResult listByGroup(@PathVariable("groupId") Long groupId)
    {
        List<EmailContact> list = emailContactService.selectEmailContactByGroupId(groupId);
        return success(list);
    }

    /**
     * 根据标签查询联系人列表
     */
    @GetMapping("/listByTag/{tag}")
    public AjaxResult listByTag(@PathVariable("tag") String tag)
    {
        List<EmailContact> list = emailContactService.selectEmailContactByTag(tag);
        return success(list);
    }

    /**
     * 查询回复率最高的联系人
     */
    @GetMapping("/topReplyRate/{limit}")
    public AjaxResult topReplyRate(@PathVariable("limit") int limit)
    {
        List<EmailContact> list = emailContactService.selectTopReplyRateContacts(limit);
        return success(list);
    }

    /**
     * 获取所有联系人列表（用于下拉选择）
     */
    @GetMapping("/all")
    public AjaxResult getAllContacts()
    {
        List<EmailContact> list = emailContactService.selectEmailContactList(new EmailContact());
        return success(list);
    }
}
