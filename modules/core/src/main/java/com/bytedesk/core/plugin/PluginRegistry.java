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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * 插件注册中心
 * 管理所有Bytedesk模块插件的注册、查询和生命周期
 */
@Slf4j
@Component
public class PluginRegistry {
    
    private final Map<String, BytedeskPlugin> plugins = new ConcurrentHashMap<>();
    private final List<BytedeskPlugin> pluginList;
    
    public PluginRegistry(List<BytedeskPlugin> pluginList) {
        this.pluginList = pluginList;
    }
    
    /**
     * 初始化：自动注册所有插件
     */
    @PostConstruct
    public void init() {
        log.info("Initializing Plugin Registry...");
        
        // 按优先级排序
        List<BytedeskPlugin> sortedPlugins = pluginList.stream()
            .sorted(Comparator.comparingInt(BytedeskPlugin::getPriority))
            .collect(Collectors.toList());

        // 依赖感知注册：优先级仅作为同层排序依据，依赖未满足的插件会在后续轮次重试
        List<BytedeskPlugin> pendingPlugins = new ArrayList<>(sortedPlugins);
        boolean progress;
        do {
            progress = false;
            Iterator<BytedeskPlugin> iterator = pendingPlugins.iterator();
            while (iterator.hasNext()) {
                BytedeskPlugin plugin = iterator.next();
                if (checkDependencies(plugin, false)) {
                    registerPlugin(plugin);
                    iterator.remove();
                    progress = true;
                }
            }
        } while (progress && !pendingPlugins.isEmpty());

        // 仍未注册成功的插件（可能是循环依赖或缺失依赖）
        for (BytedeskPlugin plugin : pendingPlugins) {
            List<String> missingDependencies = getMissingDependencies(plugin);
            log.error("Plugin {} has unmet dependencies: {}", plugin.getPluginId(), String.join(", ", missingDependencies));
        }
        
        log.info("Plugin Registry initialized with {} plugins", plugins.size());
        logRegisteredPlugins();
    }
    
    /**
     * 注册插件
     */
    public void registerPlugin(BytedeskPlugin plugin) {
        if (plugin == null) {
            log.warn("Attempted to register null plugin");
            return;
        }
        
        String pluginId = plugin.getPluginId();
        if (pluginId == null || pluginId.trim().isEmpty()) {
            log.error("Plugin ID cannot be null or empty");
            return;
        }
        
        // 检查依赖
        if (!checkDependencies(plugin)) {
            log.error("Plugin {} has unmet dependencies", pluginId);
            return;
        }
        
        // 注册插件
        plugins.put(pluginId, plugin);
        log.info("Registered plugin: {} ({}) - Version: {}, Enabled: {}", 
            plugin.getPluginName(), pluginId, plugin.getVersion(), plugin.isEnabled());
        
        // 初始化插件
        try {
            plugin.initialize();
        } catch (Exception e) {
            log.error("Failed to initialize plugin: {}", pluginId, e);
        }
    }
    
    /**
     * 注销插件
     */
    public void unregisterPlugin(String pluginId) {
        BytedeskPlugin plugin = plugins.remove(pluginId);
        if (plugin != null) {
            try {
                plugin.destroy();
                log.info("Unregistered plugin: {} ({})", plugin.getPluginName(), pluginId);
            } catch (Exception e) {
                log.error("Failed to destroy plugin: {}", pluginId, e);
            }
        }
    }
    
    /**
     * 获取插件
     */
    public Optional<BytedeskPlugin> getPlugin(String pluginId) {
        return Optional.ofNullable(plugins.get(pluginId));
    }
    
    /**
     * 获取所有插件
     */
    public List<BytedeskPlugin> getAllPlugins() {
        return new ArrayList<>(plugins.values());
    }
    
    /**
     * 获取所有已启用的插件
     */
    public List<BytedeskPlugin> getEnabledPlugins() {
        return plugins.values().stream()
            .filter(BytedeskPlugin::isEnabled)
            .sorted(Comparator.comparingInt(BytedeskPlugin::getPriority))
            .collect(Collectors.toList());
    }
    
    /**
     * 获取插件数量
     */
    public int getPluginCount() {
        return plugins.size();
    }
    
    /**
     * 获取已启用插件数量
     */
    public int getEnabledPluginCount() {
        return (int) plugins.values().stream()
            .filter(BytedeskPlugin::isEnabled)
            .count();
    }
    
