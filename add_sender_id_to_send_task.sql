-- 为email_send_task表添加sender_id字段
USE email_mgr;

-- 添加sender_id字段
ALTER TABLE `email_send_task` 
ADD COLUMN `sender_id` bigint(20) DEFAULT NULL COMMENT '发件人ID' AFTER `account_id`;

-- 创建索引
CREATE INDEX `idx_sender_id` ON `email_send_task` (`sender_id`);

-- 查看表结构
DESCRIBE email_send_task;
