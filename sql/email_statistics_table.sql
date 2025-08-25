-- 邮件统计表
CREATE TABLE IF NOT EXISTS `email_statistics` (
  `statistics_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `subject` varchar(500) DEFAULT NULL COMMENT '邮件主题',
  `sender_email` varchar(100) DEFAULT NULL COMMENT '发件人邮箱',
  `recipient_email` varchar(100) DEFAULT NULL COMMENT '收件人邮箱',
  `message_id` varchar(200) DEFAULT NULL COMMENT '邮件ID（邮件服务器返回的唯一标识）',
  `status` char(1) DEFAULT '0' COMMENT '发送状态（0：发送中，1：已发送，2：已送达，3：已打开，4：已回复，5：发送失败）',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `delivered_time` datetime DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime DEFAULT NULL COMMENT '回复时间',
  `open_count` int(11) DEFAULT 0 COMMENT '打开次数',
  `last_open_time` datetime DEFAULT NULL COMMENT '最后打开时间',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `location` varchar(200) DEFAULT NULL COMMENT '地理位置',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `browser_type` varchar(50) DEFAULT NULL COMMENT '浏览器类型',
  `operating_system` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `deleted` char(1) DEFAULT '0' COMMENT '是否删除（0：未删除，1：已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`statistics_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_status` (`status`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_recipient_email` (`recipient_email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='邮件统计表';

-- 添加索引以提高查询性能
CREATE INDEX IF NOT EXISTS `idx_email_statistics_task_status` ON `email_statistics` (`task_id`, `status`);
CREATE INDEX IF NOT EXISTS `idx_email_statistics_create_time` ON `email_statistics` (`create_time`);

