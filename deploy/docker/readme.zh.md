<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 10:27:57
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
├── compose-call-db-mysql.yaml # 历史保留：旧版 call 场景 FreeSWITCH 的 MySQL DSN 覆盖
├── compose-call-db-postgresql.yaml # 历史保留：旧版 call 场景 FreeSWITCH 的 PostgreSQL DSN 覆盖
├── compose-scenario-noai.yaml # 不使用 AI 的场景覆盖
├── compose-scenario-standard.yaml # 标准场景覆盖
├── compose-observability.yaml # 可观测性栈（Prometheus + Grafana + Zipkin，独立 overlay）
├── start.sh # 组合启动脚本：start.sh <db> <mq> <scenario> [all|middleware] [obs]
└── stop.sh # 组合停止脚本：stop.sh <db> <mq> <scenario> [stop|down] [all|middleware] [obs]
```

## 微语启动步骤

```bash
# https://www.weiyuai.cn/docs/zh-CN/docs/deploy/docker
# 克隆项目
git clone https://github.com/Bytedesk/bytedesk.git
# 进入docker目录
cd bytedesk/deploy/docker
# 配置环境变量，根据需要修改
cp .env.example .env
# 重要：密码/API Key/JWT 等敏感信息已统一迁移到 .env
# start.sh/stop.sh 会通过 --env-file 自动加载 deploy/docker/.env
# 如需调整本地 ASR 模型或设备，可在 .env 中修改 FUNASR_DEVICE、FUNASR_MODEL、FUNASR_VAD_MODEL。
# 注意：.env 务必使用 UTF-8 编码保存（推荐无 BOM），且值中不要包含中文全角引号或首尾空格，否则可能导致环境变量解析异常。

# 脚本方式（推荐）
# 参数格式：
# start.sh <db> <mq> <scenario> <target> [obs]
# stop.sh  <db> <mq> <scenario> <action> <target> [obs]
# obs（可选，最后一个参数）：obs | observability | true | yes   启用 compose-observability.yaml（Prometheus + Grafana + Zipkin）

# 本地测试（middleware，仅启动中间件）
# 1) MySQL + Artemis + 标准场景（源码本地开发）
./start.sh mysql artemis standard middleware
./stop.sh mysql artemis standard stop middleware
./stop.sh mysql artemis standard down middleware

# 2) MySQL + RabbitMQ + 标准场景（MQ 切换联调）
./start.sh mysql rabbitmq standard middleware
./stop.sh mysql rabbitmq standard stop middleware
./stop.sh mysql rabbitmq standard down middleware

# 3) PostgreSQL + Artemis + 标准场景（数据库切换验证）
./start.sh postgresql artemis standard middleware
./stop.sh postgresql artemis standard stop middleware
./stop.sh postgresql artemis standard down middleware

./start.sh postgresql rabbitmq standard middleware
./stop.sh postgresql rabbitmq standard stop middleware
./stop.sh postgresql rabbitmq standard down middleware

# 4) PostgreSQL + RabbitMQ + noai（禁用 AI 依赖）
./start.sh postgresql rabbitmq noai middleware
./stop.sh postgresql rabbitmq noai stop middleware
./stop.sh postgresql rabbitmq noai down middleware

# 5) Oracle + Artemis + noai（源码本地调试推荐）
./start.sh oracle artemis noai middleware
./stop.sh oracle artemis noai stop middleware
./stop.sh oracle artemis noai down middleware

# 6) Kingbase9 + Artemis + standard
./start.sh kingbase9 artemis standard middleware
./stop.sh kingbase9 artemis standard stop middleware
./stop.sh kingbase9 artemis standard down middleware

./start.sh kingbase9 rabbitmq standard middleware
./stop.sh kingbase9 rabbitmq standard stop middleware
./stop.sh kingbase9 rabbitmq standard down middleware

# 7) 呼叫中心中间件场景（仅 FreeSWITCH）
./start.sh mysql artemis call middleware
./stop.sh mysql artemis call stop middleware
./stop.sh mysql artemis call down middleware

