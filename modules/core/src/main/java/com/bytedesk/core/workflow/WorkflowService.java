/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-25 09:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 10:07:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.workflow.node.WorkflowBaseNode;
import com.bytedesk.core.workflow.node.WorkflowNodeFactory;
import com.bytedesk.core.workflow.node.WorkflowNodeStatusEnum;
import com.bytedesk.core.workflow_log.WorkflowLogEntity;
import com.bytedesk.core.workflow_log.WorkflowLogRepository;
import com.bytedesk.core.workflow_log.WorkflowLogTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRestService workflowRestService;
    private final WorkflowLogRepository workflowLogRepository;
    private final UidUtils uidUtils;
    private final PlatformTransactionManager transactionManager;

    // execute workflow
    @Transactional
    public WorkflowResponse execute(WorkflowRequest request) {
        log.debug("Executing workflow with UID: {}", request.getUid());
        
        // 1. 获取工作流实体
        Optional<WorkflowEntity> optional = workflowRestService.findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("工作流不存在: " + request.getUid());
        }
        
        WorkflowEntity workflow = optional.get();
        log.debug("Workflow found: {}, type: {}", workflow.getNickname(), workflow.getType());
        
        // 2. 解析工作流内容
        String content = workflow.getSchema();
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("工作流内容为空");
        }
        JSONObject workflowJson = JSON.parseObject(content);
        
        // 3. 确定开始节点
        String currentNodeId = workflow.getCurrentNodeId();
        if (currentNodeId == null || currentNodeId.isEmpty()) {
            // 如果未指定当前节点，则查找开始节点
            currentNodeId = findStartNodeId(workflowJson);
            workflow.setCurrentNodeId(currentNodeId);
            workflowRestService.save(workflow);
        }
        
        String executionUid = uidUtils.getUid();
        AtomicInteger sequence = new AtomicInteger(1);
        List<WorkflowLogEntity> executionLogs = new ArrayList<>();
        
        // 4. 执行工作流逻辑
        try {
            executeWorkflowFromNode(workflow, currentNodeId, request, workflowJson, executionUid, sequence, executionLogs);
            
            // 5. 返回更新后的工作流响应
            return workflowRestService.convertToResponse(workflow);
        } catch (Exception e) {
            log.error("工作流执行失败: {}", e.getMessage(), e);
            throw new RuntimeException("工作流执行失败: " + e.getMessage(), e);
        } finally {
            persistLogs(executionLogs);
        }
    }
    
    /**
     * 查找工作流中的开始节点ID
     * 
     * @param content 工作流JSON内容
     * @return 开始节点的UID
     */
    private String findStartNodeId(JSONObject workflowJson) {
        try {
            JSONArray nodes = workflowJson.getJSONArray("nodes");
            if (nodes != null) {
                for (int i = 0; i < nodes.size(); i++) {
                    JSONObject node = nodes.getJSONObject(i);
                    if ("start".equals(node.getString("type"))) {
                        return node.getString("id");
                    }
                }
            }
            throw new RuntimeException("工作流中未找到开始节点");
        } catch (Exception e) {
            log.error("查找开始节点失败: {}", e.getMessage(), e);
            throw new RuntimeException("查找开始节点失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从指定节点开始执行工作流
     * 
     * @param workflow 工作流实体
     * @param nodeId 当前节点UID
     * @param request 请求参数
     */
    private WorkflowEntity executeWorkflowFromNode(WorkflowEntity workflow, String nodeId, WorkflowRequest request,
            JSONObject workflowJson, String executionUid, AtomicInteger sequence,
            List<WorkflowLogEntity> executionLogs) {
        WorkflowLogEntity logEntry = null;
        long startedAt = System.currentTimeMillis();
        try {
            log.debug("Executing from node: {}", nodeId);
            JSONArray nodes = workflowJson.getJSONArray("nodes");
            JSONArray edges = workflowJson.getJSONArray("edges");
            JSONObject currentNode = findNodeById(nodes, nodeId);

            if (currentNode == null) {
                throw new RuntimeException("未找到节点: " + nodeId);
            }

            WorkflowBaseNode executableNode = WorkflowNodeFactory.parseNode(currentNode);
            if (executableNode == null) {
                throw new RuntimeException("无法解析节点: " + nodeId);
            }

            WorkflowExecutionContext context = WorkflowExecutionContext.builder()
                    .workflow(workflow)
                    .request(request)
                    .workflowJson(workflowJson)
                    .nodes(nodes)
                    .edges(edges)
                    .build();

            logEntry = startNodeLog(workflow, executableNode, request, executionUid, sequence.getAndIncrement());
            executionLogs.add(logEntry);

            WorkflowNodeExecutionResult result = executableNode.execute(context);
            if (result == null) {
                result = WorkflowNodeExecutionResult.success("节点执行完成", null, null);
            }
            finishNodeLog(logEntry, result, startedAt);

            if (result.hasNextNode()) {
                workflow.setCurrentNodeId(result.getNextNodeId());
                workflow = workflowRestService.save(workflow);
                workflow = executeWorkflowFromNode(workflow, result.getNextNodeId(), request, workflowJson, executionUid,
                        sequence, executionLogs);
            }

        } catch (Exception e) {
            if (logEntry != null && WorkflowNodeStatusEnum.PROCESSING.name().equals(logEntry.getNodeStatus())) {
                failNodeLog(logEntry, e, startedAt);
            }
            log.error("执行节点失败: {}", e.getMessage(), e);
            throw new RuntimeException("执行节点失败: " + e.getMessage(), e);
        }
        return workflow;
    }
    
    /**
     * 在节点数组中查找指定ID的节点
     */
    private JSONObject findNodeById(JSONArray nodes, String nodeId) {
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (nodeId.equals(node.getString("id"))) {
                return node;
            }
        }
        return null;
    }

    private WorkflowLogEntity startNodeLog(WorkflowEntity workflow, WorkflowBaseNode node, WorkflowRequest request,
            String executionUid, int sequence) {
        return WorkflowLogEntity.builder()
                .uid(uidUtils.getUid())
                .name(workflow.getNickname())
                .description(node.getDescription())
                .type(WorkflowLogTypeEnum.WORKFLOW.name())
                .workflowUid(workflow.getUid())
                .executionUid(executionUid)
                .sequence(sequence)
                .nodeUid(node.getId())
                .nodeName(StringUtils.hasText(node.getName()) ? node.getName() : node.getId())
                .nodeType(node.getType())
                .nodeStatus(WorkflowNodeStatusEnum.PROCESSING.name())
                .inputPayload(serialize(request))
                .orgUid(resolveOrgUid(workflow, request))
                .userUid(resolveUserUid(workflow, request))
                .build();
    }

    private void finishNodeLog(WorkflowLogEntity logEntity, WorkflowNodeExecutionResult result, long startedAt) {
        if (result.getStatus() != null) {
            logEntity.setNodeStatus(result.getStatus().name());
        } else {
            logEntity.setNodeStatus(WorkflowNodeStatusEnum.SUCCESS.name());
        }
        if (StringUtils.hasText(result.getMessage())) {
            logEntity.setDescription(result.getMessage());
        }
        logEntity.setDurationMs(elapsed(startedAt));
        logEntity.setOutputPayload(serialize(result.getOutputPayload()));
    }

    private void failNodeLog(WorkflowLogEntity logEntity, Exception e, long startedAt) {
        logEntity.setNodeStatus(WorkflowNodeStatusEnum.FAIL.name());
        logEntity.setDurationMs(elapsed(startedAt));
        logEntity.setErrorMessage(e.getMessage());
        logEntity.setErrorStacktrace(buildStackTrace(e));
    }

    private long elapsed(long startedAt) {
        return Math.max(0, System.currentTimeMillis() - startedAt);
    }

    private String serialize(Object payload) {
        if (payload == null) {
            return null;
        }
        if (payload instanceof String) {
            return (String) payload;
        }
        return JSON.toJSONString(payload);
    }

    private String resolveOrgUid(WorkflowEntity workflow, WorkflowRequest request) {
        if (request != null && StringUtils.hasText(request.getOrgUid())) {
            return request.getOrgUid();
        }
        return workflow.getOrgUid();
    }

    private String resolveUserUid(WorkflowEntity workflow, WorkflowRequest request) {
        if (request != null && StringUtils.hasText(request.getUserUid())) {
            return request.getUserUid();
        }
        return workflow.getUserUid();
    }

    private String buildStackTrace(Exception e) {
        if (e == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private void persistLogs(List<WorkflowLogEntity> logs) {
        if (logs == null || logs.isEmpty()) {
            return;
        }
        try {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            template.executeWithoutResult(status -> workflowLogRepository.saveAll(logs));
        } catch (Exception e) {
            log.error("保存工作流执行日志失败: {}", e.getMessage(), e);
        }
    }
}
