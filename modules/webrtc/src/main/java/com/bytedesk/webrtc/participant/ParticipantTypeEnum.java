/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:57:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.webrtc.participant;

public enum ParticipantTypeEnum {
    THREAD,
    VISITOR,
    CUSTOMER,
    TICKET,
    /** 会议参与者（RoomEntity 会议的参会记录） */
    MEETING,
    /** 音频客服通话参与者（2026-09-29 规划 C 线，对齐 RoomTypeEnum.AUDIO_SERVICE） */
    AUDIO_SERVICE,
    /** 视频客服通话参与者（2026-09-29 规划 C 线，对齐 RoomTypeEnum.VIDEO_SERVICE） */
    VIDEO_SERVICE,
    /** 同事间音视频通话参与者（2026-09-29 规划 C 线，对齐 RoomTypeEnum.MEMBER_CALL） */
    MEMBER_CALL
}
