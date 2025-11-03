/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 11:04:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/agent/status/setting")
@AllArgsConstructor
@Tag(name = "客服状态设置管理", description = "客服状态设置管理相关接口")
public class AgentStatusSettingRestController extends BaseRestController<AgentStatusSettingRequest, AgentStatusSettingRestService> {

    private final AgentStatusSettingRestService tagService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "查询组织", description = "query tag by org")
    @Override
    public ResponseEntity<?> queryByOrg(AgentStatusSettingRequest request) {
        
        Page<AgentStatusSettingResponse> tags = tagService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "查询用户", description = "query tag by user")
    @Override
    public ResponseEntity<?> queryByUser(AgentStatusSettingRequest request) {
        
        Page<AgentStatusSettingResponse> tags = tagService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query tag by uid")
    @Override
    public ResponseEntity<?> queryByUid(AgentStatusSettingRequest request) {
        
        AgentStatusSettingResponse tag = tagService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create tag")
    @Override
    @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(AgentStatusSettingRequest request) {
        
        AgentStatusSettingResponse tag = tagService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update tag")
    @Override
    @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(AgentStatusSettingRequest request) {
        
        AgentStatusSettingResponse tag = tagService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete tag")
    @Override
    @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(AgentStatusSettingRequest request) {
        
        tagService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export tag")
    @Override
    @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(AgentStatusSettingRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tagService,
            AgentStatusSettingExcel.class,
            "标签",
            "tag"
        );
    }

    
    
}