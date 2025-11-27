package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.ImapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.Store;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮件连接诊断控制器
 * 用于诊断和修复IMAP连接问题
 */
@RestController
@RequestMapping("/email/diagnostic")
public class EmailConnectionDiagnosticController extends BaseController {

    @Autowired
    private IEmailAccountService emailAccountService;

    @Autowired
    private ImapService imapService;

    /**
     * 诊断所有邮箱账号的连接状态
     */
    @GetMapping("/checkAllConnections")
    @Log(title = "邮箱连接诊断", businessType = BusinessType.OTHER)
    public AjaxResult checkAllConnections() {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            List<Map<String, Object>> results = new ArrayList<>();

            for (EmailAccount account : accounts) {
                Map<String, Object> result = new HashMap<>();
                result.put("accountId", account.getAccountId());
                result.put("emailAddress", account.getEmailAddress());
                result.put("accountName", account.getAccountName());
                
                try {
                    // 测试IMAP连接
                    Store store = imapService.createPersistentConnection(account);
                    if (store != null && store.isConnected()) {
                        result.put("imapStatus", "连接成功");
                        result.put("imapConnected", true);
                        store.close();
                    } else {
                        result.put("imapStatus", "连接失败");
                        result.put("imapConnected", false);
                    }
                } catch (Exception e) {
                    result.put("imapStatus", "连接异常: " + e.getMessage());
                    result.put("imapConnected", false);
                    result.put("error", e.getMessage());
                }
                
                results.add(result);
            }

            return AjaxResult.success("连接诊断完成", results);
        } catch (Exception e) {
            logger.error("诊断邮箱连接时发生异常", e);
            return AjaxResult.error("诊断失败: " + e.getMessage());
        }
    }

    /**
     * 诊断指定账号的连接状态
     */
    @GetMapping("/checkConnection/{accountId}")
    @Log(title = "邮箱连接诊断", businessType = BusinessType.OTHER)
    public AjaxResult checkConnection(@PathVariable("accountId") Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return AjaxResult.error("账号不存在");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("accountId", account.getAccountId());
            result.put("emailAddress", account.getEmailAddress());
            result.put("accountName", account.getAccountName());
            result.put("imapHost", account.getImapHost());
            result.put("imapPort", account.getImapPort());
            result.put("imapSsl", account.getImapSsl());
            
            try {
                // 测试IMAP连接
                Store store = imapService.createPersistentConnection(account);
                if (store != null && store.isConnected()) {
                    result.put("imapStatus", "连接成功");
                    result.put("imapConnected", true);
                    store.close();
                } else {
                    result.put("imapStatus", "连接失败");
                    result.put("imapConnected", false);
                }
            } catch (Exception e) {
                result.put("imapStatus", "连接异常: " + e.getMessage());
                result.put("imapConnected", false);
                result.put("error", e.getMessage());
            }

            return AjaxResult.success("连接诊断完成", result);
        } catch (Exception e) {
            logger.error("诊断邮箱连接时发生异常", e);
            return AjaxResult.error("诊断失败: " + e.getMessage());
        }
    }

    /**
     * 获取连接诊断建议
     */
    @GetMapping("/getDiagnosticSuggestions")
    public AjaxResult getDiagnosticSuggestions() {
        Map<String, Object> suggestions = new HashMap<>();
        
        List<String> commonIssues = new ArrayList<>();
        commonIssues.add("1. 检查邮箱账号和密码是否正确");
        commonIssues.add("2. 确认IMAP服务已启用（QQ邮箱需要在设置中开启IMAP服务）");
        commonIssues.add("3. 检查IMAP服务器地址和端口是否正确");
        commonIssues.add("4. 确认SSL设置是否正确");
        commonIssues.add("5. 检查网络连接是否正常");
        commonIssues.add("6. 确认邮箱服务商是否限制了登录频率");
        
        Map<String, String> imapSettings = new HashMap<>();
        imapSettings.put("QQ邮箱", "IMAP服务器: imap.qq.com, 端口: 993, SSL: 是");
        imapSettings.put("163邮箱", "IMAP服务器: imap.163.com, 端口: 993, SSL: 是");
        imapSettings.put("Gmail", "IMAP服务器: imap.gmail.com, 端口: 993, SSL: 是");
        imapSettings.put("Outlook", "IMAP服务器: outlook.office365.com, 端口: 993, SSL: 是");
        
        suggestions.put("commonIssues", commonIssues);
        suggestions.put("imapSettings", imapSettings);
        
        return AjaxResult.success("诊断建议", suggestions);
    }
}










