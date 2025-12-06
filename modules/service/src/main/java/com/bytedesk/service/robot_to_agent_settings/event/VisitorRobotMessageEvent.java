package com.bytedesk.service.robot_to_agent_settings.event;

import org.springframework.context.ApplicationEvent;

/**
 * 专用于机器人SSE消息的事件，用于后端监听器进行关键词拦截等操作。
 */
public class VisitorRobotMessageEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String messageJson;

    public VisitorRobotMessageEvent(Object source, String messageJson) {
        super(source);
        this.messageJson = messageJson;
    }

    public String getMessageJson() {
        return messageJson;
    }
}
