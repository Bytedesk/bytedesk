package com.bytedesk.call.call.event;

/**
 * 通话应答事件
 */
public class CallCallAnsweredEvent extends CallCallEvent {
    
    public CallCallAnsweredEvent(Object source, String uuid) {
        super(source, uuid, CallEventType.CALL_ANSWERED);
    }
}
