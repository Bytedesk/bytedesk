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
package com.bytedesk.core.calendar;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
 * Calendar entity for content categorization and organization
 * Provides calendarging functionality for various system entities
 * 
 * Database Table: bytedesk_core_calendar
 * Purpose: Stores calendar definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({CalendarEntityListener.class})
@Table(
    name = "bytedesk_core_calendar",
    indexes = {
        @Index(name = "idx_calendar_uid", columnList = "uuid"),
        @Index(name = "idx_calendar_org_uid", columnList = "org_uid"),
        @Index(name = "idx_calendar_user_uid", columnList = "user_uid"),
        @Index(name = "idx_calendar_type", columnList = "calendar_type")
    }
)
public class CalendarEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the calendar
     */
    @Column(length = 128)
    private String name;

    /**
     * Description of the calendar
     */
    @Builder.Default
    @Column(length = 512)
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of calendar (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "calendar_type")
    private String type = CalendarTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the calendar display
     */
    @Builder.Default
    @Column(name = "calendar_color", length = 32)
    private String color = "blue";

    /**
     * Display order
     */
    @Builder.Default
    @Column(name = "calendar_order")
    private Integer order = 0;

    /**
     * Schedule start time (optional)
     */
    @Column(name = "start_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime startAt;

    /**
     * Schedule end time (optional)
     */
    @Column(name = "end_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime endAt;

    /**
     * All-day schedule flag
     */
    @Builder.Default
    @Column(name = "is_all_day")
    private Boolean allDay = false;

    /**
     * Location (meeting room, address, etc.)
     */
    @Column(length = 255)
    private String location;

    /**
     * Status (SCHEDULED, DONE, CANCELED, etc.)
     */
    @Builder.Default
    @Column(name = "calendar_status", length = 32)
    private String status = "SCHEDULED";

    /**
     * Reminder time (optional)
     */
    @Column(name = "remind_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime remindAt;

}
