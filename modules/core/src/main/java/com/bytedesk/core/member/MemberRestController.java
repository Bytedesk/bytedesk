/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 10:52:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestControllerOverride;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 * https://www.bezkoder.com/swagger-3-annotations/#Swagger_3_annotations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member Management", description = "Member management APIs")
public class MemberRestController extends BaseRestControllerOverride<MemberRequest> {

    private final MemberRestService memberRestService;

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query member by org")
    @Operation(summary = "Query Members by Organization", description = "Retrieve member list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MemberRequest request) {
        //
        Page<MemberResponse> memberResponse = memberRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query member by user")
    @Operation(summary = "Query Members by User", description = "Retrieve member information by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.query(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_USER_UID, description = "query member by user uid")
    @Operation(summary = "Query Member by User UID", description = "Retrieve member information by user UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @GetMapping("/query/userUid")
    public ResponseEntity<?> queryByUserUid(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.queryByUserUid(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query member by uid")
    @Operation(summary = "Query Member by UID", description = "Retrieve member details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(MemberRequest request) {
        
        MemberResponse memberResponse = memberRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Operation(summary = "Create Member", description = "Create a new member")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_CREATE, description = "create member")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Update Member", description = "Update member information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_UPDATE, description = "update member")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Delete Member", description = "Delete the specified member")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PreAuthorize(MemberPermissions.HAS_MEMBER_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_DELETE, description = "delete member")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MemberRequest request) {

        memberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Members", description = "Export member data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(MemberPermissions.HAS_MEMBER_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_EXPORT, description = "export member")
    @GetMapping("/export")
    public Object export(MemberRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            memberRestService,
            MemberExcelExport.class,
            "成员",
            "member"
        );
    }

    @Operation(summary = "Activate Member", description = "Activate the specified member")
    @ApiResponse(responseCode = "200", description = "Activated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @PostMapping("/activate")
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_ACTIVATE, description = "activate member")
    public ResponseEntity<?> activate(@RequestBody MemberRequest request) {
        //
        MemberResponse member = memberRestService.activate(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Force Logout Member", description = "Force the specified member to logout from desktop and block re-login")
    @ApiResponse(responseCode = "200", description = "Force logout applied successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @PostMapping("/force/logout")
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_UPDATE, description = "force logout member")
    public ResponseEntity<?> forceLogout(@RequestBody MemberRequest request) {
        MemberResponse member = memberRestService.forceLogout(request);
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "Restore Member Login", description = "Restore the specified member login after a forced logout")
    @ApiResponse(responseCode = "200", description = "Member login restored successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize(MemberPermissions.HAS_MEMBER_UPDATE)
    @PostMapping("/restore/login")
    @ActionAnnotation(title = I18Consts.I18N_MEMBER, action = I18Consts.I18N_ACTION_UPDATE, description = "restore member login")
    public ResponseEntity<?> restoreLogin(@RequestBody MemberRequest request) {
        MemberResponse member = memberRestService.restoreLogin(request);
        return ResponseEntity.ok(JsonResult.success(member));
    }
}
