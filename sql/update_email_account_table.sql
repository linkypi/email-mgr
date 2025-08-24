-- 更新邮箱账号表，添加监听状态相关字段
-- 执行时间：2024-01-01

-- 添加 IMAP 监听状态字段
ALTER TABLE email_account ADD COLUMN listener_status VARCHAR(20) DEFAULT 'stopped' COMMENT 'IMAP监听状态(running=运行中, stopped=已停止, error=错误)';

-- 添加同步邮件数量字段
ALTER TABLE email_account ADD COLUMN sync_count INT DEFAULT 0 COMMENT '同步邮件数量';

-- 添加错误信息字段
ALTER TABLE email_account ADD COLUMN error_message TEXT COMMENT '错误信息';

-- 更新现有数据的监听状态为已停止
UPDATE email_account SET listener_status = 'stopped' WHERE listener_status IS NULL;

-- 更新现有数据的同步数量为0
UPDATE email_account SET sync_count = 0 WHERE sync_count IS NULL;

-- 添加索引以提高查询性能
CREATE INDEX idx_email_account_listener_status ON email_account(listener_status);
CREATE INDEX idx_email_account_email_address ON email_account(email_address);

-- 查看表结构
DESCRIBE email_account;
