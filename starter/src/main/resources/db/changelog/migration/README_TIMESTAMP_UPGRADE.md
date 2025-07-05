<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-04 20:44:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 20:46:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# MySQL 时间字段类型升级说明

本目录包含将 ByteDesk 数据库中的时间戳字段从 `DATETIME` 类型升级到 `TIMESTAMP` 类型的 SQL 脚本，以提供更好的国际化和时区支持。

## 文件说明

- `250703_upgrade_timestamp_to_zoned.xml` - Liquibase 变更集文件
- `250703_upgrade_timestamp_to_zoned.sql` - 可直接在 MySQL 中执行的升级脚本
- `250703_rollback_timestamp_to_datetime.sql` - 可直接在 MySQL 中执行的回滚脚本

## 使用方法

### 方法一：使用 Liquibase

如果您正在使用 Liquibase 进行数据库版本控制，只需确保变更集文件 `250703_upgrade_timestamp_to_zoned.xml` 被包含在您的主变更日志中，然后运行 Liquibase 更新。

### 方法二：直接在 MySQL 中执行

如果您不使用 Liquibase，可以直接在 MySQL 客户端中执行 SQL 脚本：

1. 连接到您的 MySQL 数据库：

   ```bash
   mysql -u username -p your_database_name
   ```

2. 执行升级脚本：

   ```sql
   source /path/to/250703_upgrade_timestamp_to_zoned.sql
   ```

## 升级内容

此升级会将 ByteDesk 数据库中所有表名以 `bytedesk_` 开头且包含 `created_at` 和 `updated_at` 列的表，将这两个时间字段从 `DATETIME` 类型升级为 `TIMESTAMP` 类型。

MySQL 中的 `TIMESTAMP` 类型具有以下优势：
- 自动存储为 UTC 时间
- 会根据会话时区自动进行转换
- 包含时区信息
- 提供更好的国际化支持和时区管理

## 回滚说明

如果需要回滚此更改，请执行回滚脚本：

```sql
source /path/to/250703_rollback_timestamp_to_datetime.sql
```

## 注意事项

1. 执行前请务必备份数据库。
2. 升级脚本会自动处理数据库中符合条件的所有表，无需手动指定表名。
3. `TIMESTAMP` 类型在 MySQL 中的有效范围是 '1970-01-01 00:00:01' UTC 到 '2038-01-19 03:14:07' UTC，请确保您的数据不会超出此范围。
4. 如果之前使用的是 `DATETIME` 类型且存储的是本地时间（如中国北京时间），升级后系统会自动将其视为 UTC 时间并进行相应转换。
