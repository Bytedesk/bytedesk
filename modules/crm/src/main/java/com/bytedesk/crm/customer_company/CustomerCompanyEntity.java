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
package com.bytedesk.crm.customer_company;

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
 * 公司类型客户信息
 * 
 * CustomerCompany entity for content categorization and organization
 * Provides customer_companyging functionality for various system entities
 * 
 * Database Table: bytedesk_crm_customer_company
 * Purpose: Stores customer_company definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({CustomerCompanyEntityListener.class})
@Table(name = "bytedesk_crm_customer_company")
public class CustomerCompanyEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the customer_company
     */
    private String name;

    /**
     * Industry of the company
     */
    @Builder.Default
    private String industry = CustomerCompanyIndustryEnum.OTHER.getCode();

    /**
     * Company size
     */
    @Column(name = "company_size")
    private String size;

    /**
     * Website of the company
     */
    private String website;

    /**
     * Owner user uid
     */
    @Column(name = "owner_user_uid")
    private String ownerUserUid;

    /**
     * Description of the customer_company
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of customer_company (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "customer_company_type")
    private String type = CustomerCompanyTypeEnum.CUSTOMER.name();

}
