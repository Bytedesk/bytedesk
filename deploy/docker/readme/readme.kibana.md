<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Kibana 日志查询（Kibana Log Viewer）

## 中文说明

Kibana（`compose/compose-kibana.yaml`，一镜像一文件）默认不随主栈启动，通过 `start.sh` 关键字或 compose 组合启用。

```bash
# 方式 A（推荐）：启动中间件/应用栈时附带 Kibana（通常与 logstash 一起使用）
./start.sh all logstash kibana
./stop.sh stop all logstash kibana
./stop.sh down all logstash kibana

# 方式 B：仅 Kibana（需先确保 bytedesk-network 存在；在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-kibana.yaml up -d

# 方式 C：与现有 compose 组合
docker compose --env-file .env -f compose/compose-elasticsearch.yaml -f compose/compose-kibana.yaml up -d bytedesk-kibana

# 查看 Kibana 运行状态
docker compose --env-file .env -f compose/compose-kibana.yaml ps
docker compose --env-file .env -f compose/compose-kibana.yaml logs -f
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

Token 失效排查：若 Kibana 日志出现 `security_exception: unable to authenticate` 或启动卡在 unavailable，说明 `.env` 中的 token 已过期/失效，按下述步骤轮换（ES 9.x 注意：实际密钥在响应的 `token.value` 字段，不是 `token`）:

```bash
# 1. 删除旧 token（同名 token 不能直接覆盖，会报 version conflict）
curl -u elastic:${ELASTIC_PASSWORD} -X DELETE "http://127.0.0.1:19200/_security/service/elastic/kibana/credential/token/bytedesk-kibana"

# 2. 重新生成并把返回的 token.value 写入 .env 的 KIBANA_SERVICE_ACCOUNT_TOKEN
curl -u elastic:${ELASTIC_PASSWORD} -X PUT "http://127.0.0.1:19200/_security/service/elastic/kibana/credential/token/bytedesk-kibana"

# 3. 重建 kibana 容器
./start.sh middleware kibana
```

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

## English

Kibana runs in its own file `compose/compose-kibana.yaml` (one image per file) and does not start with the default stack; enable it via the `kibana` keyword or compose composition.

```bash
# Option A (recommended): start the stack with Kibana attached (usually together with logstash)
./start.sh all logstash kibana
./stop.sh stop all logstash kibana
./stop.sh down all logstash kibana

# Option B: Kibana only (requires bytedesk-network to exist first; run from deploy/docker)
docker compose --env-file .env -f compose/compose-kibana.yaml up -d

# Option C: combine with existing compose files
docker compose --env-file .env -f compose/compose-elasticsearch.yaml -f compose/compose-kibana.yaml up -d bytedesk-kibana

# Inspect Kibana status and logs
docker compose --env-file .env -f compose/compose-kibana.yaml ps
docker compose --env-file .env -f compose/compose-kibana.yaml logs -f
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

Kibana itself connects to Elasticsearch through `KIBANA_SERVICE_ACCOUNT_TOKEN` in `.env`, so it does not need the superuser backend configuration.

Token troubleshooting: if Kibana logs show `security_exception: unable to authenticate` or startup stays at unavailable, the token in `.env` has gone stale. Rotate it as follows (ES 9.x note: the actual secret is in the `token.value` field of the response, not `token`):

```bash
# 1. Delete the old token (a same-name token cannot be overwritten directly; it returns version conflict)
curl -u elastic:${ELASTIC_PASSWORD} -X DELETE "http://127.0.0.1:19200/_security/service/elastic/kibana/credential/token/bytedesk-kibana"

# 2. Recreate it and put the returned token.value into KIBANA_SERVICE_ACCOUNT_TOKEN in .env
curl -u elastic:${ELASTIC_PASSWORD} -X PUT "http://127.0.0.1:19200/_security/service/elastic/kibana/credential/token/bytedesk-kibana"

# 3. Recreate the kibana container
./start.sh middleware kibana
```

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
