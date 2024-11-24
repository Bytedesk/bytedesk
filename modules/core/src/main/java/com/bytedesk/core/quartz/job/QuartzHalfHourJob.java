/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 13:26:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 15:31:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.bytedesk.core.config.BytedeskEventPublisher;

import java.io.Serializable;

/**
 * 半小时-运行一次
 *
 * @author kefux.com on 2019/4/20
 */
// @Slf4j
@AllArgsConstructor
@DisallowConcurrentExecution
public class QuartzHalfHourJob extends QuartzJobBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        // log.info("HalfHourJob");
        bytedeskEventPublisher.publishQuartzHalfHourEvent();
    }

   
}
