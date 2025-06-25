/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 09:26:42
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
@Tag(name = "群组管理", description = "群组管理相关接口")
public class GroupRestController extends BaseRestController<GroupRequest> {
    
    private final GroupRestService groupRestService;
    
    @Operation(summary = "查询组织下的群组", description = "根据组织ID查询群组列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(GroupRequest request) {
        
        Page<GroupResponse> page = groupRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的群组", description = "根据用户ID查询群组列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(GroupRequest request) {

        Page<GroupResponse> page = groupRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }
    
    @Operation(summary = "查询指定群组", description = "根据UID查询群组详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(GroupRequest request) {
        
        GroupResponse group = groupRestService.queryByUid(request);
        if (group == null) {
            return ResponseEntity.ok(JsonResult.error("not found"));
        }
        
        return ResponseEntity.ok(JsonResult.success(group));
    }
    
    @Operation(summary = "查询群组成员", description = "分页查询群组成员")
    @ApiResponse(responseCode = "200", description = "查询成功",
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

    @Operation(summary = "创建群组", description = "创建新的群组")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = "群组", action = "新建", description = "create group")
    @Override
    public ResponseEntity<?> create(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "更新群组", description = "更新群组信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = "群组", action = "更新", description = "update group")
    @Override
    public ResponseEntity<?> update(@RequestBody GroupRequest request) {

        GroupResponse group = groupRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "更新群组名称", description = "更新群组的名称")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    // update/name
    @PostMapping("/update/name")
    public ResponseEntity<?> updateGroupName(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.updateGroupName(request);
        
        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "更新群组置顶提示", description = "更新群组的置顶提示信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    // update/topTip
    @PostMapping("/update/topTip")
    public ResponseEntity<?> updateGroupTopTip(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.updateGroupTopTip(request);
        
        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "邀请成员", description = "邀请成员加入群组")
    @ApiResponse(responseCode = "200", description = "邀请成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = "群组", action = "invite", description = "invite members to group")
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMembers(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.invite(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }
    
    @Operation(summary = "加入群组", description = "加入指定的群组")
    @ApiResponse(responseCode = "200", description = "加入成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = "群组", action = "join", description = "join group")
    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.join(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "移除成员", description = "从群组中移除成员")
    @ApiResponse(responseCode = "200", description = "移除成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = "群组", action = "remove", description = "remove members from group")
    @PostMapping("/remove")
    public ResponseEntity<?> removeMembers(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.remove(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "退出群组", description = "退出指定的群组")
    @ApiResponse(responseCode = "200", description = "退出成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = GroupResponse.class)))
    @ActionAnnotation(title = "群组", action = "leave", description = "leave group")
    @PostMapping("/leave")
    public ResponseEntity<?> leaveGroup(@RequestBody GroupRequest request) {
        
        GroupResponse group = groupRestService.leave(request);

        return ResponseEntity.ok(JsonResult.success(group));
    }

    @Operation(summary = "解散群组", description = "解散指定的群组")
    @ApiResponse(responseCode = "200", description = "解散成功")
    @ActionAnnotation(title = "群组", action = "dismiss", description = "dismiss group")
    @PostMapping("/dismiss")
    public ResponseEntity<?> dismissGroup(@RequestBody GroupRequest request) {

        groupRestService.dismiss(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "删除群组", description = "删除指定的群组")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "群组", action = "删除", description = "delete group")
    @Override
    public ResponseEntity<?> delete(@RequestBody GroupRequest request) {
        
        groupRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出群组", description = "导出群组数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @ActionAnnotation(title = "群组", action = "导出", description = "export group")
    @Override
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
