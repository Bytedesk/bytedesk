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
├── compose-scenario-call.yaml # 呼叫中心语音场景扩展（freeswitch）
├── compose-scenario-webrtc.yaml # 音视频客服 WebRTC 组件（coturn/janus）
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

# 7) Call-center middleware scenarios (FreeSWITCH only)
./start.sh mysql artemis call middleware
./stop.sh mysql artemis call stop middleware
./stop.sh mysql artemis call down middleware

./start.sh mysql rabbitmq call middleware
./stop.sh mysql rabbitmq call stop middleware
./stop.sh mysql rabbitmq call down middleware

./start.sh postgresql rabbitmq call middleware
./stop.sh postgresql rabbitmq call stop middleware
./stop.sh postgresql rabbitmq call down middleware

# 8) WebRTC middleware scenarios (coturn + janus)
./start.sh mysql artemis webrtc middleware
./stop.sh mysql artemis webrtc stop middleware
./stop.sh mysql artemis webrtc down middleware

./start.sh postgresql rabbitmq webrtc middleware
./stop.sh postgresql rabbitmq webrtc stop middleware
./stop.sh postgresql rabbitmq webrtc down middleware

# 9) Call-center + WebRTC middleware scenarios (FreeSWITCH + coturn + janus)
./start.sh mysql artemis call-webrtc middleware
./stop.sh mysql artemis call-webrtc stop middleware
./stop.sh mysql artemis call-webrtc down middleware

./start.sh postgresql rabbitmq call-webrtc middleware
./stop.sh postgresql rabbitmq call-webrtc stop middleware
./stop.sh postgresql rabbitmq call-webrtc down middleware

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

# 6) MySQL + Artemis + webrtc (audio/video customer service release)
./start.sh mysql artemis webrtc all
./stop.sh mysql artemis webrtc stop all
./stop.sh mysql artemis webrtc down all

# 7) PostgreSQL + Artemis + call-webrtc (call-center + audio/video customer service release)
./start.sh postgresql artemis call-webrtc all
./stop.sh postgresql artemis call-webrtc stop all
./stop.sh postgresql artemis call-webrtc down all

# quick reference:
# db: mysql | postgresql | oracle | kingbase9
# mq: artemis | rabbitmq
# scenario: standard | noai | call | webrtc | call-webrtc
# note: call/call-webrtc scenarios support mysql and postgresql only; webrtc scenario has no extra DB restriction
# call-webrtc also supports the webrtc-call alias
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
# for mysql+call: when FREESWITCH_DATABASE is empty, start.sh auto imports deploy/sql/freeswitch-1.10.12.sql;
#                 if tables already exist, initialization is skipped to avoid re-running DROP TABLE statements.
# when you need both FreeSWITCH and WebRTC, use the call-webrtc scenario; scripts combine compose-scenario-call.yaml and compose-scenario-webrtc.yaml automatically.

# start docker compose container, -f flag to specify file path, -d flag to start container in background mode
# note: ollama is part of compose-base.yaml now
# middleware only (for source startup)
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-webrtc.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-mysql.yaml -f compose-scenario-webrtc.yaml up -d

# full stack (middleware + bytedesk image)
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-rabbitmq.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-rabbitmq.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d

# database switch examples
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-oracle.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-kingbase9.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# chat model
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# embedding model
docker exec ollama-bytedesk ollama pull bge-m3:latest
# rerank model
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# stop standard stack
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml stop
# stop call-center voice stack
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml stop
# stop call-center voice + webrtc stack
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml stop
```

## Sensitive variables (centralized in `.env`)

At minimum, set these before startup:

- DB/MQ: `MYSQL_ROOT_PASSWORD`, `POSTGRES_PASSWORD`, `ORACLE_PASSWORD`, `ORACLE_APP_USER_PASSWORD`, `KINGBASE_DB_PASSWORD`, `KINGBASE_SYSTEM_PWD`, `KINGBASE_LICENSE_FILE`, `ARTEMIS_PASSWORD`, `RABBITMQ_DEFAULT_PASS`
- Middleware: `REDIS_PASSWORD`, `ELASTIC_PASSWORD`, `MINIO_ROOT_PASSWORD`
- App auth: `BYTEDESK_ADMIN_PASSWORD`, `BYTEDESK_ADMIN_VALIDATE_CODE`, `BYTEDESK_MEMBER_PASSWORD`, `BYTEDESK_JWT_SECRET_KEY`
- Call/WebRTC scenario: `COTURN_PASS`, `FREESWITCH_ESL_PASSWORD`
- Optional API keys: `SPRING_AI_*_API_KEY`, `BYTEDESK_TRANSLATE_BAIDU_*`, `BYTEDESK_LICENSE_KEY`

## Secrets & Jasypt (optional)

Some docker compose entries may be stored as `ENC(...)`. Only when you actually use those encrypted values do you need to pass the Jasypt password into Docker:

```bash
# 1. Add the password to .env so compose picks it up (never commit real secrets).
echo 'JASYPT_ENCRYPTOR_PASSWORD=please-change-me' >> .env

