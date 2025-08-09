/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 11:33:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * FreeSwitch call detail record entity
 * Stores detailed information about voice calls processed by FreeSwitch
 * 
 * Database Table: bytedesk_call_cdr
 * Purpose: Records call metadata, timing information, and call quality metrics
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FreeSwitchCdrEntityListener.class})
@Table(name = "bytedesk_call_cdr")
public class FreeSwitchCdrEntity extends BaseEntity {
    
    /**
     * Caller's display name
     */
    private String callerIdName;

    /**
     * Caller's phone number
     */
    private String callerIdNumber;

    /**
     * Destination phone number
     */
    private String destinationNumber;

    /**
     * FreeSwitch dialplan context
     */
    private String context;

    /**
     * Call start timestamp
     */
    private ZonedDateTime startStamp;

    /**
     * Call answer timestamp when connection is established
     */
    private ZonedDateTime answerStamp;

    /**
     * Call end timestamp
     */
    private ZonedDateTime endStamp;

    /**
     * Total call duration in seconds
     */
    private Integer duration;

    /**
     * Billable duration in seconds
     */
    private Integer billsec;

    /**
     * Reason for call termination
     */
    private String hangupCause;

    /**
     * Account code for billing purposes
     */
    private String accountcode;

    /**
     * Audio codec used for receiving audio
     */
    private String readCodec;

    /**
     * Audio codec used for sending audio
     */
    private String writeCodec;

    /**
     * SIP hangup disposition information
     */
    private String sipHangupDisposition;

    /**
     * Path to call recording file
     */
    private String recordFile;

    /**
     * Call direction (inbound/outbound)
     */
    private String direction;

    /**
     * Additional call information stored as JSON
     */
    @Column(columnDefinition = "TEXT")
    private String json;

    /**
     * Get call status description
     */
    public String getCallStatusDescription() {
        if (answerStamp != null) {
            return "已接通";
        } else if (hangupCause != null) {
            return "未接通 - " + hangupCause;
        } else {
            return "未知状态";
        }
    }

    /**
     * Check if the call was successful
     */
    public boolean isSuccessfulCall() {
        return answerStamp != null && billsec != null && billsec > 0;
    }

    /**
     * Get formatted call duration
     */
    public String getFormattedDuration() {
        if (duration == null) return "00:00:00";
        
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
