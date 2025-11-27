-- 测试普通角色查询访客录入的联系人数据
-- 用于验证数据权限过滤是否正确

-- 1. 检查普通角色用户信息
SELECT '普通角色用户信息:' as info;
SELECT u.user_id, u.user_name, u.nick_name, u.status 
FROM sys_user u
INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
INNER JOIN sys_role r ON ur.role_id = r.role_id
WHERE r.role_key = 'regular'
ORDER BY u.user_id;

-- 2. 检查访客角色用户信息
SELECT '访客角色用户信息:' as info;
SELECT u.user_id, u.user_name, u.nick_name, u.status 
FROM sys_user u
INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
INNER JOIN sys_role r ON ur.role_id = r.role_id
WHERE r.role_key = 'guest'
ORDER BY u.user_id;

-- 3. 检查所有联系人数据（按创建者分组）
SELECT '所有联系人数据统计:' as info;
SELECT 
    c.create_by as '创建者',
    COUNT(*) as '联系人数量',
    GROUP_CONCAT(c.name ORDER BY c.create_time DESC LIMIT 5) as '示例联系人'
FROM email_contact c
WHERE c.deleted = '0'
GROUP BY c.create_by
ORDER BY COUNT(*) DESC;

-- 4. 模拟普通角色用户查询（使用第一个普通角色用户名）
-- 替换 'regular_user_name' 为实际的普通角色用户名
SET @regular_user_name = (SELECT u.user_name FROM sys_user u
                          INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
                          INNER JOIN sys_role r ON ur.role_id = r.role_id
                          WHERE r.role_key = 'regular'
                          LIMIT 1);

SELECT CONCAT('测试用户: ', IFNULL(@regular_user_name, '未找到普通角色用户')) as info;

-- 5. 模拟普通角色应该能查询到的联系人数据
-- 这应该包括：1) 自己创建的联系人 2) 访客创建的联系人
SELECT '普通角色应该能查询到的联系人:' as info;
SELECT 
    c.contact_id,
    c.name,
    c.email,
    c.create_by,
    c.create_time,
    CASE 
        WHEN c.create_by = @regular_user_name THEN '自己创建的'
        WHEN c.create_by IN (
            SELECT u.user_name FROM sys_user u
            INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
            INNER JOIN sys_role r ON ur.role_id = r.role_id
            WHERE r.role_key = 'guest'
        ) THEN '访客创建的'
        ELSE '其他'
    END as '数据来源'
FROM email_contact c
WHERE c.deleted = '0'
  AND (
      c.create_by = @regular_user_name
      OR c.create_by IN (
          SELECT u.user_name FROM sys_user u
          INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
          INNER JOIN sys_role r ON ur.role_id = r.role_id
          WHERE r.role_key = 'guest'
      )
  )
ORDER BY c.create_time DESC
LIMIT 20;

-- 6. 验证SQL查询条件（模拟DataScopeAspect生成的SQL）
SELECT '验证SQL查询条件:' as info;
SELECT 
    c.contact_id,
    c.name,
    c.email,
    c.create_by
FROM email_contact c
WHERE c.deleted = '0'
  AND (
      c.create_by = @regular_user_name
      OR c.create_by IN (
          SELECT u.user_name FROM sys_user u
          INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
          INNER JOIN sys_role r ON ur.role_id = r.role_id
          WHERE r.role_key = 'guest'
      )
  )
ORDER BY c.create_time DESC;

-- 7. 检查访客用户创建的联系人数量
SELECT '访客用户创建的联系人统计:' as info;
SELECT 
    c.create_by,
    COUNT(*) as '联系人数量'
FROM email_contact c
WHERE c.deleted = '0'
  AND c.create_by IN (
      SELECT u.user_name FROM sys_user u
      INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
      INNER JOIN sys_role r ON ur.role_id = r.role_id
      WHERE r.role_key = 'guest'
  )
GROUP BY c.create_by
ORDER BY COUNT(*) DESC;

-- 8. 检查角色配置
SELECT '角色配置信息:' as info;
SELECT 
    r.role_id,
    r.role_name,
    r.role_key,
    r.data_scope,
    COUNT(DISTINCT ur.user_id) as '用户数量'
FROM sys_role r
LEFT JOIN sys_user_role ur ON r.role_id = ur.role_id
WHERE r.role_key IN ('admin', 'regular', 'guest')
GROUP BY r.role_id, r.role_name, r.role_key, r.data_scope
ORDER BY r.role_id;


