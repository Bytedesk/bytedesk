package com.bytedesk.freeswitch.call.event;

/**
 * 通话应答事件
 */
public class CallAnsweredEvent extends CallEvent {
    
    public CallAnsweredEvent(Object source, String uuid) {
        super(source, uuid, CallEventType.CALL_ANSWERED);
    }
}
