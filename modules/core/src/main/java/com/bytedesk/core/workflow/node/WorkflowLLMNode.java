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

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.workflow.WorkflowExecutionContext;
import com.bytedesk.core.workflow.WorkflowNodeExecutionResult;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class WorkflowLLMNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    // LLM节点特有的字段
    private String modelType;
    private Double temperature;
    private String systemPrompt;
    private String prompt;
    
    public static WorkflowLLMNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowLLMNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        Map<String, Object> output = new HashMap<>();
        output.put("modelType", modelType);
        output.put("temperature", temperature);
        output.put("systemPrompt", systemPrompt);
        output.put("prompt", prompt != null ? prompt : (getData() != null ? getData().getContent() : null));
        if (getData() != null && getData().getInputs() != null) {
            output.put("inputs", getData().getInputs());
        }
        output.put("mockResponse", "LLM response placeholder");
        return WorkflowNodeExecutionResult.success("LLM node executed", context.findNextNodeId(getId()), output);
    }
    
} 