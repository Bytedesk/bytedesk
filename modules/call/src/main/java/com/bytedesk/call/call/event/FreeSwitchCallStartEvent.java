/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 12:38:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 12:29:59
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
 * 通话开始事件
 */
@Getter
public class FreeSwitchCallStartEvent extends FreeSwitchCallEvent {
    
    private final String callerId;
    private final String destination;
    
    public FreeSwitchCallStartEvent(Object source, String uuid, String callerId, String destination) {
        super(source, uuid, CallEventType.CALL_START);
        this.callerId = callerId;
        this.destination = destination;
    }
}
