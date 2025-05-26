/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 13:16:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 17:00:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.black.BlackEntity;
import com.bytedesk.core.black.event.BlackCreateEvent;
import com.bytedesk.core.black.event.BlackDeleteEvent;
import com.bytedesk.core.ip.black.IpBlacklistRestService;
import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorEventListener {

    private final VisitorRestService visitorService;

    private final IpBlacklistRestService ipBlacklistService;

    // @EventListener
    // public void onVisitorCreateEvent(VisitorCreateEvent event) {
    //     log.info("VisitorEventListener onVisitorCreateEvent: " + event);
    // }

    // @EventListener
    // public void onVisitorUpdateEvent(VisitorUpdateEvent event) {
    //     log.info("VisitorEventListener onVisitorUpdateEvent: " + event);
    //     // VisitorEntity visitor = event.getVisitor();
    // }

    @EventListener
    public void onBlackCreateEvent(BlackCreateEvent event) {
        log.info("VisitorEventListener onBlackCreateEvent: " + event);
        BlackEntity blackEntity = event.getBlackEntity();
        if (blackEntity.getBlockIp()) {
            Optional<VisitorEntity> visitorEntity = visitorService.findByUid(blackEntity.getBlackUid()); 
            if (visitorEntity.isPresent()) {
                // 添加到黑名单
                ipBlacklistService.addToBlacklist(
                    visitorEntity.get().getIp(), 
                    visitorEntity.get().getIpLocation(), 
                    blackEntity.getEndTime(), 
                    blackEntity.getReason(),
                    blackEntity.getBlackUid(),
                    blackEntity.getBlackNickname(), 
                    blackEntity.getUserUid(),
                    blackEntity.getUserNickname(),
                    blackEntity.getOrgUid()
                );
                // 更新访客状态
                visitorService.updateStatus(visitorEntity.get().getUid(), VisitorStatusEnum.BLOCKED.name());
            }
        }
    }

    // @EventListener
    // public void onBlackUpdateEvent(BlackUpdateEvent event) {
    //     log.info("VisitorEventListener onBlackUpdateEvent");
    // }

    @EventListener
    public void onBlackDeleteEvent(BlackDeleteEvent event) {
        log.info("VisitorEventListener onBlackDeleteEvent");
        // 更新访客状态
        visitorService.updateStatus(event.getBlackEntity().getBlackUid(), VisitorStatusEnum.OFFLINE.name());
    }

    // 更新访客在线状态：检测updatedAt时间戳，如果超过五分钟则更新为离线状态
    @EventListener
    public void onQuartzFiveMinEvent(QuartzFiveMinEvent event) {
        // log.info("visitor quartz five min event");
        List<VisitorEntity> visitorList = visitorService.findByStatus(VisitorStatusEnum.ONLINE.name());
        visitorList.forEach(visitor -> {
            // log.info("visitor: {}", visitor.getUid());
            // 使用Duration计算时间差
            if (Duration.between(visitor.getUpdatedAt(), LocalDateTime.now()).toMillis() > 5 * 60 * 1000) {
            // if (System.currentTimeMillis() - visitor.getUpdatedAt().getTime() > 5 * 60 * 1000) {
                // log.info("visitor: {} offline", visitor.getUid());
                visitorService.updateStatus(visitor.getUid(), VisitorStatusEnum.OFFLINE.name());
            }
        });
    }

    @EventListener
    public void onQuartzDay0Event(QuartzDay0Event event) {
        log.info("visitor quartz day 0 event");
        // 每天0点，检查到期的黑名单，并清理
        ipBlacklistService.findByEndTimeBefore(LocalDateTime.now()).forEach(ipBlacklist -> {
            // 修改访客状态
            visitorService.updateStatus(ipBlacklist.getBlackUid(), VisitorStatusEnum.OFFLINE.name());
            // 删除黑名单
            ipBlacklistService.deleteByUid(ipBlacklist.getUid());
        });
    }
    
}
