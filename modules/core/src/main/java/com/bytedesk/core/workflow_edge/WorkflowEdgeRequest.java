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
package com.bytedesk.core.workflow_edge;

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
public class WorkflowEdgeRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;


    /**
     * 边名称
     */
    private String name;

    /**
     * 边描述
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 边类型 (DEFAULT, CONDITION, LOOP, DATA, CONTROL, ERROR, etc.)
     */
    @Builder.Default
    private String edgeType = WorkflowEdgeTypeEnum.DEFAULT.getValue();

    /**
     * 源节点ID
     */
    private String sourceNodeId;

    /**
     * 目标节点ID
     */
    private String targetNodeId;

    /**
     * 源端口ID（可选，用于多端口节点）
     */
    private String sourcePortId;

    /**
     * 目标端口ID（可选，用于多端口节点）
     */
    private String targetPortId;

    /**
     * 边的权重或优先级
     */
    @Builder.Default
    private Integer weight = 0;

    /**
     * 条件表达式（用于条件边）
     */
    private String conditionExpression;

    /**
     * 边完整配置数据（JSON格式）
     */
    private String edgeData;

    /**
     * 边执行结果数据（JSON格式）
     */
    private String executionResult;

    /**
     * 是否启用该边
     */
    @Builder.Default
    private Boolean enabled = true;

    /**
     * 边的样式配置（JSON格式）
     */
    private String styleConfig;

    /**
     * 关联的工作流UID
     */
    private String workflowUid;

}
