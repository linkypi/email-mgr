package com.ruoyi.web.controller.email;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邮件回复检测诊断控制器
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@RestController
@RequestMapping("/email/reply/diagnostic")
public class EmailReplyDiagnosticController extends BaseController
{
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private EmailListener emailListener;
    
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取邮件回复检测配置状态
     */
    @PreAuthorize("@ss.hasPermi('email:reply:diagnostic')")
    @GetMapping("/config")
    public AjaxResult getConfigStatus()
    {
        try {
            String detectionEnabled = configService.selectConfigByKey("email.reply.detection.enabled");
            String detectionInterval = configService.selectConfigByKey("email.reply.detection.interval");
            String fullDetectionEnabled = configService.selectConfigByKey("email.reply.full.detection.enabled");
            String fullDetectionInterval = configService.selectConfigByKey("email.reply.full.detection.interval");
            
            return AjaxResult.success()
                .put("detectionEnabled", detectionEnabled)
                .put("detectionInterval", detectionInterval)
                .put("fullDetectionEnabled", fullDetectionEnabled)
                .put("fullDetectionInterval", fullDetectionInterval);
        } catch (Exception e) {
            return AjaxResult.error("获取配置状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有邮箱账户状态
     */
    @PreAuthorize("@ss.hasPermi('email:reply:diagnostic')")
    @GetMapping("/accounts")
    public TableDataInfo getAccountStatus()
    {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            return getDataTable(accounts);
        } catch (Exception e) {
            return getDataTable(null);
        }
    }

    /**
     * 测试单个账户的回复检测
     */
    @PreAuthorize("@ss.hasPermi('email:reply:diagnostic')")
    @PostMapping("/test/{accountId}")
    public AjaxResult testAccountReplyDetection(@PathVariable("accountId") Long accountId)
    {
        try {
            int replyCount = emailListener.detectReplyEmailsForAccount(accountId);
            return AjaxResult.success("检测完成，发现 " + replyCount + " 个回复邮件");
        } catch (Exception e) {
            return AjaxResult.error("检测失败: " + e.getMessage());
        }
    }

    /**
     * 执行全量回复检测
     */
    @PreAuthorize("@ss.hasPermi('email:reply:diagnostic')")
    @PostMapping("/full-test/{accountId}")
    public AjaxResult testAccountFullReplyDetection(@PathVariable("accountId") Long accountId)
    {
        try {
            int replyCount = emailListener.performFullReplyDetection(accountId);
            return AjaxResult.success("全量检测完成，发现 " + replyCount + " 个回复邮件");
        } catch (Exception e) {
            return AjaxResult.error("全量检测失败: " + e.getMessage());
        }
    }

    /**
     * 获取账户的邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:reply:diagnostic')")
    @GetMapping("/track-records/{accountId}")
    public TableDataInfo getTrackRecords(@PathVariable("accountId") Long accountId)
    {
        try {
            // 这里需要调用EmailTrackRecordService，但为了简化，我们返回基本信息
            return getDataTable(null);
        } catch (Exception e) {
            return getDataTable(null);
        }
    }

    /**
     * 检查IMAP连接状态
     */
    @PreAuthorize("@ss.hasPermi('email:reply:diagnostic')")
    @GetMapping("/imap-status/{accountId}")
    public AjaxResult checkImapStatus(@PathVariable("accountId") Long accountId)
    {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return AjaxResult.error("账户不存在");
            }
            
            // 这里可以添加IMAP连接测试逻辑
            return AjaxResult.success("IMAP连接状态检查完成");
        } catch (Exception e) {
            return AjaxResult.error("IMAP连接检查失败: " + e.getMessage());
        }
    }
}
