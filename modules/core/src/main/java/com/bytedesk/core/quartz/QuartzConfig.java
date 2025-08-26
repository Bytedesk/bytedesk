/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-28 13:05:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 15:30:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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

import com.bytedesk.core.quartz.job.QuartzDaily0Job;
import com.bytedesk.core.quartz.job.QuartzDaily8Job;
import com.bytedesk.core.quartz.job.QuartzFiveMinJob;
import com.bytedesk.core.quartz.job.QuartzFiveSecondJob;
import com.bytedesk.core.quartz.job.QuartzHalfHourJob;
import com.bytedesk.core.quartz.job.QuartzHourlyJob;
import com.bytedesk.core.quartz.job.QuartzOneMinJob;

import static org.quartz.CronScheduleBuilder.*;

/**
 * Cron使用方法：
 * https://stackoverflow.com/questions/26147044/spring-cron-expression-for-every-day-101am
 * https://docs.spring.io/spring/docs/3.0.x/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
 */

// second, minute, hour, day of month, month, day(s) of week
// "0 0 * * * *" = the top of every hour of every day. // 测试报：invalid
// "*/10 * * * * *" = every ten seconds.
// "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
// "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
// "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
// "0 0 0 25 12 ?" = every Christmas Day at midnight
// (*) means match any
// */X means "every X"
// ? ("no specific value")

/**
 * <a href=
 * "https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-quartz.html">SpringBoot官方说明</a&gt;
 * <a href=
 * "https://www.quartz-scheduler.org/documentation/2.3.1-SNAPSHOT/tutorials/index.html">Quartz官方文档</a&gt;
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
        return JobBuilder.newJob(QuartzFiveSecondJob.class)
                .withIdentity("FiveSecondJob", "bytedesk")
                .withDescription("run one 5 seconds")
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
                .withIdentity("fiveSecondJobTrigger", "bytedesk")
                .withDescription("run once 5 seconds")
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 每1分钟运行一次
     */
    @Bean
    public JobDetail oneMinJobJobDetail() {
            return JobBuilder.newJob(QuartzOneMinJob.class)
                            .withIdentity("OneMinJob", "bytedesk")
                            .withDescription("run once 1 minutes")
                            .storeDurably()
                            .build();
    }

    @Bean
    public Trigger oneMinJobTrigger() {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                            .simpleSchedule()
                            .withIntervalInMinutes(1)
                            .repeatForever();
            return TriggerBuilder.newTrigger()
                            .forJob(oneMinJobJobDetail())
                            .withIdentity("oneMinJobTrigger", "bytedesk")
                            .withDescription("run once 1")
                            .withSchedule(scheduleBuilder)
                            .build();
    }

    /**
     * 每5分钟运行一次
     */
    @Bean
    public JobDetail fiveMinJobJobDetail() {
        return JobBuilder.newJob(QuartzFiveMinJob.class)
                .withIdentity("FiveMinJob", "bytedesk")
                .withDescription("run once 5 minutes")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fiveMinJobTrigger() {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                            .simpleSchedule()
                            .withIntervalInMinutes(5)
                            .repeatForever();
            return TriggerBuilder.newTrigger()
                            .forJob(fiveMinJobJobDetail())
                            .withIdentity("fiveMinJobTrigger", "bytedesk")
                            .withDescription("run once 5 minutes")
                            .withSchedule(scheduleBuilder)
                            .build();
    }
    
    
    /**
     * 每小时整点运行一次
     */
    @Bean
    public JobDetail hourlyJobDetail() {
        return JobBuilder.newJob(
                QuartzHourlyJob.class)
                .withIdentity("HourlyJob", "bytedesk")
                .withDescription("run once every hour")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger hourlyTrigger() {
        return TriggerBuilder.newTrigger().forJob(hourlyJobDetail())
                .withIdentity("hourlyTrigger", "bytedesk")
                .withDescription("run once every hour")
                .withSchedule(cronSchedule("0 0 * * * ?"))
                .build();
    }

    /**
     * 每30分钟运行一次，在整点和半点运行
     */
    @Bean
    public JobDetail halfHourJobDetail() {
        return JobBuilder.newJob(QuartzHalfHourJob.class)
                .withIdentity("HalfHourJob", "bytedesk")
                .withDescription("run once every half hour ").storeDurably().build();
    }

    @Bean
    public Trigger halfHourTrigger() {
        return TriggerBuilder.newTrigger().forJob(
                halfHourJobDetail())
                .withIdentity("halfHourTrigger", "bytedesk")
                .withDescription("run once every half hour")
                .withSchedule(cronSchedule("0 0/30 * * * ?"))
                .build();
    }

    
    /**
     * run once at 0 o'clock
     */
    @Bean
    public JobDetail daily0JobDetail() {
        return JobBuilder.newJob(QuartzDaily0Job.class).withIdentity("Daily0Job", "bytedesk")
                .withDescription("run once at 0 o'click").storeDurably().build();
    }

    @Bean
    public Trigger daily0Trigger() {
        return TriggerBuilder.newTrigger().forJob(
                daily0JobDetail()).withIdentity("daily0Trigger", "bytedesk")
                .withDescription("run once at 0 o'clock")
                .withSchedule(cronSchedule("0 0 0 * * ?"))
                .build();
    }

    /**
     * run once at 8 o'clock
     */
    @Bean
    public JobDetail daily8JobDetail() {
        return JobBuilder.newJob(QuartzDaily8Job.class)
                .withIdentity("Daily8Job", "bytedesk")
                .withDescription("run once at 8 o'clock")
                .storeDurably().build();
    }

    @Bean
    public Trigger daily8Trigger() {
        return TriggerBuilder.newTrigger().forJob(
                daily8JobDetail())
                .withIdentity("daily8Trigger", "bytedesk")
                .withDescription("run once at 8 o'clock")
                .withSchedule(cronSchedule("0 0 8 * * ?"))
                .build();
    }

}
