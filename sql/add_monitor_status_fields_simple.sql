-- 添加邮件服务监控表缺失的字段（简化版本）
-- 执行时间：2024-01-01
-- 注意：请分步执行，如果某步报错说明已存在，可以跳过

-- 步骤1：添加 monitor_status 字段
ALTER TABLE `email_service_monitor` 
ADD COLUMN IF NOT EXISTS `monitor_status` varchar(20) DEFAULT '0' COMMENT '监控状态：0-停止，1-运行中' AFTER `monitor_enabled`;

-- 步骤2：添加 last_monitor_time 字段
ALTER TABLE `email_service_monitor` 
ADD COLUMN IF NOT EXISTS `last_monitor_time` datetime DEFAULT NULL COMMENT '最后监控时间' AFTER `monitor_status`;

-- 步骤3：尝试添加 monitor_status 索引
-- 如果报错"Duplicate key name"，说明索引已存在，可以忽略
ALTER TABLE `email_service_monitor` 
ADD INDEX `idx_monitor_status` (`monitor_status`);

-- 步骤4：尝试添加 last_monitor_time 索引
-- 如果报错"Duplicate key name"，说明索引已存在，可以忽略
ALTER TABLE `email_service_monitor` 
ADD INDEX `idx_last_monitor_time` (`last_monitor_time`);

-- 步骤5：更新现有记录的监控状态
UPDATE `email_service_monitor` 
SET `monitor_status` = '0', 
    `last_monitor_time` = NULL 
WHERE `monitor_status` IS NULL;

-- 步骤6：验证字段添加成功
DESCRIBE `email_service_monitor`;

-- 步骤7：验证索引添加成功
SHOW INDEX FROM `email_service_monitor`;

