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
package com.bytedesk.call.esl_event;

import java.util.Locale;

public enum EslEventTypeEnum {
    CHANNEL,
    CUSTOM,
    DTMF,
    API,
    PRESENCE,
    BACKGROUND_JOB,
    HEARTBEAT,
    SCHEDULE,
    SYSTEM,
    MESSAGE,
    UNKNOWN;

    public static EslEventTypeEnum fromEventName(String eventName) {
        if (eventName == null || eventName.isBlank()) {
            return UNKNOWN;
        }

        String normalized = eventName.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("CHANNEL_")) {
            return CHANNEL;
        }

        return switch (normalized) {
            case "CUSTOM" -> CUSTOM;
            case "DTMF" -> DTMF;
            case "API" -> API;

            case "PRESENCE_IN", "PRESENCE_OUT", "PRESENCE_PROBE", "NOTIFY_IN", "PHONE_FEATURE", "PHONE_FEATURE_SUBSCRIBE" -> PRESENCE;

            case "BACKGROUND_JOB" -> BACKGROUND_JOB;
            case "HEARTBEAT" -> HEARTBEAT;

            case "ADD_SCHEDULE", "DEL_SCHEDULE", "EXE_SCHEDULE", "RE_SCHEDULE" -> SCHEDULE;

            case "STARTUP", "SHUTDOWN", "SESSION_CRASH", "MODULE_LOAD", "MODULE_UNLOAD", "RELOADXML", "TRAP", "PRIVATE_COMMAND" -> SYSTEM;

            case "MESSAGE", "MESSAGE_WAITING", "MESSAGE_QUERY", "SEND_MESSAGE", "RECV_MESSAGE", "PUBLISH", "UNPUBLISH", "NOTIFY" -> MESSAGE;

            default -> UNKNOWN;
        };
    }
}
