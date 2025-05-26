/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 11:12:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 16:49:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.black;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.black.BlackEntity;
import com.bytedesk.core.black.event.BlackCreateEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IpBlacklistEventListener {

    /**
     * @{VisitorEventListener}
     * @param event
     */
     @EventListener
    public void onBlackCreateEvent(BlackCreateEvent event) {
        log.info("VisitorEventListener onBlackCreateEvent: " + event);
        BlackEntity blackEntity = event.getBlackEntity();
        if (blackEntity.getBlockIp()) {
                // 添加到黑名单
                // ipBlacklistService.addToBlacklist(
                //     visitorEntity.get().getIp(), 
                //     visitorEntity.get().getIpLocation(), 
                //     blackEntity.getEndTime(), 
                //     blackEntity.getReason(),
                //     blackEntity.getBlackUid(),
                //     blackEntity.getBlackNickname(), 
                //     blackEntity.getUserUid(),
                //     blackEntity.getUserNickname(),
                //     blackEntity.getOrgUid()
                // );
        }
    }

}
