-- 为email_statistics表添加account_id字段
-- 解决ImapEmailSyncService.createEmailStatistics方法中的字段缺失问题

-- 检查表结构，确定使用哪个表
-- 如果存在statistics_id字段，说明使用的是email_statistics_table.sql中的表结构
-- 如果存在stat_id字段，说明使用的是email_system_tables.sql中的表结构

-- 方案1：为使用statistics_id的表添加account_id字段
ALTER TABLE `email_statistics` 
ADD COLUMN `account_id` bigint(20) DEFAULT NULL COMMENT '邮箱账号ID' AFTER `statistics_id`;

-- 为account_id添加索引
CREATE INDEX `idx_account_id` ON `email_statistics` (`account_id`);

-- 方案2：为使用stat_id的表添加account_id字段（如果方案1失败）
-- ALTER TABLE `email_statistics` 
-- ADD COLUMN `account_id` bigint(20) DEFAULT NULL COMMENT '邮箱账号ID' AFTER `stat_id`;

-- 为account_id添加索引
-- CREATE INDEX `idx_account_id` ON `email_statistics` (`account_id`);

-- 验证字段添加结果
DESCRIBE `email_statistics`;
