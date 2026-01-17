/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 07:51:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 12:30:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member.event;

import com.bytedesk.service.queue_member.QueueMemberEntity;

/**
 * Event published when a new queue member is created.
 */
public class QueueMemberCreateEvent extends AbstractQueueMemberEvent {

    private static final long serialVersionUID = 1L;

    public QueueMemberCreateEvent(QueueMemberEntity member) {
        super(member, member);
    }
}
