package com.ruoyi.system.mapper.email;

import java.util.List;

import com.ruoyi.system.domain.email.EmailStatistics;

/**
 * 邮件统计Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailStatisticsMapper
{
    /**
     * 查询邮件统计
     * 
     * @param statId 邮件统计主键
     * @return 邮件统计
     */
    public EmailStatistics selectEmailStatisticsByStatId(Long statId);

    /**
     * 根据Message-ID查询邮件统计
     * 
     * @param messageId 邮件Message-ID
     * @return 邮件统计
     */
    public EmailStatistics selectEmailStatisticsByMessageId(String messageId);

    /**
     * 查询邮件统计列表
     * 
     * @param emailStatistics 邮件统计
     * @return 邮件统计集合
     */
    public List<EmailStatistics> selectEmailStatisticsList(EmailStatistics emailStatistics);

    /**
     * 新增邮件统计
     * 
     * @param emailStatistics 邮件统计
     * @return 结果
     */
    public int insertEmailStatistics(EmailStatistics emailStatistics);

    /**
     * 修改邮件统计
     * 
     * @param emailStatistics 邮件统计
     * @return 结果
     */
    public int updateEmailStatistics(EmailStatistics emailStatistics);

    /**
     * 删除邮件统计
     * 
     * @param statId 邮件统计主键
     * @return 结果
     */
    public int deleteEmailStatisticsByStatId(Long statId);

    /**
     * 批量删除邮件统计
     * 
     * @param statIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailStatisticsByStatIds(Long[] statIds);

    /**
     * 生成每日统计
     * 
     * @param statDate 统计日期
     * @return 结果
     */
    public int generateDailyStatistics(String statDate);

    /**
     * 生成月度统计
     * 
     * @param yearMonth 年月(格式: yyyy-MM)
     * @return 结果
     */
    public int generateMonthlyStatistics(String yearMonth);

    /**
     * 生成年度统计
     * 
     * @param year 年份
     * @return 结果
     */
    public int generateYearlyStatistics(String year);
}


