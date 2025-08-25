-- 更新邮箱账号表，添加 IMAP 相关字段
-- 执行时间：2024-01-01

-- 添加 IMAP 相关字段
ALTER TABLE email_account ADD COLUMN imap_host VARCHAR(255) COMMENT 'IMAP服务器地址';
ALTER TABLE email_account ADD COLUMN imap_port INT DEFAULT 993 COMMENT 'IMAP端口';
ALTER TABLE email_account ADD COLUMN imap_ssl VARCHAR(1) DEFAULT '1' COMMENT 'IMAP是否启用SSL(0否 1是)';
ALTER TABLE email_account ADD COLUMN imap_username VARCHAR(255) COMMENT 'IMAP用户名';
ALTER TABLE email_account ADD COLUMN imap_password VARCHAR(255) COMMENT 'IMAP密码(加密)';

-- 添加 Webhook 相关字段
ALTER TABLE email_account ADD COLUMN webhook_url VARCHAR(500) COMMENT 'Webhook回调地址';
ALTER TABLE email_account ADD COLUMN webhook_secret VARCHAR(255) COMMENT 'Webhook密钥';

-- 添加邮件跟踪字段
ALTER TABLE email_account ADD COLUMN tracking_enabled VARCHAR(1) DEFAULT '0' COMMENT '是否启用邮件跟踪(0否 1是)';

-- 添加最后同步时间字段（如果不存在）
ALTER TABLE email_account ADD COLUMN last_sync_time DATETIME COMMENT '最后同步时间';

-- 更新现有数据的 IMAP 配置（根据邮箱地址自动设置）
UPDATE email_account SET 
    imap_host = CASE 
        WHEN email_address LIKE '%@gmail.com' THEN 'imap.gmail.com'
        WHEN email_address LIKE '%@qq.com' THEN 'imap.qq.com'
        WHEN email_address LIKE '%@163.com' THEN 'imap.163.com'
        WHEN email_address LIKE '%@126.com' THEN 'imap.126.com'
        WHEN email_address LIKE '%@sina.com' THEN 'imap.sina.com'
        WHEN email_address LIKE '%@outlook.com' OR email_address LIKE '%@hotmail.com' THEN 'outlook.office365.com'
        ELSE 'imap.example.com'
    END,
    imap_port = CASE 
        WHEN email_address LIKE '%@gmail.com' THEN 993
        WHEN email_address LIKE '%@qq.com' THEN 993
        WHEN email_address LIKE '%@163.com' THEN 993
        WHEN email_address LIKE '%@126.com' THEN 993
        WHEN email_address LIKE '%@sina.com' THEN 993
        WHEN email_address LIKE '%@outlook.com' OR email_address LIKE '%@hotmail.com' THEN 993
        ELSE 993
    END,
    imap_ssl = '1',
    imap_username = email_address
WHERE imap_host IS NULL;

-- 添加索引以提高查询性能
CREATE INDEX idx_email_account_imap_host ON email_account(imap_host);
CREATE INDEX idx_email_account_tracking_enabled ON email_account(tracking_enabled);

-- 查看表结构
DESCRIBE email_account;

