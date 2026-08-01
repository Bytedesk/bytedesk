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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.ticket.thread.ThreadConsts;
import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.utils.FlowableIdUtils;

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
            entity.setName(request.getName());
            entity.setSchema(request.getSchema());
            entity.setDescription(request.getDescription());
            entity.setType(request.getType());
            if (StringUtils.hasText(request.getFlowgramSchema())) {
                entity.setFlowgramSchema(request.getFlowgramSchema());
            }
            // entity.setStatus(request.getStatus()); // 不允许手动修改状态
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
    protected Specification<ProcessEntity> createSpecification(ProcessRequest request) {
        return ProcessSpecification.search(request, authService);
    }

    @Override
    protected Page<ProcessEntity> executePageQuery(Specification<ProcessEntity> specification, Pageable pageable) {
        return processRepository.findAll(specification, pageable);
    }

    @Override
    public ProcessResponse convertToResponse(ProcessEntity entity) {
        return modelMapper.map(entity, ProcessResponse.class);
    }

    public void initProcess(String orgUid) {
        try {
            // 第一步：在数据库中创建 ProcessEntity 记录（去重）
            String ticketBpmn20Xml = loadDefaultProcessSchema();
            String ticketFlowgramJson = loadDefaultProcessFlowgramSchema();
            String processUid = Utils.formatUid(orgUid, TicketConsts.TICKET_PROCESS_KEY);
            String externalProcessUid = Utils.formatUid(orgUid,
                    TicketConsts.TICKET_PROCESS_KEY + TicketConsts.TICKET_EXTERNAL_PROCESS_UID_SUFFIX);

            // 创建 TICKET_INTERNAL 流程记录（使用 processUid 作为 deploymentName）
            create(buildDefaultProcessRequest(processUid, orgUid, ticketBpmn20Xml, ticketFlowgramJson,
                    ProcessTypeEnum.TICKET_INTERNAL, TicketConsts.TICKET_PROCESS_NAME));
            log.info("创建内部工单流程记录成功: processUid={}, orgUid={}", processUid, orgUid);

            // 创建 TICKET_EXTERNAL 流程记录（使用 externalProcessUid 作为 deploymentName）
            create(buildDefaultProcessRequest(externalProcessUid, orgUid, ticketBpmn20Xml, ticketFlowgramJson,
                    ProcessTypeEnum.TICKET_EXTERNAL, TicketConsts.TICKET_PROCESS_NAME_EXTERNAL));
            log.info("创建外部工单流程记录成功: processUid={}, orgUid={}", externalProcessUid, orgUid);

            // 第二步：分别处理 TICKET_INTERNAL 和 TICKET_EXTERNAL 的 Deployment
            // 直接使用 processUid 作为 deploymentName，确保唯一性
            deployProcessByType(orgUid, processUid, TicketConsts.TICKET_PROCESS_PATH);
            deployProcessByType(orgUid, externalProcessUid, TicketConsts.TICKET_PROCESS_PATH);

        } catch (IOException e) {
            log.error("部署工单流程失败: tenantId={}", orgUid, e);
        }
    }

    public void initThreadProcess(String orgUid) {
        try {
            // 第一步：加载默认流程模板
            String threadBpmn20Xml = loadDefaultThreadProcessSchema();
            String processUid = Utils.formatUid(orgUid, ThreadConsts.THREAD_PROCESS_KEY);

            // 第二步：在数据库中创建 ProcessEntity 记录（去重）
            create(buildDefaultProcessRequest(processUid, orgUid, threadBpmn20Xml, ProcessTypeEnum.THREAD,
                    ThreadConsts.THREAD_PROCESS_NAME));
            log.info("创建会话流程记录成功: processUid={}, orgUid={}", processUid, orgUid);

            // 第三步：部署流程（直接使用 processUid 作为 deploymentName）
            deployProcessByType(orgUid, processUid, ThreadConsts.THREAD_PROCESS_PATH);

        } catch (IOException e) {
            log.error("部署会话流程失败: tenantId={}", orgUid, e);
        }
    }

    /**
     * 初始化部署流程（从模板文件加载）
     * 直接使用 processUid 作为 deploymentName，确保唯一性
     */
    private void deployProcessByType(String orgUid, String processUid, String processPath)
            throws IOException {
        // 检查该 processUid 对应的 Deployment 是否已存在
        List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                .deploymentTenantId(orgUid)
                .deploymentName(processUid) // 直接使用 processUid 作为 deploymentName
                .list();

        if (!existingDeployments.isEmpty()) {
            // Deployment 已存在，仅更新 ProcessEntity 状态
            existingDeployments.sort(Comparator
                    .comparing(Deployment::getDeploymentTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed());
            Deployment latestDeployment = existingDeployments.get(0);
            markProcessAsDeployed(processUid, latestDeployment.getId());
            log.info("使用现有部署: deploymentId={}, processUid={}, tenantId={}",
                    latestDeployment.getId(), processUid, latestDeployment.getTenantId());
            return;
        }

        // 读取模板 BPMN 文件并替换 process id 为 Flowable 可接受的 key（NCName）
        Resource resource = resourceLoader.getResource("classpath:" + processPath);
        String bpmnXml;
        try (InputStream inputStream = resource.getInputStream()) {
            bpmnXml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        // 替换 process id，使每个 ProcessEntity 有唯一的 processDefinitionKey
        // 根据流程路径判断使用哪个 process key 进行替换
        String originalProcessKey;
        if (processPath.contains("thread")) {
            originalProcessKey = ThreadConsts.THREAD_PROCESS_KEY;
        } else if (processPath.contains("leave-approval")) {
            originalProcessKey = ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_KEY;
        } else if (processPath.contains("reimbursement-approval")) {
            originalProcessKey = ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_KEY;
        } else if (processPath.contains("it-support")) {
            originalProcessKey = ProcessDemoConsts.IT_SUPPORT_PROCESS_KEY;
        } else {
            originalProcessKey = TicketConsts.TICKET_PROCESS_KEY;
        }

        String processDefinitionKey = FlowableIdUtils.toProcessDefinitionKey(processUid);
        bpmnXml = rewriteBpmnProcessId(bpmnXml, originalProcessKey, processDefinitionKey);

        // 执行 Deployment 操作，直接使用 processUid 作为 deploymentName
        Deployment deployment = repositoryService.createDeployment()
                .name(processUid)
                .addString(processUid + ".bpmn20.xml", bpmnXml)
                .tenantId(orgUid)
                .deploy();
        log.info("执行流程部署成功: deploymentId={}, processUid={}, tenantId={}",
                deployment.getId(), processUid, deployment.getTenantId());

        // 更新 ProcessEntity 部署状态
        markProcessAsDeployed(processUid, deployment.getId());
        log.info("更新工单流程部署状态成功: processUid={}, deploymentId={}",
                processUid, deployment.getId());
    }

    private String rewriteBpmnProcessId(String bpmnXml, String originalProcessKey, String newProcessKey) {
        if (!StringUtils.hasText(bpmnXml) || !StringUtils.hasText(newProcessKey)) {
            return bpmnXml;
        }

        // 优先按模板 key 定位替换（最精确）
        if (StringUtils.hasText(originalProcessKey)) {
            bpmnXml = bpmnXml.replace("id=\"" + originalProcessKey + "\"",
                    "id=\"" + newProcessKey + "\"");
            // 同步 DI 平面引用，避免 bpmnElement 指向旧 process id
            bpmnXml = bpmnXml.replace("bpmnElement=\"" + originalProcessKey + "\"",
                    "bpmnElement=\"" + newProcessKey + "\"");
        }

        // 如果不是模板（或模板替换未生效），兜底把 <process id="..."> 改成目标 key
        java.util.regex.Pattern p = java.util.regex.Pattern
                .compile("(<process\\b[^>]*\\bid=\\\")([^\\\"]+)(\\\")");
        java.util.regex.Matcher m = p.matcher(bpmnXml);
        if (m.find()) {
            String oldId = m.group(2);
            if (!newProcessKey.equals(oldId)) {
                bpmnXml = m.replaceFirst("$1" + java.util.regex.Matcher.quoteReplacement(newProcessKey) + "$3");
                bpmnXml = bpmnXml.replace("bpmnElement=\"" + oldId + "\"",
                        "bpmnElement=\"" + newProcessKey + "\"");
            }
        }

        return bpmnXml;
    }

    private String loadDefaultThreadProcessSchema() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + ThreadConsts.THREAD_PROCESS_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private ProcessRequest buildDefaultProcessRequest(String uid, String orgUid, String schema,
            ProcessTypeEnum type, String processName) {
        return buildDefaultProcessRequest(uid, orgUid, schema, null, type, processName);
    }

    private ProcessRequest buildDefaultProcessRequest(String uid, String orgUid, String schema, String flowgramSchema,
            ProcessTypeEnum type, String processName) {
        return ProcessRequest.builder()
                .uid(uid)
                .name(processName)
                .schema(schema)
                .flowgramSchema(flowgramSchema)
                .type(type.name())
                .description(processName)
                .orgUid(orgUid)
                .build();
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

    /**
     * 部署流程 - 统一部署入口，供 ProcessService 和内部初始化使用
     * 使用 ProcessEntity 中存储的 schema 进行部署
     * 
     * @param processUid    流程定义 UID
     * @param checkExisting 是否检查已存在的部署（true: 初始化场景，false: 用户手动部署）
     * @return ProcessDefinitionResponse 部署结果
     */
    public ProcessDefinitionResponse deployProcess(String processUid, boolean checkExisting) {
        Optional<ProcessEntity> optional = processRepository.findByUid(processUid);
        if (optional.isEmpty()) {
            throw new RuntimeException("流程定义不存在: " + processUid);
        }

        ProcessEntity processEntity = optional.get();
        String orgUid = processEntity.getOrgUid();
        String bpmnXml = processEntity.getSchema();
        validateProcessBeforeDeploy(processEntity);
        // 统一：部署时将 BPMN <process id> 归一为可运行的 processDefinitionKey
        String processDefinitionKey = FlowableIdUtils.toProcessDefinitionKey(processUid);
        String originalProcessKey;
        if (ProcessTypeEnum.THREAD.name().equalsIgnoreCase(processEntity.getType())) {
            originalProcessKey = ThreadConsts.THREAD_PROCESS_KEY;
        } else if (ProcessTypeEnum.DEMO.name().equalsIgnoreCase(processEntity.getType())) {
            // 根据流程名称判断具体是哪个演示流程
            String name = processEntity.getName();
            if (name.contains(ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_NAME)) {
                originalProcessKey = ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_KEY;
            } else if (name.contains(ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_NAME)) {
                originalProcessKey = ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_KEY;
            } else if (name.contains(ProcessDemoConsts.IT_SUPPORT_PROCESS_NAME)) {
                originalProcessKey = ProcessDemoConsts.IT_SUPPORT_PROCESS_KEY;
            } else {
                originalProcessKey = ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_KEY; // 默认
            }
        } else {
            originalProcessKey = TicketConsts.TICKET_PROCESS_KEY;
        }
        bpmnXml = rewriteBpmnProcessId(bpmnXml, originalProcessKey, processDefinitionKey);
        // 直接使用 processUid 作为 deploymentName，确保唯一性且不受用户修改 name 影响
        String deploymentName = processUid;

        // 如果需要检查已存在的部署（初始化场景）
        if (checkExisting) {
            List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                    .deploymentTenantId(orgUid)
                    .deploymentName(deploymentName)
                    .list();

            if (!existingDeployments.isEmpty()) {
                existingDeployments.sort(Comparator
                        .comparing(Deployment::getDeploymentTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed());
                Deployment latestDeployment = existingDeployments.get(0);
                markProcessAsDeployed(processUid, latestDeployment.getId());
                log.info("使用现有部署: deploymentId={}, deploymentName={}, tenantId={}",
                        latestDeployment.getId(), deploymentName, latestDeployment.getTenantId());

                // 查询流程定义并返回
                org.flowable.engine.repository.ProcessDefinition processDefinition = repositoryService
                        .createProcessDefinitionQuery()
                        .deploymentId(latestDeployment.getId())
                        .singleResult();

                return buildProcessDefinitionResponse(processDefinition);
            }
        }

        // 执行部署操作
        Deployment deployment = repositoryService.createDeployment()
                .name(deploymentName)
                .addString(processUid + ".bpmn20.xml", bpmnXml)
                .tenantId(orgUid)
                .deploy();

        log.info("执行流程部署成功: deploymentId={}, deploymentName={}, processKey={}, tenantId={}",
                deployment.getId(), deploymentName, processUid, deployment.getTenantId());

        // 更新 ProcessEntity 部署状态
        processEntity.setDeploymentId(deployment.getId());
        processEntity.setStatus(ProcessStatusEnum.DEPLOYED.name());
        save(processEntity);

        // 验证部署结果
        org.flowable.engine.repository.ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        log.info("部署流程成功: deploymentId={}, tenantId={}, processKey={}, version={}",
                deployment.getId(), deployment.getTenantId(),
                processDefinition.getKey(), processDefinition.getVersion());

        return buildProcessDefinitionResponse(processDefinition);
    }

    /**
     * 部署流程 - 用户手动触发（不检查已存在部署，会创建新版本）
     */
    public ProcessDefinitionResponse deployProcess(String processUid) {
        return deployProcess(processUid, false);
    }

    /**
     * 查询已部署的流程定义列表（Flowable ProcessDefinition）
     */
    public List<ProcessDefinitionResponse> queryDeployedProcessDefinitions(ProcessRequest request) {
        String orgUid = request.getOrgUid();
        if (orgUid == null) {
            throw new RuntimeException("租户ID不能为空");
        }

        // 先查询已部署的流程定义实体
        List<ProcessEntity> deployedProcesses = processRepository.findByOrgUidAndStatus(
                orgUid, ProcessStatusEnum.DEPLOYED.name());

        // 收集所有部署ID
        Set<String> deploymentIds = deployedProcesses.stream()
                .map(ProcessEntity::getDeploymentId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (deploymentIds.isEmpty()) {
            return List.of();
        }

        // 查询租户流程定义
        List<org.flowable.engine.repository.ProcessDefinition> processList = repositoryService
                .createProcessDefinitionQuery()
                .deploymentIds(deploymentIds)
                .processDefinitionTenantId(orgUid)
                .latestVersion()
                .active()
                .orderByProcessDefinitionVersion().desc()
                .list();

        for (org.flowable.engine.repository.ProcessDefinition processDefinition : processList) {
            log.info("租户流程定义 tenantId={}, name={}, key={}, version={}, deploymentId={}",
                    processDefinition.getTenantId(),
                    processDefinition.getName(),
                    processDefinition.getKey(),
                    processDefinition.getVersion(),
                    processDefinition.getDeploymentId());
        }

        return processList.stream()
                .map(this::buildProcessDefinitionResponse)
                .collect(Collectors.toList());
    }

    /**
     * 取消部署流程
     */
    public List<ProcessDefinitionResponse> undeployProcess(String processUid) {
        Optional<ProcessEntity> optional = processRepository.findByUid(processUid);
        if (optional.isEmpty()) {
            throw new RuntimeException("流程定义不存在: " + processUid);
        }

        ProcessEntity processEntity = optional.get();
        String deploymentId = processEntity.getDeploymentId();

        if (deploymentId == null) {
            log.warn("流程未部署，无需取消部署: processUid={}", processUid);
            return List.of();
        }

        // 先查询要删除的流程定义
        List<org.flowable.engine.repository.ProcessDefinition> processes = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .list();
        log.info("删除前流程版本数量: {}", processes.size());

        try {
            // 级联删除部署（同时清理运行中的流程实例、任务、历史记录等）
            repositoryService.deleteDeployment(deploymentId, true);
            log.info("成功删除流程部署: deploymentId={}", deploymentId);

            // 更新实体状态
            processEntity.setStatus(ProcessStatusEnum.DRAFT.name());
            processEntity.setDeploymentId(null);
            save(processEntity);

        } catch (Exception e) {
            log.error("删除流程部署失败: deploymentId={}, error={}",
                    deploymentId, e.getMessage());
            throw new RuntimeException("删除流程部署失败: " + e.getMessage());
        }

        // 验证删除结果
        List<org.flowable.engine.repository.ProcessDefinition> remainingProcesses = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .list();

        log.info("删除后流程版本数量: {}", remainingProcesses.size());

        return remainingProcesses.stream()
                .map(this::buildProcessDefinitionResponse)
                .collect(Collectors.toList());
    }

    private ProcessDefinitionResponse buildProcessDefinitionResponse(
            org.flowable.engine.repository.ProcessDefinition processDefinition) {
        return ProcessDefinitionResponse.builder()
                .id(processDefinition.getId())
                .key(processDefinition.getKey())
                .name(processDefinition.getName())
                .description(processDefinition.getDescription())
                .version(processDefinition.getVersion())
                .deploymentId(processDefinition.getDeploymentId())
                .tenantId(processDefinition.getTenantId())
                .build();
    }

    public ProcessValidationResponse validateProcess(ProcessRequest request) {
        Optional<ProcessEntity> optional = StringUtils.hasText(request.getUid())
                ? processRepository.findByUid(request.getUid())
                : Optional.empty();
        String processUid = StringUtils.hasText(request.getUid()) ? request.getUid() : null;
        String type = StringUtils.hasText(request.getType())
                ? request.getType()
                : optional.map(ProcessEntity::getType).orElse(ProcessTypeEnum.TICKET_INTERNAL.name());
        String bpmnXml = StringUtils.hasText(request.getSchema())
                ? request.getSchema()
                : optional.map(ProcessEntity::getSchema).orElse(null);
        String flowgramSchema = StringUtils.hasText(request.getFlowgramSchema())
                ? request.getFlowgramSchema()
                : optional.map(ProcessEntity::getFlowgramSchema).orElse(null);

        List<ProcessValidationIssueResponse> errors = new ArrayList<>();
        List<ProcessValidationIssueResponse> warnings = new ArrayList<>();

        if (!StringUtils.hasText(bpmnXml)) {
            addIssue(errors, "ERROR", "BPMN_SCHEMA_REQUIRED", "流程 BPMN schema 不能为空", null);
            return buildValidationResponse(processUid, type, errors, warnings);
        }

        Set<String> bpmnUserTaskIds = new HashSet<>();
        try {
            bpmnUserTaskIds = parseBpmnUserTaskIds(bpmnXml);
        } catch (Exception e) {
            addIssue(errors, "ERROR", "BPMN_PARSE_FAILED", "BPMN XML 解析失败: " + e.getMessage(), null);
        }

        JSONObject flowgram = null;
        JSONArray nodes = null;
        if (!StringUtils.hasText(flowgramSchema)) {
            if (isTicketProcessType(type)) {
                addIssue(errors, "ERROR", "FLOWGRAM_SCHEMA_REQUIRED", "工单流程必须配置 flowgramSchema", null);
            } else {
                addIssue(warnings, "WARN", "FLOWGRAM_SCHEMA_MISSING", "未配置 flowgramSchema，将无法提供可视化流程语义", null);
            }
        } else {
            try {
                flowgram = JSON.parseObject(flowgramSchema);
                nodes = flowgram.getJSONArray("nodes");
                if (nodes == null || nodes.isEmpty()) {
                    addIssue(errors, "ERROR", "FLOWGRAM_NODES_REQUIRED", "flowgramSchema.nodes 不能为空", null);
                }
            } catch (Exception e) {
                addIssue(errors, "ERROR", "FLOWGRAM_PARSE_FAILED", "flowgramSchema JSON 解析失败: " + e.getMessage(), null);
            }
        }

        if (nodes != null && !bpmnUserTaskIds.isEmpty()) {
            validateFlowgramBpmnAlignment(bpmnUserTaskIds, nodes, errors, warnings, isTicketProcessType(type));
            if (isTicketProcessType(type)) {
                validateTicketWorkflowSemantics(nodes, errors, warnings);
            }
        }

        return buildValidationResponse(processUid, type, errors, warnings);
    }

    private void validateProcessBeforeDeploy(ProcessEntity processEntity) {
        ProcessValidationResponse validation = validateProcess(ProcessRequest.builder()
                .uid(processEntity.getUid())
                .type(processEntity.getType())
                .schema(processEntity.getSchema())
                .flowgramSchema(processEntity.getFlowgramSchema())
                .build());
        if (validation.getErrors() != null && !validation.getErrors().isEmpty()) {
            String message = validation.getErrors().stream()
                    .map(ProcessValidationIssueResponse::getMessage)
                    .collect(Collectors.joining("; "));
            throw new RuntimeException("流程校验失败: " + message);
        }
    }

    private ProcessValidationResponse buildValidationResponse(String processUid, String type,
            List<ProcessValidationIssueResponse> errors, List<ProcessValidationIssueResponse> warnings) {
        return ProcessValidationResponse.builder()
                .valid(errors.isEmpty())
                .processUid(processUid)
                .type(type)
                .errors(errors)
                .warnings(warnings)
                .build();
    }

    private Set<String> parseBpmnUserTaskIds(String bpmnXml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        Document document = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        NodeList userTasks = document.getElementsByTagNameNS("*", "userTask");
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < userTasks.getLength(); i++) {
            org.w3c.dom.Node node = userTasks.item(i);
            org.w3c.dom.Node idNode = node.getAttributes().getNamedItem("id");
            if (idNode != null && StringUtils.hasText(idNode.getNodeValue())) {
                ids.add(idNode.getNodeValue());
            }
        }
        return ids;
    }

    private void validateFlowgramBpmnAlignment(Set<String> bpmnUserTaskIds, JSONArray nodes,
            List<ProcessValidationIssueResponse> errors, List<ProcessValidationIssueResponse> warnings,
            boolean strictTicketProcess) {
        Set<String> flowgramTaskNodeIds = new HashSet<>();
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            String nodeId = node.getString("id");
            String nodeType = node.getString("type");
            if (isFlowgramTaskNode(nodeType) && StringUtils.hasText(nodeId)) {
                flowgramTaskNodeIds.add(nodeId);
            }
        }

        for (String taskId : bpmnUserTaskIds) {
            if (!flowgramTaskNodeIds.contains(taskId)) {
                addIssue(strictTicketProcess ? errors : warnings,
                        strictTicketProcess ? "ERROR" : "WARN",
                        "BPMN_TASK_MISSING_FLOWGRAM_NODE",
                        "BPMN userTask 未找到对应 flowgram 节点: " + taskId,
                        taskId);
            }
        }
        for (String nodeId : flowgramTaskNodeIds) {
            if (!bpmnUserTaskIds.contains(nodeId)) {
                addIssue(warnings, "WARN", "FLOWGRAM_NODE_MISSING_BPMN_TASK",
                        "flowgram 任务节点未找到对应 BPMN userTask: " + nodeId,
                        nodeId);
            }
        }
    }

    private void validateTicketWorkflowSemantics(JSONArray nodes,
            List<ProcessValidationIssueResponse> errors, List<ProcessValidationIssueResponse> warnings) {
        boolean hasWaitClaim = false;
        boolean hasProcessing = false;
        Set<String> stages = new HashSet<>();
        List<JSONObject> taskNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            JSONObject data = node.getJSONObject("data");
            if (data == null || !isFlowgramTaskNode(node.getString("type"))) {
                continue;
            }
            taskNodes.add(node);
            String nodeId = node.getString("id");
            String ticketStage = data.getString("ticketStage");
            if (!StringUtils.hasText(ticketStage)) {
                addIssue(warnings, "WARN", "TICKET_STAGE_MISSING",
                        "工单任务节点建议配置 ticketStage，避免依赖节点 id: " + nodeId,
                        nodeId);
            } else {
                stages.add(ticketStage);
                hasWaitClaim = hasWaitClaim || "WAIT_CLAIM".equals(ticketStage);
                hasProcessing = hasProcessing || "PROCESSING".equals(ticketStage);
            }
            validateTicketStatusConfig(nodeId, data, "ticketStatusOnEnter", errors);
            validateTicketStatusConfig(nodeId, data, "ticketStatusOnComplete", errors);
        }

        for (JSONObject node : taskNodes) {
            validateAvailableActions(node.getString("id"), node.getJSONObject("data"), stages, errors, warnings);
        }

        if (hasWaitClaim && !hasProcessing) {
            addIssue(errors, "ERROR", "PROCESSING_STAGE_REQUIRED",
                    "配置了 WAIT_CLAIM 节点，但缺少 PROCESSING 处理节点", null);
        }
        if (!stages.contains("PROCESSING")) {
            addIssue(warnings, "WARN", "PROCESSING_STAGE_MISSING",
                    "工单流程建议至少配置一个 PROCESSING 节点", null);
        }
    }

    private void validateAvailableActions(String nodeId, JSONObject data, Set<String> stages,
            List<ProcessValidationIssueResponse> errors, List<ProcessValidationIssueResponse> warnings) {
        JSONArray actions = data.getJSONArray("availableActions");
        if (actions == null || actions.isEmpty()) {
            addIssue(warnings, "WARN", "AVAILABLE_ACTIONS_MISSING",
                    "工单任务节点未配置 availableActions，将回退到后端默认动作推断: " + nodeId,
                    nodeId);
            return;
        }
        for (int i = 0; i < actions.size(); i++) {
            Object action = actions.get(i);
            JSONObject actionConfig = action instanceof JSONObject ? (JSONObject) action : null;
            String key = actionConfig != null ? actionConfig.getString("key") : String.valueOf(action);
            if (!StringUtils.hasText(key)) {
                addIssue(errors, "ERROR", "ACTION_KEY_REQUIRED", "动作配置缺少 key", nodeId);
                continue;
            }
            String actionType = actionConfig != null ? actionConfig.getString("type") : null;
            if (StringUtils.hasText(actionType) && !supportedTicketActionTypes().contains(actionType)) {
                addIssue(errors, "ERROR", "ACTION_TYPE_UNSUPPORTED", "不支持的工单动作 type: " + actionType, nodeId);
            }
            if (!StringUtils.hasText(actionType) && !supportedTicketActionKeys().contains(key)) {
                addIssue(errors, "ERROR", "ACTION_TYPE_REQUIRED",
                        "自定义工单动作 key 必须配置运行时 type: " + key, nodeId);
            }
            if (actionConfig != null && actionConfig.containsKey("decisionValue")) {
                String decisionVariable = StringUtils.hasText(actionConfig.getString("decisionVariable"))
                        ? actionConfig.getString("decisionVariable")
                        : data.getString("decisionVariable");
                if (!StringUtils.hasText(decisionVariable)) {
                    addIssue(errors, "ERROR", "DECISION_VARIABLE_REQUIRED",
                            "动作 " + key + " 配置了 decisionValue，但节点未配置 decisionVariable", nodeId);
                }
            }
            if (actionConfig != null) {
                validateTicketStatusConfig(nodeId, actionConfig, "ticketStatus", errors);
                validateTicketStatusConfig(nodeId, actionConfig, "ticketStatusOnComplete", errors);
                validateNextTaskStage(nodeId, key, actionConfig, stages, errors);
            }
            validateActionFields(nodeId, key, actionConfig, errors);
        }
    }

    private void validateTicketStatusConfig(String nodeId, JSONObject config, String fieldName,
            List<ProcessValidationIssueResponse> errors) {
        if (config == null || !StringUtils.hasText(config.getString(fieldName))) {
            return;
        }
        String status = config.getString(fieldName).trim().toUpperCase();
        try {
            TicketStatusEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            addIssue(errors, "ERROR", "TICKET_STATUS_UNSUPPORTED",
                    "不支持的工单状态配置 " + fieldName + ": " + config.getString(fieldName), nodeId);
        }
    }

    private void validateNextTaskStage(String nodeId, String actionKey, JSONObject actionConfig, Set<String> stages,
            List<ProcessValidationIssueResponse> errors) {
        String nextTaskStage = actionConfig != null ? actionConfig.getString("nextTaskStage") : null;
        if (!StringUtils.hasText(nextTaskStage) || stages.contains(nextTaskStage)) {
            return;
        }
        addIssue(errors, "ERROR", "NEXT_TASK_STAGE_NOT_FOUND",
                "动作 " + actionKey + " 配置的 nextTaskStage 未找到对应 ticketStage: " + nextTaskStage, nodeId);
    }

    private void validateActionFields(String nodeId, String actionKey, JSONObject actionConfig,
            List<ProcessValidationIssueResponse> errors) {
        JSONArray fields = actionConfig != null ? actionConfig.getJSONArray("fields") : null;
        if (fields == null) {
            return;
        }
        for (int i = 0; i < fields.size(); i++) {
            JSONObject field = fields.getJSONObject(i);
            if (field == null || !StringUtils.hasText(field.getString("name"))) {
                addIssue(errors, "ERROR", "ACTION_FIELD_NAME_REQUIRED",
                        "动作 " + actionKey + " 的字段缺少 name", nodeId);
            }
            if (field == null || !StringUtils.hasText(field.getString("component"))) {
                addIssue(errors, "ERROR", "ACTION_FIELD_COMPONENT_REQUIRED",
                        "动作 " + actionKey + " 的字段缺少 component", nodeId);
            }
        }
    }

    private boolean isTicketProcessType(String type) {
        return ProcessTypeEnum.TICKET_INTERNAL.name().equalsIgnoreCase(type)
                || ProcessTypeEnum.TICKET_EXTERNAL.name().equalsIgnoreCase(type);
    }

    private boolean isFlowgramTaskNode(String nodeType) {
        return "approval".equals(nodeType) || "task".equals(nodeType) || "userTask".equals(nodeType);
    }

    private Set<String> supportedTicketActionKeys() {
        return Set.of("CLAIM", "ASSIGN", "TRANSFER", "TRANSFER_DEPARTMENT", "COMPLETE",
                "COMPLETE_VERIFIED", "COMPLETE_REJECTED", "HOLD", "CLOSE", "DELEGATE",
                "DELEGATE_RESOLVE", "CC", "ADDSIGN", "ROLLBACK", "REVOKE");
    }

    private Set<String> supportedTicketActionTypes() {
        return Set.of("claim", "assign", "transfer", "transferDepartment", "complete", "hold", "close",
                "delegate", "delegateResolve", "cc", "addSign", "rollback", "revoke");
    }

    private void addIssue(List<ProcessValidationIssueResponse> issues, String level, String code,
            String message, String nodeId) {
        issues.add(ProcessValidationIssueResponse.builder()
                .level(level)
                .code(code)
                .message(message)
                .nodeId(nodeId)
                .build());
    }

    private String loadDefaultProcessSchema() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + TicketConsts.TICKET_PROCESS_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String loadDefaultProcessFlowgramSchema() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + TicketConsts.TICKET_PROCESS_FLOWGRAM_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * 重置流程为默认初始化内容
     */
    public ProcessResponse resetProcess(ProcessRequest request) {
        String processUid = request.getUid();
        if (!StringUtils.hasText(processUid)) {
            throw new RuntimeException("Process uid is required");
        }

        Optional<ProcessEntity> optional = processRepository.findByUid(processUid);
        if (optional.isEmpty()) {
            throw new RuntimeException("Process not found: " + processUid);
        }

        ProcessEntity entity = optional.get();
        String type = entity.getType();

        try {
            // 根据类型加载默认流程模板
            String defaultSchema;
            String defaultFlowgramSchema = null;
            // String deploymentName;

            if (ProcessTypeEnum.THREAD.name().equals(type)) {
                // THREAD 类型使用会话流程模板
                defaultSchema = loadDefaultThreadProcessSchema();
                // deploymentName = ThreadConsts.THREAD_PROCESS_NAME;
            } else {
                // TICKET_INTERNAL 和 TICKET_EXTERNAL 类型使用工单流程模板
                defaultSchema = loadDefaultProcessSchema();
                defaultFlowgramSchema = loadDefaultProcessFlowgramSchema();
                // deploymentName = ProcessTypeEnum.TICKET_INTERNAL.name().equals(type)
                // // ? TicketConsts.TICKET_PROCESS_NAME
                // : TicketConsts.TICKET_PROCESS_NAME_EXTERNAL;
            }

            // 更新 ProcessEntity 的 schema、name 和 description
            entity.setSchema(defaultSchema);
            entity.setFlowgramSchema(defaultFlowgramSchema);
            // entity.setName(deploymentName);
            // entity.setDescription(deploymentName);
            // 重置部署状态，需要重新部署
            entity.setDeploymentId(null);
            entity.setStatus(ProcessStatusEnum.DRAFT.name());
            //
            ProcessEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to reset process: unable to save entity");
            }
            log.info("重置流程成功: processUid={}, type={}", processUid, type);

            return convertToResponse(savedEntity);
        } catch (IOException e) {
            log.error("重置流程失败: processUid={}", processUid, e);
            throw new RuntimeException("Failed to reset process: " + e.getMessage());
        }
    }

    /**
     * 初始化演示流程模板
     * 用于展示工单系统的流程能力
     */
    public void initProcessDemos(String orgUid) {
        try {
            // 初始化请假审批流程
            initLeaveApprovalProcess(orgUid);

            // 初始化报销审批流程
            initReimbursementApprovalProcess(orgUid);

            // 初始化IT支持流程
            initItSupportProcess(orgUid);

            log.info("演示流程初始化成功: orgUid={}", orgUid);
        } catch (Exception e) {
            log.error("初始化演示流程失败: orgUid={}", orgUid, e);
        }
    }

    /**
     * 初始化请假审批流程
     */
    private void initLeaveApprovalProcess(String orgUid) throws IOException {
        String processUid = Utils.formatUid(orgUid, ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_KEY);
        String bpmnXml = loadDemoProcessSchema(ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_PATH);
        String flowgramJson = loadDemoProcessSchema(ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_FLOWGRAM_PATH);

        createOrSyncDemoProcess(buildDefaultProcessRequest(
                processUid,
                orgUid,
                bpmnXml,
                flowgramJson,
                ProcessTypeEnum.DEMO,
                ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_NAME));

        deployProcessByType(orgUid, processUid, ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_PATH);
        log.info("创建请假审批流程成功: processUid={}, orgUid={}", processUid, orgUid);
    }

    /**
     * 初始化报销审批流程
     */
    private void initReimbursementApprovalProcess(String orgUid) throws IOException {
        String processUid = Utils.formatUid(orgUid, ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_KEY);
        String bpmnXml = loadDemoProcessSchema(ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_PATH);
        String flowgramJson = loadDemoProcessSchema(ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_FLOWGRAM_PATH);

        createOrSyncDemoProcess(buildDefaultProcessRequest(
                processUid,
                orgUid,
                bpmnXml,
                flowgramJson,
                ProcessTypeEnum.DEMO,
                ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_NAME));

        deployProcessByType(orgUid, processUid, ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_PATH);
        log.info("创建报销审批流程成功: processUid={}, orgUid={}", processUid, orgUid);
    }

    /**
     * 初始化IT支持流程
     */
    private void initItSupportProcess(String orgUid) throws IOException {
        String processUid = Utils.formatUid(orgUid, ProcessDemoConsts.IT_SUPPORT_PROCESS_KEY);
        String bpmnXml = loadDemoProcessSchema(ProcessDemoConsts.IT_SUPPORT_PROCESS_PATH);
        String flowgramJson = loadDemoProcessSchema(ProcessDemoConsts.IT_SUPPORT_PROCESS_FLOWGRAM_PATH);

        createOrSyncDemoProcess(buildDefaultProcessRequest(
                processUid,
                orgUid,
                bpmnXml,
                flowgramJson,
                ProcessTypeEnum.DEMO,
                ProcessDemoConsts.IT_SUPPORT_PROCESS_NAME));

        deployProcessByType(orgUid, processUid, ProcessDemoConsts.IT_SUPPORT_PROCESS_PATH);
        log.info("创建IT支持流程成功: processUid={}, orgUid={}", processUid, orgUid);
    }

    /**
     * 加载演示流程模板文件
     */
    private String loadDemoProcessSchema(String processPath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + processPath);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void createOrSyncDemoProcess(ProcessRequest request) {
        Optional<ProcessEntity> optional = processRepository.findByUid(request.getUid());
        if (optional.isEmpty()) {
            create(request);
            return;
        }

        ProcessEntity entity = optional.get();
        if (StringUtils.hasText(request.getFlowgramSchema())
                && !request.getFlowgramSchema().equals(entity.getFlowgramSchema())) {
            entity.setFlowgramSchema(request.getFlowgramSchema());
            save(entity);
            log.info("同步演示流程 flowgramSchema 成功: processUid={}", entity.getUid());
        }
    }

    /**
     * 迁移脚本：为缺少 flowgramSchema 的旧流程从 resources/processes/*.json 填充 flowgram JSON
     * 在 ProcessInitializer 中调用，确保升级后旧流程可在 TicketBuilder 中打开编辑
     */
    public void migrateFlowgramSchemas() {
        List<ProcessEntity> entities = processRepository.findBySchemaIsNotNullAndFlowgramSchemaIsNull();
        if (entities.isEmpty()) {
            log.info("migrateFlowgramSchemas: no processes need migration");
            return;
        }
        log.info("migrateFlowgramSchemas: migrating {} processes", entities.size());
        int migrated = 0;
        for (ProcessEntity entity : entities) {
            try {
                // 如果 flowgramSchema 已经存在，跳过
                if (StringUtils.hasText(entity.getFlowgramSchema())) {
                    continue;
                }
                String flowgramJson = loadFlowgramResource(entity.getType(), entity.getName());
                if (flowgramJson != null) {
                    entity.setFlowgramSchema(flowgramJson);
                    save(entity);
                    migrated++;
                    log.info("migrateFlowgramSchemas: filled flowgramSchema for processUid={}, type={}",
                            entity.getUid(), entity.getType());
                } else {
                    log.warn("migrateFlowgramSchemas: no flowgram resource found for processUid={}, type={}, name={}",
                            entity.getUid(), entity.getType(), entity.getName());
                }
            } catch (Exception e) {
                log.warn("migrateFlowgramSchemas: failed for processUid={}, error={}",
                        entity.getUid(), e.getMessage());
            }
        }
        log.info("migrateFlowgramSchemas: migrated {}/{} processes", migrated, entities.size());
    }

    /**
     * 根据流程类型和名称加载对应的 flowgram JSON 资源文件
     */
    private String loadFlowgramResource(String type, String name) {
        String resourcePath = resolveFlowgramPath(type, name);
        if (resourcePath == null) {
            return null;
        }
        try {
            Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
            if (!resource.exists()) {
                log.warn("loadFlowgramResource: resource not found at {}", resourcePath);
                return null;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.warn("loadFlowgramResource: failed to load {}, error={}", resourcePath, e.getMessage());
            return null;
        }
    }

    /**
     * 根据流程类型和名称解析对应的 flowgram JSON 资源路径
     */
    private String resolveFlowgramPath(String type, String name) {
        if (type == null) {
            return null;
        }
        // 会话流程
        if (ProcessTypeEnum.THREAD.name().equalsIgnoreCase(type)) {
            return ThreadConsts.THREAD_PROCESS_FLOWGRAM_PATH;
        }
        // 工单流程（内部/外部共用同一个 flowgram）
        if (ProcessTypeEnum.TICKET_INTERNAL.name().equalsIgnoreCase(type)
                || ProcessTypeEnum.TICKET_EXTERNAL.name().equalsIgnoreCase(type)) {
            return TicketConsts.TICKET_PROCESS_FLOWGRAM_PATH;
        }
        // 演示流程：根据名称区分
        if (ProcessTypeEnum.DEMO.name().equalsIgnoreCase(type) && StringUtils.hasText(name)) {
            if (name.contains("请假")) {
                return ProcessDemoConsts.LEAVE_APPROVAL_PROCESS_FLOWGRAM_PATH;
            }
            if (name.contains("报销")) {
                return ProcessDemoConsts.REIMBURSEMENT_APPROVAL_PROCESS_FLOWGRAM_PATH;
            }
            if (name.contains("IT") || name.contains("支持")) {
                return ProcessDemoConsts.IT_SUPPORT_PROCESS_FLOWGRAM_PATH;
            }
        }
        return null;
    }

}
