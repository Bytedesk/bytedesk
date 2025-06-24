/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 17:02:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.node;

import com.bytedesk.core.base.BaseNode;

/**
 * 工作流节点工厂类
 * 用于根据节点类型创建相应的节点实例
 */
public class WorkflowNodeFactory {
    
    /**
     * 根据节点类型创建节点实例
     */
    public static BaseNode createNode(String nodeType) {
        WorkflowNodeTypeEnum type = WorkflowNodeTypeEnum.fromValue(nodeType);
        
        switch (type) {
            case START:
                return WorkflowStartNode.builder().type(nodeType).build();
            case END:
                return WorkflowEndNode.builder().type(nodeType).build();
            case CONDITION:
                return WorkflowConditionNode.builder().type(nodeType).build();
            case LOOP:
                return WorkflowLoopNode.builder().type(nodeType).build();
            case LLM:
                return WorkflowLLMNode.builder().type(nodeType).build();
            case TEXT:
                return WorkflowTextNode.builder().type(nodeType).build();
            case COMMENT:
                return WorkflowCommentNode.builder().type(nodeType).build();
            case GROUP:
                return WorkflowGroupNode.builder().type(nodeType).build();
            default:
                throw new IllegalArgumentException("Unsupported node type: " + nodeType);
        }
    }
    
    /**
     * 根据节点类型创建节点实例（带ID）
     */
    public static BaseNode createNode(String nodeType, String uid) {
        BaseNode node = createNode(nodeType);
        node.setUid(uid);
        return node;
    }
    
    /**
     * 根据节点类型创建节点实例（带ID和名称）
     */
    public static BaseNode createNode(String nodeType, String uid, String name) {
        BaseNode node = createNode(nodeType, uid);
        node.setName(name);
        return node;
    }
    
} 