/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-27 11:18:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-21 11:41:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.booking;
import java.time.ZonedDateTime;

import com.bytedesk.ai.alibaba.consumer.ConsumerEntity;
import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * 
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({BookingEntityListener.class})
@Table(name = "bytedesk_ai_booking")
public class BookingEntity extends BaseEntity {

	private String bookingNumber;

	private ZonedDateTime bookingDate;

	@Column(name = "booking_from")
	private String from;

	@Column(name = "booking_to")
	private String to;

	@Builder.Default
	@Column(name = "booking_status")
	private String status = BookingStatusEnum.CONFIRMED.name();

	@Builder.Default
	@Column(name = "booking_class")
	private String bookingClass = BookingClassEnum.ECONOMY.name();

	@ManyToOne
	private ConsumerEntity consumer;



}