./start.sh mysql rabbitmq call middleware
./stop.sh mysql rabbitmq call stop middleware
./stop.sh mysql rabbitmq call down middleware

./start.sh postgresql artemis call middleware
./stop.sh postgresql artemis call stop middleware
./stop.sh postgresql artemis call down middleware

./start.sh postgresql rabbitmq call middleware
./stop.sh postgresql rabbitmq call stop middleware
./stop.sh postgresql rabbitmq call down middleware

# 8) WebRTC 音视频客服中间件场景（coturn + janus）
./start.sh mysql artemis webrtc middleware
./stop.sh mysql artemis webrtc stop middleware
./stop.sh mysql artemis webrtc down middleware

./start.sh postgresql rabbitmq webrtc middleware
./stop.sh postgresql rabbitmq webrtc stop middleware
./stop.sh postgresql rabbitmq webrtc down middleware

# 9) 呼叫中心 + WebRTC 中间件场景（FreeSWITCH + coturn + janus）
./start.sh mysql artemis call-webrtc middleware
./stop.sh mysql artemis call-webrtc stop middleware
./stop.sh mysql artemis call-webrtc down middleware

# 增加可观测性 obs
./start.sh mysql artemis call-webrtc middleware obs
./stop.sh mysql artemis call-webrtc stop middleware obs
./stop.sh mysql artemis call-webrtc down middleware obs

./start.sh postgresql artemis call-webrtc middleware
./stop.sh postgresql artemis call-webrtc stop middleware
./stop.sh postgresql artemis call-webrtc down middleware

./start.sh oracle artemis call-webrtc middleware
./stop.sh oracle artemis call-webrtc stop middleware
./stop.sh oracle artemis call-webrtc down middleware

./start.sh postgresql rabbitmq call-webrtc middleware
./stop.sh postgresql rabbitmq call-webrtc stop middleware
./stop.sh postgresql rabbitmq call-webrtc down middleware

# 线上发布（all，中间件 + bytedesk 应用镜像）
# 1) MySQL + Artemis + 标准场景（默认发布组合）
./start.sh mysql artemis standard all
./stop.sh mysql artemis standard stop all
./stop.sh mysql artemis standard down all

# 2) PostgreSQL + RabbitMQ + 标准场景（RabbitMQ 方案发布）
./start.sh postgresql rabbitmq standard all
./stop.sh postgresql rabbitmq standard stop all
./stop.sh postgresql rabbitmq standard down all

# 3) MySQL + RabbitMQ + 呼叫中心场景（呼叫中心发布）
./start.sh mysql rabbitmq call all
./stop.sh mysql rabbitmq call stop all
./stop.sh mysql rabbitmq call down all

# 4) PostgreSQL + RabbitMQ + 呼叫中心场景（呼叫中心发布）
./start.sh postgresql rabbitmq call all
./stop.sh postgresql rabbitmq call stop all
./stop.sh postgresql rabbitmq call down all

# 5) Kingbase9 + Artemis + 标准场景
./start.sh kingbase9 artemis standard all
./stop.sh kingbase9 artemis standard stop all
./stop.sh kingbase9 artemis standard down all

# 6) MySQL + Artemis + webrtc（音视频客服发布）
./start.sh mysql artemis webrtc all
./stop.sh mysql artemis webrtc stop all
./stop.sh mysql artemis webrtc down all

# 7) PostgreSQL + Artemis + call-webrtc（呼叫中心 + 音视频客服发布）
./start.sh postgresql artemis call-webrtc all
./stop.sh postgresql artemis call-webrtc stop all
./stop.sh postgresql artemis call-webrtc down all

# 参数速查：
# db: mysql | postgresql | oracle | kingbase9
# mq: artemis | rabbitmq
# scenario: standard | noai | call | webrtc | call-webrtc
# 注意：call/call-webrtc 场景仅支持 mysql 与 postgresql；webrtc 场景无额外数据库限制
# call-webrtc 也支持别名 webrtc-call
# target: middleware | all
# action: stop(停止容器) | down(删除容器，保留卷)
# obs（可选）：obs | observability | true | yes   启用 compose-observability.yaml（Prometheus + Grafana + Zipkin），默认不启用

