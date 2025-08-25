-- 修复QQ邮箱SMTP配置
-- 将QQ邮箱的端口从587改为465（SSL端口）

UPDATE email_account 
SET smtp_port = 465 
WHERE email_address LIKE '%@qq.com' AND smtp_port = 587;

-- 确保QQ邮箱使用SSL
UPDATE email_account 
SET smtp_ssl = '1' 
WHERE email_address LIKE '%@qq.com' AND smtp_ssl = '0';

-- 显示更新结果
SELECT account_name, email_address, smtp_host, smtp_port, smtp_ssl 
FROM email_account 
WHERE email_address LIKE '%@qq.com';

