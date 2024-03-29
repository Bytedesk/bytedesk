/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-28 13:05:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 13:23:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.core.quartz.job.FiveSecondJob;

/**
 * <a href=
 * "https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-quartz.html">SpringBoot官方说明</a>
 * <a href=
 * "https://www.quartz-scheduler.org/documentation/2.3.1-SNAPSHOT/tutorials/index.html">Quartz官方文档</a>
 * 
 * @Description:
 * @Version: 1.0
 */
@Configuration
public class QuartzConfig {

    /**
     * 每5秒钟运行一次
     */
    @Bean
    public JobDetail fiveSecondJobJobDetail() {
        return JobBuilder.newJob(FiveSecondJob.class)
                .withIdentity("FiveSecondJob", "bytedesk")
                .withDescription("每5秒钟运行一次")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fiveSecondJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(5)
                .repeatForever();
        return TriggerBuilder.newTrigger()
                .forJob(fiveSecondJobJobDetail())
                .withIdentity("fiveSecondJobrigger", "bytedesk")
                .withDescription("每隔5秒钟检查一次")
                .withSchedule(scheduleBuilder)
                .build();
    }
    


}
