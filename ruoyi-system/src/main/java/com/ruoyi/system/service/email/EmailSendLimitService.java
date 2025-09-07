package com.ruoyi.system.service.email;

import com.ruoyi.system.domain.email.EmailAccount;
import java.util.List;

/**
 * 邮件发送限制服务接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public interface EmailSendLimitService 
{
    /**
     * 检查邮箱是否达到发送上限（综合检查每日限制）
     * 
     * @param accountId 邮箱账户ID
     * @return true-达到上限，false-未达到上限
     */
    boolean isSendLimitReached(Long accountId);
    
    /**
     * 检查邮箱是否达到每日发送上限（基于当天已发送数量）
     * 
     * @param accountId 邮箱账户ID
     * @return true-达到上限，false-未达到上限
     */
    boolean isDailySendLimitReached(Long accountId);
    
    /**
     * 检查邮箱是否可以在当天继续发送
     * 
     * @param accountId 邮箱账户ID
     * @return true-可以发送，false-已达到当天上限
     */
    boolean canSendToday(Long accountId);
    
    /**
     * 获取可用的发件邮箱列表（未达到发送上限的）
     * 
     * @param accountIds 邮箱账户ID列表
     * @return 可用的邮箱账户列表
     */
    List<EmailAccount> getAvailableAccounts(List<Long> accountIds);
    
    /**
     * 获取下一个可用的发件邮箱
     * 
     * @param accountIds 邮箱账户ID列表
     * @return 可用的邮箱账户，如果没有则返回null
     */
    EmailAccount getNextAvailableAccount(List<Long> accountIds);
    
    /**
     * 更新邮箱发送计数
     * 
     * @param accountId 邮箱账户ID
     * @return 更新结果
     */
    int updateSendCount(Long accountId);
    
    /**
     * 重置每日发送计数（定时任务调用）
     * 
     * @return 重置结果
     */
    int resetDailySendCount();
    
    /**
     * 获取发送间隔时间（秒）
     * 
     * @param accountId 邮箱账户ID
     * @return 发送间隔时间（秒）
     */
    int getSendInterval(Long accountId);
    
    /**
     * 获取随机发送间隔时间（在配置的间隔基础上增加随机性）
     * 
     * @param accountId 邮箱账户ID
     * @return 随机发送间隔时间（秒）
     */
    int getRandomSendInterval(Long accountId);
}
