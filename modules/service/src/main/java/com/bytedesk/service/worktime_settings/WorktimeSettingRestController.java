/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 16:36:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime_settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/worktime/setting")
@AllArgsConstructor
@Tag(name = "工作时间设置管理", description = "工作时间设置管理相关接口")
public class WorktimeSettingRestController extends BaseRestController<WorktimeSettingRequest, WorktimeSettingRestService> {

    private final WorktimeSettingRestService tagService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "查询组织", description = "query tag by org")
    @Operation(summary = "根据组织查询工作时间设置", description = "查询组织的工作时间设置")
    @Override
    public ResponseEntity<?> queryByOrg(WorktimeSettingRequest request) {
        
        Page<WorktimeSettingResponse> tags = tagService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "查询用户", description = "query tag by user")
    @Operation(summary = "根据用户查询工作时间设置", description = "查询用户的工作时间设置")
    @Override
    public ResponseEntity<?> queryByUser(WorktimeSettingRequest request) {
        
        Page<WorktimeSettingResponse> tags = tagService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query tag by uid")
    @Operation(summary = "根据UID查询工作时间设置", description = "通过UID查询具体的工作时间设置")
    @Override
    public ResponseEntity<?> queryByUid(WorktimeSettingRequest request) {
        
        WorktimeSettingResponse tag = tagService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create tag")
    @Operation(summary = "创建工作时间设置", description = "创建新的工作时间设置")
    @Override
    @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(WorktimeSettingRequest request) {
        
        WorktimeSettingResponse tag = tagService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update tag")
    @Operation(summary = "更新工作时间设置", description = "更新现有的工作时间设置")
    @Override
    @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(WorktimeSettingRequest request) {
        
        WorktimeSettingResponse tag = tagService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete tag")
    @Operation(summary = "删除工作时间设置", description = "删除指定的工作时间设置")
    @Override
    @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(WorktimeSettingRequest request) {
        
        tagService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export tag")
    @Operation(summary = "导出工作时间设置", description = "导出工作时间设置数据")
    @Override
    @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(WorktimeSettingRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tagService,
            WorktimeSettingExcel.class,
            "标签",
            "tag"
        );
    }

    
    
}