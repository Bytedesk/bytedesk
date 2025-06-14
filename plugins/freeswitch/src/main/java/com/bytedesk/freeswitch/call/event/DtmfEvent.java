package com.bytedesk.freeswitch.call.event;

import lombok.Getter;

/**
 * DTMF 事件
 */
@Getter
public class DtmfEvent extends CallEvent {
    
    private final String dtmfDigit;
    
    public DtmfEvent(Object source, String uuid, String dtmfDigit) {
        super(source, uuid, CallEventType.DTMF_RECEIVED);
        this.dtmfDigit = dtmfDigit;
    }
}
