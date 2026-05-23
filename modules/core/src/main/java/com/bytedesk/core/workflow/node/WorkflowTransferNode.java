/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-05-23 20:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-05-23 20:20:00
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

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.workflow.WorkflowExecutionContext;
import com.bytedesk.core.workflow.WorkflowNodeExecutionResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class WorkflowTransferNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    public static WorkflowTransferNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowTransferNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        Map<String, Object> output = new HashMap<>();
        output.put("routeType", getType());
        output.put("destination", resolveDataValue("transferDestination", "operator"));
        output.put("context", resolveDataValue("transferContext", "default"));
        output.put("transferData", resolveDataValue("transferData", ""));
        output.put("prompt", resolvePrompt());
        return WorkflowNodeExecutionResult.success("Route node prepared", context.findNextNodeId(getId()), output);
    }

    protected String resolvePrompt() {
        if (getData() != null && StringUtils.hasText(getData().getContent())) {
            return getData().getContent();
        }
        return "";
    }

    protected String resolveDataValue(String key, String defaultValue) {
        if (getData() == null) {
            return defaultValue;
        }
        if (getData().getProperties() != null && getData().getProperties().get(key) != null) {
            return String.valueOf(getData().getProperties().get(key));
        }
        try {
            java.lang.reflect.Field field = getData().getClass().getDeclaredField(key);
            field.setAccessible(true);
            Object value = field.get(getData());
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return defaultValue;
    }
}