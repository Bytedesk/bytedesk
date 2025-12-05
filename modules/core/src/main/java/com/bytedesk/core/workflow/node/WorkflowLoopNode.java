/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 17:23:05
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

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class WorkflowLoopNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    // 循环节点特有的字段
    private Integer loopTimes;
    
    public static WorkflowLoopNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowLoopNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        int executions = loopTimes != null && loopTimes > 0 ? loopTimes : 1;
        Map<String, Object> output = new HashMap<>();
        output.put("loopTimes", executions);
        output.put("blocks", collectBlockIds());
        return WorkflowNodeExecutionResult.success("Loop node executed", context.findNextNodeId(getId()), output);
    }

    private List<String> collectBlockIds() {
        List<String> blockIds = new ArrayList<>();
        if (getBlocks() != null) {
            getBlocks().forEach(block -> blockIds.add(block.getId()));
        }
        return blockIds;
    }
    
} 