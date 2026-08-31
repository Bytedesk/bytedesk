<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->
# docker

每个镜像一个独立 compose 文件（一镜像一文件），统一存放在 [`compose/`](./compose/) 目录，通过 `start.sh`/`stop.sh` 的关键字参数自由组合启动，不再有 db/mq/scenario 等分层文件。根目录仅保留 readme 说明、启停脚本与 `.env` 配置。

## 快速开始

```bash
# 克隆项目
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker

# 配置环境变量（按需修改密码/密钥）
cp .env.example .env

# 默认启动全栈（mysql + artemis + redis + elasticsearch + bytedesk 应用）
./start.sh

# 访问 http://127.0.0.1:9003，默认账号 admin@email.com / admin
```

## 常用命令

```bash
# 默认全栈（等价 ./start.sh all）
./start.sh
./stop.sh              # 停止
./stop.sh down         # 删除容器（保留数据卷）

# 仅中间件（源码本地开发，不启动 bytedesk 应用镜像）
./start.sh middleware
./stop.sh middleware down

# 切换数据库 / 消息队列（四选一 db，二选一 mq）
./start.sh postgresql rabbitmq middleware
./start.sh oracle middleware
./start.sh kingbase all

# 呼叫中心（call = freeswitch + mrcp；仅支持 mysql/postgresql）
./start.sh call all
./start.sh call middleware
./stop.sh call middleware down

# WebRTC 音视频（webrtc = coturn + janus）
./start.sh call webrtc all
./start.sh call webrtc middleware obs minio mrcp searxng neo4j  # 任意组合
./stop.sh call webrtc middleware obs minio mrcp searxng neo4j down

# 可选组件（任意组合）
./start.sh all minio searxng
./start.sh middleware obs        # obs = prometheus + grafana + zipkin
./start.sh middleware logstash kibana
./stop.sh middleware logstash kibana down
```

## 关键字速查

| 类别 | 关键字（别名） | 说明 |
| --- | --- | --- |
| 数据库（四选一，默认 mysql） | `mysql` `postgresql`(pg) `oracle` `kingbase`(kingbase9) | 自动建库 |
| 消息队列（二选一，默认 artemis） | `artemis` `rabbitmq` | |
| 核心中间件 | `redis` `elasticsearch`(es) | 任何栈自动包含 |
| 呼叫中心 | `freeswitch` `mrcp`，组合 `call` | call 仅支持 mysql/postgresql |
| WebRTC | `coturn` `janus`，组合 `webrtc` | |
| 搜索/存储 | `searxng`(search) `minio` `neo4j` | 企业版功能 |
| 日志 | `logstash` `kibana` | 依赖 elasticsearch |
| 可观测 | `prometheus` `grafana` `zipkin`，组合 `obs` | |
| 目标 | `middleware` / `all`(bytedesk)，默认 all | 展开见下表 |

### all / middleware 对应镜像

| 目标关键字 | 展开包含的镜像（compose 文件） | 说明 |
| --- | --- | --- |
| `all`（默认） | `middleware` 全部 + **bytedesk 应用**（compose-redis + compose-elasticsearch + compose-<db> + compose-<mq> + compose-bytedesk） | 全栈：中间件 + 应用镜像 |
| `middleware` | compose-redis + compose-elasticsearch + compose-<所选db> + compose-<所选mq> | 仅中间件，**不含** bytedesk 应用，用于源码本地开发 |

说明：redis 与 elasticsearch 为核心中间件，任何栈都自动包含；`<所选db>` 默认 mysql（可选 postgresql/oracle/kingbase），`<所选mq>` 默认 artemis（可选 rabbitmq）；其余组件（freeswitch、obs、minio 等）按关键字叠加。

停止动作：`./stop.sh [stop|down] [关键字...]`，动作词可在任意位置，关键字与启动时一致；`down` 删除容器保留数据卷。

## 文件清单

所有 compose 文件及各组件配套配置（`searxng/`、`grafana/`、`logstash/`、`prometheus.yml`、`ik-plugin-cache/`）均位于 [`compose/`](./compose/) 目录：

