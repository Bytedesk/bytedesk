/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 11:03:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:27:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlackEventListener {

    private final BlackRestService blackRestService;

     @EventListener
    public void onQuartzDay0Event(QuartzDay0Event event) {
        // 每天0点，检查到期的黑名单，并清理
        blackRestService.findByEndTimeBefore(BdDateUtils.now()).forEach(black -> {
            blackRestService.deleteByUid(black.getUid());
        });
    }
    
}
