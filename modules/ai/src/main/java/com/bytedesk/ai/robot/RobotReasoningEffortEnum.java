package com.bytedesk.ai.robot;

/**
 * 推理努力程度枚举（reasoning effort）。
 * 用于控制大模型在推理/思考阶段投入的算力档位。
 */
public enum RobotReasoningEffortEnum {
    LOW,    // 低
    HIGH,   // 高
    XHIGH,  // 超高
    MAX,    // 最大
}
