/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-19 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-19 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.webrtc.participant;

/**
 * 会议参与者状态：在会 / 已离会
 */
public enum ParticipantStatusEnum {

    /** 已加入会议（在会中） */
    JOINED,

    /** 已离开会议（离会） */
    LEFT;

    public static ParticipantStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return JOINED;
        }
        for (ParticipantStatusEnum status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return JOINED;
    }
}
