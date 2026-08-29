# WebRTC 音视频客服 / WebRTC（coturn + janus）

组合关键字 `webrtc` = `coturn` + `janus` 两个组件，也可单独使用。

## 使用示例

```bash
# WebRTC 中间件
./start.sh webrtc middleware

# WebRTC 全栈（含 bytedesk 应用）
./start.sh webrtc all

# 呼叫中心 + WebRTC + 可观测性
./start.sh call webrtc all obs

# 单独启动 coturn
./start.sh middleware coturn

# 停止（关键字保持一致）
./stop.sh webrtc down
```

## 组件说明

| 关键字 | compose 文件（位于 compose/） | 服务名 | 镜像 | 默认端口 |
| --- | --- | --- | --- | --- |
| coturn | compose/compose-coturn.yaml | bytedesk-coturn | coturn/coturn:4.6.3 | 13478(STUN/TURN)、15349(TLS)、49160-49200(UDP 中继) |
| janus | compose/compose-janus.yaml | bytedesk-janus | bytedesk/janus | 18089(HTTP API)、18188(WebSocket)、17188(Admin) |

## TURN 凭据

`.env` 中配置（应用侧与 janus 共用）：

```bash
COTURN_REALM=bytedesk.local
COTURN_USER=bytedesk
COTURN_PASS=bytedesk123
JANUS_ADMIN_SECRET=janusoverlord
```

## 健康检查

```bash
# janus（HTTP API 需 POST 到 /janus，直接 GET 返回 404 属正常）
curl -sS -X POST -H 'Content-Type: application/json' \
  --data '{"janus":"ping","transaction":"check"}' \
  http://localhost:18089/janus

# coturn
docker exec coturn-bytedesk turnutils_stunclient -p 3478 127.0.0.1
```

## 说明

- janus 不再声明对 coturn 的 depends_on（一镜像一文件后避免跨文件依赖），均已配置健康检查与 `restart: always` 自愈
- 应用侧 WebRTC 配置（`BYTEDESK_WEBRTC_JANUS_*`）默认指向 `bytedesk-janus` / `bytedesk-coturn` 服务名，无需额外配置
- janus 配置挂载自 `deploy/janus/etc/janus`
