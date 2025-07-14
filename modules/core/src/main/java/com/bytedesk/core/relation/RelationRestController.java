/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-14 09:25:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/relation")
@AllArgsConstructor
@Tag(name = "Relation Management", description = "Relation management APIs for organizing and categorizing content with relations")
public class RelationRestController extends BaseRestController<RelationRequest> {

    private final RelationRestService relationRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "关系", action = "组织查询", description = "query relation by org")
    @Operation(summary = "Query Relations by Organization", description = "Retrieve relations for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(RelationRequest request) {
        
        Page<RelationResponse> relations = relationRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(relations));
    }

    @ActionAnnotation(title = "关系", action = "用户查询", description = "query relation by user")
    @Operation(summary = "Query Relations by User", description = "Retrieve relations for the current user")
    @Override
    public ResponseEntity<?> queryByUser(RelationRequest request) {
        
        Page<RelationResponse> relations = relationRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(relations));
    }

    @ActionAnnotation(title = "关系", action = "查询详情", description = "query relation by uid")
    @Operation(summary = "Query Relation by UID", description = "Retrieve a specific relation by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(RelationRequest request) {
        
        RelationResponse relation = relationRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(relation));
    }

    @ActionAnnotation(title = "关系", action = "新建", description = "create relation")
    @Operation(summary = "Create Relation", description = "Create a new relation")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(RelationRequest request) {
        
        RelationResponse relation = relationRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(relation));
    }

    @ActionAnnotation(title = "关系", action = "更新", description = "update relation")
    @Operation(summary = "Update Relation", description = "Update an existing relation")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(RelationRequest request) {
        
        RelationResponse relation = relationRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(relation));
    }

    @ActionAnnotation(title = "关系", action = "删除", description = "delete relation")
    @Operation(summary = "Delete Relation", description = "Delete a relation")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(RelationRequest request) {
        
        relationRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "关系", action = "导出", description = "export relation")
    @Operation(summary = "Export Relations", description = "Export relations to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(RelationRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            relationRestService,
            RelationExcel.class,
            "关系",
            "relation"
        );
    }

    
    
}