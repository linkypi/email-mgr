package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailServiceMonitor;

/**
 * 邮件服务监控Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailServiceMonitorMapper 
{
    /**
     * 查询邮件服务监控
     * 
     * @param id 邮件服务监控主键
     * @return 邮件服务监控
     */
    public EmailServiceMonitor selectEmailServiceMonitorById(Long id);

    /**
     * 根据邮箱账号ID查询邮件服务监控
     * 
     * @param accountId 邮箱账号ID
     * @return 邮件服务监控
     */
    public EmailServiceMonitor selectEmailServiceMonitorByAccountId(Long accountId);

    /**
     * 查询邮件服务监控列表
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 邮件服务监控集合
     */
    public List<EmailServiceMonitor> selectEmailServiceMonitorList(EmailServiceMonitor emailServiceMonitor);

    /**
     * 新增邮件服务监控
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 结果
     */
    public int insertEmailServiceMonitor(EmailServiceMonitor emailServiceMonitor);

    /**
     * 修改邮件服务监控
     * 
     * @param emailServiceMonitor 邮件服务监控
     * @return 结果
     */
    public int updateEmailServiceMonitor(EmailServiceMonitor emailServiceMonitor);

    /**
     * 删除邮件服务监控
     * 
     * @param id 邮件服务监控主键
     * @return 结果
     */
    public int deleteEmailServiceMonitorById(Long id);

    /**
     * 批量删除邮件服务监控
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailServiceMonitorByIds(Long[] ids);

    /**
     * 根据邮箱账号ID删除邮件服务监控
     * 
     * @param accountId 邮箱账号ID
     * @return 结果
     */
    public int deleteEmailServiceMonitorByAccountId(Long accountId);
}
