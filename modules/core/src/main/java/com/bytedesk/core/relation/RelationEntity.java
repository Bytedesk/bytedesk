/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-14 09:26:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Relation entity for content categorization and organization
 * Provides relation functionality for various system entities
 * 
 * Database Table: bytedesk_core_relation
 * Purpose: Stores relation definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({RelationEntityListener.class})
@Table(name = "bytedesk_core_relation")
public class RelationEntity extends BaseEntity {

    /**
     * Name of the relation
     */
    private String name;

    /**
     * Description of the relation
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of relation (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "relation_type")
    private String type = RelationTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the relation display
     */
    @Builder.Default
    @Column(name = "relation_color")
    private String color = "red";

    /**
     * Display order of the relation
     */
    @Builder.Default
    @Column(name = "relation_order")
    private Integer order = 0;
}
