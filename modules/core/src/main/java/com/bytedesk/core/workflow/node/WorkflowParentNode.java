/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-04 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-04 15:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.workflow.WorkflowExecutionContext;
import com.bytedesk.core.workflow.WorkflowNodeExecutionResult;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 父节点/容器节点
 * 用于包含和组织子节点
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class WorkflowParentNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    /**
     * 子节点列表
     */
    private List<WorkflowBaseNode> children;
    
    /**
     * 容器标题
     */
    private String title;
    
    /**
     * 容器描述
     */
    private String description;
    
    /**
     * 是否可展开
     */
    private Boolean expandable;
    
    /**
     * 是否展开状态
     */
    private Boolean expanded;
    
    public static WorkflowParentNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowParentNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        Map<String, Object> output = new HashMap<>();
        output.put("title", title != null ? title : (getData() != null ? getData().getTitle() : null));
        output.put("description", description);
        output.put("childCount", children != null ? children.size() : 0);
        output.put("childIds", collectChildIds());
        return WorkflowNodeExecutionResult.success("Parent node evaluated", context.findNextNodeId(getId()), output);
    }
    
    private List<String> collectChildIds() {
        List<String> childIds = new ArrayList<>();
        if (children != null) {
            children.forEach(child -> childIds.add(child.getId()));
        }
        return childIds;
    }
    
}
