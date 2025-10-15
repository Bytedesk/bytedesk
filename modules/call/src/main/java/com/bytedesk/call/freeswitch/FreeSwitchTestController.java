/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-15 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-15 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.freeswitch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSWITCH 数据源测试控制器
 * 
 * 用于测试和验证 FreeSWITCH 数据源配置是否正常工作
 * 
 * 访问地址：http://localhost:9003/api/v1/freeswitch/test/*
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/freeswitch/test")
@RequiredArgsConstructor
@Tag(name = "FreeSWITCH Test API", description = "FreeSWITCH 数据源测试接口")
@ConditionalOnProperty(
    prefix = "bytedesk.datasource.freeswitch",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class FreeSwitchTestController {

    private final FreeSwitchCallService freeSwitchCallService;
    private final FreeSwitchCallRepository freeSwitchCallRepository;

    /**
     * 测试 FreeSWITCH 数据源连接
     * 
     * 访问：GET http://localhost:9003/api/v1/freeswitch/test/connection
     */
    @GetMapping("/connection")
    @Operation(summary = "测试数据源连接", description = "验证 FreeSWITCH 数据源是否正常连接")
    public ResponseEntity<?> testConnection() {
        try {
            log.info("Testing FreeSWITCH datasource connection...");
            
            // 尝试查询数据库
            long count = freeSwitchCallRepository.count();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "FreeSWITCH datasource connected successfully");
            result.put("totalCalls", count);
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("FreeSWITCH datasource connection test passed. Total calls: {}", count);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("FreeSWITCH datasource connection test failed", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Connection failed: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 获取所有通话记录（限制10条）
     * 
     * 访问：GET http://localhost:9003/api/v1/freeswitch/test/calls
     */
    @GetMapping("/calls")
    @Operation(summary = "获取通话列表", description = "获取前10条通话记录用于测试")
    public ResponseEntity<?> getAllCalls() {
        try {
            log.info("Getting all FreeSWITCH calls for testing...");
            
            List<CallEntity> calls = freeSwitchCallService.getAllCalls();
            
            // 限制返回数量，避免数据过多
            int limit = Math.min(calls.size(), 10);
            List<CallEntity> limitedCalls = calls.subList(0, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", calls.size());
            result.put("returned", limitedCalls.size());
            result.put("calls", limitedCalls);
            
            log.info("Successfully retrieved {} FreeSWITCH calls (showing {})", calls.size(), limitedCalls.size());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to get FreeSWITCH calls", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to retrieve calls: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取活动通话
     * 
     * 访问：GET http://localhost:9003/api/v1/freeswitch/test/calls/active
     */
    @GetMapping("/calls/active")
    @Operation(summary = "获取活动通话", description = "获取当前正在进行的通话")
    public ResponseEntity<?> getActiveCalls() {
        try {
            log.info("Getting active FreeSWITCH calls...");
            
            List<CallEntity> activeCalls = freeSwitchCallService.getActiveCalls();
            long activeCount = freeSwitchCallService.countActiveCalls();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("activeCount", activeCount);
            result.put("activeCalls", activeCalls);
            
            log.info("Found {} active FreeSWITCH calls", activeCount);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to get active FreeSWITCH calls", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to retrieve active calls: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据UUID查询通话
     * 
     * 访问：GET http://localhost:9003/api/v1/freeswitch/test/calls/{callUuid}
     */
    @GetMapping("/calls/{callUuid}")
    @Operation(summary = "根据UUID查询通话", description = "通过通话UUID查询特定通话记录")
    public ResponseEntity<?> getCallByUuid(
            @Parameter(description = "通话UUID", required = true)
            @PathVariable String callUuid) {
        try {
            log.info("Getting FreeSWITCH call by UUID: {}", callUuid);
            
            Optional<CallEntity> call = freeSwitchCallService.getCallByUuid(callUuid);
            
            if (call.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("found", true);
                result.put("call", call.get());
                
                log.info("Found FreeSWITCH call: {}", callUuid);
                return ResponseEntity.ok(result);
            } else {
                log.warn("FreeSWITCH call not found: {}", callUuid);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "found", false,
                    "message", "Call not found"
                ));
            }
            
        } catch (Exception e) {
            log.error("Failed to get FreeSWITCH call by UUID: {}", callUuid, e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to retrieve call: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据主叫UUID查询通话
     * 
     * 访问：GET http://localhost:9003/api/v1/freeswitch/test/calls/caller/{callerUuid}
     */
    @GetMapping("/calls/caller/{callerUuid}")
    @Operation(summary = "根据主叫UUID查询", description = "查询指定主叫的所有通话记录")
    public ResponseEntity<?> getCallsByCallerUuid(
            @Parameter(description = "主叫UUID", required = true)
            @PathVariable String callerUuid) {
        try {
            log.info("Getting FreeSWITCH calls by caller UUID: {}", callerUuid);
            
            List<CallEntity> calls = freeSwitchCallService.getCallsByCallerUuid(callerUuid);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("callerUuid", callerUuid);
            result.put("count", calls.size());
            result.put("calls", calls);
            
            log.info("Found {} FreeSWITCH calls for caller: {}", calls.size(), callerUuid);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to get FreeSWITCH calls by caller UUID: {}", callerUuid, e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to retrieve calls: " + e.getMessage()
            ));
        }
    }

    /**
     * 统计信息
     * 
     * 访问：GET http://localhost:9003/api/v1/freeswitch/test/stats
     */
    @GetMapping("/stats")
    @Operation(summary = "获取统计信息", description = "获取 FreeSWITCH 数据库的统计信息")
    public ResponseEntity<?> getStatistics() {
        try {
            log.info("Getting FreeSWITCH statistics...");
            
            long totalCalls = freeSwitchCallRepository.count();
            long activeCalls = freeSwitchCallService.countActiveCalls();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("statistics", Map.of(
                "totalCalls", totalCalls,
                "activeCalls", activeCalls,
                "completedCalls", totalCalls - activeCalls
            ));
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("FreeSWITCH statistics - Total: {}, Active: {}", totalCalls, activeCalls);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to get FreeSWITCH statistics", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to retrieve statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * 健康检查
     * 
     * 访问：GET http://localhost:9003/api/v1/freeswitch/test/health
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查 FreeSWITCH 数据源健康状态")
    public ResponseEntity<?> healthCheck() {
        try {
            // 尝试简单查询
            freeSwitchCallRepository.count();
            
            return ResponseEntity.ok(Map.of(
                "status", "UP",
                "datasource", "freeswitch",
                "message", "FreeSWITCH datasource is healthy"
            ));
            
        } catch (Exception e) {
            log.error("FreeSWITCH datasource health check failed", e);
            return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "datasource", "freeswitch",
                "message", "FreeSWITCH datasource is unhealthy",
                "error", e.getMessage()
            ));
        }
    }
}
