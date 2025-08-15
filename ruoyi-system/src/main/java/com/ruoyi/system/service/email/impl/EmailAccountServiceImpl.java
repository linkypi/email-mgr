package com.ruoyi.system.service.email.impl;

import java.util.List;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailAccountMapper;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;

/**
 * 邮箱账号Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-01-01
 */
@Service
public class EmailAccountServiceImpl implements IEmailAccountService 
{
    @Autowired
    private EmailAccountMapper emailAccountMapper;

    /**
     * 查询邮箱账号
     * 
     * @param accountId 邮箱账号主键
     * @return 邮箱账号
     */
    @Override
    public EmailAccount selectEmailAccountByAccountId(Long accountId)
    {
        return emailAccountMapper.selectEmailAccountByAccountId(accountId);
    }

    /**
     * 查询邮箱账号列表
     * 
     * @param emailAccount 邮箱账号
     * @return 邮箱账号集合
     */
    @Override
    public List<EmailAccount> selectEmailAccountList(EmailAccount emailAccount)
    {
        return emailAccountMapper.selectEmailAccountList(emailAccount);
    }

    /**
     * 新增邮箱账号
     * 
     * @param emailAccount 邮箱账号
     * @return 结果
     */
    @Override
    public int insertEmailAccount(EmailAccount emailAccount)
    {
        emailAccount.setCreateBy(SecurityUtils.getUsername());
        emailAccount.setCreateTime(DateUtils.getNowDate());
        return emailAccountMapper.insertEmailAccount(emailAccount);
    }

    /**
     * 修改邮箱账号
     * 
     * @param emailAccount 邮箱账号
     * @return 结果
     */
    @Override
    public int updateEmailAccount(EmailAccount emailAccount)
    {
        emailAccount.setUpdateBy(SecurityUtils.getUsername());
        emailAccount.setUpdateTime(DateUtils.getNowDate());
        return emailAccountMapper.updateEmailAccount(emailAccount);
    }

    /**
     * 批量删除邮箱账号
     * 
     * @param accountIds 需要删除的邮箱账号主键集合
     * @return 结果
     */
    @Override
    public int deleteEmailAccountByAccountIds(Long[] accountIds)
    {
        return emailAccountMapper.deleteEmailAccountByAccountIds(accountIds);
    }

    /**
     * 删除邮箱账号信息
     * 
     * @param accountId 邮箱账号主键
     * @return 结果
     */
    @Override
    public int deleteEmailAccountByAccountId(Long accountId)
    {
        return emailAccountMapper.deleteEmailAccountByAccountId(accountId);
    }

    /**
     * 测试邮箱账号连接
     * 
     * @param emailAccount 邮箱账号
     * @return 结果
     */
    @Override
    public boolean testEmailAccount(EmailAccount emailAccount)
    {
        try
        {
            Properties props = new Properties();
            props.put("mail.smtp.host", emailAccount.getSmtpHost());
            props.put("mail.smtp.port", emailAccount.getSmtpPort());
            props.put("mail.smtp.auth", "true");
            
            if ("1".equals(emailAccount.getSmtpSsl()))
            {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.trust", emailAccount.getSmtpHost());
            }

            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            transport.connect(emailAccount.getSmtpHost(), emailAccount.getEmailAddress(), emailAccount.getPassword());
            transport.close();
            return true;
        }
        catch (Exception e)
        {
            throw new ServiceException("邮箱连接测试失败：" + e.getMessage());
        }
    }

    /**
     * 获取可用邮箱账号列表
     * 
     * @return 邮箱账号集合
     */
    @Override
    public List<EmailAccount> getAvailableAccounts()
    {
        return emailAccountMapper.selectAvailableAccounts();
    }
}
