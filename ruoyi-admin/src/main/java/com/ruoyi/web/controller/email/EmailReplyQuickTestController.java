package com.ruoyi.web.controller.email;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮件回复检测快速测试控制器
 * 用于快速诊断回复检测问题
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@RestController
@RequestMapping("/email/reply/quick-test")
public class EmailReplyQuickTestController
{
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private EmailListener emailListener;
    
    @Autowired
    private ISysConfigService configService;

    /**
     * 快速诊断所有问题
     */
    @GetMapping("/diagnose")
    public AjaxResult quickDiagnose()
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 检查配置
            Map<String, String> configs = new HashMap<>();
            configs.put("detectionEnabled", configService.selectConfigByKey("email.reply.detection.enabled"));
            configs.put("detectionInterval", configService.selectConfigByKey("email.reply.detection.interval"));
            configs.put("fullDetectionEnabled", configService.selectConfigByKey("email.reply.full.detection.enabled"));
            configs.put("fullDetectionInterval", configService.selectConfigByKey("email.reply.full.detection.interval"));
            result.put("configs", configs);
            
            // 2. 检查账户
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            result.put("accountCount", accounts.size());
            result.put("trackingEnabledCount", accounts.stream()
                .filter(acc -> "1".equals(acc.getTrackingEnabled()))
                .count());
            
            // 3. 测试第一个账户的回复检测
            if (!accounts.isEmpty()) {
                EmailAccount firstAccount = accounts.get(0);
                try {
                    int replyCount = emailListener.detectReplyEmailsForAccount(firstAccount.getAccountId());
                    result.put("testResult", "成功检测到 " + replyCount + " 个回复");
                } catch (Exception e) {
                    result.put("testResult", "检测失败: " + e.getMessage());
                }
            }
            
            return AjaxResult.success("诊断完成", result);
            
        } catch (Exception e) {
            return AjaxResult.error("诊断失败: " + e.getMessage());
        }
    }

    /**
     * 测试指定账户
     */
    @PostMapping("/test-account/{accountId}")
    public AjaxResult testAccount(@PathVariable("accountId") Long accountId)
    {
        try {
            int replyCount = emailListener.detectReplyEmailsForAccount(accountId);
            return AjaxResult.success("账户 " + accountId + " 检测完成，发现 " + replyCount + " 个回复");
        } catch (Exception e) {
            return AjaxResult.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有账户信息
     */
    @GetMapping("/accounts")
    public AjaxResult getAccounts()
    {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            return AjaxResult.success(accounts);
        } catch (Exception e) {
            return AjaxResult.error("获取账户信息失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发全量检测
     */
    @PostMapping("/full-detect")
    public AjaxResult fullDetect()
    {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            int totalReplies = 0;
            
            for (EmailAccount account : accounts) {
                if ("1".equals(account.getTrackingEnabled())) {
                    try {
                        int replyCount = emailListener.performFullReplyDetection(account.getAccountId());
                        totalReplies += replyCount;
                    } catch (Exception e) {
                        // 记录错误但继续处理其他账户
                    }
                }
            }
            
            return AjaxResult.success("全量检测完成，共发现 " + totalReplies + " 个回复");
        } catch (Exception e) {
            return AjaxResult.error("全量检测失败: " + e.getMessage());
        }
    }
}










