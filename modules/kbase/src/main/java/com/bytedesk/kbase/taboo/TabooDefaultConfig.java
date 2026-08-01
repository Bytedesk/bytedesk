/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-09 07:39:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-07-09 07:39:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 开源模块默认敏感词服务配置。
 */
@Configuration(proxyBeanMethods = false)
public class TabooDefaultConfig {

    @Bean
    @ConditionalOnMissingBean(TabooService.class)
    public TabooService tabooService() {
        return new TabooServiceImpl();
    }
}