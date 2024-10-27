/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 16:27:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 23:41:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageSendServiceImpl implements IMessageSendService {

    @Override
    public void sendJsonMessage(String json) {
        log.debug("sendJsonMessage: {}", json);
        publishMessageJsonEvent(json);
    }

    @Override
    public void sendProtobufMessage(MessageProtobuf messageProtobuf) {
        String json = JSON.toJSONString(messageProtobuf);
        log.debug("sendProtobufMessage: {}", json);
        publishMessageJsonEvent(json);
    }

    public void publishMessageJsonEvent(String json) {
        // log.debug("publishMessageJsonEvent: {}", json);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishMessageJsonEvent(json);
    }
    
}
