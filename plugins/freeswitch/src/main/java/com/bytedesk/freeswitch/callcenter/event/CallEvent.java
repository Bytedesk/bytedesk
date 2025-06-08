package com.bytedesk.freeswitch.callcenter.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 通话事件基类
 */
@Getter
public abstract class CallEvent extends ApplicationEvent {
    
    private final String uuid;
    private final CallEventType eventType;
    
    public CallEvent(Object source, String uuid, CallEventType eventType) {
        super(source);
        this.uuid = uuid;
        this.eventType = eventType;
    }
    
    public enum CallEventType {
        CALL_START,
        CALL_ANSWERED,
        CALL_HANGUP,
        DTMF_RECEIVED
    }
}
