/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-28 13:07:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 15:28:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz.job;

import java.io.Serializable;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.bytedesk.core.quartz.service.QuartzEventPublisher;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 5 min job
 * 5分钟定时任务
 * 
 */
// @Slf4j
@AllArgsConstructor
@DisallowConcurrentExecution
public class QuartzFiveMinJob extends QuartzJobBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final QuartzEventPublisher quartzEventPublisher;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        // log.info("FiveMinJob");
        quartzEventPublisher.publishQuartzFiveMinEvent();
    }

}
