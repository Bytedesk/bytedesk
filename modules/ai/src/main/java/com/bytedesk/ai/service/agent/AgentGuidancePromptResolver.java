package com.bytedesk.ai.service.agent;

public interface AgentGuidancePromptResolver {

    AgentGuidancePromptResolution resolve(AgentGuidancePromptRequest request);
}
