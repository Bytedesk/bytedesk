/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 10:52:39
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@Slf4j
@Tag(name = "Queue Member Management", description = "Queue member management APIs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/queue/member")
public class QueueMemberRestController extends BaseRestController<QueueMemberRequest, QueueMemberRestService> {

    private final QueueMemberRestService queueMemberRestService;

    @PreAuthorize(QueueMemberPermissions.HAS_QUEUE_MEMBER_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE_MEMBER, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg queue member")
    @Operation(summary = "Query Queue Members by Organization", description = "Retrieve queue member list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(QueueMemberPermissions.HAS_QUEUE_MEMBER_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE_MEMBER, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser queue member")
    @Operation(summary = "Query Queue Members by User", description = "Retrieve queue member list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(QueueMemberPermissions.HAS_QUEUE_MEMBER_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE_MEMBER, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid queue member")
    @Operation(summary = "Query Queue Member by UID", description = "Retrieve queue member details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(QueueMemberRequest request) {
        QueueMemberResponse response = queueMemberRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }
    

    @PreAuthorize(QueueMemberPermissions.HAS_QUEUE_MEMBER_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE_MEMBER, action = I18Consts.I18N_ACTION_CREATE, description = "create queue member")
    @Operation(summary = "Create Queue Member", description = "Create a new queue member")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(QueueMemberPermissions.HAS_QUEUE_MEMBER_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE_MEMBER, action = I18Consts.I18N_ACTION_UPDATE, description = "update queue member")
    @Operation(summary = "Update Queue Member", description = "Update queue member information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueMemberResponse.class)))
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(QueueMemberPermissions.HAS_QUEUE_MEMBER_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE_MEMBER, action = I18Consts.I18N_ACTION_DELETE, description = "delete queue member")
    @Operation(summary = "Delete Queue Member", description = "Delete the specified queue member")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody QueueMemberRequest request) {
        
        queueMemberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize(QueueMemberPermissions.HAS_QUEUE_MEMBER_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE_MEMBER, action = I18Consts.I18N_ACTION_EXPORT, description = "export queue member")
    @Operation(summary = "Export Queue Members", description = "Export queue member data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @GetMapping("/export")
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
