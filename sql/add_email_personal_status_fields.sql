-- 为email_personal表添加回复和送达状态字段
-- 执行时间：2024年

-- 1. 添加is_replied字段（是否已回复）
ALTER TABLE `email_personal` 
ADD COLUMN `is_replied` tinyint(1) DEFAULT 0 COMMENT '是否已回复：0=否，1=是' AFTER `attachment_count`;

-- 2. 添加is_delivered字段（是否已送达）
ALTER TABLE `email_personal` 
ADD COLUMN `is_delivered` tinyint(1) DEFAULT 0 COMMENT '是否已送达：0=否，1=是' AFTER `is_replied`;

-- 3. 为新增字段添加索引，提高查询性能
CREATE INDEX `idx_is_replied` ON `email_personal` (`is_replied`);
CREATE INDEX `idx_is_delivered` ON `email_personal` (`is_delivered`);

-- 4. 验证字段添加结果
DESCRIBE `email_personal`;

-- 5. 验证索引创建结果
SHOW INDEX FROM `email_personal` WHERE Key_name IN ('idx_is_replied', 'idx_is_delivered');

