/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 17:29:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 17:29:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.pubsub;

import com.bytedesk.core.redis.RedisConsts;

public class RedisPubsubConst {
    private RedisPubsubConst() {}
    
    public static final String BYTEDESK_PUBSUB_CHANNEL_STRING = RedisConsts.BYTEDESK_REDIS_PREFIX + "pubsub";
    public static final String BYTEDESK_PUBSUB_CHANNEL_OBJECT = RedisConsts.BYTEDESK_REDIS_PREFIX + "pubsub_object";
    public static final String BYTEDESK_PUBSUB_KEY_EXPIRE = RedisConsts.BYTEDESK_REDIS_PREFIX + "__keyevent@*__:expired";
}
