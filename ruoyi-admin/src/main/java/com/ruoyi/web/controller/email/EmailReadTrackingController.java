package com.ruoyi.web.controller.email;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.email.EmailReadTrackingService;
import com.ruoyi.system.service.email.EmailListener;

/**
 * 邮件已读跟踪控制器
 * 提供多种邮件已读检测方式，解决Gmail等邮件服务商代理图片的问题
 * 
 * @author ruoyi
 * @date 2024-01-25
 */
@RestController
@RequestMapping("/email/read-tracking")
public class EmailReadTrackingController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailReadTrackingController.class);
    
    @Autowired
    private EmailReadTrackingService emailReadTrackingService;
    
    @Autowired
    private EmailListener emailListener;
    
    /**
     * 增强版邮件打开跟踪
     * 支持多种检测方式
     */
    @Anonymous
    @GetMapping("/open")
    public void trackEmailOpen(@RequestParam("msgid") String messageId,
                              @RequestParam(value = "method", defaultValue = "pixel") String method,
                              @RequestParam(value = "recipient", required = false) String recipient,
                              @RequestParam(value = "sender", required = false) String sender,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        
        logger.info("收到邮件打开跟踪请求: messageId={}, method={}, ip={}, userAgent={}", 
                   messageId, method, request.getRemoteAddr(), request.getHeader("User-Agent"));
        
        try {
            // 记录邮件打开事件（传统像素跟踪）
            emailListener.recordEmailOpened(messageId);
            
            // 启动增强版已读跟踪
            if (recipient != null && sender != null) {
                emailReadTrackingService.startTrackingEmailRead(messageId, recipient, sender);
                logger.info("启动增强版邮件已读跟踪: MessageID={}, 收件人={}", messageId, recipient);
            }
            
            // 分析请求来源，判断邮件客户端类型
            String userAgent = request.getHeader("User-Agent");
            String clientType = analyzeEmailClient(userAgent);
            logger.info("邮件客户端类型: {}", clientType);
            
            // 返回1x1透明像素
            response.setContentType("image/png");
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);
            response.getOutputStream().write(out.toByteArray());
            
        } catch (Exception e) {
            logger.error("处理邮件打开跟踪请求失败: MessageID={}", messageId, e);
            
            // 即使出错也要返回像素，避免邮件客户端显示破损图片
            response.setContentType("image/png");
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);
            response.getOutputStream().write(out.toByteArray());
        }
    }
    
    /**
     * 邮件点击跟踪
     */
    @Anonymous
    @GetMapping("/click")
    public void trackEmailClick(@RequestParam("msgid") String messageId,
                               @RequestParam(value = "url", required = false) String targetUrl,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        
        logger.info("收到邮件点击跟踪请求: messageId={}, targetUrl={}, ip={}", 
                   messageId, targetUrl, request.getRemoteAddr());
        
        try {
            // 记录邮件点击事件
            emailListener.recordEmailClicked(messageId);
            
            // 如果有目标URL，重定向到目标URL
            if (targetUrl != null && !targetUrl.trim().isEmpty()) {
                response.sendRedirect(targetUrl);
            } else {
                // 否则返回成功页面
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>邮件点击已记录</h1></body></html>");
            }
            
        } catch (Exception e) {
            logger.error("处理邮件点击跟踪请求失败: MessageID={}", messageId, e);
            
            // 出错时返回错误页面
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<html><body><h1>跟踪记录失败</h1></body></html>");
        }
    }
    
    /**
     * 手动标记邮件为已读
     * 用于测试或手动确认
     */
    @PostMapping("/mark-read")
    public AjaxResult markEmailAsRead(@RequestParam("messageId") String messageId,
                                     @RequestParam(value = "method", defaultValue = "manual") String method) {
        try {
            emailListener.recordEmailOpened(messageId);
            logger.info("手动标记邮件为已读: MessageID={}, 方法={}", messageId, method);
            return AjaxResult.success("邮件已读状态更新成功");
        } catch (Exception e) {
            logger.error("手动标记邮件为已读失败: MessageID={}", messageId, e);
            return AjaxResult.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取邮件已读跟踪统计
     */
    @GetMapping("/stats")
    public AjaxResult getTrackingStats() {
        try {
            Map<String, Object> stats = emailReadTrackingService.getTrackingStats();
            return AjaxResult.success(stats);
        } catch (Exception e) {
            logger.error("获取跟踪统计失败", e);
            return AjaxResult.error("获取统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 停止跟踪指定邮件
     */
    @PostMapping("/stop-tracking")
    public AjaxResult stopTracking(@RequestParam("messageId") String messageId) {
        try {
            emailReadTrackingService.stopTrackingEmail(messageId);
            logger.info("停止跟踪邮件: MessageID={}", messageId);
            return AjaxResult.success("停止跟踪成功");
        } catch (Exception e) {
            logger.error("停止跟踪失败: MessageID={}", messageId, e);
            return AjaxResult.error("停止跟踪失败: " + e.getMessage());
        }
    }
    
    /**
     * 清理过期的跟踪记录
     */
    @PostMapping("/cleanup")
    public AjaxResult cleanupExpiredTracking() {
        try {
            emailReadTrackingService.cleanupExpiredTracking();
            return AjaxResult.success("清理完成");
        } catch (Exception e) {
            logger.error("清理过期跟踪记录失败", e);
            return AjaxResult.error("清理失败: " + e.getMessage());
        }
    }
    
    /**
     * 分析邮件客户端类型
     */
    private String analyzeEmailClient(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        if (ua.contains("gmail")) {
            return "Gmail";
        } else if (ua.contains("outlook") || ua.contains("microsoft")) {
            return "Outlook";
        } else if (ua.contains("apple") || ua.contains("mail")) {
            return "Apple Mail";
        } else if (ua.contains("thunderbird")) {
            return "Thunderbird";
        } else if (ua.contains("foxmail")) {
            return "Foxmail";
        } else if (ua.contains("qq")) {
            return "QQ Mail";
        } else if (ua.contains("163") || ua.contains("126")) {
            return "NetEase Mail";
        } else if (ua.contains("sina")) {
            return "Sina Mail";
        } else {
            return "Other";
        }
    }
    
    /**
     * 测试邮件已读跟踪功能
     */
    @GetMapping("/test")
    public AjaxResult testTracking(@RequestParam("messageId") String messageId,
                                  @RequestParam("recipient") String recipient,
                                  @RequestParam("sender") String sender) {
        try {
            emailReadTrackingService.startTrackingEmailRead(messageId, recipient, sender);
            return AjaxResult.success("测试跟踪已启动");
        } catch (Exception e) {
            logger.error("测试跟踪失败: MessageID={}", messageId, e);
            return AjaxResult.error("测试失败: " + e.getMessage());
        }
    }
}


