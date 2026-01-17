/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Audit Log Entity
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.remote.entity;

// import java.time.ZonedDateTime;

// import com.bytedesk.core.base.BaseEntity;

// import jakarta.persistence.*;
// import jakarta.validation.constraints.NotNull;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import lombok.experimental.Accessors;
// import lombok.experimental.SuperBuilder;

// /**
//  * Desktop Audit Log Entity
//  * Records all events for security and compliance
//  *
//  * Database Table: bytedesk_desktop_audit_log
//  */
// @Entity
// @Data
// @Accessors(chain = true)
// @SuperBuilder
// @EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(
//     name = "bytedesk_desktop_audit_log",
//     indexes = {
//         @Index(name = "idx_desktop_audit_session", columnList = "session_uid"),
//         @Index(name = "idx_desktop_audit_type", columnList = "event_type"),
//         @Index(name = "idx_desktop_audit_timestamp", columnList = "timestamp")
//     }
// )
// public class DesktopAuditLogEntity extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     /**
//      * Related session
//      */
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "session_uid")
//     private DesktopSessionEntity session;

//     /**
//      * Event type
//      * Examples: SESSION_CREATED, SESSION_CONNECTED, CONTROL_GRANTED,
//      * CONTROL_REVOKED, QUALITY_CHANGED, FILE_TRANSFERRED, SESSION_TERMINATED
//      */
//     @NotNull(message = "Event type is required")
//     @Column(name = "event_type", nullable = false, length = 50)
//     private String eventType;

//     /**
//      * User who triggered the event
//      */
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_uid")
//     private com.bytedesk.core.rbac.user.UserEntity user;

//     /**
//      * Device related to the event
//      */
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "device_uid")
//     private DesktopDeviceEntity device;

//     /**
//      * Event details (JSON string)
//      * Contains additional context about the event
//      */
//     @Column(name = "details", columnDefinition = "TEXT")
//     private String details;

//     /**
//      * IP address of the user
//      */
//     @Column(name = "ip_address", length = 50)
//     private String ipAddress;

//     /**
//      * User agent string
//      */
//     @Column(name = "user_agent", length = 500)
//     private String userAgent;

//     /**
//      * Event timestamp
//      */
//     @NotNull(message = "Timestamp is required")
//     @Column(name = "timestamp", nullable = false)
//     private ZonedDateTime timestamp;

//     /**
//      * Is this a security-sensitive event
//      */
//     @Builder.Default
//     @Column(name = "is_sensitive", nullable = false)
//     private Boolean sensitive = false;
// }
