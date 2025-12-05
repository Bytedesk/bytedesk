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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.workflow.WorkflowExecutionContext;
import com.bytedesk.core.workflow.WorkflowNodeExecutionResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 表单节点
 * 用于收集用户输入的表单数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowFormNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    /**
     * 表单类型: label, text, digit, date, select, checkbox, radio
     */
    private String formType;
    
    /**
     * 表单字段标签
     */
    private String label;
    
    /**
     * 表单字段占位符
     */
    private String placeholder;
    
    /**
     * 是否必填
     */
    private Boolean required;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 选项列表（用于select, checkbox, radio类型）
     */
    private List<FormOption> options;
    
    /**
     * 验证规则
     */
    private List<FormValidation> validations;
    
    public static WorkflowFormNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowFormNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        Map<String, Object> output = new HashMap<>();
        output.put("formType", formType);
        output.put("label", label != null ? label : (getData() != null ? getData().getTitle() : null));
        output.put("placeholder", placeholder);
        output.put("required", required != null ? required : false);
        output.put("defaultValue", defaultValue);
        output.put("options", options);
        output.put("validations", validations);
        
        return WorkflowNodeExecutionResult.success("Form node evaluated", context.findNextNodeId(getId()), output);
    }
    
    /**
     * 表单选项
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormOption {
        private String label;
        private String value;
        private Boolean disabled;
    }
    
    /**
     * 表单验证规则
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormValidation {
        private String type; // required, min, max, pattern, email, url, etc.
        private String message;
        private Object value; // 验证规则的值
    }
    
}
