-- 邮件统计定时任务配置
-- 基于若依框架的sys_job表

-- 1. 邮件统计数据同步任务（每小时执行一次）
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(1001, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '0 0 * * * ?', '1', '1', '0', 'admin', NOW(), 'admin', NOW(), '每小时同步一次邮件统计数据，通过IMAP协议获取送达、打开、回复等统计信息');

-- 2. 每日统计数据生成任务（每天凌晨2点执行）
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(1002, '每日邮件统计生成', 'DEFAULT', 'emailStatisticsTask.generateDailyStatistics', '0 0 2 * * ?', '1', '1', '0', 'admin', NOW(), 'admin', NOW(), '每天凌晨2点生成前一天的邮件统计数据');

-- 3. 月度统计数据生成任务（每月1号凌晨3点执行）
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(1003, '月度邮件统计生成', 'DEFAULT', 'emailStatisticsTask.generateMonthlyStatistics', '0 0 3 1 * ?', '1', '1', '0', 'admin', NOW(), 'admin', NOW(), '每月1号凌晨3点生成上个月的邮件统计数据');

-- 4. 年度统计数据生成任务（每年1月1号凌晨4点执行）
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(1004, '年度邮件统计生成', 'DEFAULT', 'emailStatisticsTask.generateYearlyStatistics', '0 0 4 1 1 ?', '1', '1', '0', 'admin', NOW(), 'admin', NOW(), '每年1月1号凌晨4点生成上一年的邮件统计数据');

-- 5. 过期统计数据清理任务（每周日凌晨1点执行）
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(1005, '过期邮件统计清理', 'DEFAULT', 'emailStatisticsTask.cleanExpiredStatistics', '0 0 1 ? * SUN', '1', '1', '0', 'admin', NOW(), 'admin', NOW(), '每周日凌晨1点清理过期的邮件统计数据');

-- 添加邮件统计相关菜单权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(2100, '邮件统计', 2000, 6, 'statistics', 'email/statistics/index', NULL, 1, 0, 'C', '0', '0', 'email:statistics:list', 'chart', 'admin', NOW(), 'admin', NOW(), '邮件统计菜单'),
(2101, '邮件统计查询', 2100, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:query', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2102, '邮件统计新增', 2100, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:add', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2103, '邮件统计修改', 2100, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:edit', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2104, '邮件统计删除', 2100, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:remove', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2105, '邮件统计导出', 2100, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:export', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2106, '邮件统计同步', 2100, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:sync', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2107, '邮件统计生成', 2100, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:generate', '#', 'admin', NOW(), 'admin', NOW(), '');

-- 更新父菜单的order_num，确保邮件统计在正确位置
UPDATE `sys_menu` SET `order_num` = 6 WHERE `menu_id` = 2100;
UPDATE `sys_menu` SET `order_num` = 7 WHERE `menu_id` = 2007; -- 批量操作
UPDATE `sys_menu` SET `order_num` = 8 WHERE `menu_id` = 2008; -- 其他菜单
