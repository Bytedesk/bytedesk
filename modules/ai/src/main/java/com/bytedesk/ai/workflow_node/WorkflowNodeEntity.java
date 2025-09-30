/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 13:53:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_node;

import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.ai.workflow.node.WorkflowBaseNode;
import com.bytedesk.ai.workflow.node.WorkflowNodeFactory;
import com.bytedesk.ai.workflow.node.WorkflowNodeTypeEnum;
import com.bytedesk.ai.workflow.node.WorkflowNodeStatusEnum;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.utils.BdDateUtils;

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

/**
 * WorkflowNode entity for workflow node storage and management
 * Stores workflow node definitions compatible with the node framework
 * 
 * Database Table: bytedesk_ai_workflow_node
 * Purpose: Stores workflow node definitions, execution states, and configuration
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({WorkflowNodeEntityListener.class})
@Table(name = "bytedesk_ai_workflow_node", indexes = {
    @Index(name = "idx_workflow_node_workflow_id", columnList = "workflow_id"),
    @Index(name = "idx_workflow_node_type", columnList = "node_type"),
    @Index(name = "idx_workflow_node_status", columnList = "node_status")
})
public class WorkflowNodeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 节点名称
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * 节点描述
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 节点类型 (START, END, CONDITION, LOOP, LLM, TEXT, COMMENT, GROUP)
     */
    @Builder.Default
    @Column(name = "node_type", nullable = false, length = 32)
    private String type = WorkflowNodeTypeEnum.START.getValue();

    /**
     * 节点状态 (IDLE, RUNNING, SUCCESS, FAILED, WAITING)
     */
    @Builder.Default
    @Column(name = "node_status", nullable = false, length = 32)
    private String status = WorkflowNodeStatusEnum.IDLE.name();

    /**
     * 节点在工作流中的执行顺序
     */
    @Builder.Default
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 父节点UID（用于组织节点层次结构）
     */
    @Column(name = "parent_node_uid", length = 64)
    private String parentNodeUid;

    /**
     * 节点完整配置数据（JSON格式存储WorkflowBaseNode及其子类的完整数据）
     * 包含：data(NodeData), meta(WorkflowNodeMeta), 特定节点类型字段等
     */
    @Column(name = "node_data", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String nodeData;

    /**
     * 节点执行结果数据（JSON格式）
     */
    @Column(name = "execution_result", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String executionResult;

    /**
     * 节点执行开始时间
     */
    @Column(name = "execution_start_time")
    private java.time.ZonedDateTime executionStartTime;

    /**
     * 节点执行结束时间
     */
    @Column(name = "execution_end_time")
    private java.time.ZonedDateTime executionEndTime;

    /**
     * 节点执行错误信息
     */
    @Column(name = "error_message", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String errorMessage;

    /**
     * 是否启用该节点
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    /**
     * 关联的工作流
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private WorkflowEntity workflow;

    // ==================== 业务方法 ====================

    /**
     * 从JSON数据转换为WorkflowBaseNode实例
     */
    public WorkflowBaseNode toWorkflowNode() {
        if (this.nodeData == null || this.nodeData.trim().isEmpty()) {
            // 如果没有详细数据，创建基础节点
            return WorkflowNodeFactory.createNode(this.type, this.getUid(), this.name);
        }
        
        try {
            // 根据节点类型解析JSON数据
            return parseNodeData(this.nodeData, this.type);
        } catch (Exception e) {
            // 解析失败时返回基础节点
            WorkflowBaseNode node = WorkflowNodeFactory.createNode(this.type, this.getUid(), this.name);
            node.setDescription(this.description);
            return node;
        }
    }

    /**
     * 从WorkflowBaseNode实例更新实体数据
     */
    public void fromWorkflowNode(WorkflowBaseNode node) {
        this.setUid(node.getId());
        this.name = node.getName();
        this.description = node.getDescription();
        this.type = node.getType();
        this.status = node.getStatus();
        this.nodeData = node.toJson();
    }

    /**
     * 更新节点执行状态
     */
    public void updateExecutionStatus(WorkflowNodeStatusEnum status, String result, String error) {
        this.status = status.name();
        this.executionResult = result;
        this.errorMessage = error;
        
        if (status == WorkflowNodeStatusEnum.PROCESSING) {
            this.executionStartTime = java.time.ZonedDateTime.now();
        } else if (status == WorkflowNodeStatusEnum.SUCCESS || status == WorkflowNodeStatusEnum.FAIL) {
            this.executionEndTime = java.time.ZonedDateTime.now();
        }
    }

    /**
     * 获取节点类型枚举
     */
    public WorkflowNodeTypeEnum getNodeTypeEnum() {
        return WorkflowNodeTypeEnum.fromValue(this.type);
    }

    /**
     * 获取节点状态枚举
     */
    public WorkflowNodeStatusEnum getNodeStatusEnum() {
        try {
            return WorkflowNodeStatusEnum.valueOf(this.status);
        } catch (IllegalArgumentException e) {
            return WorkflowNodeStatusEnum.IDLE;
        }
    }

    /**
     * 是否为容器节点（GROUP, LOOP）
     */
    public boolean isContainerNode() {
        WorkflowNodeTypeEnum typeEnum = getNodeTypeEnum();
        return typeEnum == WorkflowNodeTypeEnum.GROUP || typeEnum == WorkflowNodeTypeEnum.LOOP;
    }

    /**
     * 是否为控制节点（START, END, CONDITION）
     */
    public boolean isControlNode() {
        WorkflowNodeTypeEnum typeEnum = getNodeTypeEnum();
        return typeEnum == WorkflowNodeTypeEnum.START || 
               typeEnum == WorkflowNodeTypeEnum.END || 
               typeEnum == WorkflowNodeTypeEnum.CONDITION;
    }

    /**
     * 是否为处理节点（LLM, TEXT）
     */
    public boolean isProcessingNode() {
        WorkflowNodeTypeEnum typeEnum = getNodeTypeEnum();
        return typeEnum == WorkflowNodeTypeEnum.LLM || typeEnum == WorkflowNodeTypeEnum.TEXT;
    }

    /**
     * 获取执行开始时间的字符串格式
     */
    public String getExecutionStartTimeString() {
        return BdDateUtils.formatDatetimeToString(executionStartTime);
    }

    /**
     * 获取执行结束时间的字符串格式
     */
    public String getExecutionEndTimeString() {
        return BdDateUtils.formatDatetimeToString(executionEndTime);
    }

    /**
     * 获取执行耗时（毫秒）
     */
    public Long getExecutionDurationMillis() {
        if (executionStartTime == null || executionEndTime == null) {
            return null;
        }
        return java.time.Duration.between(executionStartTime, executionEndTime).toMillis();
    }

    /**
     * 获取执行耗时（秒）
     */
    public Double getExecutionDurationSeconds() {
        Long millis = getExecutionDurationMillis();
        return millis != null ? millis / 1000.0 : null;
    }

    // ==================== 私有方法 ====================

    /**
     * 根据节点类型解析JSON数据
     */
    private WorkflowBaseNode parseNodeData(String jsonData, String nodeType) {
        WorkflowNodeTypeEnum type = WorkflowNodeTypeEnum.fromValue(nodeType);
        
        switch (type) {
            case START:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowStartNode.class);
            case END:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowEndNode.class);
            case CONDITION:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowConditionNode.class);
            case LOOP:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowLoopNode.class);
            case LLM:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowLLMNode.class);
            case TEXT:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowTextNode.class);
            case COMMENT:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowCommentNode.class);
            case GROUP:
                return JSON.parseObject(jsonData, com.bytedesk.ai.workflow.node.WorkflowGroupNode.class);
            default:
                return JSON.parseObject(jsonData, WorkflowBaseNode.class);
        }
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建节点实体的便捷方法
     */
    public static WorkflowNodeEntity of(WorkflowEntity workflow, WorkflowBaseNode node) {
        WorkflowNodeEntity entity = WorkflowNodeEntity.builder()
                .workflow(workflow)
                .uid(node.getId())
                .name(node.getName())
                .description(node.getDescription())
                .type(node.getType())
                .status(node.getStatus())
                .nodeData(node.toJson())
                .build();
        return entity;
    }

    /**
     * 创建简单节点实体
     */
    public static WorkflowNodeEntity of(WorkflowEntity workflow, String nodeUid, String name, WorkflowNodeTypeEnum type) {
        return WorkflowNodeEntity.builder()
                .workflow(workflow)
                .uid(nodeUid)
                .name(name)
                .type(type.getValue())
                .build();
    }

}
