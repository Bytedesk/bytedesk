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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
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

@Tag(name = "Queue Management", description = "Queue management APIs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/queue")
@Description("Queue Management Controller - Customer service queue and routing management APIs")
public class QueueRestController extends BaseRestController<QueueRequest, QueueRestService> {

    private final QueueRestService queueRestService;

    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg queue")
    @Operation(summary = "Query Queues by Organization", description = "Retrieve queue list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(QueueRequest request) {
        
        Page<QueueResponse> queues = queueRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(queues));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser queue")
    @Operation(summary = "Query Queues by User", description = "Retrieve queue list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(QueueRequest request) {
        
        Page<QueueResponse> queues = queueRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(queues));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid queue")
    @Operation(summary = "Query Queue by UID", description = "Retrieve queue details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(QueueRequest request) {
        
        QueueResponse queue = queueRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    // 查询排队中会话 query/queuing
    @GetMapping("/query/queuing")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_QUERY_QUEUING_THREAD, description = "query queuing threads")
    @Operation(summary = "Query Queuing Threads", description = "Retrieve threads that are currently in queue")
    public ResponseEntity<?> queryQueuing(ThreadRequest request) {

        Page<ThreadResponse> threadPage = queueRestService.queryQueuing(request);
        
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    // 查询待回复会话 query/unreplied
    @GetMapping("/query/unreplied")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_QUERY_UNREPLIED_THREAD, description = "query unreplied threads")
    @Operation(summary = "Query Unreplied Threads", description = "Retrieve threads awaiting agent replies, including unreplied visitor messages, and return the waiting duration")
    public ResponseEntity<?> queryUnreplied(ThreadRequest request) {

        Page<ThreadResponse> threadPage = queueRestService.queryUnreplied(request);

        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    @PostMapping("/update/unreplied/replied")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_UPDATE, description = "mark unreplied thread as replied")
    @Operation(summary = "Mark Unreplied Thread As Replied", description = "Mark all unreplied visitor messages in the target thread/topic as replied")
    public ResponseEntity<?> markUnrepliedThreadAsReplied(@RequestBody ThreadRequest request) {

        int updatedCount = queueRestService.markUnrepliedThreadAsReplied(request);

        return ResponseEntity.ok(JsonResult.success(updatedCount));
    }

    @PostMapping("/update/unreplied/replied/all")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_UPDATE, description = "mark all unreplied threads as replied")
    @Operation(summary = "Mark All Unreplied Threads As Replied", description = "Mark all current agent unreplied visitor messages as replied")
    public ResponseEntity<?> markAllUnrepliedThreadsAsReplied(@RequestBody(required = false) ThreadRequest request) {

        ThreadRequest actualRequest = request == null ? ThreadRequest.builder().build() : request;
        int updatedCount = queueRestService.markAllUnrepliedThreadsAsReplied(actualRequest);

        return ResponseEntity.ok(JsonResult.success(updatedCount));
    }

    // 获取客服完整排队人数统计
    @GetMapping("/agent/queuing/count")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_GET_AGENT_QUEUING_COUNT, description = "get agent queuing count")
    @Operation(summary = "Get Agent Queuing Statistics", description = "Retrieve the full queuing count for the agent, including one-to-one threads and unassigned queued threads in workgroups")
    @ApiResponse(responseCode = "200", description = "Query successful")
    public ResponseEntity<?> getAgentQueuingCount(@RequestParam String agentUid) {
        
        var queuingCount = queueRestService.getAgentTotalQueuingCount(agentUid);
        
        return ResponseEntity.ok(JsonResult.success(queuingCount));
    }

    // 获取客服完整队列统计信息
    @GetMapping("/agent/stats")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_GET_AGENT_QUEUE_STATS, description = "get agent queue stats")
    @Operation(summary = "Get Agent Queue Statistics", description = "Retrieve the full queue statistics for the agent, including today's served count, queue count, reception count, leave-message count, transfer-to-agent count, and more")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueAgentStatsResponse.class)))
    public ResponseEntity<?> getAgentQueueStats(@RequestParam String agentUid) {
        
        QueueAgentStatsResponse stats = queueRestService.getAgentQueueStats(agentUid);
        
        return ResponseEntity.ok(JsonResult.success(stats));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_CREATE, description = "create queue")
    @Operation(summary = "Create Queue", description = "Create a new queue")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody QueueRequest request) {
        
        QueueResponse queue = queueRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_UPDATE, description = "update queue")
    @Operation(summary = "Update Queue", description = "Update queue information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = QueueResponse.class)))
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody QueueRequest request) {
        
        QueueResponse queue = queueRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(queue));
    }

    @PreAuthorize(QueuePermissions.HAS_QUEUE_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_DELETE, description = "delete queue")
    @Operation(summary = "Delete Queue", description = "Delete the specified queue")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody QueueRequest request) {
        
        queueRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/export")
    @PreAuthorize(QueuePermissions.HAS_QUEUE_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_QUEUE, action = I18Consts.I18N_ACTION_EXPORT, description = "export queue")
    @Operation(summary = "Export Queues", description = "Export queue data")
    @ApiResponse(responseCode = "200", description = "Export successful")
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
