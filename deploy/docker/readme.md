<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->
# docker

One compose file per image, all collected in the [`compose/`](./compose/) folder, freely combined via `start.sh`/`stop.sh` keyword arguments. No more db/mq/scenario layered files. The root folder only keeps the readme docs, launcher scripts and `.env` config.

## Quick Start

```bash
# clone project
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker

# configure environment variables (adjust passwords/keys as needed)
cp .env.example .env

# start the default full stack (mysql + artemis + redis + elasticsearch + bytedesk app)
./start.sh

# visit http://127.0.0.1:9003, default account admin@email.com / admin
```

## Common Commands

```bash
# default full stack (equals ./start.sh all)
./start.sh
./stop.sh              # stop
./stop.sh down         # remove containers (volumes kept)

# middleware only (for local source development)
./start.sh middleware
./stop.sh middleware down

# switch database / message queue (choose 1 db, 1 mq)
./start.sh postgresql rabbitmq middleware
./start.sh oracle middleware
./start.sh kingbase all

# call center (call = freeswitch + mrcp; mysql/postgresql only)
./start.sh call all
./start.sh call middleware
./stop.sh call middleware down

# WebRTC audio/video (webrtc = coturn + janus)
./start.sh webrtc all
./start.sh call webrtc middleware obs minio mrcp searxng   # free combination
./stop.sh call webrtc middleware obs minio mrcp searxng down

# optional components (any combination)
./start.sh all minio searxng
./start.sh middleware obs        # obs = prometheus + grafana + zipkin
./start.sh middleware logstash kibana
./stop.sh middleware logstash kibana down
```

## Keyword Reference

| Category | Keywords (alias) | Notes |
| --- | --- | --- |
| Database (pick 1, default mysql) | `mysql` `postgresql`(pg) `oracle` `kingbase`(kingbase9) | auto-created |
| Message queue (pick 1, default artemis) | `artemis` `rabbitmq` | |
| Core middleware | `redis` `elasticsearch`(es) | always included |
| Call center | `freeswitch` `mrcp`, combo `call` | call supports mysql/postgresql only |
| WebRTC | `coturn` `janus`, combo `webrtc` | |
| Search/storage | `searxng`(search) `minio` `neo4j` | enterprise feature |
| Logging | `logstash` `kibana` | depends on elasticsearch |
| Observability | `prometheus` `grafana` `zipkin`, combo `obs` | |
| Target | `middleware` / `all`(bytedesk), default all | expansion see below |

### all / middleware expansion

| Target keyword | Included images (compose files) | Notes |
| --- | --- | --- |
| `all` (default) | everything in `middleware` + **bytedesk app** (compose-redis + compose-elasticsearch + compose-<db> + compose-<mq> + compose-bytedesk) | full stack: middleware + app image |
| `middleware` | compose-redis + compose-elasticsearch + compose-<db> + compose-<mq> | middleware only, **without** the bytedesk app; for local source development |

Note: redis and elasticsearch are core middleware always included; `<db>` defaults to mysql (postgresql/oracle/kingbase optional), `<mq>` defaults to artemis (rabbitmq optional); extra components (freeswitch, obs, minio, ...) stack on top via keywords.

Stop: `./stop.sh [stop|down] [keywords...]` — the action keyword may appear anywhere; pass the same keywords used at start; `down` removes containers but keeps volumes.

## File List

All compose files and their per-component configs (`searxng/`, `grafana/`, `logstash/`, `prometheus.yml`, `ik-plugin-cache/`) live in the [`compose/`](./compose/) folder:

