package com.bytedesk.ai.service.agent;

public record AgentCannedResponseMatch(
        String cannedResponseUid,
        String answer) {

    public static AgentCannedResponseMatch empty() {
        return new AgentCannedResponseMatch(null, null);
    }
}
