/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 11:24:15
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
import com.bytedesk.core.base.BaseRestController;
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
@Tag(name = "成员管理", description = "成员管理相关接口")
public class MemberRestController extends BaseRestController<MemberRequest> {

    private final MemberRestService memberRestService;

    @Operation(summary = "查询组织下的成员", description = "根据组织ID查询成员列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    // @PreAuthorize("hasAuthority('MEMBER_READ')") // 所有成员都需要拉取通讯录联系人
    @Override
    public ResponseEntity<?> queryByOrg(MemberRequest request) {
        //
        Page<MemberResponse> memberResponse = memberRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Operation(summary = "查询用户下的成员", description = "根据用户ID查询成员信息")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    // @PreAuthorize("hasAuthority('MEMBER_READ')")
    @Override
    public ResponseEntity<?> queryByUser(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.query(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Operation(summary = "根据用户UID查询成员", description = "根据用户UID查询成员信息")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    // @PreAuthorize("hasAuthority('MEMBER_READ')")
    @GetMapping("/query/userUid")
    public ResponseEntity<?> queryByUserUid(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.queryByUserUid(request);
        if (memberResponse == null) {
            return ResponseEntity.ok(JsonResult.error("login first"));
        }
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Operation(summary = "查询指定成员", description = "根据UID查询成员详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    // @PreAuthorize("hasAuthority('MEMBER_READ')")
    @Override
    public ResponseEntity<?> queryByUid(MemberRequest request) {
        
        MemberResponse memberResponse = memberRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Operation(summary = "创建成员", description = "创建新的成员")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize("hasAuthority('MEMBER_CREATE')")
    @ActionAnnotation(title = "成员", action = "新建", description = "create member")
    @Override
    public ResponseEntity<?> create(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "更新成员", description = "更新成员信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    // @PreAuthorize("hasAuthority('MEMBER_UPDATE')")
    @ActionAnnotation(title = "成员", action = "更新", description = "update member")
    @Override
    public ResponseEntity<?> update(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @Operation(summary = "删除成员", description = "删除指定的成员")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize("hasAuthority('MEMBER_DELETE')")
    @ActionAnnotation(title = "成员", action = "删除", description = "delete member")
    @Override
    public ResponseEntity<?> delete(@RequestBody MemberRequest request) {

        memberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出成员", description = "导出成员数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @PreAuthorize("hasAuthority('MEMBER_EXPORT')")
    @ActionAnnotation(title = "成员", action = "导出", description = "export member")
    @GetMapping("/export")
    public Object export(MemberRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            memberRestService,
            MemberExcel.class,
            "成员",
            "member"
        );
    }

    @Operation(summary = "激活成员", description = "激活指定的成员")
    @ApiResponse(responseCode = "200", description = "激活成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberResponse.class)))
    @PostMapping("/activate")
    @ActionAnnotation(title = "成员", action = "激活", description = "activate member")
    public ResponseEntity<?> activate(@RequestBody MemberRequest request) {
        //
        MemberResponse member = memberRestService.activate(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }
}
