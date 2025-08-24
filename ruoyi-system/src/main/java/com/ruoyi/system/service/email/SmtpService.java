package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * SMTP服务
 * 提供SMTP连接测试、邮件发送和实时监控功能
 */
@Service
public class SmtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmtpService.class);
    
    /**
     * 测试SMTP连接
     */
    public SmtpTestResult testSmtpConnection(EmailAccount account) {
        long startTime = System.currentTimeMillis();
        
        try {
            Properties props = createSmtpProperties(account);
            Session session = Session.getInstance(props);
            session.setDebug(false);
            
            Transport transport = session.getTransport("smtp");
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            
            long connectionTime = System.currentTimeMillis() - startTime;
            
            // 测试发送权限（不实际发送邮件）
            boolean canSend = transport.isConnected();
            
            transport.close();
            
            if (canSend) {
                return new SmtpTestResult(true, "SMTP连接成功，具备发送权限", 
                    null, connectionTime);
            } else {
                return new SmtpTestResult(false, "SMTP连接失败，无发送权限", 
                    "连接成功但无发送权限", connectionTime);
            }
            
        } catch (Exception e) {
            long connectionTime = System.currentTimeMillis() - startTime;
            logger.error("SMTP连接测试失败: {}", e.getMessage(), e);
            return new SmtpTestResult(false, "SMTP连接失败: " + e.getMessage(), 
                e.getMessage(), connectionTime);
        }
    }
    
    /**
     * 发送邮件
     */
    public SmtpSendResult sendEmail(EmailAccount account, String to, String subject, 
                                   String content, String messageId) {
        long startTime = System.currentTimeMillis();
        
        try {
            Properties props = createSmtpProperties(account);
            Session session = Session.getInstance(props);
            session.setDebug(false);
            
            Transport transport = session.getTransport("smtp");
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            
            // 创建邮件
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(account.getEmailAddress()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content, "UTF-8");
            message.setSentDate(new Date());
            
            // 设置Message-ID
            if (messageId != null) {
                message.setHeader("Message-ID", messageId);
            }
            
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            
            long sendTime = System.currentTimeMillis() - startTime;
            
            transport.close();
            
            return new SmtpSendResult(true, "邮件发送成功", null, sendTime, messageId);
            
        } catch (Exception e) {
            long sendTime = System.currentTimeMillis() - startTime;
            logger.error("邮件发送失败: {}", e.getMessage(), e);
            return new SmtpSendResult(false, "邮件发送失败: " + e.getMessage(), 
                e.getMessage(), sendTime, messageId);
        }
    }
    
    /**
     * 创建SMTP连接属性
     */
    public Properties createSmtpProperties(EmailAccount account) {
        Properties props = new Properties();
        
        // 基本连接设置
        props.setProperty("mail.smtp.host", account.getSmtpHost());
        props.setProperty("mail.smtp.port", String.valueOf(account.getSmtpPort()));
        props.setProperty("mail.smtp.username", account.getEmailAddress());
        props.setProperty("mail.smtp.password", account.getPassword());
        
        // SSL/TLS设置 - 根据端口自动判断
        int port = account.getSmtpPort();
        if (port == 465) {
            // SSL端口
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.port", String.valueOf(port));
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.ssl.trust", "*");
        } else if (port == 587 || port == 25) {
            // STARTTLS端口
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.starttls.required", "true");
            props.setProperty("mail.smtp.ssl.trust", "*");
        } else if ("1".equals(account.getSmtpSsl())) {
            // 手动指定SSL
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.port", String.valueOf(port));
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.ssl.trust", "*");
        } else {
            // 默认STARTTLS
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.starttls.required", "true");
            props.setProperty("mail.smtp.ssl.trust", "*");
        }
        
        // 认证设置
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.auth.plain.disable", "false");
        props.setProperty("mail.smtp.auth.login.disable", "false");
        
        // 连接超时设置
        props.setProperty("mail.smtp.connectiontimeout", "10000"); // 10秒
        props.setProperty("mail.smtp.timeout", "15000"); // 15秒
        
        // 发送超时设置
        props.setProperty("mail.smtp.writetimeout", "15000"); // 15秒
        
        // 其他优化设置
        props.setProperty("mail.smtp.quitwait", "false");
        props.setProperty("mail.smtp.allow8bitmime", "true");
        props.setProperty("mail.smtp.sendpartial", "true");
        
        return props;
    }
    
    /**
     * 创建持久SMTP连接（用于实时监控）
     */
    public Transport createPersistentConnection(EmailAccount account) throws MessagingException {
        Properties props = createSmtpProperties(account);
        Session session = Session.getInstance(props);
        session.setDebug(false);
        
        Transport transport = session.getTransport("smtp");
        transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
            account.getEmailAddress(), account.getPassword());
        
        return transport;
    }
    
    /**
     * 创建邮件会话（用于发送邮件）
     */
    public Session createSession(EmailAccount account) {
        Properties props = createSmtpProperties(account);
        Session session = Session.getInstance(props);
        session.setDebug(false);
        return session;
    }
    
    /**
     * 发送邮件（使用现有连接）
     */
    public SmtpSendResult sendEmailWithTransport(Transport transport, Session session, 
                                                String from, String to, String subject, 
                                                String content, String messageId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 创建邮件
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content, "UTF-8");
            message.setSentDate(new Date());
            
            // 设置Message-ID
            if (messageId != null) {
                message.setHeader("Message-ID", messageId);
            }
            
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            
            long sendTime = System.currentTimeMillis() - startTime;
            
            return new SmtpSendResult(true, "邮件发送成功", null, sendTime, messageId);
            
        } catch (Exception e) {
            long sendTime = System.currentTimeMillis() - startTime;
            logger.error("邮件发送失败: {}", e.getMessage(), e);
            return new SmtpSendResult(false, "邮件发送失败: " + e.getMessage(), 
                e.getMessage(), sendTime, messageId);
        }
    }
    
    /**
     * 检查连接状态
     */
    public boolean isConnectionAlive(Transport transport) {
        try {
            return transport != null && transport.isConnected();
        } catch (Exception e) {
            logger.warn("检查SMTP连接状态失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 重新连接
     */
    public boolean reconnect(Transport transport, EmailAccount account) {
        try {
            if (transport != null && transport.isConnected()) {
                transport.close();
            }
            
            transport.connect(account.getSmtpHost(), account.getSmtpPort(), 
                account.getEmailAddress(), account.getPassword());
            
            return true;
        } catch (Exception e) {
            logger.error("SMTP重新连接失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 安全关闭连接
     */
    public void closeConnection(Transport transport) {
        try {
            if (transport != null && transport.isConnected()) {
                transport.close();
            }
        } catch (Exception e) {
            logger.warn("关闭SMTP连接失败: {}", e.getMessage());
        }
    }
    
    /**
     * SMTP测试结果
     */
    public static class SmtpTestResult {
        private boolean success;
        private String message;
        private String errorMessage;
        private long connectionTime;
        
        public SmtpTestResult(boolean success, String message, String errorMessage, long connectionTime) {
            this.success = success;
            this.message = message;
            this.errorMessage = errorMessage;
            this.connectionTime = connectionTime;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public long getConnectionTime() { return connectionTime; }
        public void setConnectionTime(long connectionTime) { this.connectionTime = connectionTime; }
    }
    
    /**
     * SMTP发送结果
     */
    public static class SmtpSendResult {
        private boolean success;
        private String message;
        private String errorMessage;
        private long sendTime;
        private String messageId;
        
        public SmtpSendResult(boolean success, String message, String errorMessage, 
                             long sendTime, String messageId) {
            this.success = success;
            this.message = message;
            this.errorMessage = errorMessage;
            this.sendTime = sendTime;
            this.messageId = messageId;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public long getSendTime() { return sendTime; }
        public void setSendTime(long sendTime) { this.sendTime = sendTime; }
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
    }
}
