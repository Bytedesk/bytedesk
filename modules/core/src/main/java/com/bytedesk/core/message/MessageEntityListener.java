/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 16:35:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-05 09:58:13
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

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageEntityListener {

    @PostPersist
    public void onPostPersist(Message message) {
        // log.info("message MessageEntityListener: onPostPersist");
        Message clonedMessage = SerializationUtils.clone(message);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishMessageCreateEvent(clonedMessage);
    }

    @PostUpdate
    public void onPostUpdate(Message message) {
        // log.info("message MessageEntityListener: onPostUpdate");
        Message clonedMessage = SerializationUtils.clone(message);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishMessageUpdateEvent(clonedMessage);
    }
    
}
