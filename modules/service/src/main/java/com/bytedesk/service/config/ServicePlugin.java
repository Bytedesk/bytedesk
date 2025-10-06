/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-06 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-06 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.bytedesk.core.plugin.AbstractBytedeskPlugin;

import lombok.extern.slf4j.Slf4j;

/**
 * 在线客服模块插件
 * 提供实时聊天、会话管理、客服分配等功能
 */
@Slf4j
@Component
public class ServicePlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.service.enabled:true}")
    private boolean enabled;
    
    @Value("${bytedesk.service.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    private HealthIndicator serviceHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return serviceHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "service";
    }
    
    @Override
    public String getPluginName() {
        return "Customer Service";
    }
    
    @Override
    public String getDescription() {
        return "在线客服系统，提供实时聊天、会话管理、客服分配、消息队列等功能";
    }
    
    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public int getPriority() {
        return 10; // 客服是核心功能，优先级最高
    }
    
    @Override
    public String[] getDependencies() {
        return new String[]{"core"}; // 依赖核心模块
    }
    
    @Override
    public void initialize() {
        super.initialize();
        log.info("Customer Service Plugin initialized - Features: Live Chat, Session Management, Agent Assignment");
    }
}
