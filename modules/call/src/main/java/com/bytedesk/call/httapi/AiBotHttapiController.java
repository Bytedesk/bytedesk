/*
 * @Author: bytedesk.com
 * @Description: HTTAPI 编排控制器（非实时首版）
 *  - 路由自 FreeSWITCH dialplan 200x → httapi → /ai-bot
 *  - 回合流程：answer → playback(欢迎) → record → ASR → LLM → TTS → playback → repeat
 *  - 状态保持：使用 FreeSWITCH channel 变量（httapi_stage, turn 等）在每次回调中恢复
 */
package com.bytedesk.call.httapi;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.call.httapi.client.AiAsrClient;
import com.bytedesk.call.httapi.client.AiLlmClient;
import com.bytedesk.call.httapi.client.AiTtsClient;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@ConditionalOnProperty(prefix = "bytedesk.features.ai-bot", name = "enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class AiBotHttapiController {

    private final AiAsrClient asrClient;
    private final AiLlmClient llmClient;
    private final AiTtsClient ttsClient;

    // 是否使用 FreeSWITCH speak 应用直接 TTS（需要 TTS 引擎，若不可用将回退为 playback URL）
    @Value("${bytedesk.call.ai.useSpeak:true}")
    private boolean useSpeak;

    // 欢迎提示：可用 FS 内置文件或自定义 HTTP URL
    @Value("${bytedesk.call.ai.welcome:/usr/local/freeswitch/sounds/en/us/callie/ivr/8000/ivr-welcome.wav}")
    private String welcomeFile;

    // 录音保存路径模板（放在 FS 本地），支持 {uuid} 与 {turn}（避免 Spring 解析 ${...} 占位符导致启动失败）
    @Value("${bytedesk.call.ai.recordPath:/usr/local/freeswitch/recordings/ai-bot-{uuid}-{turn}.wav}")
    private String recordPathTpl;

    // 最大录音秒数
    @Value("${bytedesk.call.ai.recordMaxSec:10}")
    private int recordMaxSec;

    // 静音自动结束秒数
    @Value("${bytedesk.call.ai.silenceSec:2}")
    private int silenceSec;

    /**
     * FreeSWITCH HTTAPI 回调：Content-Type: application/x-www-form-urlencoded
     * 会包含大量 Channel 变量，关键包括：
     *  - Unique-ID（uuid）
     *  - variable_httapi_stage（自定义流程阶段）
     *  - variable_turn（轮次）
     *  - bot_did（拨入目的号码，来自拨号计划 querystring）
     */
    @PostMapping(value = "/ai-bot", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<?> aiBot(
            @RequestParam(name = "bot_did", required = false) String botDid,
            @RequestParam(name = "variable_httapi_stage", required = false) String stage,
            @RequestParam(name = "variable_turn", required = false) String turnParam,
            @RequestHeader(name = "Unique-ID", required = false) String uniqueId,
            @RequestParam(name = "Unique-ID", required = false) String uniqueIdParam,
            HttpServletResponse response
    ) throws Exception {

        String uuid = StringUtils.hasText(uniqueId) ? uniqueId : (StringUtils.hasText(uniqueIdParam) ? uniqueIdParam : ("no-uuid-" + Instant.now().toEpochMilli()));
        int turn = parseTurn(turnParam);
        String currentStage = (StringUtils.hasText(stage) ? stage : "init");

        log.info("[HTTAPI] uuid={} did={} stage={} turn={} ", uuid, botDid, currentStage, turn);

        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();

        switch (currentStage) {
            case "init":
                // 首次：接听 + 欢迎提示 + 设置下一阶段为 ask 并继续
                out.println(xmlHeader());
                out.println("  <work>");
                out.println("    <answer/>");
                if (StringUtils.hasText(welcomeFile)) {
                    out.printf("    <playback file=\"%s\"/>%n", xmlEscape(welcomeFile));
                }
                out.printf("    <execute application=\"set\" data=\"httapi_stage=ask\"/>%n");
                out.printf("    <execute application=\"set\" data=\"turn=%d\"/>%n", 1);
                out.println("  </work>");
                out.println("</document>");
                return ok();

            case "ask": {
                // 提示录音：录音完成后将再次回调本接口
                int nextTurn = turn > 0 ? turn : 1;
                String recordPath = resolveRecordPath(recordPathTpl, uuid, nextTurn);
                out.println(xmlHeader());
                out.println("  <work>");
                // 可加提示音：beep
                out.println("    <record file=\"" + xmlEscape(recordPath) + "\" max-sec=\"" + recordMaxSec + "\" silence-sec=\"" + silenceSec + "\" beep=\"true\"/>");
                out.printf("    <execute application=\"set\" data=\"last_record_file=%s\"/>%n", xmlEscape(recordPath));
                out.printf("    <execute application=\"set\" data=\"httapi_stage=think\"/>%n");
                out.printf("    <execute application=\"set\" data=\"turn=%d\"/>%n", nextTurn);
                out.println("  </work>");
                out.println("</document>");
                return ok();
            }

            case "think": {
                // 后台：ASR -> LLM -> TTS（生成可播放 URL 或走 speak）
                int nextTurn = turn > 0 ? turn : 1;
                String recordPath = resolveRecordPath(recordPathTpl, uuid, nextTurn);

                String text = safeAsr(recordPath, uuid, nextTurn);
                String reply = safeLlm(text, uuid, nextTurn);

                out.println(xmlHeader());
                out.println("  <work>");
                if (useSpeak) {
                    // 依赖 FS TTS 引擎（如 mod_flite），若无将无声；建议配合回退
                    String speakData = "text=" + xmlEscape(reply);
                    out.printf("    <execute application=\"speak\" data=\"%s\"/>%n", speakData);
                } else {
                    // 生成 TTS 音频 URL/文件并回放
                    String audio = safeTts(reply, uuid, nextTurn);
                    if (StringUtils.hasText(audio)) {
                        out.printf("    <playback file=\"%s\"/>%n", xmlEscape(audio));
                    } else {
                        // 回退：播放提示音
                        out.println("    <execute application=\"tone_stream\" data=\"%(1000, 400, 440)\"/>");
                    }
                }
                // 继续下一轮或结束，这里示例继续最多 3 轮
                if (nextTurn >= 3) {
                    out.println("    <hangup/>");
                } else {
                    out.printf("    <execute application=\"set\" data=\"httapi_stage=ask\"/>%n");
                    out.printf("    <execute application=\"set\" data=\"turn=%d\"/>%n", nextTurn + 1);
                }
                out.println("  </work>");
                out.println("</document>");
                return ok();
            }

            default:
                out.println(xmlHeader());
                out.println("  <work>");
                out.println("    <hangup/>");
                out.println("  </work>");
                out.println("</document>");
                return ok();
        }
    }

    private String xmlHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<document type=\"xml/freeswitch-httapi\">\n" +
               "  <params><param name=\"writable\" value=\"true\"/></params>\n" +
               "  <variables/>";
    }

    private ResponseEntity<?> ok() {
        return ResponseEntity.ok().build();
    }

    private int parseTurn(String turn) {
        try { return StringUtils.hasText(turn) ? Integer.parseInt(turn) : 0; } catch (Exception e) { return 0; }
    }

    private String resolveRecordPath(String tpl, String uuid, int turn) {
        String path = Objects.requireNonNullElse(tpl, "/usr/local/freeswitch/recordings/ai-bot-" + uuid + "-" + turn + ".wav");
        // 同时兼容 ${uuid}/${turn} 与 {uuid}/{turn} 两种模板写法
        return path
            .replace("${uuid}", uuid)
            .replace("${turn}", Integer.toString(turn))
            .replace("{uuid}", uuid)
            .replace("{turn}", Integer.toString(turn));
    }

    private String safeAsr(String recordPath, String uuid, int turn) {
        try {
            return asrClient.transcribe(recordPath, uuid, turn);
        } catch (Exception e) {
            log.warn("ASR failed, uuid={}, turn={}, file={}", uuid, turn, recordPath, e);
            return ""; // 返回空文本，交给 LLM 兜底
        }
    }

    private String safeLlm(String text, String uuid, int turn) {
        try {
            if (!StringUtils.hasText(text)) {
                text = "";
            }
            return llmClient.chat(text, uuid, turn);
        } catch (Exception e) {
            log.warn("LLM failed, uuid={}, turn={} text={}...", uuid, turn, (text.length() > 32 ? text.substring(0, 32) : text), e);
            return "抱歉，当前繁忙，请稍后再试。";
        }
    }

    private String safeTts(String reply, String uuid, int turn) {
        try {
            return ttsClient.synthesize(reply, uuid, turn);
        } catch (Exception e) {
            log.warn("TTS failed, uuid={}, turn={} reply={}...", uuid, turn, (reply.length() > 32 ? reply.substring(0, 32) : reply), e);
            return null;
        }
    }

    private String xmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
