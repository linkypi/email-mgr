-- 创建邮件服务监控相关表
-- 执行时间：2024-01-01

-- 创建邮件服务监控表
CREATE TABLE `email_service_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint(20) NOT NULL COMMENT '邮箱账号ID',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `imap_status` char(1) DEFAULT '0' COMMENT 'IMAP服务状态（0=停止,1=运行中,2=异常）',
  `imap_last_check_time` datetime DEFAULT NULL COMMENT 'IMAP最后检查时间',
  `imap_error_message` text COMMENT 'IMAP异常信息',
  `imap_error_time` datetime DEFAULT NULL COMMENT 'IMAP异常时间',
  `smtp_status` char(1) DEFAULT '0' COMMENT 'SMTP服务状态（0=停止,1=运行中,2=异常）',
  `smtp_last_check_time` datetime DEFAULT NULL COMMENT 'SMTP最后检查时间',
  `smtp_error_message` text COMMENT 'SMTP异常信息',
  `smtp_error_time` datetime DEFAULT NULL COMMENT 'SMTP异常时间',
  `monitor_status` char(1) DEFAULT '0' COMMENT '监控状态（0=停止,1=运行中）',
  `last_monitor_time` datetime DEFAULT NULL COMMENT '最后监控时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_id` (`account_id`),
  KEY `idx_email_address` (`email_address`),
  KEY `idx_imap_status` (`imap_status`),
  KEY `idx_smtp_status` (`smtp_status`),
  KEY `idx_monitor_status` (`monitor_status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务监控表';

-- 创建邮件服务监控记录表
CREATE TABLE `email_service_monitor_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint(20) NOT NULL COMMENT '邮箱账号ID',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `service_type` varchar(10) NOT NULL COMMENT '服务类型（IMAP=IMAP服务,SMTP=SMTP服务）',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型（START=启动,STOP=停止,RESTART=重启,TEST=测试,MONITOR=监控）',
  `operation_result` varchar(10) NOT NULL COMMENT '操作结果（SUCCESS=成功,FAILED=失败）',
  `operation_message` varchar(500) DEFAULT NULL COMMENT '操作消息',
  `error_details` text COMMENT '错误详情',
  `operation_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_email_address` (`email_address`),
  KEY `idx_service_type` (`service_type`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operation_result` (`operation_result`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务监控记录表';

-- 查看表结构
DESCRIBE email_service_monitor;
DESCRIBE email_service_monitor_log;

-- 查看索引
SHOW INDEX FROM email_service_monitor;
SHOW INDEX FROM email_service_monitor_log;