# 组合说明（建议收藏）
# 默认值（缺省参数时）：
# start.sh 等价于：./start.sh mysql artemis standard all
# stop.sh  等价于：./stop.sh  mysql artemis standard stop all
# db 也支持别名：pg -> postgresql，kingbase -> kingbase9
# 可通过环境变量自定义 compose 项目名（默认 bytedesk）：
# PROJECT_NAME=bytedesk-dev ./start.sh mysql artemis standard middleware
# mysql/postgresql/oracle/kingbase9 场景下：start.sh 会自动确保 bytedesk 应用数据库存在（不存在则创建）
# 默认数据库变量分别为：MYSQL_DATABASE / POSTGRES_DB / ORACLE_DATABASE / KINGBASE_DATABASE
# FreeSWITCH 当前仅作为媒体通道，默认不启用 MySQL/PostgreSQL core-db，也不会再随 mysql/postgresql 场景切换 conf
# 如需同时启用 FreeSWITCH 与 WebRTC，可使用 call-webrtc 场景，脚本会自动组合 compose-scenario-call.yaml 与 compose-scenario-webrtc.yaml

# 启动docker compose容器, -f标志来指定文件路径, -d标志表示在后台模式下启动容器
# 说明：ollama 已经放到 compose-base.yaml 公共组件
# 仅启动中间件（用于源码启动）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-webrtc.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-scenario-webrtc.yaml up -d

# 全量启动（中间件 + bytedesk 镜像）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-rabbitmq.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-rabbitmq.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d

# 切换数据库示例
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-oracle.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-kingbase9.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 拉取ollama模型
# 对话模型
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# 嵌入模型
docker exec ollama-bytedesk ollama pull bge-m3:latest
# 重新排序Rerank模型
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# 停止标准模式
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml down
# 停止呼叫中心语音模式
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml down
# 停止呼叫中心语音 + WebRTC 模式
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml down
# 停止（noai）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml down
```

## 敏感变量（统一放在 `.env`）

启动前至少配置以下变量：

- 数据库与消息队列：`MYSQL_ROOT_PASSWORD`、`POSTGRES_PASSWORD`、`ORACLE_PASSWORD`、`ORACLE_APP_USER_PASSWORD`、`KINGBASE_DB_PASSWORD`、`KINGBASE_SYSTEM_PWD`、`KINGBASE_LICENSE_FILE`、`ARTEMIS_PASSWORD`、`RABBITMQ_DEFAULT_PASS`
- 中间件：`REDIS_PASSWORD`、`ELASTIC_PASSWORD`、`MINIO_ROOT_PASSWORD`
- 应用认证：`BYTEDESK_ADMIN_PASSWORD`、`BYTEDESK_ADMIN_VALIDATE_CODE`、`BYTEDESK_MEMBER_PASSWORD`、`BYTEDESK_JWT_SECRET_KEY`
- 呼叫 / WebRTC 场景：`COTURN_PASS`、`FREESWITCH_ESL_PASSWORD`
- 可选 API 密钥：`SPRING_AI_*_API_KEY`、`BYTEDESK_TRANSLATE_BAIDU_*`、`BYTEDESK_LICENSE_KEY`

## 密钥与 Jasypt（可选）

如果在 docker compose 环境变量中把敏感信息写成 `ENC(...)`，则需要在容器启动时注入解密口令；未使用加密时可以忽略。

```bash
# 1. 将口令写入 .env（仅保留在本地，切勿提交）
echo 'JASYPT_ENCRYPTOR_PASSWORD=请修改成强口令' >> .env

