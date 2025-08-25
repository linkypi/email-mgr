-- 修复email_personal表缺失字段
-- 执行此脚本以添加邮件监控功能所需的字段

-- 添加is_replied字段（是否已回复）
ALTER TABLE `email_personal` 
ADD COLUMN `is_replied` tinyint(1) DEFAULT 0 COMMENT '是否已回复：0=否，1=是' AFTER `attachment_count`;

-- 添加is_delivered字段（是否已送达）
ALTER TABLE `email_personal` 
ADD COLUMN `is_delivered` tinyint(1) DEFAULT 0 COMMENT '是否已送达：0=否，1=是' AFTER `is_replied`;

-- 为新增字段添加索引，提高查询性能
CREATE INDEX `idx_is_replied` ON `email_personal` (`is_replied`);
CREATE INDEX `idx_is_delivered` ON `email_personal` (`is_delivered`);

