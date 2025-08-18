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
import com.ruoyi.system.domain.email.EmailTemplate;
import com.ruoyi.system.service.email.IEmailTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮件模板Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/template")
public class EmailTemplateController extends BaseController
{
    @Autowired
    private IEmailTemplateService emailTemplateService;

    /**
     * 查询邮件模板列表
     */
    @PreAuthorize("@ss.hasPermi('email:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailTemplate emailTemplate)
    {
        startPage();
        List<EmailTemplate> list = emailTemplateService.selectEmailTemplateList(emailTemplate);
        return getDataTable(list);
    }

    /**
     * 导出邮件模板列表
     */
    @PreAuthorize("@ss.hasPermi('email:template:export')")
    @Log(title = "邮件模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailTemplate emailTemplate)
    {
        List<EmailTemplate> list = emailTemplateService.selectEmailTemplateList(emailTemplate);
        ExcelUtil<EmailTemplate> util = new ExcelUtil<EmailTemplate>(EmailTemplate.class);
        util.exportExcel(response, list, "邮件模板数据");
    }

    /**
     * 获取邮件模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId)
    {
        return success(emailTemplateService.selectEmailTemplateByTemplateId(templateId));
    }

    /**
     * 新增邮件模板
     */
    @PreAuthorize("@ss.hasPermi('email:template:add')")
    @Log(title = "邮件模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailTemplate emailTemplate)
    {
        return toAjax(emailTemplateService.insertEmailTemplate(emailTemplate));
    }

    /**
     * 修改邮件模板
     */
    @PreAuthorize("@ss.hasPermi('email:template:edit')")
    @Log(title = "邮件模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailTemplate emailTemplate)
    {
        return toAjax(emailTemplateService.updateEmailTemplate(emailTemplate));
    }

    /**
     * 删除邮件模板
     */
    @PreAuthorize("@ss.hasPermi('email:template:remove')")
    @Log(title = "邮件模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(emailTemplateService.deleteEmailTemplateByTemplateIds(templateIds));
    }

    /**
     * 预览邮件模板
     */
    @PreAuthorize("@ss.hasPermi('email:template:preview')")
    @GetMapping("/preview/{templateId}")
    public AjaxResult preview(@PathVariable("templateId") Long templateId)
    {
        EmailTemplate template = emailTemplateService.selectEmailTemplateByTemplateId(templateId);
        if (template != null) {
            return success(template);
        }
        return error("模板不存在");
    }

    /**
     * 获取所有模板列表（用于下拉选择）
     */
    @GetMapping("/all")
    public AjaxResult getAllTemplates()
    {
        List<EmailTemplate> list = emailTemplateService.selectEmailTemplateList(new EmailTemplate());
        return success(list);
    }
}
