/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 21:02:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "队列成员管理", description = "队列成员管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/queue/member")
public class QueueMemberRestController extends BaseRestController<QueueMemberRequest> {

    private final QueueMemberRestService queueMemberRestService;

    @Operation(summary = "查询组织下的队列成员", description = "根据组织ID查询队列成员列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的队列成员", description = "根据用户ID查询队列成员列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询指定队列成员", description = "根据UID查询队列成员详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(QueueMemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    

    @Operation(summary = "创建队列成员", description = "创建新的队列成员")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @Override
    public ResponseEntity<?> create(QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "更新队列成员", description = "更新队列成员信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @Override
    public ResponseEntity<?> update(QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "删除队列成员", description = "删除指定的队列成员")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(QueueMemberRequest request) {
        
        queueMemberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出队列成员", description = "导出队列成员数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(QueueMemberRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            queueMemberRestService,
            QueueMemberExcel.class,
            "监控成员",
            "queue-member"
        );
    }
}
