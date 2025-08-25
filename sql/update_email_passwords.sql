-- 更新邮箱密码为应用专用密码
-- 请根据实际情况修改以下SQL语句

-- 更新Gmail密码（需要应用专用密码）
-- 1. 登录Google账户，开启两步验证
-- 2. 生成应用专用密码：https://myaccount.google.com/apppasswords
-- 3. 执行以下SQL（替换为你的真实应用专用密码）
/*
UPDATE email_account 
SET password = 'your_16_character_app_password_here',
    remark = CONCAT(remark, ' - 已更新为应用专用密码')
WHERE email_address LIKE '%@gmail.com';
*/

-- 更新QQ邮箱密码（需要授权码）
-- 1. 登录QQ邮箱，开启SMTP服务
-- 2. 获取授权码
-- 3. 执行以下SQL（替换为你的真实授权码）
/*
UPDATE email_account 
SET password = 'your_qq_authorization_code_here',
    remark = CONCAT(remark, ' - 已更新为授权码')
WHERE email_address LIKE '%@qq.com';
*/

-- 更新163邮箱密码（需要授权码）
-- 1. 登录163邮箱，开启SMTP服务
-- 2. 获取授权码
-- 3. 执行以下SQL（替换为你的真实授权码）
/*
UPDATE email_account 
SET password = 'your_163_authorization_code_here',
    remark = CONCAT(remark, ' - 已更新为授权码')
WHERE email_address LIKE '%@163.com';
*/

-- 更新Outlook/Hotmail密码（需要应用专用密码）
-- 1. 登录Microsoft账户，开启两步验证
-- 2. 生成应用专用密码
-- 3. 执行以下SQL（替换为你的真实应用专用密码）
/*
UPDATE email_account 
SET password = 'your_outlook_app_password_here',
    remark = CONCAT(remark, ' - 已更新为应用专用密码')
WHERE email_address LIKE '%@outlook.com' OR email_address LIKE '%@hotmail.com';
*/

-- 查看当前邮箱配置
SELECT 
    account_name,
    email_address,
    smtp_host,
    smtp_port,
    smtp_ssl,
    CASE 
        WHEN LENGTH(password) = 16 AND email_address LIKE '%@gmail.com' THEN '可能是应用专用密码'
        WHEN LENGTH(password) = 16 AND (email_address LIKE '%@outlook.com' OR email_address LIKE '%@hotmail.com') THEN '可能是应用专用密码'
        WHEN LENGTH(password) >= 10 AND (email_address LIKE '%@qq.com' OR email_address LIKE '%@163.com') THEN '可能是授权码'
        ELSE '可能是普通密码'
    END as password_type,
    remark
FROM email_account 
ORDER BY email_address;

