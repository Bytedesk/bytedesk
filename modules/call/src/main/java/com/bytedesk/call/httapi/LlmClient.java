package com.bytedesk.call.httapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class LlmClient {
    
    private final ObjectMapper om = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    // 优先读取 application.properties，其次回退环境变量，最后回显
    @Value("${spring.ai.zhipuai.api-key}")
    private String zhipuApiKey;
    @Value("${ai.zhipu.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String zhipuBaseUrl;
    @Value("${ai.zhipu.model:glm-4.5-flash}")
    private String zhipuModel;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;
    @Value("${ai.openai.base-url:https://api.openai.com/v1}")
    private String openaiBaseUrl;
    @Value("${ai.openai.model:gpt-4o-mini}")
    private String openaiModel;

    public String chat(String prompt, String systemPrompt) throws Exception {
        String apiKey = firstNonBlank(zhipuApiKey, getenv("ZHIPU_API_KEY"));
        String baseUrl = firstNonBlank(zhipuBaseUrl, getenvOr("ZHIPU_BASE_URL", "https://open.bigmodel.cn/api/paas/v4"));
        String model = firstNonBlank(zhipuModel, getenvOr("ZHIPU_MODEL", "glm-4.5-flash"));

        // 若未配置智谱，则回退到 OpenAI 兼容端点（优先配置文件，再环境变量）
        if (isBlank(apiKey)) {
            apiKey = firstNonBlank(openaiApiKey, getenv("OPENAI_API_KEY"));
            baseUrl = firstNonBlank(openaiBaseUrl, getenvOr("OPENAI_BASE_URL", "https://api.openai.com/v1"));
            model = firstNonBlank(openaiModel, getenvOr("OPENAI_MODEL", "gpt-4o-mini"));
        }

        if (isBlank(apiKey)) {
            // echo fallback（无密钥时不外呼）
            return prompt;
        }

        String normalizedBase = normalizeBaseUrl(baseUrl);
        String url = normalizedBase + "/chat/completions";
        String payload = "{" +
                "\"model\":\"" + escapeJson(model) + "\"," +
                "\"messages\":[{" +
                "\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\"},{" +
                "\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}]," +
                "\"temperature\":0.3," +
                "\"max_tokens\":256}";

        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() / 100 != 2) {
            log.warn("LLM HTTP {}: {}", resp.statusCode(), resp.body());
            return "抱歉，我暂时没有合适的答案。";
        }
        JsonNode root = om.readTree(resp.body());
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            JsonNode c0 = choices.get(0);
            String content = c0.path("message").path("content").asText(null);
            if (content != null && !content.isBlank()) return content.trim();
        }
        return "抱歉，我暂时没有合适的答案。";
    }

    private static String firstNonBlank(String a, String b) {
        return !isBlank(a) ? a : (!isBlank(b) ? b : "");
    }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String getenv(String k) { return System.getenv(k); }
    private static String getenvOr(String k, String d) {
        String v = System.getenv(k);
        return (v == null || v.isBlank()) ? d : v;
    }

    private static String normalizeBaseUrl(String baseUrl) {
        String b = baseUrl.replaceAll("/*$", "");
        // 若为智谱域且未包含 /v4，自动补齐（兼容配置值为 /api/paas 或 /api/paas/v4）
        if (b.contains("open.bigmodel.cn") && !b.endsWith("/v4")) {
            b = b + "/v4";
        }
        return b;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
