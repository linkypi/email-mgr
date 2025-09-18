package com.ruoyi.web.controller.email;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.email.ImapService;

/**
 * 邮件跟踪控制器
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/tracking")
public class EmailTrackingController
{
    private static final Logger logger = LoggerFactory.getLogger(EmailTrackingController.class);
    
    @Autowired
    private EmailListener emailListener;

    /**
     * 邮件打开跟踪
     */
    @Anonymous
    @GetMapping("/open")
    public void trackEmailOpen(@RequestParam("msgid") String messageId, 
                              HttpServletRequest request, 
                              HttpServletResponse response) throws IOException
    {
        logger.info("收到邮件打开跟踪请求: messageId={}, ip={}", messageId, request.getRemoteAddr());
        
        // 记录邮件打开事件
        emailListener.recordEmailOpened(messageId);
        
        // 返回1x1透明像素
        response.setContentType("image/png");
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        response.getOutputStream().write(out.toByteArray());
    }

    /**
     * 邮件点击跟踪
     */
    @Anonymous
    @GetMapping("/click")
    public void trackEmailClick(@RequestParam("msgid") String messageId,
                               @RequestParam(value = "url", required = false) String targetUrl,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException
    {
        logger.info("收到邮件点击跟踪请求: messageId={}, targetUrl={}, ip={}", 
                   messageId, targetUrl, request.getRemoteAddr());
        
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
    }

    /**
     * 获取邮件跟踪状态
     */
    @GetMapping("/status")
    public Object getEmailStatus(@RequestParam("msgid") String messageId)
    {
        EmailTrackRecord record = emailListener.getTrackRecord(messageId);
        if (record != null) {
            return record;
        } else {
            return new EmailStatusResponse("邮件跟踪记录不存在", messageId);
        }
    }
    
    /**
     * 测试邮件跟踪功能
     */
    @GetMapping("/test/{accountId}")
    public AjaxResult testEmailTracking(@PathVariable("accountId") Long accountId)
    {
        try {
            emailListener.testEmailTracking(accountId);
            return AjaxResult.success("邮件跟踪测试已启动，请查看日志");
        } catch (Exception e) {
            return AjaxResult.error("测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 强制扫描指定邮件的状态
     */
    @GetMapping("/force-scan/{accountId}")
    public AjaxResult forceScanEmailStatus(@PathVariable("accountId") Long accountId,
                                          @RequestParam("messageId") String messageId)
    {
        try {
            emailListener.forceScanEmailStatus(accountId, messageId);
            return AjaxResult.success("强制扫描已启动，请查看日志");
        } catch (Exception e) {
            return AjaxResult.error("强制扫描失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取邮件跟踪统计
     */
    @GetMapping("/stats/{accountId}")
    public AjaxResult getEmailTrackingStats(@PathVariable("accountId") Long accountId)
    {
        try {
            ImapService.EmailTrackingStats stats = emailListener.getEmailTrackingStats(accountId);
            return AjaxResult.success(stats);
        } catch (Exception e) {
            return AjaxResult.error("获取统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 诊断邮件跟踪问题
     */
    @GetMapping("/diagnose/{accountId}")
    public AjaxResult diagnoseEmailTracking(@PathVariable("accountId") Long accountId)
    {
        try {
            emailListener.diagnoseEmailTracking(accountId);
            return AjaxResult.success("诊断已启动，请查看日志获取详细信息");
        } catch (Exception e) {
            return AjaxResult.error("诊断失败: " + e.getMessage());
        }
    }
    
    /**
     * 邮件状态响应类
     */
    private static class EmailStatusResponse {
        private String message;
        private String messageId;
        
        public EmailStatusResponse(String message, String messageId) {
            this.message = message;
            this.messageId = messageId;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getMessageId() {
            return messageId;
        }
    }
}
