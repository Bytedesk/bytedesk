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
package com.bytedesk.call.esl_event;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
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
 * EslEvent entity for content categorization and organization
 * Provides esl_eventging functionality for various system entities
 * 
 * Database Table: bytedesk_call_esl_event
 * Purpose: Stores esl_event definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({EslEventEntityListener.class})
@Table(name = "bytedesk_call_esl_event")
public class EslEventEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the esl_event
     */
    private String name;

    /**
     * ESL event name, e.g. CHANNEL_CREATE/CUSTOM
     */
    @Column(name = "event_name")
    private String eventName;

    /**
     * ESL event subclass, commonly used for CUSTOM events
     */
    @Column(name = "event_subclass")
    private String eventSubclass;

    /**
     * FreeSWITCH channel Unique-ID
     */
    @Column(name = "unique_id")
    private String uniqueId;

    /**
     * Caller number when available
     */
    @Column(name = "caller_number")
    private String callerNumber;

    /**
     * Destination number when available
     */
    @Column(name = "destination_number")
    private String destinationNumber;

    /**
     * Hangup cause when available
     */
    @Column(name = "hangup_cause")
    private String hangupCause;

    /**
     * Contact header for SIP registration related events
     */
    private String contact;

    /**
     * Current event status when available
     */
    @Column(name = "event_status")
    private String status;

    /**
     * API command for API events
     */
    @Column(name = "api_command")
    private String apiCommand;

    /**
     * API argument for API events
     */
    @Column(name = "api_argument")
    private String apiArgument;

    /**
     * Serialized event headers in JSON format
     */
    @Lob
    @Column(name = "headers_json")
    private String headersJson;

    /**
     * Serialized event body lines in JSON format
     */
    @Lob
    @Column(name = "body_json")
    private String bodyJson;

    /**
     * Description of the esl_event
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Classified ESL event type, derived from Event-Name
     */
    @Builder.Default
    @Column(name = "esl_event_type")
    private String type = EslEventTypeEnum.UNKNOWN.name();

}
