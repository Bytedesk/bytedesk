/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-26 17:00:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.starter.service.SystemStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统状态监控控制器
 */
@Slf4j
@Tag(name = "system-status - 系统状态")
@RestController
@RequestMapping("/system")
public class SystemStatusController {

    @Autowired
    private SystemStatusService systemStatusService;

    /**
     * 获取系统详细状态
     * http://127.0.0.1:9003/system/status
     */
    @GetMapping("/status")
    @Operation(summary = "获取系统详细状态")
    public ResponseEntity<?> getSystemStatus() {
        try {
            Map<String, Object> status = systemStatusService.getSystemStatus();
            return ResponseEntity.ok(JsonResult.success("系统状态", status));
        } catch (Exception e) {
            log.error("获取系统状态失败", e);
            return ResponseEntity.ok(JsonResult.error("获取系统状态失败: " + e.getMessage()));
        }
    }

    /**
     * 获取系统健康状态
     * http://127.0.0.1:9003/system/health
     */
    @GetMapping("/health")
    @Operation(summary = "获取系统健康状态")
    public ResponseEntity<?> getSystemHealth() {
        try {
            Map<String, Object> health = systemStatusService.getHealth();
            return ResponseEntity.ok(JsonResult.success("系统健康状态", health));
        } catch (Exception e) {
            log.error("获取系统健康状态失败", e);
            return ResponseEntity.ok(JsonResult.error("获取系统健康状态失败: " + e.getMessage()));
        }
    }

    /**
     * 获取系统基本信息
     * http://127.0.0.1:9003/system/info
     */
    @GetMapping("/info")
    @Operation(summary = "获取系统基本信息")
    public ResponseEntity<?> getSystemInfo() {
        try {
            Map<String, Object> status = systemStatusService.getSystemStatus();
            Map<String, Object> info = Map.of(
                "timestamp", status.get("timestamp"),
                "uptime", status.get("uptime"),
                "system", status.get("system"),
                "memory", status.get("memory")
            );
            return ResponseEntity.ok(JsonResult.success("系统信息", info));
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            return ResponseEntity.ok(JsonResult.error("获取系统信息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取服务状态
     * http://127.0.0.1:9003/system/services
     */
    @GetMapping("/services")
    @Operation(summary = "获取服务状态")
    public ResponseEntity<?> getServicesStatus() {
        try {
            Map<String, Object> status = systemStatusService.getSystemStatus();
            Map<String, Object> services = Map.of(
                "services", status.get("services"),
                "database", status.get("database"),
                "redis", status.get("redis")
            );
            return ResponseEntity.ok(JsonResult.success("服务状态", services));
        } catch (Exception e) {
            log.error("获取服务状态失败", e);
            return ResponseEntity.ok(JsonResult.error("获取服务状态失败: " + e.getMessage()));
        }
    }
} 