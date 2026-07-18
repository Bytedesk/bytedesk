# 大模型 AI 语音机器人（拨 2000）设计说明

## 目标

- 当任何用户在 default 上下文拨打 2000 时，进入 AI 机器人应答流程。
- 初版：接听并播放欢迎提示，留出后续扩展的对话循环接口；呼叫结束时挂断。
- 迭代：集成实时 ASR（语音转文字）、LLM（对话）、TTS（语音合成），实现连续多轮对话。

## 架构选型

- 路由入口：FreeSWITCH Dialplan（XML），目的号码 2000。
- 呼叫控制：mod_httapi（HTTP Telephony API）。
  - 拨号计划调用 `httapi`，由本地 HTTP 服务返回 HTTAPI XML 指令。
  - 好处：呼叫逻辑在应用层，易于快速迭代、灰度和观测。
- 语音 IO：
  - 初版使用播放提示（playback）+ 挂断。
  - 完整版可用以下之一：
    - DTMF/收号驱动交互（HTTAPI 的 bind + playback）。
    - 录音 + 轮询识别（HTTAPI <record/> 上传，再调用 ASR）。
    - 实时媒体：
      - 使用 mod_rtc / mod_verto / mod_sofia + 外部媒体桥接到 WebSocket/UDP，驱动流式 ASR/TTS。
      - 或使用外部媒体服务器（如 SignalWire/Janus/rtpengine）桥接。

## 调用流程（2000）

1. 终端注册（alice/bob/1001/1002 等）→ user_context=default。
2. 拨 2000 → 命中 `conf/dialplan/default/200-ai-bot.xml`。
3. 执行 `<action application="httapi" data="{url=http://127.0.0.1:18080/ai-bot,method=POST}"/>`。
4. HTTAPI 服务（本地脚本）返回 XML：

- 初版：`answer` → 可选 `playback` 提示 → `hangup`。
- 循环：HTTAPI 每执行完一批 `<work>` 会再次请求，直到 `<hangup/>` 或 `<break/>`。

## HTTAPI 返回格式要点

- Content-Type 必须是 `text/xml`。
- 文档根：`<document type="xml/freeswitch-httapi">`，包含可选 `<params>` `<variables>` 和必需 `<work>`。
- `<work>` 内支持：`playback`、`pause`、`speak`、`say`、`record`、`execute`（应用调用）、`dial`、`hangup`、`break` 等。
- 权限：`httapi.conf.xml` 的 profile 要允许所需动作（如 `execute-apps` 内允许 `answer`、`playback`）。

## 组件与文件

- Dialplan：`conf/dialplan/default/200-ai-bot.xml`（匹配 2000 → httapi）。
- HTTAPI 配置：`conf/autoload_configs/httapi.conf.xml`（允许 answer/playback）。
- 应用服务（示例）：`scripts/ai_bot/server.py`（纯标准库 HTTP 服务器，返回 HTTAPI XML）。
- 文档：本文件与 `scripts/ai_bot/README.md`。

## 环境变量/配置

- 服务监听：`0.0.0.0:18080`（开发环境），路径 `/ai-bot`。
- 可在 dialplan 的 `httapi` data 中改为其他 URL。
- 生产建议：
  - 使用 Unix 内网地址、HTTPS、鉴权/白名单。
  - 反向代理（nginx）限流、TLS 终止。

## 开发步骤

1. 启用并配置 mod_httapi（确认模块存在且加载，调整权限允许运行 `answer`、`playback`）。
2. 新增拨号计划 `200-ai-bot.xml`，匹配号码 2000，路由至 `httapi`。
3. 编写 HTTAPI 示例服务：

- 初版：返回 `answer → playback(欢迎语) → hangup`。
- 预留：下一次请求识别用户输入、调用 LLM、合成 TTS。

4. 重新加载配置并验证：

- 在 FreeSWITCH 控制台：`fs_cli -x reloadxml`
- 启动 HTTAPI 服务：`python3 /usr/local/freeswitch/scripts/ai_bot/server.py`
- 通过分机拨 2000，观察是否接听并播放提示，然后挂断；在 `fs_cli` 查看 `mod_httapi` 日志。

## 未来演进（AI 能力）

- ASR：接入云厂商或开源（Vosk/Whisper/faster-whisper），实时/非实时两种模式。
- LLM：HTTP API（OpenAI/自建 vLLM/LLama.cpp），维护对话状态与摘要。
- TTS：云 TTS 或本地引擎（eSpeak, Coqui TTS, Piper，或 FreeSWITCH TTS 插件）。
- 实时：
  - 通过外部媒体（mod_rtc + WebSocket Gateway）实现双工流式音频，用 LLM Realtime API 实现自然对话。
