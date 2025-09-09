package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailAccountMapper;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.EmailServiceMonitorService;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮箱账号Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailAccountServiceImpl implements IEmailAccountService 
{
    private static final Logger logger = LoggerFactory.getLogger(EmailAccountServiceImpl.class);
    
    @Autowired
    private EmailAccountMapper emailAccountMapper;
    
    @Autowired
    private EmailServiceMonitorService emailServiceMonitorService;
    
    @Autowired
    private EmailListener emailListener;

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
     * 根据发件人ID查询邮箱账号列表
     * 
     * @param senderId 发件人ID
     * @return 邮箱账号集合
     */
    @Override
    public List<EmailAccount> selectEmailAccountBySenderId(Long senderId)
    {
        return emailAccountMapper.selectEmailAccountBySenderId(senderId);
    }

    /**
     * 查询邮箱账号列表
     * 
     * @param emailAccount 邮箱账号
     * @return 邮箱账号
     */
    @Override
    public List<EmailAccount> selectEmailAccountList(EmailAccount emailAccount)
    {
        logger.info("EmailAccountService查询账号列表，senderId={}", emailAccount.getSenderId());
        List<EmailAccount> result = emailAccountMapper.selectEmailAccountList(emailAccount);
        logger.info("查询结果数量：{}", result.size());
        return result;
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
        // 加密密码
        if (emailAccount.getPassword() != null && !emailAccount.getPassword().trim().isEmpty()) {
            emailAccount.setPassword(PasswordUtils.encryptPassword(emailAccount.getPassword()));
        }
        if (emailAccount.getImapPassword() != null && !emailAccount.getImapPassword().trim().isEmpty()) {
            emailAccount.setImapPassword(PasswordUtils.encryptPassword(emailAccount.getImapPassword()));
        }
        
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
        // 获取更新前的账号信息，用于比较状态变化
        EmailAccount oldAccount = null;
        if (emailAccount.getAccountId() != null) {
            oldAccount = emailAccountMapper.selectEmailAccountByAccountId(emailAccount.getAccountId());
        }
        
        // 如果密码字段不为空且不是已加密的格式，则加密密码
        if (emailAccount.getPassword() != null && !emailAccount.getPassword().trim().isEmpty()) {
            // 检查是否已经是加密格式（Base64编码的AES加密结果）
            if (!isEncryptedPassword(emailAccount.getPassword())) {
                emailAccount.setPassword(PasswordUtils.encryptPassword(emailAccount.getPassword()));
            }
        }
        if (emailAccount.getImapPassword() != null && !emailAccount.getImapPassword().trim().isEmpty()) {
            // 检查是否已经是加密格式
            if (!isEncryptedPassword(emailAccount.getImapPassword())) {
                emailAccount.setImapPassword(PasswordUtils.encryptPassword(emailAccount.getImapPassword()));
            }
        }
        
        emailAccount.setUpdateTime(DateUtils.getNowDate());
        int result = emailAccountMapper.updateEmailAccount(emailAccount);
        
        // 如果状态发生变化，处理监听和监控服务的启动/停止
        if (oldAccount != null && emailAccount.getStatus() != null && 
            !emailAccount.getStatus().equals(oldAccount.getStatus())) {
            handleAccountStatusChange(emailAccount, oldAccount.getStatus());
        }
        
        return result;
    }
    
    /**
     * 更新邮箱账号统计信息（不加密密码）
     * 
     * @param emailAccount 邮箱账号
     * @return 结果
     */
    @Override
    public int updateEmailAccountStatistics(EmailAccount emailAccount)
    {
        emailAccount.setUpdateTime(DateUtils.getNowDate());
        return emailAccountMapper.updateEmailAccount(emailAccount);
    }
    
    /**
     * 检查密码是否已经是加密格式
     * 
     * @param password 密码
     * @return 是否已加密
     */
    private boolean isEncryptedPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 尝试Base64解码，如果成功且长度合理，则认为是加密的
            byte[] decoded = java.util.Base64.getDecoder().decode(password);
            return decoded.length > 0 && decoded.length % 16 == 0; // AES加密结果长度是16的倍数
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 批量删除邮箱账号
     * 
     * @param accountIds 需要删除的邮箱账号主键
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
     * 获取邮箱账号（包含解密后的密码）
     * 
     * @param accountId 邮箱账号主键
     * @return 邮箱账号
     */
    @Override
    public EmailAccount selectEmailAccountWithDecryptedPassword(Long accountId)
    {
        EmailAccount account = emailAccountMapper.selectEmailAccountByAccountId(accountId);
        if (account != null) {
            // 解密密码
            if (account.getPassword() != null) {
                try {
                    account.setPassword(PasswordUtils.decryptPassword(account.getPassword()));
                } catch (Exception e) {
                    // 如果解密失败，可能是旧数据，保持原样
                }
            }
            if (account.getImapPassword() != null) {
                try {
                    account.setImapPassword(PasswordUtils.decryptPassword(account.getImapPassword()));
                } catch (Exception e) {
                    // 如果解密失败，可能是旧数据，保持原样
                }
            }
        }
        return account;
    }
    


    /**
     * 批量更新邮箱账号状态
     * 
     * @param emailAccount 包含accountIds和status的邮箱账号对象
     * @return 结果
     */
    @Override
    public int batchUpdateAccountStatus(EmailAccount emailAccount)
    {
        emailAccount.setUpdateTime(DateUtils.getNowDate());
        int result = emailAccountMapper.batchUpdateAccountStatus(emailAccount);
        
        // 批量处理状态变化
        if (emailAccount.getAccountIds() != null && emailAccount.getStatus() != null) {
            for (Long accountId : emailAccount.getAccountIds()) {
                try {
                    EmailAccount account = new EmailAccount();
                    account.setAccountId(accountId);
                    account.setStatus(emailAccount.getStatus());
                    
                    if ("0".equals(emailAccount.getStatus())) {
                        // 批量启用
                        EmailAccount fullAccount = emailAccountMapper.selectEmailAccountByAccountId(accountId);
                        if (fullAccount != null) {
                            startAccountServices(fullAccount);
                        }
                    } else if ("1".equals(emailAccount.getStatus())) {
                        // 批量禁用
                        stopAccountServices(accountId);
                    }
                } catch (Exception e) {
                    logger.error("批量处理账号 {} 状态变化时发生错误", accountId, e);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 处理账号状态变化
     * 
     * @param account 更新后的账号信息
     * @param oldStatus 更新前的状态
     */
    private void handleAccountStatusChange(EmailAccount account, String oldStatus) {
        try {
            logger.info("账号 {} 状态发生变化: {} -> {}", 
                account.getEmailAddress(), oldStatus, account.getStatus());
            
            if ("0".equals(account.getStatus())) {
                // 状态变为启用，启动监听和监控
                logger.info("启动账号 {} 的监听和监控服务", account.getEmailAddress());
                startAccountServices(account);
            } else if ("1".equals(account.getStatus())) {
                // 状态变为禁用，停止监听和监控
                logger.info("停止账号 {} 的监听和监控服务", account.getEmailAddress());
                stopAccountServices(account.getAccountId());
            }
        } catch (Exception e) {
            logger.error("处理账号 {} 状态变化时发生错误", account.getEmailAddress(), e);
        }
    }
    
    /**
     * 启动账号的监听和监控服务
     * 
     * @param account 账号信息
     */
    private void startAccountServices(EmailAccount account) {
        try {
            // 启动监控服务
            emailServiceMonitorService.startAccountMonitor(account.getAccountId());
            logger.info("已启动账号 {} 的监控服务", account.getEmailAddress());
            
            // 启动监听服务
            emailListener.addAccountConnection(account);
            logger.info("已启动账号 {} 的监听服务", account.getEmailAddress());
        } catch (Exception e) {
            logger.error("启动账号 {} 服务失败", account.getEmailAddress(), e);
        }
    }
    
    /**
     * 停止账号的监听和监控服务
     * 
     * @param accountId 账号ID
     */
    private void stopAccountServices(Long accountId) {
        try {
            // 停止监控服务
            emailServiceMonitorService.stopAccountMonitor(accountId);
            logger.info("已停止账号 {} 的监控服务", accountId);
            
            // 停止监听服务
            emailListener.removeAccountConnection(accountId);
            logger.info("已停止账号 {} 的监听服务", accountId);
        } catch (Exception e) {
            logger.error("停止账号 {} 服务失败", accountId, e);
        }
    }

    /**
     * 查询邮箱账号模板列表
     * 
     * @param emailAccount 邮箱账号
     * @return 邮箱账号集合
     */
    @Override
    public List<EmailAccount> selectEmailAccountTemplate(EmailAccount emailAccount)
    {
        // 创建一个模板对象，包含示例数据
        EmailAccount template = new EmailAccount();
        template.setSenderId(emailAccount.getSenderId());
        template.setAccountName("示例账号名称");
        template.setEmailAddress("example@example.com");
        template.setPassword("示例密码");
        template.setSmtpHost("smtp.example.com");
        template.setSmtpPort(587);
        template.setSmtpSsl("1");
        template.setImapHost("imap.example.com");
        template.setImapPort(993);
        template.setImapSsl("1");
        template.setImapUsername("example@example.com");
        template.setImapPassword("示例密码");
        template.setDailyLimit(100);
        template.setSendIntervalSeconds(60);
        template.setTrackingEnabled("0");
        template.setWebhookUrl("");
        template.setWebhookSecret("");
        template.setStatus("0");
        template.setRemark("示例备注");
        
        return java.util.Arrays.asList(template);
    }

    /**
     * 导入邮箱账号数据
     * 
     * @param accountList 邮箱账号列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importAccount(List<EmailAccount> accountList, Boolean isUpdateSupport, String operName)
    {
        if (accountList == null || accountList.isEmpty()) {
            return "导入账号数据不能为空！";
        }
        
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        
        for (EmailAccount account : accountList) {
            try {
                // 验证是否存在这个账号
                EmailAccount existAccount = emailAccountMapper.selectEmailAccountByEmailAddress(account.getEmailAddress());
                if (existAccount == null) {
                    // 新增账号
                    account.setCreateBy(operName);
                    account.setCreateTime(DateUtils.getNowDate());
                    this.insertEmailAccount(account);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(account.getEmailAddress()).append(" 导入成功");
                } else if (isUpdateSupport) {
                    // 更新账号
                    account.setAccountId(existAccount.getAccountId());
                    account.setUpdateBy(operName);
                    account.setUpdateTime(DateUtils.getNowDate());
                    this.updateEmailAccount(account);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(account.getEmailAddress()).append(" 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(account.getEmailAddress()).append(" 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + account.getEmailAddress() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
                logger.error(msg, e);
            }
        }
        
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            return failureMsg.toString();
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
            return successMsg.toString();
        }
    }
}
