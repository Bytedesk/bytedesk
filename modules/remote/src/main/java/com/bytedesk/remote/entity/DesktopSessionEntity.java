/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Session Entity
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.remote.entity;

// import java.time.ZonedDateTime;
// import java.util.HashSet;
// import java.util.Set;

// import com.bytedesk.core.base.BaseEntity;
// import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.remote.protocol.DesktopModeEnum;
// import com.bytedesk.remote.protocol.DesktopSessionStatusEnum;
// import com.bytedesk.remote.protocol.QualityLevelEnum;

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
//  * Desktop Remote Control Session Entity
//  * Represents a remote desktop session between a host device and a viewer
//  *
//  * Database Table: bytedesk_desktop_session
//  */
// @Entity
// @Data
// @Accessors(chain = true)
// @SuperBuilder
// @EqualsAndHashCode(callSuper = true, exclude = {"hostDevice", "viewerUser", "auditLogs"})
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(
//     name = "bytedesk_desktop_session",
//     indexes = {
//         @Index(name = "idx_desktop_session_uid", columnList = "uuid"),
//         @Index(name = "idx_desktop_session_host", columnList = "host_device_uid"),
//         @Index(name = "idx_desktop_session_viewer", columnList = "viewer_user_uid"),
//         @Index(name = "idx_desktop_session_status", columnList = "status")
//     }
// )
// public class DesktopSessionEntity extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     /**
//      * Host device (the device being controlled/viewed)
//      */
//     @NotNull(message = "Host device is required")
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "host_device_uid", nullable = false)
//     private DesktopDeviceEntity hostDevice;

//     /**
//      * Viewer user (the user controlling/viewing the host)
//      */
//     @NotNull(message = "Viewer user is required")
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "viewer_user_uid", nullable = false)
//     private UserEntity viewerUser;

//     /**
//      * Session mode (SUPPORT, REMOTE, PRESENTATION)
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "mode", nullable = false, length = 20)
//     private DesktopModeEnum mode;

//     /**
//      * Current session status
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "status", nullable = false, length = 20)
//     private DesktopSessionStatusEnum status;

//     /**
//      * Has control permission been granted to viewer
//      */
//     @Builder.Default
//     @Column(name = "control_granted", nullable = false)
//     private Boolean controlGranted = false;

//     /**
//      * Current quality level
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "quality_level", length = 20)
//     private QualityLevelEnum qualityLevel;

//     /**
//      * Screen resolution (width x height)
//      * Example: "1920x1080"
//      */
//     @Column(name = "resolution", length = 20)
//     private String resolution;

//     /**
//      * Current frame rate
//      */
//     @Column(name = "frame_rate")
//     private Integer frameRate;

//     /**
//      * Session started timestamp
//      */
//     @Column(name = "started_at")
//     private ZonedDateTime startedAt;

//     /**
//      * Session ended timestamp
//      */
//     @Column(name = "ended_at")
//     private ZonedDateTime endedAt;

//     /**
//      * Session duration in seconds
//      */
//     @Column(name = "duration_seconds")
//     private Long durationSeconds;

//     /**
//      * Termination reason (if any)
//      * Examples: "user_initiated", "connection_lost", "timeout", "error"
//      */
//     @Column(name = "termination_reason", length = 255)
//     private String terminationReason;

//     /**
//      * Total bytes transferred (screen data)
//      */
//     @Column(name = "bytes_transferred")
//     private Long bytesTransferred;

//     /**
//      * Total number of frames sent
//      */
//     @Column(name = "frames_sent")
//     private Long framesSent;

//     /**
//      * Average latency in milliseconds
//      */
//     @Column(name = "average_latency_ms")
//     private Integer averageLatencyMs;

//     /**
//      * Audit logs for this session
//      */
//     @Builder.Default
//     @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//     private Set<DesktopAuditLogEntity> auditLogs = new HashSet<>();

//     /**
//      * Start the session
//      */
//     public void startSession() {
//         this.startedAt = ZonedDateTime.now();
//         this.status = DesktopSessionStatusEnum.CONNECTED;
//     }

//     /**
//      * End the session
//      */
//     public void endSession(String reason) {
//         this.endedAt = ZonedDateTime.now();
//         this.terminationReason = reason;
//         this.status = DesktopSessionStatusEnum.TERMINATED;

//         if (this.startedAt != null) {
//             this.durationSeconds = java.time.Duration.between(this.startedAt, this.endedAt).getSeconds();
//         }
//     }

//     /**
//      * Grant control permission to viewer
//      */
//     public void grantControl() {
//         this.controlGranted = true;
//     }

//     /**
//      * Revoke control permission from viewer
//      */
//     public void revokeControl() {
//         this.controlGranted = false;
//     }

//     /**
//      * Update session statistics
//      */
//     public void updateStats(Long bytes, Long frames, Integer latency) {
//         if (bytes != null) {
//             this.bytesTransferred = (this.bytesTransferred == null ? 0 : this.bytesTransferred) + bytes;
//         }
//         if (frames != null) {
//             this.framesSent = (this.framesSent == null ? 0 : this.framesSent) + frames;
//         }
//         if (latency != null) {
//             this.averageLatencyMs = latency;
//         }
//     }

//     /**
//      * Adjust quality level
//      */
//     public void adjustQuality(QualityLevelEnum quality) {
//         this.qualityLevel = quality;
//     }
// }
