/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-03 10:12:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-03 10:12:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import com.bytedesk.core.workflow.node.WorkflowNodeStatusEnum;

import org.springframework.util.StringUtils;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkflowNodeExecutionResult {

    @Builder.Default
    private final WorkflowNodeStatusEnum status = WorkflowNodeStatusEnum.SUCCESS;

    private final String message;

    private final String nextNodeId;

    private final Object outputPayload;

    public boolean hasNextNode() {
        return StringUtils.hasText(nextNodeId);
    }

    public static WorkflowNodeExecutionResult success(String message, String nextNodeId, Object payload) {
        return WorkflowNodeExecutionResult.builder()
                .status(WorkflowNodeStatusEnum.SUCCESS)
                .message(message)
                .nextNodeId(nextNodeId)
                .outputPayload(payload)
                .build();
    }
}
