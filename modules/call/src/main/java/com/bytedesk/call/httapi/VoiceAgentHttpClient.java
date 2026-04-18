package com.bytedesk.call.httapi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class VoiceAgentHttpClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VoiceAgentChatResult chat(String appBaseUrl, String fileUrl, String conversationId, String prompt) {
        String requestBody = "{" +
                jsonField("fileUrl", fileUrl) + "," +
                jsonField("conversationId", conversationId) + "," +
                jsonField("prompt", prompt) +
                "}";
        JsonNode data = post(appBaseUrl, "/visitor/api/v1/call/voice-agent/turn", requestBody);
        return new VoiceAgentChatResult(
                textValue(data, "conversationId"),
                textValue(data, "transcript"),
                textValue(data, "replyText"),
                textValue(data, "replyAudioUrl"));
    }

    public VoiceAgentSpeakResult speak(String appBaseUrl, String text) {
        String requestBody = "{" + jsonField("text", text) + "}";
        JsonNode data = post(appBaseUrl, "/visitor/api/v1/call/voice-agent/speak", requestBody);
        return new VoiceAgentSpeakResult(textValue(data, "replyText"), textValue(data, "replyAudioUrl"));
    }

    private JsonNode post(String appBaseUrl, String path, String requestBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizeBaseUrl(appBaseUrl) + path))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("voice agent http status=" + response.statusCode());
            }
            JsonNode root = objectMapper.readTree(response.body());
            int code = root.path("code").asInt(500);
            if (code != 200) {
                throw new IllegalStateException("voice agent business code=" + code + ", message=" + root.path("message").asText());
            }
            JsonNode data = root.path("data");
            if (data.isMissingNode() || data.isNull()) {
                throw new IllegalStateException("voice agent response data is empty");
            }
            return data;
        } catch (IOException e) {
            throw new IllegalStateException("voice agent response parse failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("voice agent request interrupted", e);
        }
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalArgumentException("appBaseUrl is required");
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private static String textValue(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText(null);
    }

    private static String jsonField(String key, String value) {
        String escaped = value == null ? "" : value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        return "\"" + key + "\":\"" + escaped + "\"";
    }

    public record VoiceAgentChatResult(String conversationId, String transcript, String replyText, String replyAudioUrl) {
    }

    public record VoiceAgentSpeakResult(String replyText, String replyAudioUrl) {
    }
}