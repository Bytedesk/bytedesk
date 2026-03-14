/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.meet.room;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/room")
@AllArgsConstructor
@Tag(name = "Room Management", description = "Room management APIs for organizing and categorizing content with rooms")
@Description("Room Management Controller - Content roomging and categorization APIs")
public class RoomRestController extends BaseRestController<RoomRequest, RoomRestService> {

    private final RoomRestService roomRestService;

    @ActionAnnotation(title = "Room", action = "组织查询", description = "query room by org")
    @Operation(summary = "Query Rooms by Organization", description = "Retrieve rooms for the current organization")
    @PreAuthorize(RoomPermissions.HAS_ROOM_READ)
    @Override
    public ResponseEntity<?> queryByOrg(RoomRequest request) {
        
        Page<RoomResponse> rooms = roomRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(rooms));
    }

    @ActionAnnotation(title = "Room", action = "用户查询", description = "query room by user")
    @Operation(summary = "Query Rooms by User", description = "Retrieve rooms for the current user")
    @PreAuthorize(RoomPermissions.HAS_ROOM_READ)
    @Override
    public ResponseEntity<?> queryByUser(RoomRequest request) {
        
        Page<RoomResponse> rooms = roomRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(rooms));
    }

    @ActionAnnotation(title = "Room", action = "查询详情", description = "query room by uid")
    @Operation(summary = "Query Room by UID", description = "Retrieve a specific room by its unique identifier")
    @PreAuthorize(RoomPermissions.HAS_ROOM_READ)
    @Override
    public ResponseEntity<?> queryByUid(RoomRequest request) {
        
        RoomResponse room = roomRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(room));
    }

    @ActionAnnotation(title = "Room", action = "新建", description = "create room")
    @Operation(summary = "Create Room", description = "Create a new room")
    @Override
    @PreAuthorize(RoomPermissions.HAS_ROOM_CREATE)
    public ResponseEntity<?> create(RoomRequest request) {
        
        RoomResponse room = roomRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(room));
    }

    @ActionAnnotation(title = "Room", action = "更新", description = "update room")
    @Operation(summary = "Update Room", description = "Update an existing room")
    @Override
    @PreAuthorize(RoomPermissions.HAS_ROOM_UPDATE)
    public ResponseEntity<?> update(RoomRequest request) {
        
        RoomResponse room = roomRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(room));
    }

    @ActionAnnotation(title = "Room", action = "删除", description = "delete room")
    @Operation(summary = "Delete Room", description = "Delete a room")
    @Override
    @PreAuthorize(RoomPermissions.HAS_ROOM_DELETE)
    public ResponseEntity<?> delete(RoomRequest request) {
        
        roomRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Room", action = "导出", description = "export room")
    @Operation(summary = "Export Rooms", description = "Export rooms to Excel format")
    @Override
    @PreAuthorize(RoomPermissions.HAS_ROOM_EXPORT)
    @GetMapping("/export")
    public Object export(RoomRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            roomRestService,
            RoomExcel.class,
            "Room",
            "room"
        );
    }

    
    
}