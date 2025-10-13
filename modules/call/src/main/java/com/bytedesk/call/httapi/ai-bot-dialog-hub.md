## FreeSWITCH 200x → Spring Boot 对话中枢（HTTAPI）运行与验证指南

本指南帮助你快速联调已实现的“先易后难、分层解耦”对话中枢：
- 拨打 200 开头号码 → FreeSWITCH 使用 HTTAPI → Spring Boot `/ai-bot` 协调回合
- modules/call 负责与 FreeSWITCH 的录音/播放/状态编排（非实时 MVP）
- modules/ai 提供 LLM、ASR、TTS 的 HTTP 能力（其中 ASR/TTS 为占位实现，可平滑替换为真实服务）

---

## 1. 依赖概览

- FreeSWITCH 已启用 mod_httapi
- Dialplan 已存在路由文件：`deploy/freeswitch/conf/dialplan/default/200-ai-bot.xml`
  - 规则：`^200\d+$` → `httapi` → `http://127.0.0.1:9003/ai-bot?bot_did=${destination_number}`（POST）
- Spring Boot 应用（建议使用 `starter` 聚合启动）已包含以下模块：
  - `modules/call`：`com.bytedesk.call.httapi.AiBotHttapiController` 暴露 `/ai-bot`
  - `modules/ai`：
    - LLM：`/api/v1/chat/completions`（OpenAI 兼容，`RobotController` 已存在）
    - ASR：`/api/v1/asr/transcribe`（占位）
    - TTS：`/api/v1/tts/synthesize`（占位）

---

## 2. 关键配置

### 2.1 Spring Boot（端口、录音与调用）

确保以下属性生效（可放在应用的 `application.properties` 或等效配置源中）：

- `server.port=9003`（需与 dialplan 中的 HTTAPI URL 对齐）
- modules/call（对话编排）
  - `bytedesk.call.ai.useSpeak=true`（可选：使用 `execute speak` 兜底）
  - `bytedesk.call.ai.welcome=/usr/local/freeswitch/sounds/en/us/callie/ivr/ivr-welcome.wav`（可改为自定义欢迎音）
  - `bytedesk.call.ai.recordPath=/usr/local/freeswitch/recordings/ai-bot-${uuid}-${turn}.wav`
  - `bytedesk.call.ai.recordMaxSec=10`
  - `bytedesk.call.ai.silenceSec=2`
  - `bytedesk.ai.service.baseUrl=http://127.0.0.1:9003`（指向本机 `modules/ai` 服务）
- modules/ai（TTS 占位实现输出目录，可被 FreeSWITCH 直接播放）
  - `bytedesk.ai.tts.outputDir=/usr/local/freeswitch/recordings`

注意：`/usr/local/freeswitch/recordings` 路径需 FreeSWITCH 进程可读写。

### 2.2 FreeSWITCH（HTTAPI 权限）

在 `autoload_configs/httapi.conf.xml` 的相应 profile 中，确保允许以下应用（示例）：

```
<configuration name="httapi.conf" description="HTTP API">
  <profiles>
    <profile name="default">
      <param name="enabled" value="true"/>
      <param name="http-methods" value="POST,GET"/>
      <param name="debug" value="1"/>
      <param name="allowed-apps" value="answer,playback,record,execute,hangup,set"/>
    </profile>
  </profiles>
  ...
```

修改后记得 `reloadxml` 并重启相关模块或 FreeSWITCH（视环境而定）。

---

## 3. 启动与拨测

1) 构建并启动 Spring Boot 应用（包含 `modules/call` 与 `modules/ai`），监听 `:9003`
2) 在 FreeSWITCH 控制台执行 `reloadxml`，确认 dialplan 已加载
3) 用内部分机或软电话拨打 200 开头号码（如 2001）

期望流程：
- 第一次进入：`answer` → 播放欢迎音（可选）→ 设置 `httapi_stage=ask`，回轮询
- 询问阶段：`record` 启动录音（最大 10 秒或静音 2 秒结束，带提示音）→ 保存为 `recordPath` 解析后的文件
- 思考阶段：
  - 调 ASR：将上一步录音路径发送到 `/api/v1/asr/transcribe` 获取文本（占位实现可能返回空）
  - 调 LLM：将文本发送到 `/api/v1/chat/completions` 获取回复
  - 调 TTS：将回复转换为音频文件路径（占位实现创建一个空的 wav 文件）
  - `playback` 播放音频，循环进入下一轮（上限 3 轮，超出则 `hangup`）

---

## 4. 常见问题排查

- 无法命中 `/ai-bot`：
  - 检查 Spring Boot 是否监听 9003；访问 `http://127.0.0.1:9003/actuator/health`（若启用）
  - 确认 dialplan 的 HTTAPI URL 与端口匹配
- Unique-ID 缺失：
  - 本实现已兼容从 Header 或表单参数获取；确保 HTTAPI 使用 POST（已在 dialplan 指定）
- 录音失败或无法写入：
  - 确认 `recordPath` 解析后的目录存在且 FreeSWITCH 进程用户有写权限
- 播放失败（文件不存在或无法读取）：
  - 确认 TTS 返回的是 FreeSWITCH 可访问的本地绝对路径；或改用 HTTP URL + mod_http_cache
- 没有声音/回放空白：
  - 占位 TTS 会创建空文件，仅用于打通流程；接入真实 TTS 后恢复
- 网络与媒体：
  - 跨网或公网测试需放行 UDP 16384–32768（RTP）；本地回环联调可先忽略

---

## 5. 替换为真实 ASR/TTS 的建议

- ASR：
  - 本地：Vosk、Whisper.cpp 等（将 `/api/v1/asr/transcribe` 改为实际引擎调用并返回文本）
  - 云端：阿里/讯飞/火山引擎/Google Cloud Speech（注意鉴权与并发限额）
- TTS：
  - 本地：Coqui TTS、Edge-TTS（离线/在线混合）
  - 云端：阿里/讯飞/火山引擎/Azure/Google（返回可直接播放的 URL 或落地到 recordings 目录）

替换位置（Java）：
- `modules/ai/src/main/java/com/bytedesk/ai/voice/AsrRestController.java`
- `modules/ai/src/main/java/com/bytedesk/ai/voice/TtsRestController.java`
- `modules/call/src/main/java/com/bytedesk/call/httapi/client/AiAsrClient.java`
- `modules/call/src/main/java/com/bytedesk/call/httapi/client/AiTtsClient.java`

---

## 6. 验收标准（MVP）

- 拨打 200x 能被 HTTAPI 接入并自动应答
- 能听到欢迎音（如配置）并开始录音，录音文件按 `${uuid}-${turn}.wav` 命名落盘
- ASR/LLM/TTS 全链路调用成功且 `playback` 执行，无异常报错（占位 TTS 的回放可为静音）
- 回合数达到上限自动挂机

---

## 7. 后续方向（可选）

- 实时化：RTP fork / mod_audio_fork 或 ESL 驱动的流式 ASR + 流式 TTS（边听边说）
- 交互增强：DTMF 打断、Barge-in、情绪/敏感词检测、上下文记忆
- 生产化：鉴权、限流、观察性（Trace/Metric/Log）、灰度发布与回滚

---

如需，我可以继续：
- 加入一份示例 `application-ai-bot.properties`
- 接入你指定的 ASR/TTS 厂商 SDK 与鉴权
- 提供单元测试/集成测试骨架，覆盖 ASR/LLM/TTS 正常与异常分支
