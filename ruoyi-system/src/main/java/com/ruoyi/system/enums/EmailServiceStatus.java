package com.ruoyi.system.enums;

/**
 * 邮件服务状态枚举
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public enum EmailServiceStatus {
    
    /**
     * 停止状态
     */
    STOPPED("0", "停止", "info", false),
    
    /**
     * 运行中状态
     */
    RUNNING("1", "运行中", "success", false),
    
    /**
     * 连接中状态
     */
    CONNECTING("2", "连接中", "warning", false),
    
    /**
     * 已连接状态
     */
    CONNECTED("3", "已连接", "primary", false),
    
    /**
     * 网络超时
     */
    TIMEOUT("4", "网络超时", "danger", true),
    
    /**
     * 认证失败
     */
    AUTH_FAILED("5", "认证失败", "danger", true),
    
    /**
     * SSL错误
     */
    SSL_ERROR("6", "SSL错误", "danger", true),
    
    /**
     * 端口错误
     */
    PORT_ERROR("7", "端口错误", "danger", true),
    
    /**
     * 主机不可达
     */
    HOST_UNREACHABLE("8", "主机不可达", "danger", true),
    
    /**
     * 防火墙阻止
     */
    FIREWALL_BLOCKED("9", "防火墙阻止", "danger", true),
    
    /**
     * 服务异常
     */
    SERVICE_ERROR("10", "服务异常", "danger", true);
    
    private final String code;
    private final String name;
    private final String cssClass;
    private final boolean isError;
    
    EmailServiceStatus(String code, String name, String cssClass, boolean isError) {
        this.code = code;
        this.name = name;
        this.cssClass = cssClass;
        this.isError = isError;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCssClass() {
        return cssClass;
    }
    
    public boolean isError() {
        return isError;
    }
    
    /**
     * 根据代码获取状态枚举
     * 
     * @param code 状态代码
     * @return 状态枚举，如果不存在返回STOPPED
     */
    public static EmailServiceStatus fromCode(String code) {
        if (code == null) {
            return STOPPED;
        }
        
        for (EmailServiceStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return STOPPED;
    }
    
    /**
     * 判断是否为错误状态
     * 
     * @param code 状态代码
     * @return 是否为错误状态
     */
    public static boolean isErrorStatus(String code) {
        return fromCode(code).isError;
    }
    
    /**
     * 判断是否为连接相关状态
     * 
     * @param code 状态代码
     * @return 是否为连接相关状态
     */
    public static boolean isConnectionStatus(String code) {
        EmailServiceStatus status = fromCode(code);
        return status == CONNECTING || status == CONNECTED;
    }
    
    /**
     * 判断是否为网络错误状态
     * 
     * @param code 状态代码
     * @return 是否为网络错误状态
     */
    public static boolean isNetworkErrorStatus(String code) {
        EmailServiceStatus status = fromCode(code);
        return status == TIMEOUT || status == HOST_UNREACHABLE || status == FIREWALL_BLOCKED;
    }
    
    /**
     * 判断是否为认证错误状态
     * 
     * @param code 状态代码
     * @return 是否为认证错误状态
     */
    public static boolean isAuthErrorStatus(String code) {
        EmailServiceStatus status = fromCode(code);
        return status == AUTH_FAILED;
    }
    
    /**
     * 判断是否为配置错误状态
     * 
     * @param code 状态代码
     * @return 是否为配置错误状态
     */
    public static boolean isConfigErrorStatus(String code) {
        EmailServiceStatus status = fromCode(code);
        return status == SSL_ERROR || status == PORT_ERROR;
    }
    
    /**
     * 获取状态描述
     * 
     * @param code 状态代码
     * @return 状态描述
     */
    public static String getStatusDescription(String code) {
        EmailServiceStatus status = fromCode(code);
        switch (status) {
            case STOPPED:
                return "服务已停止";
            case RUNNING:
                return "服务正常运行";
            case CONNECTING:
                return "正在建立网络连接";
            case CONNECTED:
                return "网络连接已建立";
            case TIMEOUT:
                return "网络连接超时，请检查网络设置";
            case AUTH_FAILED:
                return "用户名或密码认证失败，请检查账号配置";
            case SSL_ERROR:
                return "SSL/TLS连接错误，请检查SSL配置";
            case PORT_ERROR:
                return "端口配置错误或端口被占用，请检查端口设置";
            case HOST_UNREACHABLE:
                return "邮件服务器主机不可达，请检查服务器地址";
            case FIREWALL_BLOCKED:
                return "防火墙阻止连接，请检查防火墙设置";
            case SERVICE_ERROR:
                return "服务发生异常，请检查服务配置";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 获取建议的解决方案
     * 
     * @param code 状态代码
     * @return 建议的解决方案
     */
    public static String getSolution(String code) {
        EmailServiceStatus status = fromCode(code);
        switch (status) {
            case TIMEOUT:
                return "1. 检查网络连接是否正常\n2. 增加连接超时时间\n3. 检查防火墙设置";
            case AUTH_FAILED:
                return "1. 检查用户名和密码是否正确\n2. 确认账号是否启用\n3. 检查是否需要开启应用专用密码";
            case SSL_ERROR:
                return "1. 检查SSL证书是否有效\n2. 确认SSL端口配置正确\n3. 尝试使用STARTTLS";
            case PORT_ERROR:
                return "1. 检查端口号是否正确\n2. 确认端口未被其他程序占用\n3. 检查防火墙端口设置";
            case HOST_UNREACHABLE:
                return "1. 检查服务器地址是否正确\n2. 确认服务器是否在线\n3. 检查DNS解析是否正常";
            case FIREWALL_BLOCKED:
                return "1. 检查本地防火墙设置\n2. 确认网络防火墙允许连接\n3. 联系网络管理员";
            case SERVICE_ERROR:
                return "1. 检查服务配置是否正确\n2. 查看服务日志获取详细错误信息\n3. 重启服务";
            default:
                return "请联系技术支持获取帮助";
        }
    }
}