| compose 文件（位于 compose/） | 镜像 | 默认端口 | 详细说明 |
| --- | --- | --- | --- |
| compose/compose-redis.yaml | redis | 16379 | — |
| compose/compose-elasticsearch.yaml | elasticsearch:9.4.2 | 19200/19300 | — |
| compose/compose-mysql.yaml | mysql | 13306 | [数据库](./readme/readme.database.md) |
| compose/compose-postgresql.yaml | postgres:17 | 15432 | [数据库](./readme/readme.database.md) |
| compose/compose-oracle.yaml | gvenzl/oracle-free:23-slim | 11521 | [数据库](./readme/readme.database.md) |
| compose/compose-kingbase.yaml | kingbase V9 | 54321 | [数据库](./readme/readme.database.md) |
| compose/compose-artemis.yaml | activemq-artemis:2.40.0 | 16161/18161 | [消息队列](./readme/readme.mq.md) |
| compose/compose-rabbitmq.yaml | rabbitmq:4.2.3 | 5673/15673 | [消息队列](./readme/readme.mq.md) |
| compose/compose-bytedesk.yaml | bytedesk 应用 | 9003/9885 | — |
| compose/compose-freeswitch.yaml | bytedesk/freeswitch | 15060/18021 | [FreeSWITCH](./readme/readme.freeswitch.md) |
| compose/compose-mrcp.yaml | bytedesk/mrcp | 11544 | [呼叫中心](./readme/readme.call.md) |
| compose/compose-coturn.yaml | coturn:4.6.3 | 13478 | [WebRTC](./readme/readme.webrtc.md) |
| compose/compose-janus.yaml | bytedesk/janus | 18089/18188 | [WebRTC](./readme/readme.webrtc.md) |
| compose/compose-searxng.yaml | searxng | 18888 | [SearXNG](./readme/readme.searxng.md) |
| compose/compose-neo4j.yaml | neo4j:5.26.30 | 17474/17687 | [Neo4j 知识图谱](./readme/readme.neo4j.md) |
| compose/compose-logstash.yaml | logstash:9.4.2 | 19600 | [Logstash](./readme/readme.logstash.md) |
| compose/compose-kibana.yaml | kibana:9.4.2 | 15601 | [Kibana](./readme/readme.kibana.md) |
| compose/compose-minio.yaml | minio | 19000/19001 | [MinIO](./readme/readme.minio.md) |
| compose/compose-prometheus.yaml | prometheus | 19090 | [可观测性](./readme/readme.observability.md) |
| compose/compose-grafana.yaml | grafana | 13000 | [可观测性](./readme/readme.observability.md) |
| compose/compose-zipkin.yaml | zipkin | 19411 | [可观测性](./readme/readme.observability.md) |

其他文件：`start.sh`/`stop.sh`（组合启停）、`watchdog.sh`（应用看门狗，详见 [watchdog 使用说明](./readme/readme.watchdog.md)）、`.env`（敏感配置）、`one/`（all-in-one 单文件部署）。

## 手动执行 docker compose

如需绕过脚本手动操作，在 `deploy/docker` 目录执行（`--env-file .env` 加载敏感配置）：

```bash
docker compose --env-file .env -p bytedesk \
  -f compose/compose-redis.yaml -f compose/compose-elasticsearch.yaml \
  -f compose/compose-mysql.yaml -f compose/compose-artemis.yaml \
  -f compose/compose-bytedesk.yaml up -d
```

说明：compose 文件内的相对路径（`./searxng`、`../../freeswitch` 等）均以 `compose/` 目录为基准解析；`.env` 中 `KINGBASE_LICENSE_FILE`、`MRCP_*_DIR` 等相对路径同样相对于 `compose/` 目录。容器名、数据卷名、网络名保持不变，存量数据无缝延续。

## 更多说明

详细文档位于 [readme/](./readme/) 目录：

- [快速开始与常见问题](./readme/readme.quickstart.md)
- [数据库切换与自动建库](./readme/readme.database.md)
- [消息队列切换](./readme/readme.mq.md)
- [呼叫中心（FreeSWITCH + MRCP）](./readme/readme.call.md) / [FreeSWITCH 详细配置](./readme/readme.freeswitch.md)
- [WebRTC 音视频（coturn + janus）](./readme/readme.webrtc.md)
- [SearXNG 联网搜索](./readme/readme.searxng.md)（企业版）
- [Neo4j 知识图谱](./readme/readme.neo4j.md)（企业版）
- [Logstash 日志采集](./readme/readme.logstash.md) / [Kibana 日志查询](./readme/readme.kibana.md)
- [MinIO 对象存储](./readme/readme.minio.md)
- [可观测性（Prometheus + Grafana + Zipkin）](./readme/readme.observability.md)
- [watchdog.sh 应用看门狗](./readme/readme.watchdog.md)
- [环境变量说明](./readme/readme.env.md)
- [旧版本迁移指南](./readme/readme.migration.md)
