# 呼叫中心 / Call Center（FreeSWITCH + MRCP）

组合关键字 `call` = `freeswitch` + `mrcp` 两个组件，也可单独使用。

## 使用示例

```bash
# 呼叫中心中间件（freeswitch + mrcp + redis/es/db/mq）
./start.sh call middleware

# 呼叫中心全栈（含 bytedesk 应用）
./start.sh call all

# 呼叫中心 + WebRTC 全栈
./start.sh call webrtc all

# 单独启动 MRCP（配合已运行的 freeswitch）
./start.sh middleware mrcp

# 停止（关键字保持一致）
./stop.sh call webrtc down
```

## 组件说明

| 关键字 | compose 文件（位于 compose/） | 服务名 | 说明 |
| --- | --- | --- | --- |
| freeswitch | compose/compose-freeswitch.yaml | bytedesk-freeswitch | FreeSWITCH 语音通话服务 |
| mrcp | compose/compose-mrcp.yaml | bytedesk-mrcp | 百度 MRCP Server（ASR/TTS），容器名默认 mrcp-server-bytedesk |

详细 FreeSWITCH 配置（conf 挂载、分机、ESL、录音、网络拓扑参数等）见 [readme.freeswitch.md](./readme/readme.freeswitch.md)。

## 限制

- **仅支持 mysql / postgresql**：`./start.sh call oracle` / `./start.sh call kingbase` 会直接报错
- FreeSWITCH 数据库默认对接 `bytedesk-mysql`（`.env` 中 `FREESWITCH_DB_HOST` 可覆盖为 `bytedesk-postgresql`）
- MRCP Server 指向：`.env` 中 `FREESWITCH_BAIDU_MRCP_SERVER_HOST=bytedesk-mrcp`（服务名，与旧版 `bytedesk-mrcp-server` 不同）
- Apple Silicon 本地运行 MRCP 使用 amd64 镜像（`MRCP_PLATFORM=linux/amd64` 由 Docker Desktop 模拟）
