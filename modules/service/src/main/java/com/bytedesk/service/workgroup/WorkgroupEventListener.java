/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:20:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:07:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup.event.WorkgroupCreateEvent;
import com.bytedesk.service.workgroup.event.WorkgroupDeleteEvent;
import com.bytedesk.service.workgroup.event.WorkgroupUpdateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class WorkgroupEventListener {

    private final AgentRestService agentRestService;
    
    private final WorkgroupRestService workgroupRestService;

    private final TopicCacheService topicCacheService;

    private final UidUtils uidUtils;

    @Order(7)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        UserEntity user = organization.getUser();
        String orgUid = organization.getUid();
        // 创建默认workgroup
        log.info("workgroup - organization created: {}", organization.getName());

        List<String> agentUids = new ArrayList<>();
        Optional<AgentEntity> agentOptional = agentRestService.findByMobileAndOrgUid(user.getMobile(), organization.getUid());
        agentOptional.ifPresent(agent -> {
            agentUids.add(agent.getUid());
        });
        // 
        // List<String> worktimeUids = new ArrayList<>();
        // String worktimeUid = worktimeService.createDefault();
        // worktimeUids.add(worktimeUid);
        
        // add workgroups
        WorkgroupRequest workgroupRequest = WorkgroupRequest.builder()
                .uid(uidUtils.getUid())
                .nickname(I18Consts.I18N_WORKGROUP_NICKNAME)
                .description(I18Consts.I18N_WORKGROUP_DESCRIPTION)
                .agentUids(agentUids)
                .orgUid(orgUid)
                .build();
        workgroupRestService.create(workgroupRequest);
    }

    @EventListener
    public void onWorkgroupCreateEvent(WorkgroupCreateEvent event) {
        WorkgroupEntity workgroup = (WorkgroupEntity) event.getSource();
        String workgroupUid = workgroup.getUid();
        String topic = TopicUtils.getQueueTopicFromUid(workgroupUid);
        log.info("workgroup - workgroup created: {}, topic: {}", workgroup.getNickname(), topic);
    }

    @EventListener
    public void onWorkgroupUpdateEvent(WorkgroupUpdateEvent event) {
        WorkgroupEntity workgroup = (WorkgroupEntity) event.getSource();
        String workgroupUid = workgroup.getUid();
        String topic = TopicUtils.getQueueTopicFromUid(workgroupUid);
        log.info("workgroup - workgroup updated: {}, topic: {}", workgroup.getNickname(), topic);
        // topicCacheService.updateTopic(topic);
    }

    @EventListener
    public void onWorkgroupDeleteEvent(WorkgroupDeleteEvent event) {
        WorkgroupEntity workgroup = (WorkgroupEntity) event.getSource();
        String workgroupUid = workgroup.getUid();
        String topic = TopicUtils.getQueueTopicFromUid(workgroupUid);
        log.info("workgroup - workgroup deleted: {}, topic: {}", workgroup.getNickname(), topic);
        // topicCacheService.removeTopic(topic);
    }

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        log.info("topic onMqttConnectedEvent uid {}, clientId {}", userUid, clientId);
        List<String> workgroupUids = workgroupRestService.findWorkgroupUidsByUserUid(userUid);
        for (String workgroupUid : workgroupUids) {
            String topic = TopicUtils.getQueueTopicFromUid(workgroupUid);
            topicCacheService.pushTopic(topic, userUid);
        }
    }
}
