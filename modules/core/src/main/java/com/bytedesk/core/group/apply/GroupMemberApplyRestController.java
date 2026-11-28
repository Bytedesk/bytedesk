/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group.apply;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/group/apply")
@Tag(name = "Group Member Apply", description = "Public group join apply APIs")
public class GroupMemberApplyRestController {

    private final GroupMemberApplyService groupMemberApplyService;

    @PreAuthorize(GroupMemberApplyPermissions.HAS_GROUP_MEMBER_APPLY_CREATE)
    @Operation(summary = "Create Join Apply", description = "Apply to join a public group")
    @ApiResponse(responseCode = "200", description = "Apply created or existing pending returned")
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_JOIN, description = "apply to join group")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody GroupMemberApplyRequest request) {
        GroupMemberApplyResponse apply = groupMemberApplyService.create(request);
        return ResponseEntity.ok(JsonResult.success(apply));
    }

    @PreAuthorize(GroupMemberApplyPermissions.HAS_GROUP_MEMBER_APPLY_READ)
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query group")
    @Operation(summary = "Query Applies by Admin Groups", description = "Pending applies of groups the current user administers")
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByAdminGroups(GroupMemberApplyRequest request) {
        Page<GroupMemberApplyResponse> page = groupMemberApplyService.queryByAdminGroups(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(GroupMemberApplyPermissions.HAS_GROUP_MEMBER_APPLY_READ)
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query group")
    @Operation(summary = "Query My Applies", description = "Applies submitted by current user")
    @GetMapping("/query/user")
    public ResponseEntity<?> queryByUser(GroupMemberApplyRequest request) {
        Page<GroupMemberApplyResponse> page = groupMemberApplyService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(GroupMemberApplyPermissions.HAS_GROUP_MEMBER_APPLY_UPDATE)
    @Operation(summary = "Approve Apply", description = "Approve a pending join apply (group owner/admin only)")
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_UPDATE, description = "approve group join apply")
    @PostMapping("/approve")
    public ResponseEntity<?> approve(@RequestBody GroupMemberApplyRequest request) {
        GroupMemberApplyResponse apply = groupMemberApplyService.approve(request);
        return ResponseEntity.ok(JsonResult.success(approvePayload(apply)));
    }

    @PreAuthorize(GroupMemberApplyPermissions.HAS_GROUP_MEMBER_APPLY_UPDATE)
    @Operation(summary = "Reject Apply", description = "Reject a pending join apply (group owner/admin only)")
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_UPDATE, description = "reject group join apply")
    @PostMapping("/reject")
    public ResponseEntity<?> reject(@RequestBody GroupMemberApplyRequest request) {
        GroupMemberApplyResponse apply = groupMemberApplyService.reject(request);
        return ResponseEntity.ok(JsonResult.success(apply));
    }

    @PreAuthorize(GroupMemberApplyPermissions.HAS_GROUP_MEMBER_APPLY_UPDATE)
    @Operation(summary = "Cancel Apply", description = "Cancel my pending apply")
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_REMOVE, description = "cancel group join apply")
    @PostMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestBody GroupMemberApplyRequest request) {
        GroupMemberApplyResponse apply = groupMemberApplyService.cancel(request);
        return ResponseEntity.ok(JsonResult.success(apply));
    }

    /** approve 已带成员入库，提示前端刷新群列表 */
    private GroupMemberApplyResponse approvePayload(GroupMemberApplyResponse apply) {
        return apply;
    }
}
