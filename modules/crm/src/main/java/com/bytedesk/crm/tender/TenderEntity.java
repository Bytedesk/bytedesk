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
package com.bytedesk.crm.tender;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

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
 * Tender entity for content categorization and organization
 * Provides tenderging functionality for various system entities
 * 
 * Database Table: bytedesk_crm_tender
 * Purpose: Stores tender definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({TenderEntityListener.class})
@Table(name = "bytedesk_crm_tender")
public class TenderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the tender
     */
    private String name;

    /**
     * Description of the tender
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of tender (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "tender_type")
    private String type = TenderTypeEnum.CUSTOMER.name();

    /**
     * 预算
     */
    @Column(name = "tender_budget")
    private BigDecimal budget;

    /**
     * 招投标阶段
     */
    @Builder.Default
    @Column(name = "tender_status")
    private String status = TenderStatusEnum.PREPARING.name();

    /**
     * 截止时间
     */
    @Column(name = "deadline_at")
    private ZonedDateTime deadlineAt;

    /**
     * 负责人
     */
    private String ownerUserUid;
}
