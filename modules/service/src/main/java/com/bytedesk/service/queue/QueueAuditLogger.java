package com.bytedesk.service.queue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Emits structured audit logs for queue lifecycle events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueueAuditLogger {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final String EVENT_QUEUE_JOIN = "QUEUE_JOIN";
    private final ObjectMapper objectMapper;

    public void logQueueJoin(QueueMemberEntity member, ThreadEntity thread, QueueEntity queue, QueueTypeEnum queueType) {
        if (!log.isInfoEnabled() || member == null || thread == null) {
            return;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("event", EVENT_QUEUE_JOIN);
        payload.put("timestamp", ISO_FORMATTER.format(ZonedDateTime.now()));
        payload.put("queueType", queueType != null ? queueType.name() : null);
        payload.put("queueUid", queue != null ? queue.getUid() : null);
        payload.put("queueDay", queue != null ? queue.getDay() : null);
        payload.put("queueStatus", queue != null ? queue.getStatus() : null);
        payload.put("queueMemberUid", member.getUid());
        payload.put("queueNumber", member.getQueueNumber());
        payload.put("queueMemberStatus", member.getStatus());
        payload.put("orgUid", member.getOrgUid());
        payload.put("threadUid", thread.getUid());
        payload.put("threadChannel", thread.getChannel());
        payload.put("agentUid", extractUid(thread.getAgentProtobuf()));
        payload.put("workgroupUid", extractUid(thread.getWorkgroupProtobuf()));

        String serialized = serializePayload(payload);
        if (serialized != null) {
            log.info("queue_audit={}", serialized);
        } else {
            log.info("queue_audit {}", payload);
        }
    }

    private String serializePayload(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to serialize queue audit payload", ex);
            return null;
        }
    }

    private String extractUid(UserProtobuf user) {
        return user != null ? user.getUid() : null;
    }
}
