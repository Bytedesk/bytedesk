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
//  * Participant Entity
//  *
//  * 会议参与者实体类，用于管理会议参与者信息
//  *
//  * Database Table: bytedesk_conference_participant
//  * Purpose: 存储会议参与者信息、状态、权限等
//  */
// @Entity
// @Data
// @SuperBuilder
// @Accessors(chain = true)
// @EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(
//     name = "bytedesk_conference_participant",
//     indexes = {
//         @Index(name = "idx_participant_uid", columnList = "uuid"),
//         @Index(name = "idx_participant_conference_uid", columnList = "conference_uid"),
//         @Index(name = "idx_participant_user_uid", columnList = "user_uid"),
//         @Index(name = "idx_participant_role", columnList = "role")
//     }
// )
// public class ParticipantEntity extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     /**
//      * 会议ID
//      */
//     @Column(name = "conference_uid", nullable = false, length = 64)
//     private String conferenceUid;

//     /**
//      * 用户ID
//      */
//     @Column(name = "user_uid", nullable = false, length = 64)
//     private String userUid;

//     /**
//      * 用户昵称
//      */
//     @Column(name = "nickname", length = 128)
//     private String nickname;

//     /**
//      * 用户头像
//      */
//     @Column(name = "avatar", length = 512)
//     private String avatar;

//     /**
//      * 用户邮箱
//      */
//     @Column(name = "email", length = 128)
//     private String email;

//     /**
//      * 用户手机号
//      */
//     @Column(name = "phone", length = 32)
//     private String phone;

//     /**
//      * 参与者角色
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "role", length = 32)
//     private ParticipantRole role;

//     /**
//      * 参与者状态
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "status", length = 32)
//     private ParticipantStatus status;

//     /**
//      * 是否已开启音频
//      */
//     @Builder.Default
//     @Column(name = "audio_enabled")
//     private Boolean audioEnabled = true;

//     /**
//      * 是否已开启视频
//      */
//     @Builder.Default
//     @Column(name = "video_enabled")
//     private Boolean videoEnabled = true;

//     /**
//      * 是否正在屏幕共享
//      */
//     @Builder.Default
//     @Column(name = "screen_sharing")
//     private Boolean screenSharing = false;

//     /**
//      * 是否已举手
//      */
//     @Builder.Default
//     @Column(name = "hand_raised")
//     private Boolean handRaised = false;

//     /**
//      * 加入时间
//      */
//     @Column(name = "join_time")
//     private LocalDateTime joinTime;

//     /**
//      * 离开时间
//      */
//     @Column(name = "leave_time")
//     private LocalDateTime leaveTime;

//     /**
//      * 设备信息
//      */
//     @Column(name = "device", length = 64)
//     private String device;

//     /**
//      * IP地址
//      */
//     @Column(name = "ip", length = 64)
//     private String ip;

//     /**
//      * 信号质量 (0-100)
//      */
//     @Builder.Default
//     @Column(name = "signal_quality")
//     private Integer signalQuality = 100;

//     /**
//      * 参与者角色枚举
//      */
//     public enum ParticipantRole {
//         HOST,           // 主持人
//         CO_HOST,        // 联合主持人
//         PRESENTER,      // 演讲者
//         ATTENDEE        // 参会者
//     }

//     /**
//      * 参与者状态枚举
//      */
//     public enum ParticipantStatus {
//         ONLINE,         // 在线
//         AWAY,           // 离开
//         OFFLINE,        // 离线
//         IN_LOBBY        // 在等候室
//     }

//     /**
//      * 是否是主持人
//      */
//     public boolean isHost() {
//         return role == ParticipantRole.HOST;
//     }

//     /**
//      * 是否是联合主持人
//      */
//     public boolean isCoHost() {
//         return role == ParticipantRole.CO_HOST;
//     }

//     /**
//      * 是否有控制权限
//      */
//     public boolean canControl() {
//         return role == ParticipantRole.HOST || role == ParticipantRole.CO_HOST;
//     }

//     /**
//      * 是否在线
//      */
//     public boolean isOnline() {
//         return status == ParticipantStatus.ONLINE;
//     }

//     /**
//      * 获取参与时长（分钟）
//      */
//     public long getDurationMinutes() {
//         if (joinTime == null) {
//             return 0;
//         }
//         LocalDateTime end = leaveTime != null ? leaveTime : LocalDateTime.now();
//         return java.time.Duration.between(joinTime, end).toMinutes();
//     }
// }
