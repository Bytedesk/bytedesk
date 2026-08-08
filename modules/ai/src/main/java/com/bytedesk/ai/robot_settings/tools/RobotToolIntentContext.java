package com.bytedesk.ai.robot_settings.tools;

import java.io.Serializable;
import java.util.List;

public record RobotToolIntentContext(
        Boolean intentRecognitionEnabled,
        String intentProvider,
        String intentModel,
    String toolProvider,
    String toolModel,
        Integer intentTimeoutMs,
        List<ResolvedRobotToolIntent> tools) implements Serializable {

    public RobotToolIntentContext {
        tools = tools == null ? List.of() : List.copyOf(tools);
    }

    public static RobotToolIntentContext empty() {
        return new RobotToolIntentContext(false, null, null, null, null, null, List.of());
    }
}