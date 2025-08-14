package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailAccount;

/**
 * 邮件账号Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailAccountMapper 
{
    /**
     * 查询邮件账号
     * 
     * @param accountId 邮件账号主键
     * @return 邮件账号
     */
    public EmailAccount selectEmailAccountByAccountId(Long accountId);

    /**
     * 查询邮件账号列表
     * 
     * @param emailAccount 邮件账号
     * @return 邮件账号集合
     */
    public List<EmailAccount> selectEmailAccountList(EmailAccount emailAccount);

    /**
     * 新增邮件账号
     * 
     * @param emailAccount 邮件账号
     * @return 结果
     */
    public int insertEmailAccount(EmailAccount emailAccount);

    /**
     * 修改邮件账号
     * 
     * @param emailAccount 邮件账号
     * @return 结果
     */
    public int updateEmailAccount(EmailAccount emailAccount);

    /**
     * 删除邮件账号
     * 
     * @param accountId 邮件账号主键
     * @return 结果
     */
    public int deleteEmailAccountByAccountId(Long accountId);

    /**
     * 批量删除邮件账号
     * 
     * @param accountIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailAccountByAccountIds(Long[] accountIds);

    /**
     * 查询可用的邮件账号
     * 
     * @return 邮件账号集合
     */
    public List<EmailAccount> selectAvailableEmailAccounts();
}
