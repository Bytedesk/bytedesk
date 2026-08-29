# 数据库切换 / Database

支持 4 种数据库，通过 start.sh 关键字选择（四选一，默认 `mysql`）：`mysql`、`postgresql`（别名 `pg`）、`oracle`、`kingbase`（别名 `kingbase9`）。

## 对应 compose 文件

| 关键字 | compose 文件（位于 compose/） | 镜像 | 默认宿主机端口 |
| --- | --- | --- | --- |
| mysql | compose/compose-mysql.yaml | mysql:latest | 13306 |
| postgresql / pg | compose/compose-postgresql.yaml | postgres:17 | 15432 |
| oracle | compose/compose-oracle.yaml | gvenzl/oracle-free:23-slim | 11521 |
| kingbase / kingbase9 | compose/compose-kingbase.yaml | kingbase V9（需 license） | 54321 |

## 使用示例

```bash
# PostgreSQL 中间件（源码开发）
./start.sh postgresql middleware

# Oracle 全栈
./start.sh oracle all

# Kingbase 全栈（.env 中需配置 KINGBASE_LICENSE_FILE 指向 ../../kingbase/ 下的 license.dat）
./start.sh kingbase all

# 切换后停止（关键字保持一致）
./stop.sh postgresql middleware down
```

## 自动建库

`start.sh` 会在启动后自动确保应用数据库存在（不存在则创建）：

| 数据库 | 使用的变量 | 默认库名 |
| --- | --- | --- |
| mysql | MYSQL_DATABASE / MYSQL_ROOT_PASSWORD | bytedesk |
| postgresql | POSTGRES_DB / POSTGRES_PASSWORD | bytedesk |
| oracle | ORACLE_DATABASE / ORACLE_PASSWORD / ORACLE_APP_USER / ORACLE_APP_USER_PASSWORD | BYTEDESK（PDB） |
| kingbase | KINGBASE_DATABASE / KINGBASE_DB_PASSWORD | bytedesk |

## 应用侧连接注入

bytedesk 应用的数据源连接（`SPRING_DATASOURCE_URL/USERNAME/PASSWORD` 等）由 `start.sh` 按所选数据库自动注入并写入 `.env.app`：

- 使用 mysql 时指向 `bytedesk-mysql:3306`
- 使用 postgresql 时指向 `bytedesk-postgresql:5432`（附带 postgres Driver）
- 使用 oracle 时指向 `bytedesk-oracle:1521/FREEPDB1`
- 使用 kingbase 时指向 `bytedesk-kingbase:54321`（附带 PostgreSQLDialect、quartz/batch/flowable postgres 适配、关闭 liquibase）

如需完全接管，可在 `.env` 中显式配置 `SPRING_DATASOURCE_*`（优先级高于脚本注入），或通过 shell 环境变量覆盖。详见 [readme.env.md](./readme.env.md)。

## 限制

- **freeswitch/mrcp（call）仅支持 mysql / postgresql**：`./start.sh call oracle` 会直接报错
- Kingbase 需要 license 文件（默认取 `deploy/kingbase/` 下的专业版 dat，可在 .env 覆盖）
- 切换数据库不会迁移数据；各数据库使用独立数据卷（如 bytedesk_mysql_data / bytedesk_postgresql_data）
