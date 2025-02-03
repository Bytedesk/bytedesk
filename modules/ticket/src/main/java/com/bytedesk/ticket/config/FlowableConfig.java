/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-15 21:33:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-03 08:28:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowableConfig {

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurationConfigurer() {
        return engineConfiguration -> {
            engineConfiguration.setEnableProcessDefinitionInfoCache(true);
            engineConfiguration.setDatabaseSchemaUpdate("true");
        };
    }
    
    // 可选：实现多租户数据源
    // private MultiTenantDataSource createMultiTenantDataSource() {
    //     // 实现多租户数据源逻辑
    //     return new MultiTenantDataSource() {
    //         @Override
    //         public DataSource getDataSource(String tenantId) {
    //             // 根据tenantId返回对应的数据源
    //             return null;
    //         }
    //     };
    // }
} 