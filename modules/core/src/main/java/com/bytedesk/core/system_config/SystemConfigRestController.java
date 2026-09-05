/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-04 09:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-04 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.system_config;

import java.util.List;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * 系统全局配置管理接口（仅超级管理员）
 * 
 * 查询返回由 SystemConfigKeyEnum 合成的完整清单（含默认值/覆盖值/生效来源）；
 * 保存为批量 upsert，value 为空即删除覆盖（恢复默认），保存后失效缓存，
 * 下一次 /config/bytedesk/properties 下发即携带新值，前端刷新后生效（无需重启）。
 */
@RestController
@RequestMapping("/api/v1/system_config")
@AllArgsConstructor
@Tag(name = "System Config", description = "System global config management APIs for platform branding and agreements")
@Description("System Config Management Controller - Platform-level global config APIs (super admin only)")
public class SystemConfigRestController {

    private final SystemConfigRestService systemConfigRestService;

    @ActionAnnotation(title = I18Consts.I18N_SYSTEM_CONFIG, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query all system configs")
    @Operation(summary = "Query System Configs", description = "Retrieve all system config items with default/override/effective values")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @GetMapping("/query")
    public ResponseEntity<?> query() {
        List<SystemConfigResponse> configs = systemConfigRestService.queryAll();
        return ResponseEntity.ok(JsonResult.success(configs));
    }

    @ActionAnnotation(title = I18Consts.I18N_SYSTEM_CONFIG, action = I18Consts.I18N_ACTION_SAVE_SYSTEM_CONFIG, description = "batch save system configs")
    @Operation(summary = "Save System Configs", description = "Batch upsert system config overrides; blank value resets to default")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody SystemConfigRequest request) {
        List<SystemConfigResponse> configs = systemConfigRestService.save(request);
        return ResponseEntity.ok(JsonResult.success(configs));
    }
}
