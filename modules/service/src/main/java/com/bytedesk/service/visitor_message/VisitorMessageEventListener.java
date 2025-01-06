/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-05 11:07:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-11 09:08:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_message;

import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 监听message中客服消息，包括uid。用于在访客端显示消息，减少message表压力
 */
// @Slf4j
@Component
public class VisitorMessageEventListener {

    // @EventListener
    // public void onMessageCreateEvent(MessageCreateEvent event) {
    //     // log.info("visitor message unread create event: " + event);

    // }

    // @EventListener
    // public void onMessageUpdateEvent(MessageUpdateEvent event) {
    //     // log.info("visitor message unread update event: " + event);
    // }

}
