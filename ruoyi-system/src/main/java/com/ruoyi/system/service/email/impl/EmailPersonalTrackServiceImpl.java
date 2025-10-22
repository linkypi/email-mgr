package com.ruoyi.system.service.email.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailTrackRecordMapper;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.IEmailPersonalTrackService;
import com.ruoyi.system.service.email.SmtpService;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.domain.email.EmailAccount;

/**
 * 个人邮件跟踪Service业务层处理
 * 基于email_track_record表实现个人邮件管理功能
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
@Service
public class EmailPersonalTrackServiceImpl implements IEmailPersonalTrackService 
{
    private static final Logger logger = LoggerFactory.getLogger(EmailPersonalTrackServiceImpl.class);

    @Autowired
    private EmailTrackRecordMapper emailTrackRecordMapper;

    @Autowired
    private SmtpService smtpService;

    @Autowired
    private IEmailAccountService emailAccountService;

    /**
     * 查询发件箱列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    @Override
    @DataScope(userAlias = "user_id")
    public List<EmailTrackRecord> selectSentList(EmailTrackRecord emailTrackRecord)
    {
        emailTrackRecord.setFolderType("sent");
        List<EmailTrackRecord> emailTrackRecords = emailTrackRecordMapper.selectPersonalTrackList(emailTrackRecord);
        return emailTrackRecords;
    }

    /**
     * 查询收件箱列表（回复邮件）
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    @Override
    @DataScope(userAlias = "user_id")
    public List<EmailTrackRecord> selectInboxList(EmailTrackRecord emailTrackRecord)
    {
        emailTrackRecord.setFolderType("inbox");
        return emailTrackRecordMapper.selectPersonalTrackList(emailTrackRecord);
    }

    /**
     * 查询星标邮件列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    @Override
    @DataScope(userAlias = "user_id")
    public List<EmailTrackRecord> selectStarredList(EmailTrackRecord emailTrackRecord)
    {
        return emailTrackRecordMapper.selectStarredList(emailTrackRecord);
    }

    /**
     * 查询已删除邮件列表
     * 
     * @param emailTrackRecord 邮件跟踪记录
     * @return 邮件跟踪记录集合
     */
    @Override
    @DataScope(userAlias = "user_id")
    public List<EmailTrackRecord> selectDeletedList(EmailTrackRecord emailTrackRecord)
    {
        return emailTrackRecordMapper.selectDeletedList(emailTrackRecord);
    }

    /**
     * 根据ID查询邮件跟踪记录
     * 
     * @param id 邮件跟踪记录主键
     * @return 邮件跟踪记录
     */
    @Override
    public EmailTrackRecord selectEmailTrackRecordById(Long id)
    {
        return emailTrackRecordMapper.selectEmailTrackRecordById(id);
    }

    /**
     * 标记邮件为星标
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int markAsStarred(Long[] ids)
    {
        return emailTrackRecordMapper.markAsStarred(ids);
    }

    /**
     * 取消星标
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int unmarkAsStarred(Long[] ids)
    {
        return emailTrackRecordMapper.unmarkAsStarred(ids);
    }

    /**
     * 标记邮件为已读
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int markAsRead(Long[] ids)
    {
        return emailTrackRecordMapper.markAsRead(ids);
    }

    /**
     * 标记邮件为未读
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int markAsUnread(Long[] ids)
    {
        return emailTrackRecordMapper.markAsUnread(ids);
    }

    /**
     * 标记邮件为重要
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int markAsImportant(Long[] ids)
    {
        return emailTrackRecordMapper.markAsImportant(ids);
    }

    /**
     * 取消重要标记
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int unmarkAsImportant(Long[] ids)
    {
        return emailTrackRecordMapper.unmarkAsImportant(ids);
    }

    /**
     * 移动到已删除
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int moveToDeleted(Long[] ids)
    {
        return emailTrackRecordMapper.moveToDeleted(ids);
    }

    /**
     * 从已删除恢复
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int restoreFromDeleted(Long[] ids)
    {
        return emailTrackRecordMapper.restoreFromDeleted(ids);
    }

    /**
     * 彻底删除邮件
     * 
     * @param ids 邮件ID数组
     * @return 结果
     */
    @Override
    public int deletePermanently(Long[] ids)
    {
        return emailTrackRecordMapper.deleteEmailTrackRecordByIds(ids);
    }

    /**
     * 获取发件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    @Override
    public int getSentUnreadCount(Long userId)
    {
        return emailTrackRecordMapper.getSentUnreadCount(userId);
    }

    /**
     * 获取发件箱总数量
     * 
     * @param userId 用户ID
     * @return 总数量
     */
    public int getSentTotalCount(Long userId)
    {
        return emailTrackRecordMapper.getSentTotalCount(userId);
    }

    /**
     * 获取收件箱未读数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    @Override
    public int getInboxUnreadCount(Long userId)
    {
        return emailTrackRecordMapper.getInboxUnreadCount(userId);
    }

    /**
     * 获取星标邮件数量
     * 
     * @param userId 用户ID
     * @return 星标数量
     */
    @Override
    public int getStarredCount(Long userId)
    {
        return emailTrackRecordMapper.getStarredCount(userId);
    }

    /**
     * 获取已删除邮件数量
     * 
     * @param userId 用户ID
     * @return 已删除数量
     */
    @Override
    public int getDeletedCount(Long userId)
    {
        return emailTrackRecordMapper.getDeletedCount(userId);
    }

    /**
     * 获取邮件统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getEmailStatistics(Long userId)
    {
        return emailTrackRecordMapper.getPersonalEmailStatistics(userId);
    }

    /**
     * 发送邮件
     * 
     * @param emailData 邮件信息
     * @return 结果
     */
    @Override
    public int sendEmail(EmailTrackRecord emailData)
    {
        try {
            // 设置邮件的基本信息
            emailData.setFolderType("sent");
            emailData.setStatus("SENT");
            emailData.setSentTime(new Date());
            emailData.setCreateBy(SecurityUtils.getUsername());
            emailData.setCreateTime(new Date());
            emailData.setUserId(SecurityUtils.getUserId());
            emailData.setIsRead(1); // 发送的邮件默认为已读
            emailData.setIsStarred(0);
            emailData.setIsImportant(0);
            
            // 生成Message-ID
            String messageId = generateMessageId();
            emailData.setMessageId(messageId);
            
            // 根据当前用户获取邮箱账号信息
            Long userId = SecurityUtils.getUserId();
            EmailAccount account = emailAccountService.selectEmailAccountByUserId(userId);
            if (account == null) {
                throw new RuntimeException("未找到用户邮箱账号");
            }
            
            // 发送邮件
            SmtpService.SmtpSendResult sendResult = smtpService.sendEmail(
                account,
                emailData.getRecipient(),
                emailData.getSubject(),
                emailData.getContent(),
                messageId
            );
            
            if (sendResult.isSuccess()) {
                // 发送成功，更新状态
                emailData.setStatus("SEND_SUCCESS");
                emailData.setAccountId(account.getAccountId());
                emailTrackRecordMapper.insertEmailTrackRecord(emailData);
                return 1; // 表示成功
            } else {
                // 发送失败
                emailData.setStatus("FAILED");
                emailData.setErrorLogs(sendResult.getErrorMessage());
                emailData.setAccountId(account.getAccountId());
                emailTrackRecordMapper.insertEmailTrackRecord(emailData);
                return 0; // 表示失败
            }
        } catch (Exception e) {
            logger.error("发送邮件失败: {}", e.getMessage(), e);
            emailData.setStatus("FAILED");
            emailData.setErrorLogs(e.getMessage());
            emailTrackRecordMapper.insertEmailTrackRecord(emailData);
            return 0; // 表示失败
        }
    }

    /**
     * 发送回复邮件
     * 
     * @param replyEmail 回复邮件信息
     * @return 结果
     */
    @Override
    public int sendReply(EmailTrackRecord replyEmail)
    {
        try {
            // 设置回复邮件的基本信息
            replyEmail.setFolderType("sent");
            replyEmail.setStatus("SENT");
            replyEmail.setSentTime(new Date());
            replyEmail.setCreateBy(SecurityUtils.getUsername());
            replyEmail.setCreateTime(new Date());
            replyEmail.setUserId(SecurityUtils.getUserId());
            
            // 生成Message-ID
            String messageId = generateMessageId();
            replyEmail.setMessageId(messageId);
            
            // 根据当前用户获取邮箱账号信息
            Long userId = SecurityUtils.getUserId();
            EmailAccount account = emailAccountService.selectEmailAccountByUserId(userId);
            if (account == null) {
                throw new RuntimeException("未找到用户邮箱账号");
            }
            
            // 设置发件人信息
            replyEmail.setSender(account.getEmailAddress());
            replyEmail.setAccountId(account.getAccountId());
            replyEmail.setIsRead(1); // 发送的邮件默认为已读
            replyEmail.setIsStarred(0);
            replyEmail.setIsImportant(0);
            
            // 构建回复邮件的特殊内容（包含邮件头信息）
            String replyContent = buildReplyContent(replyEmail);
            
            // 发送邮件
            SmtpService.SmtpSendResult sendResult = smtpService.sendEmail(
                account,
                replyEmail.getRecipient(),
                replyEmail.getSubject(),
                replyContent,
                messageId
            );
            
            if (sendResult.isSuccess()) {
                // 发送成功，更新状态
                replyEmail.setStatus("DELIVERED");
                replyEmail.setDeliveredTime(new Date());
                
                // 保存到数据库
                int result = emailTrackRecordMapper.insertEmailTrackRecord(replyEmail);
                
                // 如果这是对某封邮件的回复，更新原邮件的回复状态
                if (replyEmail.getRemark() != null && replyEmail.getRemark().contains("reply_to:")) {
                    String originalMessageId = replyEmail.getRemark().substring(replyEmail.getRemark().indexOf("reply_to:") + 9);
                    EmailTrackRecord originalEmail = emailTrackRecordMapper.selectEmailTrackRecordByMessageId(originalMessageId);
                    if (originalEmail != null) {
                        originalEmail.setRepliedTime(new Date());
                        originalEmail.setUpdateTime(new Date());
                        originalEmail.setUpdateBy(SecurityUtils.getUsername());
                        emailTrackRecordMapper.updateEmailTrackRecord(originalEmail);
                    }
                }
                
                return result;
            } else {
                // 发送失败
                replyEmail.setStatus("FAILED");
                replyEmail.setErrorLogs(sendResult.getErrorMessage());
                emailTrackRecordMapper.insertEmailTrackRecord(replyEmail);
                return 0;
            }
        } catch (Exception e) {
            // 发送异常
            replyEmail.setStatus("FAILED");
            replyEmail.setErrorLogs(e.getMessage());
            emailTrackRecordMapper.insertEmailTrackRecord(replyEmail);
            return 0;
        }
    }

    /**
     * 获取邮件详情（用于回复和转发）
     * 
     * @param emailId 邮件ID
     * @return 邮件详情
     */
    @Override
    public EmailTrackRecord getEmailDetail(Long emailId)
    {
        try {
            EmailTrackRecord emailDetail = emailTrackRecordMapper.selectEmailTrackRecordById(emailId);
            if (emailDetail != null) {
                // 确保用户只能访问自己的邮件
                Long userId = SecurityUtils.getUserId();
                if (!userId.equals(emailDetail.getUserId())) {
                    return null;
                }
            }
            return emailDetail;
        } catch (Exception e) {
            logger.error("获取邮件详情失败: emailId={}", emailId, e);
            return null;
        }
    }

    /**
     * 发送转发邮件
     * 
     * @param forwardEmail 转发邮件信息
     * @return 结果
     */
    @Override
    public int sendForward(EmailTrackRecord forwardEmail)
    {
        try {
            // 设置转发邮件的基本信息
            forwardEmail.setFolderType("sent");
            forwardEmail.setStatus("SENT");
            forwardEmail.setSentTime(new Date());
            forwardEmail.setCreateBy(SecurityUtils.getUsername());
            forwardEmail.setCreateTime(new Date());
            forwardEmail.setUserId(SecurityUtils.getUserId());
            forwardEmail.setIsRead(1); // 发送的邮件默认为已读
            forwardEmail.setIsStarred(0);
            forwardEmail.setIsImportant(0);
            
            // 生成Message-ID
            String messageId = generateMessageId();
            forwardEmail.setMessageId(messageId);
            
            // 根据当前用户获取邮箱账号信息
            Long userId = SecurityUtils.getUserId();
            EmailAccount account = emailAccountService.selectEmailAccountByUserId(userId);
            if (account == null) {
                throw new RuntimeException("未找到用户邮箱账号");
            }
            
            // 设置发件人信息
            forwardEmail.setSender(account.getEmailAddress());
            forwardEmail.setAccountId(account.getAccountId());
            
            // 构建转发邮件的特殊内容
            String forwardContent = buildForwardContent(forwardEmail);
            
            // 发送邮件
            SmtpService.SmtpSendResult sendResult = smtpService.sendEmail(
                account,
                forwardEmail.getRecipient(),
                forwardEmail.getSubject(),
                forwardContent,
                messageId
            );
            
            if (sendResult.isSuccess()) {
                // 发送成功，更新状态
                forwardEmail.setStatus("DELIVERED");
                forwardEmail.setDeliveredTime(new Date());
                
                // 保存到数据库
                return emailTrackRecordMapper.insertEmailTrackRecord(forwardEmail);
            } else {
                // 发送失败
                forwardEmail.setStatus("FAILED");
                forwardEmail.setErrorLogs(sendResult.getErrorMessage());
                emailTrackRecordMapper.insertEmailTrackRecord(forwardEmail);
                return 0;
            }
        } catch (Exception e) {
            // 发送异常
            forwardEmail.setStatus("FAILED");
            forwardEmail.setErrorLogs(e.getMessage());
            emailTrackRecordMapper.insertEmailTrackRecord(forwardEmail);
            return 0;
        }
    }

    /**
     * 构建回复邮件内容
     * 
     * @param replyEmail 回复邮件
     * @return 构建后的内容
     */
    private String buildReplyContent(EmailTrackRecord replyEmail)
    {
        StringBuilder content = new StringBuilder();
        
        // 添加回复内容
        content.append(replyEmail.getContent());
        
        // 如果包含原邮件信息，添加邮件头信息
        if (replyEmail.getRemark() != null && replyEmail.getRemark().contains("reply_to:")) {
            String originalMessageId = replyEmail.getRemark().substring(replyEmail.getRemark().indexOf("reply_to:") + 9);
            EmailTrackRecord originalEmail = emailTrackRecordMapper.selectEmailTrackRecordByMessageId(originalMessageId);
            
            if (originalEmail != null) {
                content.append("\n\n--- 原邮件信息 ---\n");
                content.append("Message-ID: ").append(originalMessageId).append("\n");
                content.append("In-Reply-To: ").append(originalMessageId).append("\n");
                content.append("References: ").append(originalMessageId).append("\n");
                content.append("发件人: ").append(originalEmail.getSender()).append("\n");
                content.append("收件人: ").append(originalEmail.getRecipient()).append("\n");
                content.append("时间: ").append(originalEmail.getSentTime() != null ? originalEmail.getSentTime() : originalEmail.getReceiveTime()).append("\n");
                content.append("主题: ").append(originalEmail.getSubject()).append("\n");
            }
        }
        
        return content.toString();
    }

    /**
     * 构建转发邮件内容
     * 
     * @param forwardEmail 转发邮件
     * @return 构建后的内容
     */
    private String buildForwardContent(EmailTrackRecord forwardEmail)
    {
        StringBuilder content = new StringBuilder();
        
        // 添加转发说明
        content.append(forwardEmail.getContent());
        
        // 如果包含原邮件信息，添加转发邮件头信息
        if (forwardEmail.getRemark() != null && forwardEmail.getRemark().contains("forward_from:")) {
            String originalMessageId = forwardEmail.getRemark().substring(forwardEmail.getRemark().indexOf("forward_from:") + 13);
            EmailTrackRecord originalEmail = emailTrackRecordMapper.selectEmailTrackRecordByMessageId(originalMessageId);
            
            if (originalEmail != null) {
                content.append("\n\n--- 转发邮件信息 ---\n");
                content.append("Message-ID: ").append(originalMessageId).append("\n");
                content.append("X-Forwarded-From: ").append(originalEmail.getSender()).append("\n");
                content.append("X-Forwarded-To: ").append(originalEmail.getRecipient()).append("\n");
                content.append("X-Forwarded-Date: ").append(originalEmail.getSentTime() != null ? originalEmail.getSentTime() : originalEmail.getReceiveTime()).append("\n");
                content.append("X-Forwarded-Subject: ").append(originalEmail.getSubject()).append("\n");
                content.append("发件人: ").append(originalEmail.getSender()).append("\n");
                content.append("收件人: ").append(originalEmail.getRecipient()).append("\n");
                content.append("时间: ").append(originalEmail.getSentTime() != null ? originalEmail.getSentTime() : originalEmail.getReceiveTime()).append("\n");
                content.append("主题: ").append(originalEmail.getSubject()).append("\n");
            }
        }
        
        return content.toString();
    }

    /**
     * 生成Message-ID
     * 
     * @return Message-ID
     */
    private String generateMessageId()
    {
        return "<" + System.currentTimeMillis() + "." + SecurityUtils.getUsername() + "@email-mgr.com>";
    }

    @Override
    public int updateEmailTrackRecord(EmailTrackRecord emailTrackRecord)
    {
        return emailTrackRecordMapper.updateEmailTrackRecord(emailTrackRecord);
    }

    @Override
    public int getDeletedUnreadCount(Long userId)
    {
        EmailTrackRecord queryRecord = new EmailTrackRecord();
        queryRecord.setUserId(userId);
        queryRecord.setFolderType("deleted");
        queryRecord.setIsRead(0); // 0表示未读
        queryRecord.getParams().put("dataScope", "user_id = " + userId);
        List<EmailTrackRecord> records = emailTrackRecordMapper.selectPersonalTrackList(queryRecord);
        return records != null ? records.size() : 0;
    }

    @Override
    public int getStarredUnreadCount(Long userId)
    {
        EmailTrackRecord queryRecord = new EmailTrackRecord();
        queryRecord.setUserId(userId);
        queryRecord.setIsStarred(1); // 1表示星标
        queryRecord.setIsRead(0); // 0表示未读
        queryRecord.getParams().put("dataScope", "user_id = " + userId);
        List<EmailTrackRecord> records = emailTrackRecordMapper.selectPersonalTrackList(queryRecord);
        return records != null ? records.size() : 0;
    }
}
