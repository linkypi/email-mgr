package com.ruoyi.system.utils;

import com.ruoyi.system.enums.EmailServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import java.io.IOException;

/**
 * 邮件服务监控工具类
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class EmailServiceMonitorUtils {
    
    private static final Logger log = LoggerFactory.getLogger(EmailServiceMonitorUtils.class);
    
    /**
     * 根据异常类型判断服务状态
     * 
     * @param e 异常
     * @return 服务状态代码
     */
    public static String determineStatusFromException(Exception e) {
        if (e == null) {
            return EmailServiceStatus.RUNNING.getCode();
        }
        
        // 注释掉频繁的异常分析日志以减少日志输出
        // log.debug("分析异常类型: {}", e.getClass().getSimpleName());
        
        // 网络连接相关异常
        if (e instanceof ConnectException) {
            return EmailServiceStatus.HOST_UNREACHABLE.getCode();
        }
        
        if (e instanceof SocketTimeoutException) {
            return EmailServiceStatus.TIMEOUT.getCode();
        }
        
        if (e instanceof UnknownHostException) {
            return EmailServiceStatus.HOST_UNREACHABLE.getCode();
        }
        
        // SSL相关异常
        if (e instanceof SSLException) {
            return EmailServiceStatus.SSL_ERROR.getCode();
        }
        
        // 认证相关异常
        if (e instanceof AuthenticationFailedException) {
            return EmailServiceStatus.AUTH_FAILED.getCode();
        }
        
        // MessagingException 需要进一步分析
        if (e instanceof MessagingException) {
            return analyzeMessagingException((MessagingException) e);
        }
        
        // IOException 需要进一步分析
        if (e instanceof IOException) {
            return analyzeIOException((IOException) e);
        }
        
        // 其他异常
        return EmailServiceStatus.SERVICE_ERROR.getCode();
    }
    
    /**
     * 分析 MessagingException 异常
     * 
     * @param e MessagingException
     * @return 服务状态代码
     */
    private static String analyzeMessagingException(MessagingException e) {
        String message = e.getMessage().toLowerCase();
        
        // 认证失败
        if (message.contains("authentication failed") || 
            message.contains("invalid credentials") ||
            message.contains("login failed")) {
            return EmailServiceStatus.AUTH_FAILED.getCode();
        }
        
        // 连接超时
        if (message.contains("timeout") || 
            message.contains("connection timed out")) {
            return EmailServiceStatus.TIMEOUT.getCode();
        }
        
        // 主机不可达
        if (message.contains("unknown host") || 
            message.contains("host unreachable") ||
            message.contains("no route to host")) {
            return EmailServiceStatus.HOST_UNREACHABLE.getCode();
        }
        
        // 端口错误
        if (message.contains("connection refused") || 
            message.contains("port") ||
            message.contains("address already in use")) {
            return EmailServiceStatus.PORT_ERROR.getCode();
        }
        
        // SSL错误
        if (message.contains("ssl") || 
            message.contains("tls") ||
            message.contains("certificate")) {
            return EmailServiceStatus.SSL_ERROR.getCode();
        }
        
        // 防火墙阻止
        if (message.contains("permission denied") || 
            message.contains("access denied") ||
            message.contains("firewall")) {
            return EmailServiceStatus.FIREWALL_BLOCKED.getCode();
        }
        
        return EmailServiceStatus.SERVICE_ERROR.getCode();
    }
    
    /**
     * 分析 IOException 异常
     * 
     * @param e IOException
     * @return 服务状态代码
     */
    private static String analyzeIOException(IOException e) {
        String message = e.getMessage().toLowerCase();
        
        // 连接超时
        if (message.contains("timeout") || 
            message.contains("timed out")) {
            return EmailServiceStatus.TIMEOUT.getCode();
        }
        
        // 连接被拒绝
        if (message.contains("connection refused") || 
            message.contains("connect failed")) {
            return EmailServiceStatus.PORT_ERROR.getCode();
        }
        
        // 主机不可达
        if (message.contains("no route to host") || 
            message.contains("network is unreachable")) {
            return EmailServiceStatus.HOST_UNREACHABLE.getCode();
        }
        
        // 权限被拒绝
        if (message.contains("permission denied")) {
            return EmailServiceStatus.FIREWALL_BLOCKED.getCode();
        }
        
        return EmailServiceStatus.SERVICE_ERROR.getCode();
    }
    
    /**
     * 获取异常的错误信息
     * 
     * @param e 异常
     * @return 错误信息
     */
    public static String getErrorMessage(Exception e) {
        if (e == null) {
            return "未知错误";
        }
        
        String statusCode = determineStatusFromException(e);
        EmailServiceStatus status = EmailServiceStatus.fromCode(statusCode);
        
        StringBuilder message = new StringBuilder();
        message.append(status.getName()).append(": ");
        
        if (e instanceof MessagingException) {
            message.append(e.getMessage());
        } else if (e instanceof IOException) {
            message.append(e.getMessage());
        } else {
            message.append(e.getMessage());
        }
        
        return message.toString();
    }
    
    /**
     * 获取异常的详细描述
     * 
     * @param e 异常
     * @return 详细描述
     */
    public static String getDetailedErrorMessage(Exception e) {
        if (e == null) {
            return "未知错误";
        }
        
        String statusCode = determineStatusFromException(e);
        EmailServiceStatus status = EmailServiceStatus.fromCode(statusCode);
        
        StringBuilder message = new StringBuilder();
        message.append("【").append(status.getName()).append("】\n");
        message.append("错误描述: ").append(EmailServiceStatus.getStatusDescription(statusCode)).append("\n");
        message.append("错误详情: ").append(e.getMessage()).append("\n");
        message.append("异常类型: ").append(e.getClass().getSimpleName()).append("\n");
        message.append("解决方案:\n").append(EmailServiceStatus.getSolution(statusCode));
        
        return message.toString();
    }
    
    /**
     * 判断是否需要重试
     * 
     * @param statusCode 状态代码
     * @return 是否需要重试
     */
    public static boolean shouldRetry(String statusCode) {
        EmailServiceStatus status = EmailServiceStatus.fromCode(statusCode);
        
        // 网络相关错误可以重试
        return status == EmailServiceStatus.TIMEOUT || 
               status == EmailServiceStatus.HOST_UNREACHABLE ||
               status == EmailServiceStatus.FIREWALL_BLOCKED ||
               status == EmailServiceStatus.SERVICE_ERROR;
    }
    
    /**
     * 判断是否为严重错误
     * 
     * @param statusCode 状态代码
     * @return 是否为严重错误
     */
    public static boolean isCriticalError(String statusCode) {
        EmailServiceStatus status = EmailServiceStatus.fromCode(statusCode);
        
        // 认证失败和配置错误是严重错误
        return status == EmailServiceStatus.AUTH_FAILED ||
               status == EmailServiceStatus.SSL_ERROR ||
               status == EmailServiceStatus.PORT_ERROR;
    }
    
    /**
     * 获取状态优先级（用于排序）
     * 
     * @param statusCode 状态代码
     * @return 优先级（数字越小优先级越高）
     */
    public static int getStatusPriority(String statusCode) {
        EmailServiceStatus status = EmailServiceStatus.fromCode(statusCode);
        
        switch (status) {
            case RUNNING:
                return 1;
            case CONNECTED:
                return 2;
            case CONNECTING:
                return 3;
            case STOPPED:
                return 4;
            case TIMEOUT:
                return 5;
            case HOST_UNREACHABLE:
                return 6;
            case FIREWALL_BLOCKED:
                return 7;
            case SERVICE_ERROR:
                return 8;
            case AUTH_FAILED:
                return 9;
            case SSL_ERROR:
                return 10;
            case PORT_ERROR:
                return 11;
            default:
                return 12;
        }
    }
    
    /**
     * 格式化响应时间
     * 
     * @param responseTime 响应时间（毫秒）
     * @return 格式化的响应时间
     */
    public static String formatResponseTime(long responseTime) {
        if (responseTime < 1000) {
            return responseTime + "ms";
        } else if (responseTime < 60000) {
            return String.format("%.1fs", responseTime / 1000.0);
        } else {
            return String.format("%.1fm", responseTime / 60000.0);
        }
    }
    
    /**
     * 判断响应时间是否正常
     * 
     * @param responseTime 响应时间（毫秒）
     * @return 是否正常
     */
    public static boolean isResponseTimeNormal(long responseTime) {
        // 正常响应时间应该在30秒以内
        return responseTime <= 30000;
    }
    
    /**
     * 获取响应时间等级
     * 
     * @param responseTime 响应时间（毫秒）
     * @return 响应时间等级（1-优秀，2-良好，3-一般，4-较慢，5-很慢）
     */
    public static int getResponseTimeLevel(long responseTime) {
        if (responseTime <= 1000) {
            return 1; // 优秀
        } else if (responseTime <= 3000) {
            return 2; // 良好
        } else if (responseTime <= 10000) {
            return 3; // 一般
        } else if (responseTime <= 30000) {
            return 4; // 较慢
        } else {
            return 5; // 很慢
        }
    }
}

