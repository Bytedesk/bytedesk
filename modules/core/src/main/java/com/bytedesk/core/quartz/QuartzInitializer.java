/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 22:35:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class QuartzInitializer {

    private final QuartzRepository quartzRepository;

    private final QuartzRestService quartzRestService;

    @PostConstruct
    public void init() {

        if (quartzRepository.count() > 0) {
            return;
        }

        //
        String jobName = "test1name";
        String group = "testGroup";
        QuartzRequest quartzRequest = QuartzRequest.builder()
                .jobName(jobName)
                .jobGroup(group)
                .jobClassName("com.bytedesk.core.quartz.QuartzJob")
                .jobMethodName("test1")
                .description("quartz test")
                .cronExpression("*/5 * * * * ?")
                .triggerName(jobName + "trigger")
                .triggerGroup(group)
                .triggerType("cron")
                .triggerState("started")
                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                .build();
                quartzRestService.create(quartzRequest);
        
    }
    
}
