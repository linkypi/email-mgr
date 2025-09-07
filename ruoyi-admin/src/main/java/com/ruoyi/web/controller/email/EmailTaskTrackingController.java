package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.EmailListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务邮件跟踪控制器
 * 提供基于任务ID的邮件跟踪功能
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/email/task-tracking")
public class EmailTaskTrackingController extends BaseController {
    
    @Autowired
    private EmailListener emailListener;
    
    /**
     * 根据任务ID获取邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:tracking:view')")
    @GetMapping("/records/{taskId}")
    public AjaxResult getEmailTrackRecordsByTaskId(@PathVariable("taskId") Long taskId) {
        try {
            List<EmailTrackRecord> records = emailListener.getEmailTrackRecordsByTaskId(taskId);
            return success(records);
        } catch (Exception e) {
            return error("获取任务邮件跟踪记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据任务ID获取邮件统计
     */
    @PreAuthorize("@ss.hasPermi('email:tracking:view')")
    @GetMapping("/statistics/{taskId}")
    public AjaxResult getTaskEmailStatistics(@PathVariable("taskId") Long taskId) {
        try {
            Map<String, Object> statistics = emailListener.getTaskEmailStatistics(taskId);
            return success(statistics);
        } catch (Exception e) {
            return error("获取任务邮件统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据任务ID批量更新邮件状态
     */
    @PreAuthorize("@ss.hasPermi('email:tracking:edit')")
    @Log(title = "任务邮件跟踪", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{taskId}")
    public AjaxResult updateEmailStatusByTaskId(@PathVariable("taskId") Long taskId, 
                                               @RequestParam("status") String status) {
        try {
            int updateCount = emailListener.updateEmailStatusByTaskId(taskId, status);
            return success("批量更新完成，共更新 " + updateCount + " 条记录");
        } catch (Exception e) {
            return error("批量更新任务邮件状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取任务邮件跟踪概览
     */
    @PreAuthorize("@ss.hasPermi('email:tracking:view')")
    @GetMapping("/overview/{taskId}")
    public AjaxResult getTaskTrackingOverview(@PathVariable("taskId") Long taskId) {
        try {
            // 获取邮件跟踪记录
            List<EmailTrackRecord> records = emailListener.getEmailTrackRecordsByTaskId(taskId);
            
            // 获取统计信息
            Map<String, Object> statistics = emailListener.getTaskEmailStatistics(taskId);
            
            // 构建概览数据
            Map<String, Object> overview = new java.util.HashMap<>();
            overview.put("taskId", taskId);
            overview.put("totalRecords", records.size());
            overview.put("statistics", statistics);
            overview.put("recentRecords", records.size() > 10 ? records.subList(0, 10) : records);
            
            return success(overview);
        } catch (Exception e) {
            return error("获取任务邮件跟踪概览失败：" + e.getMessage());
        }
    }
}