| Compose file (in compose/) | Image | Default ports | Details |
| --- | --- | --- | --- |
| compose/compose-redis.yaml | redis | 16379 | — |
| compose/compose-elasticsearch.yaml | elasticsearch:9.4.2 | 19200/19300 | — |
| compose/compose-mysql.yaml | mysql | 13306 | [Database](./readme/readme.database.md) |
| compose/compose-postgresql.yaml | postgres:17 | 15432 | [Database](./readme/readme.database.md) |
| compose/compose-oracle.yaml | gvenzl/oracle-free:23-slim | 11521 | [Database](./readme/readme.database.md) |
| compose/compose-kingbase.yaml | kingbase V9 | 54321 | [Database](./readme/readme.database.md) |
| compose/compose-artemis.yaml | activemq-artemis:2.40.0 | 16161/18161 | [Message queue](./readme/readme.mq.md) |
| compose/compose-rabbitmq.yaml | rabbitmq:4.2.3 | 5673/15673 | [Message queue](./readme/readme.mq.md) |
| compose/compose-bytedesk.yaml | bytedesk app | 9003/9885 | — |
| compose/compose-freeswitch.yaml | bytedesk/freeswitch | 15060/18021 | [FreeSWITCH](./readme/readme.freeswitch.md) |
| compose/compose-mrcp.yaml | bytedesk/mrcp | 11544 | [Call center](./readme/readme.call.md) |
| compose/compose-coturn.yaml | coturn:4.6.3 | 13478 | [WebRTC](./readme/readme.webrtc.md) |
| compose/compose-janus.yaml | bytedesk/janus | 18089/18188 | [WebRTC](./readme/readme.webrtc.md) |
| compose/compose-searxng.yaml | searxng | 18888 | [SearXNG](./readme/readme.searxng.md) |
| compose/compose-neo4j.yaml | neo4j:5.26.30 | 17474/17687 | [Neo4j](./readme/readme.neo4j.md) |
| compose/compose-logstash.yaml | logstash:9.4.2 | 19600 | [Logstash](./readme/readme.logstash.md) |
| compose/compose-kibana.yaml | kibana:9.4.2 | 15601 | [Kibana](./readme/readme.kibana.md) |
| compose/compose-minio.yaml | minio | 19000/19001 | [MinIO](./readme/readme.minio.md) |
| compose/compose-prometheus.yaml | prometheus | 19090 | [Observability](./readme/readme.observability.md) |
| compose/compose-grafana.yaml | grafana | 13000 | [Observability](./readme/readme.observability.md) |
| compose/compose-zipkin.yaml | zipkin | 19411 | [Observability](./readme/readme.observability.md) |

Other files: `start.sh`/`stop.sh` (compose launcher), `watchdog.sh` (app watchdog, see [watchdog guide](./readme/readme.watchdog.md)), `.env` (secrets), `one/` (all-in-one deployment).

## Manual docker compose

To bypass the scripts, run from the `deploy/docker` directory (`--env-file .env` loads the secrets):

```bash
docker compose --env-file .env -p bytedesk \
  -f compose/compose-redis.yaml -f compose/compose-elasticsearch.yaml \
  -f compose/compose-mysql.yaml -f compose/compose-artemis.yaml \
  -f compose/compose-bytedesk.yaml up -d
```

Note: relative paths inside the compose files (`./searxng`, `../../freeswitch`, ...) resolve against the `compose/` directory; relative values in `.env` (such as `KINGBASE_LICENSE_FILE`, `MRCP_*_DIR`) do as well. Container names, volume names and network names are unchanged, so existing data carries over seamlessly.

## More

Detailed docs live in [readme/](./readme/): [quickstart](./readme/readme.quickstart.md) · [database](./readme/readme.database.md) · [message queue](./readme/readme.mq.md) · [call center](./readme/readme.call.md) · [FreeSWITCH](./readme/readme.freeswitch.md) · [WebRTC](./readme/readme.webrtc.md) · [SearXNG](./readme/readme.searxng.md) · [Neo4j](./readme/readme.neo4j.md) · [Logstash](./readme/readme.logstash.md) · [Kibana](./readme/readme.kibana.md) · [MinIO](./readme/readme.minio.md) · [observability](./readme/readme.observability.md) · [watchdog](./readme/readme.watchdog.md) · [env vars](./readme/readme.env.md) · [migration guide](./readme/readme.migration.md)
