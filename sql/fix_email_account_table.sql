USE email_mgr;

-- 检查并添加send_interval_seconds字段
ALTER TABLE email_account
    ADD COLUMN  send_interval_seconds INT DEFAULT 60 COMMENT '发送间隔秒数';


ALTER TABLE email_account
    ADD COLUMN  daily_sent_count INT DEFAULT 60 COMMENT '每日发送数量';

ALTER TABLE email_account
    ADD COLUMN  last_send_date DATETIME DEFAULT NULL COMMENT '最后发送日期';


-- 检查并添加sender_id字段（如果不存在）
ALTER TABLE email_account
    ADD COLUMN  sender_id BIGINT(20) COMMENT '发件人ID';

-- 检查并添加sender_name字段（如果不存在）
ALTER TABLE email_account
    ADD COLUMN  sender_name VARCHAR(100) COMMENT '发件人姓名';

-- 显示表结构
DESCRIBE email_account;