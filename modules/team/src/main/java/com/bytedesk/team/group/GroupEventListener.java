/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 23:00:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 12:39:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import java.util.Iterator;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.team.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class GroupEventListener {

    private final ThreadRestService threadService;

    private final GroupRestService groupService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        //
        if (ThreadTypeEnum.GROUP.name().equals(thread.getType())) {
            String topic = thread.getTopic();
            log.info("group ThreadCreateEvent: {}", topic);
            // 同事群组会话：org/group/{group_uid}
            String groupUid = topic.split("/")[2];
            log.info("groupUid {}", groupUid);
            Optional<GroupEntity> groupOptional = groupService.findByUid(groupUid);
            if (groupOptional.isPresent()
                    && groupOptional.get().getCreator().getUid().equals(thread.getOwner().getUid())) {
                // 仅允许群组创建者首次创建thread时，创建群组成员的thread
                Iterator<MemberEntity> iterator = groupOptional.get().getMembers().iterator();
                while (iterator.hasNext()) {
                    MemberEntity member = iterator.next();
                    //
                    threadService.createGroupMemberThread(thread, member.getUser());
                }
            }
        }
    }


}
