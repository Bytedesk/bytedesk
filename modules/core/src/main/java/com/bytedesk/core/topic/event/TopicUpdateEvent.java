/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-04 11:15:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 11:16:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic.event;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TopicUpdateEvent extends ApplicationEvent {

    private String topic;

    private String userUid;

    public TopicUpdateEvent(Object source, String topic, String userUid) {
        super(source);
        this.topic = topic;
        this.userUid = userUid;
    }
}
