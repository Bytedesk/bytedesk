/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.opinion;

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
 * Opinion entity for content categorization and organization
 * Provides opinion functionality for various system entities
 * 
 * Database Table: bytedesk_core_opinion
 * Purpose: Stores opinion definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({OpinionEntityListener.class})
@Table(name = "bytedesk_voc_opinion")
public class OpinionEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * Name of the opinion
     */
    private String name;

    /**
     * Description of the opinion
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of opinion (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "opinion_type")
    private String type = OpinionTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the opinion display
     */
    @Builder.Default
    @Column(name = "opinion_color")
    private String color = "red";

    /**
     * Display order of the opinion
     */
    @Builder.Default
    @Column(name = "opinion_order")
    private Integer order = 0;
}
