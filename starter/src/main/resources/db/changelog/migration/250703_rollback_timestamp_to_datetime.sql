-- 250703_rollback_timestamp_to_datetime.sql
-- MySQL版本：将 bytedesk_ 表中的时间戳字段从 TIMESTAMP 回滚到 DATETIME
-- 此脚本将所有 bytedesk_ 表的 created_at 和 updated_at 列
-- 从 TIMESTAMP (ZonedDateTime) 回滚到 DATETIME (LocalDateTime)

-- 设置会话变量以确保兼容性
SET SESSION sql_mode = "NO_AUTO_VALUE_ON_ZERO";

-- 创建临时表存储需要回滚的表
CREATE TEMPORARY TABLE IF NOT EXISTS temp_tables_to_rollback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(255),
    has_created_at BOOLEAN DEFAULT FALSE,
    has_updated_at BOOLEAN DEFAULT FALSE,
    created_at_type VARCHAR(50),
    updated_at_type VARCHAR(50)
);

-- 查找所有具有 created_at 和 updated_at 列的 bytedesk_ 表
INSERT INTO temp_tables_to_rollback (table_name, has_created_at, has_updated_at, created_at_type, updated_at_type)
SELECT 
    t.TABLE_NAME,
    CASE WHEN c1.COLUMN_NAME IS NOT NULL THEN TRUE ELSE FALSE END as has_created_at,
    CASE WHEN c2.COLUMN_NAME IS NOT NULL THEN TRUE ELSE FALSE END as has_updated_at,
    c1.DATA_TYPE as created_at_type,
    c2.DATA_TYPE as updated_at_type
FROM INFORMATION_SCHEMA.TABLES t
LEFT JOIN INFORMATION_SCHEMA.COLUMNS c1 ON t.TABLE_NAME = c1.TABLE_NAME AND c1.COLUMN_NAME = 'created_at'
LEFT JOIN INFORMATION_SCHEMA.COLUMNS c2 ON t.TABLE_NAME = c2.TABLE_NAME AND c2.COLUMN_NAME = 'updated_at'
WHERE t.TABLE_SCHEMA = DATABASE()
AND t.TABLE_NAME LIKE 'bytedesk_%'
AND t.TABLE_NAME NOT LIKE 'flyway_%'
AND t.TABLE_NAME NOT LIKE 'liquibase_%'
AND c1.COLUMN_NAME IS NOT NULL
AND c2.COLUMN_NAME IS NOT NULL;

-- 声明变量用于循环处理
SET @done = FALSE;
SET @cur_table = '';

-- 创建游标来遍历所有需要回滚的表
DELIMITER //
DROP PROCEDURE IF EXISTS rollback_timestamp_columns //
CREATE PROCEDURE rollback_timestamp_columns()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur_table VARCHAR(255);
    DECLARE alter_stmt VARCHAR(1000);
    
    -- 声明游标
    DECLARE table_cursor CURSOR FOR 
        SELECT table_name
        FROM temp_tables_to_rollback 
        WHERE has_created_at = TRUE AND has_updated_at = TRUE
        AND (created_at_type = 'timestamp' OR updated_at_type = 'timestamp');
    
    -- 声明 NOT FOUND 处理器
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 打开游标
    OPEN table_cursor;
    
    -- 开始循环
    read_loop: LOOP
        FETCH table_cursor INTO cur_table;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 为当前表生成 ALTER TABLE 语句
        SET @alter_sql = CONCAT(
            'ALTER TABLE `', cur_table, '` ',
            'MODIFY COLUMN `created_at` DATETIME NULL, ',
            'MODIFY COLUMN `updated_at` DATETIME NULL'
        );
        
        -- 执行 ALTER TABLE 语句
        PREPARE stmt FROM @alter_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        -- 输出信息
        SELECT CONCAT('Rolled back table: ', cur_table) AS message;
    END LOOP;
    
    -- 关闭游标
    CLOSE table_cursor;
END //
DELIMITER ;

-- 执行存储过程
CALL rollback_timestamp_columns();

-- 清理
DROP PROCEDURE IF EXISTS rollback_timestamp_columns;
DROP TEMPORARY TABLE IF EXISTS temp_tables_to_rollback;

-- 添加注释
SELECT 'Rollback completed. All bytedesk_ tables with created_at and updated_at columns have been rolled back from TIMESTAMP to DATETIME.' AS message;
