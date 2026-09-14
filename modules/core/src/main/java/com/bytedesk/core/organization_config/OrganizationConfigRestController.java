/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-14 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.organization_config;

import java.util.List;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * 组织级配置管理接口（组织管理员）
 *
 * 查询返回由 OrganizationConfigKeyEnum 合成的完整清单（含系统级默认值/组织覆盖值/生效来源）；
 * 保存为批量 upsert，value 为空即删除覆盖（恢复系统级默认），保存后失效缓存。
 * apiKey 类 key 响应脱敏（只报是否已配置 + 掩码）。
 */
@RestController
@RequestMapping("/api/v1/organization_config")
@AllArgsConstructor
@Tag(name = "Organization Config", description = "Organization-level config management APIs (org admin only)")
@Description("Organization Config Management Controller - Organization-level config APIs (e.g. TTS/ASR/OCR apiKeys)")
public class OrganizationConfigRestController {

    private final com.bytedesk.core.rbac.auth.AuthService authService;

    private final OrganizationConfigRestService organizationConfigRestService;

    @ActionAnnotation(title = "组织级配置", action = "查询", description = "query organization configs")
    @Operation(summary = "Query Organization Configs", description = "Retrieve organization config items with system default/override/effective values (apiKey masked). orgUid defaults to current user's organization; SUPER may query any org")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @GetMapping("/query")
    public ResponseEntity<?> query(@RequestParam(value = "orgUid", required = false) String orgUid) {
        String targetOrgUid = orgUid;
        if (!StringUtils.hasText(targetOrgUid)) {
            UserEntity user = authService.getUser();
            if (user == null || !StringUtils.hasText(user.getOrgUid())) {
                return ResponseEntity.ok(JsonResult.error("orgUid is required"));
            }
            targetOrgUid = user.getOrgUid();
        }
        List<OrganizationConfigResponse> configs = organizationConfigRestService.queryAll(targetOrgUid);
        return ResponseEntity.ok(JsonResult.success(configs));
    }

    @ActionAnnotation(title = "组织级配置", action = "保存", description = "batch save organization configs")
    @Operation(summary = "Save Organization Configs", description = "Batch upsert organization config overrides; blank value resets to system default. Writes go to current user's organization (SUPER may specify another orgUid)")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody OrganizationConfigRequest request) {
        List<OrganizationConfigResponse> configs = organizationConfigRestService.save(request);
        return ResponseEntity.ok(JsonResult.success(configs));
    }
}
