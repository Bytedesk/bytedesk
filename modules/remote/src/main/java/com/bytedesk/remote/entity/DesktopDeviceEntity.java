/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Device Entity
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.remote.entity;

// import java.time.ZonedDateTime;
// import java.util.HashSet;
// import java.util.Set;

// import com.bytedesk.core.base.BaseEntity;
// import com.bytedesk.core.rbac.user.UserEntity;

// import jakarta.persistence.*;
// import jakarta.validation.constraints.NotBlank;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import lombok.experimental.Accessors;
// import lombok.experimental.SuperBuilder;

// /**
//  * Desktop Device Entity
//  * Represents a device (computer) that can host or view remote desktop sessions
//  *
//  * Database Table: bytedesk_desktop_device
//  */
// @Entity
// @Data
// @Accessors(chain = true)
// @SuperBuilder
// @EqualsAndHashCode(callSuper = true, exclude = {"user", "sessions"})
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(
//     name = "bytedesk_desktop_device",
//     indexes = {
//         @Index(name = "idx_desktop_device_uid", columnList = "uuid"),
//         @Index(name = "idx_desktop_device_user", columnList = "user_uid"),
//         @Index(name = "idx_desktop_device_name", columnList = "device_name")
//     }
// )
// public class DesktopDeviceEntity extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     /**
//      * Device name (e.g., "Office PC", "MacBook Pro")
//      */
//     @NotBlank(message = "Device name is required")
//     @Column(name = "device_name", nullable = false, length = 255)
//     private String deviceName;

//     /**
//      * Operating system type (WINDOWS, MACOS, LINUX)
//      */
//     @Column(name = "os_type", length = 50)
//     private String osType;

//     /**
//      * OS version (e.g., "Windows 11", "macOS 14.2")
//      */
//     @Column(name = "os_version", length = 50)
//     private String osVersion;

//     /**
//      * Device capabilities (JSON string)
//      * Examples: {"screen_capture": true, "multi_monitor": true, "input_emulation": true}
//      */
//     @Column(name = "capabilities", columnDefinition = "TEXT")
//     private String capabilities;

//     /**
//      * Device IP address (last known)
//      */
//     @Column(name = "ip_address", length = 50)
//     private String ipAddress;

//     /**
//      * Is device currently online
//      */
//     @Builder.Default
//     @Column(name = "is_online", nullable = false)
//     private Boolean online = false;

//     /**
//      * Last online timestamp
//      */
//     @Column(name = "last_online")
//     private ZonedDateTime lastOnline;

//     /**
//      * Device owner user
//      */
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_uid", nullable = false)
//     private UserEntity user;

//     /**
//      * Sessions hosted by this device
//      */
//     @Builder.Default
//     @OneToMany(mappedBy = "hostDevice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//     private Set<DesktopSessionEntity> sessions = new HashSet<>();

//     /**
//      * Update last online timestamp
//      */
//     public void updateLastOnline() {
//         this.lastOnline = ZonedDateTime.now();
//         this.online = true;
//     }

//     /**
//      * Mark device as offline
//      */
//     public void markOffline() {
//         this.online = false;
//     }
// }
