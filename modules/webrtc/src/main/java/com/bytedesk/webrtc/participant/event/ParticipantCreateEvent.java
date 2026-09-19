/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:59:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 10:00:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.webrtc.participant.event;

import com.bytedesk.webrtc.participant.ParticipantEntity;

/**
 * Event published when a new participant is created.
 */
public class ParticipantCreateEvent extends AbstractParticipantEvent {

    private static final long serialVersionUID = 1L;

    public ParticipantCreateEvent(ParticipantEntity participant) {
        super(participant, participant);
    }
}
