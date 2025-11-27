-- =============================================
-- 邮件管理系统 - 个人邮件表创建脚本
-- 版本: V1.0.0
-- 创建时间: 2024-01-15
-- 描述: 创建个人邮件表，支持收件箱、发件箱、星标邮件等功能
-- =============================================

-- 个人邮件表
DROP TABLE IF EXISTS `email_personal`;
CREATE TABLE `email_personal` (
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

