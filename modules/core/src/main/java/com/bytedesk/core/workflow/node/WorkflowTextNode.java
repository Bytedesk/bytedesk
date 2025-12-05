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

import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTextNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    private String text;

    public static WorkflowTextNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowTextNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        Map<String, Object> output = new HashMap<>();
        String resolvedText = resolveText();
        output.put("text", resolvedText);
        return WorkflowNodeExecutionResult.success("Text node rendered", context.findNextNodeId(getId()), output);
    }

    private String resolveText() {
        if (StringUtils.hasText(text)) {
            return text;
        }
        if (getData() != null) {
            if (StringUtils.hasText(getData().getContent())) {
                return getData().getContent();
            }
            if (getData().getInputsValues() != null && getData().getInputsValues().get("text") != null) {
                return String.valueOf(getData().getInputsValues().get("text"));
            }
            if (getData().getInputs() != null && getData().getInputs().get("text") != null) {
                return String.valueOf(getData().getInputs().get("text"));
            }
        }
        return "";
    }
    
}
