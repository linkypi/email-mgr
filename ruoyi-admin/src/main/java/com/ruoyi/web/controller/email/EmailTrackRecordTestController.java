package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.system.service.email.IEmailTrackRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 邮件跟踪记录测试控制器
 * 用于测试邮件跟踪记录的插入和查询
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/email/track-test")
public class EmailTrackRecordTestController extends BaseController {
    
    @Autowired
    private EmailListener emailListener;
    
    @Autowired
    private IEmailTrackRecordService emailTrackRecordService;
    
    /**
     * 测试插入邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:test:edit')")
    @Log(title = "邮件跟踪测试", businessType = BusinessType.INSERT)
    @PostMapping("/insert")
    public AjaxResult testInsertTrackRecord(@RequestParam("taskId") Long taskId,
                                          @RequestParam("messageId") String messageId,
                                          @RequestParam("subject") String subject,
                                          @RequestParam("recipient") String recipient,
                                          @RequestParam("sender") String sender) {
        try {
            // 创建测试跟踪记录
            EmailTrackRecord trackRecord = new EmailTrackRecord();
            trackRecord.setTaskId(taskId);
            trackRecord.setMessageId(messageId);
            trackRecord.setSubject(subject);
            trackRecord.setRecipient(recipient);
            trackRecord.setSender(sender);
            trackRecord.setContent("测试邮件内容");
            trackRecord.setStatus("PENDING");
            trackRecord.setSentTime(new Date());
            trackRecord.setAccountId(1L);
            trackRecord.setCreateBy("test");
            trackRecord.setCreateTime(new Date());
            
            // 插入记录
            int result = emailTrackRecordService.insertEmailTrackRecord(trackRecord);
            
            if (result > 0) {
                return success("邮件跟踪记录插入成功，影响行数: " + result);
            } else {
                return error("邮件跟踪记录插入失败");
            }
            
        } catch (Exception e) {
            return error("插入邮件跟踪记录时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 测试查询邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:test:view')")
    @GetMapping("/query/{messageId}")
    public AjaxResult testQueryTrackRecord(@PathVariable("messageId") String messageId) {
        try {
            EmailTrackRecord record = emailTrackRecordService.selectEmailTrackRecordByMessageId(messageId);
            if (record != null) {
                return success(record);
            } else {
                return error("未找到邮件跟踪记录: " + messageId);
            }
        } catch (Exception e) {
            return error("查询邮件跟踪记录时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 测试根据任务ID查询邮件跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('email:test:view')")
    @GetMapping("/query-by-task/{taskId}")
    public AjaxResult testQueryTrackRecordByTaskId(@PathVariable("taskId") Long taskId) {
        try {
            List<EmailTrackRecord> records = emailTrackRecordService.selectEmailTrackRecordByTaskId(taskId);
            return success(records);
        } catch (Exception e) {
            return error("根据任务ID查询邮件跟踪记录时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 测试发送带跟踪的邮件
     */
    @PreAuthorize("@ss.hasPermi('email:test:edit')")
    @Log(title = "邮件跟踪测试", businessType = BusinessType.OTHER)
    @PostMapping("/send-tracking-email")
    public AjaxResult testSendTrackingEmail(@RequestParam("accountId") Long accountId,
                                          @RequestParam("to") String to,
                                          @RequestParam("subject") String subject,
                                          @RequestParam("content") String content,
                                          @RequestParam("taskId") Long taskId) {
        try {
            // 创建测试邮箱账号
            EmailAccount account = new EmailAccount();
            account.setAccountId(accountId);
            account.setEmailAddress("test@example.com");
            account.setSmtpHost("smtp.example.com");
            account.setSmtpPort(587);
            account.setPassword("testpassword");
            
            // 发送带跟踪的邮件
            String messageId = emailListener.sendEmailWithTracking(account, to, subject, content, taskId);
            
            if (messageId != null) {
                return success("邮件发送成功，MessageID: " + messageId);
            } else {
                return error("邮件发送失败");
            }
            
        } catch (Exception e) {
            return error("发送带跟踪的邮件时发生异常: " + e.getMessage());
        }
    }
}
