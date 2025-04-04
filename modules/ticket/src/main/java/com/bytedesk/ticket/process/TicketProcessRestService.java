/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 12:36:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.ticket.thread.ThreadConsts;
import com.bytedesk.ticket.ticket.TicketConsts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TicketProcessRestService
        extends BaseRestService<TicketProcessEntity, TicketProcessRequest, TicketProcessResponse> {

    private final TicketProcessRepository processRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final RepositoryService repositoryService;

    private final ResourceLoader resourceLoader;

    @Override
    public Page<TicketProcessResponse> queryByOrg(TicketProcessRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TicketProcessEntity> spec = TicketProcessSpecification.search(request);
        Page<TicketProcessEntity> page = processRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TicketProcessResponse> queryByUser(TicketProcessRequest request) {
        // 获取当前登录用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        String userUid = user.getUid();
        request.setUserUid(userUid);
        //
        return queryByOrg(request);
    }

    @Cacheable(value = "process", key = "#uid", unless = "#result==null")
    @Override
    public Optional<TicketProcessEntity> findByUid(String uid) {
        return processRepository.findByUid(uid);
    }

    @Override
    public TicketProcessResponse create(TicketProcessRequest request) {
        // 判断uid是否存在
        if (StringUtils.hasText(request.getUid())) {
            Optional<TicketProcessEntity> optional = processRepository.findByUid(request.getUid());
            if (optional.isPresent()) {
                return convertToResponse(optional.get());
            }
        } else {
            // 生成uid
            request.setUid(uidUtils.getUid());
        }
        // 流程key不能重复
        Optional<TicketProcessEntity> optionalKey = processRepository.findByKeyAndOrgUid(request.getKey(),
                request.getOrgUid());
        if (optionalKey.isPresent()) {
            throw new RuntimeException(
                    "Process key " + request.getKey() + " in org " + request.getOrgUid() + " already exists");
        }

        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        } else {
            // 如果用户为空，则为系统自动创建
        }

        TicketProcessEntity entity = modelMapper.map(request, TicketProcessEntity.class);
        // entity.setUid(uidUtils.getUid()); // 移动到开头

        TicketProcessEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create process failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public TicketProcessResponse update(TicketProcessRequest request) {
        Optional<TicketProcessEntity> optional = processRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TicketProcessEntity entity = optional.get();
            entity.setKey(request.getKey());
            entity.setName(request.getName());
            entity.setContent(request.getContent());
            entity.setDescription(request.getDescription());
            //
            TicketProcessEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update process failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("TicketProcess not found");
        }
    }

    @Override
    public TicketProcessEntity save(TicketProcessEntity entity) {
        try {
            return processRepository.save(entity);
        } catch (Exception e) {
            log.error("Save process failed: {}", e.getMessage());
            throw new RuntimeException("Save process failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TicketProcessEntity> optional = processRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        } else {
            throw new RuntimeException("TicketProcess not found");
        }
    }

    @Override
    public void delete(TicketProcessRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TicketProcessEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TicketProcessResponse convertToResponse(TicketProcessEntity entity) {
        return modelMapper.map(entity, TicketProcessResponse.class);
    }

    public void initTicketGroupProcess(String orgUid) {
        // 检查是否已经部署
        List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                .deploymentTenantId(orgUid)
                .deploymentName(TicketConsts.TICKET_PROCESS_NAME)
                .list();

        if (!existingDeployments.isEmpty()) {
            log.info("工单流程已存在，跳过部署: tenantId={}", orgUid);
            return;
        }

        // 读取并部署流程
        try {
            Resource resource = resourceLoader
                    .getResource("classpath:" + TicketConsts.TICKET_PROCESS_PATH);
            String groupTicketBpmn20Xml = "";

            try (InputStream inputStream = resource.getInputStream()) {
                groupTicketBpmn20Xml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            // 生成 processUid 并创建流程记录
            String processUid = Utils.formatUid(orgUid, TicketConsts.TICKET_PROCESS_KEY);
            TicketProcessRequest processRequest = TicketProcessRequest.builder()
                    .uid(processUid)
                    .name(TicketConsts.TICKET_PROCESS_NAME)
                    .content(groupTicketBpmn20Xml)
                    .type(TicketProcessTypeEnum.TICKET.name())
                    .key(TicketConsts.TICKET_PROCESS_KEY)
                    .description(TicketConsts.TICKET_PROCESS_NAME)
                    .orgUid(orgUid)
                    .build();
            create(processRequest);

            // 部署流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(TicketConsts.TICKET_PROCESS_NAME)
                    .addClasspathResource(TicketConsts.TICKET_PROCESS_PATH)
                    .tenantId(orgUid)
                    .deploy();

            // 更新 TicketProcessEntity
            Optional<TicketProcessEntity> processEntity = findByUid(processUid);
            if (processEntity.isPresent()) {
                processEntity.get().setDeploymentId(deployment.getId());
                processEntity.get().setDeployed(true);
                save(processEntity.get());
            }

            log.info("部署租户流程成功: deploymentId={}, tenantId={}",
                    deployment.getId(), deployment.getTenantId());

        } catch (IOException e) {
            log.error("部署工单流程失败: tenantId={}", orgUid, e);
        }
    }

    public void initThreadGroupProcess(String orgUid) {
        // 检查是否已经部署
        List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                .deploymentTenantId(orgUid)
                .deploymentName(ThreadConsts.THREAD_PROCESS_NAME)
                .list();

        if (!existingDeployments.isEmpty()) {
            log.info("会话工单流程已存在，跳过部署: tenantId={}", orgUid);
            return;
        }

        // 读取并部署流程
        try {
            Resource resource = resourceLoader
                    .getResource("classpath:" + ThreadConsts.THREAD_PROCESS_PATH);
            String groupThreadBpmn20Xml = "";

            try (InputStream inputStream = resource.getInputStream()) {
                groupThreadBpmn20Xml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            // 生成 processUid 并创建流程记录
            String processUid = Utils.formatUid(orgUid, ThreadConsts.THREAD_PROCESS_KEY);
            TicketProcessRequest processRequest = TicketProcessRequest.builder()
                    .uid(processUid)
                    .name(ThreadConsts.THREAD_PROCESS_NAME)
                    .content(groupThreadBpmn20Xml)
                    .type(TicketProcessTypeEnum.THREAD.name())
                    .key(ThreadConsts.THREAD_PROCESS_KEY)
                    .description(ThreadConsts.THREAD_PROCESS_NAME)
                    .orgUid(orgUid)
                    .build();
            create(processRequest);

            // 部署流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(ThreadConsts.THREAD_PROCESS_NAME)
                    .addClasspathResource(ThreadConsts.THREAD_PROCESS_PATH)
                    .tenantId(orgUid)
                    .deploy();

            // 更新 TicketProcessEntity
            Optional<TicketProcessEntity> processEntity = findByUid(processUid);
            if (processEntity.isPresent()) {
                processEntity.get().setDeploymentId(deployment.getId());
                processEntity.get().setDeployed(true);
                save(processEntity.get());
            }

            log.info("部署租户流程成功: deploymentId={}, tenantId={}",
                    deployment.getId(), deployment.getTenantId());

        } catch (IOException e) {
            log.error("部署工单流程失败: tenantId={}", orgUid, e);
        }
    }

    
    
}
