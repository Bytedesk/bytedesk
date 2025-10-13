package com.bytedesk.call.httapi.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiLlmClient {

    private final RestTemplate restTemplate;

    @Value("${bytedesk.ai.service.baseUrl:http://127.0.0.1:9003}")
    private String aiBaseUrl;

    /**
     * 简化：调用 OpenAI 兼容 chat/completions 接口（modules/ai/RobotController）
     */
    @SuppressWarnings("unchecked")
    public String chat(String userText, String uuid, int turn) {
        String url = aiBaseUrl + "/api/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "bytedesk-ai");
        body.put("stream", false);
        body.put("messages", List.of(
            Map.of("role", "system", "content", "你是电话语音助手，回答要简洁明了。"),
            Map.of("role", "user", "content", userText == null ? "" : userText)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        Map<String, Object> resp = restTemplate.postForObject(url, req, Map.class);
        if (resp == null) return "";
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> first = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) first.get("message");
                Object content = message.get("content");
                return content != null ? content.toString() : "";
            }
        } catch (Exception ignored) {}
        return "";
    }
}
