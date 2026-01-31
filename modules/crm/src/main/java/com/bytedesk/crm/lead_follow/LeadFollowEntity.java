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
package com.bytedesk.crm.lead_follow;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.crm.lead.LeadEntity;

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
 * LeadFollow entity for content categorization and organization
 * Provides lead_followging functionality for various system entities
 * 
 * Database Table: bytedesk_crm_lead_follow
 * Purpose: Stores lead_follow definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({LeadFollowEntityListener.class})
@Table(name = "bytedesk_crm_lead_follow")
public class LeadFollowEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the lead_follow
     */
    private String name;

    /**
     * Description of the lead_follow
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of lead_follow (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "lead_follow_type")
    private String type = LeadFollowTypeEnum.CUSTOMER.name();

    /**
     * 线索ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", referencedColumnName = "id")
    private LeadEntity lead;

    /**
     * 预计沟通内容
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * 预计开始时间
     */
    @Column(name = "estimated_at")
    private ZonedDateTime estimatedAt;

    /**
     * 跟进方式
     */
    @Builder.Default
    @Column(name = "lead_follow_method")
    private String method = LeadFollowMethodEnum.CALL.name();

    /**
     * 状态
     */
    @Builder.Default
    @Column(name = "lead_follow_status")
    private String status = LeadFollowStatusEnum.TODO.name();

    /**
     * 是否转为跟进记录
     */
    @Builder.Default
    @Column(name = "is_converted")
    private Boolean converted = false;

    /**
     * 负责人
     */
    private String ownerUserUid;


}
