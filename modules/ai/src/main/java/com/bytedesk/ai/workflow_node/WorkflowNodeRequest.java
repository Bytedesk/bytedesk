/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 14:24:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_node;

import com.bytedesk.ai.workflow.node.WorkflowNodeTypeEnum;
import com.bytedesk.ai.workflow.node.WorkflowNodeStatusEnum;
import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowNodeRequest extends BaseRequest {

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点描述
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 节点类型 (START, END, CONDITION, LOOP, LLM, TEXT, COMMENT, GROUP)
     */
    @Builder.Default
    private String nodeType = WorkflowNodeTypeEnum.START.getValue();

    /**
     * 节点状态 (IDLE, RUNNING, SUCCESS, FAILED, WAITING)
     */
    @Builder.Default
    private String nodeStatus = WorkflowNodeStatusEnum.IDLE.name();

    /**
     * 节点在工作流中的执行顺序
     */
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 父节点UID（用于组织节点层次结构）
     */
    private String parentNodeUid;

    /**
     * 节点完整配置数据（JSON格式存储WorkflowBaseNode及其子类的完整数据）
     */
    private String nodeData;

    /**
     * 节点执行结果数据（JSON格式）
     */
    private String executionResult;

    /**
     * 节点执行错误信息
     */
    private String errorMessage;

    /**
     * 是否启用该节点
     */
    @Builder.Default
    private Boolean enabled = true;

    /**
     * 关联的工作流UID
     */
    private String workflowUid;

}
