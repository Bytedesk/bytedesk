/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task_list;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.event.MemberCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TaskListEventListener {

    private final TaskListRestService taskListRestService;

    @EventListener
    public void onMemberCreateEvent(MemberCreateEvent event) {
        MemberEntity member = event.getMember();
        if (member == null) {
            log.warn("MemberCreateEvent has null member, source={}", event.getSource());
            return;
        }

        UserEntity user = member.getUser();
        if (user != null && user.getUid() != null) {
            String userUid = user.getUid();
            log.info("Creating default task list for member user: {}", userUid);
            
            TaskListRequest request = TaskListRequest.builder()
                    .name("My Tasks")
                    .description("Default task list")
                    .userUid(userUid)
                    .type(TaskListTypeEnum.TASK.name())
                    .color("blue")
                    .order(0)
                    .archived(false)
                    .build();
            
            try {
                taskListRestService.create(request);
                log.info("Default task list created successfully for user: {}", userUid);
            } catch (Exception e) {
                log.error("Failed to create default task list for user: {}", userUid, e);
            }
        } else {
            log.warn("Member {} has no associated user", member.getUid());
        }
    }

 
}

