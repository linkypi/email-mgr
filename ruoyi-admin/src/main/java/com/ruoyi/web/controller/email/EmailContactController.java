package com.ruoyi.web.controller.email;

import java.util.ArrayList;
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
import com.github.pagehelper.PageHelper;
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
     * 调试接口 - 检查当前用户信息和数据权限
     */
    @GetMapping("/debug")
    public AjaxResult debug()
    {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 获取当前用户信息
            String currentUser = getUsername();
            Long currentUserId = getUserId();
            result.put("currentUser", currentUser);
            result.put("currentUserId", currentUserId);
            
            // 查询所有联系人（不经过数据权限过滤）
            EmailContact queryContact = new EmailContact();
            List<EmailContact> allContacts = emailContactService.selectEmailContactList(queryContact);
            result.put("allContactsCount", allContacts.size());
            
            // 查询当前用户创建的联系人
            List<EmailContact> userContacts = allContacts.stream()
                .filter(contact -> currentUser.equals(contact.getCreateBy()))
                .collect(java.util.stream.Collectors.toList());
            result.put("userContactsCount", userContacts.size());
            
            // 显示前5个联系人
            List<EmailContact> sampleContacts = allContacts.stream()
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
            result.put("sampleContacts", sampleContacts);
            
            return success(result);
        } catch (Exception e) {
            return error("调试失败: " + e.getMessage());
        }
    }

    /**
     * 测试联系人统计更新功能
     */
    @GetMapping("/testStatistics")
    public AjaxResult testStatistics()
    {
        try {
            // 获取所有联系人
            EmailContact queryContact = new EmailContact();
            List<EmailContact> allContacts = emailContactService.selectEmailContactList(queryContact);
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalContacts", allContacts.size());
            
            // 测试更新前5个联系人的统计信息
            int updateCount = 0;
            for (int i = 0; i < Math.min(5, allContacts.size()); i++) {
                EmailContact contact = allContacts.get(i);
                try {
                    int updateResult = emailContactService.updateContactStatistics(contact.getContactId());
                    if (updateResult > 0) {
                        updateCount++;
                    }
                } catch (Exception e) {
                    logger.error("更新联系人统计失败: contactId={}, error={}", contact.getContactId(), e.getMessage());
                }
            }
            
            result.put("updateCount", updateCount);
            result.put("message", "统计更新测试完成");
            
            return success(result);
        } catch (Exception e) {
            return error("统计更新测试失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新所有联系人统计信息
     */
    @PreAuthorize("@ss.hasPermi('email:contact:edit')")
    @Log(title = "邮件联系人", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAllStatistics")
    public AjaxResult updateAllStatistics()
    {
        try {
            // 获取所有联系人
            EmailContact queryContact = new EmailContact();
            List<EmailContact> allContacts = emailContactService.selectEmailContactList(queryContact);
            
            int updateCount = 0;
            int totalCount = allContacts.size();
            
            for (EmailContact contact : allContacts) {
                try {
                    int updateResult = emailContactService.updateContactStatistics(contact.getContactId());
                    if (updateResult > 0) {
                        updateCount++;
                    }
                } catch (Exception e) {
                    logger.error("更新联系人统计失败: contactId={}, email={}, error={}", 
                        contact.getContactId(), contact.getEmail(), e.getMessage());
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalContacts", totalCount);
            result.put("updateCount", updateCount);
            result.put("message", "批量更新完成，成功更新 " + updateCount + " 个联系人的统计信息");
            
            return success(result);
        } catch (Exception e) {
            return error("批量更新统计失败: " + e.getMessage());
        }
    }

    /**
     * 诊断联系人统计问题
     */
    @GetMapping("/diagnoseStatistics")
    public AjaxResult diagnoseStatistics()
    {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 检查联系人数据
            EmailContact queryContact = new EmailContact();
            List<EmailContact> allContacts = emailContactService.selectEmailContactList(queryContact);
            result.put("totalContacts", allContacts.size());
            
            // 2. 检查email_track_record表数据
            // 这里需要注入EmailTrackRecordService，暂时跳过
            
            // 3. 显示前5个联系人的邮箱地址
            List<Map<String, Object>> contactEmails = new ArrayList<>();
            for (int i = 0; i < Math.min(5, allContacts.size()); i++) {
                EmailContact contact = allContacts.get(i);
                Map<String, Object> contactInfo = new HashMap<>();
                contactInfo.put("contactId", contact.getContactId());
                contactInfo.put("name", contact.getName());
                contactInfo.put("email", contact.getEmail());
                contactInfo.put("sendCount", contact.getSendCount());
                contactInfo.put("replyCount", contact.getReplyCount());
                contactInfo.put("replyRate", contact.getReplyRate());
                contactEmails.add(contactInfo);
            }
            result.put("sampleContacts", contactEmails);
            
            // 4. 测试更新第一个联系人的统计
            if (!allContacts.isEmpty()) {
                EmailContact firstContact = allContacts.get(0);
                try {
                    int updateResult = emailContactService.updateContactStatistics(firstContact.getContactId());
                    result.put("testUpdateResult", updateResult);
                    result.put("testContactId", firstContact.getContactId());
                    result.put("testContactEmail", firstContact.getEmail());
                } catch (Exception e) {
                    result.put("testUpdateError", e.getMessage());
                }
            }
            
            result.put("message", "诊断完成");
            return success(result);
        } catch (Exception e) {
            return error("诊断失败: " + e.getMessage());
        }
    }

    /**
     * 强制修复联系人统计问题
     */
    @PostMapping("/forceFixStatistics")
    public AjaxResult forceFixStatistics()
    {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 获取所有联系人
            EmailContact queryContact = new EmailContact();
            List<EmailContact> allContacts = emailContactService.selectEmailContactList(queryContact);
            
            int successCount = 0;
            int failCount = 0;
            List<Map<String, Object>> updateResults = new ArrayList<>();
            
            // 2. 逐个更新联系人统计
            for (EmailContact contact : allContacts) {
                Map<String, Object> updateResult = new HashMap<>();
                updateResult.put("contactId", contact.getContactId());
                updateResult.put("name", contact.getName());
                updateResult.put("email", contact.getEmail());
                
                try {
                    // 先重置统计为0
                    contact.setSendCount(0);
                    contact.setReplyCount(0);
                    contact.setOpenCount(0);
                    contact.setReplyRate(0.0);
                    emailContactService.updateEmailContact(contact);
                    
                    // 然后重新计算统计
                    int updateCount = emailContactService.updateContactStatistics(contact.getContactId());
                    
                    if (updateCount > 0) {
                        successCount++;
                        updateResult.put("status", "success");
                        updateResult.put("updateCount", updateCount);
                        
                        // 重新查询更新后的数据
                        EmailContact updatedContact = emailContactService.selectEmailContactByContactId(contact.getContactId());
                        updateResult.put("newSendCount", updatedContact.getSendCount());
                        updateResult.put("newReplyCount", updatedContact.getReplyCount());
                        updateResult.put("newReplyRate", updatedContact.getReplyRate());
                    } else {
                        failCount++;
                        updateResult.put("status", "failed");
                        updateResult.put("reason", "更新返回0");
                    }
                } catch (Exception e) {
                    failCount++;
                    updateResult.put("status", "error");
                    updateResult.put("error", e.getMessage());
                }
                
                updateResults.add(updateResult);
            }
            
            result.put("totalContacts", allContacts.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("updateResults", updateResults);
            result.put("message", String.format("强制修复完成：成功 %d 个，失败 %d 个", successCount, failCount));
            
            return success(result);
        } catch (Exception e) {
            return error("强制修复失败: " + e.getMessage());
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
            // 设置创建者信息
            emailContact.setCreateBy(getUsername());
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
    public TableDataInfo search(@RequestBody Map<String, Object> params)
    {
        // 提取分页参数
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        
        // 设置默认分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 构建搜索参数对象
        EmailContact searchParams = new EmailContact();
        if (params.get("name") != null) {
            searchParams.setName((String) params.get("name"));
        }
        if (params.get("email") != null) {
            searchParams.setEmail((String) params.get("email"));
        }
        if (params.get("company") != null) {
            searchParams.setCompany((String) params.get("company"));
        }
        if (params.get("level") != null) {
            searchParams.setLevel((String) params.get("level"));
        }
        if (params.get("groupId") != null) {
            searchParams.setGroupId(Long.valueOf(params.get("groupId").toString()));
        }
        if (params.get("tags") != null) {
            searchParams.setTags((String) params.get("tags"));
        }
        if (params.get("status") != null) {
            searchParams.setStatus((String) params.get("status"));
        }
        
        // 先查询总数
        long total = emailContactService.countContactsBySearch(searchParams);
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        List<EmailContact> list = emailContactService.searchContacts(searchParams);
        
        // 构建分页结果
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
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
