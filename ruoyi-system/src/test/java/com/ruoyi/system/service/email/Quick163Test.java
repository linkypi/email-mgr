package com.ruoyi.system.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * 163邮箱快速测试
 * 最简单的163邮箱发送测试
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class Quick163Test {
    
    private static final Logger logger = LoggerFactory.getLogger(Quick163Test.class);
    
    public static void main(String[] args) {
        logger.info("开始163邮箱快速测试...");
        
        // 163邮箱配置
        String email = "19147797833@163.com";
        String password = "MKsH2qnuE2qVFiBx";
        String smtpHost = "smtp.163.com";
        int smtpPort = 465;
        String recipient = "182867664@qq.com";
        
        try {
            // 创建邮件配置
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(smtpPort));
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.trust", "*");
            
            // 启用调试
            props.put("mail.debug", "true");
            
            // 创建会话
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });
            
            // 创建邮件
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("163邮箱测试 - " + new Date());
            message.setText("这是一封来自163邮箱的测试邮件。\n时间: " + new Date());
            message.setSentDate(new Date());
            
            // 发送邮件
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, smtpPort, email, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
            logger.info("163邮箱测试成功！邮件已发送到: {}", recipient);
            
        } catch (Exception e) {
            logger.error("163邮箱测试失败", e);
            System.exit(1);
        }
    }
}






