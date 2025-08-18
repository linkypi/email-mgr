-- 邮件管理系统菜单配置
-- 基于若依框架设计，根据UI设计图调整

-- 清空现有邮件相关菜单
DELETE FROM sys_menu WHERE menu_name LIKE '%邮件%' OR menu_name LIKE '%收件%' OR menu_name LIKE '%发件%' OR menu_name LIKE '%联系人%' OR menu_name LIKE '%群组%' OR menu_name LIKE '%标签%' OR menu_name LIKE '%模板%' OR menu_name LIKE '%批量%' OR menu_name LIKE '%统计%' OR menu_name LIKE '%邮箱账号%';

-- 1. 邮件管理主菜单
INSERT INTO sys_menu VALUES(1800, '邮件管理', 0, 4, 'email', NULL, '', '', 1, 0, 'M', '0', '0', '', 'email', 'admin', NOW(), '', NULL, '邮件管理目录');

-- 2. 个人邮件子菜单
INSERT INTO sys_menu VALUES(1801, '个人邮件', 1800, 1, 'personal', 'email/personal/index', '', '', 1, 0, 'C', '0', '0', 'email:personal:list', 'inbox', 'admin', NOW(), '', NULL, '个人邮件菜单');
INSERT INTO sys_menu VALUES(1802, '收件箱', 1801, 1, 'inbox', 'email/personal/inbox', '', '', 1, 0, 'C', '0', '0', 'email:inbox:list', 'inbox', 'admin', NOW(), '', NULL, '收件箱菜单');
INSERT INTO sys_menu VALUES(1803, '发件箱', 1801, 2, 'sent', 'email/personal/sent', '', '', 1, 0, 'C', '0', '0', 'email:sent:list', 'paper-plane', 'admin', NOW(), '', NULL, '发件箱菜单');
INSERT INTO sys_menu VALUES(1804, '星标邮件', 1801, 3, 'starred', 'email/personal/starred', '', '', 1, 0, 'C', '0', '0', 'email:starred:list', 'star', 'admin', NOW(), '', NULL, '星标邮件菜单');
INSERT INTO sys_menu VALUES(1805, '已删除', 1801, 4, 'deleted', 'email/personal/deleted', '', '', 1, 0, 'C', '0', '0', 'email:deleted:list', 'trash', 'admin', NOW(), '', NULL, '已删除邮件菜单');

-- 3. 邮件管理子菜单
INSERT INTO sys_menu VALUES(1810, '收件人管理', 1800, 2, 'contact', 'email/contact/index', '', '', 1, 0, 'C', '0', '0', 'email:contact:list', 'users', 'admin', NOW(), '', NULL, '收件人管理菜单');
INSERT INTO sys_menu VALUES(1811, '收件人查询', 1810, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1812, '收件人新增', 1810, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1813, '收件人修改', 1810, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1814, '收件人删除', 1810, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1815, '收件人导出', 1810, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:export', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1816, '收件人导入', 1810, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:import', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu VALUES(1820, '群组管理', 1800, 3, 'group', 'email/group/index', '', '', 1, 0, 'C', '0', '0', 'email:group:list', 'folder', 'admin', NOW(), '', NULL, '群组管理菜单');
INSERT INTO sys_menu VALUES(1821, '群组查询', 1820, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1822, '群组新增', 1820, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1823, '群组修改', 1820, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1824, '群组删除', 1820, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:remove', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu VALUES(1830, '标签管理', 1800, 4, 'tag', 'email/tag/index', '', '', 1, 0, 'C', '0', '0', 'email:tag:list', 'tag', 'admin', NOW(), '', NULL, '标签管理菜单');
INSERT INTO sys_menu VALUES(1831, '标签查询', 1830, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1832, '标签新增', 1830, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1833, '标签修改', 1830, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1834, '标签删除', 1830, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:remove', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu VALUES(1840, '邮件模板', 1800, 5, 'template', 'email/template/index', '', '', 1, 0, 'C', '0', '0', 'email:template:list', 'file-medical', 'admin', NOW(), '', NULL, '邮件模板菜单');
INSERT INTO sys_menu VALUES(1841, '模板查询', 1840, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1842, '模板新增', 1840, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1843, '模板修改', 1840, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1844, '模板删除', 1840, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1845, '模板预览', 1840, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:preview', '#', 'admin', NOW(), '', NULL, '');

-- 4. 批量操作子菜单
INSERT INTO sys_menu VALUES(1850, '批量操作', 1800, 6, 'batch', 'email/batch/index', '', '', 1, 0, 'C', '0', '0', 'email:batch:list', 'share-square', 'admin', NOW(), '', NULL, '批量操作菜单');
INSERT INTO sys_menu VALUES(1851, '导入收件人', 1850, 1, 'import', 'email/batch/import', '', '', 1, 0, 'C', '0', '0', 'email:import:list', 'upload', 'admin', NOW(), '', NULL, '导入收件人菜单');
INSERT INTO sys_menu VALUES(1852, '邮件批量发送', 1850, 2, 'send', 'email/batch/send', '', '', 1, 0, 'C', '0', '0', 'email:send:list', 'paper-plane', 'admin', NOW(), '', NULL, '邮件批量发送菜单');

-- 5. 数据统计子菜单
INSERT INTO sys_menu VALUES(1860, '数据统计', 1800, 7, 'statistics', 'email/statistics/index', '', '', 1, 0, 'C', '0', '0', 'email:statistics:list', 'chart-bar', 'admin', NOW(), '', NULL, '数据统计菜单');
INSERT INTO sys_menu VALUES(1861, '统计查询', 1860, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1862, '统计导出', 1860, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:export', '#', 'admin', NOW(), '', NULL, '');

-- 6. 邮箱账号管理子菜单（添加到系统管理下）
INSERT INTO sys_menu VALUES(1870, '邮箱账号管理', 1, 8, 'account', 'system/account/index', '', '', 1, 0, 'C', '0', '0', 'system:account:list', 'at', 'admin', NOW(), '', NULL, '邮箱账号管理菜单');
INSERT INTO sys_menu VALUES(1871, '账号查询', 1870, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:account:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1872, '账号新增', 1870, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:account:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1873, '账号修改', 1870, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:account:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1874, '账号删除', 1870, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:account:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1875, '账号测试', 1870, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:account:test', '#', 'admin', NOW(), '', NULL, '');
