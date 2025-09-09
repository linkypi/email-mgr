-- 修复email_statistics表缺少account_id字段的问题
-- 执行此脚本来解决SQL错误

USE email_mgr;

-- 检查当前表结构
DESCRIBE email_statistics;

-- 添加account_id字段
ALTER TABLE `email_statistics` 
ADD COLUMN `account_id` bigint(20) DEFAULT NULL COMMENT '邮箱账号ID' AFTER `stat_id`;

-- 为account_id添加索引
CREATE INDEX `idx_account_id` ON `email_statistics` (`account_id`);

-- 验证字段添加结果
DESCRIBE `email_statistics`;

-- 显示成功信息
SELECT 'email_statistics表已成功添加account_id字段' AS result;
