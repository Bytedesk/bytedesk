<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Neo4j 知识图谱（Knowledge Graph）

微语提供独立的图数据库 compose 文件 `compose/compose-neo4j.yaml`（一镜像一文件），包含 [Neo4j](https://github.com/neo4j/neo4j) 社区版（原生图数据库，用于知识图谱 / GraphRAG 能力验证），通过关键字 `neo4j` 按需启动，自动接入 `bytedesk-network`。

> **企业版高级功能**：应用侧知识图谱代码位于 `enterprise/ai` 模块（`com.bytedesk.ai.neo4j` 包），仅企业版/平台版可用，社区版发布镜像中不包含此功能。

- 官方文档：<https://neo4j.com/docs/operations-manual/current/docker/introduction/>
- 镜像：<https://hub.docker.com/_/neo4j>

## 启动 Neo4j

`start.sh` / `stop.sh` 支持关键字 `neo4j`，用于启停 `compose/compose-neo4j.yaml`：

```bash
cd deploy/docker

# 方式 A（推荐）：在启动中间件/应用栈时附带 Neo4j
./start.sh middleware neo4j
./start.sh all neo4j                # 全量栈 + 图数据库
./stop.sh stop middleware neo4j
./stop.sh down middleware neo4j     # 删除容器，数据卷保留

# 方式 B：仅 Neo4j（需先确保 bytedesk-network 存在；在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-neo4j.yaml up -d
```

## 访问地址

| 服务 | 地址 | 说明 |
| --- | --- | --- |
| Neo4j Browser | <http://127.0.0.1:17474> | 浏览器可视化与管理 |
| Bolt 连接 | <code>bolt://127.0.0.1:17687</code> | 源码本地运行时应用使用；docker 全量时应用容器内为 `bolt://neo4j-bytedesk:7687` |

默认账号 `neo4j` / `${NEO4J_PASSWORD}`（`.env` 中配置，默认 `bytedesk-neo4j`）。

## 密码说明（重要）

- `NEO4J_AUTH` 中的初始密码**仅对空数据卷首次启动生效**；复用已有 `neo4j-data` 卷时，修改 `.env` 中的 `NEO4J_PASSWORD` **不会**覆盖库内旧密码
- 若在 Browser 中已修改过密码，需同步更新 `.env` 的 `NEO4J_PASSWORD` 与应用侧 `BYTEDESK_AI_NEO4J_PASSWORD`，否则应用连接会认证失败
- 需要彻底重置密码时，先 `./stop.sh down middleware neo4j` 再手动删除 `neo4j-data` 数据卷（`docker volume rm bytedesk_neo4j-data`），或按 Neo4j 官方方式在库内改密

## 启用应用侧知识图谱

Neo4j 启动后，还需在应用侧打开开关（默认关闭，不影响正常启动）：

```bash
# .env 中配置（docker 全量启动时生效，容器内通过容器名访问）
BYTEDESK_AI_NEO4J_ENABLED=true
BYTEDESK_AI_NEO4J_URI=bolt://neo4j-bytedesk:7687
BYTEDESK_AI_NEO4J_PASSWORD=<与 NEO4J_PASSWORD 保持一致>

# 或源码本地运行时，在 properties 中配置（宿主机端口）
# bytedesk.ai.neo4j.enabled=true
# bytedesk.ai.neo4j.uri=bolt://127.0.0.1:17687
```

## 测试接口（bytedesk.debug=true 时可用）

```bash
# 1. 服务状态与连通性检查
curl http://127.0.0.1:9003/spring/ai/api/v1/neo4j/status

# 2. 写入演示知识图谱（幂等，:BytedeskDemo 标签隔离，模拟 分类-[:CONTAINS]->FAQ、FAQ-[:RELATED_TO]->FAQ、FAQ-[:MENTIONS]->产品实体）
curl -X POST http://127.0.0.1:9003/spring/ai/api/v1/neo4j/seed-demo

# 3. 读取演示图谱节点与关系
curl "http://127.0.0.1:9003/spring/ai/api/v1/neo4j/graph-demo?limit=100"

# 4. 清理演示图谱数据
curl -X POST http://127.0.0.1:9003/spring/ai/api/v1/neo4j/clear-demo
```

## 说明

- 镜像固定 `neo4j:5.26.30`（5.x LTS 社区版），不使用 `latest`，避免大版本漂移
- 应用侧相关参数：`bytedesk.ai.neo4j.enabled`（开关）、`uri`、`username`、`password`、`database`、`connect-timeout-ms`、`max-connection-pool-size`
- 开关关闭（默认）时应用不触发任何连库行为，不影响启动；演示数据统一打 `:BytedeskDemo {demo:true}` 标签，与业务数据完全隔离
- 本阶段仅做连通性与演示验证，GraphRAG 检索增强等业务能力见 `docs/plans/` 知识图谱业务规划
