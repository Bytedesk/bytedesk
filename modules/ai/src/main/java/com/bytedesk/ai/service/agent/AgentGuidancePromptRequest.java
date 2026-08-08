package com.bytedesk.ai.service.agent;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.message.MessageProtobuf;

public record AgentGuidancePromptRequest(
        String query,
        String context,
        RobotProtobuf robot,
        MessageProtobuf messageProtobufQuery,
        boolean streaming) {
}
