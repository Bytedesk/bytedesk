/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-21 10:31:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.booking;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.ai.alibaba.demo.booking.event.BookingCreateEvent;
import com.bytedesk.ai.alibaba.demo.booking.event.BookingUpdateEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BookingEntityListener {

    @PostPersist
    public void onPostPersist(BookingEntity booking) {
        log.info("onPostPersist: {}", booking);
        BookingEntity cloneBooking = SerializationUtils.clone(booking);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new BookingCreateEvent(cloneBooking));
    }

    @PostUpdate
    public void onPostUpdate(BookingEntity booking) {
        log.info("onPostUpdate: {}", booking);
        BookingEntity cloneBooking = SerializationUtils.clone(booking);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new BookingUpdateEvent(cloneBooking));
    }
    
}
