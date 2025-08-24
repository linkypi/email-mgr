package com.ruoyi.system.service.email.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.domain.email.EmailStatistics;
import com.ruoyi.system.mapper.email.EmailStatisticsMapper;
import com.ruoyi.system.service.email.IEmailStatisticsService;

/**
 * 邮件统计Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailStatisticsServiceImpl implements IEmailStatisticsService
{
    @Autowired
    private EmailStatisticsMapper emailStatisticsMapper;

    /**
     * 查询邮件统计
     * 
     * @param statId 邮件统计主键
     * @return 邮件统计
     */
    @Override
    public EmailStatistics selectEmailStatisticsByStatId(Long statId)
    {
        return emailStatisticsMapper.selectEmailStatisticsByStatId(statId);
    }

    /**
     * 根据Message-ID查询邮件统计
     * 
     * @param messageId 邮件Message-ID
     * @return 邮件统计
     */
    @Override
    public EmailStatistics selectEmailStatisticsByMessageId(String messageId)
    {
        return emailStatisticsMapper.selectEmailStatisticsByMessageId(messageId);
    }

    /**
     * 查询邮件统计列表
     * 
     * @param emailStatistics 邮件统计
     * @return 邮件统计
     */
    @Override
    public List<EmailStatistics> selectEmailStatisticsList(EmailStatistics emailStatistics)
    {
        return emailStatisticsMapper.selectEmailStatisticsList(emailStatistics);
    }

    /**
     * 新增邮件统计
     * 
     * @param emailStatistics 邮件统计
     * @return 结果
     */
    @Override
    public int insertEmailStatistics(EmailStatistics emailStatistics)
    {
        emailStatistics.setCreateTime(new Date());
        return emailStatisticsMapper.insertEmailStatistics(emailStatistics);
    }

    /**
     * 修改邮件统计
     * 
     * @param emailStatistics 邮件统计
     * @return 结果
     */
    @Override
    public int updateEmailStatistics(EmailStatistics emailStatistics)
    {
        emailStatistics.setUpdateTime(new Date());
        return emailStatisticsMapper.updateEmailStatistics(emailStatistics);
    }

    /**
     * 批量删除邮件统计
     * 
     * @param statIds 需要删除的邮件统计主键
     * @return 结果
     */
    @Override
    public int deleteEmailStatisticsByStatIds(Long[] statIds)
    {
        return emailStatisticsMapper.deleteEmailStatisticsByStatIds(statIds);
    }

    /**
     * 删除邮件统计信息
     * 
     * @param statId 邮件统计主键
     * @return 结果
     */
    @Override
    public int deleteEmailStatisticsByStatId(Long statId)
    {
        return emailStatisticsMapper.deleteEmailStatisticsByStatId(statId);
    }

    /**
     * 记录邮件送达
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailDelivered(String messageId)
    {
        EmailStatistics stats = selectEmailStatisticsByMessageId(messageId);
        if (stats != null)
        {
            stats.setDeliveredTime(new Date());
            stats.setStatus("2"); // 已送达
            return updateEmailStatistics(stats);
        }
        return 0;
    }

    /**
     * 记录邮件打开
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailOpened(String messageId)
    {
        EmailStatistics stats = selectEmailStatisticsByMessageId(messageId);
        if (stats != null)
        {
            stats.setOpenedTime(new Date());
            stats.setStatus("3"); // 已打开
            return updateEmailStatistics(stats);
        }
        return 0;
    }

    /**
     * 记录邮件回复
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailReplied(String messageId)
    {
        EmailStatistics stats = selectEmailStatisticsByMessageId(messageId);
        if (stats != null)
        {
            stats.setRepliedTime(new Date());
            stats.setStatus("4"); // 已回复
            return updateEmailStatistics(stats);
        }
        return 0;
    }

    /**
     * 记录邮件点击
     * 
     * @param messageId 邮件Message-ID
     * @return 结果
     */
    @Override
    public int recordEmailClicked(String messageId)
    {
        EmailStatistics stats = selectEmailStatisticsByMessageId(messageId);
        if (stats != null)
        {
            stats.setClickedTime(new Date());
            return updateEmailStatistics(stats);
        }
        return 0;
    }

    /**
     * 生成每日统计
     * 
     * @param statDate 统计日期
     * @return 结果
     */
    @Override
    public int generateDailyStatistics(String statDate)
    {
        return emailStatisticsMapper.generateDailyStatistics(statDate);
    }

    /**
     * 生成月度统计
     * 
     * @param yearMonth 年月(格式: yyyy-MM)
     * @return 结果
     */
    @Override
    public int generateMonthlyStatistics(String yearMonth)
    {
        return emailStatisticsMapper.generateMonthlyStatistics(yearMonth);
    }

    /**
     * 生成年度统计
     * 
     * @param year 年份
     * @return 结果
     */
    @Override
    public int generateYearlyStatistics(String year)
    {
        return emailStatisticsMapper.generateYearlyStatistics(year);
    }
}


