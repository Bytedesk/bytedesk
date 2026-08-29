# 消息队列切换 / Message Queue

支持 2 种消息队列，通过 start.sh 关键字选择（二选一，默认 `artemis`）：`artemis`、`rabbitmq`。

## 对应 compose 文件

| 关键字 | compose 文件（位于 compose/） | 镜像 | 默认宿主机端口 |
| --- | --- | --- | --- |
| artemis | compose/compose-artemis.yaml | apache/activemq-artemis:2.40.0 | 16161(JMS) / 18161(控制台) |
| rabbitmq | compose/compose-rabbitmq.yaml | rabbitmq:4.2.3-management | 5673(AMQP) / 15673(管理后台) |

## 使用示例

```bash
# RabbitMQ 中间件
./start.sh rabbitmq middleware

# PostgreSQL + RabbitMQ 全栈
./start.sh postgresql rabbitmq all

# 停止（关键字保持一致）
./stop.sh rabbitmq middleware down
```

## 控制台 / Console

- Artemis Web Console: <http://127.0.0.1:18161/console> （`ARTEMIS_USER` / `ARTEMIS_PASSWORD`，默认 admin/admin）
- RabbitMQ Management: <http://127.0.0.1:15673> （`RABBITMQ_DEFAULT_USER` / `RABBITMQ_DEFAULT_PASS`，默认 admin/admin）

## 应用侧连接注入

bytedesk 应用的 MQ 连接由 `start.sh` 按所选 MQ 自动注入并写入 `.env.app`：

- **artemis**：`BYTEDESK_MQ_TYPE=artemis`、`SPRING_ARTEMIS_MODE=embedded`、`SPRING_ARTEMIS_BROKER_URL=tcp://bytedesk-artemis:61616`、`SPRING_ARTEMIS_USER/PASSWORD`、`SPRING_JMS_CACHE_ENABLED=true`、`BYTEDESK_MQ_RABBITMQ_ENABLED=false`
- **rabbitmq**：`BYTEDESK_MQ_TYPE=rabbitmq`、`BYTEDESK_MQ_RABBITMQ_ENABLED=true`、`SPRING_RABBITMQ_HOST=bytedesk-rabbitmq`、`SPRING_RABBITMQ_PORT/USERNAME/PASSWORD/VIRTUAL_HOST/CONNECTION_TIMEOUT`、`SPRING_ARTEMIS_MODE=embedded`

在 `.env` 中显式配置同名变量可覆盖注入值（优先级更高），详见 [readme.env.md](./readme.env.md)。
