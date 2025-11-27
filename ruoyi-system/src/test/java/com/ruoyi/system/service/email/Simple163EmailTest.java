package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

/**
 * 163邮箱简单发送测试
 * 快速测试163邮箱的邮件发送功能
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class Simple163EmailTest {
    
    private static final Logger logger = LoggerFactory.getLogger(Simple163EmailTest.class);
    
    // 163邮箱测试配置
    private static final String TEST_EMAIL = "19147797833@163.com";
    private static final String TEST_PASSWORD = "MKsH2qnuE2qVFiBx";
    private static final String TEST_SMTP_HOST = "smtp.163.com";
    private static final int TEST_SMTP_PORT = 465;
    private static final String TEST_RECIPIENT = "182867664@qq.com";
    
    /**
     * 163邮箱快速发送测试
     */
    public static void testQuickSend() {
        logger.info("=== 开始163邮箱快速发送测试 ===");
        logger.info("发件人: {}", TEST_EMAIL);
        logger.info("收件人: {}", TEST_RECIPIENT);
        logger.info("SMTP服务器: {}:{}", TEST_SMTP_HOST, TEST_SMTP_PORT);
        
        try {
            // 1. 创建邮件配置
            Properties props = new Properties();
            props.put("mail.smtp.host", TEST_SMTP_HOST);
            props.put("mail.smtp.port", TEST_SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(TEST_SMTP_PORT));
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.auth.plain.disable", "false");
            props.put("mail.smtp.auth.login.disable", "false");
            
            // 启用调试模式查看详细日志
            props.put("mail.debug", "true");
            props.put("mail.smtp.debug", "true");
            
            // 2. 创建邮件会话
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(TEST_EMAIL, TEST_PASSWORD);
                }
            });
            
            // 3. 创建邮件
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(TEST_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TEST_RECIPIENT));
            
            String subject = "163邮箱测试邮件 - " + new Date();
            String content = "这是一封来自163邮箱的测试邮件。\n\n" +
                           "测试时间: " + new Date() + "\n" +
                           "发件人: " + TEST_EMAIL + "\n" +
                           "收件人: " + TEST_RECIPIENT + "\n" +
                           "测试ID: " + UUID.randomUUID().toString() + "\n\n" +
                           "如果您收到这封邮件，说明163邮箱SMTP配置正确！";
            
            message.setSubject(subject);
            message.setText(content, "UTF-8");
            message.setSentDate(new Date());
            message.setHeader("Message-ID", "<test-" + System.currentTimeMillis() + "@163.com>");
            
            // 4. 发送邮件
            Transport transport = session.getTransport("smtp");
            logger.info("正在连接SMTP服务器...");
            transport.connect(TEST_SMTP_HOST, TEST_SMTP_PORT, TEST_EMAIL, TEST_PASSWORD);
            logger.info("SMTP连接成功，开始发送邮件...");
            
            transport.sendMessage(message, message.getAllRecipients());
            logger.info("邮件发送成功！");
            
            transport.close();
            logger.info("SMTP连接已关闭");
            
        } catch (Exception e) {
            logger.error("163邮箱发送测试失败", e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
        
        logger.info("=== 163邮箱快速发送测试完成 ===");
    }
    
    /**
     * 163邮箱连接测试
     */
    public static void testConnection() {
        logger.info("=== 开始163邮箱连接测试 ===");
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", TEST_SMTP_HOST);
            props.put("mail.smtp.port", TEST_SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(TEST_SMTP_PORT));
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.auth.plain.disable", "false");
            props.put("mail.smtp.auth.login.disable", "false");
            
            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            
            logger.info("正在测试SMTP连接...");
            transport.connect(TEST_SMTP_HOST, TEST_SMTP_PORT, TEST_EMAIL, TEST_PASSWORD);
            
            boolean connected = transport.isConnected();
            logger.info("连接状态: {}", connected ? "成功" : "失败");
            
            if (connected) {
                logger.info("163邮箱SMTP连接测试成功！");
            } else {
                logger.error("163邮箱SMTP连接测试失败！");
            }
            
            transport.close();
            
        } catch (Exception e) {
            logger.error("163邮箱连接测试异常", e);
            throw new RuntimeException("连接测试失败: " + e.getMessage(), e);
        }
        
        logger.info("=== 163邮箱连接测试完成 ===");
    }
    
    /**
     * 163邮箱HTML邮件测试
     */
    public static void testHtmlEmail() {
        logger.info("=== 开始163邮箱HTML邮件测试 ===");
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", TEST_SMTP_HOST);
            props.put("mail.smtp.port", TEST_SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(TEST_SMTP_PORT));
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.auth.plain.disable", "false");
            props.put("mail.smtp.auth.login.disable", "false");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(TEST_EMAIL, TEST_PASSWORD);
                }
            });
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(TEST_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TEST_RECIPIENT));
            
            String subject = "163邮箱HTML测试邮件 - " + new Date();
            String htmlContent = "<html>" +
                               "<head><meta charset='UTF-8'></head>" +
                               "<body style='font-family: Arial, sans-serif;'>" +
                               "<h2 style='color: #0066cc;'>163邮箱HTML测试邮件</h2>" +
                               "<p>这是一封来自<strong>163邮箱</strong>的HTML格式测试邮件。</p>" +
                               "<div style='background-color: #f0f0f0; padding: 10px; border-left: 4px solid #0066cc;'>" +
                               "<p><strong>测试信息：</strong></p>" +
                               "<ul>" +
                               "<li>测试时间: " + new Date() + "</li>" +
                               "<li>发件人: " + TEST_EMAIL + "</li>" +
                               "<li>收件人: " + TEST_RECIPIENT + "</li>" +
                               "<li>测试ID: " + UUID.randomUUID().toString() + "</li>" +
                               "</ul>" +
                               "</div>" +
                               "<p style='color: #666; font-size: 12px; margin-top: 20px;'>" +
                               "此邮件由邮件管理系统自动发送，用于测试163邮箱SMTP功能。</p>" +
                               "</body>" +
                               "</html>";
            
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            message.setSentDate(new Date());
            message.setHeader("Message-ID", "<html-test-" + System.currentTimeMillis() + "@163.com>");
            
            Transport transport = session.getTransport("smtp");
            transport.connect(TEST_SMTP_HOST, TEST_SMTP_PORT, TEST_EMAIL, TEST_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
            logger.info("163邮箱HTML邮件发送成功！");
            
        } catch (Exception e) {
            logger.error("163邮箱HTML邮件发送失败", e);
            throw new RuntimeException("HTML邮件发送失败: " + e.getMessage(), e);
        }
        
        logger.info("=== 163邮箱HTML邮件测试完成 ===");
    }
    
    /**
     * 使用SmtpService测试163邮箱发送
     */
    public static void testWithSmtpService() {
        logger.info("=== 开始使用SmtpService测试163邮箱发送 ===");
        
        try {
            // 创建测试邮箱账户
            EmailAccount account = new EmailAccount();
            account.setAccountName("163邮箱测试账户");
            account.setEmailAddress(TEST_EMAIL);
            account.setPassword(TEST_PASSWORD);
            account.setSmtpHost(TEST_SMTP_HOST);
            account.setSmtpPort(TEST_SMTP_PORT);
            account.setSmtpSsl("1"); // 启用SSL
            account.setStatus("1"); // 启用状态
            
            SmtpService smtpService = new SmtpService();
            
            // 测试连接
            logger.info("测试SMTP连接...");
            SmtpService.SmtpTestResult connectionResult = smtpService.testSmtpConnection(account);
            logger.info("连接测试结果: {}", connectionResult.isSuccess() ? "成功" : "失败");
            logger.info("连接消息: {}", connectionResult.getMessage());
            logger.info("连接时间: {}ms", connectionResult.getConnectionTime());
            
            if (!connectionResult.isSuccess()) {
                logger.error("连接失败: {}", connectionResult.getErrorMessage());
                return;
            }
            
            // 发送测试邮件
            logger.info("发送测试邮件...");
            String subject = "SmtpService测试邮件 - " + new Date();
            String content = "这是使用SmtpService发送的163邮箱测试邮件。\n\n" +
                           "测试时间: " + new Date() + "\n" +
                           "发件人: " + TEST_EMAIL + "\n" +
                           "收件人: " + TEST_RECIPIENT + "\n" +
                           "测试ID: " + UUID.randomUUID().toString();
            
            String messageId = "<smtp-service-test-" + System.currentTimeMillis() + "@163.com>";
            
            SmtpService.SmtpSendResult sendResult = smtpService.sendEmail(
                account, 
                TEST_RECIPIENT, 
                subject, 
                content, 
                messageId
            );
            
            logger.info("发送结果: {}", sendResult.isSuccess() ? "成功" : "失败");
            logger.info("发送消息: {}", sendResult.getMessage());
            logger.info("发送时间: {}ms", sendResult.getSendTime());
            logger.info("邮件ID: {}", sendResult.getMessageId());
            
            if (!sendResult.isSuccess()) {
                logger.error("发送失败: {}", sendResult.getErrorMessage());
            }
            
        } catch (Exception e) {
            logger.error("SmtpService测试异常", e);
            throw new RuntimeException("SmtpService测试失败: " + e.getMessage(), e);
        }
        
        logger.info("=== SmtpService测试163邮箱发送完成 ===");
    }
    
    /**
     * 主方法 - 运行所有测试
     */
    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("163邮箱发送测试开始");
        logger.info("========================================");
        
        try {
            // 1. 连接测试
            testConnection();
            logger.info("");
            
            // 2. 快速发送测试
            testQuickSend();
            logger.info("");
            
            // 3. HTML邮件测试
            testHtmlEmail();
            logger.info("");
            
            // 4. SmtpService测试
            testWithSmtpService();
            
        } catch (Exception e) {
            logger.error("测试过程中发生异常", e);
            System.exit(1);
        }
        
        logger.info("========================================");
        logger.info("163邮箱发送测试完成");
        logger.info("========================================");
    }
}