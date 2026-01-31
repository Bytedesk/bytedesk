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
package com.bytedesk.crm.lead;

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
 * Lead entity for content categorization and organization
 * Provides leadging functionality for various system entities
 * 
 * Database Table: bytedesk_crm_lead
 * Purpose: Stores lead definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({LeadEntityListener.class})
@Table(name = "bytedesk_crm_lead")
public class LeadEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the lead
     */
    private String name;

    /**
     * Description of the lead
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of lead (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "lead_type")
    private String type = LeadTypeEnum.CUSTOMER.name();

    /**
     * 线索阶段
     */
    @Builder.Default
    @Column(name = "lead_status")
    private String status = LeadStatusEnum.NEW.name();

    /**
     * 联系人名称
     */
    private String contact;

    /**
     * 联系人电话
     */
    private String phone;

    /**
     * 意向产品ID列表
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> productUids = new ArrayList<>();

    /**
     * 是否在线索池
     */
    @Builder.Default
    private Boolean inSharedPool = false;

    /**
     * 转移成客户或者线索
     */
    @Builder.Default
    @Column(name = "transition_type")
    private String transitionType = LeadTransitionTypeEnum.NONE.name();

    /**
     * 客户ID或者线索ID
     */
    private String transitionUid;

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
     * 负责人
     */
    private String ownerUserUid;

}
