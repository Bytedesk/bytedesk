/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 13:51:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

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
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/message/leave")
@AllArgsConstructor
@Tag(name = "Leave Message Management", description = "Leave message management APIs")
public class MessageLeaveRestController extends BaseRestController<MessageLeaveRequest, MessageLeaveRestService> {

    private final MessageLeaveRestService messageLeaveRestService;

    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg leave message")
    @Override
    @Operation(summary = "Query Leave Messages")
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MessageLeaveRequest request) {

        Page<MessageLeaveResponse> page = messageLeaveRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser leave message")
    @Override
    @Operation(summary = "Query User Leave Messages")
    @GetMapping({ "/query", "/query/user" })
    public ResponseEntity<?> queryByUser(MessageLeaveRequest request) {
        
        Page<MessageLeaveResponse> page = messageLeaveRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid leave message")
    @Override
    @Operation(summary = "Query Leave Message Details")
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(MessageLeaveRequest request) {
        
        MessageLeaveResponse response = messageLeaveRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @GetMapping("/query/threads")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_QUERY_RELATED_THREAD, description = "query threads by leave message")
    @Operation(summary = "Query Threads Related to Leave Messages")
    public ResponseEntity<?> queryThreadsByLeaveMessage(MessageLeaveRequest request) {
        
        Page<ThreadResponse> page = messageLeaveRestService.queryThreadsByLeaveMessage(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/count/pending")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_COUNT_PENDING, description = "count pending leave messages")
    @Operation(summary = "Count Pending Leave Messages in Current Organization")
    public ResponseEntity<?> countPendingByOrg(MessageLeaveRequest request) {
        
        long count = messageLeaveRestService.countPendingByOrg(request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success(count));
    }

    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_CREATE, description = "create leave message")
    @Override
    @Operation(summary = "Create Leave Message")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_UPDATE, description = "update leave message")
    @Override
    @Operation(summary = "Update Leave Message")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/reply")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_REPLY, description = "reply leave message")
    @Operation(summary = "Reply to Leave Message")
    public ResponseEntity<?> reply(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveRestService.reply(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/status/update")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_UPDATE_MESSAGE_LEAVE_STATUS, description = "update leave message status")
    @Operation(summary = "Update Leave Message Status")
    public ResponseEntity<?> updateStatus(@RequestBody MessageLeaveRequest request) {
        
        MessageLeaveResponse response = messageLeaveRestService.updateStatus(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/read")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_MARK_AS_READ, description = "mark leave message as read")
    @Operation(summary = "Mark Leave Message as Read")
    public ResponseEntity<?> markAsRead(@RequestBody MessageLeaveRequest request) {
        
        MessageLeaveResponse response = messageLeaveRestService.markAsRead(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/transfer")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_TRANSFER, description = "transfer leave message")
    @Operation(summary = "Transfer Leave Message")
    public ResponseEntity<?> transfer(@RequestBody MessageLeaveRequest request) {
        
        MessageLeaveResponse response = messageLeaveRestService.transfer(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/close")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_CLOSE, description = "close leave message")
    @Operation(summary = "Close Leave Message")
    public ResponseEntity<?> close(@RequestBody MessageLeaveRequest request) {
        
        MessageLeaveResponse response = messageLeaveRestService.close(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/spam")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_MARK_AS_SPAM, description = "mark leave message as spam")
    @Operation(summary = "Mark Leave Message as Spam")
    public ResponseEntity<?> markAsSpam(@RequestBody MessageLeaveRequest request) {
        
        MessageLeaveResponse response = messageLeaveRestService.markAsSpam(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_DELETE, description = "delete leave message")
    @Override
    @Operation(summary = "Delete Leave Message")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MessageLeaveRequest request) {
        
        messageLeaveRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/export")
    @PreAuthorize(MessageLeavePermissions.HAS_MESSAGE_LEAVE_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_LEAVE, action = I18Consts.I18N_ACTION_EXPORT, description = "export leave message")
    @Override
    @Operation(summary = "Export Leave Messages")
    public Object export(MessageLeaveRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            messageLeaveRestService,
            MessageLeaveExcel.class,
            "留言消息",
            "message-leave"
        );
    }

}
