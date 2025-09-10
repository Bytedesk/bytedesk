/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-19 13:05:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-10 11:34:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bytedesk.ai.springai.providers.custom.SpringAICustomService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SpringAI服务注册表
 * 用于管理和获取不同AI提供商的服务实例
 * 支持多模块架构，通过服务提供商接口实现解耦
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAIServiceRegistry {

    // 注入所有的服务提供商，按优先级排序
    private final List<SpringAIServiceProvider> serviceProviders;
    // 自定义服务，用于处理未知或未注册的提供商，兼容OpenAI
    private final Optional<SpringAICustomService> springAICustomService;

    // 服务注册表，用于存储各种AI服务提供商的实现
    private final Map<String, SpringAIService> serviceRegistry = new HashMap<>();

    @PostConstruct
    public void registerServices() {
        // 按优先级排序服务提供商（优先级数值越小越优先）
        serviceProviders.sort((p1, p2) -> Integer.compare(p1.getPriority(), p2.getPriority()));
        
        // 从所有服务提供商中注册服务，优先级高的会覆盖优先级低的
        for (SpringAIServiceProvider provider : serviceProviders) {
            for (String providerName : provider.getSupportedProviders()) {
                SpringAIService service = provider.getService(providerName);
                if (service != null) {
                    serviceRegistry.put(providerName, service);
                    log.info("已注册AI服务提供商: {} (来源: {}, 优先级: {})", 
                            providerName, provider.getClass().getSimpleName(), provider.getPriority());
                }
            }
        }
        
        log.info("SpringAIServiceRegistry初始化完成，共注册 {} 个AI服务", serviceRegistry.size());
    }

    /**
     * 获取指定提供商的服务实现
     * 
     * @param providerName 提供商名称
     * @return SpringAI服务实现
     * @throws IllegalArgumentException 如果提供商不存在
     */
    public SpringAIService getServiceByProviderName(String providerName) {
        SpringAIService service = serviceRegistry.get(providerName);
        if (service == null) {
            // 如果找不到具体的提供商，尝试使用自定义服务作为fallback
            return springAICustomService
                .orElseThrow(() -> new IllegalArgumentException("未找到AI服务提供商: " + providerName));
        }
        return service;
    }

    /**
     * 检查指定提供商是否可用
     * 
     * @param providerName 提供商名称
     * @return 如果提供商可用返回true，否则返回false
     */
    public Boolean isServiceAvailable(String providerName) {
        return serviceRegistry.containsKey(providerName);
    }

    /**
     * 获取所有已注册的服务提供商
     * 
     * @return 已注册的服务提供商映射
     */
    public Map<String, SpringAIService> getAllServices() {
        return new HashMap<>(serviceRegistry);
    }
}