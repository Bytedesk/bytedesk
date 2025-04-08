/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 11:45:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 18:41:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.event.MessageUpdateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageLeaveEventListener {

    private final MessageLeaveRestService MessageLeaveService;

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        // log.info("message leave_msg update event: {}", message);
        //
        if (message.getStatus().equals(MessageStatusEnum.LEAVE_MSG_SUBMIT.name())) {
            MessageLeaveExtra extra = JSON.parseObject(message.getContent(), MessageLeaveExtra.class);
            //
            MessageLeaveRequest request = MessageLeaveRequest.builder()
                    .contact(extra.getContact())
                    .content(extra.getContent())
                    .images(extra.getImages())
                    .threadTopic(message.getThread().getTopic())
                    .user(message.getUser())
                    .build();
            request.setOrgUid(extra.getOrgUid());
            //
            MessageLeaveService.create(request);
        }
    }

}
