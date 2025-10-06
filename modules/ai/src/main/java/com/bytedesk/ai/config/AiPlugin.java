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
package com.bytedesk.ai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.bytedesk.core.plugin.AbstractBytedeskPlugin;

import lombok.extern.slf4j.Slf4j;

/**
 * AI模块插件
 * 提供大模型集成、智能客服、对话生成等功能
 */
@Slf4j
@Component
public class AiPlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.ai.enabled:true}")
    private boolean enabled;
    
    @Value("${bytedesk.ai.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    private HealthIndicator aiHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return aiHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "ai";
    }
    
    @Override
    public String getPluginName() {
        return "AI Assistant";
    }
    
    @Override
    public String getDescription() {
        return "AI智能助手，提供大模型集成、智能客服、对话生成、语义理解等功能";
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
        return 15; // AI功能优先级较高
    }
    
    @Override
    public String[] getDependencies() {
        return new String[]{"core", "kbase"}; // 依赖核心模块和知识库
    }
    
    @Override
    public void initialize() {
        super.initialize();
        log.info("AI Assistant Plugin initialized - Features: LLM Integration, Intelligent Q&A, Semantic Understanding");
    }
}
