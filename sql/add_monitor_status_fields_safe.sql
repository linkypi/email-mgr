-- 添加邮件服务监控表缺失的字段（安全版本）
-- 执行时间：2024-01-01

-- 1. 添加 monitor_status 字段
ALTER TABLE `email_service_monitor` 
ADD COLUMN IF NOT EXISTS `monitor_status` varchar(20) DEFAULT '0' COMMENT '监控状态：0-停止，1-运行中' AFTER `monitor_enabled`;

-- 2. 添加 last_monitor_time 字段
ALTER TABLE `email_service_monitor` 
ADD COLUMN IF NOT EXISTS `last_monitor_time` datetime DEFAULT NULL COMMENT '最后监控时间' AFTER `monitor_status`;

-- 3. 安全添加索引（检查是否存在）
-- 创建临时存储过程来检查索引是否存在
DELIMITER $$

DROP PROCEDURE IF EXISTS `AddIndexIfNotExists`$$

CREATE PROCEDURE `AddIndexIfNotExists`(
    IN tableName VARCHAR(64),
    IN indexName VARCHAR(64),
    IN columnName VARCHAR(64)
)
BEGIN
    DECLARE indexExists INT DEFAULT 0;
    
    -- 检查索引是否存在
    SELECT COUNT(1) INTO indexExists
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE()
      AND table_name = tableName
      AND index_name = indexName;
    
    -- 如果索引不存在，则添加
    IF indexExists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', tableName, '` ADD INDEX `', indexName, '` (`', columnName, '`)');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('Index ', indexName, ' added successfully') AS result;
    ELSE
        SELECT CONCAT('Index ', indexName, ' already exists') AS result;
    END IF;
END$$

DELIMITER ;

-- 4. 执行添加索引
CALL AddIndexIfNotExists('email_service_monitor', 'idx_monitor_status', 'monitor_status');
CALL AddIndexIfNotExists('email_service_monitor', 'idx_last_monitor_time', 'last_monitor_time');

-- 5. 清理存储过程
DROP PROCEDURE IF EXISTS `AddIndexIfNotExists`;

-- 6. 更新现有记录的监控状态
UPDATE `email_service_monitor` 
SET `monitor_status` = '0', 
    `last_monitor_time` = NULL 
WHERE `monitor_status` IS NULL;

-- 7. 验证字段添加成功
DESCRIBE `email_service_monitor`;

-- 8. 验证索引添加成功
SHOW INDEX FROM `email_service_monitor`;
