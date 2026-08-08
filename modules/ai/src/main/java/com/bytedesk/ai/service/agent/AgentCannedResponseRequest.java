package com.bytedesk.ai.service.agent;

import java.util.Map;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.message.MessageProtobuf;

public record AgentCannedResponseRequest(
        String query,
        RobotProtobuf robot,
        MessageProtobuf messageProtobufQuery,
        Map<String, String> evidenceFields,
        boolean streaming,
        String defaultAnswer) {
}
