/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-25 11:09:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "队列管理", description = "队列管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/queue")
public class QueueRestController extends BaseRestController<QueueRequest> {

    private final QueueRestService queueRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "查询组织下的队列", description = "根据组织ID查询队列列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(QueueRequest request) {
        
        Page<QueueResponse> queues = queueRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(queues));
    }

    @Operation(summary = "查询用户下的队列", description = "根据用户ID查询队列列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(QueueRequest request) {
        
        Page<QueueResponse> queues = queueRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(queues));
    }

    @Operation(summary = "查询指定队列", description = "根据UID查询队列详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(QueueRequest request) {
        
        QueueResponse queue = queueRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    @Operation(summary = "创建队列", description = "创建新的队列")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> create(QueueRequest request) {
        
        QueueResponse queue = queueRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    @Operation(summary = "更新队列", description = "更新队列信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> update(QueueRequest request) {
        
        QueueResponse queue = queueRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    @Operation(summary = "删除队列", description = "删除指定的队列")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(QueueRequest request) {
        
        queueRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出队列", description = "导出队列数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(QueueRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            queueRestService,
            QueueExcel.class,
            "队列",
            "queue"
        );
    }

    

    


}
