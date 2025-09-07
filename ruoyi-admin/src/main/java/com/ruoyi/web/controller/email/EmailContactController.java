package com.ruoyi.web.controller.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 测试接口 - 检查数据库连接和数据
     */
    @GetMapping("/test")
    public AjaxResult test()
    {
        try {
            // 查询总数
            EmailContact queryContact = new EmailContact();
            List<EmailContact> allContacts = emailContactService.selectEmailContactList(queryContact);
            int totalCount = allContacts.size();
            
            // 查询前5条数据
            List<EmailContact> sampleContacts = allContacts.stream()
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", totalCount);
            result.put("sampleContacts", sampleContacts);
            result.put("message", "数据库连接正常");
            
            return success(result);
        } catch (Exception e) {
            return error("数据库连接异常: " + e.getMessage());
        }
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
        try {
            return toAjax(emailContactService.insertEmailContact(emailContact));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("新增失败: " + e.getMessage());
        }
    }

    /**
     * 修改邮件联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "邮件联系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailContact emailContact)
    {
        try {
            return toAjax(emailContactService.updateEmailContact(emailContact));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("修改失败: " + e.getMessage());
        }
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

    /**
     * 批量导入联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:import')")
    @Log(title = "邮件联系人", businessType = BusinessType.IMPORT)
    @PostMapping("/batchImport")
    public AjaxResult batchImport(@RequestBody List<EmailContact> contacts)
    {
        try {
            String result = emailContactService.batchImportContacts(contacts);
            return success(result);
        } catch (Exception e) {
            return error("批量导入失败：" + e.getMessage());
        }
    }

    /**
     * 根据邮箱地址查询联系人
     */
    @GetMapping("/getByEmail/{email}")
    public AjaxResult getByEmail(@PathVariable("email") String email)
    {
        EmailContact contact = emailContactService.selectEmailContactByEmail(email);
        return success(contact);
    }

    /**
     * 根据群组ID列表查询联系人
     */
    @PostMapping("/listByGroupIds")
    public AjaxResult listByGroupIds(@RequestBody List<String> groupIds)
    {
        List<EmailContact> list = emailContactService.selectContactsByGroupIds(groupIds);
        return success(list);
    }

    /**
     * 根据标签ID列表查询联系人
     */
    @PostMapping("/listByTagIds")
    public AjaxResult listByTagIds(@RequestBody List<String> tagIds)
    {
        List<EmailContact> list = emailContactService.selectContactsByTagIds(tagIds);
        return success(list);
    }

    /**
     * 根据联系人ID列表查询联系人
     */
    @PostMapping("/listByIds")
    public AjaxResult listByIds(@RequestBody List<String> contactIds)
    {
        List<EmailContact> list = emailContactService.selectContactsByIds(contactIds);
        return success(list);
    }

    /**
     * 更新联系人统计信息
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "邮件联系人", businessType = BusinessType.UPDATE)
    @PutMapping("/updateStatistics/{contactId}")
    public AjaxResult updateStatistics(@PathVariable("contactId") Long contactId)
    {
        return toAjax(emailContactService.updateContactStatistics(contactId));
    }

    /**
     * 批量更新联系人统计信息
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "邮件联系人", businessType = BusinessType.UPDATE)
    @PutMapping("/batchUpdateStatistics")
    public AjaxResult batchUpdateStatistics(@RequestBody List<Long> contactIds)
    {
        int result = emailContactService.batchUpdateContactStatistics(contactIds);
        return success("成功更新 " + result + " 个联系人的统计信息");
    }

    /**
     * 搜索联系人（支持多条件搜索）
     */
    @PostMapping("/search")
    public TableDataInfo search(@RequestBody EmailContact searchParams)
    {
        startPage();
        List<EmailContact> list = emailContactService.searchContacts(searchParams);
        return getDataTable(list);
    }

    /**
     * 获取联系人统计信息
     */
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        Map<String, Object> statistics = emailContactService.getContactStatistics();
        return success(statistics);
    }

    /**
     * 验证邮箱地址是否已存在
     */
    @GetMapping("/validateEmail/{email}")
    public AjaxResult validateEmail(@PathVariable("email") String email)
    {
        boolean exists = emailContactService.isEmailExists(email);
        return success(exists);
    }

    /**
     * 批量删除联系人（软删除）
     */
    @PreAuthorize("@ss.hasPermi('email:contact:remove')")
    @Log(title = "邮件联系人", businessType = BusinessType.DELETE)
    @PostMapping("/batchDelete")
    public AjaxResult batchDelete(@RequestBody List<Long> contactIds)
    {
        return toAjax(emailContactService.batchDeleteContacts(contactIds));
    }

    /**
     * 恢复已删除的联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "邮件联系人", businessType = BusinessType.UPDATE)
    @PutMapping("/restore/{contactId}")
    public AjaxResult restore(@PathVariable("contactId") Long contactId)
    {
        return toAjax(emailContactService.restoreContact(contactId));
    }

    /**
     * 批量恢复已删除的联系人
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "邮件联系人", businessType = BusinessType.UPDATE)
    @PostMapping("/batchRestore")
    public AjaxResult batchRestore(@RequestBody List<Long> contactIds)
    {
        int result = emailContactService.batchRestoreContacts(contactIds);
        return success("成功恢复 " + result + " 个联系人");
    }
}
