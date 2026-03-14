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

            eslEventRepository.save(entity);
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
