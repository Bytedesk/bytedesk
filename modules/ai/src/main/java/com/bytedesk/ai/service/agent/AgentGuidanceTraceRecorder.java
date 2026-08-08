package com.bytedesk.ai.service.agent;

import com.bytedesk.core.message.MessageProtobuf;

public interface AgentGuidanceTraceRecorder {

    void recordPromptBuild(AgentGuidancePromptRequest request, AgentGuidancePromptResolution resolution,
            String fullPrompt);

    void recordReplyPersistence(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            boolean isUnanswered, long latencyMs, long promptTokens, long completionTokens, long totalTokens, String fullPrompt,
            String aiProvider, String aiModel, String cannedResponseUid);
}
