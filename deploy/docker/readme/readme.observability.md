# 可观测性 / Observability（Prometheus + Grafana + Zipkin + OTel Collector）

组合关键字 `obs` = `prometheus` + `grafana` + `zipkin` + `otelcol` 四个组件（一镜像一文件），也可单独使用。

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
docker compose --env-file .env -f compose/compose-otelcol.yaml up -d
```

> 注意：`otelcol` 若要转发 trace 到 Zipkin，需与 `zipkin` 在同一 compose 项目中启动（即通过 `./start.sh zipkin otelcol` 或 `obs` 组合），单独 `docker compose -f compose/compose-otelcol.yaml up` 会因网络隔离解析不到 `zipkin-bytedesk`。

## 组件说明

| 关键字 | compose 文件（位于 compose/） | 服务名 | 访问地址 |
| --- | --- | --- | --- |
| prometheus | compose/compose-prometheus.yaml | bytedesk-prometheus | <http://127.0.0.1:19090> |
| grafana | compose/compose-grafana.yaml | bytedesk-grafana | <http://127.0.0.1:13000（admin/admin，.env> 可覆盖） |
| zipkin | compose/compose-zipkin.yaml | bytedesk-zipkin | <http://127.0.0.1:19411> |
| otelcol | compose/compose-otelcol.yaml | bytedesk-otelcol | OTLP HTTP <http://127.0.0.1:14318/v1/traces>、OTLP gRPC `127.0.0.1:14317` |

## 应用侧要求

- 应用需暴露 `/actuator/prometheus`（Prometheus 采集业务指标）
- Grafana 已预置 `compose/grafana/provisioning` 中的数据源与 AI 观测仪表板（ai-observability.json）

## 分布式追踪：两种模式（二选一）

应用侧同时引入了 `spring-boot-starter-zipkin`（Brave）与 `spring-boot-starter-opentelemetry`（OTLP），运行时只能有一种 tracer 生效（共存时 Brave 优先，需显式排除才能切到 OTel）。配置见 `starter/src/main/resources/properties/<profile>/actuator.properties`。

默认策略：**local profile 默认开启 Zipkin/Brave 模式**（便于日常本地调试）；prod/open/noai 等 profile 默认全部关闭（避免后端未启动时产生连接拒绝告警）。均可通过环境变量覆盖。

### 模式一：Zipkin / Brave（默认推荐）

后端直连 Zipkin，span 走 Zipkin v2 HTTP 协议（`/api/v2/spans`）。

```bash
# 1. 启动 Zipkin（或直接用 obs 组合）
./start.sh zipkin

# 2. 启动应用（local profile 已默认开启，无需额外环境变量）
./starter/mvnw -f starter/pom.xml spring-boot:run
# 若临时不需要追踪：MANAGEMENT_TRACING_ENABLED=false ./starter/mvnw ...
```

| 环境变量 | local 默认值 | 其他 profile 默认值 | 说明 |
| --- | --- | --- | --- |
| `MANAGEMENT_TRACING_ENABLED` | **true** | false | 追踪总开关 |
| `MANAGEMENT_ZIPKIN_TRACING_ENABLED` | **true** | false | Zipkin 导出开关 |
| `MANAGEMENT_TRACING_SAMPLING_PROBABILITY` | **1.0** | 0.0 | 采样率（0.0~1.0，本地调试建议 1.0，生产调低） |
| `MANAGEMENT_ZIPKIN_TRACING_ENDPOINT` | <http://127.0.0.1:19411/api/v2/spans> | 同左 | Zipkin 上报地址 |

### 模式二：OpenTelemetry / OTLP（经 OTel Collector）

应用以 OTLP 协议上报到 otelcol，collector 再转发到 Zipkin（UI 查询不变）；也可指向任意 OTLP 兼容后端（Jaeger、Tempo、阿里云 ARMS 等）。

```bash
# 1. 启动 collector（转发依赖 Zipkin，建议一并启动；或使用 obs 组合）
./start.sh zipkin otelcol

# 2. 应用侧开启（关键：排除 Brave 自动配置；local 下 MANAGEMENT_TRACING_ENABLED 已默认 true）
SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.micrometer.tracing.brave.autoconfigure.BraveAutoConfiguration \
MANAGEMENT_OPENTELEMETRY_ENABLED=true \
./starter/mvnw -f starter/pom.xml spring-boot:run
```

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `MANAGEMENT_OPENTELEMETRY_ENABLED` | false | OTel SDK 总开关 |
| `MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT` | <http://127.0.0.1:14318/v1/traces> | OTLP 上报地址（改后端只需改此变量） |
| `MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_TRANSPORT` | http | OTLP 传输方式：`http` 或 `grpc` |
| `MANAGEMENT_OTLP_METRICS_EXPORT_ENABLED` | false | OTLP 指标导出（开启后每分钟向 4318 推指标） |
| `SPRING_AUTOCONFIGURE_EXCLUDE` | （空） | 切 OTel 模式时填 Brave 自动配置类全名 |

> Spring Boot 4.x 注意：旧属性 `management.tracing.enabled` / `management.zipkin.tracing.*` 已废弃（deprecation: error，不生效），新属性为 `management.tracing.export.enabled` / `management.tracing.export.zipkin.*` / `management.opentelemetry.*`。

## 说明

- 四个组件相互独立，均已配置健康检查自愈；grafana 不声明对 prometheus 的 depends_on
- Zipkin 默认内存存储（`STORAGE_TYPE=mem`），重启后 trace 数据不保留；生产环境建议接 elasticsearch/mysql
- otelcol 配置文件为 `compose/otelcol/otelcol-config.yaml`（OTLP 接收 → batch → zipkin exporter），可通过挂载替换以对接其他后端
- prod/open/noai 等非 local profile 追踪默认全关，后端未启动时应用日志干净无连接告警；local profile 默认开启，未启动 Zipkin 时会有 span 上报失败告警，可临时 `MANAGEMENT_TRACING_ENABLED=false` 关闭
