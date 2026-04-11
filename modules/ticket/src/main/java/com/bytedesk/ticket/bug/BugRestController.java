/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.bug;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/bug")
@AllArgsConstructor
@Tag(name = "Bug Management", description = "Bug management APIs for organizing and categorizing content with bugs")
@Description("Bug Management Controller - Content bugging and categorization APIs")
public class BugRestController extends BaseRestController<BugRequest, BugRestService> {

    private final BugRestService bugRestService;

    @ActionAnnotation(title = I18Consts.I18N_BUG, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query bug by org")
    @Operation(summary = "Query Bugs by Organization", description = "Retrieve bugs for the current organization")
    @PreAuthorize(BugPermissions.HAS_BUG_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(BugRequest request) {
        
        Page<BugResponse> bugs = bugRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(bugs));
    }

    @ActionAnnotation(title = I18Consts.I18N_BUG, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query bug by user")
    @Operation(summary = "Query Bugs by User", description = "Retrieve bugs for the current user")
    @PreAuthorize(BugPermissions.HAS_BUG_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(BugRequest request) {
        
        Page<BugResponse> bugs = bugRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(bugs));
    }

    @ActionAnnotation(title = I18Consts.I18N_BUG, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query bug by uid")
    @Operation(summary = "Query Bug by UID", description = "Retrieve a specific bug by its unique identifier")
    @PreAuthorize(BugPermissions.HAS_BUG_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(BugRequest request) {
        
        BugResponse bug = bugRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(bug));
    }

    @ActionAnnotation(title = I18Consts.I18N_BUG, action = I18Consts.I18N_ACTION_CREATE, description = "create bug")
    @Operation(summary = "Create Bug", description = "Create a new bug")
    @Override
    @PreAuthorize(BugPermissions.HAS_BUG_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody BugRequest request) {
        
        BugResponse bug = bugRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(bug));
    }

    @ActionAnnotation(title = I18Consts.I18N_BUG, action = I18Consts.I18N_ACTION_UPDATE, description = "update bug")
    @Operation(summary = "Update Bug", description = "Update an existing bug")
    @Override
    @PreAuthorize(BugPermissions.HAS_BUG_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody BugRequest request) {
        
        BugResponse bug = bugRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(bug));
    }

    @ActionAnnotation(title = I18Consts.I18N_BUG, action = I18Consts.I18N_ACTION_DELETE, description = "delete bug")
    @Operation(summary = "Delete Bug", description = "Delete a bug")
    @Override
    @PreAuthorize(BugPermissions.HAS_BUG_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody BugRequest request) {
        
        bugRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_BUG, action = I18Consts.I18N_ACTION_EXPORT, description = "export bug")
    @Operation(summary = "Export Bugs", description = "Export bugs to Excel format")
    @Override
    @PreAuthorize(BugPermissions.HAS_BUG_EXPORT)
    @GetMapping("/export")
    public Object export(BugRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            bugRestService,
            BugExcel.class,
            "Bug",
            "bug"
        );
    }

    
    
}