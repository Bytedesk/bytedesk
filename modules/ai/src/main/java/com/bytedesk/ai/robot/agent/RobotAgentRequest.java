package com.bytedesk.ai.robot.agent;

import lombok.Data;

/**
 * 机器人代理请求参数
 */
@Data
public class RobotAgentRequest {
    
    /**
     * 请求内容
     */
    private String content;
    // 为避免前端添加content字段，这里添加一个question字段
    private String question;
    
    /**
     * 组织ID
     */
    private String orgUid;
} 