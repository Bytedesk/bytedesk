/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 15:02:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-27 18:45:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// @Slf4j 
@EnableAsync
// @EnableCaching // 迁移到cacheconfig
@EnableScheduling
@EnableJpaAuditing
@ComponentScan("com.bytedesk.*")
@SpringBootApplication
// @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60 * 24 * 7)
public class StarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarterApplication.class, args);
	}
}
