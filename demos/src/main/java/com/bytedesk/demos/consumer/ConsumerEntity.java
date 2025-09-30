/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 17:30:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.demos.consumer;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.demos.booking.BookingEntity;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
 * Consumer entity for content categorization and organization
 * Provides consumer functionality for various system entities
 * 
 * Database Table: bytedesk_ai_consumer
 * Purpose: Stores consumer definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ConsumerEntityListener.class})
@Table(name = "bytedesk_ai_consumer")
public class ConsumerEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * Name of the consumer
     */
    private String name;

    /**
     * Description of the consumer
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of consumer (BOOKING, SHOPPING, etc.)
     */
    @Builder.Default
    @Column(name = "consumer_type")
    private String type = ConsumerTypeEnum.BOOKING.name();

    @Builder.Default
    @OneToMany(mappedBy = "consumer")
	private List<BookingEntity> bookings = new ArrayList<>();
}
