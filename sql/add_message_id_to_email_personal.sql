-- 为email_personal表添加message_id字段
-- 执行时间：2024年

-- 1. 添加message_id字段
ALTER TABLE `email_personal` 
ADD COLUMN `message_id` varchar(255) DEFAULT NULL COMMENT '邮件Message-ID' AFTER `email_id`;

-- 2. 为message_id字段添加索引，提高查询性能
CREATE INDEX `idx_message_id` ON `email_personal` (`message_id`);

-- 3. 验证字段添加结果
DESCRIBE `email_personal`;

-- 4. 验证索引创建结果
SHOW INDEX FROM `email_personal` WHERE Key_name = 'idx_message_id';

