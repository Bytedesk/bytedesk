/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-10 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-10 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.service;

import java.util.Set;

/**
 * AI服务提供商接口
 * 用于不同模块提供各自支持的AI服务
 */
public interface SpringAIServiceProvider {
    
    /**
     * 获取该提供商支持的AI服务类型
     * @return 支持的AI服务类型集合
     */
    Set<String> getSupportedProviders();
    
    /**
     * 根据提供商名称获取对应的AI服务实例
     * @param providerName 提供商名称
     * @return AI服务实例，如果不支持则返回null
     */
    SpringAIService getService(String providerName);
    
    /**
     * 获取提供商的优先级，数值越小优先级越高
     * @return 优先级
     */
    default int getPriority() {
        return 100; // 默认优先级
    }
    
    /**
     * 检查指定提供商是否可用
     * @param providerName 提供商名称
     * @return 如果提供商可用返回true，否则返回false
     */
    default boolean isServiceAvailable(String providerName) {
        return getSupportedProviders().contains(providerName) && getService(providerName) != null;
    }
}
