/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 13:09:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

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

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 * https://www.bezkoder.com/swagger-3-annotations/#Swagger_3_annotations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
// @Tag(name = "member", description = "member apis")
public class MemberRestController extends BaseRestController<MemberRequest> {

    private final MemberRestService memberRestService;

    // @PreAuthorize("hasAuthority('MEMBER_READ')") // 所有成员都需要拉取通讯录联系人
    @Override
    public ResponseEntity<?> queryByOrg(MemberRequest request) {
        //
        Page<MemberResponse> memberResponse = memberRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    // @PreAuthorize("hasAuthority('MEMBER_READ')")
    @Override
    public ResponseEntity<?> queryByUser(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.query(request);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    // @PreAuthorize("hasAuthority('MEMBER_READ')")
    @GetMapping("/query/userUid")
    public ResponseEntity<?> queryByUserUid(MemberRequest request) {
        //
        MemberResponse memberResponse = memberRestService.queryByUserUid(request);
        if (memberResponse == null) {
            return ResponseEntity.ok(JsonResult.error("user not found"));
        }
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    // @PreAuthorize("hasAuthority('MEMBER_READ')")
    @Override
    public ResponseEntity<?> queryByUid(MemberRequest request) {
        
        MemberResponse memberResponse = memberRestService.queryByUid(request);
        if (memberResponse == null) {
            return ResponseEntity.ok(JsonResult.error("user not found"));
        }

        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @PreAuthorize("hasAuthority('MEMBER_CREATE')")
    @ActionAnnotation(title = "成员", action = "新建", description = "create member")
    @Override
    public ResponseEntity<?> create(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(member));
    }

    // @PreAuthorize("hasAuthority('MEMBER_UPDATE')")
    @ActionAnnotation(title = "成员", action = "更新", description = "update member")
    @Override
    public ResponseEntity<?> update(@RequestBody MemberRequest request) {

        MemberResponse member = memberRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @PreAuthorize("hasAuthority('MEMBER_DELETE')")
    @ActionAnnotation(title = "成员", action = "删除", description = "delete member")
    @Override
    public ResponseEntity<?> delete(@RequestBody MemberRequest request) {

        memberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

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

    // 激活 member, activate member
    @PostMapping("/activate")
    @ActionAnnotation(title = "成员", action = "激活", description = "activate member")
    public ResponseEntity<?> activate(@RequestBody MemberRequest request) {
        //
        MemberResponse member = memberRestService.activate(request);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

}
