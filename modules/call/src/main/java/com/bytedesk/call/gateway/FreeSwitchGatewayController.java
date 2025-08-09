/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.gateway;

import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FreeSwitch网关管理REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/freeswitch/api/v1/gateways")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchGatewayController {

    private final FreeSwitchGatewayService gatewayService;

    /**
     * 创建网关
     */
    @PostMapping
    public ResponseEntity<JsonResult<?>> createGateway(@Valid @RequestBody FreeSwitchGatewayRequest request) {
        try {
            FreeSwitchGatewayEntity gateway = gatewayService.createGateway(
                    request.getGatewayName(),
                    request.getDescription(),
                    request.getProxy(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getFromUser(),
                    request.getFromDomain(),
                    request.getRegister(),
                    request.getRegisterTransport()
            );
            
            FreeSwitchGatewayResponse response = FreeSwitchGatewayResponse.fromEntitySafe(gateway);
            return ResponseEntity.ok(JsonResult.success("网关创建成功", response));
        } catch (Exception e) {
            log.error("创建网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取网关详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<JsonResult<?>> getGateway(@PathVariable Long id) {
        Optional<FreeSwitchGatewayEntity> gateway = gatewayService.findById(id);
        if (gateway.isPresent()) {
            FreeSwitchGatewayResponse response = FreeSwitchGatewayResponse.fromEntitySafe(gateway.get());
            return ResponseEntity.ok(JsonResult.success("获取网关详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据名称获取网关
     */
    @GetMapping("/name/{gatewayName}")
    public ResponseEntity<JsonResult<?>> getGatewayByName(@PathVariable String gatewayName) {
        Optional<FreeSwitchGatewayEntity> gateway = gatewayService.findByGatewayName(gatewayName);
        if (gateway.isPresent()) {
            FreeSwitchGatewayResponse response = FreeSwitchGatewayResponse.fromEntitySafe(gateway.get());
            return ResponseEntity.ok(JsonResult.success("获取网关详情成功", response));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取网关列表（分页）
     */
    @GetMapping
    public ResponseEntity<JsonResult<?>> getGateways(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FreeSwitchGatewayEntity> gatewayPage = gatewayService.findAll(pageable);
        Page<FreeSwitchGatewayResponse> responsePage = gatewayPage.map(FreeSwitchGatewayResponse::fromEntitySafe);
        
        return ResponseEntity.ok(JsonResult.success("获取网关列表成功", responsePage));
    }

    /**
     * 获取启用的网关列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<JsonResult<?>> getEnabledGateways() {
        List<FreeSwitchGatewayEntity> gateways = gatewayService.findEnabledGateways();
        List<FreeSwitchGatewayResponse> responses = gateways.stream()
                .map(FreeSwitchGatewayResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取启用网关列表成功", responses));
    }

    /**
     * 获取在线的网关列表
     */
    @GetMapping("/online")
    public ResponseEntity<JsonResult<?>> getOnlineGateways() {
        List<FreeSwitchGatewayEntity> gateways = gatewayService.findOnlineGateways();
        List<FreeSwitchGatewayResponse> responses = gateways.stream()
                .map(FreeSwitchGatewayResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取在线网关列表成功", responses));
    }

    /**
     * 根据状态获取网关列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<JsonResult<?>> getGatewaysByStatus(@PathVariable String status) {
        List<FreeSwitchGatewayEntity> gateways = gatewayService.findByStatus(status);
        List<FreeSwitchGatewayResponse> responses = gateways.stream()
                .map(FreeSwitchGatewayResponse::fromEntitySafe)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(JsonResult.success("获取网关列表成功", responses));
    }

    /**
     * 更新网关
     */
    @PutMapping("/{id}")
    public ResponseEntity<JsonResult<?>> updateGateway(
            @PathVariable Long id,
            @Valid @RequestBody FreeSwitchGatewayRequest request) {
        try {
            FreeSwitchGatewayEntity gateway = gatewayService.updateGateway(
                    id,
                    request.getDescription(),
                    request.getProxy(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getFromUser(),
                    request.getFromDomain(),
                    request.getRegister(),
                    request.getRegisterTransport()
            );
            
            FreeSwitchGatewayResponse response = FreeSwitchGatewayResponse.fromEntitySafe(gateway);
            return ResponseEntity.ok(JsonResult.success("网关更新成功", response));
        } catch (Exception e) {
            log.error("更新网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 启用网关
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<JsonResult<?>> enableGateway(@PathVariable Long id) {
        try {
            gatewayService.enableGateway(id);
            return ResponseEntity.ok(JsonResult.success("网关启用成功"));
        } catch (Exception e) {
            log.error("启用网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 禁用网关
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<JsonResult<?>> disableGateway(@PathVariable Long id) {
        try {
            gatewayService.disableGateway(id);
            return ResponseEntity.ok(JsonResult.success("网关禁用成功"));
        } catch (Exception e) {
            log.error("禁用网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新网关状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<JsonResult<?>> updateGatewayStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            gatewayService.updateGatewayStatus(id, status);
            return ResponseEntity.ok(JsonResult.success("网关状态更新成功"));
        } catch (Exception e) {
            log.error("更新网关状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 删除网关
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResult<?>> deleteGateway(@PathVariable Long id) {
        try {
            gatewayService.deleteGateway(id);
            return ResponseEntity.ok(JsonResult.success("网关删除成功"));
        } catch (Exception e) {
            log.error("删除网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取网关统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<JsonResult<?>> getGatewayStats() {
        long totalCount = gatewayService.countTotal();
        long enabledCount = gatewayService.countEnabled();
        long onlineCount = gatewayService.countOnline();
        
        GatewayStats stats = new GatewayStats(totalCount, enabledCount, onlineCount);
        return ResponseEntity.ok(JsonResult.success("获取网关统计成功", stats));
    }

    /**
     * 检查网关名称是否存在
     */
    @GetMapping("/exists/{gatewayName}")
    public ResponseEntity<JsonResult<?>> checkGatewayNameExists(@PathVariable String gatewayName) {
        boolean exists = gatewayService.existsByGatewayName(gatewayName);
        return ResponseEntity.ok(JsonResult.success("检查网关名称成功", exists));
    }

    /**
     * 网关统计信息内部类
     */
    public static class GatewayStats {
        private final long totalCount;
        private final long enabledCount;
        private final long onlineCount;

        public GatewayStats(long totalCount, long enabledCount, long onlineCount) {
            this.totalCount = totalCount;
            this.enabledCount = enabledCount;
            this.onlineCount = onlineCount;
        }

        public long getTotalCount() { return totalCount; }
        public long getEnabledCount() { return enabledCount; }
        public long getOnlineCount() { return onlineCount; }
    }
}
