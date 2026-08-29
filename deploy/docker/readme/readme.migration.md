# 旧版本迁移指南 / Migration Guide

本文档帮助从旧版（compose-base/db/mq/app/scenario 结构）迁移到新版（一镜像一文件 + 关键字启动）。

## 命令对照表

| 旧命令 | 新命令（等价） |
| --- | --- |
| `./start.sh mysql artemis standard all` | `./start.sh` |
| `./start.sh mysql artemis standard middleware` | `./start.sh middleware` |
| `./start.sh postgresql rabbitmq standard middleware` | `./start.sh postgresql rabbitmq middleware` |
| `./start.sh oracle artemis noai middleware` | `./start.sh oracle middleware` |
| `./start.sh kingbase9 artemis standard all` | `./start.sh kingbase all` |
| `./start.sh mysql artemis call middleware` | `./start.sh call middleware` |
| `./start.sh mysql artemis webrtc all` | `./start.sh webrtc all` |
| `./start.sh mysql artemis call-webrtc all obs` | `./start.sh call webrtc all obs` |
| `./start.sh mysql artemis standard all minio searxng` | `./start.sh all minio searxng` |
| `./start.sh mysql artemis standard middleware logstash kibana` | `./start.sh middleware logstash kibana` |
| `./stop.sh mysql artemis standard stop all` | `./stop.sh` |
| `./stop.sh mysql artemis standard down all` | `./stop.sh down` |
| `./stop.sh mysql artemis call stop middleware` | `./stop.sh call middleware` |

注意：旧参数（如 `standard`/`noai`/`call-webrtc` 作为位置参数）**不再兼容**，传入会直接报错，请使用新关键字语法。

## 文件名对照表

| 旧文件 | 新文件 |
| --- | --- |
| compose-base.yaml | compose-redis.yaml + compose-elasticsearch.yaml |
| compose-db-mysql.yaml | compose-mysql.yaml |
| compose-db-postgresql.yaml | compose-postgresql.yaml |
| compose-db-oracle.yaml | compose-oracle.yaml |
| compose-db-kingbase9.yaml | compose-kingbase.yaml |
| compose-mq-artemis.yaml | compose-artemis.yaml |
| compose-mq-rabbitmq.yaml | compose-rabbitmq.yaml |
| compose-app-bytedesk.yaml | compose-bytedesk.yaml |
| compose-app-mq-artemis.yaml / compose-app-mq-rabbitmq.yaml | 删除（由 start.sh 注入应用 env，见下） |
| compose-scenario-standard.yaml | 删除（空壳） |
| compose-scenario-noai.yaml | 删除（AI 开关移交 .env） |
| compose-scenario-call.yaml | compose-freeswitch.yaml + compose-mrcp.yaml |
| compose-scenario-webrtc.yaml | compose-coturn.yaml + compose-janus.yaml |
| compose-observability.yaml | compose-prometheus.yaml + compose-grafana.yaml + compose-zipkin.yaml |

> 2026-08-26 起所有 compose 文件统一移入 `deploy/docker/compose/` 目录（文件名不变）；同时 `.env` 中 `KINGBASE_LICENSE_FILE`、`MRCP_CONF_DIR`、`MRCP_LOG_DIR`、`MRCP_AUDIO_DIR` 的相对路径由 `../` 改为 `../../`（相对于 `compose/` 目录解析）。容器名、数据卷名、网络名均不变，存量数据无缝延续。

## 服务名变更

| 旧服务名 | 新服务名 |
| --- | --- |
| bytedesk-db | bytedesk-mysql / bytedesk-postgresql / bytedesk-oracle / bytedesk-kingbase |
| bytedesk-mq | bytedesk-artemis / bytedesk-rabbitmq |
| bytedesk-mrcp-server | bytedesk-mrcp |

**容器名、数据卷名、网络名（bytedesk-network）均未变化，存量数据无缝延续。**

如有自定义脚本引用旧服务名（如 `docker compose exec bytedesk-db ...`），请改为对应新服务名。

## .env 需要同步修改的值

```bash
# FreeSWITCH 数据库主机（旧：bytedesk-db）
FREESWITCH_DB_HOST=bytedesk-mysql
# 百度 MRCP Server 地址（旧：bytedesk-mrcp-server）
FREESWITCH_BAIDU_MRCP_SERVER_HOST=bytedesk-mrcp
```

## 应用连接注入的变化

- 旧版：db/mq 连接参数写在 compose-db-*/compose-app-mq-* overlay 文件中
- 新版：`start.sh` 按关键字自动注入并写入 `.env.app`（自动生成、勿手工编辑）；`.env` 显式配置优先级更高
- 单独 `docker compose --env-file .env -f compose/compose-bytedesk.yaml up -d`（不经 start.sh，在 deploy/docker 目录执行）时应用按 mysql/artemis 默认值连接；其他数据库请通过 `--env-file .env --env-file .env.app` 或环境变量提供连接参数

## 升级步骤

```bash
# 1.（推荐）先用旧脚本停净旧栈，再拉取新版本
./stop.sh <旧参数> down

# 2. 若已升级但残留旧容器，按 compose 项目标签兜底清理
docker rm -f $(docker ps -aq --filter label=com.docker.compose.project=bytedesk)

# 3. 更新 .env 中两个服务名相关值（见上）
# 4. 使用新命令启动
./start.sh
```

## watchdog.sh 变更

- 旧环境变量 `WATCHDOG_DB` / `WATCHDOG_MQ` / `WATCHDOG_SCENARIO` 已移除（不再需要）
- 新增 `PROJECT_NAME`（默认 bytedesk，与 start.sh 一致）
- 重启策略：优先 `docker start` 保留容器原始环境；容器不存在时回退 `docker compose --env-file .env [--env-file .env.app] -f compose/compose-bytedesk.yaml up -d --no-deps bytedesk`（配合 .env.app）
