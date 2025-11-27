-- 个人邮件功能数据库设置脚本
-- 确保所有必要的表和字段都存在

-- 1. 检查并创建个人邮件表
CREATE TABLE IF NOT EXISTS `email_personal` (
  `email_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '邮件ID',
  `message_id` varchar(255) DEFAULT NULL COMMENT '邮件Message-ID',
  `from_address` varchar(255) NOT NULL COMMENT '发件人邮箱',
  `to_address` varchar(255) NOT NULL COMMENT '收件人邮箱',
  `subject` varchar(500) DEFAULT NULL COMMENT '邮件主题',
  `content` text COMMENT '邮件内容',
  `html_content` longtext COMMENT 'HTML内容',
  `status` varchar(20) DEFAULT 'unread' COMMENT '邮件状态(unread=未读,read=已读,starred=星标,deleted=已删除)',
  `is_starred` tinyint(1) DEFAULT 0 COMMENT '是否星标(0=否,1=是)',
  `is_important` tinyint(1) DEFAULT 0 COMMENT '是否重要(0=否,1=是)',
  `receive_time` datetime DEFAULT NULL COMMENT '接收时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `email_type` varchar(20) DEFAULT 'inbox' COMMENT '邮件类型(inbox=收件箱,sent=发件箱,starred=星标,deleted=已删除)',
  `attachment_count` int(11) DEFAULT 0 COMMENT '附件数量',
  `is_replied` tinyint(1) DEFAULT 0 COMMENT '是否已回复：0=否，1=是',
  `is_delivered` tinyint(1) DEFAULT 0 COMMENT '是否已送达：0=否，1=是',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`email_id`),
  KEY `idx_from_address` (`from_address`),
  KEY `idx_to_address` (`to_address`),
  KEY `idx_status` (`status`),
  KEY `idx_email_type` (`email_type`),
  KEY `idx_receive_time` (`receive_time`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_create_by` (`create_by`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_is_replied` (`is_replied`),
  KEY `idx_is_delivered` (`is_delivered`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='个人邮件表';

-- 2. 插入一些测试数据
INSERT IGNORE INTO `email_personal` VALUES 
(1, '<test1@example.com>', 'sender1@example.com', 'user@example.com', '测试邮件1', '这是一封测试邮件的内容', '<p>这是一封测试邮件的内容</p>', 'unread', 0, 1, '2024-01-15 10:00:00', NULL, 'inbox', 0, 0, 1, NULL, 'admin', '2024-01-15 10:00:00', NULL, NULL, '测试邮件'),
(2, '<test2@example.com>', 'user@example.com', 'recipient1@example.com', '发送的邮件1', '这是我发送的邮件内容', '<p>这是我发送的邮件内容</p>', 'sent', 1, 0, NULL, '2024-01-15 11:00:00', 'sent', 1, 0, 1, NULL, 'admin', '2024-01-15 11:00:00', NULL, NULL, '发送的邮件'),
(3, '<test3@example.com>', 'sender2@example.com', 'user@example.com', '重要邮件', '这是一封重要邮件', '<p>这是一封重要邮件</p>', 'read', 1, 1, '2024-01-15 12:00:00', NULL, 'inbox', 0, 1, 1, NULL, 'admin', '2024-01-15 12:00:00', NULL, NULL, '重要邮件'),
(4, '<test4@example.com>', 'user@example.com', 'recipient2@example.com', '已删除的邮件', '这是已删除的邮件', '<p>这是已删除的邮件</p>', 'read', 0, 0, NULL, '2024-01-15 13:00:00', 'deleted', 0, 0, 1, '2024-01-15 14:00:00', 'admin', '2024-01-15 13:00:00', NULL, NULL, '已删除的邮件');

-- 3. 验证数据
SELECT 
    '收件箱邮件数' as type,
    COUNT(*) as count
FROM `email_personal` 
WHERE `email_type` = 'inbox'

UNION ALL

SELECT 
    '发件箱邮件数' as type,
    COUNT(*) as count
FROM `email_personal` 
WHERE `email_type` = 'sent'

UNION ALL

SELECT 
    '星标邮件数' as type,
    COUNT(*) as count
FROM `email_personal` 
WHERE `is_starred` = 1

UNION ALL

SELECT 
    '已删除邮件数' as type,
    COUNT(*) as count
FROM `email_personal` 
WHERE `email_type` = 'deleted'

UNION ALL

SELECT 
    '未读邮件数' as type,
    COUNT(*) as count
FROM `email_personal` 
WHERE `status` = 'unread' AND `email_type` = 'inbox';

















