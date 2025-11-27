-- =============================================
-- 邮件管理系统 - 个人邮件状态字段修复脚本
-- 版本: V1.1.1
-- 创建时间: 2024-01-20
-- 描述: 修复个人邮件表状态字段，统一状态值
-- =============================================

-- 更新邮件状态，统一状态值
UPDATE `email_personal` SET `status` = 'unread' WHERE `status` = '0' OR `status` = '未读';
UPDATE `email_personal` SET `status` = 'read' WHERE `status` = '1' OR `status` = '已读';
UPDATE `email_personal` SET `status` = 'starred' WHERE `status` = '2' OR `status` = '星标';
UPDATE `email_personal` SET `status` = 'deleted' WHERE `status` = '3' OR `status` = '已删除';

-- 更新邮件类型，统一类型值
UPDATE `email_personal` SET `email_type` = 'inbox' WHERE `email_type` = '1' OR `email_type` = '收件';
UPDATE `email_personal` SET `email_type` = 'sent' WHERE `email_type` = '2' OR `email_type` = '发件';
UPDATE `email_personal` SET `email_type` = 'starred' WHERE `email_type` = '3' OR `email_type` = '星标';
UPDATE `email_personal` SET `email_type` = 'deleted' WHERE `email_type` = '4' OR `email_type` = '已删除';

-- 更新布尔字段，统一为0/1
UPDATE `email_personal` SET `is_starred` = 0 WHERE `is_starred` IS NULL OR `is_starred` = '0' OR `is_starred` = '否';
UPDATE `email_personal` SET `is_starred` = 1 WHERE `is_starred` = '1' OR `is_starred` = '是';

UPDATE `email_personal` SET `is_important` = 0 WHERE `is_important` IS NULL OR `is_important` = '0' OR `is_important` = '否';
UPDATE `email_personal` SET `is_important` = 1 WHERE `is_important` = '1' OR `is_important` = '是';

UPDATE `email_personal` SET `is_replied` = 0 WHERE `is_replied` IS NULL OR `is_replied` = '0' OR `is_replied` = '否';
UPDATE `email_personal` SET `is_replied` = 1 WHERE `is_replied` = '1' OR `is_replied` = '是';

UPDATE `email_personal` SET `is_delivered` = 0 WHERE `is_delivered` IS NULL OR `is_delivered` = '0' OR `is_delivered` = '否';
UPDATE `email_personal` SET `is_delivered` = 1 WHERE `is_delivered` = '1' OR `is_delivered` = '是';

