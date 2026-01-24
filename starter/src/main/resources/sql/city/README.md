# City SQL 导入说明

为了避免项目启动被大 SQL 阻塞，城市库导入采用“首次启动后异步后台导入”的方式（可开关控制）。

## 需要的 SQL 文件

- `bytedesk_core_city.sql`：直接向 `bytedesk_core_city` 表写入数据（可包含建表语句）。

> 注意：该脚本可能比较大，建议部署时可放到运行目录（`${user.dir}`）下，文件名默认为 `bytedesk_core_city.sql`，或自行放入 classpath 路径 `sql/city/bytedesk_core_city.sql`。

## 开关配置

在 `application.properties` 中开启：

- `bytedesk.city.import.enabled=true`

默认会尝试按以下顺序找 staging 脚本：

1. `classpath:sql/city/bytedesk_core_city.sql`
2. `file:${bytedesk.city.import.fallback-staging-file}`（默认 `${user.dir}/bytedesk_core_city.sql`）
