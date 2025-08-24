-- 更新邮件跟踪记录表，添加缺失的字段
-- 执行时间：2024-01-01

-- 添加 create_by 字段
ALTER TABLE `email_track_record` 
ADD COLUMN `create_by` varchar(64) DEFAULT '' COMMENT '创建者' AFTER `tracking_link_url`;

-- 添加 update_by 字段
ALTER TABLE `email_track_record` 
ADD COLUMN `update_by` varchar(64) DEFAULT '' COMMENT '更新者' AFTER `create_time`;

-- 添加 deleted 字段
ALTER TABLE `email_track_record` 
ADD COLUMN `deleted` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）' AFTER `update_time`;

-- 添加 deleted 字段的索引
ALTER TABLE `email_track_record` 
ADD INDEX `idx_deleted` (`deleted`);

-- 查看更新后的表结构
DESCRIBE email_track_record;

-- 查看更新后的索引
SHOW INDEX FROM email_track_record;
