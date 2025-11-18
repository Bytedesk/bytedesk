/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-30 14:02:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-30 15:00:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.identity;

// import lombok.RequiredArgsConstructor;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;
// import lombok.extern.slf4j.Slf4j;

// import com.bytedesk.service.agent.AgentEntity;
// import com.bytedesk.service.agent.event.AgentUpdateEvent;
// import com.bytedesk.service.workgroup.event.WorkgroupUpdateEvent;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class TicketIdentityListener {

//     // private final TicketIdentityService identityService;

//     /**
//      * 监听用户创建/更新事件
//      */
//     @EventListener
//     public void onAgentUpdated(AgentUpdateEvent event) {
//         AgentEntity agent = event.getAgent();
//         // 同步用户到Flowable
//         // identityService.syncUser(agent);
//     }

//     /**
//      * 监听工作组变更事件（最小载荷）
//      */
//     @EventListener
//     public void onWorkgroupUpdated(WorkgroupUpdateEvent event) {
//         // 使用最小载荷字段进行同步，避免实体序列化问题
//         // identityService.syncWorkgroupByBasic(event.getWorkgroupUid(), event.getNickname());
//     }

//     /**
//      * 监听用户加入工作组事件
//      */
//     // @EventListener
//     // public void onAgentJoinWorkgroup(AgentJoinWorkgroupEvent event) {
//     //     // 同步用户和工作组关系
//     //     identityService.syncMembership(event.getAgentId(), event.getWorkgroupId());
//     // }
// } 