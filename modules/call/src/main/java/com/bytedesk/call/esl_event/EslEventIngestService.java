package com.bytedesk.call.esl_event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import com.bytedesk.call.esl.client.transport.event.EslEvent;
import com.bytedesk.core.uid.UidUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EslEventIngestService {

    private static final int DATABASE_SAFE_PAYLOAD_LENGTH = 255;

    private final EslEventRepository eslEventRepository;
    private final UidUtils uidUtils;
    private final ObjectMapper objectMapper;
    private final EslEventIngestProperties ingestProperties;

    @Async("eslEventTaskExecutor")
    public void ingest(EslEvent eslEvent) {
        if (!shouldIngest(eslEvent)) {
            return;
        }
        try {
            Map<String, String> headers = eslEvent.getEventHeaders() == null ? Collections.emptyMap() : eslEvent.getEventHeaders();
            List<String> bodyLines = eslEvent.getEventBodyLines() == null ? Collections.emptyList() : eslEvent.getEventBodyLines();

            EslEventEntity entity = EslEventEntity.builder()
                    .uid(uidUtils.getUid())
                    .name(buildName(eslEvent.getEventName(), eslEvent.getEventSubclass()))
                    .eventName(eslEvent.getEventName())
                    .eventSubclass(eslEvent.getEventSubclass())
                    .uniqueId(headers.get("Unique-ID"))
                    .callerNumber(firstNonBlank(headers,
                            "Caller-Caller-ID-Number",
                            "Caller-Caller-ID-Name",
                            "from-user"))
                    .destinationNumber(firstNonBlank(headers,
                            "Caller-Destination-Number",
                            "to-user"))
                    .hangupCause(firstNonBlank(headers,
                            "Hangup-Cause",
                            "hangup_cause"))
                    .contact(headers.get("contact"))
                    .status(headers.get("status"))
                    .apiCommand(headers.get("API-Command"))
                    .apiArgument(headers.get("API-Command-Argument"))
                        .headersJson(limitLength(toJson(headers)))
                        .bodyJson(limitLength(toJson(bodyLines)))
                    .type(EslEventTypeEnum.fromEventName(eslEvent.getEventName()).name())
                    .description(firstNonBlank(headers,
                            "Event-Calling-Function",
                            "Event-Name"))
                    .build();

            saveEntity(entity);
        } catch (Exception e) {
            log.warn("写入EslEventEntity失败: {}", e.getMessage());
        }
    }

    private boolean shouldIngest(EslEvent eslEvent) {
        if (!ingestProperties.isEnabled()) {
            return false;
        }

        double sampleRate = ingestProperties.getSampleRate();
        if (sampleRate <= 0d) {
            return false;
        }
        if (sampleRate < 1d && ThreadLocalRandom.current().nextDouble() > sampleRate) {
            return false;
        }

        String eventSubclass = eslEvent.getEventSubclass();
        if (StringUtils.hasText(eventSubclass) && containsIgnoreCase(ingestProperties.getIgnoreSubclasses(), eventSubclass)) {
            return false;
        }

        Map<String, String> headers = eslEvent.getEventHeaders();
        if (headers != null) {
            String apiCommand = headers.get("API-Command");
            if (StringUtils.hasText(apiCommand) && containsIgnoreCase(ingestProperties.getIgnoreApiCommands(), apiCommand)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsIgnoreCase(List<String> list, String value) {
        if (list == null || list.isEmpty() || !StringUtils.hasText(value)) {
            return false;
        }
        String normalized = value.trim();
        for (String item : list) {
            if (StringUtils.hasText(item) && normalized.equalsIgnoreCase(item.trim())) {
                return true;
            }
        }
        return false;
    }

    private String limitLength(String value) {
        if (value == null) {
            return null;
        }
        int maxLen = Math.max(256, ingestProperties.getMaxPayloadLength());
        return value.length() <= maxLen ? value : value.substring(0, maxLen);
    }

    private void saveEntity(EslEventEntity entity) {
        try {
            eslEventRepository.save(entity);
        } catch (Exception ex) {
            if (!isPayloadTooLong(ex)) {
                throw ex;
            }

            entity.setApiArgument(truncateForCurrentSchema(entity.getApiArgument()));
            entity.setHeadersJson(truncateForCurrentSchema(entity.getHeadersJson()));
            entity.setBodyJson(truncateForCurrentSchema(entity.getBodyJson()));
            log.warn("ESL事件载荷超过当前数据库列宽，截断到 {} 字符后重试保存", DATABASE_SAFE_PAYLOAD_LENGTH);
            eslEventRepository.save(entity);
        }
    }

    private boolean isPayloadTooLong(Exception ex) {
        Throwable current = ex;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains("Data too long for column")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String truncateForCurrentSchema(String value) {
        if (value == null || value.length() <= DATABASE_SAFE_PAYLOAD_LENGTH) {
            return value;
        }
        return value.substring(0, DATABASE_SAFE_PAYLOAD_LENGTH);
    }

    private String buildName(String eventName, String eventSubclass) {
        String safeEventName = eventName == null ? "UNKNOWN" : eventName;
        if (eventSubclass == null || eventSubclass.isBlank()) {
            return safeEventName;
        }
        return safeEventName + "#" + eventSubclass;
    }

    private String firstNonBlank(Map<String, String> headers, String... keys) {
        for (String key : keys) {
            String value = headers.get(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.debug("ESL事件序列化失败: {}", e.getMessage());
            return "{}";
        }
    }
}
