/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 15:11:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-04 11:19:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TopicEventListener {

    private final TopicService topicService;

    private final TopicCacheService topicCacheService;
    // 此处使用static，否则在定时器中无法读取初始化时期的数据
    // private final static TopicCacheService topicCacheService = new TopicCacheService();
    // private final static String cacheKey = "topicList";

    @EventListener
    public void onTopicCreateEvent(TopicCreateEvent event) {
        log.info("topic onTopicCreateEvent: {}", event);
        // 注意：来自member和department的创建事件，大量事件会导致topicService.create()方法被调用多次，
        // 导致乐观锁冲突。所以，将事件缓存起来，然后定时刷新到数据库中
        // topicService.create(event.getTopic(), event.getUserUid());
        //
        TopicRequest request = TopicRequest.builder()
                .topic(event.getTopic())
                .userUid(event.getUserUid())
                .build();
        topicCacheService.pushRequest(request);
    }

    @EventListener
    public void onTopicUpdateEvent(TopicUpdateEvent event) {
        log.info("topic onTopicUpdateEvent: {}", event);
        // topicService.update(event.getTopic(), event.getUserUid());
    }
    
    @EventListener
    public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
        // 定时刷新缓存中的topic事件到数据库中
        List<String> list = topicCacheService.getList();
        if (list != null) {
            list.forEach(item -> {
                log.info("topic onQuartzFiveSecondEvent {}", item);
                TopicRequest topicRequest = JSON.parseObject(item, TopicRequest.class);
                topicService.create(topicRequest);
            });
        }
    }

}
