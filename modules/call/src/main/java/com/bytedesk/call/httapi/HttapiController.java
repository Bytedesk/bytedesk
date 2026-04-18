package com.bytedesk.call.httapi;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.call.config.CallConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HttapiController {

    private final LlmClient llm;
    private final VoiceAgentHttpClient voiceAgentHttpClient;

    // Accept GET and POST and be tolerant about Content-Type so FreeSWITCH requests
    // that don't set exact Content-Type still hit this handler.
    @RequestMapping(value = "/ai-bot", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/xml;charset=UTF-8")
    public @ResponseBody byte[] aiBot(@RequestParam(required = false) MultiValueMap<String, String> form, HttpServletRequest request) {
        Map<String, String> vars = (form != null && !form.isEmpty()) ? flatten(form) : new HashMap<>();
        // If form params were empty, try to read the raw body and parse a query-string style body
        if (vars.isEmpty()) {
            String body = readRequestBody(request);
            if (body != null && !body.isBlank()) {
                // If body looks like key=val&... parse it
                if (body.contains("=") && body.contains("&")) {
                    Map<String, String> parsed = parseQueryString(body);
                    vars.putAll(parsed);
                }
            }
        }
        String turn = vars.getOrDefault("turn", vars.getOrDefault("variable_turn", "1"));
        // MRCP 连通性探测：默认关闭（避免分离部署/容器环境中误判）。
        // 可通过环境变量 HTTAPI_MRCP_PROBE=true 与 HTTAPI_MRCP_HOST/PORT 开启。
        boolean probe = Boolean.parseBoolean(System.getenv().getOrDefault(CallConstants.ENV_HTTAPI_MRCP_PROBE, "false"));
        String mrcpHost = System.getenv().getOrDefault(CallConstants.ENV_HTTAPI_MRCP_HOST, CallConstants.LOOPBACK_IPV4);
        int mrcpPort = parseIntOrDefault(System.getenv().get(CallConstants.ENV_HTTAPI_MRCP_PORT), CallConstants.DEFAULT_HTTAPI_MRCP_PORT);
        boolean mrcpReady = probe && mrcpUp(mrcpHost, mrcpPort, 300);

        // -- Incoming request trace for troubleshooting no-audio/hangup on 9201
        try {
            String botDid = Optional.ofNullable(vars.get("bot_did")).orElse(vars.getOrDefault("variable_bot_did", ""));
            String modeReq = Optional.ofNullable(vars.get("mode")).orElse(vars.getOrDefault("variable_mode", ""));
            String remote = safe(request.getRemoteAddr()) + ":" + request.getRemotePort();
            String xff = safe(request.getHeader("X-Forwarded-For"));
            String xfp = safe(request.getHeader("X-Forwarded-Proto"));
            String xfh = safe(request.getHeader("X-Forwarded-Host"));
            String ua = safe(request.getHeader("User-Agent"));
            String ct = safe(request.getContentType());
            String qs = safe(request.getQueryString());
            log.info(
                    "HTTAPI /ai-bot turn={} mode='{}' bot_did='{}' mrcpReady={} (probe={}) remote={} xff='{}' proto='{}' host='{}' ua='{}' ct='{}' qs='{}' paramKeys={}",
                    turn, modeReq, botDid, mrcpReady, probe, remote, xff, xfp, xfh, truncate(ua, 120), ct, qs,
                    vars.keySet());
            // Key recognition variables snapshot (shortened)
            String recog = Optional.ofNullable(vars.get("RECOG_RESULT"))
                    .orElse(vars.getOrDefault("variable_RECOG_RESULT", ""));
            String dsrt = Optional.ofNullable(vars.get("detect_speech_result_text"))
                    .orElse(vars.getOrDefault("variable_detect_speech_result_text", ""));
            log.info("HTTAPI vars RECOG_RESULT='{}' detect_speech_result_text='{}'", truncate(recog, 200),
                    truncate(dsrt, 200));
        } catch (Exception ignore) {
        }

        if ("1".equals(turn)) {
            return firstTurn(vars, request);
        }
        return secondTurn(vars, request);
    }

    private byte[] firstTurn(Map<String, String> vars, HttpServletRequest request) {
        if (useVoiceAgent(vars)) {
            return firstTurnVoiceAgent(vars, request);
        }

        HttapiXml x = new HttapiXml();
        log.info("HTTAPI firstTurn (no MRCP gating)");
        // 读取可选参数：setup（仅下发变量，不直接播报）、greet/greet_ssml（覆盖默认问候）
        String setup = Optional.ofNullable(vars.get("setup"))
                .orElse(vars.getOrDefault("variable_setup", ""))
                .trim().toLowerCase(Locale.ROOT);
        String customSsml = Optional.ofNullable(vars.get("greet_ssml"))
                .orElse(vars.getOrDefault("variable_greet_ssml", ""));
        String customText = Optional.ofNullable(vars.get("greet"))
                .orElse(vars.getOrDefault("variable_greet", ""));

        String greetText = customText != null && !customText.isBlank()
                ? customText.trim()
                : "您好，我是微语智能助手，请问您有什么问题？";
        String greetSsml = (customSsml != null && !customSsml.isBlank())
                ? customSsml
                : "<speak version='1.0' xml:lang='zh-CN'><p>" + HttapiXml.xmlEscape(greetText) + "</p></speak>";

        // 下发变量给拨号计划，以便在本地使用 speak 播报（避免 HTTAPI 返回与后续步骤的竞态）
        x.execute("export", "greet_ssml=" + greetSsml);
        x.execute("export", "greet_done=1");
        x.execute("set", "bot_state=awaiting_user");

        // 若未指定 setup=1/true，则在 HTTAPI 侧播报一遍（供通用入口使用）。
        // 注意：不再做“MRCP 不可达”嘟声兜底，以免与拨号计划/代理时序打架。
        boolean doSpeakHere = !("1".equals(setup) || "true".equals(setup));
        if (doSpeakHere) {
            x.execute("answer", null);
            x.execute("set", "tts_engine=unimrcp");
            x.execute("set", "tts_profile=baidu");
            x.execute("set", "unimrcp:profile=baidu");
            x.execute("set", "synth-content-type=application/ssml+xml");
            x.execute("set", "unimrcp:header:Speech-Language=zh-CN");
            x.speakSsml("unimrcp", greetSsml);
        }

        x.breakTag();
        return x.build().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] firstTurnVoiceAgent(Map<String, String> vars, HttpServletRequest request) {
        HttapiXml x = new HttapiXml();
        String greetText = Optional.ofNullable(vars.get("greet"))
                .orElse(vars.getOrDefault("variable_greet", "您好，我是微语智能助手，请问您有什么可以帮您？"));
        try {
            VoiceAgentHttpClient.VoiceAgentSpeakResult speakResult = voiceAgentHttpClient
                    .speak(resolveAppBaseUrl(request), greetText);
            if (hasText(speakResult.replyAudioUrl())) {
                x.execute("playback", normalizePlaybackUrl(speakResult.replyAudioUrl(), request));
            } else {
                x.execute("playback", "tone_stream://%(300,1000,440);loops=1");
            }
        } catch (Exception ex) {
            log.warn("voice-agent firstTurn speak failed: {}", ex.toString());
            x.execute("playback", "tone_stream://%(300,1000,440);loops=1");
        }
        x.execute("export", "bot_continue=1");
        x.breakTag();
        return x.build().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] secondTurn(Map<String, String> vars, HttpServletRequest request) {
        if (useVoiceAgent(vars) || hasText(pickFirstNonEmpty(vars,
                "file_url", "turn_record_url", "record_url",
                "variable_file_url", "variable_turn_record_url", "variable_record_url"))) {
            return secondTurnVoiceAgent(vars, request);
        }

        HttapiXml x = new HttapiXml();
        String userText = pickFirstNonEmpty(vars,
                "RECOG_RESULT", "detect_speech_result_text", "speech_detection_result",
                "bot_user_text", "asr_text",
                "variable_RECOG_RESULT", "variable_detect_speech_result_text", "variable_speech_detection_result",
                "variable_bot_user_text", "variable_asr_text");
        if (userText == null || userText.isBlank()) {
            // try NLSML if present
            String nlsml = pickFirstNonEmpty(vars, "detect_speech_result", "variable_detect_speech_result");
            userText = HttapiXml.nlsmlToText(nlsml);
        }

        // mode: single (default) | multi | unlimited
        String mode = Optional.ofNullable(vars.get("mode"))
                .orElse(Optional.ofNullable(vars.get("variable_mode")).orElse("single"))
                .trim().toLowerCase(Locale.ROOT);
        boolean exitRequested = containsExitIntent(userText);

        log.info("HTTAPI secondTurn mode='{}' exitIntent={} userText='{}' (no MRCP gating)",
                mode, exitRequested, truncate(userText, 200));

        if (userText == null || userText.isBlank()) {
            x.execute("set", "synth-content-type=application/ssml+xml");
            x.execute("set", "unimrcp:profile=baidu");
            x.execute("set", "unimrcp:header:Speech-Language=zh-CN");
            x.speakSsml("unimrcp", "<speak version='1.0' xml:lang='zh-CN'><p>若未识别任何内容，请靠近话筒再试。</p></speak>");
            // 使用 export 确保变量在 HTTAPI 返回后在会话级可见
            if ("unlimited".equals(mode) || "multi".equals(mode)) {
                x.execute("export", "bot_continue=1");
            } else {
                x.execute("export", "bot_continue=0");
            }
            x.breakTag();
            return x.build().getBytes(StandardCharsets.UTF_8);
        }

        String answer;
        try {
            answer = llm.chat(userText, "你是一个简洁、可靠的中文语音助理。用简短口语化中文回答。");
        } catch (Exception e) {
            log.warn("LLM error: {}", e.toString());
            answer = userText; // echo
        }

        x.execute("set", "synth-content-type=application/ssml+xml");
        x.execute("set", "unimrcp:profile=baidu");
        x.execute("set", "unimrcp:header:Speech-Language=zh-CN");
        String ssml = "<speak version='1.0' xml:lang='zh-CN'><p>" + HttapiXml.xmlEscape(answer) + "</p></speak>";
        x.speakSsml("unimrcp", ssml);
        // 控制循环：unlimited/multi 继续，遇到退出意图则停止；single 默认不继续
        if ("unlimited".equals(mode) || "multi".equals(mode)) {
            x.execute("export", "bot_continue=" + (exitRequested ? "0" : "1"));
        } else {
            x.execute("export", "bot_continue=0");
        }
        x.breakTag();
        return x.build().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] secondTurnVoiceAgent(Map<String, String> vars, HttpServletRequest request) {
        HttapiXml x = new HttapiXml();
        String mode = Optional.ofNullable(vars.get("mode"))
                .orElse(Optional.ofNullable(vars.get("variable_mode")).orElse("single"))
                .trim().toLowerCase(Locale.ROOT);
        String fileUrl = pickFirstNonEmpty(vars,
                "file_url", "turn_record_url", "record_url",
                "variable_file_url", "variable_turn_record_url", "variable_record_url");
        String conversationId = pickFirstNonEmpty(vars,
                "conversation_id", "variable_conversation_id",
                "uuid", "variable_uuid");

        if (!hasText(fileUrl)) {
            return buildVoiceAgentRetryReply(x, mode, request, "我还没有收到本轮录音，请您再说一次。", true);
        }

        try {
            VoiceAgentHttpClient.VoiceAgentChatResult result = voiceAgentHttpClient.chat(
                    resolveAppBaseUrl(request), fileUrl, conversationId, null);
            String transcript = result.transcript();
            boolean exitRequested = containsExitIntent(transcript) || containsExitIntent(result.replyText());
            String audioUrl = result.replyAudioUrl();

            if (!hasText(audioUrl) && hasText(result.replyText())) {
                audioUrl = voiceAgentHttpClient.speak(resolveAppBaseUrl(request), result.replyText()).replyAudioUrl();
            }

            if (hasText(transcript)) {
                x.execute("export", "bot_user_text=" + transcript.trim());
            }
            if (hasText(result.replyText())) {
                x.execute("export", "bot_reply_text=" + result.replyText().trim());
            }
            if (hasText(audioUrl)) {
                x.execute("playback", normalizePlaybackUrl(audioUrl, request));
            } else {
                x.execute("playback", "tone_stream://%(300,1000,440);loops=1");
            }
            x.execute("export", "bot_continue=" + resolveBotContinue(mode, exitRequested));
            x.breakTag();
            return x.build().getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.warn("voice-agent secondTurn failed fileUrl={} : {}", fileUrl, ex.toString());
            return buildVoiceAgentRetryReply(x, mode, request, "我暂时没有听清，请您再说一次。", false);
        }
    }

    private byte[] buildVoiceAgentRetryReply(HttapiXml x, String mode, HttpServletRequest request, String text, boolean missingFile) {
        try {
            String audioUrl = voiceAgentHttpClient.speak(resolveAppBaseUrl(request), text).replyAudioUrl();
            if (hasText(audioUrl)) {
                x.execute("playback", normalizePlaybackUrl(audioUrl, request));
            } else {
                x.execute("playback", "tone_stream://%(300,1000,440);loops=1");
            }
        } catch (Exception ex) {
            log.warn("voice-agent fallback speak failed missingFile={} : {}", missingFile, ex.toString());
            x.execute("playback", "tone_stream://%(300,1000,440);loops=1");
        }
        x.execute("export", "bot_continue=" + resolveBotContinue(mode, false));
        x.breakTag();
        return x.build().getBytes(StandardCharsets.UTF_8);
    }

    private static boolean containsExitIntent(String text) {
        if (text == null)
            return false;
        String t = text.toLowerCase(Locale.ROOT);
        return t.contains("退出") || t.contains("再见") || t.contains("挂断") || t.contains("结束")
                || t.contains("bye") || t.contains("goodbye") || t.contains("exit") || t.contains("quit");
    }

    private static boolean mrcpUp(String host, int port, int timeoutMs) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static int parseIntOrDefault(String s, int def) {
        if (s == null || s.isBlank())
            return def;
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception ignore) {
            return def;
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static Map<String, String> flatten(MultiValueMap<String, String> form) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> e : form.entrySet()) {
            String k = e.getKey();
            List<String> v = e.getValue();
            map.put(k, (v != null && !v.isEmpty()) ? v.get(0) : "");
            // normalize (variable_ prefix and case-insensitive)
            map.putIfAbsent("variable_" + k, map.get(k));
            map.putIfAbsent(k.toUpperCase(Locale.ROOT), map.get(k));
            map.putIfAbsent("variable_" + k.toUpperCase(Locale.ROOT), map.get(k));
        }
        return map;
    }

    private static String readRequestBody(HttpServletRequest request) {
        try (java.io.BufferedReader br = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static Map<String, String> parseQueryString(String qs) {
        Map<String, String> m = new HashMap<>();
        try {
            String[] parts = qs.split("&");
            for (String p : parts) {
                int i = p.indexOf('=');
                if (i > 0) {
                    String k = java.net.URLDecoder.decode(p.substring(0, i), StandardCharsets.UTF_8.name());
                    String v = java.net.URLDecoder.decode(p.substring(i + 1), StandardCharsets.UTF_8.name());
                    m.put(k, v);
                    m.putIfAbsent("variable_" + k, v);
                    m.putIfAbsent(k.toUpperCase(Locale.ROOT), v);
                }
            }
        } catch (Exception ignore) {
        }
        return m;
    }

    private static String pickFirstNonEmpty(Map<String, String> vars, String... keys) {
        for (String k : keys) {
            String v = vars.get(k);
            if (v != null && !v.isBlank())
                return v;
        }
        return null;
    }

    private boolean useVoiceAgent(Map<String, String> vars) {
        String explicit = pickFirstNonEmpty(vars, "voice_agent", "variable_voice_agent");
        if (hasText(explicit)) {
            return "1".equals(explicit) || "true".equalsIgnoreCase(explicit);
        }
        String botDid = pickFirstNonEmpty(vars, "bot_did", "variable_bot_did");
        return "9201".equals(botDid) || "9203".equals(botDid);
    }

    private String resolveAppBaseUrl(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String scheme = hasText(forwardedProto) ? forwardedProto.trim() : request.getScheme();
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        if (hasText(forwardedHost)) {
            return scheme + "://" + forwardedHost.trim();
        }

        int port = request.getServerPort();
        boolean defaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);
        return defaultPort
                ? scheme + "://" + request.getServerName()
                : scheme + "://" + request.getServerName() + ":" + port;
    }

    private String resolveBotContinue(String mode, boolean exitRequested) {
        if ("unlimited".equals(mode) || "multi".equals(mode)) {
            return exitRequested ? "0" : "1";
        }
        return "0";
    }

    private String normalizePlaybackUrl(String audioUrl, HttpServletRequest request) {
        if (!hasText(audioUrl)) {
            return audioUrl;
        }
        try {
            URI uri = URI.create(audioUrl);
            String host = uri.getHost();
            if (host == null) {
                return audioUrl;
            }
            String normalizedUrl = audioUrl;
            if (!"127.0.0.1".equals(host) && !"localhost".equalsIgnoreCase(host) && !"0.0.0.0".equals(host)) {
                normalizedUrl = audioUrl;
            } else {
                String publicBaseUrl = resolveAppBaseUrl(request);
                String path = uri.getRawPath() == null ? "" : uri.getRawPath();
                String query = uri.getRawQuery();
                normalizedUrl = publicBaseUrl + path + (hasText(query) ? "?" + query : "");
            }

            URI normalizedUri = URI.create(normalizedUrl);
            if (requiresShoutPlayback(normalizedUrl)) {
                StringBuilder shout = new StringBuilder("shout://");
                shout.append(normalizedUri.getHost());
                if (normalizedUri.getPort() > 0) {
                    shout.append(":").append(normalizedUri.getPort());
                }
                if (normalizedUri.getRawPath() != null) {
                    shout.append(normalizedUri.getRawPath());
                }
                if (hasText(normalizedUri.getRawQuery())) {
                    shout.append("?").append(normalizedUri.getRawQuery());
                }
                return shout.toString();
            }
            return normalizedUrl;
        } catch (Exception ex) {
            log.warn("normalizePlaybackUrl failed for audioUrl={}: {}", audioUrl, ex.toString());
            return audioUrl;
        }
    }

    static boolean requiresShoutPlayback(String audioUrl) {
        if (audioUrl == null || audioUrl.isBlank()) {
            return false;
        }
        try {
            URI uri = URI.create(audioUrl);
            String path = uri.getPath();
            return path != null && path.toLowerCase(Locale.ROOT).endsWith(".mp3");
        } catch (Exception ex) {
            return audioUrl.toLowerCase(Locale.ROOT).endsWith(".mp3");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String truncate(String s, int max) {
        if (s == null)
            return null;
        if (s.length() <= max)
            return s;
        return s.substring(0, max) + "...";
    }
}
