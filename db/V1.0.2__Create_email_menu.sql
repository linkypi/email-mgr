-- =============================================
-- 邮件管理系统 - 菜单配置脚本
-- 版本: V1.0.2
-- 创建时间: 2024-01-15
-- 描述: 创建邮件管理相关的菜单配置
-- =============================================

-- 删除旧的邮件管理菜单
DELETE FROM sys_menu WHERE menu_name LIKE '%邮件%' OR menu_name LIKE '%Email%';

-- 插入新的邮件管理菜单结构

-- 1. 个人邮件主菜单
INSERT INTO sys_menu VALUES (2000, '个人邮件', 0, 1, 'personal', NULL, '', 'personal', 1, 0, 'M', '0', '0', '', 'email', 'admin', NOW(), '', NULL, '个人邮件管理');

-- 1.1 收件箱
INSERT INTO sys_menu VALUES (2001, '收件箱', 2000, 1, 'inbox', 'email/personal/inbox', '', 'inbox', 1, 0, 'C', '0', '0', 'email:personal:inbox:list', 'message', 'admin', NOW(), '', NULL, '收件箱管理');
INSERT INTO sys_menu VALUES (2002, '收件箱查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:inbox:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2003, '收件箱删除', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:inbox:remove', '#', 'admin', NOW(), '', NULL, '');

