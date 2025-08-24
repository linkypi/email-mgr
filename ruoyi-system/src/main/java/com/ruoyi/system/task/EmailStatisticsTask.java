package com.ruoyi.system.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.IEmailStatisticsService;
import com.ruoyi.system.service.email.ImapEmailSyncService;

/**
 * 邮件统计定时任务
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Component("emailStatisticsTask")
public class EmailStatisticsTask
{
    private static final Logger logger = LoggerFactory.getLogger(EmailStatisticsTask.class);

    @Autowired
    private IEmailStatisticsService emailStatisticsService;

    @Autowired
    private IEmailAccountService emailAccountService;

    @Autowired
    private ImapEmailSyncService imapEmailSyncService;

    /**
     * 同步邮件统计数据
     * 通过IMAP协议获取邮件送达、打开、回复等统计数据
     */
    public void syncEmailStatistics()
    {
        logger.info("开始执行邮件统计数据同步任务");
        
        try
        {
            // 获取所有启用的邮箱账号
            EmailAccount queryAccount = new EmailAccount();
            queryAccount.setStatus("0"); // 正常状态
            List<EmailAccount> emailAccounts = emailAccountService.selectEmailAccountList(queryAccount);
            
            if (emailAccounts != null && !emailAccounts.isEmpty())
            {
                logger.info("找到 {} 个邮箱账号需要同步统计数据", emailAccounts.size());
                
                // 批量同步所有邮箱账号的邮件统计数据
                imapEmailSyncService.syncAllEmailAccounts(emailAccounts);
            }
            else
            {
                logger.warn("没有找到需要同步的邮箱账号");
            }
            
            logger.info("邮件统计数据同步任务执行完成");
        }
        catch (Exception e)
        {
            logger.error("邮件统计数据同步任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 生成每日统计数据
     */
    public void generateDailyStatistics()
    {
        logger.info("开始执行每日邮件统计数据生成任务");
        
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String yesterday = sdf.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
            
            int result = emailStatisticsService.generateDailyStatistics(yesterday);
            logger.info("每日邮件统计数据生成完成，处理记录数: {}", result);
        }
        catch (Exception e)
        {
            logger.error("每日邮件统计数据生成任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 生成月度统计数据
     */
    public void generateMonthlyStatistics()
    {
        logger.info("开始执行月度邮件统计数据生成任务");
        
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String lastMonth = sdf.format(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
            
            int result = emailStatisticsService.generateMonthlyStatistics(lastMonth);
            logger.info("月度邮件统计数据生成完成，处理记录数: {}", result);
        }
        catch (Exception e)
        {
            logger.error("月度邮件统计数据生成任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 生成年度统计数据
     */
    public void generateYearlyStatistics()
    {
        logger.info("开始执行年度邮件统计数据生成任务");
        
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String lastYear = sdf.format(new Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000));
            
            int result = emailStatisticsService.generateYearlyStatistics(lastYear);
            logger.info("年度邮件统计数据生成完成，处理记录数: {}", result);
        }
        catch (Exception e)
        {
            logger.error("年度邮件统计数据生成任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 清理过期统计数据
     * 清理超过一定时间的统计数据，避免数据过多
     */
    public void cleanExpiredStatistics()
    {
        logger.info("开始执行过期邮件统计数据清理任务");
        
        try
        {
            // 这里可以根据需要实现清理逻辑
            // 例如：清理超过1年的统计数据
            logger.info("过期邮件统计数据清理任务执行完成");
        }
        catch (Exception e)
        {
            logger.error("过期邮件统计数据清理任务执行失败: {}", e.getMessage(), e);
        }
    }
}
