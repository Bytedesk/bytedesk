/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 07:51:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 12:31:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.service.queue_member.event.QueueMemberCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class QueueMemberEventListener {


    @EventListener
    public void onQueueMemberCreateEvent(QueueMemberCreateEvent event) {
        log.info("queue member create event ");
    }    

    // private QueueMemberRestService counterService;
    // @EventListener
    // public void onQuartzDay0Event(QuartzDay0Event event) {
    //     log.info("counter quartz day0 event ");
    //     // counterService.deleteAll();
    // }
}
