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
// import java.util.HashSet;
// import java.util.Set;

// import com.bytedesk.core.base.BaseEntity;
// import com.bytedesk.core.constant.I18Consts;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.Index;
// import jakarta.persistence.Table;
// import jakarta.persistence.Transient;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import lombok.experimental.Accessors;
// import lombok.experimental.SuperBuilder;

// /**
//  * Conference Entity
//  *
//  * 会议实体类，用于管理会议信息
//  *
//  * Database Table: bytedesk_conference
//  * Purpose: 存储会议基本信息、状态、参与者等
//  */
// @Entity
// @Data
// @SuperBuilder
// @Accessors(chain = true)
// @EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(
//     name = "bytedesk_conference",
//     indexes = {
//         @Index(name = "idx_conference_uid", columnList = "uuid"),
//         @Index(name = "idx_conference_host_uid", columnList = "host_uid"),
//         @Index(name = "idx_conference_room_id", columnList = "room_id"),
//         @Index(name = "idx_conference_status", columnList = "status"),
//         @Index(name = "idx_conference_start_time", columnList = "start_time")
//     }
// )
// public class MeetingEntity extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     /**
//      * 会议主题
//      */
//     @Column(length = 256)
//     private String topic;

//     /**
//      * 会议描述
//      */
//     @Builder.Default
//     @Column(length = 1024)
//     private String description = I18Consts.I18N_DESCRIPTION;

//     /**
//      * 主持人用户ID
//      */
//     @Column(name = "host_uid", nullable = false, length = 64)
//     private String hostUid;

//     /**
//      * 主持人昵称
//      */
//     @Column(name = "host_nickname", length = 128)
//     private String hostNickname;

//     /**
//      * 会议室ID
//      */
//     @Column(name = "room_id", length = 64)
//     private String roomId;

//     /**
//      * 会议密码
//      */
//     @Column(name = "password", length = 64)
//     private String password;

//     /**
//      * 会议类型
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "conference_type", length = 32)
//     private ConferenceType type;

//     /**
//      * 会议状态
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(length = 32)
//     private ConferenceStatus status;

//     /**
//      * 会议开始时间
//      */
//     @Column(name = "start_time")
//     private LocalDateTime startTime;

//     /**
//      * 会议结束时间
//      */
//     @Column(name = "end_time")
//     private LocalDateTime endTime;

//     /**
//      * 会议时长（分钟）
//      */
//     @Builder.Default
//     @Column(name = "duration")
//     private Integer duration = 60;

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
//      * 入会是否静音
//      */
//     @Builder.Default
//     @Column(name = "mute_on_entry")
//     private Boolean muteOnEntry = false;

//     /**
//      * 入会是否开启视频
//      */
//     @Builder.Default
//     @Column(name = "video_on_entry")
//     private Boolean videoOnEntry = true;

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
//      * 是否正在录制
//      */
//     @Builder.Default
//     @Column(name = "recording")
//     private Boolean recording = false;

//     /**
//      * 录制文件路径
//      */
//     @Column(name = "record_path", length = 512)
//     private String recordPath;

//     /**
//      * 是否允许屏幕共享
//      */
//     @Builder.Default
//     @Column(name = "allow_screen_share")
//     private Boolean allowScreenShare = true;

//     /**
//      * 是否允许聊天
//      */
//     @Builder.Default
//     @Column(name = "allow_chat")
//     private Boolean allowChat = true;

//     /**
//      * 会议类型枚举
//      */
//     public enum ConferenceType {
//         QUICK_MEETING,       // 快速会议
//         SCHEDULED_MEETING,   // 预约会议
//         RECURRING_MEETING    // 周期性会议
//     }

//     /**
//      * 会议状态枚举
//      */
//     public enum ConferenceStatus {
//         NOT_STARTED,         // 未开始
//         IN_PROGRESS,         // 进行中
//         ENDED,               // 已结束
//         CANCELLED            // 已取消
//     }

//     // Transient fields (不持久化到数据库)

//     /**
//      * 参与者列表（运行时）
//      */
//     @Transient
//     @Builder.Default
//     private Set<String> participants = new HashSet<>();

//     /**
//      * 会议是否活跃
//      */
//     public boolean isActive() {
//         return status == ConferenceStatus.IN_PROGRESS;
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
//      * 添加参与者
//      */
//     public void addParticipant(String userUid) {
//         if (participants.add(userUid)) {
//             currentParticipants++;
//         }
//     }

//     /**
//      * 移除参与者
//      */
//     public void removeParticipant(String userUid) {
//         if (participants.remove(userUid)) {
//             currentParticipants--;
//         }
//     }
// }
