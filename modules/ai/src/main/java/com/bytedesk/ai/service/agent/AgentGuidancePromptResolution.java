package com.bytedesk.ai.service.agent;

import java.util.List;

public record AgentGuidancePromptResolution(
        String systemPrompt,
        List<String> matchedGuidelineUids) {

    public static AgentGuidancePromptResolution empty() {
        return new AgentGuidancePromptResolution(null, List.of());
    }
}
