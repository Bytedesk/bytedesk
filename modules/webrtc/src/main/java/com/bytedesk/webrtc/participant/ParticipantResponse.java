/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:36:28
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

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String type;

    /** 关联会议室 uid */
    private String roomUid;

    /** 关联通话 uid（type=AUDIO_SERVICE/VIDEO_SERVICE/MEMBER_CALL 时使用） */
    private String callUid;

    /** Janus AudioBridge 参与者 id */
    private Long janusParticipantId;

    /** 加入会议时间 */
    private ZonedDateTime joinedAt;

    /** 离开会议时间（在会中为 null） */
    private ZonedDateTime leftAt;

    /** 参会时长（秒） */
    private Long duration;

    /** 参与者状态：JOINED / LEFT */
    private String status;

    /** 是否主持人 */
    private Boolean host;

}
