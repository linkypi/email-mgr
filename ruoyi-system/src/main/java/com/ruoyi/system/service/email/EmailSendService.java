package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailSendRecord;
import java.util.List;
import java.util.Map;

/**
 * 邮件发送服务接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailSendService 
{
    /**
     * 发送单封邮件
     * 
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param accountId 发送账号ID
     * @return 发送结果
     */
    boolean sendSingleEmail(String toEmail, String subject, String content, Long accountId);

    /**
     * 批量发送邮件
     * 
     * @param contacts 联系人列表
     * @param subject 邮件主题模板
     * @param content 邮件内容模板
     * @param accountId 发送账号ID
     * @param timeInterval 发送间隔(秒)
     * @return 发送结果
     */
    boolean sendBatchEmails(List<EmailContact> contacts, String subject, String content, Long accountId, int timeInterval);

    /**
     * 个性化邮件内容
     * 
     * @param template 邮件模板
     * @param contact 联系人信息
     * @return 个性化后的内容
     */
    String personalizeContent(String template, EmailContact contact);

    /**
     * 获取可用发送账号
     * 
     * @return 可用账号列表
     */
    List<EmailAccount> getAvailableAccounts();

    /**
     * 更新账号发送计数
     * 
     * @param accountId 账号ID
     * @param count 发送数量
     */
    void updateAccountSendCount(Long accountId, int count);

    /**
     * 记录邮件发送状态
     * 
     * @param record 发送记录
     */
    void recordEmailStatus(EmailSendRecord record);

    /**
     * 检查邮件打开状态
     * 
     * @param messageId 邮件消息ID
     * @return 是否已打开
     */
    boolean checkEmailOpenStatus(String messageId);
}
