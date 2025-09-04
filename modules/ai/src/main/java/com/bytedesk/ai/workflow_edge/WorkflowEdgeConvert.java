/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-04 18:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 18:30:00
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

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

/**
 * WorkflowEdge converter utility class
 * Handles conversion between Entity, Request, and Response objects
 */
public class WorkflowEdgeConvert {

    /**
     * Convert WorkflowEdgeEntity to WorkflowEdgeResponse
     */
    public static WorkflowEdgeResponse toResponse(WorkflowEdgeEntity entity) {
        if (entity == null) {
            return null;
        }

        WorkflowEdgeResponse response = WorkflowEdgeResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .edgeType(entity.getType())
                .sourceNodeId(entity.getSourceNodeId())
                .targetNodeId(entity.getTargetNodeId())
                .sourcePortId(entity.getSourcePortId())
                .targetPortId(entity.getTargetPortId())
                .weight(entity.getWeight())
                .conditionExpression(entity.getConditionExpression())
                .edgeData(entity.getEdgeData())
                .executionResult(entity.getExecutionResult())
                .lastExecutionTime(entity.getLastExecutionTime())
                .executionCount(entity.getExecutionCount())
                .enabled(entity.getEnabled())
                .styleConfig(entity.getStyleConfig())
                .workflowUid(entity.getWorkflow() != null ? entity.getWorkflow().getUid() : null)
                .isConditionalEdge(entity.isConditionalEdge())
                .isControlEdge(entity.isControlEdge())
                .isDataEdge(entity.isDataEdge())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        return response;
    }

    /**
     * Convert WorkflowEdgeRequest to WorkflowEdgeEntity
     */
    public static WorkflowEdgeEntity toEntity(WorkflowEdgeRequest request, WorkflowEntity workflow) {
        if (request == null) {
            return null;
        }

        WorkflowEdgeEntity entity = WorkflowEdgeEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getEdgeType())
                .sourceNodeId(request.getSourceNodeId())
                .targetNodeId(request.getTargetNodeId())
                .sourcePortId(request.getSourcePortId())
                .targetPortId(request.getTargetPortId())
                .weight(request.getWeight())
                .conditionExpression(request.getConditionExpression())
                .edgeData(request.getEdgeData())
                .executionResult(request.getExecutionResult())
                .enabled(request.getEnabled())
                .styleConfig(request.getStyleConfig())
                .workflow(workflow)
                .build();

