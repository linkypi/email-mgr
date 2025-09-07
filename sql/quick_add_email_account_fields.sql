-- 快速添加邮箱账户表新字段
USE email_mgr;

-- 添加发送时间间隔字段（秒）
ALTER TABLE `email_account` 
ADD COLUMN `send_interval_seconds` int(11) DEFAULT 60 COMMENT '发送时间间隔(秒)' AFTER `daily_limit`;

-- 添加当天已发送数量字段
ALTER TABLE `email_account` 
ADD COLUMN `daily_sent_count` int(11) DEFAULT 0 COMMENT '当天已发送数量' AFTER `send_interval_seconds`;

-- 添加最后发送日期字段（用于重置每日计数）
ALTER TABLE `email_account` 
ADD COLUMN `last_send_date` date DEFAULT NULL COMMENT '最后发送日期' AFTER `daily_sent_count`;

-- 更新现有数据的默认值
UPDATE `email_account` SET 
    `send_interval_seconds` = 60,
    `daily_sent_count` = 0
WHERE `send_interval_seconds` IS NULL;

SELECT 'Email account table fields added successfully!' as result;
