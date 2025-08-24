-- 为email_account表添加监控相关字段
-- 执行前请备份数据库

-- 添加监控启用字段
ALTER TABLE email_account ADD COLUMN monitor_enabled CHAR(1) DEFAULT '0' COMMENT '是否启用监控(0否 1是)';

-- 添加自动启动监听字段
ALTER TABLE email_account ADD COLUMN auto_start_monitor CHAR(1) DEFAULT '0' COMMENT '是否自动启动监听(0否 1是)';

-- 添加监控优先级字段
ALTER TABLE email_account ADD COLUMN monitor_priority VARCHAR(10) DEFAULT 'medium' COMMENT '监控优先级(high=高, medium=中, low=低)';

-- 添加监控状态字段
ALTER TABLE email_account ADD COLUMN monitor_status VARCHAR(20) DEFAULT 'stopped' COMMENT '监控状态(running=运行中, stopped=已停止, error=错误, connecting=连接中)';

-- 添加IMAP监控状态字段
ALTER TABLE email_account ADD COLUMN imap_monitor_status VARCHAR(20) DEFAULT 'stopped' COMMENT 'IMAP监控状态';

-- 添加SMTP监控状态字段
ALTER TABLE email_account ADD COLUMN smtp_monitor_status VARCHAR(20) DEFAULT 'stopped' COMMENT 'SMTP监控状态';

-- 添加最后监控时间字段
ALTER TABLE email_account ADD COLUMN last_monitor_time DATETIME COMMENT '最后监控时间';

-- 添加监控错误信息字段
ALTER TABLE email_account ADD COLUMN monitor_error_message TEXT COMMENT '监控错误信息';

-- 为监控相关字段添加索引
CREATE INDEX idx_email_account_monitor_enabled ON email_account(monitor_enabled);
CREATE INDEX idx_email_account_monitor_status ON email_account(monitor_status);
CREATE INDEX idx_email_account_monitor_priority ON email_account(monitor_priority);

-- 更新现有记录的默认值
UPDATE email_account SET 
    monitor_enabled = '0',
    auto_start_monitor = '0',
    monitor_priority = 'medium',
    monitor_status = 'stopped',
    imap_monitor_status = 'stopped',
    smtp_monitor_status = 'stopped'
WHERE monitor_enabled IS NULL;
