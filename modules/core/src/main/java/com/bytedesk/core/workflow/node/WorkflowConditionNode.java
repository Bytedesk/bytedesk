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

import java.util.List;

import com.alibaba.fastjson2.JSON;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class WorkflowConditionNode extends WorkflowBaseNode {

    // 条件节点特有的字段
    private List<ConditionItem> conditions;
    
    public static WorkflowConditionNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowConditionNode.class);
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