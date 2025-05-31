/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 10:20:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.llm_webpage.event.WebpageCreateEvent;
import com.bytedesk.kbase.llm_webpage.event.WebpageDeleteEvent;
import com.bytedesk.kbase.llm_webpage.event.WebpageUpdateDocEvent;
import com.bytedesk.kbase.llm_webpage.mq.WebpageMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebpageEventListener {
 
    private final WebpageMessageService webpageMessageService;

    /**
     * 处理网页创建事件
     * 通过消息队列异步处理索引创建
     * 
     * @param event 网页创建事件
     */
    @EventListener
    public void onWebpageCreateEvent(WebpageCreateEvent event) {
        WebpageEntity webpage = event.getWebpage();
        log.info("WebpageEventListener onWebpageCreateEvent: {}", webpage.getTitle());

        // 使用消息队列异步处理索引，避免乐观锁冲突
        webpageMessageService.sendToIndexQueue(webpage.getUid());
    }

    /**
     * 处理网页更新事件
     * 通过消息队列异步更新索引
     * 
     * @param event 网页更新事件
     */
    @EventListener
    public void onWebpageUpdateDocEvent(WebpageUpdateDocEvent event) {
        WebpageEntity webpage = event.getWebpage();
        log.info("WebpageEventListener WebpageUpdateDocEvent: {}", webpage.getTitle());

        // 使用消息队列异步处理索引更新
        webpageMessageService.sendToIndexQueue(webpage.getUid());
    }

    /**
     * 处理网页删除事件
     * 通过消息队列异步删除索引
     * 
     * @param event 网页删除事件
     */
    @EventListener
    public void onWebpageDeleteEvent(WebpageDeleteEvent event) {
        WebpageEntity webpage = event.getWebpage();
        log.info("WebpageEventListener onWebpageDeleteEvent: {}", webpage.getTitle());

        // 删除操作通过消息队列异步处理
        webpageMessageService.sendToDeleteQueue(webpage.getUid());
    }
}

