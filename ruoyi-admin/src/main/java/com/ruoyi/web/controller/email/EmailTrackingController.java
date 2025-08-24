package com.ruoyi.web.controller.email;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.ImapEmailSyncService;

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
    @Autowired
    private ImapEmailSyncService imapEmailSyncService;

    /**
     * 邮件打开跟踪
     */
    @GetMapping("/open")
    public void trackEmailOpen(@RequestParam("msgid") String messageId, 
                              HttpServletRequest request, 
                              HttpServletResponse response) throws IOException
    {
        // 记录邮件打开事件
        imapEmailSyncService.recordEmailOpened(messageId);
        
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
    @GetMapping("/click")
    public void trackEmailClick(@RequestParam("msgid") String messageId,
                               @RequestParam(value = "url", required = false) String targetUrl,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException
    {
        // 记录邮件点击事件
        imapEmailSyncService.recordEmailClicked(messageId);
        
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
        EmailTrackRecord record = imapEmailSyncService.getTrackRecord(messageId);
        if (record != null) {
            return record;
        } else {
            return new EmailStatusResponse("邮件跟踪记录不存在", messageId);
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
