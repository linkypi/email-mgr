-- 邮件发送任务调度器定时任务配置
-- 插入到sys_job表中

-- 1. 邮件发送任务检查定时任务（每分钟检查一次）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
('EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', '0 * * * * ?', '1', '1', '0', 'admin', NOW(), 'admin', NOW(), '邮件发送任务检查定时任务 - 每分钟检查一次即将启动的发送任务');

-- 2. 邮件统计数据同步定时任务（每5分钟同步一次）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
('EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', '0 */5 * * * ?', '1', '1', '0', 'admin', NOW(), 'admin', NOW(), '邮件统计数据同步定时任务 - 每5分钟同步一次邮件统计数据');

-- 如果已存在相同名称的任务，则更新
-- 更新邮件发送任务检查定时任务
UPDATE `sys_job` SET 
    `invoke_target` = 'emailTaskScheduler.checkPendingEmailTasks()',
    `cron_expression` = '0 * * * * ?',
    `misfire_policy` = '1',
    `concurrent` = '1',
    `status` = '0',
    `update_by` = 'admin',
    `update_time` = NOW(),
    `remark` = '邮件发送任务检查定时任务 - 每分钟检查一次即将启动的发送任务'
WHERE `job_name` = 'EmailTaskScheduler' AND `job_group` = 'DEFAULT';

-- 更新邮件统计数据同步定时任务
UPDATE `sys_job` SET 
    `invoke_target` = 'emailTaskScheduler.syncEmailStatistics()',
    `cron_expression` = '0 */5 * * * ?',
    `misfire_policy` = '1',
    `concurrent` = '1',
    `status` = '0',
    `update_by` = 'admin',
    `update_time` = NOW(),
    `remark` = '邮件统计数据同步定时任务 - 每5分钟同步一次邮件统计数据'
WHERE `job_name` = 'EmailStatisticsSync' AND `job_group` = 'DEFAULT';

-- 查看已配置的定时任务
SELECT 
    job_id,
    job_name,
    job_group,
    invoke_target,
    cron_expression,
    misfire_policy,
    concurrent,
    status,
    remark,
    create_time
FROM `sys_job` 
WHERE `job_name` IN ('EmailTaskScheduler', 'EmailStatisticsSync')
ORDER BY `job_name`;
