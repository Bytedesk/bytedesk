/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 23:00:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-29 23:33:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import com.bytedesk.core.thread.ThreadCreateEvent;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.team.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class GroupEventListener {

    private final ThreadService threadService;

    private final GroupService groupService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        // ThreadRequest request = event.getRequest();
        log.info("group ThreadCreateEvent: {}", thread.getUid());
        //
        if (thread.getType().equals(ThreadTypeEnum.GROUP.name())) {
            String topic = thread.getTopic();
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
