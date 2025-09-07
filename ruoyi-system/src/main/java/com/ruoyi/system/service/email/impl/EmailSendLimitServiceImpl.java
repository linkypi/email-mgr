package com.ruoyi.system.service.email.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.mapper.email.EmailAccountMapper;
import com.ruoyi.system.service.email.EmailSendLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 邮件发送限制服务实现
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Service
public class EmailSendLimitServiceImpl implements EmailSendLimitService 
{
    private static final Logger log = LoggerFactory.getLogger(EmailSendLimitServiceImpl.class);
    
    @Autowired
    private EmailAccountMapper emailAccountMapper;
    
    @Override
    public boolean isSendLimitReached(Long accountId) 
    {
        // 直接调用每日限制检查
        return isDailySendLimitReached(accountId);
    }
    
    @Override
    public boolean canSendToday(Long accountId) 
    {
        // 检查是否达到每日发送上限
        return !isDailySendLimitReached(accountId);
    }
    
    @Override
    public boolean isDailySendLimitReached(Long accountId) 
    {
        try 
        {
            EmailAccount account = emailAccountMapper.selectEmailAccountByAccountId(accountId);
            if (account == null) 
            {
                log.warn("邮箱账户不存在: {}", accountId);
                return true;
            }
            
            // 检查是否需要重置每日计数
            resetDailyCountIfNeeded(account);
            
            // 重新获取账户信息（可能已被重置）
            account = emailAccountMapper.selectEmailAccountByAccountId(accountId);
            
            // 检查每日发送数量
            if (account.getDailyLimit() != null && account.getDailySentCount() != null) 
            {
                if (account.getDailySentCount() >= account.getDailyLimit()) 
                {
                    log.info("邮箱 {} 已达到每日发送上限: {}/{}", 
                        account.getEmailAddress(), account.getDailySentCount(), account.getDailyLimit());
                    return true;
                }
            }
            
            return false;
        } 
        catch (Exception e) 
        {
            log.error("检查每日发送限制失败: {}", e.getMessage(), e);
            return true; // 出错时保守处理，认为已达到上限
        }
    }
    
    @Override
    public List<EmailAccount> getAvailableAccounts(List<Long> accountIds) 
    {
        if (accountIds == null || accountIds.isEmpty()) 
        {
            return new ArrayList<>();
        }
        
        try 
        {
            List<EmailAccount> accounts = new ArrayList<>();
            for (Long accountId : accountIds) 
            {
                EmailAccount account = emailAccountMapper.selectEmailAccountByAccountId(accountId);
                if (account != null && !isSendLimitReached(accountId)) 
                {
                    accounts.add(account);
                }
            }
            
            log.info("从 {} 个邮箱中筛选出 {} 个可用邮箱", accountIds.size(), accounts.size());
            return accounts;
        } 
        catch (Exception e) 
        {
            log.error("获取可用邮箱列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public EmailAccount getNextAvailableAccount(List<Long> accountIds) 
    {
        List<EmailAccount> availableAccounts = getAvailableAccounts(accountIds);
        if (availableAccounts.isEmpty()) 
        {
            log.warn("没有可用的发件邮箱");
            return null;
        }
        
        // 按使用次数排序，优先使用使用次数较少的邮箱
        availableAccounts.sort((a, b) -> {
            int aCount = a.getUsedCount() != null ? a.getUsedCount() : 0;
            int bCount = b.getUsedCount() != null ? b.getUsedCount() : 0;
            return Integer.compare(aCount, bCount);
        });
        
        EmailAccount selectedAccount = availableAccounts.get(0);
        log.info("选择邮箱: {} (使用次数: {})", selectedAccount.getEmailAddress(), selectedAccount.getUsedCount());
        return selectedAccount;
    }
    
    @Override
    @Transactional
    public int updateSendCount(Long accountId) 
    {
        try 
        {
            EmailAccount account = emailAccountMapper.selectEmailAccountByAccountId(accountId);
            if (account == null) 
            {
                log.warn("邮箱账户不存在: {}", accountId);
                return 0;
            }
            
            // 重置每日计数（如果需要）
            resetDailyCountIfNeeded(account);
            
            // 更新发送计数
            int dailyCount = account.getUsedCount() != null ? account.getUsedCount() + 1 : 1;
            int dailySentCount = account.getDailySentCount() != null ? account.getDailySentCount() + 1 : 1;
            
            account.setUsedCount(dailyCount);
            account.setDailySentCount(dailySentCount);
            account.setLastSendTime(DateUtils.getTime());
            account.setLastSendDate(DateUtils.getDate());
            account.setUpdateTime(DateUtils.getNowDate());
            
            int result = emailAccountMapper.updateEmailAccount(account);
            log.info("更新邮箱 {} 发送计数: 每日={}, 当天={}", 
                account.getEmailAddress(), dailyCount, dailySentCount);
            
            return result;
        } 
        catch (Exception e) 
        {
            log.error("更新发送计数失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public int resetDailySendCount() 
    {
        try 
        {
            int result = emailAccountMapper.resetDailySendCount();
            log.info("重置每日发送计数完成，影响行数: {}", result);
            return result;
        } 
        catch (Exception e) 
        {
            log.error("重置每日发送计数失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    
    @Override
    public int getSendInterval(Long accountId) 
    {
        try 
        {
            EmailAccount account = emailAccountMapper.selectEmailAccountByAccountId(accountId);
            if (account == null || account.getSendIntervalSeconds() == null) 
            {
                return 60; // 默认60秒
            }
            return account.getSendIntervalSeconds();
        } 
        catch (Exception e) 
        {
            log.error("获取发送间隔失败: {}", e.getMessage(), e);
            return 60; // 默认60秒
        }
    }
    
    @Override
    public int getRandomSendInterval(Long accountId) 
    {
        int baseInterval = getSendInterval(accountId);
        // 在基础间隔的基础上增加 ±20% 的随机性
        Random random = new Random();
        int variation = (int) (baseInterval * 0.2);
        int randomVariation = random.nextInt(variation * 2) - variation;
        int finalInterval = baseInterval + randomVariation;
        
        // 确保最小间隔为10秒
        return Math.max(finalInterval, 10);
    }
    
    /**
     * 检查并重置每日计数（如果需要）
     * 
     * @param account 邮箱账户
     */
    private void resetDailyCountIfNeeded(EmailAccount account) 
    {
        if (account.getLastSendDate() == null) 
        {
            return;
        }
        
        try 
        {
            Date lastSendDate = DateUtils.parseDate(account.getLastSendDate());
            Date now = new Date();
            
            // 如果距离上次发送超过1天，重置每日计数
            long diffInDays = (now.getTime() - lastSendDate.getTime()) / (1000 * 60 * 60 * 24);
            if (diffInDays >= 1) 
            {
                account.setDailySentCount(0);
                account.setLastSendDate(DateUtils.getDate());
                emailAccountMapper.updateEmailAccount(account);
                log.info("重置邮箱 {} 的每日发送计数", account.getEmailAddress());
            }
        } 
        catch (Exception e) 
        {
            log.error("重置每日计数失败: {}", e.getMessage(), e);
        }
    }
}
