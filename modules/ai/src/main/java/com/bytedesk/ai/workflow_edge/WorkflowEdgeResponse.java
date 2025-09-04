/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:36:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_edge;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseResponse;
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
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowEdgeResponse extends BaseResponse {

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
     * 边最后执行时间
     */
    private ZonedDateTime lastExecutionTime;

    /**
     * 边执行次数
     */
    private Long executionCount;

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

    /**
     * 是否为条件边
     */
    private Boolean isConditionalEdge;

    /**
     * 是否为控制边
     */
    private Boolean isControlEdge;

    /**
     * 是否为数据边
     */
    private Boolean isDataEdge;

}
