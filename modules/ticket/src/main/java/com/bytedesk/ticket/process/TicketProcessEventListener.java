/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 12:39:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 21:38:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.ticket.consts.TicketConsts;
import com.bytedesk.ticket.process.event.TicketProcessCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketProcessEventListener {

    private final RepositoryService repositoryService;

    private final ResourceLoader resourceLoader;

    private final TicketProcessRestService ticketProcessRestService;

    @Order(8)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("ticket process - organization created: {}", orgUid);

        // 检查是否已经部署
        List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                .deploymentTenantId(orgUid)
                .deploymentName(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                .list();

        if (!existingDeployments.isEmpty()) {
            log.info("工单流程已存在，跳过部署: tenantId={}", orgUid);
            return;
        }

        // 读取并部署流程
        try {
            Resource resource = resourceLoader.getResource("classpath:processes/group-ticket-process.bpmn20.xml");
            // String groupTicketBpmn20Xml = FileUtils.readFileToString(resource.getFile(), "UTF-8");
            String groupTicketBpmn20Xml = "";
            
            try (InputStream inputStream = resource.getInputStream()) {
                groupTicketBpmn20Xml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            // 生成 processUid 并创建流程记录
            String processUid = (orgUid + "_" + TicketConsts.TICKET_PROCESS_KEY_GROUP).toLowerCase();
            TicketProcessRequest processRequest = TicketProcessRequest.builder()
                    .name(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                    .key(TicketConsts.TICKET_PROCESS_KEY_GROUP)
                    .description(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                    .build();
            processRequest.setUid(processUid);
            processRequest.setContent(groupTicketBpmn20Xml);
            processRequest.setOrgUid(orgUid);
            ticketProcessRestService.create(processRequest);

            // 部署流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                    .addClasspathResource("processes/group-ticket-process.bpmn20.xml")
                    .tenantId(orgUid)
                    .deploy();

            // 更新 TicketProcessEntity
            Optional<TicketProcessEntity> processEntity = ticketProcessRestService.findByUid(processUid);
            if (processEntity.isPresent()) {
                processEntity.get().setDeploymentId(deployment.getId());
                processEntity.get().setDeployed(true);
                ticketProcessRestService.save(processEntity.get());
            }

            log.info("部署租户流程成功: deploymentId={}, tenantId={}",
                    deployment.getId(), deployment.getTenantId());

        } catch (IOException e) {
            log.error("部署工单流程失败: tenantId={}", orgUid, e);
        }
    }

    @EventListener
    public void onTicketProcessCreateEvent(TicketProcessCreateEvent event) {
        log.info("TicketProcessEventListener onTicketProcessCreateEvent: {}", event);
    }

}