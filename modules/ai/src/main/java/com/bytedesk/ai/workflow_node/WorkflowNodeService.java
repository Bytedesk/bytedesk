/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-04 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 11:08:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_node;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.ai.workflow.node.WorkflowBaseNode;
import com.bytedesk.ai.workflow.node.WorkflowNodeStatusEnum;
import com.bytedesk.ai.workflow.node.WorkflowNodeTypeEnum;
import com.bytedesk.core.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * 工作流节点服务类
 * 提供节点的CRUD操作和格式转换功能
 */
@Slf4j
@Service
public class WorkflowNodeService {

    @Autowired
    private WorkflowNodeRestService workflowNodeRestService;

    /**
     * 创建工作流节点
     */
    @Transactional
    public WorkflowNodeEntity createNode(WorkflowEntity workflow, WorkflowBaseNode node) {
        // 检查节点UID是否已存在
        if (workflowNodeRestService.nodeExistsByUid(node.getId())) {
            throw new IllegalArgumentException("Node with UID " + node.getId() + " already exists");
        }

        WorkflowNodeEntity entity = WorkflowNodeEntity.of(workflow, node);
        
        // 如果是START节点，设置为启用状态
        if (entity.getNodeTypeEnum() == WorkflowNodeTypeEnum.START) {
            entity.setEnabled(true);
        }

        return workflowNodeRestService.createNode(entity);
    }

    /**
     * 批量创建节点（用于导入工作流）
     */
    @Transactional
    public List<WorkflowNodeEntity> createNodes(WorkflowEntity workflow, List<WorkflowBaseNode> nodes) {
        return nodes.stream()
                .map(node -> createNode(workflow, node))
                .collect(Collectors.toList());
    }

    /**
     * 更新节点数据
     */
    @Transactional
    public WorkflowNodeEntity updateNode(String uid, WorkflowBaseNode node) {
        WorkflowNodeEntity entity = findByUid(uid);
        entity.fromWorkflowNode(node);
        return workflowNodeRestService.updateNode(entity);
    }

    /**
     * 更新节点执行状态
     */
    @Transactional
    public WorkflowNodeEntity updateNodeStatus(String uid, WorkflowNodeStatusEnum status, 
                                             String result, String error) {
        WorkflowNodeEntity entity = findByUid(uid);
        entity.updateExecutionStatus(status, result, error);
        return workflowNodeRestService.updateNode(entity);
    }

    /**
     * 根据节点UID查找节点
     */
    public WorkflowNodeEntity findByUid(String uid) {
        WorkflowNodeEntity entity = workflowNodeRestService.findByUidAndDeletedFalse(uid);
        if (entity == null) {
            throw new NotFoundException("WorkflowNode not found with UID: " + uid);
        }
        return entity;
    }

    /**
     * 查找工作流的所有节点（按排序字段升序）
     */
    public List<WorkflowNodeEntity> findByWorkflow(WorkflowEntity workflow) {
        return workflowNodeRestService.findByWorkflowOrderBySortOrderAsc(workflow);
    }

    /**
     * 查找工作流的特定类型节点
     */
    public List<WorkflowNodeEntity> findByWorkflowAndType(WorkflowEntity workflow, WorkflowNodeTypeEnum type) {
        return workflowNodeRestService.findByWorkflowAndType(workflow, type.getValue());
    }

    /**
     * 查找工作流的启用节点
     */
    public List<WorkflowNodeEntity> findEnabledNodesByWorkflow(WorkflowEntity workflow) {
        return workflowNodeRestService.findByWorkflowAndEnabledTrueOrderBySortOrderAsc(workflow);
    }

    /**
     * 转换节点实体为WorkflowBaseNode
     */
    public WorkflowBaseNode toWorkflowNode(WorkflowNodeEntity entity) {
        return entity.toWorkflowNode();
    }

    /**
     * 转换节点实体列表为WorkflowBaseNode列表
     */
    public List<WorkflowBaseNode> toWorkflowNodes(List<WorkflowNodeEntity> entities) {
        return entities.stream()
                .map(this::toWorkflowNode)
                .collect(Collectors.toList());
    }

    /**
     * 获取工作流的开始节点
     */
    public Optional<WorkflowNodeEntity> findStartNode(WorkflowEntity workflow) {
        List<WorkflowNodeEntity> startNodes = findByWorkflowAndType(workflow, WorkflowNodeTypeEnum.START);
        return startNodes.isEmpty() ? Optional.empty() : Optional.of(startNodes.get(0));
    }

