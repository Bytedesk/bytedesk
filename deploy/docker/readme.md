<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 10:28:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# docker

## 文件说明

```bash
.
├── compose-base.yaml # 公共基础中间件服务（不含 bytedesk 镜像）
├── compose-db-mysql.yaml # MySQL 数据库覆盖（默认）
├── compose-db-postgresql.yaml # PostgreSQL 数据库覆盖
├── compose-db-oracle.yaml # Oracle 数据库覆盖
├── compose-db-kingbase9.yaml # KingbaseES V9 数据库覆盖
├── compose-mq-artemis.yaml # Artemis MQ 组件覆盖（默认）
├── compose-mq-rabbitmq.yaml # RabbitMQ MQ 组件覆盖
├── compose-app-bytedesk.yaml # bytedesk 镜像服务（已从 base 拆分）
├── compose-app-mq-artemis.yaml # bytedesk 的 Artemis 配置覆盖
├── compose-app-mq-rabbitmq.yaml # bytedesk 的 RabbitMQ 配置覆盖
├── compose-scenario-call.yaml # 呼叫中心场景扩展（coturn/freeswitch/janus）
├── compose-call-db-mysql.yaml # call 场景下 FreeSWITCH 的 MySQL DSN 覆盖
├── compose-call-db-postgresql.yaml # call 场景下 FreeSWITCH 的 PostgreSQL DSN 覆盖
├── compose-scenario-noai.yaml # 不使用 AI 的场景覆盖
├── compose-scenario-standard.yaml # 标准场景覆盖
├── start.sh # 组合启动脚本：start.sh <db> <mq> <scenario> [all|middleware]
└── stop.sh # 组合停止脚本：stop.sh <db> <mq> <scenario> [stop|down] [all|middleware]
```

## docker compose

```bash
# https://www.weiyuai.cn/docs/docs/deploy/docker
# clone project
git clone https://github.com/Bytedesk/bytedesk.git
# enter docker directory
cd bytedesk/deploy/docker
# configure environment variables, modify as needed
cp .env.example .env
# IMPORTANT: all sensitive values are now centralized in .env
# (passwords/api keys/jwt/redis/minio/mq/oracle/call credentials)
# start.sh/stop.sh automatically load deploy/docker/.env via --env-file
# start docker compose container, -f flag to specify file path, -d flag to start container in background mode
# note: ollama is part of compose-base.yaml now
# middleware only (for source startup)
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml up -d

# full stack (middleware + bytedesk image)
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-rabbitmq.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d

# database switch examples
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-oracle.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-kingbase9.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# script examples (recommended)
# format:
# start.sh <db> <mq> <scenario> <target>
# stop.sh  <db> <mq> <scenario> <action> <target>

# local testing (middleware only)
# 1) MySQL + Artemis + standard (local source development)
./start.sh mysql artemis standard middleware
./stop.sh mysql artemis standard stop middleware
./stop.sh mysql artemis standard down middleware

# 2) MySQL + RabbitMQ + standard (MQ switch integration)
./start.sh mysql rabbitmq standard middleware
./stop.sh mysql rabbitmq standard stop middleware
./stop.sh mysql rabbitmq standard down middleware

# 3) PostgreSQL + Artemis + standard (DB switch verification)
./start.sh postgresql artemis standard middleware
./stop.sh postgresql artemis standard stop middleware
./stop.sh postgresql artemis standard down middleware

# 4) PostgreSQL + RabbitMQ + noai (without AI dependencies)
./start.sh postgresql rabbitmq noai middleware
./stop.sh postgresql rabbitmq noai stop middleware
./stop.sh postgresql rabbitmq noai down middleware

# 5) Oracle + Artemis + noai (recommended for local source startup)
./start.sh oracle artemis noai middleware
./stop.sh oracle artemis noai stop middleware
./stop.sh oracle artemis noai down middleware

# 6) Kingbase9 + Artemis + standard
./start.sh kingbase9 artemis standard middleware
./stop.sh kingbase9 artemis standard stop middleware
./stop.sh kingbase9 artemis standard down middleware

# 7) Call-center middleware scenarios
./start.sh mysql artemis call middleware
./stop.sh mysql artemis call stop middleware
./stop.sh mysql artemis call down middleware

./start.sh mysql rabbitmq call middleware
./stop.sh mysql rabbitmq call stop middleware
./stop.sh mysql rabbitmq call down middleware

./start.sh postgresql rabbitmq call middleware
./stop.sh postgresql rabbitmq call stop middleware
./stop.sh postgresql rabbitmq call down middleware

# production release (all: middleware + bytedesk app image)
# 1) MySQL + Artemis + standard (default release combination)
./start.sh mysql artemis standard all
./stop.sh mysql artemis standard stop all
./stop.sh mysql artemis standard down all

# 2) PostgreSQL + RabbitMQ + standard (RabbitMQ release scenario)
./start.sh postgresql rabbitmq standard all
./stop.sh postgresql rabbitmq standard stop all
./stop.sh postgresql rabbitmq standard down all

# 3) MySQL + RabbitMQ + call (call-center release)
./start.sh mysql rabbitmq call all
./stop.sh mysql rabbitmq call stop all
./stop.sh mysql rabbitmq call down all

# 4) PostgreSQL + RabbitMQ + call (call-center release)
./start.sh postgresql rabbitmq call all
./stop.sh postgresql rabbitmq call stop all
./stop.sh postgresql rabbitmq call down all

# 5) Kingbase9 + Artemis + standard
./start.sh kingbase9 artemis standard all
./stop.sh kingbase9 artemis standard stop all
./stop.sh kingbase9 artemis standard down all

# quick reference:
# db: mysql | postgresql | oracle | kingbase9
# mq: artemis | rabbitmq
# scenario: standard | noai | call
# note: call scenario supports mysql and postgresql (oracle/kingbase9 are not verified)
# target: middleware | all
# action: stop (stop containers) | down (remove containers, keep volumes)

# composition guide (quick keep)
# defaults when args are omitted:
# start.sh == ./start.sh mysql artemis standard all
# stop.sh  == ./stop.sh  mysql artemis standard stop all
# db alias supported: pg -> postgresql, kingbase -> kingbase9
# override compose project name via env var (default: bytedesk):
# PROJECT_NAME=bytedesk-dev ./start.sh mysql artemis standard middleware
# for kingbase9: start.sh will auto ensure KINGBASE_DATABASE exists (create if missing)

# chat model
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# embedding model
docker exec ollama-bytedesk ollama pull bge-m3:latest
# rerank model
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# stop standard stack
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml stop
# stop call-center stack
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml stop
```

