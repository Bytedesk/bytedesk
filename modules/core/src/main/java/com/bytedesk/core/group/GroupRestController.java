/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:22:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.member.MemberProtobuf;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "Group Management", description = "Group management APIs")
public class GroupRestController extends BaseRestController<GroupRequest, GroupRestService> {
    
    private final GroupRestService groupRestService;
    
    @Operation(summary = "Query Groups by Organization", description = "Retrieve group list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(GroupRequest request) {
        
        Page<GroupResponse> page = groupRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Groups by User", description = "Retrieve group list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(GroupRequest request) {

        Page<GroupResponse> page = groupRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }
    
    @Operation(summary = "Query Group by UID", description = "Retrieve group details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(GroupRequest request) {
        
        GroupResponse group = groupRestService.queryByUid(request);
        
        return ResponseEntity.ok(JsonResult.success(group));
    }
    
    @Operation(summary = "Query Group Members", description = "Retrieve group members with pagination")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MemberProtobuf.class)))
    /**
     * 分页查询群组成员
     * @param request 包含群组uid和分页参数
     * @return 成员列表分页结果
     */
    @GetMapping("/query/members")
    public ResponseEntity<?> queryMembers(GroupRequest request) {
        
        Page<MemberProtobuf> page = groupRestService.queryGroupMembers(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Create Group", description = "Create a new group")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_CREATE, description = "create group")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "Update Group", description = "Update group information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_UPDATE, description = "update group")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody GroupRequest request) {

        GroupResponse group = groupRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "Update Group Name", description = "Update the group name")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    // update/name
    @PostMapping("/update/name")
    public ResponseEntity<?> updateGroupName(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.updateGroupName(request);
        
        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "Update Group Top Tip", description = "Update the group top-tip information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    // update/topTip
    @PostMapping("/update/topTip")
    public ResponseEntity<?> updateGroupTopTip(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.updateGroupTopTip(request);
        
        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "Invite Members", description = "Invite members to join the group")
    @ApiResponse(responseCode = "200", description = "Invited successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_INVITE, description = "invite members to group")
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMembers(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.invite(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }
    
    @Operation(summary = "Join Group", description = "Join the specified group")
    @ApiResponse(responseCode = "200", description = "Joined successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_JOIN, description = "join group")
    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.join(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "Remove Members", description = "Remove members from the group")
    @ApiResponse(responseCode = "200", description = "Removed successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_REMOVE, description = "remove members from group")
    @PostMapping("/remove")
    public ResponseEntity<?> removeMembers(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.remove(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "Leave Group", description = "Leave the specified group")
    @ApiResponse(responseCode = "200", description = "Left successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_LEAVE, description = "leave group")
    @PostMapping("/leave")
    public ResponseEntity<?> leaveGroup(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.leave(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "Dismiss Group", description = "Dismiss the specified group")
    @ApiResponse(responseCode = "200", description = "Dismissed successfully")
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_DISMISS, description = "dismiss group")
    @PostMapping("/dismiss")
    public ResponseEntity<?> dismissGroup(@RequestBody GroupRequest request) {

        groupRestService.dismiss(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Delete Group", description = "Delete the specified group")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_DELETE, description = "delete group")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody GroupRequest request) {
        
        groupRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Groups", description = "Export group data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @ActionAnnotation(title = I18Consts.I18N_GROUP, action = I18Consts.I18N_ACTION_EXPORT, description = "export group")
    @Override
    @GetMapping("/export")
    public Object export(GroupRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            groupRestService,
            GroupExcel.class,
            "群组",
            "group"
        );
    }
}
