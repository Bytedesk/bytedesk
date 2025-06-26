/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 12:31:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 21:44:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.webrtc.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.freeswitch.webrtc.FreeSwitchWebRTCEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FreeSwitchWebRTCDeleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private FreeSwitchWebRTCEntity webrtc;

    public FreeSwitchWebRTCDeleteEvent(FreeSwitchWebRTCEntity webrtc) {
        super(webrtc);
        this.webrtc = webrtc;
    }
}
