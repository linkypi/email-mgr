-- =============================================
-- 邮件管理系统 - 个人邮件表字段扩展脚本
-- 版本: V1.1.0
-- 创建时间: 2024-01-20
-- 描述: 为个人邮件表添加新字段，支持更多功能
-- =============================================

-- 添加邮件回复状态字段
ALTER TABLE `email_personal` ADD COLUMN `is_replied` tinyint(1) DEFAULT 0 COMMENT '是否已回复：0=否，1=是' AFTER `attachment_count`;

-- 添加邮件送达状态字段
ALTER TABLE `email_personal` ADD COLUMN `is_delivered` tinyint(1) DEFAULT 0 COMMENT '是否已送达：0=否，1=是' AFTER `is_replied`;

-- 添加邮件Message-ID字段（如果不存在）
ALTER TABLE `email_personal` ADD COLUMN `message_id` varchar(255) DEFAULT NULL COMMENT '邮件Message-ID' AFTER `email_id`;

-- 添加相关索引
ALTER TABLE `email_personal` ADD INDEX `idx_is_replied` (`is_replied`);
ALTER TABLE `email_personal` ADD INDEX `idx_is_delivered` (`is_delivered`);
ALTER TABLE `email_personal` ADD INDEX `idx_message_id` (`message_id`);

-- 更新现有数据，设置默认值
UPDATE `email_personal` SET `is_replied` = 0 WHERE `is_replied` IS NULL;
UPDATE `email_personal` SET `is_delivered` = 1 WHERE `is_delivered` IS NULL;

