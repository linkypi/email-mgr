package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailAccount;

/**
 * 邮箱账号Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailAccountMapper 
{
    /**
     * 查询邮箱账号
     * 
     * @param accountId 邮箱账号主键
     * @return 邮箱账号
     */
    public EmailAccount selectEmailAccountByAccountId(Long accountId);

    /**
     * 查询邮箱账号列表
     * 
     * @param emailAccount 邮箱账号
     * @return 邮箱账号集合
     */
    public List<EmailAccount> selectEmailAccountList(EmailAccount emailAccount);

    /**
     * 新增邮箱账号
     * 
     * @param emailAccount 邮箱账号
     * @return 结果
     */
    public int insertEmailAccount(EmailAccount emailAccount);

    /**
     * 修改邮箱账号
     * 
     * @param emailAccount 邮箱账号
     * @return 结果
     */
    public int updateEmailAccount(EmailAccount emailAccount);

    /**
     * 删除邮箱账号
     * 
     * @param accountId 邮箱账号主键
     * @return 结果
     */
    public int deleteEmailAccountByAccountId(Long accountId);

    /**
     * 批量删除邮箱账号
     * 
     * @param accountIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailAccountByAccountIds(Long[] accountIds);
    


    /**
     * 批量更新邮箱账号状态
     * 
     * @param emailAccount 包含accountIds和status的邮箱账号对象
     * @return 结果
     */
    public int batchUpdateAccountStatus(EmailAccount emailAccount);
    
    /**
     * 重置每日发送计数
     * 
     * @return 结果
     */
    public int resetDailySendCount();
}
