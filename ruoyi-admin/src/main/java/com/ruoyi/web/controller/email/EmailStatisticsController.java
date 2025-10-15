package com.ruoyi.web.controller.email;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.email.IEmailTrackRecordService;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.domain.email.EmailTrackRecord;

/**
 * 邮件统计Controller
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
@RestController
@RequestMapping("/email/statistics")
public class EmailStatisticsController extends BaseController
{
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;

    @Autowired
    private IEmailAccountService emailAccountService;

    /**
     * 获取邮件跟踪统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:track')")
    @GetMapping("/track")
    public AjaxResult getTrackStatistics()
    {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取总邮件数
            long totalEmails = emailTrackRecordService.countTotalEmails();
            statistics.put("totalEmails", totalEmails);
            
            // 获取今日发送邮件数
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long todaySent = emailTrackRecordService.countTodaySentEmails(today);
            statistics.put("todaySent", todaySent);
            
            // 获取回复邮件数
            long repliedEmails = emailTrackRecordService.countRepliedEmails();
            statistics.put("repliedEmails", repliedEmails);
            
            // 获取今日打开邮件数
            long todayOpened = emailTrackRecordService.countTodayOpenedEmails(today);
            statistics.put("todayOpened", todayOpened);
            
            // 获取总打开邮件数
            long totalOpened = emailTrackRecordService.countTotalOpenedEmails();
            statistics.put("totalOpened", totalOpened);
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取邮件跟踪统计失败", e);
            return error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取邮件计数统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:count')")
    @GetMapping("/count")
    public AjaxResult getCountStatistics()
    {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取总邮件数
            long totalEmails = emailTrackRecordService.countTotalEmails();
            statistics.put("total", totalEmails);
            
            // 获取今日发送邮件数
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long todaySent = emailTrackRecordService.countTodaySentEmails(today);
            statistics.put("today", todaySent);
            
            // 获取回复邮件数
            long repliedEmails = emailTrackRecordService.countRepliedEmails();
            statistics.put("replied", repliedEmails);
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取邮件计数统计失败", e);
            return error("获取计数统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:today')")
    @GetMapping("/today")
    public AjaxResult getTodayStatistics()
    {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 获取今日发送邮件数
            long todaySent = emailTrackRecordService.countTodaySentEmails(today);
            statistics.put("sent", todaySent);
            
            // 获取今日送达邮件数
            long todayDelivered = emailTrackRecordService.countTodayDeliveredEmails(today);
            statistics.put("delivered", todayDelivered);
            
            // 获取今日打开邮件数
            long todayOpened = emailTrackRecordService.countTodayOpenedEmails(today);
            statistics.put("opened", todayOpened);
            
            // 获取今日点击邮件数
            long todayClicked = emailTrackRecordService.countTodayClickedEmails(today);
            statistics.put("clicked", todayClicked);
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取今日统计失败", e);
            return error("获取今日统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取总数统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:total')")
    @GetMapping("/total")
    public AjaxResult getTotalStatistics()
    {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取总邮件数
            long totalEmails = emailTrackRecordService.countTotalEmails();
            statistics.put("emails", totalEmails);
            
            // 获取总打开邮件数
            long totalOpened = emailTrackRecordService.countTotalOpenedEmails();
            statistics.put("opened", totalOpened);
            
            // 获取回复邮件数
            long repliedEmails = emailTrackRecordService.countRepliedEmails();
            statistics.put("replied", repliedEmails);
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取总数统计失败", e);
            return error("获取总数统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取趋势统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:trends')")
    @GetMapping("/trends")
    public AjaxResult getTrendsStatistics(@RequestParam(defaultValue = "7") int days)
    {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 计算日期范围
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 获取发送邮件趋势
            Map<String, Long> sentTrends = emailTrackRecordService.getSentEmailsByDateRange(startDateStr, endDateStr);
            statistics.put("sent", sentTrends);
            
            // 获取送达邮件趋势
            Map<String, Long> deliveredTrends = emailTrackRecordService.getDeliveredEmailsByDateRange(startDateStr, endDateStr);
            statistics.put("delivered", deliveredTrends);
            
            // 构建前端期望的数据格式
            List<String> dateLabels = new ArrayList<>();
            List<Long> sendData = new ArrayList<>();
            List<Long> receivedData = new ArrayList<>();
            
            // 生成日期标签和数据
            for (int i = 0; i < days; i++) {
                LocalDate date = startDate.plusDays(i);
                String dateLabel = date.format(DateTimeFormatter.ofPattern("MM-dd"));
                String dateKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                dateLabels.add(dateLabel);
                sendData.add(sentTrends.getOrDefault(dateKey, 0L));
                receivedData.add(deliveredTrends.getOrDefault(dateKey, 0L));
            }
            
            // 添加前端期望的字段
            statistics.put("dateLabels", dateLabels);
            statistics.put("sendData", sendData);
            statistics.put("receivedData", receivedData);
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取趋势统计失败", e);
            return error("获取趋势统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取回复率统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:reply-rates')")
    @GetMapping("/reply-rates")
    public AjaxResult getReplyRatesStatistics()
    {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取总邮件数和回复数
            long totalEmails = emailTrackRecordService.countTotalEmails();
            long repliedEmails = emailTrackRecordService.countRepliedEmails();
            
            // 计算回复率
            double replyRate = totalEmails > 0 ? (double) repliedEmails / totalEmails * 100 : 0;
            
            statistics.put("totalEmails", totalEmails);
            statistics.put("repliedEmails", repliedEmails);
            statistics.put("replyRate", Math.round(replyRate * 100.0) / 100.0);
            
            // 添加账号回复率数据（前端期望的格式）
            List<Map<String, Object>> accountReplyRates = new ArrayList<>();
            // 这里可以添加按账号分组的回复率数据
            // 暂时返回空数组，因为需要更复杂的查询逻辑
            statistics.put("accountReplyRates", accountReplyRates);
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取回复率统计失败", e);
            return error("获取回复率统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取账号统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:accounts')")
    @GetMapping("/accounts")
    public AjaxResult getAccountsStatistics()
    {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 这里可以添加账号相关的统计逻辑
            // 暂时返回空数据
            statistics.put("activeAccounts", 0);
            statistics.put("totalAccounts", 0);
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取账号统计失败", e);
            return error("获取账号统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取详细统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:detailed')")
    @GetMapping("/detailed")
    public AjaxResult getDetailedStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long accountId)
    {
        try {
            // 如果没有提供日期，默认使用最近7天
            if (startDate == null || endDate == null) {
                LocalDate end = LocalDate.now();
                LocalDate start = end.minusDays(6);
                startDate = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                endDate = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            List<Map<String, Object>> detailedStats = emailTrackRecordService.getDetailedStatistics(startDate, endDate, accountId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("statisticsList", detailedStats);
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("accountId", accountId);
            
            return success(result);
        } catch (Exception e) {
            logger.error("获取详细统计失败", e);
            return error("获取详细统计失败: " + e.getMessage());
        }
    }
}
