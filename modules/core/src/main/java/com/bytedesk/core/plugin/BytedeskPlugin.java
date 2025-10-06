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

import java.util.Map;

/**
 * Bytedesk插件接口
 * 所有模块插件必须实现此接口
 */
public interface BytedeskPlugin {
    
    /**
     * 获取插件唯一标识符
     * @return 插件ID，如：kbase, service, ticket, ai, call, voc
     */
    String getPluginId();
    
    /**
     * 获取插件名称
     * @return 插件显示名称
     */
    String getPluginName();
    
    /**
     * 获取插件描述
     * @return 插件功能描述
     */
    String getDescription();
    
    /**
     * 获取插件版本
     * @return 版本号
     */
    String getVersion();
    
    /**
     * 获取插件作者
     * @return 作者信息
     */
    String getAuthor();
    
    /**
     * 获取插件官网
     * @return 官网URL
     */
    default String getWebsite() {
        return "https://bytedesk.com";
    }
    
    /**
     * 插件是否启用
     * @return true表示启用，false表示禁用
     */
    boolean isEnabled();
    
    /**
     * 插件优先级（数字越小优先级越高）
     * @return 优先级值，默认100
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * 获取插件依赖的其他插件ID列表
     * @return 依赖的插件ID数组，如果没有依赖返回空数组
     */
    default String[] getDependencies() {
        return new String[0];
    }
    
    /**
     * 获取插件健康状态
     * @return 健康状态信息
     */
    Map<String, Object> getHealthStatus();
    
    /**
     * 获取插件统计信息
     * @return 统计数据
     */
    default Map<String, Object> getStatistics() {
        return Map.of(
            "enabled", isEnabled(),
            "version", getVersion()
        );
    }
    
    /**
     * 插件初始化
     * 在插件注册后调用
     */
    default void initialize() {
        // 默认空实现
    }
    
    /**
     * 插件销毁
     * 在应用关闭时调用
     */
    default void destroy() {
        // 默认空实现
    }
}
