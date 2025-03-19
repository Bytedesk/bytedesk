/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 08:50:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.thread.event.ThreadUpdateEvent;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorThreadEventListener {

    private final VisitorThreadService visitorThreadService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor ThreadCreateEvent: {}, type {}", thread.getUid(), thread.getType());
        // 仅同步客服会话
        if (thread.isCustomerService()) {
            visitorThreadService.copyFromThread(event.getThread());
        } else {
            log.info("visitor ThreadCreateEvent not isCustomerService: {}, type {}", thread.getUid(), thread.getType());
        }
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor onThreadUpdateEvent: {}", thread.getUid());
        // 更新visitor_thread表
        if (thread.isCustomerService()) {
            visitorThreadService.update(event.getThread());
        }
    }

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor onThreadCloseEvent: {}", thread.getUid());
        if (thread.isAutoClose()) {
            // TODO: 自动关闭，根据会话类型显示提示语
            if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {

            } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
                
            } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
                
            }
        } else {
            // TODO: 非自动关闭，客服手动关闭，显示客服关闭提示语
            // UserProtobuf agentObject = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        }

        // 发送消息
        // MessageTypeEnum messageTypeEnum = threadRequest.getAutoClose() ? MessageTypeEnum.AUTO_CLOSED
        //         : MessageTypeEnum.AGENT_CLOSED;
        // MessageProtobuf messageProtobuf = MessageUtils.createThreadMessage(uidUtils.getUid(),
        //         updateThread,
        //         messageTypeEnum,
        //         content);
        // messageSendService.sendProtobufMessage(messageProtobuf);
        
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("visitor_thread quartz one min event: " + event);
        // auto close thread
        visitorThreadService.autoCloseThread();
    }

}
