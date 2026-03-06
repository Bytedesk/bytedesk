/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:34:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-30 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/api/v1/taboo")
@AllArgsConstructor
@Tag(name = "Taboo Management", description = "Taboo word management APIs for content filtering")
public class TabooRestController extends BaseRestController<TabooRequest, TabooRestService> {

    private final TabooRestService tabooRestService;

    @ActionAnnotation(title = "敏感词", action = "组织查询", description = "query taboo by org")
    @Operation(summary = "Query Taboo by Organization", description = "Retrieve taboo words for the current organization")
    @PreAuthorize(TabooPermissions.HAS_TABOO_READ)
    @Override
    public ResponseEntity<?> queryByOrg(TabooRequest request) {
        
        Page<TabooResponse> page = tabooRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "敏感词", action = "用户查询", description = "query taboo by user")
    @Operation(summary = "Query Taboo by User", description = "Retrieve taboo words for the current user")
    @PreAuthorize(TabooPermissions.HAS_TABOO_READ)
    @Override
    public ResponseEntity<?> queryByUser(TabooRequest request) {
        
        Page<TabooResponse> page = tabooRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "敏感词", action = "查询详情", description = "query taboo by uid")
    @Operation(summary = "Query Taboo by UID", description = "Retrieve a specific taboo word by its unique identifier")
    @PreAuthorize(TabooPermissions.HAS_TABOO_READ)
    @Override
    public ResponseEntity<?> queryByUid(TabooRequest request) {
        
        TabooResponse taboo = tabooRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(taboo));
    }

    @ActionAnnotation(title = "敏感词", action = "新建", description = "create taboo")
    @Operation(summary = "Create Taboo", description = "Create a new taboo word")
    @PreAuthorize(TabooPermissions.HAS_TABOO_CREATE)
    @Override
    public ResponseEntity<?> create(TabooRequest request) {
        
        TabooResponse taboo = tabooRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(taboo));
    }

    @ActionAnnotation(title = "敏感词", action = "更新", description = "update taboo")
    @Operation(summary = "Update Taboo", description = "Update an existing taboo word")
    @PreAuthorize(TabooPermissions.HAS_TABOO_UPDATE)
    @Override
    public ResponseEntity<?> update(TabooRequest request) {
        
        TabooResponse taboo = tabooRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(taboo));
    }

    @ActionAnnotation(title = "敏感词", action = "删除", description = "delete taboo")
    @Operation(summary = "Delete Taboo", description = "Delete a taboo word")
    @PreAuthorize(TabooPermissions.HAS_TABOO_DELETE)
    @Override
    public ResponseEntity<?> delete(TabooRequest request) {
        
        tabooRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    @ActionAnnotation(title = "敏感词", action = "启用", description = "enable taboo")
    @Operation(summary = "Enable Taboo", description = "Enable a taboo word")
    @PreAuthorize(TabooPermissions.HAS_TABOO_UPDATE)
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody TabooRequest request) {
        
        TabooResponse taboo = tabooRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(taboo));
    }
    
    @ActionAnnotation(title = "敏感词", action = "导出", description = "export taboo")
    @Operation(summary = "Export Taboo", description = "Export taboo words to Excel format")
    @PreAuthorize(TabooPermissions.HAS_TABOO_EXPORT)
    @Override
    @GetMapping("/export")
    public Object export(TabooRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tabooRestService,
            TabooExcel.class,
            "敏感词",
            "taboo"
        );
    }

    
}
