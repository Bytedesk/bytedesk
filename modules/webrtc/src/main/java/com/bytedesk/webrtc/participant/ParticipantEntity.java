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
package com.bytedesk.webrtc.participant;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Participant entity for content categorization and organization
 * Provides participantging functionality for various system entities
 *
 * Database Table: bytedesk_core_participant
 * Purpose: Stores participant definitions, colors, and organization settings
 *
 * 会议参与者（type=MEETING）：每次加入会议生成一条参会记录（类似 CDR），
 * 记录参与者信息、加入/离开会议时间与参会时长；继承 BaseEntity 的 uid/orgUid/userUid/createdAt 等。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ParticipantEntityListener.class})
@Table(name = "bytedesk_core_participant")
public class ParticipantEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the participant（会议场景下为用户展示名/昵称）
     */
    private String name;

    /**
     * Description of the participant
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of participant (MEETING, THREAD, VISITOR, CUSTOMER, TICKET, etc.)
     */
    @Builder.Default
    @Column(name = "participant_type")
    private String type = ParticipantTypeEnum.CUSTOMER.name();

    /**
     * 关联会议室 uid（RoomEntity.uid，type=MEETING 时使用）
     */
    @Column(name = "room_uid", length = 64)
    private String roomUid;

    /**
     * Janus AudioBridge 分配的参与者 id（joined 事件返回的 id，用于回调对齐；可为空）
     */
    @Column(name = "janus_participant_id")
    private Long janusParticipantId;

    /**
     * 加入会议时间
     */
    @Column(name = "joined_at")
    private ZonedDateTime joinedAt;

    /**
     * 离开会议时间（在会中为 null）
     */
    @Column(name = "left_at")
    private ZonedDateTime leftAt;

    /**
     * 参会时长（秒，离会时由后端计算冗余存储，便于统计查询）
     */
    @Column(name = "duration")
    private Long duration;

    /**
     * 参与者状态：JOINED（在会）/ LEFT（已离会）
     */
    @Builder.Default
    @Column(name = "participant_status", length = 32)
    private String status = ParticipantStatusEnum.JOINED.name();

    /**
     * 是否会议主持人（创建会议室的用户加入时标记）
     */
    @Builder.Default
    @Column(name = "is_host")
    private Boolean host = false;

}
