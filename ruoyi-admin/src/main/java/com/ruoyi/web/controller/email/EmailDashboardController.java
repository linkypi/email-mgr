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
import com.ruoyi.system.service.email.IEmailTrackRecordService;

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

    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;

    /**
     * 获取仪表板数据
     */
    @GetMapping("/data")
    public AjaxResult getDashboardData()
    {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取今日发送邮件数
            String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long todaySent = emailTrackRecordService.countTodaySentEmails(today);
            data.put("todaySent", todaySent);
            
            // 获取昨日发送邮件数
            String yesterday = java.time.LocalDate.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long yesterdaySent = emailTrackRecordService.countTodaySentEmails(yesterday);
            data.put("yesterdaySent", yesterdaySent);
            
            // 获取总发送邮件数
            long totalSent = emailTrackRecordService.countTotalEmails();
            data.put("totalSent", totalSent);
            
            // 计算月度增长率（简化计算）
            long monthGrowth = yesterdaySent > 0 ? Math.round((double)(todaySent - yesterdaySent) / yesterdaySent * 100) : 0;
            data.put("monthGrowth", monthGrowth);
            
            // 获取总联系人数量
            long totalContacts = emailContactService.countTotalContacts();
            data.put("totalContacts", totalContacts);
            data.put("activeContacts", totalContacts); // 简化处理，假设所有联系人都活跃
            
            // 计算平均回复率
            long totalReplied = emailTrackRecordService.countRepliedEmails();
            double avgReplyRate = totalSent > 0 ? (double) totalReplied / totalSent * 100 : 0;
            data.put("avgReplyRate", Math.round(avgReplyRate * 10.0) / 10.0);
            
            return success(data);
        } catch (Exception e) {
            logger.error("获取仪表板数据失败", e);
            return error("获取仪表板数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取联系人统计
     */
    @GetMapping("/contact/statistics")
    public AjaxResult getContactStatistics()
    {
        // 获取回复率最高的联系人列表
        List<EmailContact> list = emailContactService.selectTopReplyRateContacts(10);
        return success(list);
    }

    /**
     * 获取模板统计
     */
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
