package com.ruoyi.system.utils;

import com.ruoyi.system.domain.email.EmailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 邮件配置修复工具类
 * 专门用于修复163邮箱535错误
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class EmailConfigFixUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailConfigFixUtil.class);
    
    /**
     * 创建修复后的SMTP配置属性
     * 基于Simple163EmailTest的成功配置
     * 
     * @param account 邮箱账户
     * @return 修复后的Properties配置
     */
    public static Properties createFixedSmtpProperties(EmailAccount account) {
        Properties props = new Properties();
        
        // 基本连接设置
        props.put("mail.smtp.host", account.getSmtpHost());
        props.put("mail.smtp.port", account.getSmtpPort());
        props.put("mail.smtp.auth", "true");
        
        // 根据端口判断使用SSL还是STARTTLS
        int port = account.getSmtpPort();
        if (port == 465) {
            // SSL连接 - 修复163邮箱535错误的关键配置
            props.put("mail.smtp.socketFactory.port", account.getSmtpPort());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.enable", "true"); // 明确启用SSL
            props.put("mail.smtp.ssl.trust", "*"); // 信任所有主机
        } else if (port == 587) {
            // STARTTLS连接
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "*");
        } else if (port == 25) {
            // 普通连接
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "*");
        }
        
        // 认证设置 - 修复163邮箱535错误的关键配置
        props.put("mail.smtp.auth.plain.disable", "false");
        props.put("mail.smtp.auth.login.disable", "false");
        
        // 设置超时时间
        props.put("mail.smtp.timeout", "60000");
        props.put("mail.smtp.connectiontimeout", "60000");
        props.put("mail.smtp.writetimeout", "60000");
        
        // 其他优化设置
        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.allow8bitmime", "true");
        props.put("mail.smtp.sendpartial", "true");
        
        // 163邮箱特殊设置
        if (account.getSmtpHost() != null && account.getSmtpHost().contains("163.com")) {
            logger.info("应用163邮箱特殊配置: {}", account.getEmailAddress());
            // 163邮箱可能需要额外的SSL配置
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.ciphersuites", "TLS_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_256_CBC_SHA");
        }
        
        // QQ邮箱特殊设置
        if (account.getSmtpHost() != null && account.getSmtpHost().contains("qq.com")) {
            logger.info("应用QQ邮箱特殊配置: {}", account.getEmailAddress());
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.quitwait", "false");
        }
        
        // Gmail特殊设置
        if (account.getSmtpHost() != null && account.getSmtpHost().contains("gmail.com")) {
            logger.info("应用Gmail特殊配置: {}", account.getEmailAddress());
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        }
        
        logger.debug("创建SMTP配置完成: {}:{}", account.getSmtpHost(), account.getSmtpPort());
        return props;
    }
    
    /**
     * 检查邮箱账户配置是否正确
     * 
     * @param account 邮箱账户
     * @return 配置检查结果
     */
    public static String checkEmailAccountConfig(EmailAccount account) {
        StringBuilder issues = new StringBuilder();
        
        if (account == null) {
            return "邮箱账户为空";
        }
        
        if (account.getEmailAddress() == null || account.getEmailAddress().trim().isEmpty()) {
            issues.append("邮箱地址为空; ");
        }
        
        if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            issues.append("密码为空; ");
        }
        
        if (account.getSmtpHost() == null || account.getSmtpHost().trim().isEmpty()) {
            issues.append("SMTP主机为空; ");
        }
        
        if (account.getSmtpPort() == null || account.getSmtpPort() <= 0) {
            issues.append("SMTP端口无效; ");
        }
        
        // 检查163邮箱特殊配置
        if (account.getEmailAddress() != null && account.getEmailAddress().contains("@163.com")) {
            if (account.getSmtpPort() != 465 && account.getSmtpPort() != 587) {
                issues.append("163邮箱建议使用465(SSL)或587(STARTTLS)端口; ");
            }
            
            if (!"1".equals(account.getSmtpSsl()) && account.getSmtpPort() == 465) {
                issues.append("163邮箱使用465端口时应启用SSL; ");
            }
        }
        
        // 检查QQ邮箱特殊配置
        if (account.getEmailAddress() != null && account.getEmailAddress().contains("@qq.com")) {
            if (account.getSmtpPort() != 465 && account.getSmtpPort() != 587) {
                issues.append("QQ邮箱建议使用465(SSL)或587(STARTTLS)端口; ");
            }
        }
        
        // 检查Gmail特殊配置
        if (account.getEmailAddress() != null && account.getEmailAddress().contains("@gmail.com")) {
            if (account.getSmtpPort() != 587 && account.getSmtpPort() != 465) {
                issues.append("Gmail建议使用587(STARTTLS)或465(SSL)端口; ");
            }
        }
        
        return issues.length() > 0 ? issues.toString() : "配置检查通过";
    }
    
    /**
     * 获取邮箱服务商建议配置
     * 
     * @param emailAddress 邮箱地址
     * @return 建议配置信息
     */
    public static String getEmailProviderSuggestions(String emailAddress) {
        if (emailAddress == null) {
            return "邮箱地址为空";
        }
        
        if (emailAddress.contains("@163.com")) {
            return "163邮箱建议配置:\n" +
                   "- SMTP服务器: smtp.163.com\n" +
                   "- 端口: 465 (SSL) 或 587 (STARTTLS)\n" +
                   "- 认证: 使用授权码，不是登录密码\n" +
                   "- SSL: 启用\n" +
                   "- 认证方式: 支持PLAIN和LOGIN";
        } else if (emailAddress.contains("@qq.com")) {
            return "QQ邮箱建议配置:\n" +
                   "- SMTP服务器: smtp.qq.com\n" +
                   "- 端口: 465 (SSL) 或 587 (STARTTLS)\n" +
                   "- 认证: 使用授权码，不是QQ密码\n" +
                   "- SSL: 启用\n" +
                   "- 认证方式: 支持PLAIN和LOGIN";
        } else if (emailAddress.contains("@gmail.com")) {
            return "Gmail建议配置:\n" +
                   "- SMTP服务器: smtp.gmail.com\n" +
                   "- 端口: 587 (STARTTLS) 或 465 (SSL)\n" +
                   "- 认证: 使用应用专用密码\n" +
                   "- SSL: 启用\n" +
                   "- 认证方式: 支持PLAIN和LOGIN";
        } else {
            return "未知邮箱服务商，请参考邮箱服务商官方文档";
        }
    }
}






