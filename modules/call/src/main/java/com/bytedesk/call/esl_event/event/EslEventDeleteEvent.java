/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 12:31:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 12:31:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.esl_event.event;

import com.bytedesk.call.esl_event.EslEventEntity;

/**
 * Event published when a esl_event is deleted.
 */
public class EslEventDeleteEvent extends AbstractEslEventEvent {

    private static final long serialVersionUID = 1L;

    public EslEventDeleteEvent(EslEventEntity esl_event) {
        super(esl_event, esl_event);
    }
}
