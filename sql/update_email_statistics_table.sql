-- 更新邮件统计表，添加缺失的字段
-- 执行时间：2024-01-01

-- 添加邮件 Message-ID 字段
ALTER TABLE email_statistics ADD COLUMN message_id VARCHAR(255) COMMENT '邮件Message-ID';

-- 添加邮件状态字段
ALTER TABLE email_statistics ADD COLUMN status VARCHAR(50) COMMENT '邮件状态';

-- 添加送达时间字段
ALTER TABLE email_statistics ADD COLUMN delivered_time DATETIME COMMENT '送达时间';

-- 添加打开时间字段
ALTER TABLE email_statistics ADD COLUMN opened_time DATETIME COMMENT '打开时间';

-- 添加回复时间字段
ALTER TABLE email_statistics ADD COLUMN replied_time DATETIME COMMENT '回复时间';

-- 添加点击时间字段
ALTER TABLE email_statistics ADD COLUMN clicked_time DATETIME COMMENT '点击时间';

-- 添加索引以提高查询性能
CREATE INDEX idx_email_statistics_message_id ON email_statistics(message_id);
CREATE INDEX idx_email_statistics_status ON email_statistics(status);

-- 查看表结构
DESCRIBE email_statistics;

