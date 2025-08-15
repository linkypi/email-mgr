package com.ruoyi.web.controller.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.service.email.IEmailContactService;

/**
 * 邮件仪表板Controller
 * 
 * @author ruoyi
 * @date 2023-01-01
 */
@RestController
@RequestMapping("/email/dashboard")
public class EmailDashboardController extends BaseController
{
    @Autowired
    private IEmailContactService emailContactService;

    /**
     * 获取仪表板数据
     */
    @PreAuthorize("@ss.hasPermi('email:dashboard:view')")
    @GetMapping("/data")
    public AjaxResult getDashboardData()
    {
        Map<String, Object> data = new HashMap<>();
        
        // 模拟数据，实际应该从数据库查询
        data.put("todaySent", 287);
        data.put("yesterdaySent", 215);
        data.put("totalSent", 4586);
        data.put("monthGrowth", 15);
        data.put("totalContacts", 1425);
        data.put("activeContacts", 1389);
        data.put("avgReplyRate", 64.7);
        
        return success(data);
    }

    /**
     * 获取联系人统计
     */
    @PreAuthorize("@ss.hasPermi('email:contact:list')")
    @GetMapping("/contact/statistics")
    public AjaxResult getContactStatistics()
    {
        EmailContact emailContact = new EmailContact();
        List<EmailContact> list = emailContactService.selectEmailContactStatisticsList(emailContact);
        return success(list);
    }

    /**
     * 获取模板统计
     */
    @PreAuthorize("@ss.hasPermi('email:template:list')")
    @GetMapping("/template/statistics")
    public AjaxResult getTemplateStatistics()
    {
        // 模拟模板统计数据
        Map<String, Object> data = new HashMap<>();
        data.put("templateName", "产品推广邮件模板");
        data.put("sendCount", 287);
        data.put("deliveredCount", 281);
        data.put("openedCount", 189);
        data.put("repliedCount", 54);
        data.put("deliveryRate", 97.9);
        data.put("conversionRate", 19.2);
        
        return success(data);
    }
}
