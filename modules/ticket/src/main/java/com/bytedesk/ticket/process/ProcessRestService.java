/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:31:43
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
import java.util.Comparator;
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
import com.bytedesk.core.exception.NotLoginException;
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
public class ProcessRestService
        extends BaseRestService<ProcessEntity, ProcessRequest, ProcessResponse> {

    private final ProcessRepository processRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final RepositoryService repositoryService;

    private final ResourceLoader resourceLoader;

    @Override
    public Page<ProcessResponse> queryByOrg(ProcessRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ProcessEntity> spec = ProcessSpecification.search(request, authService);
        Page<ProcessEntity> page = processRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ProcessResponse> queryByUser(ProcessRequest request) {
        // 获取当前登录用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        String userUid = user.getUid();
        request.setUserUid(userUid);
        //
        return queryByOrg(request);
    }

    @Cacheable(value = "process", key = "#uid", unless = "#result==null")
    @Override
    public Optional<ProcessEntity> findByUid(String uid) {
        return processRepository.findByUid(uid);
    }

    @Override
    public ProcessResponse create(ProcessRequest request) {
        String normalizedType = StringUtils.hasText(request.getType())
                ? request.getType()
                : ProcessTypeEnum.TICKET_INTERNAL.name();
        request.setType(normalizedType);
        // 判断uid是否存在
        if (StringUtils.hasText(request.getUid())) {
            Optional<ProcessEntity> optional = processRepository.findByUid(request.getUid());
            if (optional.isPresent()) {
                return convertToResponse(optional.get());
            }
        } else {
            // 生成uid
            request.setUid(uidUtils.getUid());
        }
        // 流程key不能重复
        Optional<ProcessEntity> optionalKey = processRepository.findByKeyAndOrgUidAndType(request.getKey(),
                request.getOrgUid(), normalizedType);
        if (optionalKey.isPresent()) {
            throw new RuntimeException(
                    "Process key " + request.getKey() + " in org " + request.getOrgUid() + " with type "
                            + normalizedType + " already exists");
        }

        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        } else {
            // 如果用户为空，则为系统自动创建
        }

        ProcessEntity entity = modelMapper.map(request, ProcessEntity.class);
        // entity.setUid(uidUtils.getUid()); // 移动到开头

        ProcessEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create process failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ProcessResponse update(ProcessRequest request) {
        Optional<ProcessEntity> optional = processRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ProcessEntity entity = optional.get();
            entity.setKey(request.getKey());
            entity.setName(request.getName());
            entity.setSchema(request.getSchema());
            entity.setDescription(request.getDescription());
            //
            ProcessEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update process failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Process not found");
        }
    }

    @Override
    protected ProcessEntity doSave(ProcessEntity entity) {
        return processRepository.save(entity);
    }

    @Override
    public ProcessEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            ProcessEntity entity) {
        try {
            Optional<ProcessEntity> latest = processRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ProcessEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return processRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("处理乐观锁冲突失败: {}", ex.getMessage());
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ProcessEntity> optional = processRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        } else {
            throw new RuntimeException("Process not found");
        }
    }

    @Override
    public void delete(ProcessRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ProcessResponse convertToResponse(ProcessEntity entity) {
        return modelMapper.map(entity, ProcessResponse.class);
    }

    public void initProcess(String orgUid) {
        // 检查是否已经部署
        List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                .deploymentTenantId(orgUid)
                .deploymentName(TicketConsts.TICKET_PROCESS_NAME)
                .list();

        existingDeployments.sort(Comparator
            .comparing(Deployment::getDeploymentTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .reversed());

        Deployment latestDeployment = existingDeployments.isEmpty() ? null : existingDeployments.get(0);

        if (!existingDeployments.isEmpty()) {
            ensureExternalProcess(orgUid, latestDeployment);
            return;
        }

        // 读取并部署流程
        try {
            String ticketBpmn20Xml = loadDefaultProcessSchema();

            String processUid = Utils.formatUid(orgUid, TicketConsts.TICKET_PROCESS_KEY);
            String externalProcessUid = buildExternalProcessUid(orgUid);

            create(buildDefaultProcessRequest(processUid, orgUid, ticketBpmn20Xml, ProcessTypeEnum.TICKET_INTERNAL));
            create(buildDefaultProcessRequest(externalProcessUid, orgUid, ticketBpmn20Xml, ProcessTypeEnum.TICKET_EXTERNAL));

            // 部署流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(TicketConsts.TICKET_PROCESS_NAME)
                    .addClasspathResource(TicketConsts.TICKET_PROCESS_PATH)
                    .tenantId(orgUid)
                    .deploy();

            markProcessAsDeployed(processUid, deployment.getId());
            markProcessAsDeployed(externalProcessUid, deployment.getId());

            log.info("部署租户流程成功: deploymentId={}, tenantId={}",
                    deployment.getId(), deployment.getTenantId());

        } catch (IOException e) {
            log.error("部署工单流程失败: tenantId={}", orgUid, e);
        }
    }

    public void initThreadProcess(String orgUid) {
        // 检查是否已经部署
        List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                .deploymentTenantId(orgUid)
                .deploymentName(ThreadConsts.THREAD_PROCESS_NAME)
                .list();

        if (!existingDeployments.isEmpty()) {
            // log.info("会话工单流程已存在，跳过部署: tenantId={}", orgUid);
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
            ProcessRequest processRequest = ProcessRequest.builder()
                    .uid(processUid)
                    .name(ThreadConsts.THREAD_PROCESS_NAME)
                    .schema(groupThreadBpmn20Xml)
                    .type(ProcessTypeEnum.THREAD.name())
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

            // 更新 ProcessEntity
            Optional<ProcessEntity> processEntity = findByUid(processUid);
            if (processEntity.isPresent()) {
                processEntity.get().setDeploymentId(deployment.getId());
                // processEntity.get().setDeployed(true);
                processEntity.get().setStatus(ProcessStatusEnum.DEPLOYED.name());
                save(processEntity.get());
            }

            log.info("部署租户流程成功: deploymentId={}, tenantId={}",
                    deployment.getId(), deployment.getTenantId());

        } catch (IOException e) {
            log.error("部署工单流程失败: tenantId={}", orgUid, e);
        }
    }

    @Override
    protected Specification<ProcessEntity> createSpecification(ProcessRequest request) {
        return ProcessSpecification.search(request, authService);
    }

    @Override
    protected Page<ProcessEntity> executePageQuery(Specification<ProcessEntity> specification, Pageable pageable) {
        return processRepository.findAll(specification, pageable);
    }

    private ProcessRequest buildDefaultProcessRequest(String uid, String orgUid, String schema,
            ProcessTypeEnum type) {
        return ProcessRequest.builder()
                .uid(uid)
                .name(TicketConsts.TICKET_PROCESS_NAME)
                .schema(schema)
                .type(type.name())
                .key(TicketConsts.TICKET_PROCESS_KEY)
                .description(TicketConsts.TICKET_PROCESS_NAME)
                .orgUid(orgUid)
                .build();
    }

    private String buildExternalProcessUid(String orgUid) {
        return Utils.formatUid(orgUid,
                TicketConsts.TICKET_PROCESS_KEY + TicketConsts.TICKET_EXTERNAL_PROCESS_UID_SUFFIX);
    }

    private void ensureExternalProcess(String orgUid, Deployment latestDeployment) {
        String externalType = ProcessTypeEnum.TICKET_EXTERNAL.name();
        Optional<ProcessEntity> externalProcess = processRepository
                .findByKeyAndOrgUidAndType(TicketConsts.TICKET_PROCESS_KEY, orgUid, externalType);
        if (externalProcess.isPresent()) {
            return;
        }

        String schema = processRepository
                .findByKeyAndOrgUidAndType(TicketConsts.TICKET_PROCESS_KEY, orgUid,
                        ProcessTypeEnum.TICKET_INTERNAL.name())
                .map(ProcessEntity::getSchema)
                .orElseGet(() -> {
                    try {
                        return loadDefaultProcessSchema();
                    } catch (IOException e) {
                        throw new RuntimeException("加载默认工单流程定义失败", e);
                    }
                });

        String externalProcessUid = buildExternalProcessUid(orgUid);
        create(buildDefaultProcessRequest(externalProcessUid, orgUid, schema, ProcessTypeEnum.TICKET_EXTERNAL));

        if (latestDeployment != null) {
            markProcessAsDeployed(externalProcessUid, latestDeployment.getId());
        }

        log.info("已为租户补充外部工单流程: tenantId={}, processUid={}", orgUid, externalProcessUid);
    }

    private void markProcessAsDeployed(String processUid, String deploymentId) {
        if (!StringUtils.hasText(processUid) || !StringUtils.hasText(deploymentId)) {
            return;
        }
        findByUid(processUid).ifPresent(entity -> {
            entity.setDeploymentId(deploymentId);
            entity.setStatus(ProcessStatusEnum.DEPLOYED.name());
            save(entity);
        });
    }

    private String loadDefaultProcessSchema() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + TicketConsts.TICKET_PROCESS_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