-- 1.2 发件箱
INSERT INTO sys_menu VALUES (2004, '发件箱', 2000, 2, 'sent', 'email/personal/sent', '', 'sent', 1, 0, 'C', '0', '0', 'email:personal:sent:list', 'upload', 'admin', NOW(), '', NULL, '发件箱管理');
INSERT INTO sys_menu VALUES (2005, '发件箱查询', 2004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:sent:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2006, '发件箱删除', 2004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:sent:remove', '#', 'admin', NOW(), '', NULL, '');

-- 1.3 星标邮件
INSERT INTO sys_menu VALUES (2007, '星标邮件', 2000, 3, 'starred', 'email/personal/starred', '', 'starred', 1, 0, 'C', '0', '0', 'email:personal:starred:list', 'star', 'admin', NOW(), '', NULL, '星标邮件管理');
INSERT INTO sys_menu VALUES (2008, '星标邮件查询', 2007, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:starred:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2009, '星标邮件删除', 2007, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:starred:remove', '#', 'admin', NOW(), '', NULL, '');

-- 1.4 已删除
INSERT INTO sys_menu VALUES (2010, '已删除', 2000, 4, 'deleted', 'email/personal/deleted', '', 'deleted', 1, 0, 'C', '0', '0', 'email:personal:deleted:list', 'delete', 'admin', NOW(), '', NULL, '已删除邮件管理');
INSERT INTO sys_menu VALUES (2011, '已删除查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:deleted:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2012, '已删除恢复', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:deleted:restore', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2013, '已删除彻底删除', 2010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:deleted:delete', '#', 'admin', NOW(), '', NULL, '');

-- 2. 邮件管理主菜单
INSERT INTO sys_menu VALUES (2020, '邮件管理', 0, 2, 'management', NULL, '', 'management', 1, 0, 'M', '0', '0', '', 'setting', 'admin', NOW(), '', NULL, '邮件管理');

-- 2.1 发件人管理
INSERT INTO sys_menu VALUES (2021, '发件人管理', 2020, 1, 'account', 'email/account/index', '', 'account', 1, 0, 'C', '0', '0', 'email:account:list', 'user', 'admin', NOW(), '', NULL, '发件人管理');
INSERT INTO sys_menu VALUES (2022, '发件人查询', 2021, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2023, '发件人新增', 2021, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2024, '发件人修改', 2021, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2025, '发件人删除', 2021, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2026, '发件人导出', 2021, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:export', '#', 'admin', NOW(), '', NULL, '');

-- 2.2 收件人管理
INSERT INTO sys_menu VALUES (2027, '收件人管理', 2020, 2, 'contact', 'email/contact/index', '', 'contact', 1, 0, 'C', '0', '0', 'email:contact:list', 'people', 'admin', NOW(), '', NULL, '收件人管理');
INSERT INTO sys_menu VALUES (2028, '收件人查询', 2027, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2029, '收件人新增', 2027, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2030, '收件人修改', 2027, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2031, '收件人删除', 2027, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2032, '收件人导出', 2027, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:export', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2033, '收件人导入', 2027, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:import', '#', 'admin', NOW(), '', NULL, '');

-- 2.3 群组管理
INSERT INTO sys_menu VALUES (2040, '群组管理', 2020, 3, 'group', 'email/group/index', '', 'group', 1, 0, 'C', '0', '0', 'email:group:list', 'peoples', 'admin', NOW(), '', NULL, '群组管理');
INSERT INTO sys_menu VALUES (2041, '群组查询', 2040, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2042, '群组新增', 2040, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2043, '群组修改', 2040, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2044, '群组删除', 2040, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2045, '群组导出', 2040, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:export', '#', 'admin', NOW(), '', NULL, '');

-- 2.4 标签管理
INSERT INTO sys_menu VALUES (2050, '标签管理', 2020, 4, 'tag', 'email/tag/index', '', 'tag', 1, 0, 'C', '0', '0', 'email:tag:list', 'collection-tag', 'admin', NOW(), '', NULL, '标签管理');
INSERT INTO sys_menu VALUES (2051, '标签查询', 2050, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2052, '标签新增', 2050, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2053, '标签修改', 2050, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2054, '标签删除', 2050, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2055, '标签导出', 2050, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:export', '#', 'admin', NOW(), '', NULL, '');

-- 2.5 邮件模板
INSERT INTO sys_menu VALUES (2060, '邮件模板', 2020, 5, 'template', 'email/template/index', '', 'template', 1, 0, 'C', '0', '0', 'email:template:list', 'documentation', 'admin', NOW(), '', NULL, '邮件模板管理');
INSERT INTO sys_menu VALUES (2061, '模板查询', 2060, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2062, '模板新增', 2060, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2063, '模板修改', 2060, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2064, '模板删除', 2060, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2065, '模板导出', 2060, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:export', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2066, '模板预览', 2060, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:preview', '#', 'admin', NOW(), '', NULL, '');

-- 3. 批量操作主菜单
INSERT INTO sys_menu VALUES (2070, '批量操作', 0, 3, 'batch', NULL, '', 'batch', 1, 0, 'M', '0', '0', '', 'tool', 'admin', NOW(), '', NULL, '批量操作');

-- 3.1 导入收件人
INSERT INTO sys_menu VALUES (2071, '导入收件人', 2070, 1, 'import', 'email/batch/import', '', 'import', 1, 0, 'C', '0', '0', 'email:batch:import:list', 'upload', 'admin', NOW(), '', NULL, '批量导入收件人');
INSERT INTO sys_menu VALUES (2072, '导入查询', 2071, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2073, '导入新增', 2071, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2074, '导入删除', 2071, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2075, '导入导出', 2071, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:export', '#', 'admin', NOW(), '', NULL, '');

-- 3.2 邮件批量发送
INSERT INTO sys_menu VALUES (2080, '邮件批量发送', 2070, 2, 'send', 'email/batch/send', '', 'send', 1, 0, 'C', '0', '0', 'email:batch:send:list', 's-promotion', 'admin', NOW(), '', NULL, '邮件批量发送');
INSERT INTO sys_menu VALUES (2081, '开始发送', 2080, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:send:create', '#', 'admin', NOW(), '', NULL, '');

-- 4. 数据统计主菜单
INSERT INTO sys_menu VALUES (2090, '数据统计', 0, 4, 'statistics', 'email/statistics/index', '', 'statistics', 1, 0, 'C', '0', '0', 'email:statistics:list', 'chart', 'admin', NOW(), '', NULL, '邮件数据统计');
INSERT INTO sys_menu VALUES (2091, '统计查询', 2090, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2092, '统计导出', 2090, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:export', '#', 'admin', NOW(), '', NULL, '');

