/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 17:13:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 17:23:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisPubsubObjectListener implements MessageListener {
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("RedisPubsubObjectListener onMessage: " + new String(message.getBody()));
    }

    

}
