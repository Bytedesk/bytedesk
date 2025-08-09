package com.bytedesk.call.call.event;

/**
 * 通话应答事件
 */
public class FreeSwitchCallAnsweredEvent extends FreeSwitchCallEvent {
    
    public FreeSwitchCallAnsweredEvent(Object source, String uuid) {
        super(source, uuid, CallEventType.CALL_ANSWERED);
    }
}
