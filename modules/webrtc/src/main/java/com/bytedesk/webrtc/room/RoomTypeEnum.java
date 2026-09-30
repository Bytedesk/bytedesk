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
package com.bytedesk.webrtc.room;

/**
 * 房间业务类别（2026-09-28 规划收敛，详见 docs/plans/2026-09-22-meet-audio-video-service-on-roomtype-plan.md §5.1）
 *
 * 只描述业务场景，不描述会议创建方式或会中媒体能力：
 * - 即时/定时（原 INSTANT/SCHEDULED）是会议排期方式，不是房间类别；
 * - 屏幕共享（原 SCREEN_SHARE）是会中媒体能力，同一会议可随时开关，不改变房间类型。
 * 旧枚举值已直接删除，不做读取/筛选兼容（用户决策 2026-09-28）。
 */
public enum RoomTypeEnum {
    MEETING,
    AUDIO_SERVICE,
    VIDEO_SERVICE,
    /**
     * 同事间音视频通话房间（2026-09-29 规划 §5.4，单值不拆音频/视频：媒体类型由 WebrtcEntity.type 表达）。
     * 由 MEMBER_CALL 通话 invite 自动建档，不出现在普通会议创建入口。
     */
    MEMBER_CALL;

    /**
     * 解析房间类型：忽略大小写精确匹配；空/未知值统一回退 MEETING。
     */
    public static RoomTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return MEETING;
        }

        for (RoomTypeEnum type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        return MEETING;
    }
}