    /**
     * 检查插件是否存在
     */
    public boolean hasPlugin(String pluginId) {
        return plugins.containsKey(pluginId);
    }
    
    /**
     * 检查插件是否启用
     */
    public boolean isPluginEnabled(String pluginId) {
        return getPlugin(pluginId)
            .map(BytedeskPlugin::isEnabled)
            .orElse(false);
    }
    
    /**
     * 获取所有插件的健康状态
     */
    public Map<String, Map<String, Object>> getAllPluginsHealthStatus() {
        Map<String, Map<String, Object>> healthStatus = new LinkedHashMap<>();
        
        plugins.values().stream()
            .sorted(Comparator.comparingInt(BytedeskPlugin::getPriority))
            .forEach(plugin -> {
                try {
                    healthStatus.put(plugin.getPluginId(), plugin.getHealthStatus());
                } catch (Exception e) {
                    log.error("Failed to get health status for plugin: {}", plugin.getPluginId(), e);
                    healthStatus.put(plugin.getPluginId(), Map.of(
                        "status", "ERROR",
                        "error", e.getMessage()
                    ));
                }
            });
        
        return healthStatus;
    }
    
    /**
     * 获取所有插件的统计信息
     */
    public Map<String, Map<String, Object>> getAllPluginsStatistics() {
        Map<String, Map<String, Object>> statistics = new LinkedHashMap<>();
        
        plugins.values().stream()
            .sorted(Comparator.comparingInt(BytedeskPlugin::getPriority))
            .forEach(plugin -> {
                try {
                    statistics.put(plugin.getPluginId(), plugin.getStatistics());
                } catch (Exception e) {
                    log.error("Failed to get statistics for plugin: {}", plugin.getPluginId(), e);
                }
            });
        
        return statistics;
    }
    
    /**
     * 检查插件依赖是否满足
     */
    private boolean checkDependencies(BytedeskPlugin plugin) {
        return checkDependencies(plugin, true);
    }

    private boolean checkDependencies(BytedeskPlugin plugin, boolean logMissing) {
        String[] dependencies = plugin.getDependencies();
        if (dependencies == null || dependencies.length == 0) {
            return true;
        }
        
        for (String dependency : dependencies) {
            if (!plugins.containsKey(dependency)) {
                if (logMissing) {
                    log.warn("Plugin {} depends on {}, but it's not registered yet", 
                        plugin.getPluginId(), dependency);
                }
                return false;
            }
        }
        
        return true;
    }

    private List<String> getMissingDependencies(BytedeskPlugin plugin) {
        String[] dependencies = plugin.getDependencies();
        if (dependencies == null || dependencies.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(dependencies)
            .filter(dependency -> !plugins.containsKey(dependency))
            .collect(Collectors.toList());
    }
    
    /**
     * 记录已注册的插件信息
     */
    private void logRegisteredPlugins() {
        if (plugins.isEmpty()) {
            log.warn("No plugins registered");
            return;
        }
        
        log.info("==================================================");
        log.info("Registered Plugins ({})", plugins.size());
        log.info("==================================================");
        
        plugins.values().stream()
            .sorted(Comparator.comparingInt(BytedeskPlugin::getPriority))
            .forEach(plugin -> {
                log.info("  - {} ({}) v{} [{}] Priority: {}", 
                    plugin.getPluginName(),
                    plugin.getPluginId(),
                    plugin.getVersion(),
                    plugin.isEnabled() ? "ENABLED" : "DISABLED",
                    plugin.getPriority());
                
                String[] deps = plugin.getDependencies();
                if (deps != null && deps.length > 0) {
                    log.info("    Dependencies: {}", String.join(", ", deps));
                }
            });
        
        log.info("==================================================");
    }
    
    /**
     * 销毁：注销所有插件
     */
    @PreDestroy
    public void destroy() {
        log.info("Destroying Plugin Registry...");
        
        // 按优先级逆序销毁
        List<BytedeskPlugin> sortedPlugins = new ArrayList<>(plugins.values());
        sortedPlugins.sort(Comparator.comparingInt(BytedeskPlugin::getPriority).reversed());
        
        for (BytedeskPlugin plugin : sortedPlugins) {
            try {
                plugin.destroy();
                log.info("Destroyed plugin: {}", plugin.getPluginId());
            } catch (Exception e) {
                log.error("Failed to destroy plugin: {}", plugin.getPluginId(), e);
            }
        }
        
        plugins.clear();
        log.info("Plugin Registry destroyed");
    }
}
