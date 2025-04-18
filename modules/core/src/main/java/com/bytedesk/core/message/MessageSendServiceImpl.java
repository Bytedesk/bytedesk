/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 16:27:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-27 20:47:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.annotation.BlackIpFilter;
import com.bytedesk.core.annotation.BlackUserFilter;
import com.bytedesk.core.annotation.TabooJsonFilter;
import com.bytedesk.core.annotation.TabooProtobufFilter;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageSendServiceImpl implements IMessageSendService {

    @BlackIpFilter(title = "black", action = "sendJsonMessage")
    @BlackUserFilter(title = "black", action = "sendJsonMessage")
    @TabooJsonFilter(title = "敏感词", action = "sendJsonMessage")
    @Override
    public void sendJsonMessage(String json) {
        // log.debug("sendJsonMessage: {}", json);
        publishMessageJsonEvent(json);
    }

    @BlackIpFilter(title = "black", action = "sendProtobufMessage")
    @BlackUserFilter(title = "black", action = "sendProtobufMessage")
    @TabooProtobufFilter(title = "敏感词", action = "sendProtobufMessage")
    @Override
    public void sendProtobufMessage(MessageProtobuf messageProtobuf) {
        String json = JSON.toJSONString(messageProtobuf);
        // log.debug("sendProtobufMessage: {}", json);
        publishMessageJsonEvent(json);
    }

    public void publishMessageJsonEvent(String json) {
        // log.debug("publishMessageJsonEvent: {}", json);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishMessageJsonEvent(json);
    }
    
}