# 2. 正常启动全量 compose 文件，服务会自动读取变量
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
# 或
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
# 或
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-scenario-webrtc.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
# 或
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
```

- 当没有加密内容时，保持该变量为空即可，Jasypt 会自动降级为明文。
- 需要调整算法或迭代次数时，可额外设置 `BYTEDESK_SECURITY_JASYPT_ALGORITHM`、`BYTEDESK_SECURITY_JASYPT_KEY_OBTENTION_ITERATIONS` 等环境变量。

## Logstash 日志采集

`compose-base.yaml` 已内置 `bytedesk-logstash`，默认同时采集两类日志：

- Docker 应用容器写入共享卷 `/app/logs/bytedeskim.log` 的日志
- 本地源码运行写入 [starter/logs](starter/logs) 的 `bytedeskim.log`

```bash
# 启动全量服务后，Logstash 会自动读取 bytedesk 应用日志并写入 Elasticsearch
./start.sh mysql artemis standard all

# 如果你是本地源码运行 starter，只需要保证 Logstash 已启动，starter/logs/bytedeskim.log 也会被采集
docker compose -p bytedesk --env-file .env -f compose-base.yaml up -d bytedesk-logstash

# 查看 Logstash 运行状态
docker compose -p bytedesk --env-file .env -f compose-base.yaml ps bytedesk-logstash
docker compose -p bytedesk --env-file .env -f compose-base.yaml logs -f bytedesk-logstash

# 在 Elasticsearch 中查看日志索引
curl -u elastic:${ELASTIC_PASSWORD} http://127.0.0.1:19200/_cat/indices/bytedesk-logs-*?v

# 查询最近 20 条日志
curl -u elastic:${ELASTIC_PASSWORD} 'http://127.0.0.1:19200/bytedesk-logs-*/_search?size=20&sort=@timestamp:desc'
```

说明：

- Logstash 监控接口暴露在 `19600` 端口。
- 日志索引命名为 `bytedesk-logs-YYYY.MM.dd`。
- 管道会自动合并 Java 异常堆栈；应用文件日志已切换为纯文本格式，便于在 Elasticsearch 中检索。
- 本地源码运行时，默认读取 [starter/logs](starter/logs) 下的 `bytedeskim.log`。
- 如果 starter 已经在本地运行，请在本次更新后重启一次源码进程，让新的纯文本文件日志格式生效；重启后新写入的日志会被稳定解析并按字段入库。

## Kibana 日志查询

`compose-base.yaml` 已内置 `bytedesk-kibana`，启动后可通过浏览器直接查看 Elasticsearch 中的日志索引。

```bash
# 单独启动 Kibana
docker compose -p bytedesk --env-file .env -f compose-base.yaml up -d bytedesk-kibana

