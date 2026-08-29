<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Logstash 日志采集（Logstash Log Ingestion）

## 中文说明

Logstash（`compose/compose-logstash.yaml`，一镜像一文件）默认不随主栈启动，通过 `start.sh` 关键字或 compose 组合启用。默认同时采集两类日志：

- Docker 应用容器写入共享卷 `bytedesk_log_data`（`/app/logs/bytedeskim.log`）的日志
- 本地源码运行写入 [starter/logs](../../../starter/logs) 的 `bytedeskim.log`

```bash
# 方式 A（推荐）：启动中间件/应用栈时附带 Logstash
./start.sh all logstash
./start.sh middleware logstash
./stop.sh all logstash
./stop.sh down all logstash

# 方式 B：仅 Logstash（需先确保 bytedesk-network 存在；在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-logstash.yaml up -d

# 方式 C：与现有 compose 组合
docker compose --env-file .env -f compose/compose-elasticsearch.yaml -f compose/compose-logstash.yaml up -d bytedesk-logstash

# 查看 Logstash 运行状态
docker compose --env-file .env -f compose/compose-logstash.yaml ps
docker compose --env-file .env -f compose/compose-logstash.yaml logs -f

# 在 Elasticsearch 中查看日志索引
curl -u elastic:${ELASTIC_PASSWORD} http://127.0.0.1:19200/_cat/indices/bytedesk-logs-*?v

# 查询最近 20 条日志
curl -u elastic:${ELASTIC_PASSWORD} 'http://127.0.0.1:19200/bytedesk-logs-*/_search?size=20&sort=@timestamp:desc'
```

说明：

- Logstash 监控接口暴露在 `19600` 端口。
- 日志索引命名为 `bytedesk-logs-YYYY.MM.dd`。
- 管道会自动合并 Java 异常堆栈；应用文件日志已切换为纯文本格式，便于在 Elasticsearch 中检索。
- 本地源码运行时，默认读取 [starter/logs](../../../starter/logs) 下的 `bytedeskim.log`。
- 共享卷 `bytedesk_log_data` 由 app（写入）与 logstash（只读采集）共同挂载，两个 compose 文件中均显式声明。
- 如果 starter 已经在本地运行，请在更新后重启一次源码进程，让新的纯文本文件日志格式生效；重启后新写入的日志会被稳定解析并按字段入库。

## English

Logstash runs in its own file `compose/compose-logstash.yaml` (one image per file) and does not start with the default stack; enable it via the `logstash` keyword or compose composition. It collects logs from both of these sources by default:

- Docker app container logs written to the shared volume `bytedesk_log_data` (`/app/logs/bytedeskim.log`)
- Local source-run logs written to [starter/logs](../../../starter/logs)/bytedeskim.log

```bash
# Option A (recommended): start the stack with Logstash attached
./start.sh all logstash
./start.sh middleware logstash
./stop.sh all logstash
./stop.sh down all logstash

# Option B: Logstash only (requires bytedesk-network to exist first; run from deploy/docker)
docker compose --env-file .env -f compose/compose-logstash.yaml up -d

# Option C: combine with existing compose files
docker compose --env-file .env -f compose/compose-elasticsearch.yaml -f compose/compose-logstash.yaml up -d bytedesk-logstash

# Inspect Logstash status and logs
docker compose --env-file .env -f compose/compose-logstash.yaml ps
docker compose --env-file .env -f compose/compose-logstash.yaml logs -f

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
