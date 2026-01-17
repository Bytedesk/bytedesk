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
//  * Schedule Entity
//  *
//  * 会议日程实体类，用于管理预约会议信息
//  *
//  * Database Table: bytedesk_conference_schedule
//  * Purpose: 存储会议日程、重复规则、提醒设置等
//  */
// @Entity
// @Data
// @SuperBuilder
// @Accessors(chain = true)
// @EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(
//     name = "bytedesk_conference_schedule",
//     indexes = {
//         @Index(name = "idx_schedule_uid", columnList = "uuid"),
//         @Index(name = "idx_schedule_conference_uid", columnList = "conference_uid"),
//         @Index(name = "idx_schedule_host_uid", columnList = "host_uid"),
//         @Index(name = "idx_schedule_start_time", columnList = "start_time")
//     }
// )
// public class ScheduleEntity extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     /**
//      * 会议ID
//      */
//     @Column(name = "conference_uid", length = 64)
//     private String conferenceUid;

//     /**
//      * 日程标题
//      */
//     @Column(length = 256)
//     private String title;

//     /**
//      * 日程描述
//      */
//     @Builder.Default
//     @Column(length = 1024)
//     private String description = I18Consts.I18N_DESCRIPTION;

//     /**
//      * 主持人ID
//      */
//     @Column(name = "host_uid", nullable = false, length = 64)
//     private String hostUid;

//     /**
//      * 主持人昵称
//      */
//     @Column(name = "host_nickname", length = 128)
//     private String hostNickname;

//     /**
//      * 开始时间
//      */
//     @Column(name = "start_time", nullable = false)
//     private LocalDateTime startTime;

//     /**
//      * 结束时间
//      */
//     @Column(name = "end_time", nullable = false)
//     private LocalDateTime endTime;

//     /**
//      * 是否全天
//      */
//     @Builder.Default
//     @Column(name = "all_day")
//     private Boolean allDay = false;

//     /**
//      * 时区
//      */
//     @Builder.Default
//     @Column(name = "timezone", length = 64)
//     private String timezone = "Asia/Shanghai";

//     /**
//      * 地点
//      */
//     @Column(name = "location", length = 256)
//     private String location;

//     /**
//      * 重复类型
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "repeat_type", length = 32)
//     private RepeatType repeatType;

//     /**
//      * 重复间隔（如 "1,3,5" 表示周一、三、五）
//      */
//     @Column(name = "repeat_interval", length = 64)
//     private String repeatInterval;

//     /**
//      * 重复结束日期
//      */
//     @Column(name = "repeat_end_date")
//     private LocalDateTime repeatEndDate;

//     /**
//      * 提醒类型
//      */
//     @Enumerated(EnumType.STRING)
//     @Column(name = "reminder_type", length = 32)
//     private ReminderType reminderType;

//     /**
//      * 邀请的参与者ID列表（JSON格式存储）
//      */
//     @Column(name = "participants", columnDefinition = "TEXT")
//     private String participants;

//     /**
//      * 是否已发送提醒
//      */
//     @Builder.Default
//     @Column(name = "reminder_sent")
//     private Boolean reminderSent = false;

//     /**
//      * 重复类型枚举
//      */
//     public enum RepeatType {
//         NONE,           // 不重复
//         DAILY,          // 每天
//         WEEKLY,         // 每周
//         MONTHLY,        // 每月
//         CUSTOM          // 自定义
//     }

//     /**
//      * 提醒类型枚举
//      */
//     public enum ReminderType {
//         NONE,           // 不提醒
//         MINUTES_5,      // 5分钟前
//         MINUTES_15,     // 15分钟前
//         MINUTES_30,     // 30分钟前
//         HOUR_1,         // 1小时前
//         HOURS_2,        // 2小时前
//         DAY_1           // 1天前
//     }

//     // Transient fields (不持久化到数据库)

//     /**
//      * 参与者列表（运行时）
//      */
//     @Transient
//     @Builder.Default
//     private Set<String> participantSet = new HashSet<>();

//     /**
//      * 是否重复日程
//      */
//     public boolean isRecurring() {
//         return repeatType != null && repeatType != RepeatType.NONE;
//     }

//     /**
//      * 是否有提醒
//      */
//     public boolean hasReminder() {
//         return reminderType != null && reminderType != ReminderType.NONE;
//     }

//     /**
//      * 是否即将开始（15分钟内）
//      */
//     public boolean isUpcoming() {
//         return startTime.isAfter(LocalDateTime.now()) &&
//                startTime.isBefore(LocalDateTime.now().plusMinutes(15));
//     }

//     /**
//      * 是否已过期
//      */
//     public boolean isPast() {
//         return endTime.isBefore(LocalDateTime.now());
//     }

//     /**
//      * 是否正在进行
//      */
//     public boolean isActive() {
//         LocalDateTime now = LocalDateTime.now();
//         return !now.isBefore(startTime) && !now.isAfter(endTime);
//     }

//     /**
//      * 获取会议时长（分钟）
//      */
//     public long getDurationMinutes() {
//         return java.time.Duration.between(startTime, endTime).toMinutes();
//     }
// }