# 查看 Kibana 运行状态
docker compose -p bytedesk --env-file .env -f compose-base.yaml ps bytedesk-kibana
docker compose -p bytedesk --env-file .env -f compose-base.yaml logs -f bytedesk-kibana
```

访问地址：

- Kibana: <http://127.0.0.1:15601>
- Elasticsearch: <http://127.0.0.1:19200>
- 登录方式: 浏览器打开后直接进入登录页，使用 Elasticsearch 内置账号登录即可
- 推荐账号: `elastic`
- 登录密码: `.env` 中的 `ELASTIC_PASSWORD`

当前本地默认值（若你没有改过 `deploy/docker/.env`）：

- 用户名: `elastic`
- 密码: `bytedesk123`

说明：Kibana 服务自身连接 Elasticsearch 使用的是 `.env` 中的 `KIBANA_SERVICE_ACCOUNT_TOKEN`，无需再用超级账号作为后端连接账户。

首次进入建议：

- 打开 <http://127.0.0.1:15601>，输入 `elastic` 和 `.env` 中的 `ELASTIC_PASSWORD` 登录
- 若浏览器提示跳转到 `/login?next=%2F`，属于正常行为，继续在登录页输入账号密码即可
- 在 Kibana 的 Data Views 中创建索引模式 `bytedesk-logs-*`
- 时间字段选择 `@timestamp`
- 之后可在 Discover 页面直接按 `requestId`、`traceId`、`message` 检索日志

推荐查询路径：

- 左侧进入 `Discover`
- 选择刚创建的 `bytedesk-logs-*` Data View
- 在顶部搜索框输入例如 `requestId : "a9d759fa-f7af-4551-b219-9d358403553d"`
- 或输入 `message : "Completed 200 OK"` 查看某次请求链路

## FreeSWITCH 媒体模式说明

当前 FreeSWITCH 仅作为媒体通道使用，默认不启用 MySQL/PostgreSQL core-db，也不再根据 mysql/postgresql 场景切换 FreeSWITCH conf。

说明：

- `start.sh` 仍会自动确保 bytedesk 应用数据库存在，即 `MYSQL_DATABASE` / `POSTGRES_DB` / `ORACLE_DATABASE` / `KINGBASE_DATABASE`。
- `call` / `call-webrtc` 场景下，FreeSWITCH 不再依赖 `FREESWITCH_DATABASE`、`FS_CORE_DB_MODULE`、`FS_CORE_DB_DSN`。
- 仓库中的 `compose-call-db-mysql.yaml`、`compose-call-db-postgresql.yaml` 仅作历史保留，当前启动脚本不会再自动加载。

### 向 bytedesk_freeswitch 插入测试数据

推荐使用脚本（更短、更不易输错）：

```bash
cd deploy/docker
# MySQL + Artemis
./insert-freeswitch-input.sh mysql "hello-freeswitch" artemis
# PostgreSQL + RabbitMQ
./insert-freeswitch-input.sh postgresql "hello-freeswitch" rabbitmq
```

参数说明：`./insert-freeswitch-input.sh <db> <message> [mq]`

- `db`: `mysql | postgresql | pg`
- `message`: 要插入的文本内容
- `mq`: `artemis | rabbitmq`（默认 `artemis`）

### 常见问题排查

```bash
cd deploy/docker
./stop.sh postgresql artemis call down middleware
./start.sh postgresql artemis call middleware
docker inspect --format 'Status={{.State.Status}} Health={{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}} RestartCount={{.RestartCount}}' freeswitch-bytedesk
docker logs --tail 200 freeswitch-bytedesk 2>&1 | grep -E "NO SUITABLE DATABASE INTERFACE|Cannot load modules" || true
```

- 预期结果：`Health=healthy`，且日志不再出现上述错误关键字。

```bash
docker compose -p bytedesk \
 -f compose-base.yaml \
 -f compose-db-mysql.yaml \
 -f compose-mq-rabbitmq.yaml \
 -f compose-scenario-call.yaml \
 -f compose-app-bytedesk.yaml \
 -f compose-app-mq-rabbitmq.yaml \
 up -d --force-recreate --remove-orphans bytedesk-db bytedesk-freeswitch
```

1) 验证 FreeSWITCH 是否就绪：

```bash
docker exec freeswitch-bytedesk bash -lc "(echo >/dev/tcp/127.0.0.1/8021) >/dev/null 2>&1 && echo TCP_8021_OPEN || echo TCP_8021_CLOSED"
docker exec freeswitch-bytedesk fs_cli -H 127.0.0.1 -P 8021 -p bytedesk123 -x "status"
```

## 停止和重启服务

```bash
# 停止所有服务（保留数据）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down

# 停止所有服务并删除数据卷（谨慎操作，会删除所有数据）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down -v

# 重启特定服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml restart bytedesk

# 重启所有服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml restart
```

## 升级bytedesk镜像

```bash
# 1. 停止当前服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down
# 或
docker stop bytedesk redis-bytedesk elasticsearch-bytedesk ollama-bytedesk mysql-bytedesk artemis-bytedesk

# 2. 拉取最新镜像
docker pull registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest

# 3. 重新启动服务（会自动使用最新镜像）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 或者使用以下命令强制重新构建并启动
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d --force-recreate bytedesk
```

## 删除MySQL数据挂载

如果需要删除MySQL数据挂载并重新初始化数据库，请按以下步骤操作：

```bash
# 1. 停止所有服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down

# 1. 强制删除MySQL容器（即使它已经退出）
docker rm -f mysql-bytedesk

# 2. 现在可以删除数据卷了
docker volume rm bytedesk_mysql_data

