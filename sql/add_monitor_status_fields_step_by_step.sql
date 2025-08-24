-- 添加邮件服务监控表缺失的字段（分步执行版本）
-- 执行时间：2024-01-01
-- 注意：请逐条执行，如果某条报错说明已存在，可以跳过

-- ========================================
-- 第一步：添加 monitor_status 字段
-- ========================================
-- 执行这条语句：
ALTER TABLE `email_service_monitor` 
ADD COLUMN `monitor_status` varchar(20) DEFAULT '0' COMMENT '监控状态：0-停止，1-运行中' AFTER `monitor_enabled`;

-- 如果报错"Duplicate column name 'monitor_status'"，说明字段已存在，可以忽略

-- ========================================
-- 第二步：添加 last_monitor_time 字段
-- ========================================
-- 执行这条语句：
ALTER TABLE `email_service_monitor` 
ADD COLUMN `last_monitor_time` datetime DEFAULT NULL COMMENT '最后监控时间' AFTER `monitor_status`;

-- 如果报错"Duplicate column name 'last_monitor_time'"，说明字段已存在，可以忽略

-- ========================================
-- 第三步：添加 monitor_status 索引
-- ========================================
-- 执行这条语句：
ALTER TABLE `email_service_monitor` 
ADD INDEX `idx_monitor_status` (`monitor_status`);

-- 如果报错"Duplicate key name 'idx_monitor_status'"，说明索引已存在，可以忽略

-- ========================================
-- 第四步：添加 last_monitor_time 索引
-- ========================================
-- 执行这条语句：
ALTER TABLE `email_service_monitor` 
ADD INDEX `idx_last_monitor_time` (`last_monitor_time`);

-- 如果报错"Duplicate key name 'idx_last_monitor_time'"，说明索引已存在，可以忽略

-- ========================================
-- 第五步：更新现有记录的监控状态
-- ========================================
-- 执行这条语句：
UPDATE `email_service_monitor` 
SET `monitor_status` = '0', 
    `last_monitor_time` = NULL 
WHERE `monitor_status` IS NULL;

-- ========================================
-- 第六步：验证字段添加成功
-- ========================================
-- 执行这条语句：
DESCRIBE `email_service_monitor`;

-- ========================================
-- 第七步：验证索引添加成功
-- ========================================
-- 执行这条语句：
SHOW INDEX FROM `email_service_monitor`;
