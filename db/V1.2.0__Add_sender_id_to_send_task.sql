-- =============================================
-- 邮件管理系统 - 发送任务表添加发件人ID字段
-- 版本: V1.2.0
-- 创建时间: 2024-01-25
-- 描述: 为发送任务表添加发件人ID字段，支持发件人轮换功能
-- =============================================

-- 检查email_send_task表是否存在，如果不存在则创建
CREATE TABLE IF NOT EXISTS `email_send_task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` varchar(255) NOT NULL COMMENT '任务名称',
  `template_id` bigint(20) DEFAULT NULL COMMENT '模板ID',
  `sender_id` bigint(20) DEFAULT NULL COMMENT '发件人ID',
  `status` varchar(20) DEFAULT 'pending' COMMENT '任务状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件发送任务表';

-- 为email_send_task表添加sender_id字段（如果不存在）
ALTER TABLE `email_send_task` ADD COLUMN `sender_id` bigint(20) DEFAULT NULL COMMENT '发件人ID' AFTER `template_id`;

-- 添加外键约束（可选，根据实际需要）
-- ALTER TABLE `email_send_task` ADD CONSTRAINT `fk_send_task_sender` FOREIGN KEY (`sender_id`) REFERENCES `email_account` (`account_id`) ON DELETE SET NULL;

-- 添加索引
ALTER TABLE `email_send_task` ADD INDEX `idx_sender_id` (`sender_id`);
ALTER TABLE `email_send_task` ADD INDEX `idx_status` (`status`);

-- 更新现有数据，设置默认发件人ID（如果有email_account表的话）
-- UPDATE `email_send_task` SET `sender_id` = (SELECT `account_id` FROM `email_account` LIMIT 1) WHERE `sender_id` IS NULL;

