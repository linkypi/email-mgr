-- 为普通角色（regular）添加查看和编辑访客录入联系人的权限
-- 此脚本确保普通角色可以查看及编辑访客录入的信息

-- 1. 检查普通角色ID
SELECT '普通角色ID:' as info;
SELECT role_id, role_name, role_key FROM sys_role WHERE role_key = 'regular' OR role_name LIKE '%普通%';

-- 2. 检查联系人相关的菜单权限
SELECT '联系人相关菜单权限:' as info;
SELECT m.menu_id, m.menu_name, m.perms 
FROM sys_menu m 
WHERE m.perms LIKE 'email:contact:%' 
ORDER BY m.menu_id;

-- 3. 为普通角色分配联系人编辑和删除权限（如果还没有分配）
-- 假设普通角色的 role_id 是 2，如果不是请根据实际情况调整
-- 获取联系人相关的菜单ID
SET @regular_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'regular' LIMIT 1);
SET @contact_list_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'email:contact:list' LIMIT 1);
SET @contact_query_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'email:contact:query' LIMIT 1);
SET @contact_add_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'email:contact:add' LIMIT 1);
SET @contact_edit_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'email:contact:edit' LIMIT 1);
SET @contact_remove_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'email:contact:remove' LIMIT 1);
SET @contact_export_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'email:contact:export' LIMIT 1);
SET @contact_import_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'email:contact:import' LIMIT 1);

-- 为普通角色分配联系人相关权限（如果还没有分配）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) 
SELECT @regular_role_id, @contact_list_menu_id WHERE @contact_list_menu_id IS NOT NULL;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) 
SELECT @regular_role_id, @contact_query_menu_id WHERE @contact_query_menu_id IS NOT NULL;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) 
SELECT @regular_role_id, @contact_add_menu_id WHERE @contact_add_menu_id IS NOT NULL;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) 
SELECT @regular_role_id, @contact_edit_menu_id WHERE @contact_edit_menu_id IS NOT NULL;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) 
SELECT @regular_role_id, @contact_remove_menu_id WHERE @contact_remove_menu_id IS NOT NULL;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) 
SELECT @regular_role_id, @contact_export_menu_id WHERE @contact_export_menu_id IS NOT NULL;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) 
SELECT @regular_role_id, @contact_import_menu_id WHERE @contact_import_menu_id IS NOT NULL;

-- 4. 验证普通角色的联系人权限配置
SELECT '普通角色联系人权限配置结果:' as info;
SELECT r.role_id, r.role_name, r.role_key, m.menu_id, m.menu_name, m.perms
FROM sys_role r
JOIN sys_role_menu rm ON r.role_id = rm.role_id
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE (r.role_key = 'regular' OR r.role_name LIKE '%普通%')
  AND m.perms LIKE 'email:contact:%'
ORDER BY m.menu_id;

-- 5. 显示普通角色可以访问的所有联系人相关权限
SELECT '普通角色联系人权限汇总:' as info;
SELECT 
    r.role_name as '角色名称',
    COUNT(DISTINCT m.menu_id) as '权限数量',
    GROUP_CONCAT(DISTINCT m.perms ORDER BY m.perms SEPARATOR ', ') as '权限列表'
FROM sys_role r
JOIN sys_role_menu rm ON r.role_id = rm.role_id
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE (r.role_key = 'regular' OR r.role_name LIKE '%普通%')
  AND m.perms LIKE 'email:contact:%'
GROUP BY r.role_id, r.role_name;


