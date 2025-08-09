/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 12:39:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 12:30:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.call.event;

import lombok.Getter;

/**
 * DTMF 事件
 */
@Getter
public class CallDtmfEvent extends CallCallEvent {
    
    private final String dtmfDigit;
    
    public CallDtmfEvent(Object source, String uuid, String dtmfDigit) {
        super(source, uuid, CallEventType.DTMF_RECEIVED);
        this.dtmfDigit = dtmfDigit;
    }
}
