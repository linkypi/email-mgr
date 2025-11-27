-- =============================================
-- 邮件管理系统 - 联系人标签结构更新脚本
-- 版本: V1.2.1
-- 创建时间: 2024-01-25
-- 描述: 更新联系人标签表结构，支持多标签功能
-- =============================================

-- 创建联系人标签关联表
CREATE TABLE IF NOT EXISTS `email_contact_tag` (
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`contact_id`, `tag_id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人标签关联表';

-- 创建标签表（如果不存在）
CREATE TABLE IF NOT EXISTS `email_tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(100) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  `description` varchar(500) DEFAULT NULL COMMENT '标签描述',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件标签表';

-- 创建联系人表（如果不存在）
CREATE TABLE IF NOT EXISTS `email_contact` (
  `contact_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
  `contact_name` varchar(100) NOT NULL COMMENT '联系人姓名',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `company` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `position` varchar(100) DEFAULT NULL COMMENT '职位',
  `notes` text COMMENT '备注',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态(active=活跃,inactive=非活跃)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`contact_id`),
  UNIQUE KEY `uk_email_address` (`email_address`),
  KEY `idx_contact_name` (`contact_name`),
  KEY `idx_company` (`company`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件联系人表';

-- 插入默认标签数据
INSERT IGNORE INTO `email_tag` (`tag_name`, `tag_color`, `description`, `create_by`, `create_time`) VALUES
('重要客户', '#F56C6C', '重要客户标签', 'admin', NOW()),
('潜在客户', '#E6A23C', '潜在客户标签', 'admin', NOW()),
('合作伙伴', '#67C23A', '合作伙伴标签', 'admin', NOW()),
('内部员工', '#409EFF', '内部员工标签', 'admin', NOW()),
('供应商', '#909399', '供应商标签', 'admin', NOW());

-- 如果email_contact表中有tags字段，需要迁移数据到新的关联表
-- 这里假设原来的tags字段存储的是逗号分隔的标签名称
-- 注意：这个迁移脚本需要根据实际的数据结构调整

-- 示例：迁移tags字段数据到关联表（需要根据实际情况调整）
/*
INSERT INTO `email_contact_tag` (`contact_id`, `tag_id`, `create_time`)
SELECT 
    c.contact_id,
    t.tag_id,
    NOW()
FROM `email_contact` c
CROSS JOIN `email_tag` t
WHERE FIND_IN_SET(t.tag_name, REPLACE(c.tags, '，', ',')) > 0
AND c.tags IS NOT NULL 
AND c.tags != '';
*/

-- 删除旧的tags字段（如果存在且数据已迁移）
-- ALTER TABLE `email_contact` DROP COLUMN `tags`;

