-- 为email_send_task表添加template_variables字段
ALTER TABLE email_send_task ADD COLUMN IF NOT EXISTS template_variables TEXT COMMENT '模板变量(JSON格式)';
