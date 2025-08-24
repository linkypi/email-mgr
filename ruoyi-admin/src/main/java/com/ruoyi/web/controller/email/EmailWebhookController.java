package com.ruoyi.web.controller.email;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.system.service.email.IEmailStatisticsService;

/**
 * 邮件Webhook Controller
 * 用于接收邮件服务商的回调通知，更新邮件统计数据
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/webhook")
public class EmailWebhookController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(EmailWebhookController.class);

    @Autowired
    private IEmailStatisticsService emailStatisticsService;

    /**
     * 接收邮件送达回调
     * 
     * @param request HTTP请求
     * @param payload 回调数据
     * @return 处理结果
     */
    @PostMapping("/delivered")
    public AjaxResult handleDelivered(HttpServletRequest request, @RequestBody Map<String, Object> payload)
    {
        try
        {
            logger.info("收到邮件送达回调: {}", payload);
            
            // 解析回调数据
            String messageId = extractMessageId(payload);
            if (messageId != null)
            {
                emailStatisticsService.recordEmailDelivered(messageId);
                logger.info("邮件送达记录成功: {}", messageId);
            }
            
            return success("邮件送达回调处理成功");
        }
        catch (Exception e)
        {
            logger.error("处理邮件送达回调失败: {}", e.getMessage(), e);
            return error("处理邮件送达回调失败: " + e.getMessage());
        }
    }

    /**
     * 接收邮件打开回调
     * 
     * @param request HTTP请求
     * @param payload 回调数据
     * @return 处理结果
     */
    @PostMapping("/opened")
    public AjaxResult handleOpened(HttpServletRequest request, @RequestBody Map<String, Object> payload)
    {
        try
        {
            logger.info("收到邮件打开回调: {}", payload);
            
            // 解析回调数据
            String messageId = extractMessageId(payload);
            String ipAddress = extractIpAddress(request, payload);
            String userAgent = extractUserAgent(request, payload);
            String location = extractLocation(payload);
            
            if (messageId != null)
            {
                emailStatisticsService.recordEmailOpened(messageId);
                logger.info("邮件打开记录成功: {}", messageId);
            }
            
            return success("邮件打开回调处理成功");
        }
        catch (Exception e)
        {
            logger.error("处理邮件打开回调失败: {}", e.getMessage(), e);
            return error("处理邮件打开回调失败: " + e.getMessage());
        }
    }

    /**
     * 接收邮件回复回调
     * 
     * @param request HTTP请求
     * @param payload 回调数据
     * @return 处理结果
     */
    @PostMapping("/replied")
    public AjaxResult handleReplied(HttpServletRequest request, @RequestBody Map<String, Object> payload)
    {
        try
        {
            logger.info("收到邮件回复回调: {}", payload);
            
            // 解析回调数据
            String messageId = extractMessageId(payload);
            if (messageId != null)
            {
                emailStatisticsService.recordEmailReplied(messageId);
                logger.info("邮件回复记录成功: {}", messageId);
            }
            
            return success("邮件回复回调处理成功");
        }
        catch (Exception e)
        {
            logger.error("处理邮件回复回调失败: {}", e.getMessage(), e);
            return error("处理邮件回复回调失败: " + e.getMessage());
        }
    }

    /**
     * 接收邮件退回回调
     * 
     * @param request HTTP请求
     * @param payload 回调数据
     * @return 处理结果
     */
    @PostMapping("/bounced")
    public AjaxResult handleBounced(HttpServletRequest request, @RequestBody Map<String, Object> payload)
    {
        try
        {
            logger.info("收到邮件退回回调: {}", payload);
            
            // 解析回调数据
            String messageId = extractMessageId(payload);
            if (messageId != null)
            {
                // 邮件退回可以记录为发送失败，这里暂时不处理，因为新的统计服务不跟踪失败状态
                logger.info("邮件退回记录成功: {}", messageId);
            }
            
            return success("邮件退回回调处理成功");
        }
        catch (Exception e)
        {
            logger.error("处理邮件退回回调失败: {}", e.getMessage(), e);
            return error("处理邮件退回回调失败: " + e.getMessage());
        }
    }

    /**
     * 通用回调处理接口
     * 
     * @param request HTTP请求
     * @param payload 回调数据
     * @return 处理结果
     */
    @PostMapping("/callback")
    public AjaxResult handleCallback(HttpServletRequest request, @RequestBody Map<String, Object> payload)
    {
        try
        {
            logger.info("收到通用邮件回调: {}", payload);
            
            // 根据回调类型处理
            String eventType = extractEventType(payload);
            String messageId = extractMessageId(payload);
            
            if (messageId != null)
            {
                switch (eventType)
                {
                    case "delivered":
                        emailStatisticsService.recordEmailDelivered(messageId);
                        break;
                    case "opened":
                        emailStatisticsService.recordEmailOpened(messageId);
                        break;
                    case "replied":
                        emailStatisticsService.recordEmailReplied(messageId);
                        break;
                    case "bounced":
                        // 邮件退回可以记录为发送失败，这里暂时不处理，因为新的统计服务不跟踪失败状态
                        break;
                    default:
                        logger.warn("未知的回调事件类型: {}", eventType);
                }
                
                logger.info("邮件回调处理成功: {} - {}", eventType, messageId);
            }
            
            return success("邮件回调处理成功");
        }
        catch (Exception e)
        {
            logger.error("处理邮件回调失败: {}", e.getMessage(), e);
            return error("处理邮件回调失败: " + e.getMessage());
        }
    }

    /**
     * 从回调数据中提取邮件ID
     */
    private String extractMessageId(Map<String, Object> payload)
    {
        // 根据不同的邮件服务商API格式提取messageId
        // 这里提供通用的提取逻辑，实际使用时需要根据具体的邮件服务商调整
        
        if (payload.containsKey("message_id"))
        {
            return (String) payload.get("message_id");
        }
        else if (payload.containsKey("messageId"))
        {
            return (String) payload.get("messageId");
        }
        else if (payload.containsKey("id"))
        {
            return (String) payload.get("id");
        }
        else if (payload.containsKey("email_id"))
        {
            return (String) payload.get("email_id");
        }
        
        return null;
    }

    /**
     * 从回调数据中提取事件类型
     */
    private String extractEventType(Map<String, Object> payload)
    {
        if (payload.containsKey("event"))
        {
            return (String) payload.get("event");
        }
        else if (payload.containsKey("type"))
        {
            return (String) payload.get("type");
        }
        else if (payload.containsKey("event_type"))
        {
            return (String) payload.get("event_type");
        }
        
        return "unknown";
    }

    /**
     * 从请求中提取IP地址
     */
    private String extractIpAddress(HttpServletRequest request, Map<String, Object> payload)
    {
        // 优先从回调数据中获取
        if (payload.containsKey("ip"))
        {
            return (String) payload.get("ip");
        }
        else if (payload.containsKey("ip_address"))
        {
            return (String) payload.get("ip_address");
        }
        
        // 从HTTP请求头中获取
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }

    /**
     * 从请求中提取用户代理
     */
    private String extractUserAgent(HttpServletRequest request, Map<String, Object> payload)
    {
        // 优先从回调数据中获取
        if (payload.containsKey("user_agent"))
        {
            return (String) payload.get("user_agent");
        }
        else if (payload.containsKey("userAgent"))
        {
            return (String) payload.get("userAgent");
        }
        
        // 从HTTP请求头中获取
        return request.getHeader("User-Agent");
    }

    /**
     * 从回调数据中提取地理位置
     */
    private String extractLocation(Map<String, Object> payload)
    {
        if (payload.containsKey("location"))
        {
            return (String) payload.get("location");
        }
        else if (payload.containsKey("country"))
        {
            return (String) payload.get("country");
        }
        else if (payload.containsKey("city"))
        {
            return (String) payload.get("city");
        }
        
        return null;
    }
}
