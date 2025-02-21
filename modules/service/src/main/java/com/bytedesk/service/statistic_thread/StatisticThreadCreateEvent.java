/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-21 09:32:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-23 16:24:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_thread;

import org.springframework.context.ApplicationEvent;
import com.bytedesk.core.thread.ThreadEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

// 专门用于统计会话创建的事件，区别于threadCreateEvent和threadUpdateEvent
@Data
@EqualsAndHashCode(callSuper = false)
public class StatisticThreadCreateEvent extends ApplicationEvent {

    private ThreadEntity thread;

    public StatisticThreadCreateEvent(Object source, ThreadEntity thread) {
        super(source);
        this.thread = thread;
    }
}
