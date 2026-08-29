# 可观测性 / Observability（Prometheus + Grafana + Zipkin）

组合关键字 `obs` = `prometheus` + `grafana` + `zipkin` 三个组件（一镜像一文件），也可单独使用。

## 快速开始

```bash
# 中间件 + 观测栈
./start.sh middleware obs

# 线上全量 + 观测栈
./start.sh all obs

# 停止（关键字保持一致）
./stop.sh middleware obs down

# 单独启动某个组件（在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-prometheus.yaml up -d
docker compose --env-file .env -f compose/compose-grafana.yaml up -d
docker compose --env-file .env -f compose/compose-zipkin.yaml up -d
```

## 组件说明

| 关键字 | compose 文件（位于 compose/） | 服务名 | 访问地址 |
| --- | --- | --- | --- |
| prometheus | compose/compose-prometheus.yaml | bytedesk-prometheus | <http://127.0.0.1:19090> |
| grafana | compose/compose-grafana.yaml | bytedesk-grafana | <http://127.0.0.1:13000（admin/admin，.env> 可覆盖） |
| zipkin | compose/compose-zipkin.yaml | bytedesk-zipkin | <http://127.0.0.1:19411> |

## 应用侧要求

- 应用需暴露 `/actuator/prometheus`（Prometheus 采集业务指标）
- Zipkin 需设置 `MANAGEMENT_TRACING_ENABLED=true` 才会向 Zipkin 上报 span（默认关闭），可通过 `.env` 添加同名变量覆盖
- Grafana 已预置 `compose/grafana/provisioning` 中的数据源与 AI 观测仪表板（ai-observability.json）

## 说明

- 三个组件相互独立，grafana 不再声明对 prometheus 的 depends_on，均已配置健康检查自愈
- Zipkin 默认内存存储（`STORAGE_TYPE=mem`），重启后 trace 数据不保留
- Zipkin 容器已从 `one/` 下 compose 文件中移除，统一由 `compose/compose-zipkin.yaml` 按需提供，与应用侧 `management.tracing.enabled=false` 的默认策略对齐
