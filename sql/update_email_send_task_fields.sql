-- 更新email_send_task表结构
-- 添加缺失的字段

-- 1. 添加account_id字段
ALTER TABLE `email_send_task` 
ADD COLUMN `account_id` bigint(20) DEFAULT NULL COMMENT '发件人账号ID' AFTER `template_id`;

-- 2. 添加group_ids字段 
ALTER TABLE `email_send_task` 
ADD COLUMN `group_ids` text DEFAULT NULL COMMENT '群组ID列表(逗号分隔)' AFTER `recipient_ids`;

-- 3. 添加tag_ids字段
ALTER TABLE `email_send_task` 
ADD COLUMN `tag_ids` text DEFAULT NULL COMMENT '标签ID列表(逗号分隔)' AFTER `group_ids`;

-- 4. 添加contact_ids字段
ALTER TABLE `email_send_task` 
ADD COLUMN `contact_ids` text DEFAULT NULL COMMENT '联系人ID列表(逗号分隔)' AFTER `tag_ids`;

-- 5. 为account_id添加索引
ALTER TABLE `email_send_task` 
ADD INDEX `idx_account_id` (`account_id`);
