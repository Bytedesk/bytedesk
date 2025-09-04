/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 18:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_edge;

import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.ai.workflow.edge.WorkflowEdge;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Index;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import com.alibaba.fastjson2.JSON;
import java.time.ZonedDateTime;

/**
 * WorkflowEdge entity for workflow edge storage and management
 * Stores workflow edge definitions compatible with the edge framework
 * 
 * Database Table: bytedesk_ai_workflow_edge
 * Purpose: Stores workflow edge definitions, connections, and configuration
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({WorkflowEdgeEntityListener.class})
@Table(name = "bytedesk_ai_workflow_edge", indexes = {
    @Index(name = "idx_workflow_edge_workflow_id", columnList = "workflow_id"),
    @Index(name = "idx_workflow_edge_type", columnList = "edge_type"),
    @Index(name = "idx_workflow_edge_source_node", columnList = "source_node_id"),
    @Index(name = "idx_workflow_edge_target_node", columnList = "target_node_id")
})
public class WorkflowEdgeEntity extends BaseEntity {

    /**
     * 边名称
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * 边描述
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 边类型 (DEFAULT, CONDITION, LOOP, DATA, CONTROL, ERROR, etc.)
     */
    @Builder.Default
    @Column(name = "edge_type", nullable = false, length = 32)
    private String type = WorkflowEdgeTypeEnum.DEFAULT.getValue();

    /**
     * 源节点ID
     */
    @Column(name = "source_node_id", nullable = false, length = 64)
    private String sourceNodeId;

    /**
     * 目标节点ID
     */
    @Column(name = "target_node_id", nullable = false, length = 64)
    private String targetNodeId;

    /**
     * 源端口ID（可选，用于多端口节点）
     */
    @Column(name = "source_port_id", length = 64)
    private String sourcePortId;

    /**
     * 目标端口ID（可选，用于多端口节点）
     */
    @Column(name = "target_port_id", length = 64)
    private String targetPortId;

    /**
     * 边的权重或优先级
     */
    @Builder.Default
    @Column(name = "weight")
    private Integer weight = 0;

    /**
     * 条件表达式（用于条件边）
     */
    @Column(name = "condition_expression", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String conditionExpression;

    /**
     * 边完整配置数据（JSON格式存储WorkflowEdge的完整数据）
     * 包含：data(EdgeData), 自定义属性等
     */
    @Column(name = "edge_data", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String edgeData;

    /**
     * 边执行结果数据（JSON格式）
     */
    @Column(name = "execution_result", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String executionResult;

    /**
     * 边最后执行时间
     */
    @Column(name = "last_execution_time")
    private ZonedDateTime lastExecutionTime;

    /**
     * 边执行次数
     */
    @Builder.Default
    @Column(name = "execution_count")
    private Long executionCount = 0L;

    /**
     * 是否启用该边
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    /**
     * 边的样式配置（JSON格式，包含颜色、线型等）
     */
    @Column(name = "style_config", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String styleConfig;

    /**
     * 关联的工作流
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private WorkflowEntity workflow;

    // ==================== 业务方法 ====================

    /**
     * 从JSON数据转换为WorkflowEdge实例
     */
    public WorkflowEdge toWorkflowEdge() {
        if (this.edgeData == null || this.edgeData.trim().isEmpty()) {
            // 如果没有详细数据，创建基础边
            return WorkflowEdge.builder()
                    .id(this.getUid())
                    .name(this.name)
                    .sourceNodeID(this.sourceNodeId)
                    .targetNodeID(this.targetNodeId)
                    .sourcePortID(this.sourcePortId)
                    .targetPortID(this.targetPortId)
                    .build();
        }
        
        try {
            // 解析JSON数据
            WorkflowEdge edge = JSON.parseObject(this.edgeData, WorkflowEdge.class);
            // 确保基本字段正确
            edge.setId(this.getUid());
            edge.setName(this.name);
            edge.setSourceNodeID(this.sourceNodeId);
            edge.setTargetNodeID(this.targetNodeId);
            edge.setSourcePortID(this.sourcePortId);
            edge.setTargetPortID(this.targetPortId);
            return edge;
        } catch (Exception e) {
            // 解析失败时返回基础边
            return WorkflowEdge.builder()
                    .id(this.getUid())
                    .name(this.name)
                    .sourceNodeID(this.sourceNodeId)
                    .targetNodeID(this.targetNodeId)
                    .sourcePortID(this.sourcePortId)
                    .targetPortID(this.targetPortId)
                    .build();
        }
    }

    /**
     * 从WorkflowEdge实例更新实体数据
     */
    public void fromWorkflowEdge(WorkflowEdge edge) {
        this.setUid(edge.getId());
        this.name = edge.getName();
        this.sourceNodeId = edge.getSourceNodeID();
        this.targetNodeId = edge.getTargetNodeID();
        this.sourcePortId = edge.getSourcePortID();
        this.targetPortId = edge.getTargetPortID();
        this.edgeData = edge.toJson();
    }

    /**
     * 更新边执行统计
     */
    public void updateExecutionStats() {
        this.lastExecutionTime = ZonedDateTime.now();
        this.executionCount = (this.executionCount == null ? 0L : this.executionCount) + 1;
    }

    /**
     * 获取边类型枚举
     */
    public WorkflowEdgeTypeEnum getEdgeTypeEnum() {
        try {
            return WorkflowEdgeTypeEnum.fromValue(this.type);
        } catch (IllegalArgumentException e) {
            return WorkflowEdgeTypeEnum.DEFAULT;
        }
    }

    /**
     * 是否为条件边
     */
    public boolean isConditionalEdge() {
        return getEdgeTypeEnum().isConditional();
    }

    /**
     * 是否为控制边
     */
    public boolean isControlEdge() {
        return getEdgeTypeEnum().isControl();
    }

    /**
     * 是否为数据边
     */
    public boolean isDataEdge() {
        return getEdgeTypeEnum().isData();
    }

    /**
     * 检查是否连接指定的两个节点
     */
    public boolean connectsNodes(String sourceNodeId, String targetNodeId) {
        return this.sourceNodeId != null && this.sourceNodeId.equals(sourceNodeId) &&
               this.targetNodeId != null && this.targetNodeId.equals(targetNodeId);
    }

    /**
     * 检查是否连接指定的节点（作为源或目标）
     */
    public boolean connectsNode(String nodeId) {
        return (this.sourceNodeId != null && this.sourceNodeId.equals(nodeId)) ||
               (this.targetNodeId != null && this.targetNodeId.equals(nodeId));
    }

    /**
     * 获取连接的另一个节点ID
     */
    public String getOtherNodeId(String nodeId) {
        if (this.sourceNodeId != null && this.sourceNodeId.equals(nodeId)) {
            return this.targetNodeId;
        } else if (this.targetNodeId != null && this.targetNodeId.equals(nodeId)) {
            return this.sourceNodeId;
        }
        return null;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建边实体的便捷方法
     */
    public static WorkflowEdgeEntity of(WorkflowEntity workflow, WorkflowEdge edge) {
        WorkflowEdgeEntity entity = WorkflowEdgeEntity.builder()
                .workflow(workflow)
                .uid(edge.getId())
                .name(edge.getName())
                .sourceNodeId(edge.getSourceNodeID())
                .targetNodeId(edge.getTargetNodeID())
                .sourcePortId(edge.getSourcePortID())
                .targetPortId(edge.getTargetPortID())
                .edgeData(edge.toJson())
                .build();
        return entity;
    }

    /**
     * 创建简单边实体
     */
    public static WorkflowEdgeEntity of(WorkflowEntity workflow, String edgeUid, String name, 
                                       String sourceNodeId, String targetNodeId, WorkflowEdgeTypeEnum type) {
        return WorkflowEdgeEntity.builder()
                .workflow(workflow)
                .uid(edgeUid)
                .name(name)
                .sourceNodeId(sourceNodeId)
                .targetNodeId(targetNodeId)
                .type(type.getValue())
                .build();
    }

    /**
     * 创建条件边实体
     */
    public static WorkflowEdgeEntity ofCondition(WorkflowEntity workflow, String edgeUid, String name,
                                                String sourceNodeId, String targetNodeId, String condition) {
        return WorkflowEdgeEntity.builder()
                .workflow(workflow)
                .uid(edgeUid)
                .name(name)
                .sourceNodeId(sourceNodeId)
                .targetNodeId(targetNodeId)
                .type(WorkflowEdgeTypeEnum.CONDITION.getValue())
                .conditionExpression(condition)
                .build();
    }

}
