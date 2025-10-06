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
package com.bytedesk.ticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.bytedesk.core.plugin.AbstractBytedeskPlugin;

import lombok.extern.slf4j.Slf4j;

/**
 * 工单系统模块插件
 * 提供工单管理、工单流转、SLA等功能
 */
@Slf4j
@Component
public class TicketPlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.ticket.enabled:true}")
    private boolean enabled;
    
    @Value("${bytedesk.ticket.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    private HealthIndicator ticketHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return ticketHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "ticket";
    }
    
    @Override
    public String getPluginName() {
        return "Ticket System";
    }
    
    @Override
    public String getDescription() {
        return "工单管理系统，提供工单创建、分配、流转、SLA管理等功能";
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
        return 30; // 工单系统优先级中等
    }
    
    @Override
    public String[] getDependencies() {
        return new String[]{"core"}; // 依赖核心模块
    }
    
    @Override
    public void initialize() {
        super.initialize();
        log.info("Ticket System Plugin initialized - Features: Ticket Management, Workflow, SLA");
    }
}
