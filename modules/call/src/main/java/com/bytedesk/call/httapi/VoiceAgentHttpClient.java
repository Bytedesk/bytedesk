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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VoiceAgentHttpClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VoiceAgentChatResult chat(String appBaseUrl, String fileUrl, String conversationId, String callUuid, String prompt) {
        String requestBody = "{" +
                jsonField("fileUrl", fileUrl) + "," +
                jsonField("conversationId", conversationId) + "," +
                jsonField("callUuid", callUuid) + "," +
                jsonField("prompt", prompt) +
                "}";
        JsonNode data = post(appBaseUrl, "/visitor/api/v1/call/voice-agent/turn", requestBody);
        return new VoiceAgentChatResult(
                textValue(data, "conversationId"),
                textValue(data, "transcript"),
                textValue(data, "replyText"),
                textValue(data, "replyAudioUrl"),
                textValue(data, "nextActionType"),
                textValue(data, "queueName"),
                textValue(data, "queueUid"),
                textValue(data, "leaveReason"),
                textValue(data, "promptText"),
                integerValue(data, "maxRecordSeconds"),
                integerValue(data, "ringTimeoutSeconds"),
                textValue(data, "ivrMenuUid"),
                textValue(data, "ivrExtensionNumber"));
    }

    public VoiceAgentChatResult chat(String appBaseUrl,
            String fileUrl,
            String conversationId,
            String callUuid,
            String prompt,
            String orgUid,
            String did,
            String provider,
            String instructions,
            String realtimeModel,
            String realtimeVoice,
            String ttsModel,
            String ttsVoice) {
        String requestBody = "{" +
                jsonField("fileUrl", fileUrl) + "," +
                jsonField("conversationId", conversationId) + "," +
            jsonField("callUuid", callUuid) + "," +
                jsonField("prompt", prompt) + "," +
                jsonField("orgUid", orgUid) + "," +
                jsonField("did", did) + "," +
                jsonField("provider", provider) + "," +
                jsonField("instructions", instructions) + "," +
                jsonField("realtimeModel", realtimeModel) + "," +
                jsonField("realtimeVoice", realtimeVoice) + "," +
                jsonField("ttsModel", ttsModel) + "," +
                jsonField("ttsVoice", ttsVoice) +
                "}";
        JsonNode data = post(appBaseUrl, "/visitor/api/v1/call/voice-agent/turn", requestBody);
        return new VoiceAgentChatResult(
                textValue(data, "conversationId"),
                textValue(data, "transcript"),
                textValue(data, "replyText"),
                textValue(data, "replyAudioUrl"),
                textValue(data, "nextActionType"),
                textValue(data, "queueName"),
                textValue(data, "queueUid"),
                textValue(data, "leaveReason"),
                textValue(data, "promptText"),
                integerValue(data, "maxRecordSeconds"),
                integerValue(data, "ringTimeoutSeconds"),
                textValue(data, "ivrMenuUid"),
                textValue(data, "ivrExtensionNumber"));
    }

    public VoiceAgentSpeakResult speak(String appBaseUrl, String text) {
        String requestBody = "{" + jsonField("text", text) + "}";
        JsonNode data = post(appBaseUrl, "/visitor/api/v1/call/voice-agent/speak", requestBody);
        return new VoiceAgentSpeakResult(textValue(data, "replyText"), textValue(data, "replyAudioUrl"));
    }

    public VoiceAgentWelcomeResult welcome(String appBaseUrl, String orgUid, String did) {
        String requestBody = "{" +
                jsonField("orgUid", orgUid) + "," +
                jsonField("did", did) +
                "}";
        JsonNode data = post(appBaseUrl, "/visitor/api/v1/call/voice-agent/welcome", requestBody);
        return new VoiceAgentWelcomeResult(
                textValue(data, "welcomeType"),
                textValue(data, "welcomeText"),
                textValue(data, "welcomeAudioUrl"));
    }

    private JsonNode post(String appBaseUrl, String path, String requestBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizeBaseUrl(appBaseUrl) + path))
                    .timeout(Duration.ofSeconds(60))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("voice agent http status=" + response.statusCode());
            }
            JsonNode root = objectMapper.readTree(response.body());
            int code = root.path("code").asInt(500);
            if (code != 200) {
                throw new IllegalStateException(
                        "voice agent business code=" + code + ", message=" + root.path("message").asText());
            }
            JsonNode data = root.path("data");
            if (data.isMissingNode() || data.isNull()) {
                throw new IllegalStateException("voice agent response data is empty");
            }
            return data;
        } catch (IOException e) {
            log.warn("voice agent request failed path={} baseUrl={} error={}", path, appBaseUrl, e.toString());
            throw new IllegalStateException("voice agent request failed: " + e.getMessage(), e);
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

    private static Integer integerValue(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }

    private static String jsonField(String key, String value) {
        String escaped = value == null ? ""
                : value
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r");
        return "\"" + key + "\":\"" + escaped + "\"";
    }

    public record VoiceAgentChatResult(
            String conversationId,
            String transcript,
            String replyText,
            String replyAudioUrl,
            String nextActionType,
            String queueName,
            String queueUid,
            String leaveReason,
            String promptText,
            Integer maxRecordSeconds,
            Integer ringTimeoutSeconds,
            String ivrMenuUid,
            String ivrExtensionNumber) {
    }

    public record VoiceAgentSpeakResult(String replyText, String replyAudioUrl) {
    }

    public record VoiceAgentWelcomeResult(String welcomeType, String welcomeText, String welcomeAudioUrl) {
    }
}