# 3. 重新启动服务（会自动创建新的数据卷和数据库）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 注意：删除数据卷后，所有数据都会丢失，需要重新初始化管理员账户
```

## 数据卷管理

```bash
# 查看所有数据卷
docker volume ls | grep bytedesk

# 查看数据卷详细信息
docker volume inspect bytedesk_mysql_data
docker volume inspect bytedesk_redis_data
docker volume inspect bytedesk_elasticsearch_data
docker volume inspect bytedesk_upload_data
docker volume inspect bytedesk_ollama_models
docker volume inspect bytedesk_artemis_data

# 备份数据卷（可选）
docker run --rm -v bytedesk_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz -C /data .

# 恢复数据卷（可选）
docker run --rm -v bytedesk_mysql_data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql_backup.tar.gz -C /data
```

## 可观测性（Observability）

微语提供独立的可观测性 overlay compose 文件 `compose-observability.yaml`，包含 Prometheus（指标采集）、Grafana（可视化）、Zipkin（分布式追踪）三个服务，通过 Docker `external` 网络接入现有 `bytedesk-network`，不侵入 `compose-base.yaml`。

### 启动观测栈

`start.sh` / `stop.sh` 的最后一个参数可选 `obs`（或 `observability` / `true` / `yes`），用于一键启停 `compose-observability.yaml`：

```bash
cd deploy/docker

# 方式 A（推荐）：在启动中间件/应用栈时附带观测栈
./start.sh mysql artemis standard middleware obs
./start.sh mysql artemis standard all       obs   # 线上全量 + 观测栈
./stop.sh  mysql artemis standard stop middleware obs
./stop.sh  mysql artemis standard down middleware obs

# 方式 B：仅观测栈（需先确保 bytedesk-network 存在）
docker compose -f compose-observability.yaml up -d

# 方式 C：与现有 compose 组合
./start.sh mysql artemis standard middleware
docker compose -f compose-observability.yaml up -d
```

### 访问地址

| 服务 | 地址 | 默认账密 |
| --- | --- | --- |
| Prometheus | http://localhost:19090 | 无 |
| Grafana | http://localhost:13000 | admin / admin（可通过 `.env` 覆盖） |
| Zipkin | http://localhost:19411 | 无 |

### 启用 Zipkin 分布式追踪

Zipkin 默认不随 compose-base 启动。需要时通过 `obs` 参数或 `compose-observability.yaml` 启动，并在应用侧设置环境变量：

```bash
docker compose -f compose-observability.yaml up -d bytedesk-zipkin
export MANAGEMENT_TRACING_ENABLED=true
export MANAGEMENT_ZIPKIN_TRACING_ENABLED=true
export MANAGEMENT_TRACING_SAMPLING_PROBABILITY=1.0
```

### 验证指标

```bash
# 应用指标端点
curl http://localhost:9003/actuator/prometheus | grep gen_ai
```

更多指标参考、PromQL 示例、告警规则与故障排查，请参阅 [AI Observability 文档](../../docs/docs/ops-monitoring/ai-observability.md)。

> Zipkin 容器已从 `one/` 下 5 个 compose 文件中移除，统一由 `compose-observability.yaml` 按需提供，与应用侧 `management.tracing.enabled=false` 的默认策略对齐。

## 故障排除

如果遇到数据库连接问题或服务启动失败，可以尝试以下步骤：

```bash
# 查看容器状态
docker ps -a

# 查看服务日志
docker logs mysql-bytedesk
docker logs bytedesk

# 如果服务启动失败，可以尝试重启
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 检查网络连接
docker network inspect bytedesk-network

# 清理未使用的资源
docker system prune -f
```

## 常用命令

```bash
# 查看服务状态
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml ps

# 查看服务日志
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml logs

# 查看特定服务日志
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml logs bytedesk

# 进入容器内部
docker exec -it bytedesk /bin/bash
docker exec -it mysql-bytedesk mysql -u root -p

# 查看容器资源使用情况
docker stats

# 查看容器镜像架构
docker inspect registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest --format='{{.Architecture}}'
```

## 补充说明

配置docker engine

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
