/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23 14:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-23 14:45:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_settings;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "客服配置管理", description = "客服配置相关接口")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent/settings")
public class AgentSettingsController {

    private final AgentSettingsService agentSettingsService;

    @Operation(summary = "获取或创建默认配置", description = "根据组织UID获取或创建默认客服配置")
    @GetMapping("/default")
    public ResponseEntity<?> getOrCreateDefault(@RequestParam String orgUid) {
        try {
            AgentSettingsEntity settings = agentSettingsService.getOrCreateDefault(orgUid);
            return ResponseEntity.ok(JsonResult.success("获取默认配置成功", settings));
        } catch (Exception e) {
            log.error("获取默认配置失败", e);
            return ResponseEntity.ok(JsonResult.error("获取默认配置失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "根据UID查询配置", description = "根据配置UID查询客服配置详情")
    @GetMapping("/{uid}")
    public ResponseEntity<?> findByUid(@PathVariable String uid) {
        return agentSettingsService.findByUid(uid)
                .map(settings -> ResponseEntity.ok(JsonResult.success("查询成功", settings)))
                .orElse(ResponseEntity.ok(JsonResult.error("配置不存在")));
    }

    @Operation(summary = "创建配置", description = "创建新的客服配置")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AgentSettingsRequest request) {
        try {
            AgentSettingsEntity settings = AgentSettingsEntity.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                    .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                    .serviceSettings(request.getServiceSettings())
                    .messageLeaveSettings(request.getMessageLeaveSettings())
                    .autoReplySettings(request.getAutoReplySettings())
                    .queueSettings(request.getQueueSettings())
                    .rateDownSettings(request.getRateDownSettings())
                    .build();
            settings.setOrgUid(request.getOrgUid());
            
            AgentSettingsEntity created = agentSettingsService.create(settings);
            return ResponseEntity.ok(JsonResult.success("创建成功", created));
        } catch (Exception e) {
            log.error("创建配置失败", e);
            return ResponseEntity.ok(JsonResult.error("创建配置失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "更新配置", description = "更新现有的客服配置")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AgentSettingsRequest request) {
        try {
            return agentSettingsService.findByUid(request.getUid())
                    .map(settings -> {
                        settings.setName(request.getName());
                        settings.setDescription(request.getDescription());
                        settings.setEnabled(request.getEnabled());
                        settings.setServiceSettings(request.getServiceSettings());
                        settings.setMessageLeaveSettings(request.getMessageLeaveSettings());
                        settings.setAutoReplySettings(request.getAutoReplySettings());
                        settings.setQueueSettings(request.getQueueSettings());
                        settings.setRateDownSettings(request.getRateDownSettings());
                        
                        AgentSettingsEntity updated = agentSettingsService.update(settings);
                        return ResponseEntity.ok(JsonResult.success("更新成功", updated));
                    })
                    .orElse(ResponseEntity.ok(JsonResult.error("配置不存在")));
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return ResponseEntity.ok(JsonResult.error("更新配置失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "删除配置", description = "根据UID删除客服配置")
    @DeleteMapping("/{uid}")
    public ResponseEntity<?> delete(@PathVariable String uid) {
        try {
            agentSettingsService.delete(uid);
            return ResponseEntity.ok(JsonResult.success("删除成功"));
        } catch (Exception e) {
            log.error("删除配置失败", e);
            return ResponseEntity.ok(JsonResult.error("删除配置失败: " + e.getMessage()));
        }
    }
}
