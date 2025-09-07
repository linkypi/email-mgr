package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.email.EmailTrackingStatsService;
import com.ruoyi.system.service.email.ImapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮件跟踪统计控制器
 * 用于首页统计显示
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/email/tracking-stats")
public class EmailTrackingStatsController extends BaseController {
    
    @Autowired
    private EmailListener emailListener;
    
    @Autowired
    private EmailTrackingStatsService emailTrackingStatsService;
    
    /**
     * 获取邮件跟踪统计概览
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/overview")
    public AjaxResult getTrackingOverview() {
        try {
            Map<Long, ImapService.EmailTrackingStats> allStats = emailListener.getAllEmailTrackingStats();
            
            // 计算总计
            int totalDsnCount = 0;
            int totalReplyCount = 0;
            int totalStatusChangeCount = 0;
            
            for (ImapService.EmailTrackingStats stats : allStats.values()) {
                totalDsnCount += stats.getDsnCount();
                totalReplyCount += stats.getReplyCount();
                totalStatusChangeCount += stats.getStatusChangeCount();
            }
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("totalAccounts", allStats.size());
            overview.put("totalDsnCount", totalDsnCount);
            overview.put("totalReplyCount", totalReplyCount);
            overview.put("totalStatusChangeCount", totalStatusChangeCount);
            overview.put("accountStats", allStats);
            
            return success(overview);
        } catch (Exception e) {
            return error("获取邮件跟踪统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取指定账号的邮件跟踪统计
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/account/{accountId}")
    public AjaxResult getAccountTrackingStats(@PathVariable("accountId") Long accountId) {
        try {
            ImapService.EmailTrackingStats stats = emailListener.getEmailTrackingStats(accountId);
            return success(stats);
        } catch (Exception e) {
            return error("获取账号邮件跟踪统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取邮件发送统计
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/send-stats")
    public AjaxResult getSendStats() {
        try {
            Map<String, Object> sendStats = emailTrackingStatsService.getSendStatsOverview();
            return success(sendStats);
        } catch (Exception e) {
            return error("获取邮件发送统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取实时监控状态
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/monitor-status")
    public AjaxResult getMonitorStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("isRunning", emailListener.isRunning());
            status.put("activeConnectionCount", emailListener.getActiveConnectionCount());
            status.put("allConnectionStatus", emailListener.getAllConnectionStatus());
            
            return success(status);
        } catch (Exception e) {
            return error("获取监控状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取邮件跟踪趋势数据
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/trends")
    public AjaxResult getTrackingTrends(@RequestParam(value = "days", defaultValue = "7") int days) {
        try {
            Map<String, Object> trends = emailTrackingStatsService.getSendTrends(days);
            return success(trends);
        } catch (Exception e) {
            return error("获取邮件跟踪趋势失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取邮件状态分布
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/status-distribution")
    public AjaxResult getStatusDistribution() {
        try {
            Map<String, Object> distribution = emailTrackingStatsService.getStatusDistribution();
            return success(distribution);
        } catch (Exception e) {
            return error("获取邮件状态分布失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取最近发送的邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/recent-emails")
    public AjaxResult getRecentEmails(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> recentEmails = emailTrackingStatsService.getRecentEmails(limit);
            return success(recentEmails);
        } catch (Exception e) {
            return error("获取最近发送的邮件列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取指定账号的详细统计
     */
    @PreAuthorize("@ss.hasPermi('email:stats:view')")
    @GetMapping("/account-detail/{accountId}")
    public AjaxResult getAccountDetailStats(@PathVariable("accountId") Long accountId) {
        try {
            Map<String, Object> accountStats = emailTrackingStatsService.getAccountStats(accountId);
            return success(accountStats);
        } catch (Exception e) {
            return error("获取账号详细统计失败：" + e.getMessage());
        }
    }
}
