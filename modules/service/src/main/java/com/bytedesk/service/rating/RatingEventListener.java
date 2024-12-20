/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-16 13:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 12:39:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.rating;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageEntity;
// import com.bytedesk.core.message.MessageCreateEvent;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.event.MessageUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingEventListener {

    private final RatingService rateService;

    // @EventListener
    // public void onMessageCreateEvent(MessageCreateEvent event) {
    //     Message message = event.getMessage();
    //     log.info("message rate create event: {}", message.getContent());
    // }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        // 判断消息状态，生成评价记录
        if (message.getStatus().equals(MessageStatusEnum.RATE_SUBMIT.name())) {
            log.info("message rate update event: {}", message);
            // 
            RatingMessageExtra extra = JSON.parseObject(message.getContent(), RatingMessageExtra.class);
            //
            RatingRequest request = RatingRequest.builder()
                    .rating(extra.getScore())
                    .comment(extra.getContent())
                    .threadTopic(message.getThreadTopic())
                    .user(message.getUser())
                    .build();
            request.setType(RatingTypeEnum.THREAD.name());
            request.setOrgUid(extra.getOrgUid());
            //
            rateService.create(request);
        }
    }

}
