package com.ruoyi.web.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.IEmailContactService;
import com.ruoyi.system.service.email.IEmailPersonalService;
import com.ruoyi.system.service.email.IEmailSenderService;
import com.ruoyi.system.service.email.IEmailTrackRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页仪表板控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private IEmailAccountService emailAccountService;

    @Autowired
    private IEmailContactService emailContactService;

    // @Autowired
    // private IEmailPersonalService emailPersonalService;

    // @Autowired
    // private IEmailSenderService emailSenderService;

    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;

    /**
     * 获取仪表板统计数据
     */
    @PreAuthorize("@ss.hasPermi('dashboard:statistics:total')")
    @GetMapping("/stats")
    @Log(title = "仪表板统计", businessType = BusinessType.OTHER)
    public AjaxResult getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取总邮件数（使用email_track_record表）
            long totalEmails = emailTrackRecordService.countTotalEmails();
            stats.put("totalEmails", totalEmails);
            
            // 获取总联系人数量
            long totalContacts = emailContactService.countTotalContacts();
            stats.put("totalContacts", totalContacts);
            
            // 获取今日发送邮件数
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long todaySent = emailTrackRecordService.countTodaySentEmails(today);
            stats.put("todaySent", todaySent);
            
            // 计算回复率（使用email_track_record表）
            long totalReplied = emailTrackRecordService.countRepliedEmails();
            double replyRate = totalEmails > 0 ? (double) totalReplied / totalEmails * 100 : 0;
            stats.put("replyRate", Math.round(replyRate * 10.0) / 10.0);
            
            // 获取活跃账号数
            long activeAccounts = emailAccountService.countActiveAccounts();
            stats.put("activeAccounts", activeAccounts);
            
            // 获取今日打开邮件数
            long todayOpened = emailTrackRecordService.countTodayOpenedEmails(today);
            stats.put("todayOpened", todayOpened);
            
            // 获取今日点击邮件数（暂时设为0，因为没有点击跟踪）
            stats.put("todayClicked", 0);
            
            return AjaxResult.success(stats);
        } catch (Exception e) {
            logger.error("获取仪表板统计数据失败", e);
            return AjaxResult.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统信息
     */
    @PreAuthorize("@ss.hasPermi('dashboard:system:info')")
    @GetMapping("/system/info")
    @Log(title = "系统信息", businessType = BusinessType.OTHER)
    public AjaxResult getSystemInfo() {
        try {
            Map<String, Object> systemInfo = new HashMap<>();
            
            // 系统运行时间（这里简化处理，实际可以从系统启动时间计算）
            systemInfo.put("uptime", "7天12小时");
            systemInfo.put("status", "running");
            systemInfo.put("version", "3.9.0");
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            
            // 内存使用情况
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            systemInfo.put("totalMemory", totalMemory);
            systemInfo.put("usedMemory", usedMemory);
            systemInfo.put("freeMemory", freeMemory);
            systemInfo.put("maxMemory", maxMemory);
            systemInfo.put("memoryUsagePercent", Math.round((double) usedMemory / maxMemory * 100));
            
            return AjaxResult.success(systemInfo);
        } catch (Exception e) {
            logger.error("获取系统信息失败", e);
            return AjaxResult.error("获取系统信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近活动
     */
    @PreAuthorize("@ss.hasPermi('dashboard:activities:view')")
    @GetMapping("/activities")
    @Log(title = "最近活动", businessType = BusinessType.OTHER)
    public AjaxResult getRecentActivities() {
        try {
            Map<String, Object> activities = new HashMap<>();
            
            // 这里可以添加获取最近活动的逻辑
            // 比如最近的邮件发送、回复、打开等记录
            
            // 暂时注释掉这些方法调用，因为服务不存在
            // activities.put("recentEmails", emailPersonalService.getRecentEmails(10));
            // activities.put("recentReplies", emailPersonalService.getRecentReplies(10));
            activities.put("recentEmails", new ArrayList<>());
            activities.put("recentReplies", new ArrayList<>());
            
            return AjaxResult.success(activities);
        } catch (Exception e) {
            logger.error("获取最近活动失败", e);
            return AjaxResult.error("获取最近活动失败: " + e.getMessage());
        }
    }

    /**
     * 获取邮件仪表板数据
     */
    @PreAuthorize("@ss.hasPermi('dashboard:email:data')")
    @GetMapping("/email/data")
    @Log(title = "邮件仪表板数据", businessType = BusinessType.OTHER)
    public AjaxResult getEmailDashboardData() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取今日发送邮件数
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long todaySent = emailTrackRecordService.countTodaySentEmails(today);
            data.put("todaySent", todaySent);
            
            // 获取总联系人数量
            long totalContacts = emailContactService.countTotalContacts();
            data.put("totalContacts", totalContacts);
            
            // 计算平均回复率
            long totalEmails = emailTrackRecordService.countTotalEmails();
            long totalReplied = emailTrackRecordService.countRepliedEmails();
            double avgReplyRate = totalEmails > 0 ? (double) totalReplied / totalEmails * 100 : 0;
            data.put("avgReplyRate", Math.round(avgReplyRate * 10.0) / 10.0);
            
            // 计算平均打开率
            long totalOpened = emailTrackRecordService.countTotalOpenedEmails();
            double avgOpenRate = totalEmails > 0 ? (double) totalOpened / totalEmails * 100 : 0;
            data.put("avgOpenRate", Math.round(avgOpenRate * 10.0) / 10.0);
            
            // 获取总发送数
            data.put("totalSent", totalEmails);
            
            return AjaxResult.success(data);
        } catch (Exception e) {
            logger.error("获取邮件仪表板数据失败", e);
            return AjaxResult.error("获取邮件仪表板数据失败: " + e.getMessage());
        }
    }
}


