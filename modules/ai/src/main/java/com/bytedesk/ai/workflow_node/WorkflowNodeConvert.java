/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-04 17:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 17:45:00
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

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

/**
 * WorkflowNode converter utility class
 * Handles conversion between Entity, Request, and Response objects
 */
public class WorkflowNodeConvert {

    /**
     * Convert WorkflowNodeEntity to WorkflowNodeResponse
     */
    public static WorkflowNodeResponse toResponse(WorkflowNodeEntity entity) {
        if (entity == null) {
            return null;
        }

        WorkflowNodeResponse response = WorkflowNodeResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .nodeType(entity.getType())
                .nodeStatus(entity.getStatus())
                .sortOrder(entity.getSortOrder())
                .parentNodeUid(entity.getParentNodeUid())
                .nodeData(entity.getNodeData())
                .executionResult(entity.getExecutionResult())
                .executionStartTime(entity.getExecutionStartTime())
                .executionEndTime(entity.getExecutionEndTime())
                .errorMessage(entity.getErrorMessage())
                .enabled(entity.getEnabled())
                .workflowUid(entity.getWorkflow() != null ? entity.getWorkflow().getUid() : null)
                .executionDurationMillis(entity.getExecutionDurationMillis())
                .executionDurationSeconds(entity.getExecutionDurationSeconds())
                .isContainerNode(entity.isContainerNode())
                .isControlNode(entity.isControlNode())
                .isProcessingNode(entity.isProcessingNode())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        return response;
    }

    /**
     * Convert WorkflowNodeRequest to WorkflowNodeEntity
     */
    public static WorkflowNodeEntity toEntity(WorkflowNodeRequest request, WorkflowEntity workflow) {
        if (request == null) {
            return null;
        }

        WorkflowNodeEntity entity = WorkflowNodeEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getNodeType())
                .status(request.getNodeStatus())
                .sortOrder(request.getSortOrder())
                .parentNodeUid(request.getParentNodeUid())
                .nodeData(request.getNodeData())
                .executionResult(request.getExecutionResult())
                .errorMessage(request.getErrorMessage())
                .enabled(request.getEnabled())
                .workflow(workflow)
                .build();

