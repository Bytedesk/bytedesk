/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 17:30:21
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
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
@Description("Queue Management Controller - Customer service queue and routing management APIs")
public class QueueRestController extends BaseRestController<QueueRequest, QueueRestService> {

    private final QueueRestService queueRestService;

    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = "队列管理", action = "查询组织队列", description = "queryByOrg queue")
    @Operation(summary = "查询组织下的队列", description = "根据组织ID查询队列列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(QueueRequest request) {
        
        Page<QueueResponse> queues = queueRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(queues));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = "队列管理", action = "查询用户队列", description = "queryByUser queue")
    @Operation(summary = "查询用户下的队列", description = "根据用户ID查询队列列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(QueueRequest request) {
        
        Page<QueueResponse> queues = queueRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(queues));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = "队列管理", action = "查询队列详情", description = "queryByUid queue")
    @Operation(summary = "查询指定队列", description = "根据UID查询队列详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(QueueRequest request) {
        
        QueueResponse queue = queueRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    // 查询排队中会话 query/queuing
    @GetMapping("/query/queuing")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = "队列管理", action = "查询排队会话", description = "query queuing threads")
    @Operation(summary = "查询排队中会话", description = "查询当前排队中的会话")
    public ResponseEntity<?> queryQueuing(ThreadRequest request) {

        Page<ThreadResponse> threadPage = queueRestService.queryQueuing(request);
        
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    // 查询待回复会话 query/unreplied
    @GetMapping("/query/unreplied")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = "队列管理", action = "查询待回复会话", description = "query unreplied threads")
    @Operation(summary = "查询待回复会话", description = "查询当前客服待回复的会话（含未回复访客消息），并返回等待回复时长")
    public ResponseEntity<?> queryUnreplied(ThreadRequest request) {

        Page<ThreadResponse> threadPage = queueRestService.queryUnreplied(request);

        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    // 获取客服完整排队人数统计
    @GetMapping("/agent/queuing/count")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = "队列管理", action = "获取排队统计", description = "get agent queuing count")
    @Operation(summary = "获取客服排队统计", description = "获取客服的完整排队人数统计，包括一对一会话和工作组未分配的排队会话")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<?> getAgentQueuingCount(@RequestParam String agentUid) {
        
        var queuingCount = queueRestService.getAgentTotalQueuingCount(agentUid);
        
        return ResponseEntity.ok(JsonResult.success(queuingCount));
    }

    // 获取客服完整队列统计信息
    @GetMapping("/agent/stats")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = "队列管理", action = "获取队列统计", description = "get agent queue stats")
    @Operation(summary = "获取客服队列统计", description = "获取客服的完整队列统计信息，包括今日服务人数、排队人数、接待人数、留言数、转人工数等")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentQueueStatsResponse.class)))
    public ResponseEntity<?> getAgentQueueStats(@RequestParam String agentUid) {
        
        AgentQueueStatsResponse stats = queueRestService.getAgentQueueStats(agentUid);
        
        return ResponseEntity.ok(JsonResult.success(stats));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_CREATE)
    @ActionAnnotation(title = "队列管理", action = "创建队列", description = "create queue")
    @Operation(summary = "创建队列", description = "创建新的队列")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> create(QueueRequest request) {
        
        QueueResponse queue = queueRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_UPDATE)
    @ActionAnnotation(title = "队列管理", action = "更新队列", description = "update queue")
    @Operation(summary = "更新队列", description = "更新队列信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @Override
    public ResponseEntity<?> update(QueueRequest request) {
        
        QueueResponse queue = queueRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_DELETE)
    @ActionAnnotation(title = "队列管理", action = "删除队列", description = "delete queue")
    @Operation(summary = "删除队列", description = "删除指定的队列")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(QueueRequest request) {
        
        queueRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/export")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_EXPORT)
    @ActionAnnotation(title = "队列管理", action = "导出队列", description = "export queue")
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
