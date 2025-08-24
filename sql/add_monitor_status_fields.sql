-- 添加邮件服务监控表缺失的字段
-- 执行时间：2024-01-01

-- 1. 添加 monitor_status 字段
ALTER TABLE `email_service_monitor` 
ADD COLUMN IF NOT EXISTS `monitor_status` varchar(20) DEFAULT '0' COMMENT '监控状态：0-停止，1-运行中' AFTER `monitor_enabled`;

-- 2. 添加 last_monitor_time 字段
ALTER TABLE `email_service_monitor` 
ADD COLUMN IF NOT EXISTS `last_monitor_time` datetime DEFAULT NULL COMMENT '最后监控时间' AFTER `monitor_status`;

-- 3. 添加索引（如果不存在）
-- 注意：MySQL 的 ADD INDEX 不支持 IF NOT EXISTS，需要手动检查
-- 如果索引已存在，执行时会报错，可以忽略错误

-- 添加 monitor_status 索引
ALTER TABLE `email_service_monitor` 
ADD INDEX `idx_monitor_status` (`monitor_status`);

-- 添加 last_monitor_time 索引  
ALTER TABLE `email_service_monitor` 
ADD INDEX `idx_last_monitor_time` (`last_monitor_time`);

-- 4. 更新现有记录的监控状态
UPDATE `email_service_monitor` 
SET `monitor_status` = '0', 
    `last_monitor_time` = NULL 
WHERE `monitor_status` IS NULL;

-- 5. 验证字段添加成功
DESCRIBE `email_service_monitor`;
