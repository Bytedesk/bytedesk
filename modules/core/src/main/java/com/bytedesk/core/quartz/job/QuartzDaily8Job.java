/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 13:07:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 15:32:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz.job;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.bytedesk.core.quartz.QuartzEventPublisher;

import java.io.Serializable;

/**
 * 1天-运行一次，每天8点
 *
 * @author kefux.com on 2019/4/20
 */
// @Slf4j
@AllArgsConstructor
@DisallowConcurrentExecution
public class QuartzDaily8Job extends QuartzJobBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final QuartzEventPublisher quartzEventPublisher;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        // log.info("Daily8Job");
        quartzEventPublisher.publishQuartzDay8Event();
    }
}