## Sensitive variables (centralized in `.env`)

At minimum, set these before startup:

- DB/MQ: `MYSQL_ROOT_PASSWORD`, `POSTGRES_PASSWORD`, `ORACLE_PASSWORD`, `ORACLE_APP_USER_PASSWORD`, `KINGBASE_DB_PASSWORD`, `KINGBASE_SYSTEM_PWD`, `KINGBASE_LICENSE_FILE`, `ARTEMIS_PASSWORD`, `RABBITMQ_DEFAULT_PASS`
- Middleware: `REDIS_PASSWORD`, `ELASTIC_PASSWORD`, `MINIO_ROOT_PASSWORD`
- App auth: `BYTEDESK_ADMIN_PASSWORD`, `BYTEDESK_ADMIN_VALIDATE_CODE`, `BYTEDESK_MEMBER_PASSWORD`, `BYTEDESK_JWT_SECRET_KEY`
- Call scenario: `COTURN_PASS`, `FREESWITCH_ESL_PASSWORD`
- Optional API keys: `SPRING_AI_*_API_KEY`, `BYTEDESK_TRANSLATE_BAIDU_*`, `BYTEDESK_LICENSE_KEY`

## Secrets & Jasypt (optional)

Some docker compose entries may be stored as `ENC(...)`. Only when you actually use those encrypted values do you need to pass the Jasypt password into Docker:

```bash
# 1. Add the password to .env so compose picks it up (never commit real secrets).
echo 'JASYPT_ENCRYPTOR_PASSWORD=please-change-me' >> .env

# 2. Start any full stack as usual. The Bytedesk service will read the env var.
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
```

- Leave `JASYPT_ENCRYPTOR_PASSWORD` blank (or remove the line) when no encrypted values are in use—startup will fall back to plain text.
- You can also override algorithms or iterations with additional variables (for example `BYTEDESK_SECURITY_JASYPT_ALGORITHM=PBEWITHHMACSHA512ANDAES_256`).
