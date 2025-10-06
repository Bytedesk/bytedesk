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
package com.bytedesk.voc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.bytedesk.core.plugin.AbstractBytedeskPlugin;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户之声模块插件
 * 提供客户反馈、满意度调查、数据分析等功能
 */
@Slf4j
@Component
public class VocPlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.voc.enabled:true}")
    private boolean enabled;
    
    @Value("${bytedesk.voc.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    private HealthIndicator vocHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return vocHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "voc";
    }
    
    @Override
    public String getPluginName() {
        return "Voice of Customer";
    }
    
    @Override
    public String getDescription() {
        return "客户之声系统，提供客户反馈收集、满意度调查、数据分析、报表生成等功能";
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
        return 50; // VOC优先级较低
    }
    
    @Override
    public String[] getDependencies() {
        return new String[]{"core"}; // 依赖核心模块
    }
    
    @Override
    public void initialize() {
        super.initialize();
        log.info("Voice of Customer Plugin initialized - Features: Feedback Collection, Satisfaction Survey, Analytics");
    }
}
