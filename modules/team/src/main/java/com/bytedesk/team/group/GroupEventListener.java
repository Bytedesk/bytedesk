/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 23:00:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 10:29:44
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
// import java.util.Optional;

import org.springframework.context.event.EventListener;
// import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
// import org.springframework.transaction.event.TransactionPhase;
// import org.springframework.transaction.event.TransactionalEventListener;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.team.group.event.GroupCreateEvent;
import com.bytedesk.team.group.event.GroupUpdateEvent;
import com.bytedesk.team.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class GroupEventListener {

    private final ThreadRestService threadRestService;

    // @EventListener
    // public void onThreadCreateEvent(ThreadCreateEvent event) {
    //     ThreadEntity thread = event.getThread();
    //     //
    //     if (ThreadTypeEnum.GROUP.name().equals(thread.getType())) {
    //         String topic = thread.getTopic();
    //         log.info("group ThreadCreateEvent: {}", topic);
    //         // 同事群组会话：org/group/{group_uid}
    //         String groupUid = topic.split("/")[2];
    //         log.info("groupUid {}", groupUid);
    //         Optional<GroupEntity> groupOptional = groupRestService.findByUid(groupUid);
    //         if (groupOptional.isPresent()
    //                 // 必须添加此过滤，否则创建群组成员会话时，会重复调用，只有创建者才会创建群组成员的thread
    //                 && groupOptional.get().getCreator().getUid().equals(thread.getOwner().getUid())) {
    //             // 仅允许群组创建者首次创建thread时，创建群组成员的thread
    //             Iterator<MemberEntity> iterator = groupOptional.get().getMembers().iterator();
    //             while (iterator.hasNext()) {
    //                 MemberEntity member = iterator.next();
    //                 threadRestService.createGroupMemberThread(thread, member.getUser());
    //             }
    //         }
    //     }
    // }

    // 使用事务监听器，在事务提交后处理事件
    @EventListener
    public void onGroupCreateEvent(GroupCreateEvent event) {
        GroupEntity group = event.getGroup();
        log.info("GroupCreateEvent: {}", group.getUid());
        // 仅允许群组创建者首次创建thread时，创建群组成员的thread
        Iterator<MemberEntity> iterator = group.getMembers().iterator();
        while (iterator.hasNext()) {
            MemberEntity member = iterator.next();
            String user = UserProtobuf.builder()
                    .uid(group.getUid())
                    .nickname(group.getName())
                    .avatar(group.getAvatar())
                    .build()
                    .toJson();
            String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
            String orgUid = group.getOrgUid();
            log.info("Creating group member thread for user: {}, topic: {}, orgUid: {}", user, topic, orgUid);
            threadRestService.createGroupMemberThread(user, topic, orgUid, member.getUser());
        }
    }

    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void onGroupUpdateEvent(GroupUpdateEvent event) {
        GroupEntity group = event.getGroup();
        log.info("GroupUpdateEvent: {}", group.getUid());
        // 更新群组时，更新群组的thread
        // threadRestService.updateGroupThread(group);
    }


}
