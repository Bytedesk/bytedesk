<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# SearXNG 联网搜索（Web Search）

微语提供独立的联网搜索 compose 文件 `compose/compose-searxng.yaml`（一镜像一文件），包含 [SearXNG](https://github.com/searxng/searxng)（开源元搜索引擎，聚合 Google/Bing/DuckDuckGo 等结果），通过关键字 `searxng` 按需启动，自动接入 `bytedesk-network`。

> **企业版高级功能**：应用侧联网搜索代码位于 `enterprise/ai` 模块（`com.bytedesk.ai.searxng` 包），仅企业版/平台版可用，社区版发布镜像中不包含此功能。

- 官方文档：<https://docs.searxng.org/admin/installation-docker.html>
- 镜像：<https://hub.docker.com/r/searxng/searxng>

## 启动 SearXNG

`start.sh` / `stop.sh` 支持关键字 `searxng`（别名 `search`），用于启停 `compose/compose-searxng.yaml`：

```bash
cd deploy/docker

# 方式 A（推荐）：在启动中间件/应用栈时附带 SearXNG
./start.sh middleware searxng
./start.sh all obs searxng          #   # 线上全量 + 观测栈 + 联网搜索
./stop.sh stop middleware searxng
./stop.sh down middleware searxng

# 方式 B：仅 SearXNG（需先确保 bytedesk-network 存在；在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-searxng.yaml up -d

# 方式 C：与现有 compose 组合
docker compose --env-file .env -f compose/compose-searxng.yaml up -d
```

## 访问地址

| 服务 | 地址 | 说明 |
| --- | --- | --- |
| SearXNG 搜索页面 | <http://127.0.0.1:18888> | 浏览器可直接使用 |
| SearXNG JSON API | <http://127.0.0.1:18888/search?q=bytedesk&format=json> | 供应用调用 |
| SearXNG 健康检查 | <http://127.0.0.1:18888/healthz> | 容器 healthcheck 同款 |

## 启用应用侧联网搜索

SearXNG 启动后，还需在应用侧打开开关（默认关闭，不影响正常启动）：

```bash
# .env 中配置（docker 全量启动时生效，容器内通过容器名访问）
BYTEDESK_AI_SEARXNG_ENABLED=true
BYTEDESK_AI_SEARXNG_BASE_URL=http://searxng-bytedesk:8080

# 或源码本地运行时，在 properties 中配置（宿主机端口）
# bytedesk.ai.searxng.enabled=true
# bytedesk.ai.searxng.base-url=http://127.0.0.1:18888
```

## 测试接口（bytedesk.debug=true 时可用）

```bash
# 1. 服务状态与连通性检查
curl http://127.0.0.1:9003/spring/ai/api/v1/searxng/status

# 2. 联网搜索（返回原始结果）
curl "http://127.0.0.1:9003/spring/ai/api/v1/searxng/search?query=bytedesk&maxResults=5"

# 3. 联网搜索 + 大模型总结回答
curl -X POST http://127.0.0.1:9003/spring/ai/api/v1/searxng/chat \
  -H 'Content-Type: application/json' \
  -d '{"message": "微语bytedesk是什么", "query": "bytedesk 微语", "maxResults": 5}'
```

## 说明

- 自定义配置位于 `compose/searxng/settings.yml`（已开启 JSON 输出格式并关闭 limiter，否则 JSON API 会返回 403）。
- 实例密钥通过 `.env` 中的 `SEARXNG_SECRET` 注入，生产环境务必修改。
- 应用侧相关参数：`bytedesk.ai.searxng.enabled`（开关）、`base-url`、`timeout-ms`、`max-results`、`language`、`categories`、`safe-search`。
- 部分搜索引擎在国内网络可能不可用，可在 `searxng/settings.yml` 中调整 engines。
