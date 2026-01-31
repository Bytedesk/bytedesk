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
package com.bytedesk.crm.contract;

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
 * Contract entity for content categorization and organization
 * Provides contractging functionality for various system entities
 * 
 * Database Table: bytedesk_crm_contract
 * Purpose: Stores contract definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ContractEntityListener.class})
@Table(name = "bytedesk_crm_contract")
public class ContractEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the contract
     */
    private String name;

    /**
     * Description of the contract
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of contract (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "contract_type")
    private String type = ContractTypeEnum.CUSTOMER.name();

    /**
     * 客户ID
     */
    private String customerUid;

    /**
     * 金额
     */
    @Column(name = "contract_amount")
    private BigDecimal amount;

    /**
     * 编号
     */
    @Column(name = "contract_number")
    private String number;

    /**
     * 合同状态（统一状态字段）
     */
    @Builder.Default
    @Column(name = "contract_status")
    private String status = ContractStatusEnum.DRAFT.name();

    /**
     * 合同开始时间
     */
    @Column(name = "start_at")
    private ZonedDateTime startAt;

    /**
     * 合同结束时间
     */
    @Column(name = "end_at")
    private ZonedDateTime endAt;

    /**
     * 作废原因
     */
    private String voidReason;

    /**
     * 合同负责人
     */
    private String ownerUserUid;

}
