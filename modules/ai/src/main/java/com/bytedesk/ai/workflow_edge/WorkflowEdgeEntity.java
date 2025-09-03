/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 13:52:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_edge;

import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * WorkflowEdge entity for content categorization and organization
 * Provides workflow_edge functionality for various system entities
 * 
 * Database Table: bytedesk_ai_workflow_edge
 * Purpose: Stores workflow_edge definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({WorkflowEdgeEntityListener.class})
@Table(name = "bytedesk_ai_workflow_edge")
public class WorkflowEdgeEntity extends BaseEntity {

    /**
     * Name of the workflow_edge
     */
    private String name;

    /**
     * Description of the workflow_edge
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of workflow_edge (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "edge_type")
    private String type = WorkflowEdgeTypeEnum.CUSTOMER.name();

    // 
    @Column(name = "edge_schema", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String schema;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private WorkflowEntity workflow;

}
