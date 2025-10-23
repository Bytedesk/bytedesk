package com.bytedesk.call.httapi;

import java.net.InetSocketAddress;
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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HttapiController {

    private final LlmClient llm;

    // Accept GET and POST and be tolerant about Content-Type so FreeSWITCH requests
    // that don't set exact Content-Type still hit this handler.
    @RequestMapping(value = "/ai-bot", method = {RequestMethod.POST, RequestMethod.GET}, produces = "text/xml;charset=UTF-8")
    public @ResponseBody byte[] aiBot(@RequestParam(required = false) MultiValueMap<String, String> form,
                                      HttpServletRequest request) {
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
        boolean probe = Boolean.parseBoolean(System.getenv().getOrDefault("HTTAPI_MRCP_PROBE", "false"));
        String mrcpHost = System.getenv().getOrDefault("HTTAPI_MRCP_HOST", "127.0.0.1");
        int mrcpPort = parseIntOrDefault(System.getenv().get("HTTAPI_MRCP_PORT"), 8060);
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
            log.info("HTTAPI /ai-bot turn={} mode='{}' bot_did='{}' mrcpReady={} (probe={}) remote={} xff='{}' proto='{}' host='{}' ua='{}' ct='{}' qs='{}' paramKeys={}",
                    turn, modeReq, botDid, mrcpReady, probe, remote, xff, xfp, xfh, truncate(ua, 120), ct, qs, vars.keySet());
            // Key recognition variables snapshot (shortened)
            String recog = Optional.ofNullable(vars.get("RECOG_RESULT")).orElse(vars.getOrDefault("variable_RECOG_RESULT", ""));
            String dsrt = Optional.ofNullable(vars.get("detect_speech_result_text")).orElse(vars.getOrDefault("variable_detect_speech_result_text", ""));
            log.info("HTTAPI vars RECOG_RESULT='{}' detect_speech_result_text='{}'", truncate(recog, 200), truncate(dsrt, 200));
        } catch (Exception ignore) {
        }

        if ("1".equals(turn)) {
            return firstTurn(vars /* no longer gate by mrcpReady */);
        }
        return secondTurn(vars /* no longer gate by mrcpReady */);
    }

    private byte[] firstTurn(Map<String, String> vars) {
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

    private byte[] secondTurn(Map<String, String> vars) {
        HttapiXml x = new HttapiXml();
        String userText = pickFirstNonEmpty(vars,
                "RECOG_RESULT","detect_speech_result_text","speech_detection_result",
                "bot_user_text","asr_text",
                "variable_RECOG_RESULT","variable_detect_speech_result_text","variable_speech_detection_result",
                "variable_bot_user_text","variable_asr_text");
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

    private static boolean containsExitIntent(String text) {
        if (text == null) return false;
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
        if (s == null || s.isBlank()) return def;
        try { return Integer.parseInt(s.trim()); } catch (Exception ignore) { return def; }
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
        } catch (Exception ignore) {}
        return m;
    }

    private static String pickFirstNonEmpty(Map<String, String> vars, String... keys) {
        for (String k : keys) {
            String v = vars.get(k);
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }
}
