package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.SmtpService;
import com.ruoyi.system.utils.EmailConfigFixUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件配置测试控制器
 * 用于测试和验证邮件配置修复效果
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/email/config-test")
public class EmailConfigTestController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailConfigTestController.class);
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private SmtpService smtpService;
    
    /**
     * 测试163邮箱配置修复
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @PostMapping("/test163/{accountId}")
    @Log(title = "163邮箱配置测试", businessType = BusinessType.OTHER)
    public AjaxResult test163Config(@PathVariable("accountId") Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return AjaxResult.error("邮箱账户不存在");
            }
            
            // 检查配置
            String configCheck = EmailConfigFixUtil.checkEmailAccountConfig(account);
            logger.info("配置检查结果: {}", configCheck);
            
            // 使用修复后的配置测试连接
            Properties fixedProps = EmailConfigFixUtil.createFixedSmtpProperties(account);
            
            Session session = Session.getInstance(fixedProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account.getEmailAddress(), account.getPassword());
                }
            });
            
            // 测试连接
            Transport transport = session.getTransport("smtp");
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            
            boolean connected = transport.isConnected();
            transport.close();
            
            Map<String, Object> result = new HashMap<>();
            result.put("accountId", accountId);
            result.put("emailAddress", account.getEmailAddress());
            result.put("smtpHost", account.getSmtpHost());
            result.put("smtpPort", account.getSmtpPort());
            result.put("configCheck", configCheck);
            result.put("connectionTest", connected ? "成功" : "失败");
            result.put("suggestions", EmailConfigFixUtil.getEmailProviderSuggestions(account.getEmailAddress()));
            
            if (connected) {
                return AjaxResult.success("163邮箱配置测试成功", result);
            } else {
                return AjaxResult.error("163邮箱配置测试失败", result);
            }
            
        } catch (Exception e) {
            logger.error("163邮箱配置测试异常", e);
            return AjaxResult.error("测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送测试邮件验证修复效果
     */
    @PreAuthorize("@ss.hasPermi('email:account:edit')")
    @PostMapping("/send-test/{accountId}")
    @Log(title = "发送测试邮件", businessType = BusinessType.OTHER)
    public AjaxResult sendTestEmail(@PathVariable("accountId") Long accountId, 
                                   @RequestParam(defaultValue = "182867664@qq.com") String recipient) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return AjaxResult.error("邮箱账户不存在");
            }
            
            // 使用修复后的配置
            Properties fixedProps = EmailConfigFixUtil.createFixedSmtpProperties(account);
            
            Session session = Session.getInstance(fixedProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account.getEmailAddress(), account.getPassword());
                }
            });
            
            // 创建测试邮件
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(account.getEmailAddress()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            
            String subject = "配置修复测试邮件 - " + new Date();
            String content = "这是一封用于验证配置修复效果的测试邮件。\n\n" +
                           "发送时间: " + new Date() + "\n" +
                           "发件人: " + account.getEmailAddress() + "\n" +
                           "收件人: " + recipient + "\n" +
                           "SMTP服务器: " + account.getSmtpHost() + ":" + account.getSmtpPort() + "\n\n" +
                           "如果您收到这封邮件，说明535错误已修复！";
            
            message.setSubject(subject);
            message.setText(content, "UTF-8");
            message.setSentDate(new Date());
            message.setHeader("Message-ID", "<config-fix-test-" + System.currentTimeMillis() + "@" + 
                account.getSmtpHost().replace("smtp.", ""));
            
            // 发送邮件
            Transport transport = session.getTransport("smtp");
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
            Map<String, Object> result = new HashMap<>();
            result.put("accountId", accountId);
            result.put("emailAddress", account.getEmailAddress());
            result.put("recipient", recipient);
            result.put("subject", subject);
            result.put("sendTime", new Date());
            result.put("status", "成功");
            
            return AjaxResult.success("测试邮件发送成功", result);
            
        } catch (Exception e) {
            logger.error("发送测试邮件失败", e);
            return AjaxResult.error("发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有邮箱账户的配置检查结果
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @GetMapping("/check-all")
    @Log(title = "检查所有邮箱配置", businessType = BusinessType.OTHER)
    public AjaxResult checkAllConfigs() {
        try {
            List<EmailAccount> accounts = emailAccountService.selectEmailAccountList(new EmailAccount());
            Map<String, Object> results = new HashMap<>();
            
            for (EmailAccount account : accounts) {
                Map<String, Object> accountResult = new HashMap<>();
                accountResult.put("accountId", account.getAccountId());
                accountResult.put("emailAddress", account.getEmailAddress());
                accountResult.put("smtpHost", account.getSmtpHost());
                accountResult.put("smtpPort", account.getSmtpPort());
                accountResult.put("smtpSsl", account.getSmtpSsl());
                
                // 检查配置
                String configCheck = EmailConfigFixUtil.checkEmailAccountConfig(account);
                accountResult.put("configCheck", configCheck);
                accountResult.put("suggestions", EmailConfigFixUtil.getEmailProviderSuggestions(account.getEmailAddress()));
                
                // 测试连接
                try {
                    SmtpService.SmtpTestResult testResult = smtpService.testSmtpConnection(account);
                    accountResult.put("connectionTest", testResult.isSuccess() ? "成功" : "失败");
                    accountResult.put("connectionMessage", testResult.getMessage());
                    accountResult.put("connectionTime", testResult.getConnectionTime());
                } catch (Exception e) {
                    accountResult.put("connectionTest", "异常");
                    accountResult.put("connectionMessage", e.getMessage());
                }
                
                results.put(account.getEmailAddress(), accountResult);
            }
            
            return AjaxResult.success("配置检查完成", results);
            
        } catch (Exception e) {
            logger.error("检查所有邮箱配置失败", e);
            return AjaxResult.error("检查失败: " + e.getMessage());
        }
    }
}






