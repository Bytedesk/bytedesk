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
package com.bytedesk.core.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 核心模块插件
 * 提供基础功能：用户管理、权限控制、消息系统等
 */
@Slf4j
@Component
public class CorePlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.core.enabled:true}")
    private boolean enabled;
    
    @Value("${bytedesk.core.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    private HealthIndicator coreHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return coreHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "core";
    }
    
    @Override
    public String getPluginName() {
        return "Core Module";
    }
    
    @Override
    public String getDescription() {
        return "核心模块，提供用户管理、权限控制、消息系统、通知等基础功能";
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
        return 1; // 核心模块优先级最高
    }
    
    @Override
    public String[] getDependencies() {
        return new String[0]; // 核心模块不依赖其他模块
    }
    
    @Override
    public void initialize() {
        super.initialize();
        log.info("Core Module Plugin initialized - Features: User Management, RBAC, Messaging, Notification");
    }
}
