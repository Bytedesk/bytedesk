/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-28 06:48:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-31 17:01:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;

public class ThreadUtils {
    
    public static ThreadProtobuf getThreadProtobuf(String topic, ThreadTypeEnum type, UserProtobuf user) {
        return ThreadProtobuf.builder()
                .topic(topic)
                .type(type)
                .user(user)
        .build();
    }
}
