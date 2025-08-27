/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02 10:11:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 12:08:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.variable;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 工作流变量实体
 * 用于存储工作流执行过程中的变量
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_workflow_variable")
public class WorkflowVariableEntity extends BaseEntity {

    /**
     * 变量名称
     */
    private String name;
    
    /**
     * 变量描述
     */
    private String description;
    
    /**
     * 变量值（JSON格式）
     */
    @Column(name = "variable_value", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String value;
    
    /**
     * 变量类型
     */
    @Column(name = "variable_type")
    private String type;
    
    /**
     * 所属工作流实例UID
     */
    private String workflowUid;
    
    /**
     * 所属节点UID
     */
    private String nodeUid;
    
    /**
     * 变量作用域
     * - GLOBAL: 全局变量，在整个工作流中可见
     * - LOCAL: 局部变量，仅在当前节点可见
     * - SESSION: 会话变量，在整个会话过程中可见
     */
    @Builder.Default
    @Column(name = "variable_scope")
    private String scope = WorkflowVariableScopeEnum.GLOBAL.name();
    
    /**
     * 是否为系统变量
     */
    @Builder.Default
    @Column(name = "is_system")
    private Boolean system = false;
}
