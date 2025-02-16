/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 13:03:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 21:38:13
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
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRequest;
import com.bytedesk.core.rbac.authority.AuthorityService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationService;
import com.bytedesk.ticket.consts.TicketConsts;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketProcessInitializer implements SmartInitializingSingleton {

    private final AuthorityService authorityService;

    private final OrganizationService organizationService;

    private final TicketProcessRestService ticketProcessRestService;

    private final RepositoryService repositoryService;

    private final ResourceLoader resourceLoader;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        initProcess();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = TicketProcessPermissions.PROCESS_PREFIX + permission.name();
            if (authorityService.existsByValue(permissionValue)) {
                continue;
            }
            AuthorityRequest authRequest = AuthorityRequest.builder()
                    .name(I18Consts.I18N_PREFIX + permissionValue)
                    .value(permissionValue)
                    .description("Permission for " + permissionValue)
                    .build();
            authRequest.setUid(permissionValue.toLowerCase());
            authorityService.create(authRequest);
        }
    }

    private void initProcess() {
        try {
            // 使用 getInputStream() 而不是 getFile()
            Resource resource = resourceLoader.getResource("classpath:processes/group-ticket-process.bpmn20.xml");
            String groupTicketBpmn20Xml = "";
            
            try (InputStream inputStream = resource.getInputStream()) {
                groupTicketBpmn20Xml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
            
            // 判断所有组织是否存在 TicketProcessEntity
            List<OrganizationEntity> organizations = organizationService.findAll();
            for (OrganizationEntity organization : organizations) {
                String orgUid = organization.getUid();
                // 检查是否已经部署
                List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                        .deploymentTenantId(orgUid)
                        .deploymentName(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                        .list();

                if (!existingDeployments.isEmpty()) {
                    log.info("工单流程已存在，跳过部署: tenantId={}", orgUid);
                    continue;
                }

                String processUid = (orgUid + "_" + TicketConsts.TICKET_PROCESS_KEY_GROUP).toLowerCase();
                // 初始化 TicketProcessEntity
                TicketProcessRequest processRequest = TicketProcessRequest.builder()
                        .name(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                        .key(TicketConsts.TICKET_PROCESS_KEY_GROUP)
                        .description(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                        .build();
                processRequest.setUid(processUid);
                processRequest.setOrgUid(orgUid);
                processRequest.setContent(groupTicketBpmn20Xml);
                ticketProcessRestService.create(processRequest);

                // 部署流程
                Deployment deployment = repositoryService.createDeployment()
                        .name(TicketConsts.TICKET_PROCESS_NAME_GROUP)
                        .addInputStream(TicketConsts.TICKET_PROCESS_KEY_GROUP + ".bpmn20.xml", 
                            resource.getInputStream())  // 使用 InputStream
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
            }

        } catch (IOException e) {
            log.error("读取流程文件失败", e);
            throw new RuntimeException("读取流程文件失败: " + e.getMessage());
        }
    }
}
