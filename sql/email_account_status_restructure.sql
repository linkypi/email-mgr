-- 邮箱账号状态重构脚本
-- 明确区分账号启用状态和监控状态
-- 执行时间：2024-01-01

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

-- 2. 重新定义status字段的含义
-- status字段现在专门表示账号是否启用发送邮件功能
-- 0=启用发送邮件，1=禁用发送邮件
-- 不再与监控状态混淆

-- 3. 清理监控相关字段，这些字段将在email_service_monitor表中管理
-- 移除email_account表中的监控相关字段，避免数据冗余和逻辑混乱

-- 4. 更新现有数据，确保逻辑一致
-- 如果账号状态为正常(0)，则启用发送邮件功能
-- 如果账号状态为停用(1)，则禁用发送邮件功能
UPDATE email_account 
SET status = '0' 
WHERE status IS NULL OR status = '';

-- 5. 确保tracking_enabled字段有正确的默认值
UPDATE email_account 
SET tracking_enabled = '1' 
WHERE tracking_enabled IS NULL OR tracking_enabled = '';

-- 6. 查看重构后的状态
SELECT '重构后账号状态' as info;
SELECT 
    account_id,
    account_name,
    email_address,
    status,
    tracking_enabled,
    CASE 
        WHEN status = '0' THEN '启用发送邮件'
        WHEN status = '1' THEN '禁用发送邮件'
        ELSE '未知状态'
    END as status_desc,
    CASE 
        WHEN tracking_enabled = '1' THEN '启用邮件跟踪'
        WHEN tracking_enabled = '0' THEN '禁用邮件跟踪'
        ELSE '未知状态'
    END as tracking_desc
FROM email_account 
ORDER BY account_id;

-- 7. 统计重构结果
SELECT 
    '重构结果统计' as info,
    COUNT(*) as total_accounts,
    SUM(CASE WHEN status = '0' THEN 1 ELSE 0 END) as enabled_accounts,
    SUM(CASE WHEN status = '1' THEN 1 ELSE 0 END) as disabled_accounts,
    SUM(CASE WHEN status = '0' AND tracking_enabled = '1' THEN 1 ELSE 0 END) as should_monitor_accounts
FROM email_account;

-- 8. 清理email_service_monitor表中对应已禁用账号的记录
DELETE FROM email_service_monitor 
WHERE account_id IN (
    SELECT account_id FROM email_account WHERE status = '1'
);

-- 9. 显示清理结果
SELECT 
    '监控记录清理结果' as info,
    COUNT(*) as remaining_monitor_records
FROM email_service_monitor;

-- 10. 创建视图简化查询
CREATE OR REPLACE VIEW `v_email_account_status` AS
SELECT 
    a.account_id,
    a.account_name,
    a.email_address,
    a.status,
    a.tracking_enabled,
    CASE 
        WHEN a.status = '0' THEN '启用发送邮件'
        WHEN a.status = '1' THEN '禁用发送邮件'
        ELSE '未知状态'
    END as status_desc,
    CASE 
        WHEN a.tracking_enabled = '1' THEN '启用邮件跟踪'
        WHEN a.tracking_enabled = '0' THEN '禁用邮件跟踪'
        ELSE '未知状态'
    END as tracking_desc,
    m.imap_status,
    m.smtp_status,
    m.imap_last_check_time,
    m.smtp_last_check_time,
    CASE 
        WHEN a.status = '0' AND m.account_id IS NOT NULL THEN '监控中'
        WHEN a.status = '0' AND m.account_id IS NULL THEN '未监控'
        ELSE '不监控'
    END as monitor_status
FROM email_account a
LEFT JOIN email_service_monitor m ON a.account_id = m.account_id
WHERE a.deleted = '0';

-- 11. 显示视图数据
SELECT '账号状态视图' as info;
SELECT * FROM v_email_account_status ORDER BY account_id;

