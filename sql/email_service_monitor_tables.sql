-- 邮件服务监控状态表
CREATE TABLE IF NOT EXISTS `email_service_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint(20) NOT NULL COMMENT '邮箱账号ID',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `imap_status` varchar(20) DEFAULT 'stopped' COMMENT 'IMAP服务状态：running/stopped/error',
  `smtp_status` varchar(20) DEFAULT 'stopped' COMMENT 'SMTP服务状态：running/stopped/error',
  `imap_last_check_time` datetime DEFAULT NULL COMMENT 'IMAP最后检查时间',
  `smtp_last_check_time` datetime DEFAULT NULL COMMENT 'SMTP最后检查时间',
  `imap_error_message` text COMMENT 'IMAP错误信息',
  `smtp_error_message` text COMMENT 'SMTP错误信息',
  `imap_error_time` datetime DEFAULT NULL COMMENT 'IMAP错误时间',
  `smtp_error_time` datetime DEFAULT NULL COMMENT 'SMTP错误时间',
  `monitor_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用监控：0-禁用，1-启用',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_id` (`account_id`),
  KEY `idx_email_address` (`email_address`),
  KEY `idx_imap_status` (`imap_status`),
  KEY `idx_smtp_status` (`smtp_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务监控状态表';

-- 邮件服务监控日志表
CREATE TABLE IF NOT EXISTS `email_service_monitor_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint(20) NOT NULL COMMENT '邮箱账号ID',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `service_type` varchar(10) NOT NULL COMMENT '服务类型：IMAP/SMTP',
  `status` varchar(20) NOT NULL COMMENT '状态：success/error',
  `message` text COMMENT '状态信息',
  `check_time` datetime NOT NULL COMMENT '检查时间',
  `response_time` int(11) DEFAULT NULL COMMENT '响应时间(毫秒)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_email_address` (`email_address`),
  KEY `idx_service_type` (`service_type`),
  KEY `idx_status` (`status`),
  KEY `idx_check_time` (`check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务监控日志表';

-- 插入一些测试数据
INSERT INTO `email_service_monitor` (`account_id`, `email_address`, `imap_status`, `smtp_status`, `monitor_enabled`, `create_time`) 
SELECT `account_id`, `email_address`, 'stopped', 'stopped', 1, NOW() 
FROM `email_account` 
WHERE `deleted` = 0;
