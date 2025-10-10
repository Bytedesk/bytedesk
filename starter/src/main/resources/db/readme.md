<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-17 15:21:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-10 16:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 数据库版本管理

## 目录结构

```
db/
├── readme.md                                    # 本文档
└── changelog/
    ├── master.xml                               # Liquibase 主配置文件
    └── migration/                               # 迁移脚本目录
        ├── 250821_rename_kb_type_to_kbase_type.xml
        ├── 250910_rename_api_url_to_base_url.xml
        ├── 250916_rename_name_to_provider_type.xml
        ├── 250922_rename_content_to_process_schema.xml
        ├── 250923_rename_provider_name_to_provider_type.xml
        ├── 250928_add_register_source_to_user.xml
        ├── 251010_create_service_statistic_views.xml    # 客服统计视图
        └── 251010_create_quality_statistic_views.xml   # 质检统计视图
```

## Liquibase 版本管理

本项目使用 **Liquibase** 进行数据库版本管理和迁移。

### 自动执行

应用启动时，Liquibase 会自动执行所有未执行的迁移脚本。

### 查看迁移历史

```sql
-- 查看 Liquibase 执行历史
SELECT * FROM DATABASECHANGELOG ORDER BY DATEEXECUTED DESC;

-- 查看 Liquibase 锁状态
SELECT * FROM DATABASECHANGELOGLOCK;
```

## 创建新的迁移

### 1. 创建迁移文件

格式：`YYMMDD_description.xml`

```bash
cd starter/src/main/resources/db/changelog/migration/
touch 251011_your_migration_description.xml
```

### 2. 编写迁移脚本

```xml
<?xml version="1.0" encoding="utf-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog 
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <changeSet id="251011-your-change-description" author="your-name">
        <preConditions onFail="MARK_RAN">
            <!-- 前置条件检查 -->
        </preConditions>
        
        <!-- 你的变更操作 -->
        
        <rollback>
            <!-- 回滚操作 -->
        </rollback>
    </changeSet>

</databaseChangeLog>
```

### 3. 更新 master.xml

```xml
<include file="db/changelog/migration/251011_your_migration_description.xml" />
```

### 4. 启动应用测试

启动应用后，Liquibase 会自动执行新的迁移。

## 回滚

### 回滚最后一次迁移

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

### 回滚到指定日期

```bash
mvn liquibase:rollback -Dliquibase.rollbackDate=2025-10-09
```

## 最佳实践

1. **命名规范**：使用 `YYMMDD_description.xml` 格式
2. **原子性**：每个 changeSet 只做一件事
3. **可回滚**：为每个 changeSet 提供 rollback 操作
4. **前置条件**：使用 preConditions 避免重复执行
5. **测试**：在开发环境充分测试后再部署到生产

## 常见问题

### 视图已存在错误

如果遇到视图已存在的错误：

1. 检查 preConditions 是否正确
2. 手动删除视图后重新执行：
   ```sql
   DROP VIEW IF EXISTS view_agent_thread_stats;
   ```
3. 使用 Liquibase 回滚后重新执行

### Liquibase 锁定

如果 Liquibase 被锁定：

```sql
UPDATE DATABASECHANGELOGLOCK SET LOCKED = FALSE;
```

### 查看视图

```sql
-- 查看所有视图
SHOW FULL TABLES WHERE table_type = 'VIEW';

-- 查看视图定义
SHOW CREATE VIEW bytedesk_service_view_agent_thread_stats;

-- 查看所有 bytedesk 视图
SHOW FULL TABLES WHERE table_type = 'VIEW' AND Tables_in_weiyuim LIKE 'bytedesk_%';
```

## 相关文档

- [Liquibase 官方文档](https://docs.liquibase.com/)
- [MySQL 视图文档](https://dev.mysql.com/doc/refman/8.0/en/views.html)
- 项目视图对比文档：`enterprise/service/src/main/resources/db/views/VIEW_COMPARISON.md`
