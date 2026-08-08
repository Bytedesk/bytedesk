package com.bytedesk.ai.service.agent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AgentResponseTimingContext {

    private final Map<String, Long> startedAtNanosByReplyUid = new ConcurrentHashMap<>();

    public void start(String replyUid) {
        if (!StringUtils.hasText(replyUid)) {
            return;
        }
        startedAtNanosByReplyUid.put(replyUid, System.nanoTime());
    }

    public long finishMillis(String replyUid) {
        if (!StringUtils.hasText(replyUid)) {
            return 0L;
        }
        Long startedAt = startedAtNanosByReplyUid.remove(replyUid);
        if (startedAt == null) {
            return 0L;
        }
        long elapsedNanos = System.nanoTime() - startedAt;
        return elapsedNanos > 0 ? elapsedNanos / 1_000_000L : 0L;
    }
}
