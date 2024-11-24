/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-20 17:25:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 17:32:43
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

/**
 * 需要在redis配置文件中添加如下配置，开启过期事件：
 * notify-keyspace-events Ex
 * 或者，在redis cli中执行如下命令：
 * CONFIG SET notify-keyspace-events Ex
 */
@Slf4j
@Component
public class RedisKeyExpirationListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("Expired key: {}", expiredKey);
        // 处理过期的 clientId
        handleExpiredClientId(expiredKey);
    }

    private void handleExpiredClientId(String clientId) {
        // 处理逻辑，例如从 MqttSessionService 中移除过期的会话
        log.info("Handling expired clientId: {}", clientId);
    }
}