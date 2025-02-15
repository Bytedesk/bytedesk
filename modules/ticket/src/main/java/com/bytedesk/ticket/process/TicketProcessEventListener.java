/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 12:39:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-15 13:07:47
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

import org.apache.commons.io.FileUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.ticket.consts.TicketConsts;
import com.bytedesk.ticket.process.event.TicketProcessCreateEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketProcessEventListener {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private TicketProcessRestService ticketProcessRestService;

    @Order(8)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("ticket process - organization created: {}", orgUid);
        // 读取 processes/group-ticket-process.bpmn20.xml 文件内容
        Resource resource = resourceLoader.getResource("classpath:processes/group-ticket-process.bpmn20.xml");
        try {
            // 读取文件内容
            String groupTicketBpmn20Xml = FileUtils.readFileToString(resource.getFile(), "UTF-8");
            log.info("groupTicketBpmn20Xml: {}", groupTicketBpmn20Xml);
            // 生成 processUid
            String processUid = orgUid + "_" + TicketConsts.TICKET_PROCESS_KEY_GROUP;
            // 创建 TicketProcessEntity
            TicketProcessRequest processRequest = TicketProcessRequest.builder()
                    .name(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                    .key(TicketConsts.TICKET_PROCESS_KEY_GROUP)
                    .description(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                    .build();
            processRequest.setUid(processUid.toLowerCase());
            processRequest.setContent(groupTicketBpmn20Xml);
            processRequest.setOrgUid(orgUid);
            ticketProcessRestService.create(processRequest);
        } catch (IOException e) {
            log.error("读取 processes/group-ticket-process.bpmn20.xml 文件内容失败", e);
        }

        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                .addClasspathResource("processes/group-ticket-process.bpmn20.xml")
                .tenantId(orgUid)                         // 设置租户ID
                .deploy();
        log.info("部署租户流程成功: deploymentId={}, tenantId={}", deployment.getId(), deployment.getTenantId());
    }

    @EventListener
    public void onTicketProcessCreateEvent(TicketProcessCreateEvent event) {
        log.info("TicketProcessEventListener onTicketProcessCreateEvent: {}", event);
    }


}   