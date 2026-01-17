/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @LastEditors: bytedesk.com
 * @LastEditTime: 2025-01-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: support@bytedesk.com
 *  联系：support@bytedesk.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.conference.entity;

// import java.time.LocalDateTime;

// import com.bytedesk.core.base.BaseEntity;
// import com.bytedesk.core.constant.I18Consts;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.Index;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import lombok.experimental.Accessors;
// import lombok.experimental.SuperBuilder;

// /**
//  * Room Entity
//  *
//  * 会议室实体类，用于管理会议室信息
//  *
//  * Database Table: bytedesk_conference_room
//  * Purpose: 存储会议室基本信息、状态、容量等
//  */
// @Entity
// @Data
// @SuperBuilder
// @Accessors(chain = true)
// @EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(
//     name = "bytedesk_conference_room",
//     indexes = {
//         @Index(name = "idx_room_uid", columnList = "uuid"),
//         @Index(name = "idx_room_host_uid", columnList = "host_uid"),
//         @Index(name = "idx_room_status", columnList = "status"),
//         @Index(name = "idx_room_type", columnList = "room_type")
//     }
// )
// public class RoomEntity extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     /**
//      * 会议室名称
//      */
//     @Column(length = 256)
//     private String name;

//     /**
//      * 会议室描述
//      */
//     @Builder.Default
//     @Column(length = 1024)
//     private String description = I18Consts.I18N_DESCRIPTION;

//     /**
//      * 会议室类型
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "room_type", length = 32)
//     private RoomType type;

//     /**
//      * 会议室状态
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "status", length = 32)
//     private RoomStatus status;

//     /**
//      * 主持人ID
//      */
//     @Column(name = "host_uid", nullable = false, length = 64)
//     private String hostUid;

//     /**
//      * 会议室密码
//      */
//     @Column(name = "password", length = 64)
//     private String password;

//     /**
//      * 最大参与者数量
//      */
//     @Builder.Default
//     @Column(name = "max_participants")
//     private Integer maxParticipants = 100;

//     /**
//      * 当前参与者数量
//      */
//     @Builder.Default
//     @Column(name = "current_participants")
//     private Integer currentParticipants = 0;

//     /**
//      * 是否启用等候室
//      */
//     @Builder.Default
//     @Column(name = "waiting_room_enabled")
//     private Boolean waitingRoomEnabled = false;

//     /**
//      * 是否已锁定
//      */
//     @Builder.Default
//     @Column(name = "locked")
//     private Boolean locked = false;

//     /**
//      * 创建时间
//      */
//     @Column(name = "created_at")
//     private LocalDateTime createdAt;

//     /**
//      * 结束时间
//      */
//     @Column(name = "ended_at")
//     private LocalDateTime endedAt;

//     /**
//      * 会议室类型枚举
//      */
//     public enum RoomType {
//         MEETING,        // 会议
//         WEBINAR,        // 网络研讨会
//         TRAINING,       // 培训
//         SUPPORT         // 客服支持
//     }

//     /**
//      * 会议室状态枚举
//      */
//     public enum RoomStatus {
//         IDLE,           // 空闲
//         ACTIVE,         // 活跃
//         LOCKED,         // 锁定
//         ARCHIVED        // 归档
//     }

//     /**
//      * 是否活跃
//      */
//     public boolean isActive() {
//         return status == RoomStatus.ACTIVE;
//     }

//     /**
//      * 是否已锁定
//      */
//     public boolean isLocked() {
//         return status == RoomStatus.LOCKED || locked;
//     }

//     /**
//      * 是否有密码
//      */
//     public boolean hasPassword() {
//         return password != null && !password.isEmpty();
//     }

//     /**
//      * 是否已满员
//      */
//     public boolean isFull() {
//         return currentParticipants >= maxParticipants;
//     }

//     /**
//      * 增加参与者数量
//      */
//     public void incrementParticipants() {
//         currentParticipants++;
//     }

//     /**
//      * 减少参与者数量
//      */
//     public void decrementParticipants() {
//         if (currentParticipants > 0) {
//             currentParticipants--;
//         }
//     }

//     /**
//      * 激活会议室
//      */
//     public void activate() {
//         this.status = RoomStatus.ACTIVE;
//         this.createdAt = LocalDateTime.now();
//     }

//     /**
//      * 归档会议室
//      */
//     public void archive() {
//         this.status = RoomStatus.ARCHIVED;
//         this.endedAt = LocalDateTime.now();
//     }
// }
