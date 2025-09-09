package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailSenderMapper;
import com.ruoyi.system.domain.email.EmailSender;
import com.ruoyi.system.service.email.IEmailSenderService;
import com.ruoyi.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发件人信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-01
 */
@Service
public class EmailSenderServiceImpl implements IEmailSenderService 
{
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderServiceImpl.class);
    
    @Autowired
    private EmailSenderMapper emailSenderMapper;

    /**
     * 查询发件人信息
     * 
     * @param senderId 发件人信息主键
     * @return 发件人信息
     */
    @Override
    public EmailSender selectEmailSenderBySenderId(Long senderId)
    {
        return emailSenderMapper.selectEmailSenderBySenderId(senderId);
    }

    /**
     * 查询发件人信息列表
     * 
     * @param emailSender 发件人信息
     * @return 发件人信息
     */
    @Override
    public List<EmailSender> selectEmailSenderList(EmailSender emailSender)
    {
        return emailSenderMapper.selectEmailSenderList(emailSender);
    }

    /**
     * 新增发件人信息
     * 
     * @param emailSender 发件人信息
     * @return 结果
     */
    @Override
    public int insertEmailSender(EmailSender emailSender)
    {
        emailSender.setCreateTime(DateUtils.getNowDate());
        return emailSenderMapper.insertEmailSender(emailSender);
    }

    /**
     * 修改发件人信息
     * 
     * @param emailSender 发件人信息
     * @return 结果
     */
    @Override
    public int updateEmailSender(EmailSender emailSender)
    {
        emailSender.setUpdateTime(DateUtils.getNowDate());
        return emailSenderMapper.updateEmailSender(emailSender);
    }

    /**
     * 批量删除发件人信息
     * 
     * @param senderIds 需要删除的发件人信息主键
     * @return 结果
     */
    @Override
    public int deleteEmailSenderBySenderIds(Long[] senderIds)
    {
        return emailSenderMapper.deleteEmailSenderBySenderIds(senderIds);
    }

    /**
     * 删除发件人信息信息
     * 
     * @param senderId 发件人信息主键
     * @return 结果
     */
    @Override
    public int deleteEmailSenderBySenderId(Long senderId)
    {
        return emailSenderMapper.deleteEmailSenderBySenderId(senderId);
    }

    /**
     * 查询发件人信息及其关联的邮箱账号
     * 
     * @param senderId 发件人信息主键
     * @return 发件人信息
     */
    @Override
    public EmailSender selectEmailSenderWithAccounts(Long senderId)
    {
        return emailSenderMapper.selectEmailSenderWithAccounts(senderId);
    }

    /**
     * 更新发件人统计信息
     * 
     * @param emailSender 发件人信息
     * @return 结果
     */
    @Override
    public int updateEmailSenderStatistics(EmailSender emailSender)
    {
        return emailSenderMapper.updateEmailSenderStatistics(emailSender);
    }

    /**
     * 批量更新发件人状态
     * 
     * @param emailSender 包含senderIds和status的发件人对象
     * @return 结果
     */
    @Override
    public int batchUpdateSenderStatus(EmailSender emailSender)
    {
        return emailSenderMapper.batchUpdateSenderStatus(emailSender);
    }

    /**
     * 获取发件人选项列表（用于下拉选择）
     * 
     * @return 发件人信息集合
     */
    @Override
    public List<EmailSender> selectEmailSenderOptions()
    {
        EmailSender emailSender = new EmailSender();
        emailSender.setStatus("0"); // 只查询正常状态的发件人
        return emailSenderMapper.selectEmailSenderList(emailSender);
    }
}

