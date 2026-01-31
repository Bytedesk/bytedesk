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
package com.bytedesk.crm.product;

import java.math.BigDecimal;

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
 * Product entity for content categorization and organization
 * Provides productging functionality for various system entities
 * 
 * Database Table: bytedesk_crm_product
 * Purpose: Stores product definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ProductEntityListener.class})
@Table(name = "bytedesk_crm_product")
public class ProductEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the product
     */
    private String name;

    /**
     * Description of the product
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of product (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "product_type")
    private String type = ProductTypeEnum.CUSTOMER.name();

    /**
     * 价格
     */
    @Column(name = "product_price")
    private BigDecimal price;

    /**
     * 状态
     */
    @Builder.Default
    @Column(name = "product_status")
    private String status = ProductStatusEnum.ENABLED.name();

    /**
     * 库存数量
     */
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

}
