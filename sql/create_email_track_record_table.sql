-- 创建邮件跟踪记录表
-- 执行时间：2024-01-01

-- 创建邮件跟踪记录表
CREATE TABLE `email_track_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `message_id` varchar(255) NOT NULL COMMENT '邮件Message-ID',
  `subject` varchar(500) DEFAULT NULL COMMENT '邮件主题',
  `recipient` varchar(255) DEFAULT NULL COMMENT '收件人',
  `sender` varchar(255) DEFAULT NULL COMMENT '发件人',
  `content` text COMMENT '邮件内容',
  `status` varchar(50) DEFAULT 'PENDING' COMMENT '邮件状态',
  `sent_time` datetime DEFAULT NULL COMMENT '发送时间',
  `delivered_time` datetime DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime DEFAULT NULL COMMENT '回复时间',
  `clicked_time` datetime DEFAULT NULL COMMENT '点击时间',
  `retry_count` int(11) DEFAULT 0 COMMENT '重试次数',
  `error_logs` text COMMENT '错误日志',
  `account_id` bigint(20) DEFAULT NULL COMMENT '邮箱账号ID',
  `tracking_pixel_url` varchar(500) DEFAULT NULL COMMENT '跟踪像素URL',
  `tracking_link_url` varchar(500) DEFAULT NULL COMMENT '跟踪链接URL',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sent_time` (`sent_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件跟踪记录表';

-- 查看表结构
DESCRIBE email_track_record;

-- 查看索引
SHOW INDEX FROM email_track_record;
