/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-04 11:32:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 11:04:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.event.UserCreateEvent;
import com.bytedesk.core.thread.ThreadRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AssistantEventListener {

    private final ThreadRestService threadRestService;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserCreateEvent(UserCreateEvent event) {
        UserEntity user = event.getUser();
        log.info("assistant onUserCreateEvent: {}", user.getUid());
        //
        try {
            // 每创建一个用户，自动给此用户生成一条文件助理的会话
            threadRestService.createFileAssistantThread(user);
            // 每创建一个用户，自动给此用户生成一条剪贴助理的会话
            // threadService.createClipboardAssistantThread(user);
        } catch (Exception e) {
            log.error("Failed to create file assistant thread for user {}: {}", user.getUid(), e.getMessage());
            // 不重新抛出异常，避免影响用户创建流程
        }
    }

}
