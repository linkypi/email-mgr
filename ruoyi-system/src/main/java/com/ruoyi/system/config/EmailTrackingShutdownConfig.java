package com.ruoyi.system.config;

import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.email.ImapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * 邮件跟踪监控优雅关闭配置
 * 确保在应用关闭时正确停止所有邮件跟踪任务
 * 
 * @author ruoyi
 */
@Component
public class EmailTrackingShutdownConfig implements ApplicationListener<ContextClosedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailTrackingShutdownConfig.class);
    
    @Autowired
    private EmailListener emailListener;
    
    @Autowired
    private ImapService imapService;
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("应用正在关闭，开始停止邮件跟踪监控服务...");
        
        try {
            // 停止EmailListener服务
            if (emailListener != null) {
                emailListener.stopListener();
                logger.info("EmailListener服务已停止");
            }
            
            // ImapService的shutdown方法会通过@PreDestroy自动调用
            logger.info("邮件跟踪监控服务关闭完成");
            
        } catch (Exception e) {
            logger.error("关闭邮件跟踪监控服务时发生异常", e);
        }
    }
}
