-- 修复 regular 角色权限配置
-- 为 regular 角色添加邮件统计和个人邮件相关的权限

-- 1. 首先检查 regular 角色的ID
SELECT role_id, role_name, role_key FROM sys_role WHERE role_key = 'regular' OR role_name LIKE '%普通%';

-- 2. 为 regular 角色添加邮件统计相关权限
-- 假设 regular 角色的 role_id 是 2，如果不是，请修改下面的 role_id

-- 添加邮件统计菜单权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES 
-- 邮件统计主菜单
(2, 2090),  -- 数据统计
(2, 2091),  -- 统计查询
(2, 2092),  -- 统计导出

-- 个人邮件菜单权限
(2, 2000),  -- 个人邮件主菜单
(2, 2001),  -- 收件箱
(2, 2002),  -- 收件箱查询
(2, 2003),  -- 收件箱删除
(2, 2004),  -- 发件箱
(2, 2005),  -- 发件箱查询
(2, 2006),  -- 发件箱删除
(2, 2007),  -- 星标邮件
(2, 2008),  -- 星标邮件查询
(2, 2009),  -- 星标邮件删除
(2, 2010),  -- 已删除
(2, 2011),  -- 已删除查询
(2, 2012),  -- 已删除恢复
(2, 2013);  -- 已删除彻底删除

-- 3. 添加必要的功能权限（这些权限在控制器中被使用）
-- 注意：这些权限可能需要在 sys_menu 表中先创建，如果不存在的话

-- 检查是否存在这些权限菜单
SELECT menu_id, menu_name, perms FROM sys_menu WHERE perms IN (
    'email:personal:count',
    'email:statistics:track',
    'email:statistics:count',
    'email:statistics:today',
    'email:statistics:total',
    'email:statistics:trends',
    'email:statistics:reply-rates',
    'email:statistics:accounts',
    'email:statistics:detailed',
    'dashboard:statistics:today',
    'dashboard:statistics:total',
    'dashboard:statistics:trends',
    'dashboard:statistics:reply-rates',
    'dashboard:statistics:accounts',
    'dashboard:statistics:detailed'
);

-- 4. 如果上述权限菜单不存在，需要先创建它们
-- 这里我们创建必要的权限菜单

-- 创建邮件个人计数权限菜单
INSERT IGNORE INTO sys_menu VALUES 
(3201, '邮件计数', 2000, 5, 'count', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:count', '#', 'admin', NOW(), '', NULL, '邮件计数权限');

-- 注意：后端控制器中所有邮件个人相关接口都使用同一个权限 'email:personal:count'
-- 因此不需要创建单独的未读数量权限菜单

-- 创建邮件统计相关权限菜单
INSERT IGNORE INTO sys_menu VALUES 
(3202, '统计跟踪', 2090, 3, 'track', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:track', '#', 'admin', NOW(), '', NULL, '统计跟踪权限'),
(3203, '统计计数', 2090, 4, 'count', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:count', '#', 'admin', NOW(), '', NULL, '统计计数权限'),
(3204, '今日统计', 2090, 5, 'today', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:today', '#', 'admin', NOW(), '', NULL, '今日统计权限'),
(3205, '总体统计', 2090, 6, 'total', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:total', '#', 'admin', NOW(), '', NULL, '总体统计权限'),
(3206, '趋势统计', 2090, 7, 'trends', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:trends', '#', 'admin', NOW(), '', NULL, '趋势统计权限'),
(3207, '回复率统计', 2090, 8, 'reply-rates', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:reply-rates', '#', 'admin', NOW(), '', NULL, '回复率统计权限'),
(3208, '账号统计', 2090, 9, 'accounts', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:accounts', '#', 'admin', NOW(), '', NULL, '账号统计权限'),
(3209, '详细统计', 2090, 10, 'detailed', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:detailed', '#', 'admin', NOW(), '', NULL, '详细统计权限');

-- 创建仪表板统计相关权限菜单
INSERT IGNORE INTO sys_menu VALUES 
(3210, '仪表板今日统计', 1, 1, 'dashboard-today', '', '', '', 1, 0, 'F', '0', '0', 'dashboard:statistics:today', '#', 'admin', NOW(), '', NULL, '仪表板今日统计权限'),
(3211, '仪表板总体统计', 1, 2, 'dashboard-total', '', '', '', 1, 0, 'F', '0', '0', 'dashboard:statistics:total', '#', 'admin', NOW(), '', NULL, '仪表板总体统计权限'),
(3212, '仪表板趋势统计', 1, 3, 'dashboard-trends', '', '', '', 1, 0, 'F', '0', '0', 'dashboard:statistics:trends', '#', 'admin', NOW(), '', NULL, '仪表板趋势统计权限'),
(3213, '仪表板回复率统计', 1, 4, 'dashboard-reply-rates', '', '', '', 1, 0, 'F', '0', '0', 'dashboard:statistics:replyRates', '#', 'admin', NOW(), '', NULL, '仪表板回复率统计权限'),
(3214, '仪表板账号统计', 1, 5, 'dashboard-accounts', '', '', '', 1, 0, 'F', '0', '0', 'dashboard:statistics:accounts', '#', 'admin', NOW(), '', NULL, '仪表板账号统计权限'),
(3215, '仪表板详细统计', 1, 6, 'dashboard-detailed', '', '', '', 1, 0, 'F', '0', '0', 'dashboard:statistics:detailed', '#', 'admin', NOW(), '', NULL, '仪表板详细统计权限');

-- 5. 为 regular 角色添加这些新创建的权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES 
-- 邮件个人计数权限（包含所有邮件个人相关接口的权限）
(2, 3201),

-- 邮件统计相关权限
(2, 3202),  -- 统计跟踪
(2, 3203),  -- 统计计数
(2, 3204),  -- 今日统计
(2, 3205),  -- 总体统计
(2, 3206),  -- 趋势统计
(2, 3207),  -- 回复率统计
(2, 3208),  -- 账号统计
(2, 3209),  -- 详细统计

-- 仪表板统计相关权限
(2, 3210),  -- 仪表板今日统计
(2, 3211),  -- 仪表板总体统计
(2, 3212),  -- 仪表板趋势统计
(2, 3213),  -- 仪表板回复率统计
(2, 3214),  -- 仪表板账号统计
(2, 3215);  -- 仪表板详细统计

-- 6. 验证权限分配结果
SELECT 
    r.role_name,
    r.role_key,
    m.menu_name,
    m.perms
FROM sys_role r
JOIN sys_role_menu rm ON r.role_id = rm.role_id
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE r.role_key = 'regular' OR r.role_name LIKE '%普通%'
ORDER BY m.menu_id;

-- 7. 检查是否有遗漏的权限
SELECT DISTINCT perms FROM sys_menu WHERE perms LIKE 'email:%' OR perms LIKE 'dashboard:%'
AND perms NOT IN (
    SELECT DISTINCT m.perms 
    FROM sys_role r
    JOIN sys_role_menu rm ON r.role_id = rm.role_id
    JOIN sys_menu m ON rm.menu_id = m.menu_id
    WHERE r.role_key = 'regular' OR r.role_name LIKE '%普通%'
);
