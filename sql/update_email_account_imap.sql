-- 更新邮箱账号表结构，添加IMAP相关配置字段
-- 用于支持邮件统计和跟踪功能

-- 1. 添加IMAP相关字段
ALTER TABLE `email_account` 
ADD COLUMN `imap_host` varchar(100) DEFAULT NULL COMMENT 'IMAP服务器' AFTER `smtp_ssl`,
ADD COLUMN `imap_port` int(5) DEFAULT NULL COMMENT 'IMAP端口' AFTER `imap_host`,
ADD COLUMN `imap_ssl` char(1) DEFAULT '1' COMMENT 'IMAP是否启用SSL(0否 1是)' AFTER `imap_port`,
ADD COLUMN `imap_username` varchar(200) DEFAULT NULL COMMENT 'IMAP用户名(通常与邮箱地址相同)' AFTER `imap_ssl`,
ADD COLUMN `imap_password` varchar(200) DEFAULT NULL COMMENT 'IMAP密码(加密，通常与SMTP密码相同)' AFTER `imap_username`,
ADD COLUMN `webhook_url` varchar(500) DEFAULT NULL COMMENT 'Webhook回调地址' AFTER `imap_password`,
ADD COLUMN `webhook_secret` varchar(200) DEFAULT NULL COMMENT 'Webhook密钥' AFTER `webhook_url`,
ADD COLUMN `tracking_enabled` char(1) DEFAULT '1' COMMENT '是否启用邮件跟踪(0否 1是)' AFTER `webhook_secret`,
ADD COLUMN `last_sync_time` datetime DEFAULT NULL COMMENT '最后同步时间' AFTER `last_send_time`;

-- 2. 为现有邮箱账号设置默认的IMAP配置
-- Gmail配置
UPDATE `email_account` SET 
    `imap_host` = 'imap.gmail.com',
    `imap_port` = 993,
    `imap_ssl` = '1',
    `imap_username` = `email_address`,
    `imap_password` = `password`,
    `webhook_url` = CONCAT('https://your-domain.com/email/webhook/callback'),
    `tracking_enabled` = '1'
WHERE `email_address` LIKE '%@gmail.com' AND `imap_host` IS NULL;

-- QQ邮箱配置
UPDATE `email_account` SET 
    `imap_host` = 'imap.qq.com',
    `imap_port` = 993,
    `imap_ssl` = '1',
    `imap_username` = `email_address`,
    `imap_password` = `password`,
    `webhook_url` = CONCAT('https://your-domain.com/email/webhook/callback'),
    `tracking_enabled` = '1'
WHERE `email_address` LIKE '%@qq.com' AND `imap_host` IS NULL;

-- 163邮箱配置
UPDATE `email_account` SET 
    `imap_host` = 'imap.163.com',
    `imap_port` = 993,
    `imap_ssl` = '1',
    `imap_username` = `email_address`,
    `imap_password` = `password`,
    `webhook_url` = CONCAT('https://your-domain.com/email/webhook/callback'),
    `tracking_enabled` = '1'
WHERE `email_address` LIKE '%@163.com' AND `imap_host` IS NULL;

-- Outlook/Hotmail配置
UPDATE `email_account` SET 
    `imap_host` = 'outlook.office365.com',
    `imap_port` = 993,
    `imap_ssl` = '1',
    `imap_username` = `email_address`,
    `imap_password` = `password`,
    `webhook_url` = CONCAT('https://your-domain.com/email/webhook/callback'),
    `tracking_enabled` = '1'
WHERE (`email_address` LIKE '%@outlook.com' OR `email_address` LIKE '%@hotmail.com') AND `imap_host` IS NULL;

-- 其他邮箱配置（使用通用IMAP设置）
UPDATE `email_account` SET 
    `imap_host` = CONCAT('imap.', SUBSTRING_INDEX(`email_address`, '@', -1)),
    `imap_port` = 993,
    `imap_ssl` = '1',
    `imap_username` = `email_address`,
    `imap_password` = `password`,
    `webhook_url` = CONCAT('https://your-domain.com/email/webhook/callback'),
    `tracking_enabled` = '1'
WHERE `imap_host` IS NULL;

-- 3. 显示更新结果
SELECT 
    account_id,
    account_name,
    email_address,
    smtp_host,
    smtp_port,
    imap_host,
    imap_port,
    imap_ssl,
    tracking_enabled,
    webhook_url,
    status
FROM `email_account` 
ORDER BY account_id;

-- 4. 添加索引以提高查询性能
CREATE INDEX IF NOT EXISTS `idx_email_account_imap` ON `email_account` (`imap_host`, `imap_port`);
CREATE INDEX IF NOT EXISTS `idx_email_account_tracking` ON `email_account` (`tracking_enabled`);
CREATE INDEX IF NOT EXISTS `idx_email_account_sync_time` ON `email_account` (`last_sync_time`);

