package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
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
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * 163邮箱发送测试控制器
 * 严格按照Simple163EmailTest的成功配置
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/email/163-test")
public class Email163TestController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(Email163TestController.class);
    
    @Autowired
    private IEmailAccountService emailAccountService;
    
    /**
     * 严格按照Simple163EmailTest发送163邮箱测试邮件
     */
    @PreAuthorize("@ss.hasPermi('email:account:edit')")
    @PostMapping("/send-test/{accountId}")
    @Log(title = "163邮箱发送测试", businessType = BusinessType.OTHER)
    public AjaxResult sendTestEmail(@PathVariable("accountId") Long accountId,
                                   @RequestParam(defaultValue = "182867664@qq.com") String recipient) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return AjaxResult.error("邮箱账户不存在");
            }
            
            logger.info("=== 开始163邮箱发送测试 ===");
            logger.info("发件人: {}", account.getEmailAddress());
            logger.info("收件人: {}", recipient);
            logger.info("SMTP服务器: {}:{}", account.getSmtpHost(), account.getSmtpPort());
            
            // 严格按照Simple163EmailTest的配置
            Properties props = new Properties();
            props.put("mail.smtp.host", account.getSmtpHost());
            props.put("mail.smtp.port", account.getSmtpPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(account.getSmtpPort()));
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.auth.plain.disable", "false");
            props.put("mail.smtp.auth.login.disable", "false");
            
            // 启用调试模式查看详细日志
            props.put("mail.debug", "true");
            props.put("mail.smtp.debug", "true");
            
            // 创建邮件会话 - 严格按照Simple163EmailTest
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account.getEmailAddress(), account.getPassword());
                }
            });
            
            // 创建邮件 - 严格按照Simple163EmailTest
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(account.getEmailAddress()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            
            String subject = "163邮箱测试邮件 - " + new Date();
            String content = "这是一封来自163邮箱的测试邮件。\n\n" +
                           "测试时间: " + new Date() + "\n" +
                           "发件人: " + account.getEmailAddress() + "\n" +
                           "收件人: " + recipient + "\n" +
                           "测试ID: " + UUID.randomUUID().toString() + "\n\n" +
                           "如果您收到这封邮件，说明163邮箱SMTP配置正确！";
            
            message.setSubject(subject);
            message.setText(content, "UTF-8");
            message.setSentDate(new Date());
            message.setHeader("Message-ID", "<test-" + System.currentTimeMillis() + "@163.com>");
            
            // 发送邮件 - 严格按照Simple163EmailTest
            Transport transport = session.getTransport("smtp");
            logger.info("正在连接SMTP服务器...");
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            logger.info("SMTP连接成功，开始发送邮件...");
            
            transport.sendMessage(message, message.getAllRecipients());
            logger.info("邮件发送成功！");
            
            transport.close();
            logger.info("SMTP连接已关闭");
            
            Map<String, Object> result = new HashMap<>();
            result.put("accountId", accountId);
            result.put("emailAddress", account.getEmailAddress());
            result.put("recipient", recipient);
            result.put("subject", subject);
            result.put("sendTime", new Date());
            result.put("status", "成功");
            result.put("message", "严格按照Simple163EmailTest配置发送成功");
            
            return AjaxResult.success("163邮箱测试邮件发送成功", result);
            
        } catch (Exception e) {
            logger.error("163邮箱发送测试失败", e);
            return AjaxResult.error("发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试163邮箱连接
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @PostMapping("/test-connection/{accountId}")
    @Log(title = "163邮箱连接测试", businessType = BusinessType.OTHER)
    public AjaxResult testConnection(@PathVariable("accountId") Long accountId) {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return AjaxResult.error("邮箱账户不存在");
            }
            
            logger.info("=== 开始163邮箱连接测试 ===");
            
            // 严格按照Simple163EmailTest的配置
            Properties props = new Properties();
            props.put("mail.smtp.host", account.getSmtpHost());
            props.put("mail.smtp.port", account.getSmtpPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(account.getSmtpPort()));
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.auth.plain.disable", "false");
            props.put("mail.smtp.auth.login.disable", "false");
            
            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            
            logger.info("正在测试SMTP连接...");
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            
            boolean connected = transport.isConnected();
            logger.info("连接状态: {}", connected ? "成功" : "失败");
            
            transport.close();
            
            Map<String, Object> result = new HashMap<>();
            result.put("accountId", accountId);
            result.put("emailAddress", account.getEmailAddress());
            result.put("smtpHost", account.getSmtpHost());
            result.put("smtpPort", account.getSmtpPort());
            result.put("connectionStatus", connected ? "成功" : "失败");
            result.put("testTime", new Date());
            
            if (connected) {
                return AjaxResult.success("163邮箱连接测试成功", result);
            } else {
                return AjaxResult.error("163邮箱连接测试失败", result);
            }
            
        } catch (Exception e) {
            logger.error("163邮箱连接测试异常", e);
            return AjaxResult.error("连接测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取163邮箱配置建议
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @GetMapping("/config-suggestions")
    public AjaxResult getConfigSuggestions() {
        Map<String, Object> suggestions = new HashMap<>();
        
        suggestions.put("smtpHost", "smtp.163.com");
        suggestions.put("smtpPort", 465);
        suggestions.put("smtpSsl", "1");
        suggestions.put("authRequired", true);
        suggestions.put("passwordType", "授权码（不是登录密码）");
        
        suggestions.put("configSteps", new String[]{
            "1. 登录163邮箱网页版",
            "2. 进入设置 -> POP3/SMTP/IMAP",
            "3. 开启SMTP服务",
            "4. 生成授权码",
            "5. 使用授权码作为密码"
        });
        
        suggestions.put("keySettings", new String[]{
            "mail.smtp.ssl.enable = true",
            "mail.smtp.auth.plain.disable = false",
            "mail.smtp.auth.login.disable = false",
            "mail.smtp.ssl.trust = *"
        });
        
        return AjaxResult.success("163邮箱配置建议", suggestions);
    }
}






