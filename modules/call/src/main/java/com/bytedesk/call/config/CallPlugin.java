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
package com.bytedesk.call.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.bytedesk.core.plugin.AbstractBytedeskPlugin;

import lombok.extern.slf4j.Slf4j;

/**
 * 呼叫中心模块插件
 * 提供语音通话、FreeSWITCH集成等功能
 */
@Slf4j
@Component
public class CallPlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.call.enabled:false}")
    private boolean enabled;
    
    @Value("${bytedesk.call.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    @Qualifier("callHealthIndicator")
    private HealthIndicator callHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return callHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "call";
    }
    
    @Override
    public String getPluginName() {
        return "Call Center";
    }
    
    @Override
    public String getDescription() {
        return "呼叫中心系统，提供语音通话、FreeSWITCH集成、通话记录等功能";
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
        return 40; // 呼叫中心优先级中等
    }
    
    @Override
    public String[] getDependencies() {
        return new String[]{"core"}; // 依赖核心模块
    }
    
    @Override
    public void initialize() {
        super.initialize();
        log.info("Call Center Plugin initialized - Features: Voice Call, FreeSWITCH Integration, Call Recording");
    }
}
