-- 邮箱账号测试数据
-- 注意：这些是示例数据，实际使用时需要替换为真实的应用专用密码或授权码
-- 详细配置说明请参考：doc/email_configuration_guide.md

INSERT INTO email_account (account_name, email_address, password, smtp_host, smtp_port, smtp_ssl, daily_limit, used_count, status, create_time, remark) VALUES
('Gmail测试账号', 'test@gmail.com', 'your_gmail_app_password_here', 'smtp.gmail.com', 587, '1', 100, 0, '0', NOW(), 'Gmail测试账号 - 需要使用应用专用密码'),
('QQ邮箱测试账号', 'test@qq.com', 'your_qq_authorization_code_here', 'smtp.qq.com', 465, '1', 100, 0, '0', NOW(), 'QQ邮箱测试账号 - 需要使用授权码'),
('163邮箱测试账号', 'test@163.com', 'your_163_authorization_code_here', 'smtp.163.com', 25, '0', 100, 0, '0', NOW(), '163邮箱测试账号 - 需要使用授权码'),
('Outlook测试账号', 'test@outlook.com', 'your_outlook_app_password_here', 'smtp-mail.outlook.com', 587, '1', 100, 0, '0', NOW(), 'Outlook测试账号 - 需要使用应用专用密码'),
('企业邮箱测试账号', 'test@company.com', 'your_company_email_password_here', 'smtp.company.com', 465, '1', 100, 0, '0', NOW(), '企业邮箱测试账号');

