package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailServiceMonitorLog;

/**
 * 邮件服务监控记录Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailServiceMonitorLogMapper 
{
    /**
     * 查询邮件服务监控记录
     * 
     * @param id 邮件服务监控记录主键
     * @return 邮件服务监控记录
     */
    public EmailServiceMonitorLog selectEmailServiceMonitorLogById(Long id);

    /**
     * 查询邮件服务监控记录列表
     * 
     * @param emailServiceMonitorLog 邮件服务监控记录
     * @return 邮件服务监控记录集合
     */
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogList(EmailServiceMonitorLog emailServiceMonitorLog);

    /**
     * 根据邮箱账号ID查询邮件服务监控记录列表
     * 
     * @param accountId 邮箱账号ID
     * @return 邮件服务监控记录集合
     */
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogByAccountId(Long accountId);

    /**
     * 根据服务类型查询邮件服务监控记录列表
     * 
     * @param serviceType 服务类型
     * @return 邮件服务监控记录集合
     */
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogByServiceType(String serviceType);

    /**
     * 根据操作结果查询邮件服务监控记录列表
     * 
     * @param operationResult 操作结果
     * @return 邮件服务监控记录集合
     */
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogByOperationResult(String operationResult);

    /**
     * 新增邮件服务监控记录
     * 
     * @param emailServiceMonitorLog 邮件服务监控记录
     * @return 结果
     */
    public int insertEmailServiceMonitorLog(EmailServiceMonitorLog emailServiceMonitorLog);

    /**
     * 修改邮件服务监控记录
     * 
     * @param emailServiceMonitorLog 邮件服务监控记录
     * @return 结果
     */
    public int updateEmailServiceMonitorLog(EmailServiceMonitorLog emailServiceMonitorLog);

    /**
     * 删除邮件服务监控记录
     * 
     * @param id 邮件服务监控记录主键
     * @return 结果
     */
    public int deleteEmailServiceMonitorLogById(Long id);

    /**
     * 批量删除邮件服务监控记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailServiceMonitorLogByIds(Long[] ids);

    /**
     * 根据邮箱账号ID删除邮件服务监控记录
     * 
     * @param accountId 邮箱账号ID
     * @return 结果
     */
    public int deleteEmailServiceMonitorLogByAccountId(Long accountId);

    /**
     * 统计邮箱账号的操作记录
     * 
     * @param accountId 邮箱账号ID
     * @return 统计结果
     */
    public List<EmailServiceMonitorLog> selectEmailServiceMonitorLogStatsByAccountId(Long accountId);
    
    /**
     * 清理历史日志
     * 
     * @param days 保留天数
     * @return 结果
     */
    public int cleanHistoryLogs(int days);
}
