package com.bytedesk.ai.workflow.node;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 事件节点参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventOptions implements Serializable {
    private static final long serialVersionUID = 1L;

    // Timer
    private String timerDefinitionType; // timeDate/timeDuration/timeCycle
    private String timerDefinition;     // e.g. ISO8601, PT5M, cron

    // Message
    private String messageName;

    // Signal
    private String signalName;
}
