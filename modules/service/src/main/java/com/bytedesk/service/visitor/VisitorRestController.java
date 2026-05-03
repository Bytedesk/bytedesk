/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 15:02:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Visitor Management", description = "Visitor management APIs")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor")
@Description("Visitor Management Controller - Visitor information and interaction management APIs")
public class VisitorRestController extends BaseRestController<VisitorRequest, VisitorRestService> {

    private final VisitorRestService visitorRestService;

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query visitor by org")
    @Operation(summary = "Query Visitors by Organization", description = "Retrieve visitor list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_READ)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(VisitorRequest request) {

        Page<VisitorResponse> page = visitorRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query visitor by user")
    @Operation(summary = "Query Visitors by User", description = "Retrieve visitor list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_READ)
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(VisitorRequest visitorRequest) {
        //
        Page<VisitorResponse> visitorResponse = visitorRestService.queryByUser(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query visitor by uid")
    @Operation(summary = "Query Visitor by UID", description = "Retrieve visitor details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_READ)
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(VisitorRequest request) {
        
        VisitorResponse visitorResponse = visitorRestService.queryByUid(request);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_QUERY_VISITOR_UID, description = "query visitor by visitorUid")
    @Operation(summary = "Query Visitor by visitorUid", description = "Retrieve visitor details by visitorUid and orgUid")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = VisitorResponse.class)))
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_READ)
    @GetMapping("/query/visitorUid")
    public ResponseEntity<?> queryByVisitorUid(@RequestParam("orgUid") String orgUid,
            @RequestParam("visitorUid") String visitorUid) {
        VisitorRequest request = VisitorRequest.builder().orgUid(orgUid).visitorUid(visitorUid).build();
        VisitorResponse visitorResponse = visitorRestService.queryByVisitorUid(request);
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_CREATE, description = "create visitor")
    @Operation(summary = "Create Visitor", description = "Create a new visitor")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_CREATE)
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody VisitorRequest request) {
        
        VisitorResponse visitorResponse = visitorRestService.create(request);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_UPDATE, description = "update visitor")
    @Operation(summary = "Update Visitor", description = "Update visitor information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_UPDATE)
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody VisitorRequest visitorRequest) {

        VisitorResponse visitorResponse = visitorRestService.update(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    // update tagList
    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_UPDATE_TAG_LIST, description = "update visitor tagList")
    @Operation(summary = "Update Visitor Tag List", description = "Update the visitor tag list")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_UPDATE)
    @PostMapping("/update/tagList")
    public ResponseEntity<?> updateTagList(@RequestBody VisitorRequest visitorRequest) {

        VisitorResponse visitorResponse = visitorRestService.updateTagList(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_DELETE, description = "delete visitor")
    @Operation(summary = "Delete Visitor", description = "Delete the specified visitor")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_DELETE)
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody VisitorRequest visitorRequest) {

        visitorRestService.delete(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success("delete success"));
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_UPDATE, description = "restore visitor")
    @Operation(summary = "Restore Visitor", description = "Restore the specified soft-deleted visitor")
    @ApiResponse(responseCode = "200", description = "Restored successfully")
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_UPDATE)
    @Override
    @PostMapping("/restore")
    public ResponseEntity<?> restore(@RequestBody VisitorRequest visitorRequest) {
        return super.restore(visitorRequest);
    }

    @ActionAnnotation(title = I18Consts.I18N_VISITOR, action = I18Consts.I18N_ACTION_EXPORT, description = "export visitor")
    @Operation(summary = "Export Visitors", description = "Export visitor data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(VisitorPermissions.HAS_VISITOR_EXPORT)
    @GetMapping("/export")
    @Override
    public Object export(VisitorRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            visitorRestService,
            VisitorExcel.class,
            "访客",
            "visitor"
        );
    }

}