# 2. Start any full stack as usual. The Bytedesk service will read the env var.
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
```

- Leave `JASYPT_ENCRYPTOR_PASSWORD` blank (or remove the line) when no encrypted values are in use—startup will fall back to plain text.
- You can also override algorithms or iterations with additional variables (for example `BYTEDESK_SECURITY_JASYPT_ALGORITHM=PBEWITHHMACSHA512ANDAES_256`).

## Logstash Log Ingestion

`compose-base.yaml` now includes `bytedesk-logstash`, which collects logs from both of these sources by default:

- Docker app container logs written to the shared `/app/logs/bytedeskim.log`
- Local source-run logs written to [starter/logs](starter/logs)/bytedeskim.log

```bash
# Start the full stack; Logstash will begin shipping Bytedesk application logs automatically
./start.sh mysql artemis standard all

# If you run starter from source locally, only Logstash needs to be up; starter/logs/bytedeskim.log will also be collected
docker compose -p bytedesk --env-file .env -f compose-base.yaml up -d bytedesk-logstash

# Inspect Logstash status and logs
docker compose -p bytedesk --env-file .env -f compose-base.yaml ps bytedesk-logstash
docker compose -p bytedesk --env-file .env -f compose-base.yaml logs -f bytedesk-logstash

# List the generated Elasticsearch log indices
curl -u elastic:${ELASTIC_PASSWORD} http://127.0.0.1:19200/_cat/indices/bytedesk-logs-*?v

# Fetch the latest 20 log events
curl -u elastic:${ELASTIC_PASSWORD} 'http://127.0.0.1:19200/bytedesk-logs-*/_search?size=20&sort=@timestamp:desc'
```

Notes:

- The Logstash monitoring API is exposed on port `19600`.
- Log indices are named `bytedesk-logs-YYYY.MM.dd`.
- The pipeline merges Java stack traces, and the application file log is now emitted as plain text for clean Elasticsearch indexing.
- For local source runs, Logstash reads starter/logs/bytedeskim.log by default.
- If starter is already running locally, restart that source-run process once after this update so the new plain-text file logging pattern takes effect; newly written lines will then be parsed and indexed reliably.

## Kibana Log Viewer

`compose-base.yaml` now also includes `bytedesk-kibana`, so you can browse Elasticsearch log indices from a web UI.

```bash
# Start Kibana only
docker compose -p bytedesk --env-file .env -f compose-base.yaml up -d bytedesk-kibana

# Inspect Kibana status and logs
docker compose -p bytedesk --env-file .env -f compose-base.yaml ps bytedesk-kibana
docker compose -p bytedesk --env-file .env -f compose-base.yaml logs -f bytedesk-kibana
```

Access:

- Kibana: <http://127.0.0.1:15601>
- Elasticsearch: <http://127.0.0.1:19200>
- Login flow: open the browser UI and sign in with a built-in Elasticsearch user
- Recommended username: `elastic`
- Password: `ELASTIC_PASSWORD` from `.env`

Current local default values if you have not changed `deploy/docker/.env`:

- Username: `elastic`
- Password: `bytedesk123`

Kibana itself connects to Elasticsearch through `KIBANA_SERVICE_ACCOUNT_TOKEN` in `.env`, so it does not need the forbidden superuser backend configuration.

Recommended first steps:

- Open <http://127.0.0.1:15601> and sign in with `elastic` and the `ELASTIC_PASSWORD` value from `.env`
- If the browser redirects to `/login?next=%2F`, that is expected; just continue on the login page
- Create a data view for `bytedesk-logs-*`
- Use `@timestamp` as the time field
- Search logs in Discover by `requestId`, `traceId`, or `message`

Suggested query flow:

- Open `Discover`
- Select the `bytedesk-logs-*` data view
- Search for `requestId : "a9d759fa-f7af-4551-b219-9d358403553d"`
- Or search for `message : "Completed 200 OK"` to inspect one request path

## more info

config docker engine

```bash
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
    }
  },
  "debug": true,
  "experimental": true,
  "insecure-registries": [
    "121.37.217.138:5000"
  ],
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.mybacc.com",
    "https://dytt.online",
    "https://lispy.org",
    "https://docker.xiaogenban1993.com",
    "https://docker.yomansunter.com",
    "https://aicarbon.xyz",
    "https://666860.xyz",
    "https://docker.zhai.cm",
    "https://a.ussh.net",
    "https://hub.littlediary.cn",
    "https://hub.rat.dev",
    "https://docker.m.daocloud.io"
  ]
}
```
