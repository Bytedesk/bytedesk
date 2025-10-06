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
package com.bytedesk.kbase.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.bytedesk.core.plugin.AbstractBytedeskPlugin;

import lombok.extern.slf4j.Slf4j;

/**
 * 知识库模块插件
 * 提供知识库、文章、FAQ、向量检索等功能
 */
@Slf4j
@Component
public class KbasePlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.kbase.enabled:true}")
    private boolean enabled;
    
    @Value("${bytedesk.kbase.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    private HealthIndicator kbaseHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return kbaseHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "kbase";
    }
    
    @Override
    public String getPluginName() {
        return "Knowledge Base";
    }
    
    @Override
    public String getDescription() {
        return "知识库管理系统，提供文章、FAQ、智能问答、向量检索等功能";
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
        return 20; // 知识库优先级较高
    }
    
    @Override
    public String[] getDependencies() {
        return new String[]{"core"}; // 依赖核心模块
    }
    
    @Override
    public void initialize() {
        super.initialize();
        log.info("Knowledge Base Plugin initialized - Features: Article, FAQ, Vector Search, AI Integration");
    }
}
