package com.bytedesk.call.call.event;

import lombok.Getter;

/**
 * 通话挂断事件
 */
@Getter
public class CallCallHangupEvent extends CallCallEvent {
    
    private final String hangupCause;
    
    public CallCallHangupEvent(Object source, String uuid, String hangupCause) {
        super(source, uuid, CallEventType.CALL_HANGUP);
        this.hangupCause = hangupCause;
    }
}
