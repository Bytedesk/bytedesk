/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-16 17:51:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-19 11:52:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.order;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单信息
 */
@Entity
@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_order")
public class OrderEntity extends BaseEntity {
 
    private static final long serialVersionUID = 1L;

    private String title;

    private String description;

    @Builder.Default
    private double price = 0.0;

    @Builder.Default
    @Column(name = "order_state")
    private String state = OrderStateEnum.WAIT_PAYMENT.name();
    
    private String name;
}
