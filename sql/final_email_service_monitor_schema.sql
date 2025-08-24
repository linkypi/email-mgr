-- 邮件服务监控系统最终数据库结构
-- 确保与 EmailListener 和 EmailServiceMonitorService 完全对应
-- 执行时间：2024-08-22

-- 删除旧表（谨慎执行，会丢失数据）
-- DROP TABLE IF EXISTS `email_service_monitor_log`;
-- DROP TABLE IF EXISTS `email_service_monitor`;

-- 1. 创建邮件服务监控主表
CREATE TABLE IF NOT EXISTS `email_service_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint(20) NOT NULL COMMENT '邮箱账号ID',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `imap_status` varchar(20) DEFAULT 'stopped' COMMENT 'IMAP服务状态：running/stopped/error/connecting/connected/test_success/test_failed',
  `smtp_status` varchar(20) DEFAULT 'stopped' COMMENT 'SMTP服务状态：running/stopped/error/connecting/connected/test_success/test_failed',
  `imap_last_check_time` datetime DEFAULT NULL COMMENT 'IMAP最后检查时间',
  `smtp_last_check_time` datetime DEFAULT NULL COMMENT 'SMTP最后检查时间',
  `imap_error_message` text COMMENT 'IMAP错误信息',
  `smtp_error_message` text COMMENT 'SMTP错误信息',
  `imap_error_time` datetime DEFAULT NULL COMMENT 'IMAP错误时间',
  `smtp_error_time` datetime DEFAULT NULL COMMENT 'SMTP错误时间',
  `monitor_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用监控：0-禁用，1-启用',
  `create_by` varchar(64) DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT 'system' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_id` (`account_id`),
  KEY `idx_email_address` (`email_address`),
  KEY `idx_imap_status` (`imap_status`),
  KEY `idx_smtp_status` (`smtp_status`),
  KEY `idx_monitor_enabled` (`monitor_enabled`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务监控状态表';

-- 2. 创建邮件服务监控日志表
CREATE TABLE IF NOT EXISTS `email_service_monitor_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint(20) DEFAULT NULL COMMENT '邮箱账号ID (全局操作时可为空)',
  `email_address` varchar(255) DEFAULT NULL COMMENT '邮箱地址 (全局操作时可为空)',
  `service_type` varchar(20) NOT NULL COMMENT '服务类型：IMAP/SMTP/GLOBAL/ACCOUNT/NOTIFICATION',
  `status` varchar(20) NOT NULL COMMENT '状态：SUCCESS/FAILED/ERROR/SENT',
  `message` text COMMENT '操作消息或状态信息',
  `check_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '检查时间',
  `response_time` int(11) DEFAULT NULL COMMENT '响应时间(毫秒)',
  `create_by` varchar(64) DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT 'system' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_email_address` (`email_address`),
  KEY `idx_service_type` (`service_type`),
  KEY `idx_status` (`status`),
  KEY `idx_check_time` (`check_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务监控日志表';

-- 3. 添加外键约束（如果需要，可选）
-- ALTER TABLE `email_service_monitor` 
-- ADD CONSTRAINT `fk_monitor_account` 
-- FOREIGN KEY (`account_id`) REFERENCES `email_account` (`account_id`) ON DELETE CASCADE;

-- 4. 插入或更新现有邮箱账号的监控记录
INSERT INTO `email_service_monitor` (
  `account_id`, 
  `email_address`, 
  `imap_status`, 
  `smtp_status`, 
  `monitor_enabled`, 
  `create_time`,
  `update_time`
) 
SELECT 
  `account_id`,
  `email_address`,
  'stopped',
  'stopped',
  1,
  NOW(),
  NOW()
FROM `email_account`
WHERE `deleted` = '0'
  AND `account_id` NOT IN (
    SELECT DISTINCT `account_id` FROM `email_service_monitor`
  );

-- 5. 创建系统配置表（用于存储管理员邮箱等配置）
CREATE TABLE IF NOT EXISTS `email_system_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` varchar(500) DEFAULT NULL COMMENT '配置值',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `config_type` varchar(20) DEFAULT 'string' COMMENT '配置类型：string/number/boolean/json',
  `is_system` tinyint(1) DEFAULT 0 COMMENT '是否系统配置：0-否，1-是',
  `create_by` varchar(64) DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT 'system' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_type` (`config_type`),
  KEY `idx_is_system` (`is_system`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件系统配置表';

-- 6. 插入默认配置
INSERT INTO `email_system_config` (`config_key`, `config_value`, `config_desc`, `config_type`, `is_system`) 
VALUES 
('admin_email', 'admin@example.com', '管理员邮箱地址，用于接收系统告警邮件', 'string', 1),
('notification_enabled', '1', '是否启用邮件通知：0-禁用，1-启用', 'boolean', 1),
('monitor_check_interval', '30', '监控检查间隔（秒）', 'number', 1),
('connection_timeout', '30000', '连接超时时间（毫秒）', 'number', 1),
('idle_timeout', '300000', 'IMAP IDLE超时时间（毫秒）', 'number', 1)
ON DUPLICATE KEY UPDATE 
  `config_desc` = VALUES(`config_desc`),
  `update_time` = NOW();

-- 7. 创建索引优化查询
CREATE INDEX IF NOT EXISTS `idx_monitor_log_time_range` ON `email_service_monitor_log` (`check_time`, `service_type`, `status`);
CREATE INDEX IF NOT EXISTS `idx_monitor_status_combo` ON `email_service_monitor` (`imap_status`, `smtp_status`, `monitor_enabled`);

-- 8. 创建视图简化查询
CREATE OR REPLACE VIEW `v_email_service_status` AS
SELECT 
  m.`id`,
  m.`account_id`,
  m.`email_address`,
  m.`imap_status`,
  m.`smtp_status`,
  m.`imap_last_check_time`,
  m.`smtp_last_check_time`,
  m.`imap_error_message`,
  m.`smtp_error_message`,
  m.`imap_error_time`,
  m.`smtp_error_time`,
  m.`monitor_enabled`,
  a.`account_name`,
  a.`imap_host`,
  a.`smtp_host`,
  a.`status` as `account_status`,
  CASE 
    WHEN m.`imap_status` = 'running' AND m.`smtp_status` = 'running' THEN 'running'
    WHEN m.`imap_status` = 'error' OR m.`smtp_status` = 'error' THEN 'error'
    ELSE 'stopped'
  END as `overall_status`
FROM `email_service_monitor` m
LEFT JOIN `email_account` a ON m.`account_id` = a.`account_id`
WHERE a.`deleted` = '0';

-- 9. 数据完整性检查和清理
-- 删除无效的监控记录（对应的邮箱账号不存在或已删除）
DELETE FROM `email_service_monitor` 
WHERE `account_id` NOT IN (
  SELECT `account_id` FROM `email_account` WHERE `deleted` = '0'
);

-- 10. 显示表结构确认
SHOW CREATE TABLE `email_service_monitor`;
SHOW CREATE TABLE `email_service_monitor_log`;
SHOW CREATE TABLE `email_system_config`;

-- 11. 统计信息
SELECT 'email_service_monitor' as table_name, COUNT(*) as record_count FROM `email_service_monitor`
UNION ALL
SELECT 'email_service_monitor_log' as table_name, COUNT(*) as record_count FROM `email_service_monitor_log`
UNION ALL
SELECT 'email_system_config' as table_name, COUNT(*) as record_count FROM `email_system_config`;

-- 12. 创建存储过程用于清理历史日志（可选）
DELIMITER $$
CREATE PROCEDURE IF NOT EXISTS `sp_cleanup_monitor_logs`(IN days_to_keep INT)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE cleanup_date DATETIME;
    
    SET cleanup_date = DATE_SUB(NOW(), INTERVAL days_to_keep DAY);
    
    DELETE FROM `email_service_monitor_log` 
    WHERE `create_time` < cleanup_date;
    
    SELECT ROW_COUNT() as deleted_rows, cleanup_date as cleanup_before;
END$$
DELIMITER ;

-- 使用示例：清理30天前的日志
-- CALL sp_cleanup_monitor_logs(30);