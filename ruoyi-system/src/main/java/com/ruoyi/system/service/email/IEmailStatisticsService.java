package com.ruoyi.system.service.email;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.email.EmailStatistics;

/**
 * 邮件统计Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailStatisticsService 
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
     * 批量删除邮件统计
     * 
     * @param statisticsIds 需要删除的邮件统计主键集合
     * @return 结果
     */
    public int deleteEmailStatisticsByStatisticsIds(Long[] statisticsIds);

    /**
     * 删除邮件统计信息
     * 
     * @param statisticsId 邮件统计主键
     * @return 结果
     */
    public int deleteEmailStatisticsByStatisticsId(Long statisticsId);

    /**
     * 获取今日统计数据
     * 
     * @return 统计数据
     */
    public Map<String, Object> getTodayStatistics();

    /**
     * 获取总统计数据
     * 
     * @return 统计数据
     */
    public Map<String, Object> getTotalStatistics();

    /**
     * 获取发送趋势数据
     * 
     * @param days 天数
     * @return 趋势数据
     */
    public List<Map<String, Object>> getSendTrendData(Integer days);

    /**
     * 获取回复率对比数据
     * 
     * @return 对比数据
     */
    public List<Map<String, Object>> getReplyRateComparisonData();

    /**
     * 生成统计数据
     * 
     * @param date 统计日期
     * @return 结果
     */
    public int generateStatistics(String date);
}
