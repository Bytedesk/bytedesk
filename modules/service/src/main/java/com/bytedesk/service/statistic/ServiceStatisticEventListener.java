/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-10 23:11:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 13:56:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.service.statistic_thread.StatisticThreadCreateEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO:
 * 统计thread和message的创建和更新
 */
@Slf4j
@Component
public class ServiceStatisticEventListener {

    @EventListener
    public void onStatisticThreadCreateEvent(StatisticThreadCreateEvent event) {
        log.info("ServiceStatisticEventListener onServiceStatisticThreadCreateEvent: {}", event);
    }

    // @EventListener
    // public void onThreadCreateEvent(ThreadCreateEvent event) {
    // Thread thread = event.getThread();
    // log.info("thread log ThreadCreateEvent: {}", thread.getUid());
    // // if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
    // // // TODO:
    // // } else if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
    // // // TODO:
    // // } else {
    // // // TODO:
    // // }
    // }

    // @EventListener
    // public void onThreadUpdateEvent(ThreadUpdateEvent event) {
    // Thread thread = event.getThread();
    // log.info("thread log onThreadUpdateEvent: {}", thread.getUid());
    // //
    // // if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
    // // // TODO:
    // // } else if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
    // // // TODO:
    // // } else {
    // // // TODO:
    // // }
    // }

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (message.getType().equals(MessageTypeEnum.STREAM.name())) {
            return;
        }
        // log.info("message unread create event: {}", message.getContent());
    }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        // log.info("message unread update event: {}", message.getContent());
        if (message.getType().equals(MessageTypeEnum.STREAM.name())) {
            return;
        }
    }

}
