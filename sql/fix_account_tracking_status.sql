-- 修复邮箱账号邮件跟踪状态脚本
-- 用于确保启用了邮件跟踪的账号能够被正确监控

-- 1. 查看当前状态
SELECT '当前账号状态' as info;
SELECT 
    account_id,
    account_name,
    email_address,
    status,
    tracking_enabled,
    monitor_enabled,
    auto_start_monitor
FROM email_account 
ORDER BY account_id;

-- 2. 修复邮件跟踪状态
-- 将所有正常状态的账号的邮件跟踪设置为启用
UPDATE email_account 
SET tracking_enabled = '1' 
WHERE status = '0' AND (tracking_enabled IS NULL OR tracking_enabled = '0');

-- 3. 确保监控相关字段有正确的默认值
UPDATE email_account 
SET 
    monitor_enabled = '0',
    auto_start_monitor = '0',
    monitor_priority = 'medium'
WHERE monitor_enabled IS NULL OR auto_start_monitor IS NULL OR monitor_priority IS NULL;

-- 4. 查看修复后的状态
SELECT '修复后账号状态' as info;
SELECT 
    account_id,
    account_name,
    email_address,
    status,
    tracking_enabled,
    monitor_enabled,
    auto_start_monitor,
    '应该被监控' as should_monitor
FROM email_account 
WHERE status = '0' AND tracking_enabled = '1'
ORDER BY account_id;

-- 5. 统计修复结果
SELECT 
    '修复结果统计' as info,
    COUNT(*) as total_accounts,
    SUM(CASE WHEN status = '0' AND tracking_enabled = '1' THEN 1 ELSE 0 END) as should_monitor_accounts,
    SUM(CASE WHEN status = '0' AND tracking_enabled = '0' THEN 1 ELSE 0 END) as normal_but_no_tracking,
    SUM(CASE WHEN status = '1' THEN 1 ELSE 0 END) as disabled_accounts
FROM email_account;
