/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-22 15:06:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class MqttMessageIdService {

    // 使用AtomicInteger确保线程安全
    // MQTT协议要求messageId在1-65535之间
    private final AtomicInteger counter = new AtomicInteger(1);
    
    // MQTT协议最大messageId值
    private static final int MAX_MESSAGE_ID = 65535;

    /**
     * 获取下一个MQTT消息ID
     * 确保线程安全且符合MQTT协议规范（1-65535）
     * 
     * @return 有效的MQTT消息ID
     */
    public int getNextMessageId() {
        int current, next;
        do {
            current = counter.get();
            // 如果达到最大值，重置为1
            next = (current >= MAX_MESSAGE_ID) ? 1 : current + 1;
        } while (!counter.compareAndSet(current, next));
        
        return next;
    }
    
    /**
     * 重置消息ID计数器（主要用于测试）
     */
    public void reset() {
        counter.set(1);
    }
    
    /**
     * 获取当前计数器值（主要用于测试和监控）
     */
    public int getCurrentCounter() {
        return counter.get();
    }
}
