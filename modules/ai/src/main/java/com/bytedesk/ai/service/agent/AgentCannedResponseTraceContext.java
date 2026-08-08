package com.bytedesk.ai.service.agent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AgentCannedResponseTraceContext {

    private final Map<String, String> cannedResponseUidByReplyUid = new ConcurrentHashMap<>();

    public void store(String replyUid, String cannedResponseUid) {
        if (!StringUtils.hasText(replyUid) || !StringUtils.hasText(cannedResponseUid)) {
            return;
        }
        cannedResponseUidByReplyUid.put(replyUid, cannedResponseUid);
    }

    public String consume(String replyUid) {
        if (!StringUtils.hasText(replyUid)) {
            return null;
        }
        return cannedResponseUidByReplyUid.remove(replyUid);
    }
}
