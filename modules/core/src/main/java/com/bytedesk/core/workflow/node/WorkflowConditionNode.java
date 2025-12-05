/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 17:19:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.workflow.WorkflowExecutionContext;
import com.bytedesk.core.workflow.WorkflowNodeExecutionResult;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class WorkflowConditionNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    // 条件节点特有的字段
    private List<ConditionItem> conditions;
    
    public static WorkflowConditionNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowConditionNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        Map<String, Object> output = new HashMap<>();
        ConditionItem selectedCondition = selectCondition();
        String nextNodeId = null;
        if (selectedCondition != null) {
            output.put("selectedKey", selectedCondition.getKey());
            output.put("expression", selectedCondition.getValue());
            if (StringUtils.hasText(selectedCondition.getOutgoingEdgeId())) {
                nextNodeId = context.findTargetNodeIdByEdgeId(selectedCondition.getOutgoingEdgeId());
            }
        }
        if (!StringUtils.hasText(nextNodeId)) {
            nextNodeId = context.findNextNodeId(getId());
        }
        output.put("nextNodeId", nextNodeId);
        return WorkflowNodeExecutionResult.success("Condition evaluated", nextNodeId, output);
    }

    private ConditionItem selectCondition() {
        if (CollectionUtils.isEmpty(conditions)) {
            return null;
        }
        // TODO 结合表达式解析动态选择满足条件的分支
        return conditions.get(0);
    }
    
    /**
     * 条件项
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class ConditionItem {
        private String key;
        private ConditionValue value;
        private String outgoingEdgeId;
    }
    
    /**
     * 条件值
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class ConditionValue {
        private String type; // "expression"
        private String content;
    }
    
} 