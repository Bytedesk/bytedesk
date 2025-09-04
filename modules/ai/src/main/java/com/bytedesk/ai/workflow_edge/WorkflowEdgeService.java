/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-04 18:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 18:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_edge;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.ai.workflow.edge.WorkflowEdge;
import com.bytedesk.core.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * 工作流边服务类
 * 提供边的CRUD操作和格式转换功能
 */
@Slf4j
@Service
public class WorkflowEdgeService {

    @Autowired
    private WorkflowEdgeRestService workflowEdgeRestService;

    /**
     * 创建工作流边
     */
    @Transactional
    public WorkflowEdgeEntity createEdge(WorkflowEntity workflow, WorkflowEdge edge) {
        // 检查边UID是否已存在
        if (workflowEdgeRestService.edgeExistsByUid(edge.getId())) {
            throw new IllegalArgumentException("Edge with UID " + edge.getId() + " already exists");
        }

        // 验证连接的节点是否存在
        validateEdgeConnection(workflow, edge.getSourceNodeID(), edge.getTargetNodeID());

        WorkflowEdgeEntity entity = WorkflowEdgeEntity.of(workflow, edge);
        
        return workflowEdgeRestService.createEdge(entity);
    }

    /**
     * 批量创建边（用于导入工作流）
     */
    @Transactional
    public List<WorkflowEdgeEntity> createEdges(WorkflowEntity workflow, List<WorkflowEdge> edges) {
        return edges.stream()
                .map(edge -> createEdge(workflow, edge))
                .collect(Collectors.toList());
    }

    /**
     * 更新边数据
     */
    @Transactional
    public WorkflowEdgeEntity updateEdge(String uid, WorkflowEdge edge) {
        WorkflowEdgeEntity entity = findByUid(uid);
        entity.fromWorkflowEdge(edge);
        return workflowEdgeRestService.updateEdge(entity);
    }

    /**
     * 更新边执行统计
     */
    @Transactional
    public WorkflowEdgeEntity updateExecutionStats(String uid, String result) {
        WorkflowEdgeEntity entity = findByUid(uid);
        entity.updateExecutionStats();
        entity.setExecutionResult(result);
        return workflowEdgeRestService.updateEdge(entity);
    }

    /**
     * 根据边UID查找边
     */
    public WorkflowEdgeEntity findByUid(String uid) {
        WorkflowEdgeEntity entity = workflowEdgeRestService.findEdgeByUid(uid);
        if (entity == null) {
            throw new NotFoundException("WorkflowEdge not found with UID: " + uid);
        }
        return entity;
    }

    /**
     * 查找工作流的所有边
     */
    public List<WorkflowEdgeEntity> findByWorkflow(WorkflowEntity workflow) {
        return workflowEdgeRestService.findByWorkflow(workflow);
    }

    /**
     * 查找工作流的特定类型边
     */
    public List<WorkflowEdgeEntity> findByWorkflowAndType(WorkflowEntity workflow, WorkflowEdgeTypeEnum type) {
        return workflowEdgeRestService.findByWorkflowAndType(workflow, type.getValue());
    }

    /**
     * 查找工作流的启用边
     */
    public List<WorkflowEdgeEntity> findEnabledEdgesByWorkflow(WorkflowEntity workflow) {
        return workflowEdgeRestService.findEnabledEdgesByWorkflow(workflow);
    }

    /**
     * 查找连接指定节点的所有边
     */
    public List<WorkflowEdgeEntity> findEdgesConnectingNode(WorkflowEntity workflow, String nodeId) {
        return workflowEdgeRestService.findEdgesConnectingNode(workflow, nodeId);
    }

    /**
     * 查找从指定节点出发的边
     */
    public List<WorkflowEdgeEntity> findEdgesFromNode(WorkflowEntity workflow, String sourceNodeId) {
        return workflowEdgeRestService.findEdgesFromNode(workflow, sourceNodeId);
    }

    /**
     * 查找到达指定节点的边
     */
    public List<WorkflowEdgeEntity> findEdgesToNode(WorkflowEntity workflow, String targetNodeId) {
        return workflowEdgeRestService.findEdgesToNode(workflow, targetNodeId);
    }

    /**
     * 查找连接两个特定节点的边
     */
    public List<WorkflowEdgeEntity> findEdgesBetweenNodes(WorkflowEntity workflow, String sourceNodeId, String targetNodeId) {
        return workflowEdgeRestService.findEdgesBetweenNodes(workflow, sourceNodeId, targetNodeId);
    }

    /**
     * 转换边实体为WorkflowEdge
     */
    public WorkflowEdge toWorkflowEdge(WorkflowEdgeEntity entity) {
        return entity.toWorkflowEdge();
    }

