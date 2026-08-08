package com.bytedesk.ai.service.agent;

public interface AgentCannedResponseResolver {

    AgentCannedResponseMatch resolve(AgentCannedResponseRequest request);
}
