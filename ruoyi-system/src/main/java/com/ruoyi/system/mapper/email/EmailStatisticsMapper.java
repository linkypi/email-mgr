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
     * @param statisticsId 邮件统计主键
     * @return 邮件统计
     */
    public EmailStatistics selectEmailStatisticsByStatisticsId(Long statisticsId);

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
     * @param statisticsId 邮件统计主键
     * @return 结果
     */
    public int deleteEmailStatisticsByStatisticsId(Long statisticsId);

    /**
     * 批量删除邮件统计
     * 
     * @param statisticsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailStatisticsByStatisticsIds(Long[] statisticsIds);

    /**
     * 获取今日统计数据
     * 
     * @return 今日统计数据
     */
    public EmailStatistics getTodayStatistics();

    /**
     * 获取总统计数据
     * 
     * @return 总统计数据
     */
    public EmailStatistics getTotalStatistics();

    /**
     * 获取发送趋势数据
     * 
     * @param days 天数
     * @return 趋势数据
     */
    public List<EmailStatistics> getSendTrendData(int days);

    /**
     * 获取回复率对比数据
     * 
     * @return 对比数据
     */
    public List<EmailStatistics> getReplyRateComparisonData();
}
