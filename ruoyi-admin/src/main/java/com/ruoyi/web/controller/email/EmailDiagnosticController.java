package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.ImapService;
import com.ruoyi.system.service.email.EmailServiceMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 邮件诊断控制器
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@RestController
@RequestMapping("/email/diagnostic")
public class EmailDiagnosticController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(EmailDiagnosticController.class);

    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private ImapService imapService;
    
    @Autowired
    private EmailServiceMonitorService emailServiceMonitorService;

    /**
     * 诊断所有邮箱账户的IMAP连接状态
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @GetMapping("/imap/status")
    public AjaxResult diagnoseImapStatus()
    {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            Map<String, Object> results = new HashMap<>();
            
            for (EmailAccount account : accounts) {
                Map<String, Object> accountResult = new HashMap<>();
                accountResult.put("accountId", account.getAccountId());
                accountResult.put("emailAddress", account.getEmailAddress());
                accountResult.put("imapHost", account.getImapHost());
                accountResult.put("imapPort", account.getImapPort());
                accountResult.put("imapSsl", account.getImapSsl());
                accountResult.put("imapUsername", account.getImapUsername());
                
                // 测试IMAP连接
                try {
                    ImapService.ImapTestResult testResult = imapService.testImapConnection(account);
                    accountResult.put("connectionStatus", testResult.isSuccess() ? "成功" : "失败");
                    accountResult.put("message", testResult.getMessage());
                    accountResult.put("connectionTime", testResult.getConnectionTime());
                } catch (Exception e) {
                    accountResult.put("connectionStatus", "异常");
                    accountResult.put("message", "连接测试异常: " + e.getMessage());
                    accountResult.put("connectionTime", 0);
                }
                
                // 检查监控状态
                try {
                    boolean isMonitoring = emailServiceMonitorService.isAccountMonitoring(account.getAccountId());
                    accountResult.put("monitoringStatus", isMonitoring ? "监控中" : "未监控");
                } catch (Exception e) {
                    accountResult.put("monitoringStatus", "状态未知");
                }
                
                results.put(account.getEmailAddress(), accountResult);
            }
            
            return success(results);
        } catch (Exception e) {
            logger.error("诊断IMAP状态失败", e);
            return error("诊断失败: " + e.getMessage());
        }
    }

    /**
     * 测试单个邮箱账户的IMAP连接
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @PostMapping("/imap/test/{accountId}")
    public AjaxResult testImapConnection(@PathVariable("accountId") Long accountId)
    {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return error("邮箱账户不存在");
            }
            
            ImapService.ImapTestResult result = imapService.testImapConnection(account);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accountId", accountId);
            response.put("emailAddress", account.getEmailAddress());
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("connectionTime", result.getConnectionTime());
            response.put("errorMessage", result.getErrorMessage());
            
            return success(response);
        } catch (Exception e) {
            logger.error("测试IMAP连接失败", e);
            return error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 获取邮箱配置建议
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @GetMapping("/config/suggestions")
    public AjaxResult getConfigSuggestions()
    {
        Map<String, Object> suggestions = new HashMap<>();
        
        // QQ邮箱配置建议
        Map<String, Object> qqConfig = new HashMap<>();
        qqConfig.put("imapHost", "imap.qq.com");
        qqConfig.put("imapPort", 993);
        qqConfig.put("imapSsl", "1");
        qqConfig.put("imapUsername", "邮箱地址");
        qqConfig.put("imapPassword", "授权码（不是登录密码）");
        qqConfig.put("setupSteps", new String[]{
            "1. 登录QQ邮箱网页版",
            "2. 进入设置 -> 账户",
            "3. 开启IMAP/SMTP服务",
            "4. 生成授权码",
            "5. 使用授权码作为IMAP密码"
        });
        suggestions.put("qq.com", qqConfig);
        
        // Gmail配置建议
        Map<String, Object> gmailConfig = new HashMap<>();
        gmailConfig.put("imapHost", "imap.gmail.com");
        gmailConfig.put("imapPort", 993);
        gmailConfig.put("imapSsl", "1");
        gmailConfig.put("imapUsername", "邮箱地址");
        gmailConfig.put("imapPassword", "应用专用密码");
        gmailConfig.put("setupSteps", new String[]{
            "1. 登录Google账户",
            "2. 进入安全设置",
            "3. 开启两步验证",
            "4. 生成应用专用密码",
            "5. 使用应用专用密码作为IMAP密码"
        });
        suggestions.put("gmail.com", gmailConfig);
        
        // 163邮箱配置建议
        Map<String, Object> neteaseConfig = new HashMap<>();
        neteaseConfig.put("imapHost", "imap.163.com");
        neteaseConfig.put("imapPort", 993);
        neteaseConfig.put("imapSsl", "1");
        neteaseConfig.put("imapUsername", "邮箱地址");
        neteaseConfig.put("imapPassword", "授权码");
        neteaseConfig.put("setupSteps", new String[]{
            "1. 登录163邮箱网页版",
            "2. 进入设置 -> POP3/SMTP/IMAP",
            "3. 开启IMAP服务",
            "4. 生成授权码",
            "5. 使用授权码作为IMAP密码"
        });
        suggestions.put("163.com", neteaseConfig);
        
        return success(suggestions);
    }

    /**
     * 手动触发回复检测
     */
    @PreAuthorize("@ss.hasPermi('email:account:edit')")
    @PostMapping("/reply/scan/{accountId}")
    public AjaxResult manualReplyScan(@PathVariable("accountId") Long accountId)
    {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return error("邮箱账户不存在");
            }
            
            // 手动触发回复检测（功能待实现）
            // emailServiceMonitorService.manualReplyDetection(accountId);
            
            return success("手动回复检测功能待实现");
        } catch (Exception e) {
            logger.error("手动回复检测失败", e);
            return error("检测失败: " + e.getMessage());
        }
    }

    /**
     * 获取回复检测统计
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @GetMapping("/reply/statistics")
    public AjaxResult getReplyStatistics()
    {
        try {
            // 这里可以添加回复检测的统计信息
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalAccounts", emailAccountService.selectEmailAccountList(new EmailAccount()).size());
            statistics.put("monitoringAccounts", 0); // 需要实现统计逻辑
            statistics.put("lastScanTime", new java.util.Date());
            
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取回复统计失败", e);
            return error("获取统计失败: " + e.getMessage());
        }
    }
}
