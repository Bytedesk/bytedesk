/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 11:45:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 10:16:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageLeaveEventListener {

    private final MessageLeaveRestService MessageLeaveService;

    private final QueueMemberRestService queueMemberRestService;

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
                    .threadUid(message.getThread().getUid())
                    // .thread(message.getThread())
                    .user(message.getUser())
                    .client(message.getClient())
                    .orgUid(extra.getOrgUid())
                    .build();
            MessageLeaveService.create(request);

            // 更新queue_member表
            Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService.findByThreadUid(message.getThread().getUid());
            if (queueMemberOptional.isPresent()) {
                QueueMemberEntity queueMember = queueMemberOptional.get();
                queueMember.setLeaveMsg(true);
                queueMember.setLeaveMsgAt(message.getCreatedAt());
                queueMemberRestService.save(queueMember);
            }
        }
    }

}