- 健壮性：超时、重试、降级（改为 IVR 菜单或留言）、回落到人工坐席。
- 监控：
  - FreeSWITCH CDR、channel vars、HTTAPI 服务访问日志、时延分布。
  - 关键指标：ASR 成功率、轮次、平均响应时延、用户满意度。

## 测试与运维

- 测试：
  - 本地注册两个分机，互拨后拨 2000。
  - 观察 `log/freeswitch.log`、`fs_cli` 中 HTTAPI 请求日志。
- 运维：
  - HTTAPI 服务以 systemd/pm2/supervisor 管理。
  - 声音文件缓存（mod_http_cache 或本地化音频）。
  - 配置版本化与灰度（新建 `210-ai-bot-v2.xml`，逐步切换）。

## 我打算将 asr、llm、tts 等在另外一个服务 中使用 spring boot 框架搭建，是否可行？

  可行，而且是常见落地方案；建议按“先易后难、分层解耦”的思路把 Spring Boot 服务做成对话中枢，FreeSWITCH 只负责媒体采集/播放与会话驱动。

## 两种主线路架构

- 方案A：非实时（最快上线）
  - 流程：FS 通过 HTTAPI 驱动交互 → 每轮“说话”用 <record> 录音到 WAV → Spring Boot 接口接收音频做 ASR → 调 LLM → 调 TTS 生成 WAV/HTTP 地址 → HTTAPI 返回下一步指令 playback 该音频。
  - 依赖与对接：
    - FreeSWITCH：mod_httapi（已启用）、mod_sndfile（WAV 播放）、mod_curl/HTTP 文件可选（若要回放 HTTP 音频，推荐 mod_http_cache 或将 TTS 产物落到可访问的 HTTP 存储）。
    - Spring Boot：提供 REST 接口（上传录音/返回文本、TTS 产物 URL）。
  - 优点：实现简单、易调试；缺点：交互有回合延迟（录音-识别-合成）。

- 方案B：实时双工（更自然）
  - 流程：话路媒体实时流式送入 Spring Boot；ASR/LLM 在线推理，TTS 流式回放。
  - 对接方式（任选其一）：
    - WebSocket 音频分流：安装 mod_audio_fork（或等价 WS/UDP 音频 fork 模块），将 16k PCM/Opus 流推送到 Spring Boot WebSocket；服务端回推 TTS 切片，FS 端以广播/插入方式实时播放。
    - MRCP 路线：用 UniMRCP（mod_unimrcp）对接第三方 ASR/TTS，Spring Boot 做编排与业务（非必须自己算子）。
    - SIP/媒体网关：通过外部媒体服务器（Janus/rtpengine/SignalWire Realtime）桥接到 Spring Boot 的 WS/gRPC。
  - 优点：低时延、体验好；缺点：工程复杂度与模块依赖更高。

## Spring Boot 服务职责和接口“契约”

- 统一会话编排
  - 输入：会话 id（FS uuid/sip_call_id）、来电号码、轮次、音频切片/录音 URL、用户文本（如 DTMF）。
  - 输出：本轮回复（文本）、TTS 音频（URL 或流）、下一步策略（继续/挂断/转人工）。
- ASR
  - 非实时：POST /asr transcribe，body=音频URL/上传；返回 text、时间戳。
  - 实时：WS /asr-stream，客户端发送 PCM/Opus 帧，服务端持续推送部分转写与最终转写。
- LLM
  - POST /llm/chat，输入对话上下文（对话 id、角色消息、工具调用结果），返回回复文本与函数调用意图（可选）。
- TTS
  - 非实时：POST /tts synthesize，输入 text、voice、format=wav 16k；返回音频URL。
  - 实时：WS /tts-stream，输入 TTS 文本片段，回推音频帧。
- 观测
  - 所有接口接受 trace-id 与 FS uuid；返回时附带 RT、token 用量（如有）、错误码。

建议媒体参数

- 采样：16 kHz、单声道、PCM s16le（或 Opus 16k）
- 容器：WAV（便于 FS 直接 playback）
- 时延目标：非实时 0.8–1.5s/轮；实时端到端 < 300ms

## 与 FreeSWITCH 的集成点

- 你当前已用 HTTAPI 驱动（拨 200x → /ai-bot），完全可以把 Spring Boot 路由放在 HTTAPI 背后：
  - 版本1（非实时）：HTTAPI 返回 work 序列：record → HTTP 回调给 Spring Boot → playback TTS URL → 循环/挂断。
  - 如需回放 HTTP 音频：启用 mod_http_cache 或将文件先落本地再 playback。