        // Set UID if provided
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        }

        return entity;
    }

    /**
     * Update WorkflowEdgeEntity from WorkflowEdgeRequest
     */
    public static void updateEntity(WorkflowEdgeEntity entity, WorkflowEdgeRequest request) {
        if (entity == null || request == null) {
            return;
        }

        // Update basic fields
        if (StringUtils.hasText(request.getName())) {
            entity.setName(request.getName());
        }
        
        if (StringUtils.hasText(request.getDescription())) {
            entity.setDescription(request.getDescription());
        }
        
        if (StringUtils.hasText(request.getEdgeType())) {
            entity.setType(request.getEdgeType());
        }
        
        if (StringUtils.hasText(request.getSourceNodeId())) {
            entity.setSourceNodeId(request.getSourceNodeId());
        }
        
        if (StringUtils.hasText(request.getTargetNodeId())) {
            entity.setTargetNodeId(request.getTargetNodeId());
        }
        
        if (StringUtils.hasText(request.getSourcePortId())) {
            entity.setSourcePortId(request.getSourcePortId());
        }
        
        if (StringUtils.hasText(request.getTargetPortId())) {
            entity.setTargetPortId(request.getTargetPortId());
        }
        
        if (request.getWeight() != null) {
            entity.setWeight(request.getWeight());
        }
        
        if (StringUtils.hasText(request.getConditionExpression())) {
            entity.setConditionExpression(request.getConditionExpression());
        }
        
        if (StringUtils.hasText(request.getEdgeData())) {
            entity.setEdgeData(request.getEdgeData());
        }
        
        if (StringUtils.hasText(request.getExecutionResult())) {
            entity.setExecutionResult(request.getExecutionResult());
        }
        
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }
        
        if (StringUtils.hasText(request.getStyleConfig())) {
            entity.setStyleConfig(request.getStyleConfig());
        }
    }

    /**
     * Convert WorkflowEdge to WorkflowEdgeResponse
     */
    public static WorkflowEdgeResponse toResponse(WorkflowEdge edge, String workflowUid) {
        if (edge == null) {
            return null;
        }

        return WorkflowEdgeResponse.builder()
                .uid(edge.getId())
                .name(edge.getName())
                .description(edge.getDescription())
                .edgeType(edge.getType())
                .sourceNodeId(edge.getSourceNodeID())
                .targetNodeId(edge.getTargetNodeID())
                .sourcePortId(edge.getSourcePortID())
                .targetPortId(edge.getTargetPortID())
                .weight(edge.getWeight())
                .conditionExpression(edge.getConditionExpression())
                .edgeData(edge.toJson())
                .workflowUid(workflowUid)
                .enabled(true)
                .build();
    }

    /**
     * Convert WorkflowEdgeRequest to WorkflowEdge
     */
    public static WorkflowEdge toWorkflowEdge(WorkflowEdgeRequest request) {
        if (request == null) {
            return null;
        }

        // Create edge from request data
        WorkflowEdge edge = WorkflowEdge.builder()
                .id(StringUtils.hasText(request.getUid()) ? 
                    request.getUid() : java.util.UUID.randomUUID().toString())
                .name(StringUtils.hasText(request.getName()) ? 
                      request.getName() : "Unnamed Edge")
                .description(request.getDescription())
                .type(request.getEdgeType())
                .sourceNodeID(request.getSourceNodeId())
                .targetNodeID(request.getTargetNodeId())
                .sourcePortID(request.getSourcePortId())
                .targetPortID(request.getTargetPortId())
                .weight(request.getWeight())
                .conditionExpression(request.getConditionExpression())
                .label(request.getName())
                .build();

        return edge;
    }

    /**
     * Get WorkflowEdgeTypeEnum from string value
     */
    public static WorkflowEdgeTypeEnum getEdgeTypeEnum(String type) {
        if (!StringUtils.hasText(type)) {
            return WorkflowEdgeTypeEnum.DEFAULT;
        }
        
        try {
            return WorkflowEdgeTypeEnum.fromValue(type);
        } catch (Exception e) {
            return WorkflowEdgeTypeEnum.DEFAULT;
        }
    }

    /**
     * Validate edge type
     */
    public static boolean isValidEdgeType(String type) {
        if (!StringUtils.hasText(type)) {
            return false;
        }
        
        try {
            WorkflowEdgeTypeEnum.fromValue(type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Convert WorkflowEdgeEntity to WorkflowEdgeRequest
     */
    public static WorkflowEdgeRequest toRequest(WorkflowEdgeEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkflowEdgeRequest.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .edgeType(entity.getType())
                .sourceNodeId(entity.getSourceNodeId())
                .targetNodeId(entity.getTargetNodeId())
                .sourcePortId(entity.getSourcePortId())
                .targetPortId(entity.getTargetPortId())
                .weight(entity.getWeight())
                .conditionExpression(entity.getConditionExpression())
                .edgeData(entity.getEdgeData())
                .executionResult(entity.getExecutionResult())
                .enabled(entity.getEnabled())
                .styleConfig(entity.getStyleConfig())
                .workflowUid(entity.getWorkflow() != null ? entity.getWorkflow().getUid() : null)
                .build();
    }

    /**
     * Copy properties from request to entity (excluding null values)
     */
    public static void copyPropertiesIgnoreNull(WorkflowEdgeRequest source, WorkflowEdgeEntity target) {
        if (source == null || target == null) {
            return;
        }

        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        
        // Handle special field mappings
        if (StringUtils.hasText(source.getEdgeType())) {
            target.setType(source.getEdgeType());
        }
    }

    /**
     * Get null property names from object
     */
    private static String[] getNullPropertyNames(Object source) {
        final java.util.List<String> nullProperties = new java.util.ArrayList<>();
        
        if (source instanceof WorkflowEdgeRequest) {
            WorkflowEdgeRequest request = (WorkflowEdgeRequest) source;
            
            if (request.getName() == null) nullProperties.add("name");
            if (request.getDescription() == null) nullProperties.add("description");
            if (request.getEdgeType() == null) nullProperties.add("edgeType");
            if (request.getSourceNodeId() == null) nullProperties.add("sourceNodeId");
            if (request.getTargetNodeId() == null) nullProperties.add("targetNodeId");
            if (request.getSourcePortId() == null) nullProperties.add("sourcePortId");
            if (request.getTargetPortId() == null) nullProperties.add("targetPortId");
            if (request.getWeight() == null) nullProperties.add("weight");
            if (request.getConditionExpression() == null) nullProperties.add("conditionExpression");
            if (request.getEdgeData() == null) nullProperties.add("edgeData");
            if (request.getExecutionResult() == null) nullProperties.add("executionResult");
            if (request.getEnabled() == null) nullProperties.add("enabled");
            if (request.getStyleConfig() == null) nullProperties.add("styleConfig");
            if (request.getWorkflowUid() == null) nullProperties.add("workflowUid");
        }
        
        return nullProperties.toArray(new String[0]);
    }

    /**
     * Validate edge connection
     */
    public static boolean isValidConnection(String sourceNodeId, String targetNodeId) {
        return StringUtils.hasText(sourceNodeId) && 
               StringUtils.hasText(targetNodeId) && 
               !sourceNodeId.equals(targetNodeId); // 防止自环，除非是特殊的LOOP类型
    }

    /**
     * Create simple edge for connection
     */
    public static WorkflowEdge createSimpleEdge(String sourceNodeId, String targetNodeId, String name) {
        return WorkflowEdge.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(StringUtils.hasText(name) ? name : "Edge")
                .type(WorkflowEdgeTypeEnum.DEFAULT.getValue())
                .sourceNodeID(sourceNodeId)
                .targetNodeID(targetNodeId)
                .weight(0)
                .build();
    }

    /**
     * Create conditional edge
     */
    public static WorkflowEdge createConditionalEdge(String sourceNodeId, String targetNodeId, 
                                                    String name, String condition) {
        return WorkflowEdge.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(StringUtils.hasText(name) ? name : "Conditional Edge")
                .type(WorkflowEdgeTypeEnum.CONDITION.getValue())
                .sourceNodeID(sourceNodeId)
                .targetNodeID(targetNodeId)
                .conditionExpression(condition)
                .weight(0)
                .build();
    }
}
