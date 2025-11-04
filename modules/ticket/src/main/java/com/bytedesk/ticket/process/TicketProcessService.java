/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 15:10:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 16:08:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketProcessService {
    
    private final RepositoryService repositoryService;

    private final TicketProcessRepository ticketProcessRepository;

    // 查询流程
    public List<ProcessDefinition> query(TicketProcessRequest request) {
        String orgUid = request.getOrgUid();
        if (orgUid == null) {
            throw new RuntimeException("租户ID不能为空");
        }
        
        // 先查询已部署的流程定义实体
        List<TicketProcessEntity> deployedProcesses = ticketProcessRepository.findByOrgUidAndDeployedTrue(orgUid);
        
        // 收集所有部署ID
        Set<String> deploymentIds = deployedProcesses.stream()
            .map(TicketProcessEntity::getDeploymentId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
        
        if (deploymentIds.isEmpty()) {
            return List.of();
        }

        // 查询租户流程定义
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery()
            .deploymentIds(deploymentIds)           // 按部署ID过滤
            .processDefinitionTenantId(orgUid)      // 按租户ID过滤
            .latestVersion()                        // 只查询最新版本
            .active()                               // 查询激活的流程
            .orderByProcessDefinitionVersion().desc() // 按版本降序排序
            .list();

        for (ProcessDefinition processDefinition : processList) {
            log.info("租户流程定义 tenantId={}, name={}, key={}, version={}, deploymentId={}", 
                processDefinition.getTenantId(),
                processDefinition.getName(),
                processDefinition.getKey(),
                processDefinition.getVersion(),
                processDefinition.getDeploymentId());
        }

        return processList;
    }

    // 部署流程
    public TicketProcessDefinitionResponse deploy(TicketProcessRequest request) {
        Optional<TicketProcessEntity> ticketProcess = ticketProcessRepository.findByUid(request.getUid());
        if (!ticketProcess.isPresent()) {
            throw new RuntimeException("流程定义不存在" + request.getUid());
        }
        String orgUid = ticketProcess.get().getOrgUid();
        String bpmnXml = ticketProcess.get().getSchema();

        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name(request.getName())
                .addString(request.getKey() + ".bpmn20.xml", bpmnXml)
                .tenantId(orgUid)
                .deploy();

        // 更新流程定义
        // ticketProcess.get().setDeployed(true);
        ticketProcess.get().setStatus(TicketProcessStatusEnum.DEPLOYED.name());
        ticketProcess.get().setDeploymentId(deployment.getId());
        ticketProcessRepository.save(ticketProcess.get());
        
        // 验证部署结果
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        
        log.info("部署租户流程成功: deploymentId={}, tenantId={}, processKey={}, version={}", 
            deployment.getId(),
            deployment.getTenantId(),
            processDefinition.getKey(),
            processDefinition.getVersion());

        // 转换为 DTO 返回
        TicketProcessDefinitionResponse dto = TicketProcessDefinitionResponse.builder()
            .id(processDefinition.getId())
            .key(processDefinition.getKey())
            .name(processDefinition.getName())
            .description(processDefinition.getDescription())
            .version(processDefinition.getVersion())
            .deploymentId(processDefinition.getDeploymentId())
            .tenantId(processDefinition.getTenantId())
            .build();

        return dto;
    }

    // 删除流程
    public List<ProcessDefinition> undeploy(TicketProcessRequest request) {

        Optional<TicketProcessEntity> ticketProcess = ticketProcessRepository.findByUid(request.getUid());
        if (!ticketProcess.isPresent()) {
            throw new RuntimeException("流程定义不存在" + request.getUid());
        }
        
        // 获取部署ID
        String deploymentId = ticketProcess.get().getDeploymentId();
        if (deploymentId == null) {
            log.warn("流程未部署，无需取消部署: processUid={}", request.getUid());
            return List.of();
        }

        // 先查询要删除的流程定义
        List<ProcessDefinition> processes = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)  // 使用部署ID查询
                .list();
        log.info("删除前流程版本数量: {}", processes.size());
        
        try {
            // 删除部署
            repositoryService.deleteDeployment(deploymentId, false);
            log.info("成功删除流程部署: deploymentId={}", deploymentId);
            
            // 更新实体状态
            // ticketProcess.get().setDeployed(false);
            ticketProcess.get().setStatus(TicketProcessStatusEnum.DRAFT.name());
            ticketProcess.get().setDeploymentId(null);
            ticketProcessRepository.save(ticketProcess.get());
            
        } catch (Exception e) {
            log.error("删除流程部署失败: deploymentId={}, error={}", 
                deploymentId, e.getMessage());
            throw new RuntimeException("删除流程部署失败: " + e.getMessage());
        }

        // 验证删除结果
        List<ProcessDefinition> remainingProcesses = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .list();
        
        log.info("删除后流程版本数量: {}", remainingProcesses.size());

        return processes;  // 返回删除前的流程定义列表
    }
    
    
}
