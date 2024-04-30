/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-17 10:35:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-17 10:54:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * async push
 * https://www.baeldung.com/spring-async
 * https://spring.io/guides/gs/async-method
 */
@EnableAsync
@Configuration
public class PushConfig implements AsyncConfigurer {
    
    // @Override
    // public Executor getAsyncExecutor() {
    //     ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    //     threadPoolTaskExecutor.setCorePoolSize(2);
    //     threadPoolTaskExecutor.setMaxPoolSize(2);
    //     threadPoolTaskExecutor.setQueueCapacity(500);
    //     threadPoolTaskExecutor.setThreadNamePrefix("PushService-"); // default prefix ：TaskExecutor-
    //     threadPoolTaskExecutor.initialize();
    //     return threadPoolTaskExecutor;
    // }
}
