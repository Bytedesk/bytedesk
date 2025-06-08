package com.bytedesk.freeswitch.callcenter.event;

import lombok.Getter;

/**
 * 通话开始事件
 */
@Getter
public class CallStartEvent extends CallEvent {
    
    private final String callerId;
    private final String destination;
    
    public CallStartEvent(Object source, String uuid, String callerId, String destination) {
        super(source, uuid, CallEventType.CALL_START);
        this.callerId = callerId;
        this.destination = destination;
    }
}
