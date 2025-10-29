package com.ruoyi.system.service.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailSender;

/**
 * 发件人信息Service接口
 * 
 * @author ruoyi
 * @date 2025-01-01
 */
public interface IEmailSenderService 
{
    /**
     * 查询发件人信息
     * 
     * @param senderId 发件人信息主键
     * @return 发件人信息
     */
    public EmailSender selectEmailSenderBySenderId(Long senderId);

    /**
     * 查询发件人信息列表
     * 
     * @param emailSender 发件人信息
     * @return 发件人信息集合
     */
    public List<EmailSender> selectEmailSenderList(EmailSender emailSender);

    /**
     * 新增发件人信息
     * 
     * @param emailSender 发件人信息
     * @return 结果
     */
    public int insertEmailSender(EmailSender emailSender);

    /**
     * 修改发件人信息
     * 
     * @param emailSender 发件人信息
     * @return 结果
     */
    public int updateEmailSender(EmailSender emailSender);

    /**
     * 批量删除发件人信息
     * 
     * @param senderIds 需要删除的发件人信息主键集合
     * @return 结果
     */
    public int deleteEmailSenderBySenderIds(Long[] senderIds);

    /**
     * 删除发件人信息信息
     * 
     * @param senderId 发件人信息主键
     * @return 结果
     */
    public int deleteEmailSenderBySenderId(Long senderId);

    /**
     * 查询发件人信息及其关联的邮箱账号
     * 
     * @param senderId 发件人信息主键
     * @return 发件人信息
     */
    public EmailSender selectEmailSenderWithAccounts(Long senderId);

    /**
     * 更新发件人统计信息
     * 
     * @param emailSender 发件人信息
     * @return 结果
     */
    public int updateEmailSenderStatistics(EmailSender emailSender);

    /**
     * 批量更新发件人状态
     * 
     * @param emailSender 包含senderIds和status的发件人对象
     * @return 结果
     */
    public int batchUpdateSenderStatus(EmailSender emailSender);

    /**
     * 获取发件人选项列表（用于下拉选择）
     * 
     * @return 发件人信息集合
     */
    public List<EmailSender> selectEmailSenderOptions();
}



















