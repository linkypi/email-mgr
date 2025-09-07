package com.ruoyi.system.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络诊断工具
 * 用于诊断邮件服务器连接问题
 */
@Component
public class NetworkDiagnostic {
    
    private static final Logger logger = LoggerFactory.getLogger(NetworkDiagnostic.class);
    
    /**
     * 诊断邮件服务器连接
     */
    public NetworkDiagnosticResult diagnoseEmailServer(String host, int port) {
        NetworkDiagnosticResult result = new NetworkDiagnosticResult();
        result.setHost(host);
        result.setPort(port);
        
        logger.info("开始诊断邮件服务器连接: {}:{}", host, port);
        
        // 1. DNS解析测试
        result.setDnsResolved(testDnsResolution(host));
        
        // 2. 端口连通性测试
        result.setPortReachable(testPortConnectivity(host, port));
        
        // 3. 网络路由测试
        result.setRouteTrace(testNetworkRoute(host));
        
        // 4. 代理检测
        result.setProxyDetected(detectProxy());
        
        logger.info("邮件服务器诊断完成: {}:{} - DNS:{}, 端口:{}, 路由:{}, 代理:{}", 
                   host, port, result.isDnsResolved(), result.isPortReachable(), 
                   result.isRouteTrace(), result.isProxyDetected());
        
        return result;
    }
    
    /**
     * 测试DNS解析
     */
    private boolean testDnsResolution(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            logger.info("DNS解析成功: {} -> {}", host, address.getHostAddress());
            return true;
        } catch (UnknownHostException e) {
            logger.error("DNS解析失败: {} - {}", host, e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试端口连通性
     */
    private boolean testPortConnectivity(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 10000); // 10秒超时
            logger.info("端口连通性测试成功: {}:{}", host, port);
            return true;
        } catch (IOException e) {
            logger.error("端口连通性测试失败: {}:{} - {}", host, port, e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试网络路由
     */
    private boolean testNetworkRoute(String host) {
        try {
            // 简单的ping测试
            InetAddress address = InetAddress.getByName(host);
            boolean reachable = address.isReachable(5000); // 5秒超时
            logger.info("网络路由测试: {} - {}", host, reachable ? "可达" : "不可达");
            return reachable;
        } catch (IOException e) {
            logger.error("网络路由测试失败: {} - {}", host, e.getMessage());
            return false;
        }
    }
    
    /**
     * 检测代理设置
     */
    private boolean detectProxy() {
        String httpProxy = System.getProperty("http.proxyHost");
        String httpsProxy = System.getProperty("https.proxyHost");
        String socksProxy = System.getProperty("socksProxyHost");
        
        boolean hasProxy = (httpProxy != null && !httpProxy.isEmpty()) ||
                          (httpsProxy != null && !httpsProxy.isEmpty()) ||
                          (socksProxy != null && !socksProxy.isEmpty());
        
        if (hasProxy) {
            logger.info("检测到代理设置 - HTTP:{}, HTTPS:{}, SOCKS:{}", 
                       httpProxy, httpsProxy, socksProxy);
        } else {
            logger.info("未检测到代理设置");
        }
        
        return hasProxy;
    }
    
    /**
     * 获取推荐的邮件服务器配置
     */
    public List<EmailServerConfig> getRecommendedEmailServers() {
        List<EmailServerConfig> configs = new ArrayList<>();
        
        // QQ邮箱配置
        configs.add(new EmailServerConfig("QQ邮箱", "smtp.qq.com", 587, "STARTTLS"));
        configs.add(new EmailServerConfig("QQ邮箱", "smtp.qq.com", 465, "SSL"));
        
        // 163邮箱配置
        configs.add(new EmailServerConfig("163邮箱", "smtp.163.com", 25, "STARTTLS"));
        configs.add(new EmailServerConfig("163邮箱", "smtp.163.com", 994, "SSL"));
        
        // 126邮箱配置
        configs.add(new EmailServerConfig("126邮箱", "smtp.126.com", 25, "STARTTLS"));
        configs.add(new EmailServerConfig("126邮箱", "smtp.126.com", 994, "SSL"));
        
        // Gmail配置（如果网络允许）
        configs.add(new EmailServerConfig("Gmail", "smtp.gmail.com", 587, "STARTTLS"));
        configs.add(new EmailServerConfig("Gmail", "smtp.gmail.com", 465, "SSL"));
        
        return configs;
    }
    
    /**
     * 网络诊断结果
     */
    public static class NetworkDiagnosticResult {
        private String host;
        private int port;
        private boolean dnsResolved;
        private boolean portReachable;
        private boolean routeTrace;
        private boolean proxyDetected;
        
        // Getters and setters
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public boolean isDnsResolved() { return dnsResolved; }
        public void setDnsResolved(boolean dnsResolved) { this.dnsResolved = dnsResolved; }
        public boolean isPortReachable() { return portReachable; }
        public void setPortReachable(boolean portReachable) { this.portReachable = portReachable; }
        public boolean isRouteTrace() { return routeTrace; }
        public void setRouteTrace(boolean routeTrace) { this.routeTrace = routeTrace; }
        public boolean isProxyDetected() { return proxyDetected; }
        public void setProxyDetected(boolean proxyDetected) { this.proxyDetected = proxyDetected; }
        
        public boolean isConnectable() {
            return dnsResolved && portReachable;
        }
        
        public String getDiagnosticMessage() {
            if (isConnectable()) {
                return "网络连接正常";
            } else if (!dnsResolved) {
                return "DNS解析失败，请检查网络设置";
            } else if (!portReachable) {
                return "端口连接失败，可能被防火墙阻止或需要代理";
            } else {
                return "网络连接异常";
            }
        }
    }
    
    /**
     * 邮件服务器配置
     */
    public static class EmailServerConfig {
        private String name;
        private String host;
        private int port;
        private String security;
        
        public EmailServerConfig(String name, String host, int port, String security) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.security = security;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public String getSecurity() { return security; }
        public void setSecurity(String security) { this.security = security; }
    }
}
