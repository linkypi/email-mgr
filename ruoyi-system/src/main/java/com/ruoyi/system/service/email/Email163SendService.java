package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.domain.email.EmailSendTask;
import com.ruoyi.system.domain.email.EmailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

/**
 * 163邮箱发送服务
 * 严格按照Simple163EmailTest的成功配置
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@Service
public class Email163SendService {
    
    private static final Logger logger = LoggerFactory.getLogger(Email163SendService.class);
    
    /**
     * 严格按照Simple163EmailTest发送邮件
     * 
     * @param account 邮箱账户
     * @param recipient 收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 发送结果
     */
    public boolean sendEmail(EmailAccount account, String recipient, String subject, String content) {
        logger.info("=== 开始163邮箱发送 ===");
        logger.info("发件人: {}", account.getEmailAddress());
        logger.info("收件人: {}", recipient);
        logger.info("SMTP服务器: {}:{}", account.getSmtpHost(), account.getSmtpPort());
        
        try {
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
            
            return true;
            
        } catch (Exception e) {
            logger.error("163邮箱发送失败", e);
            return false;
        }
    }
    
    /**
     * 发送邮件到联系人
     * 
     * @param account 邮箱账户
     * @param contact 联系人
     * @param template 邮件模板
     * @param task 发送任务
     * @return 发送结果
     */
    public boolean sendEmailToContact(EmailAccount account, EmailContact contact, 
                                    EmailTemplate template, EmailSendTask task) {
        String subject;
        String content;
        
        if (template != null) {
            // 使用模板
            subject = template.getSubject();
            content = template.getContent();
        } else {
            // 使用任务中的直接内容
            subject = task.getSubject();
            content = task.getContent();
        }
        
        // 替换占位符
        if (subject != null) {
            subject = replacePlaceholders(subject, contact);
        }
        if (content != null) {
            content = replacePlaceholders(content, contact);
        }
        
        return sendEmail(account, contact.getEmail(), subject, content);
    }
    
    /**
     * 替换邮件内容中的占位符
     * 
     * @param text 原始文本
     * @param contact 联系人
     * @return 替换后的文本
     */
    private String replacePlaceholders(String text, EmailContact contact) {
        if (text == null || contact == null) {
            return text;
        }
        
        // 替换常见的占位符
        text = text.replace("${contactName}", contact.getName() != null ? contact.getName() : "");
        text = text.replace("${contactEmail}", contact.getEmail() != null ? contact.getEmail() : "");
        text = text.replace("${contactPhone}", ""); // EmailContact没有phone字段
        text = text.replace("${contactCompany}", contact.getCompany() != null ? contact.getCompany() : "");
        text = text.replace("${contactPosition}", ""); // EmailContact没有position字段
        
        return text;
    }
    
    /**
     * 测试163邮箱连接
     * 
     * @param account 邮箱账户
     * @return 连接结果
     */
    public boolean testConnection(EmailAccount account) {
        logger.info("=== 开始163邮箱连接测试 ===");
        
        try {
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
            
            return connected;
            
        } catch (Exception e) {
            logger.error("163邮箱连接测试异常", e);
            return false;
        }
    }
}