        // Set UID if provided
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        }

        return entity;
    }

    /**
     * Update WorkflowNodeEntity from WorkflowNodeRequest
     */
    public static void updateEntity(WorkflowNodeEntity entity, WorkflowNodeRequest request) {
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
        
        if (StringUtils.hasText(request.getNodeType())) {
            entity.setType(request.getNodeType());
        }
        
        if (StringUtils.hasText(request.getNodeStatus())) {
            entity.setStatus(request.getNodeStatus());
        }
        
        if (request.getSortOrder() != null) {
            entity.setSortOrder(request.getSortOrder());
        }
        
        if (StringUtils.hasText(request.getParentNodeUid())) {
            entity.setParentNodeUid(request.getParentNodeUid());
        }
        
        if (StringUtils.hasText(request.getNodeData())) {
            entity.setNodeData(request.getNodeData());
        }
        
        if (StringUtils.hasText(request.getExecutionResult())) {
            entity.setExecutionResult(request.getExecutionResult());
        }
        
        if (StringUtils.hasText(request.getErrorMessage())) {
            entity.setErrorMessage(request.getErrorMessage());
        }
        
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }
    }

    /**
     * Convert WorkflowBaseNode to WorkflowNodeResponse
     */
    public static WorkflowNodeResponse toResponse(WorkflowBaseNode node, String workflowUid) {
        if (node == null) {
            return null;
        }

        return WorkflowNodeResponse.builder()
                .uid(node.getId())
                .name(node.getName())
                .description(node.getDescription())
                .nodeType(node.getType())
                .nodeStatus(node.getStatus())
                .nodeData(node.toJson())
                .workflowUid(workflowUid)
                .enabled(true)
                .build();
    }

    /**
     * Convert WorkflowNodeRequest to WorkflowBaseNode
     */
    public static WorkflowBaseNode toWorkflowNode(WorkflowNodeRequest request) {
        if (request == null) {
            return null;
        }

        // Create node using factory based on type
        String nodeType = StringUtils.hasText(request.getNodeType()) ? 
                          request.getNodeType() : WorkflowNodeTypeEnum.START.getValue();
        
        String nodeId = StringUtils.hasText(request.getUid()) ? 
                        request.getUid() : java.util.UUID.randomUUID().toString();
        
        String nodeName = StringUtils.hasText(request.getName()) ? 
                          request.getName() : "Unnamed Node";
        
        WorkflowBaseNode node = WorkflowNodeFactory.createNode(nodeType, nodeId, nodeName);
        
        // Set additional properties
        if (StringUtils.hasText(request.getDescription())) {
            node.setDescription(request.getDescription());
        }
        
        if (StringUtils.hasText(request.getNodeStatus())) {
            node.setStatus(request.getNodeStatus());
        }

        return node;
    }

    /**
     * Get WorkflowNodeTypeEnum from string value
     */
    public static WorkflowNodeTypeEnum getNodeTypeEnum(String type) {
        if (!StringUtils.hasText(type)) {
            return WorkflowNodeTypeEnum.START;
        }
        
        try {
            return WorkflowNodeTypeEnum.fromValue(type);
        } catch (Exception e) {
            return WorkflowNodeTypeEnum.START;
        }
    }

    /**
     * Validate node type
     */
    public static boolean isValidNodeType(String type) {
        if (!StringUtils.hasText(type)) {
            return false;
        }
        
        try {
            WorkflowNodeTypeEnum.fromValue(type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Convert WorkflowNodeEntity to WorkflowNodeRequest
     */
    public static WorkflowNodeRequest toRequest(WorkflowNodeEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkflowNodeRequest.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .nodeType(entity.getType())
                .nodeStatus(entity.getStatus())
                .sortOrder(entity.getSortOrder())
                .parentNodeUid(entity.getParentNodeUid())
                .nodeData(entity.getNodeData())
                .executionResult(entity.getExecutionResult())
                .errorMessage(entity.getErrorMessage())
                .enabled(entity.getEnabled())
                .workflowUid(entity.getWorkflow() != null ? entity.getWorkflow().getUid() : null)
                .build();
    }

    /**
     * Copy properties from request to entity (excluding null values)
     */
    public static void copyPropertiesIgnoreNull(WorkflowNodeRequest source, WorkflowNodeEntity target) {
        if (source == null || target == null) {
            return;
        }

        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        
        // Handle special field mappings
        if (StringUtils.hasText(source.getNodeType())) {
            target.setType(source.getNodeType());
        }
        
        if (StringUtils.hasText(source.getNodeStatus())) {
            target.setStatus(source.getNodeStatus());
        }
    }

    /**
     * Get null property names from object
     */
    private static String[] getNullPropertyNames(Object source) {
        final java.util.List<String> nullProperties = new java.util.ArrayList<>();
        
        if (source instanceof WorkflowNodeRequest) {
            WorkflowNodeRequest request = (WorkflowNodeRequest) source;
            
            if (request.getName() == null) nullProperties.add("name");
            if (request.getDescription() == null) nullProperties.add("description");
            if (request.getNodeType() == null) nullProperties.add("nodeType");
            if (request.getNodeStatus() == null) nullProperties.add("nodeStatus");
            if (request.getSortOrder() == null) nullProperties.add("sortOrder");
            if (request.getParentNodeUid() == null) nullProperties.add("parentNodeUid");
            if (request.getNodeData() == null) nullProperties.add("nodeData");
            if (request.getExecutionResult() == null) nullProperties.add("executionResult");
            if (request.getErrorMessage() == null) nullProperties.add("errorMessage");
            if (request.getEnabled() == null) nullProperties.add("enabled");
            if (request.getWorkflowUid() == null) nullProperties.add("workflowUid");
        }
        
        return nullProperties.toArray(new String[0]);
    }
}