- 实时优化可选：
  - 安装/启用 mod_audio_fork（WebSocket 分流），由 Spring Boot 消费流式音频；TTS 回放可通过 uuid_broadcast 注入已合成的短片段（200–500ms 切片），或用支持回传的双工插件。
  - 也可考虑 UniMRCP 方案，Spring Boot 只做业务与策略，ASR/TTS 交给 MRCP 服务。

### Qwen-Audio-Realtime 电话接入状态

- 9205 当前是已验证的 HTTAPI 回合制兜底：record → 后端调用 Qwen-Audio-Realtime → TTS WAV playback → 继续循环。它会持续到客户挂断，但不是实时双工媒体。
- 真正实时双工的后端入口已预留：`/visitor/api/v1/call/voice-agent/qwen-realtime/media`。
  - 下行客户端到 Spring Boot：WebSocket binary frame，16 kHz、mono、PCM s16le。
  - 上行 Spring Boot 到客户端：Qwen `response.audio.delta` 解码后的 binary PCM 帧，同时用 text frame 转发 Qwen 事件。
  - 可选 text 控制帧：`{"type":"commit"}` 提交当前输入并请求响应，`{"type":"response.create"}` 单独请求响应，`{"type":"end"}` 关闭桥。
- 当前 `freeswitch-bytedesk` 容器只加载了 `mod_event_socket`、`mod_httapi`、`mod_sofia`、`mod_unimrcp` 等模块，未加载 `mod_audio_fork` / `mod_audio_stream`。因此不能只靠拨号计划完成电话媒体实时双工；需要先在 FreeSWITCH 镜像中安装/启用一个支持双向 WebSocket 音频的模块，再把该模块连接到上述后端 WebSocket。
- 如果选用只支持单向 fork 的模块，TTS 回灌还需要通过 ESL/`uuid_broadcast` 注入短音频片段；如果选用支持双向 audio stream 的模块，应直接消费后端返回的 binary PCM 帧。

9205 已接入一个默认关闭的实时媒体桥分支：

- 默认：`qwen_realtime_media_bridge_enabled=false`，9205 继续走 HTTAPI 回合制兜底。
- 启用：安装并加载 `mod_audio_stream` 后，将 `qwen_realtime_media_bridge_enabled=true`，或在 Docker 环境中设置 `FREESWITCH_QWEN_REALTIME_MEDIA_BRIDGE_ENABLED=true`。
- WebSocket：默认 `qwen_realtime_media_ws_url` 指向 Spring Boot 的 `/visitor/api/v1/call/voice-agent/qwen-realtime/media`。电话桥接专用端点会在后端默认切换为 `mod_audio_stream` 输出并关闭事件回传。
- 拨号计划：9205 实时分支通过 `api_on_answer=uuid_audio_stream ${uuid} start ... mono 16k ...` 启动媒体桥，然后 `park` 保持通话直到客户挂断。
- 后端返回给 `mod_audio_stream` 的播放帧格式为 `{"type":"streamAudio","data":{"audioDataType":"raw","sampleRate":24000,"audioData":"...base64..."}}`。

## 安全与可靠性

- 认证：HTTAPI 服务器与 Spring Boot 互相鉴权（JWT/TLS 双向），限制来源 IP。
- 重试与降级：ASR/TTS 失败→改 IVR 菜单/回放提示；LLM 超时→返回静态兜底文案。
- 音频缓存：短音频 CDN/本地缓存，避免重复下载；设置缓存控制。
- 监控：以 FS uuid 关联 CDR（你已启用 mod_odbc_cdr），在 Spring Boot 侧记录每轮 ASR/LLM/TTS 时延与错误。

## 最小可行落地路径（建议）

1) 延续现状（HTTAPI 编排）

- Spring Boot 暴露：
  - POST /asr（收录音 URL）
  - POST /tts（回 URL）
  - POST /ai-bot（返回 HTTAPI XML 或业务 JSON，由现有 HTTAPI 服务做翻译）
- HTTAPI 流程：answer → record（5–10s 或端点检测）→ 调 ASR/LLM/TTS → playback(TTS URL) → repeat
- 先做到业务通顺、可观测，后续再引入实时流。

1) 准备实时能力（并行推进）

- 评估/部署 mod_audio_fork 或 UniMRCP；在 Spring Boot 增加 WS/gRPC 流处理通道。
- 把“首句识别”和“打断播放（barge-in）”作为第一批实时特性。

结论：用 Spring Boot 搭 ASR/LLM/TTS 服务完全可行，且与你当前基于 HTTAPI 的编排天然契合；建议先走非实时录音-识别-合成的闭环快速上线，随后迭代接入流式通道以优化时延与体验。如果你定了第一版接口形态，我可以直接更新 HTTAPI 服务与拨号计划、并给出 Spring Boot 控制器示例骨架。
