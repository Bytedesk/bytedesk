package com.bytedesk.freeswitch.call.event;

import lombok.Getter;

/**
 * 通话挂断事件
 */
@Getter
public class CallHangupEvent extends CallEvent {
    
    private final String hangupCause;
    
    public CallHangupEvent(Object source, String uuid, String hangupCause) {
        super(source, uuid, CallEventType.CALL_HANGUP);
        this.hangupCause = hangupCause;
    }
}
