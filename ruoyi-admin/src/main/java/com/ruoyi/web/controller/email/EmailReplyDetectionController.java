package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.task.EmailReplyDetectionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邮件回复检测配置Controller
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@RestController
@RequestMapping("/email/reply-detection")
public class EmailReplyDetectionController extends BaseController
{
    @Autowired
    private ISysConfigService configService;
    
    @Autowired
    private EmailReplyDetectionTask emailReplyDetectionTask;
    
    /**
     * 获取邮件回复检测配置列表
     */
    @PreAuthorize("@ss.hasPermi('email:reply:config')")
    @GetMapping("/config")
    public TableDataInfo getConfig()
    {
        startPage();
        List<SysConfig> list = configService.selectConfigList(new SysConfig());
        return getDataTable(list);
    }
    
    /**
     * 获取邮件回复检测配置详情
     */
    @PreAuthorize("@ss.hasPermi('email:reply:config')")
    @GetMapping("/config/{configId}")
    public AjaxResult getConfigInfo(@PathVariable("configId") Long configId)
    {
        return success(configService.selectConfigById(configId));
    }
    
    /**
     * 更新邮件回复检测配置
     */
    @PreAuthorize("@ss.hasPermi('email:reply:config')")
    @Log(title = "邮件回复检测配置", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public AjaxResult updateConfig(@RequestBody SysConfig config)
    {
        try {
            int result = configService.updateConfig(config);
            if (result > 0) {
                // 如果是检测间隔配置，重新启动定时任务
                if ("email.reply.detection.interval".equals(config.getConfigKey()) || 
                    "email.reply.full.detection.interval".equals(config.getConfigKey())) {
                    emailReplyDetectionTask.restartScheduledTasks();
                }
                return success("配置更新成功");
            } else {
                return error("配置更新失败");
            }
        } catch (Exception e) {
            return error("配置更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发回复检测
     */
    @PreAuthorize("@ss.hasPermi('email:reply:detect')")
    @Log(title = "邮件回复检测", businessType = BusinessType.OTHER)
    @PostMapping("/manual-detect")
    public AjaxResult manualDetect()
    {
        try {
            emailReplyDetectionTask.detectReplyEmails();
            return success("手动回复检测已触发，请查看日志了解详细结果");
        } catch (Exception e) {
            return error("手动回复检测失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发全量回复检测
     */
    @PreAuthorize("@ss.hasPermi('email:reply:detect')")
    @Log(title = "邮件回复检测", businessType = BusinessType.OTHER)
    @PostMapping("/manual-full-detect")
    public AjaxResult manualFullDetect()
    {
        try {
            emailReplyDetectionTask.fullReplyDetection();
            return success("手动全量回复检测已触发，请查看日志了解详细结果");
        } catch (Exception e) {
            return error("手动全量回复检测失败: " + e.getMessage());
        }
    }
    
    /**
     * 重启定时任务
     */
    @PreAuthorize("@ss.hasPermi('email:reply:config')")
    @Log(title = "邮件回复检测", businessType = BusinessType.OTHER)
    @PostMapping("/restart")
    public AjaxResult restartTasks()
    {
        try {
            emailReplyDetectionTask.restartScheduledTasks();
            return success("定时任务已重启");
        } catch (Exception e) {
            return error("重启定时任务失败: " + e.getMessage());
        }
    }
}
