package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.email.IEmailAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邮件回复测试控制器
 * 用于测试邮件回复检测功能
 */
@RestController
@RequestMapping("/email/replyTest")
public class EmailReplyTestController extends BaseController {

    @Autowired
    private EmailListener emailListener;

    @Autowired
    private IEmailAccountService emailAccountService;

    /**
     * 获取所有邮箱账号
     */
    @GetMapping("/accounts")
    public AjaxResult getAccounts() {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            return AjaxResult.success("获取账号列表成功", accounts);
        } catch (Exception e) {
            logger.error("获取账号列表失败", e);
            return AjaxResult.error("获取账号列表失败: " + e.getMessage());
        }
    }

    /**
     * 手动检测所有账号的回复邮件
     */
    @PostMapping("/detectAllReplies")
    @Log(title = "手动检测回复邮件", businessType = BusinessType.OTHER)
    public AjaxResult detectAllReplies() {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            int successCount = 0;
            int failCount = 0;
            
            for (EmailAccount account : accounts) {
                try {
                    emailListener.detectReplyEmailsForAccount(account.getAccountId());
                    successCount++;
                    logger.info("成功检测账号 {} 的回复邮件", account.getEmailAddress());
                } catch (Exception e) {
                    failCount++;
                    logger.error("检测账号 {} 的回复邮件失败: {}", account.getEmailAddress(), e.getMessage());
                }
            }
            
            return AjaxResult.success(String.format("检测完成，成功: %d, 失败: %d", successCount, failCount));
        } catch (Exception e) {
            logger.error("手动检测回复邮件失败", e);
            return AjaxResult.error("检测失败: " + e.getMessage());
        }
    }

    /**
     * 手动检测指定账号的回复邮件
     */
    @PostMapping("/detectAccountReplies/{accountId}")
    @Log(title = "手动检测回复邮件", businessType = BusinessType.OTHER)
    public AjaxResult detectAccountReplies(@PathVariable("accountId") Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return AjaxResult.error("账号不存在");
            }
            
            emailListener.detectReplyEmailsForAccount(accountId);
            return AjaxResult.success("检测完成: " + account.getEmailAddress());
        } catch (Exception e) {
            logger.error("检测账号 {} 的回复邮件失败", accountId, e);
            return AjaxResult.error("检测失败: " + e.getMessage());
        }
    }
}