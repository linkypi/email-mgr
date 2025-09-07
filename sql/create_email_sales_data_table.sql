-- 创建邮件销售数据表
USE email_mgr;

-- 删除表（如果存在）
DROP TABLE IF EXISTS `email_sales_data`;

-- 创建邮件销售数据表
CREATE TABLE `email_sales_data` (
  `sales_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '销售数据ID',
  `user_email` varchar(100) NOT NULL COMMENT '用户邮箱',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `sales_date` date NOT NULL COMMENT '带货日期',
  `product_model` varchar(100) NOT NULL COMMENT '带货型号',
  `sales_quantity` int(11) DEFAULT 0 COMMENT '带货单量',
  `play_count` bigint(20) DEFAULT 0 COMMENT '播放次数',
  `conversion_rate` decimal(5,4) DEFAULT 0.0000 COMMENT '转化率',
  `discount_type` varchar(20) DEFAULT '无折扣' COMMENT '折扣类型',
  `discount_ratio` decimal(5,4) DEFAULT 0.0000 COMMENT '折扣比例',
  `source_channel` varchar(100) DEFAULT NULL COMMENT '来源渠道',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`sales_id`),
  KEY `idx_user_email` (`user_email`),
  KEY `idx_sales_date` (`sales_date`),
  KEY `idx_product_model` (`product_model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件销售数据表';

-- 插入测试数据
INSERT INTO `email_sales_data` VALUES 
(1, 'user1@example.com', '张三', '2024-01-15', 'iPhone 15 Pro', 25, 15000, 0.0167, '满减', 0.1000, '抖音', '热销产品', 'admin', '2024-01-15 10:30:00', '', NULL),
(2, 'user1@example.com', '张三', '2024-01-14', 'MacBook Pro M3', 8, 8500, 0.0094, '折扣', 0.1500, '小红书', '高端产品', 'admin', '2024-01-14 15:20:00', '', NULL),
(3, 'user1@example.com', '张三', '2024-01-13', 'AirPods Pro', 45, 22000, 0.0205, '优惠券', 0.0800, '淘宝', '配件产品', 'admin', '2024-01-13 09:15:00', '', NULL),
(4, 'user2@example.com', '李四', '2024-01-15', 'iPad Air', 18, 12000, 0.0150, '满减', 0.1200, '京东', '平板产品', 'admin', '2024-01-15 14:30:00', '', NULL),
(5, 'user2@example.com', '李四', '2024-01-14', 'Apple Watch', 32, 18000, 0.0178, '折扣', 0.1000, '抖音', '智能手表', 'admin', '2024-01-14 11:45:00', '', NULL),
(6, 'user3@example.com', '王五', '2024-01-15', 'iPhone 15', 12, 9500, 0.0126, '无折扣', 0.0000, '天猫', '标准版手机', 'admin', '2024-01-15 16:20:00', '', NULL),
(7, 'user3@example.com', '王五', '2024-01-13', 'MacBook Air M2', 6, 6800, 0.0088, '优惠券', 0.2000, '小红书', '轻薄本', 'admin', '2024-01-13 13:30:00', '', NULL),
(8, 'user1@example.com', '张三', '2024-01-12', 'iPhone 14 Pro', 35, 25000, 0.0140, '满减', 0.1500, '抖音', '上一代旗舰', 'admin', '2024-01-12 10:15:00', '', NULL),
(9, 'user2@example.com', '李四', '2024-01-12', 'iPad Pro', 15, 14000, 0.0107, '折扣', 0.1800, '京东', '专业平板', 'admin', '2024-01-12 15:45:00', '', NULL),
(10, 'user3@example.com', '王五', '2024-01-11', 'AirPods Max', 8, 5200, 0.0154, '优惠券', 0.1200, '天猫', '头戴式耳机', 'admin', '2024-01-11 12:30:00', '', NULL);

SELECT 'Email sales data table created successfully with test data!' as result;
