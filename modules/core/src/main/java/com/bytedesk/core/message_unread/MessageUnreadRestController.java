/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:38:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 17:58:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/message/unread")
@AllArgsConstructor
@Tag(name = "Unread Message Management", description = "Unread message management APIs")
public class MessageUnreadRestController extends BaseRestController<MessageUnreadRequest, MessageUnreadRestService> {

    private final MessageUnreadRestService messageUnreadRestService;

    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query message unread")
    @Override
    @Operation(summary = "Query Unread Messages by Organization")
    public ResponseEntity<?> queryByOrg(MessageUnreadRequest request) {
        
        Page<MessageUnreadResponse> messagePage = messageUnreadRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messagePage));
    }

    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query message unread")
    @Override
    @Operation(summary = "Query Unread Messages by User")
    public ResponseEntity<?> queryByUser(MessageUnreadRequest request) {

        Page<MessageUnreadResponse> messagePage = messageUnreadRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messagePage));
    }

    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query message unread")
    @Override
    @Operation(summary = "Query Unread Message by User ID")
    public ResponseEntity<?> queryByUid(MessageUnreadRequest request) {
        
        MessageUnreadResponse messageUnread = messageUnreadRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success("get unread message success", messageUnread));
    }

    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_CREATE, description = "create message unread")
    @Override
    @Operation(summary = "Create Unread Message")
    public ResponseEntity<?> create(MessageUnreadRequest request) {
        
        MessageUnreadResponse messageUnread = messageUnreadRestService.create(request);

        return ResponseEntity.ok(JsonResult.success("create unread message success", messageUnread));
    }

    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_UPDATE, description = "update message unread")
    @Override
    @Operation(summary = "Update Unread Message")
    public ResponseEntity<?> update(MessageUnreadRequest request) {
        
        MessageUnreadResponse messageUnread = messageUnreadRestService.update(request);

        return ResponseEntity.ok(JsonResult.success("update unread message success", messageUnread));
    }

    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_DELETE, description = "delete message unread")
    @Override
    @Operation(summary = "Delete Unread Message")
    public ResponseEntity<?> delete(MessageUnreadRequest request) {
        
        messageUnreadRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete unread message success"));
    }

    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_EXPORT, description = "export message unread")
    @Override
    @Operation(summary = "Export Unread Messages")
    public Object export(MessageUnreadRequest request, HttpServletResponse response) {
        //
        // messageUnreadService.export(request, response);

        return JsonResult.success("export unread messages success");
    }

    // 获取未读消息数
    @PreAuthorize(MessageUnreadPermissions.HAS_MESSAGE_UNREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_QUERY_UNREAD_COUNT, description = "query unread count of message unread")
    @GetMapping("/count")
    @Operation(summary = "Get Total Unread Message Count")
    public ResponseEntity<?> getMessageUnreadCount(MessageUnreadRequest request) {

        long count = messageUnreadRestService.getUnreadCount(request);

        return ResponseEntity.ok(JsonResult.success("get unread messages count success", count));
    }

    // 清空当前用户所有未读消息
    @PreAuthorize(MessageUnreadPermissions.HAS_MESSAGE_UNREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_UNREAD, action = I18Consts.I18N_ACTION_UPDATE_UNREAD, description = "update unread count of message unread")
    @PostMapping("/clear")
    @Operation(summary = "Clear All Unread Messages for Current User")
    public ResponseEntity<?> clearMessageUnread(@RequestBody MessageUnreadRequest request) {
        // 
        messageUnreadRestService.clearUnreadMessages(request);
        // 看下是否清空了
        long count = messageUnreadRestService.getUnreadCount(request);

        return ResponseEntity.ok(JsonResult.success("clear unread messages count success", count));
    }
    
}
