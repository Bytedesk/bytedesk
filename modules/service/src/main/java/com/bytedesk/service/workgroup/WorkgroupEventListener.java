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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.topic.TopicEntity;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRestService;
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

    private final TopicRestService topicRestService;

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
        String workgroupUid = event.getWorkgroupUid();
        String topic = TopicUtils.getQueueTopicFromUid(workgroupUid);
        log.info("workgroup - workgroup created: {}, topic: {}", event.getWorkgroupUid(), topic);
        syncAgentSubscriptions(workgroupUid, topic, false);
    }

    @EventListener
    public void onWorkgroupUpdateEvent(WorkgroupUpdateEvent event) {
        String workgroupUid = event.getWorkgroupUid();
        String topic = TopicUtils.getQueueTopicFromUid(workgroupUid);
        log.info("workgroup - workgroup updated: {}, topic: {}", event.getNickname(), topic);
        syncAgentSubscriptions(workgroupUid, topic, true);
    }

    @EventListener
    public void onWorkgroupDeleteEvent(WorkgroupDeleteEvent event) {
        String workgroupUid = event.getWorkgroupUid();
        String topic = TopicUtils.getQueueTopicFromUid(workgroupUid);
        log.info("workgroup - workgroup deleted: {}, topic: {}", workgroupUid, topic);
        removeAllSubscriptions(topic);
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

    private void syncAgentSubscriptions(String workgroupUid, String topic, boolean pruneStale) {
        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
        if (!workgroupOptional.isPresent()) {
            log.warn("workgroup - workgroup not found for uid: {}", workgroupUid);
            return;
        }

        WorkgroupEntity workgroup = workgroupOptional.get();
        Set<String> agentUserUids = extractAgentUserUids(workgroup);
        if (agentUserUids.isEmpty()) {
            log.debug("workgroup - no agents bound to workgroup {} while syncing topics", workgroupUid);
        }

        agentUserUids.forEach(userUid -> topicCacheService.pushTopic(topic, userUid));

        if (pruneStale) {
            removeStaleSubscriptions(topic, agentUserUids);
        }
    }

    private Set<String> extractAgentUserUids(WorkgroupEntity workgroup) {
        Set<String> agentUserUids = new HashSet<>();
        if (workgroup.getAgents() == null) {
            return agentUserUids;
        }
        workgroup.getAgents().forEach(agent -> {
            if (agent != null && StringUtils.hasText(agent.getUserUid())) {
                agentUserUids.add(agent.getUserUid());
            }
        });
        return agentUserUids;
    }

    private void removeStaleSubscriptions(String topic, Set<String> validUserUids) {
        Set<TopicEntity> topicEntities = topicRestService.findByTopic(topic);
        if (topicEntities == null || topicEntities.isEmpty()) {
            return;
        }
        topicEntities.forEach(topicEntity -> {
            if (topicEntity == null || !StringUtils.hasText(topicEntity.getUserUid())) {
                return;
            }
            if (!validUserUids.contains(topicEntity.getUserUid())) {
                topicRestService.remove(topic, topicEntity.getUserUid());
            }
        });
    }

    private void removeAllSubscriptions(String topic) {
        Set<TopicEntity> topicEntities = topicRestService.findByTopic(topic);
        if (topicEntities == null || topicEntities.isEmpty()) {
            return;
        }
        topicEntities.forEach(topicEntity -> {
            if (topicEntity != null && StringUtils.hasText(topicEntity.getUserUid())) {
                topicRestService.remove(topic, topicEntity.getUserUid());
            }
        });
    }

}
