/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 21:38:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.conference;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.freeswitch.conference.event.FreeSwitchConferenceCreateEvent;
import com.bytedesk.freeswitch.conference.event.FreeSwitchConferenceUpdateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch事件监听器
 */
@Slf4j
@Component
public class FreeSwitchConferenceEventListener {

    
    @EventListener
    public void onFreeSwitchConferenceCreateEvent(FreeSwitchConferenceCreateEvent event) {
        FreeSwitchConferenceEntity conference = event.getConference();
        log.info("收到FreeSwitch会议室创建事件: name={}, maxMembers={}", 
                conference.getConferenceName(), conference.getMaxMembers());
        
        // 这里可以添加额外的处理逻辑，比如：
        // 1. 通知相关用户新的会议室已创建
        // 2. 预分配会议资源
    }
    
    @EventListener
    public void onFreeSwitchConferenceUpdateEvent(FreeSwitchConferenceUpdateEvent event) {
        FreeSwitchConferenceEntity conference = event.getConference();
        log.info("收到FreeSwitch会议室更新事件: name={}, enabled={}", 
                conference.getConferenceName(), conference.getEnabled());
        
        // 这里可以添加额外的处理逻辑
    }
    
    
}