    /**
     * 转换边实体列表为WorkflowEdge列表
     */
    public List<WorkflowEdge> toWorkflowEdges(List<WorkflowEdgeEntity> entities) {
        return entities.stream()
                .map(this::toWorkflowEdge)
                .collect(Collectors.toList());
    }

    /**
     * 启用/禁用边
     */
    @Transactional
    public WorkflowEdgeEntity toggleEdgeEnabled(String uid, boolean enabled) {
        WorkflowEdgeEntity entity = findByUid(uid);
        entity.setEnabled(enabled);
        return workflowEdgeRestService.updateEdge(entity);
    }

    /**
     * 删除边
     */
    @Transactional
    public void deleteEdge(String uid) {
        WorkflowEdgeEntity entity = findByUid(uid);
        workflowEdgeRestService.deleteEdgeEntity(entity);
    }

    /**
     * 删除连接指定节点的所有边
     */
    @Transactional
    public void deleteEdgesConnectingNode(WorkflowEntity workflow, String nodeId) {
        List<WorkflowEdgeEntity> edges = findEdgesConnectingNode(workflow, nodeId);
        workflowEdgeRestService.deleteAllEdges(edges);
    }

    /**
     * 删除工作流的所有边
     */
    @Transactional
    public void deleteEdgesByWorkflow(WorkflowEntity workflow) {
        List<WorkflowEdgeEntity> edges = findByWorkflow(workflow);
        workflowEdgeRestService.deleteAllEdges(edges);
    }

    /**
     * 复制边（用于模板克隆）
     */
    @Transactional
    public WorkflowEdgeEntity cloneEdge(WorkflowEdgeEntity sourceEdge, WorkflowEntity targetWorkflow, 
                                       String newEdgeUid, String newSourceNodeId, String newTargetNodeId) {
        // 转换为WorkflowEdge后修改ID和连接
        WorkflowEdge workflowEdge = sourceEdge.toWorkflowEdge();
        workflowEdge.setId(newEdgeUid);
        workflowEdge.setSourceNodeID(newSourceNodeId);
        workflowEdge.setTargetNodeID(newTargetNodeId);

        // 创建新的边实体
        return createEdge(targetWorkflow, workflowEdge);
    }

    /**
     * 导出工作流边数据（用于备份或迁移）
     */
    public List<WorkflowEdge> exportWorkflowEdges(WorkflowEntity workflow) {
        List<WorkflowEdgeEntity> entities = findByWorkflow(workflow);
        return toWorkflowEdges(entities);
    }

    /**
     * 导入工作流边数据（用于恢复或迁移）
     */
    @Transactional
    public List<WorkflowEdgeEntity> importWorkflowEdges(WorkflowEntity workflow, List<WorkflowEdge> edges) {
        // 先删除现有边
        deleteEdgesByWorkflow(workflow);
        
        // 创建新边
        return createEdges(workflow, edges);
    }

    /**
     * 获取工作流边的统计信息
     */
    public WorkflowEdgeStats getWorkflowEdgeStats(WorkflowEntity workflow) {
        List<WorkflowEdgeEntity> allEdges = findByWorkflow(workflow);
        
        long totalEdges = allEdges.size();
        long enabledEdges = allEdges.stream().filter(WorkflowEdgeEntity::getEnabled).count();
        long conditionalEdges = allEdges.stream().filter(WorkflowEdgeEntity::isConditionalEdge).count();
        long controlEdges = allEdges.stream().filter(WorkflowEdgeEntity::isControlEdge).count();
        long dataEdges = allEdges.stream().filter(WorkflowEdgeEntity::isDataEdge).count();
        
        return WorkflowEdgeStats.builder()
                .totalEdges(totalEdges)
                .enabledEdges(enabledEdges)
                .disabledEdges(totalEdges - enabledEdges)
                .conditionalEdges(conditionalEdges)
                .controlEdges(controlEdges)
                .dataEdges(dataEdges)
                .build();
    }

    /**
     * 验证边连接的有效性
     */
    private void validateEdgeConnection(WorkflowEntity workflow, String sourceNodeId, String targetNodeId) {
        // 这里可以添加节点存在性验证逻辑
        // 例如检查 WorkflowNodeEntity 中是否存在这些节点
        // 暂时跳过验证，实际使用时可以注入 WorkflowNodeService 进行验证
        log.debug("Validating edge connection from {} to {} in workflow {}", 
                sourceNodeId, targetNodeId, workflow.getUid());
    }

    /**
     * 工作流边统计信息
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowEdgeStats {
        private long totalEdges;
        private long enabledEdges;
        private long disabledEdges;
        private long conditionalEdges;
        private long controlEdges;
        private long dataEdges;
    }
}
