<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# FreeSWITCH 媒体模式说明

当前 FreeSWITCH 仅作为媒体通道使用，默认不启用 MySQL/PostgreSQL core-db，也不再根据 mysql/postgresql 场景切换 FreeSWITCH conf。

说明：

- `start.sh` 仍会自动确保 bytedesk 应用数据库存在，即 `MYSQL_DATABASE` / `POSTGRES_DB` / `ORACLE_DATABASE` / `KINGBASE_DATABASE`。
- `call` / `call webrtc` 组合下，FreeSWITCH 不再依赖 `FREESWITCH_DATABASE`、`FS_CORE_DB_MODULE`、`FS_CORE_DB_DSN`。
- 历史文件 `compose-call-db-mysql.yaml`、`compose-call-db-postgresql.yaml` 已删除，启动脚本不会再加载。

## 向 bytedesk_freeswitch 插入测试数据

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

## 常见问题排查

```bash
cd deploy/docker
./stop.sh postgresql call middleware down
./start.sh postgresql call middleware
docker inspect --format 'Status={{.State.Status}} Health={{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}} RestartCount={{.RestartCount}}' freeswitch-bytedesk
docker logs --tail 200 freeswitch-bytedesk 2>&1 | grep -E "NO SUITABLE DATABASE INTERFACE|Cannot load modules" || true
```

- 预期结果：`Health=healthy`，且日志不再出现上述错误关键字。

```bash
docker compose --env-file .env -p bytedesk \
 -f compose/compose-redis.yaml \
 -f compose/compose-elasticsearch.yaml \
 -f compose/compose-postgresql.yaml \
 -f compose/compose-artemis.yaml \
 -f compose/compose-freeswitch.yaml \
 -f compose/compose-mrcp.yaml \
 up -d --force-recreate --remove-orphans bytedesk-postgresql bytedesk-freeswitch
```

1) 验证 FreeSWITCH 是否就绪：

```bash
docker exec freeswitch-bytedesk bash -lc "(echo >/dev/tcp/127.0.0.1/8021) >/dev/null 2>&1 && echo TCP_8021_OPEN || echo TCP_8021_CLOSED"
docker exec freeswitch-bytedesk fs_cli -H 127.0.0.1 -P 8021 -p bytedesk123 -x "status"
```
