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

/**
 * 联系人Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/contact")
public class EmailContactController extends BaseController
{
    @Autowired
    private IEmailContactService emailContactService;

    /**
     * 查询联系人列表
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
     * 导出联系人列表
     */
    @PreAuthorize("@ss.hasPermi('email:contact:export')")
    @Log(title = "联系人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailContact emailContact)
    {
        List<EmailContact> list = emailContactService.selectEmailContactList(emailContact);
        ExcelUtil<EmailContact> util = new ExcelUtil<EmailContact>(EmailContact.class);
        util.exportExcel(response, list, "联系人数据");
    }

    /**
     * 获取联系人详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:contact:query')")
    @GetMapping(value = "/{contactId}")
    public AjaxResult getInfo(@PathVariable("contactId") Long contactId)
    {
        return success(emailContactService.selectEmailContactByContactId(contactId));
    }

    /**
     * 新增联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:add')")
    @Log(title = "联系人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailContact emailContact)
    {
        return toAjax(emailContactService.insertEmailContact(emailContact));
    }

    /**
     * 修改联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "联系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailContact emailContact)
    {
        return toAjax(emailContactService.updateEmailContact(emailContact));
    }

    /**
     * 删除联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:remove')")
    @Log(title = "联系人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contactIds}")
    public AjaxResult remove(@PathVariable Long[] contactIds)
    {
        return toAjax(emailContactService.deleteEmailContactByContactIds(contactIds));
    }

    /**
     * 批量导入联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:import')")
    @Log(title = "批量导入联系人", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestBody List<EmailContact> contactList)
    {
        return toAjax(emailContactService.batchInsertEmailContact(contactList));
    }
}
