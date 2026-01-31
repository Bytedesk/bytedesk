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
package com.bytedesk.crm.opportunity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
 * Opportunity entity for content categorization and organization
 * Provides opportunityging functionality for various system entities
 * 
 * Database Table: bytedesk_crm_opportunity
 * Purpose: Stores opportunity definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({OpportunityEntityListener.class})
@Table(name = "bytedesk_crm_opportunity")
public class OpportunityEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the opportunity
     */
    private String name;

    /**
     * Description of the opportunity
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of opportunity (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "opportunity_type")
    private String type = OpportunityTypeEnum.CUSTOMER.name();

    /**
     * 客户ID
     */
    private String customerUid;

    /**
     * 金额
     */
    @Column(name = "opportunity_amount")
    private BigDecimal amount;

    /**
     * 可能性
     */
    @Builder.Default
    @Column(name = "opportunity_possible")
    private String possible = OpportunityPossibleEnum.MEDIUM.name();

    /**
     * 意向产品ID列表
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> productUids = new ArrayList<>();

    /**
     * 商机阶段
     */
    @Builder.Default
    @Column(name = "opportunity_status")
    private String status = OpportunityStatusEnum.NEW.name();

    /**
     * 最新跟进人
     */
    private String followerUserUid;

    /**
     * 最新跟进时间
     */
    @Column(name = "follow_at")
    private ZonedDateTime followAt;

    /**
     * 结束时间
     */
    @Column(name = "expected_end_at")
    private ZonedDateTime expectedEndAt;

    /**
     * 实际结束时间
     */
    @Column(name = "actual_end_at")
    private ZonedDateTime actualEndAt;

    /**
     * 失败原因
     */
    private String failureReason;

    /**
     * 责任人
     */
    private String ownerUserUid;

}
