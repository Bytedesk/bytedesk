/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-23 15:20:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 16:33:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

public class RedisConsts {
    private RedisConsts() {}
    
    //
    public static final String BYTEDESK_REDIS_PREFIX = "bytedeskim:";
    // 
    public static final String CONNECTED_MQTT_CLIENT_IDS = BYTEDESK_REDIS_PREFIX + "connected-mqtt-client-ids";

    // 消息未读相关常量
    public static final String MESSAGE_UNREAD_PREFIX = BYTEDESK_REDIS_PREFIX + "message_unread:";
    public static final String MESSAGE_UNREAD_EXISTS_VALUE = "1";

    // 自动回复去重相关常量
    public static final String AUTO_REPLY_PROCESSED_PREFIX = BYTEDESK_REDIS_PREFIX + "auto_reply_processed:";
    public static final String AUTO_REPLY_PROCESSED_VALUE = "1";

    // 登录失败重试相关常量
    public static final String LOGIN_FAILED_PREFIX = BYTEDESK_REDIS_PREFIX + "login_failed:";
    public static final String LOGIN_LOCKED_PREFIX = BYTEDESK_REDIS_PREFIX + "login_locked:";
    public static final String LOGIN_LOCKED_VALUE = "1";

}
