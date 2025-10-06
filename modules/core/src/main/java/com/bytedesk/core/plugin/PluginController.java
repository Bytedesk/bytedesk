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
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 插件管理控制器
 * 提供插件信息查询和管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/plugins")
@RequiredArgsConstructor
@Tag(name = "Plugin Management", description = "插件管理接口")
public class PluginController {
    
    private final PluginRegistry pluginRegistry;
    
    /**
     * 获取所有插件列表
     * http://127.0.0.1:9003/api/v1/plugins
     */
    @GetMapping
    @Operation(summary = "获取所有插件", description = "获取系统中所有已注册的插件列表")
    public ResponseEntity<Map<String, Object>> getAllPlugins() {
        List<BytedeskPlugin> plugins = pluginRegistry.getAllPlugins();
        
        List<Map<String, Object>> pluginList = plugins.stream()
            .sorted(Comparator.comparingInt(BytedeskPlugin::getPriority))
            .map(this::convertPluginToMap)
            .collect(Collectors.toList());
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", plugins.size());
        response.put("enabled", pluginRegistry.getEnabledPluginCount());
        response.put("plugins", pluginList);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取已启用的插件列表
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取已启用插件", description = "获取系统中所有已启用的插件列表")
    public ResponseEntity<Map<String, Object>> getEnabledPlugins() {
        List<BytedeskPlugin> plugins = pluginRegistry.getEnabledPlugins();
        
        List<Map<String, Object>> pluginList = plugins.stream()
            .map(this::convertPluginToMap)
            .collect(Collectors.toList());
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", plugins.size());
        response.put("plugins", pluginList);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取指定插件信息
     */
    @GetMapping("/{pluginId}")
    @Operation(summary = "获取插件详情", description = "根据插件ID获取插件详细信息")
    public ResponseEntity<Map<String, Object>> getPlugin(@PathVariable String pluginId) {
        Optional<BytedeskPlugin> plugin = pluginRegistry.getPlugin(pluginId);
        
        if (plugin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = convertPluginToDetailMap(plugin.get());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取插件健康状态
     */
    @GetMapping("/{pluginId}/health")
    @Operation(summary = "获取插件健康状态", description = "获取指定插件的健康检查状态")
    public ResponseEntity<Map<String, Object>> getPluginHealth(@PathVariable String pluginId) {
        Optional<BytedeskPlugin> plugin = pluginRegistry.getPlugin(pluginId);
        
        if (plugin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> health = plugin.get().getHealthStatus();
        return ResponseEntity.ok(health);
    }
    
    /**
     * 获取所有插件的健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "获取所有插件健康状态", description = "获取系统中所有插件的健康检查状态")
    public ResponseEntity<Map<String, Object>> getAllPluginsHealth() {
        Map<String, Map<String, Object>> healthStatus = pluginRegistry.getAllPluginsHealthStatus();
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("total", pluginRegistry.getPluginCount());
        response.put("enabled", pluginRegistry.getEnabledPluginCount());
        response.put("plugins", healthStatus);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取插件统计信息
     */
    @GetMapping("/{pluginId}/statistics")
    @Operation(summary = "获取插件统计信息", description = "获取指定插件的统计数据")
    public ResponseEntity<Map<String, Object>> getPluginStatistics(@PathVariable String pluginId) {
        Optional<BytedeskPlugin> plugin = pluginRegistry.getPlugin(pluginId);
        
        if (plugin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> statistics = plugin.get().getStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 获取所有插件的统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取所有插件统计信息", description = "获取系统中所有插件的统计数据")
    public ResponseEntity<Map<String, Object>> getAllPluginsStatistics() {
        Map<String, Map<String, Object>> statistics = pluginRegistry.getAllPluginsStatistics();
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("total", pluginRegistry.getPluginCount());
        response.put("enabled", pluginRegistry.getEnabledPluginCount());
        response.put("plugins", statistics);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取插件概览信息
     */
    @GetMapping("/overview")
    @Operation(summary = "获取插件概览", description = "获取插件系统的概览信息")
    public ResponseEntity<Map<String, Object>> getPluginsOverview() {
        Map<String, Object> overview = new LinkedHashMap<>();
        
        // 基本统计
        overview.put("totalPlugins", pluginRegistry.getPluginCount());
        overview.put("enabledPlugins", pluginRegistry.getEnabledPluginCount());
        overview.put("disabledPlugins", pluginRegistry.getPluginCount() - pluginRegistry.getEnabledPluginCount());
        
        // 插件列表（简化信息）
        List<Map<String, Object>> pluginSummary = pluginRegistry.getAllPlugins().stream()
            .sorted(Comparator.comparingInt(BytedeskPlugin::getPriority))
            .map(plugin -> {
                Map<String, Object> summary = new LinkedHashMap<>();
                summary.put("id", plugin.getPluginId());
                summary.put("name", plugin.getPluginName());
                summary.put("version", plugin.getVersion());
                summary.put("enabled", plugin.isEnabled());
                summary.put("priority", plugin.getPriority());
                return summary;
            })
            .collect(Collectors.toList());
        
        overview.put("plugins", pluginSummary);
        
        return ResponseEntity.ok(overview);
    }
    
    /**
     * 转换插件为Map（简化版）
     */
    private Map<String, Object> convertPluginToMap(BytedeskPlugin plugin) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", plugin.getPluginId());
        map.put("name", plugin.getPluginName());
        map.put("description", plugin.getDescription());
        map.put("version", plugin.getVersion());
        map.put("enabled", plugin.isEnabled());
        map.put("priority", plugin.getPriority());
        return map;
    }
    
    /**
     * 转换插件为Map（详细版）
     */
    private Map<String, Object> convertPluginToDetailMap(BytedeskPlugin plugin) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", plugin.getPluginId());
        map.put("name", plugin.getPluginName());
        map.put("description", plugin.getDescription());
        map.put("version", plugin.getVersion());
        map.put("author", plugin.getAuthor());
        map.put("website", plugin.getWebsite());
        map.put("enabled", plugin.isEnabled());
        map.put("priority", plugin.getPriority());
        map.put("dependencies", plugin.getDependencies());
        map.put("statistics", plugin.getStatistics());
        map.put("health", plugin.getHealthStatus());
        return map;
    }
}
