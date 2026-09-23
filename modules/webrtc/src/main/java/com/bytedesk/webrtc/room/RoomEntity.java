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
package com.bytedesk.webrtc.room;

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
 * Room entity for content categorization and organization
 * Provides roomging functionality for various system entities
 * 
 * Database Table: bytedesk_webrtc_room
 * Purpose: Stores room definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({RoomEntityListener.class})
@Table(name = "bytedesk_webrtc_room")
public class RoomEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the room
     */
    private String name;

    /**
     * Description of the room
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Public invite identifier used as the meeting number / share code.
     */
    @Column(name = "invite_uid", unique = true, length = 64)
    private String inviteUid;

    /**
     * Type of room for meeting scenarios.
     */
    @Builder.Default
    @Column(name = "room_type")
    private String type = RoomTypeEnum.MEETING.name();

    /**
     * 会议最大同时发布（publish）人数上限：Janus videoroom 的 publishers 上限，
     * 含纯音频发布者（统一会议默认以纯音频 publisher 身份加入）。
     * 默认 24；创建/更新时可配置（建议档位 6/12/24，取值不限于固定集合以便后续扩展）。
     * 历史房间该列可能为 NULL，读取侧需回退默认值
     */
    @Builder.Default
    @Column(name = "max_participants")
    private Integer maxParticipants = 24;

}
