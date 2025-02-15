/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 15:10:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-15 15:17:26
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

    // 查询流程
    public List<ProcessDefinition> query(TicketProcessRequest request) {
        String orgUid = request.getOrgUid();
        // 查询租户流程定义
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionTenantId(orgUid)          // 按租户ID过滤
                .latestVersion()                           // 只查询最新版本
                .active()                                  // 查询激活的流程
                .orderByProcessDefinitionVersion().desc()  // 按版本降序排序
                .list();

        for (ProcessDefinition processDefinition : processList) {
            log.info("租户流程定义 tenantId={}, name={}, key={}, version={}", 
                processDefinition.getTenantId(),
                processDefinition.getName(),
                processDefinition.getKey(),
                processDefinition.getVersion());
        }

        return processList;
    }

    // 部署流程
    public ProcessDefinition deploy(TicketProcessRequest request) {
        // 部署流程
        String orgUid = request.getOrgUid();
        // BPMN流程定义XML字符串
        String bpmnXml = request.getContent();

        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name(request.getName())
                .addString(request.getKey() + ".bpmn20.xml", bpmnXml)
                .tenantId(orgUid)
                .deploy();
        
        // 验证部署结果
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        
        log.info("部署租户流程成功: deploymentId={}, tenantId={}, processKey={}, version={}", 
            deployment.getId(),
            deployment.getTenantId(),
            processDefinition.getKey(),
            processDefinition.getVersion());

        return processDefinition;
    }

    // 删除流程
    public List<ProcessDefinition> delete(TicketProcessRequest request) {
        // 删除流程
        String orgUid = request.getOrgUid();
        String processKey = request.getKey();
        
        // 查询指定租户的所有版本流程定义
        List<ProcessDefinition> processes = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .processDefinitionTenantId(orgUid)
                .orderByProcessDefinitionVersion().desc()
                .list();
        
        log.info("删除前流程版本数量: {}", processes.size());
        
        for (ProcessDefinition pd : processes) {
            log.info("流程定义: deploymentId={}, version={}", pd.getDeploymentId(), pd.getVersion());
            
            try {
                // 第一种方式：级联删除，会删除和当前规则相关的所有信息，包括历史
                // repositoryService.deleteDeployment(pd.getDeploymentId(), true);
                
                // 第二种方式：非级联删除，如果当前规则下有正在执行的流程，则抛出异常
                repositoryService.deleteDeployment(pd.getDeploymentId(), false);
                
                log.info("成功删除流程定义: deploymentId={}", pd.getDeploymentId());
            } catch (Exception e) {
                log.error("删除流程定义失败: deploymentId={}, error={}", 
                    pd.getDeploymentId(), e.getMessage());
            }
        }
        
        // 验证删除结果
        List<ProcessDefinition> remainingProcesses = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .processDefinitionTenantId(orgUid)
                .orderByProcessDefinitionVersion().desc()
                .list();
        
        log.info("删除后流程版本数量: {}", remainingProcesses.size());

        return remainingProcesses;
    }
    
    
}
