/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 14:24:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.webrtc.participant;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    // @Builder.Default
    // private String type = ParticipantTypeEnum.CUSTOMER.name();

    /** 关联会议室 uid（type=MEETING） */
    private String roomUid;

    /** 关联通话 uid（type=AUDIO_SERVICE/VIDEO_SERVICE/MEMBER_CALL，按通话查询时使用） */
    private String callUid;

    /** Janus AudioBridge 参与者 id（前端 joined 后回传，可选） */
    private Long janusParticipantId;

    /** 是否主持人（可选，默认 false；创建者加入时传 true） */
    private Boolean host;

}
