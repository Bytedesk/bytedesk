/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 13:16:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 17:49:00
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

    private final VisitorRestService visitorRestService;

    private final IpBlacklistRestService ipBlacklistRestService;

    @EventListener
    public void onBlackCreateEvent(BlackCreateEvent event) {
        log.info("VisitorEventListener onBlackCreateEvent: " + event);
        BlackEntity blackEntity = event.getBlackEntity();
        if (blackEntity.getBlockIp()) {
            Optional<VisitorEntity> visitorEntity = visitorRestService.findByUid(blackEntity.getBlackUid());
            if (visitorEntity.isPresent()) {
                // 添加到黑名单
                ipBlacklistRestService.addToBlacklist(visitorEntity.get().getIp(), visitorEntity.get().getIpLocation(), blackEntity);
                // 更新访客状态
                visitorRestService.updateStatus(visitorEntity.get().getUid(), VisitorStatusEnum.BLOCKED.name());
            }
        }
    }

    @EventListener
    public void onBlackDeleteEvent(BlackDeleteEvent event) {
        log.info("VisitorEventListener onBlackDeleteEvent");
        BlackEntity blackEntity = event.getBlackEntity();
        //
        Optional<VisitorEntity> visitorEntity = visitorRestService.findByUid(blackEntity.getBlackUid());
        if (visitorEntity.isPresent()) {
            // 从黑名单中删除
            ipBlacklistRestService.deleteByIp(visitorEntity.get().getIp());
        }
        // 更新访客状态
        visitorRestService.updateStatus(event.getBlackEntity().getBlackUid(), VisitorStatusEnum.OFFLINE.name());
    }

    // 更新访客在线状态：检测updatedAt时间戳，如果超过五分钟则更新为离线状态
    @EventListener
    public void onQuartzFiveMinEvent(QuartzFiveMinEvent event) {
        // log.info("visitor quartz five min event");
        List<VisitorEntity> visitorList = visitorRestService.findByStatus(VisitorStatusEnum.ONLINE.name());
        visitorList.forEach(visitor -> {
            // log.info("visitor: {}", visitor.getUid());
            // 使用Duration计算时间差
            if (Duration.between(visitor.getUpdatedAt(), LocalDateTime.now()).toMillis() > 5 * 60 * 1000) {
                // if (System.currentTimeMillis() - visitor.getUpdatedAt().getTime() > 5 * 60 *
                // 1000) {
                // log.info("visitor: {} offline", visitor.getUid());
                visitorRestService.updateStatus(visitor.getUid(), VisitorStatusEnum.OFFLINE.name());
            }
        });
    }

    @EventListener
    public void onQuartzDay0Event(QuartzDay0Event event) {
        log.info("visitor quartz day 0 event");
        // 每天0点，检查到期的黑名单，并清理
        ipBlacklistRestService.findByEndTimeBefore(LocalDateTime.now()).forEach(ipBlacklist -> {
            // 修改访客状态
            visitorRestService.updateStatus(ipBlacklist.getBlackUid(), VisitorStatusEnum.OFFLINE.name());
            // 删除黑名单
            ipBlacklistRestService.deleteByUid(ipBlacklist.getUid());
        });
    }

}