    /**
     * 获取工作流的结束节点
     */
    public List<WorkflowNodeEntity> findEndNodes(WorkflowEntity workflow) {
        return findByWorkflowAndType(workflow, WorkflowNodeTypeEnum.END);
    }

    /**
     * 获取处理中的节点
     */
    public List<WorkflowNodeEntity> findProcessingNodes(WorkflowEntity workflow) {
        return workflowNodeRestService.findByWorkflowAndStatus(workflow, WorkflowNodeStatusEnum.PROCESSING.name());
    }

    /**
     * 启用/禁用节点
     */
    @Transactional
    public WorkflowNodeEntity toggleNodeEnabled(String uid, boolean enabled) {
        WorkflowNodeEntity entity = findByUid(uid);
        entity.setEnabled(enabled);
        return workflowNodeRestService.updateNode(entity);
    }

    /**
     * 删除节点
     */
    @Transactional
    public void deleteNode(String uid) {
        WorkflowNodeEntity entity = findByUid(uid);
        workflowNodeRestService.deleteNodeEntity(entity);
    }

    /**
     * 删除工作流的所有节点
     */
    @Transactional
    public void deleteNodesByWorkflow(WorkflowEntity workflow) {
        List<WorkflowNodeEntity> nodes = findByWorkflow(workflow);
        workflowNodeRestService.deleteAllNodes(nodes);
    }

    /**
     * 复制节点（用于模板克隆）
     */
    @Transactional
    public WorkflowNodeEntity cloneNode(WorkflowNodeEntity sourceNode, WorkflowEntity targetWorkflow, String newNodeUid) {
        // 转换为WorkflowBaseNode后修改ID
        WorkflowBaseNode workflowNode = sourceNode.toWorkflowNode();
        workflowNode.setId(newNodeUid);

        // 创建新的节点实体
        return createNode(targetWorkflow, workflowNode);
    }

    /**
     * 重置节点执行状态（用于重新执行工作流）
     */
    @Transactional
    public void resetExecutionStatus(WorkflowEntity workflow) {
        List<WorkflowNodeEntity> nodes = findByWorkflow(workflow);
        nodes.forEach(node -> {
            node.setStatus(WorkflowNodeStatusEnum.IDLE.name());
            node.setExecutionResult(null);
            node.setErrorMessage(null);
            node.setExecutionStartTime(null);
            node.setExecutionEndTime(null);
        });
        workflowNodeRestService.saveAllNodes(nodes);
    }

    /**
     * 导出工作流节点数据（用于备份或迁移）
     */
    public List<WorkflowBaseNode> exportWorkflowNodes(WorkflowEntity workflow) {
        List<WorkflowNodeEntity> entities = findByWorkflow(workflow);
        return toWorkflowNodes(entities);
    }

    /**
     * 导入工作流节点数据（用于恢复或迁移）
     */
    @Transactional
    public List<WorkflowNodeEntity> importWorkflowNodes(WorkflowEntity workflow, List<WorkflowBaseNode> nodes) {
        // 先删除现有节点
        deleteNodesByWorkflow(workflow);
        
        // 创建新节点
        return createNodes(workflow, nodes);
    }

    /**
     * 获取工作流节点的统计信息
     */
    public WorkflowNodeStats getWorkflowNodeStats(WorkflowEntity workflow) {
        List<WorkflowNodeEntity> allNodes = findByWorkflow(workflow);
        
        long totalNodes = allNodes.size();
        long enabledNodes = allNodes.stream().filter(WorkflowNodeEntity::getEnabled).count();
        long startNodes = allNodes.stream().filter(node -> 
                node.getNodeTypeEnum() == WorkflowNodeTypeEnum.START).count();
        long endNodes = allNodes.stream().filter(node -> 
                node.getNodeTypeEnum() == WorkflowNodeTypeEnum.END).count();
        long processingNodes = allNodes.stream().filter(node -> 
                WorkflowNodeStatusEnum.PROCESSING.name().equals(node.getStatus())).count();
        
        return WorkflowNodeStats.builder()
                .totalNodes(totalNodes)
                .enabledNodes(enabledNodes)
                .disabledNodes(totalNodes - enabledNodes)
                .startNodes(startNodes)
                .endNodes(endNodes)
                .processingNodes(processingNodes)
                .build();
    }

    /**
     * 工作流节点统计信息
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowNodeStats {
        private long totalNodes;
        private long enabledNodes;
        private long disabledNodes;
        private long startNodes;
        private long endNodes;
        private long processingNodes;
    }
}
