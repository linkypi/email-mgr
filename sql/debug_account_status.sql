-- 调试脚本：检查邮箱账号状态
-- 用于诊断邮件跟踪与监控集成问题

-- 1. 查看所有邮箱账号的基本信息
SELECT 
    account_id,
    account_name,
    email_address,
    status,
    tracking_enabled,
    monitor_enabled,
    auto_start_monitor,
    monitor_status,
    imap_monitor_status,
    smtp_monitor_status,
    create_time,
    update_time
FROM email_account 
ORDER BY account_id;

-- 2. 统计各状态的账号数量
SELECT 
    '账号状态统计' as info,
    COUNT(*) as total_accounts,
    SUM(CASE WHEN status = '0' THEN 1 ELSE 0 END) as normal_accounts,
    SUM(CASE WHEN status = '1' THEN 1 ELSE 0 END) as disabled_accounts,
    SUM(CASE WHEN tracking_enabled = '1' THEN 1 ELSE 0 END) as tracking_enabled_accounts,
    SUM(CASE WHEN tracking_enabled = '0' THEN 1 ELSE 0 END) as tracking_disabled_accounts,
    SUM(CASE WHEN status = '0' AND tracking_enabled = '1' THEN 1 ELSE 0 END) as should_monitor_accounts
FROM email_account;

-- 3. 查看应该被监控的账号（状态正常且启用邮件跟踪）
SELECT 
    account_id,
    account_name,
    email_address,
    status,
    tracking_enabled,
    '应该被监控' as should_monitor
FROM email_account 
WHERE status = '0' AND tracking_enabled = '1'
ORDER BY account_id;

-- 4. 查看不应该被监控的账号
SELECT 
    account_id,
    account_name,
    email_address,
    status,
    tracking_enabled,
    CASE 
        WHEN status != '0' THEN '账号被禁用'
        WHEN tracking_enabled != '1' THEN '邮件跟踪未启用'
        ELSE '其他原因'
    END as reason
FROM email_account 
WHERE status != '0' OR tracking_enabled != '1'
ORDER BY account_id;

-- 5. 检查监控状态表
SELECT 
    '监控状态表统计' as info,
    COUNT(*) as total_monitors,
    SUM(CASE WHEN monitor_status = '1' THEN 1 ELSE 0 END) as running_monitors,
    SUM(CASE WHEN monitor_status = '0' THEN 1 ELSE 0 END) as stopped_monitors,
    SUM(CASE WHEN imap_status = '1' OR imap_status = '2' OR imap_status = '3' THEN 1 ELSE 0 END) as imap_running,
    SUM(CASE WHEN smtp_status = '1' OR smtp_status = '2' OR smtp_status = '3' THEN 1 ELSE 0 END) as smtp_running
FROM email_service_monitor;

-- 6. 查看监控状态详情
SELECT 
    account_id,
    email_address,
    monitor_status,
    imap_status,
    smtp_status,
    monitor_enabled,
    create_time,
    update_time
FROM email_service_monitor 
ORDER BY account_id;
