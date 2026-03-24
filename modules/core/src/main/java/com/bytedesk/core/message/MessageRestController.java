/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 17:06:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息管理接口控制器
 * 
 * @author Jackning (270580156@qq.com)
 * @since 2024-01-29
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message")
@Tag(name = "Message Management", description = "Message management APIs, including query, create, update, and delete operations")
@Description("Message Management Controller - Message management APIs for CRUD operations")
public class MessageRestController extends BaseRestController<MessageRequest, MessageRestService> {

    private final MessageRestService messageRestService;

    private final IMessageSendService messageSendService;

    /**
     * 根据组织查询消息
     * 
     * @param request 查询请求
     * @return 分页消息列表
     */
    @Operation(summary = "Query Messages by Organization", description = "Return the message list for the current organization")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MessageRequest request) {

        Page<MessageResponse> messagePage = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(messagePage));
    }

    /**
     * 根据用户查询消息
     * 
     * @param request 查询请求
     * @return 分页消息列表
     */
    @Operation(summary = "Query Messages by User", description = "Return the message list for the current user")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByUser(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 根据UID查询消息
     * 
     * @param request 查询请求
     * @return 消息详情
     */
    @Operation(summary = "Query Message by UID", description = "Query the message by unique identifier")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(MessageRequest request) {
        
        MessageResponse response = messageRestService.queryByUid(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 客服端-根据会话topic-查询未读消息
     * 
     * @param request 查询请求
     * @return 未读消息列表
     * @deprecated 此功能已迁移到企业版，请使用 /api/v1/vip/message/unread
     */
    @Operation(summary = "Query Unread Messages", description = "Query unread messages, migrated to the enterprise edition")
    @GetMapping("/unread")
    @Deprecated
    public ResponseEntity<?> queryUnread(MessageRequest request) {
        // 此功能已迁移到企业版
        return ResponseEntity.status(410).body(JsonResult.error("此功能已迁移到企业版，请使用 /api/v1/vip/message/unread"));
    }

    /**
     * 根据主题查询消息
     * 
     * @param request 查询请求
     * @return 分页消息列表
     */
    @Operation(summary = "Query Messages by Topic", description = "Query related messages by topic")
    @GetMapping("/thread/topic")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_READ)
    public ResponseEntity<?> queryByThreadTopic(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 根据会话UID查询消息
     * 
     * @param request 查询请求
     * @return 分页消息列表
     */
    @Operation(summary = "Query Messages by Thread UID", description = "Query related messages by thread unique identifier")
    @GetMapping("/thread/uid")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_READ)
    public ResponseEntity<?> queryByThreadUid(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 创建消息
     * 
     * @param request 创建请求
     * @return 创建的消息
     */
    @Operation(summary = "Create Message", description = "Create a new message record")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_CREATE)
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MessageRequest request) {
        
        MessageResponse response = messageRestService.create(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 更新消息
     * 
     * @param request 更新请求
     * @return 更新后的消息
     */
    @Operation(summary = "Update Message", description = "Update an existing message record")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_UPDATE)
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MessageRequest request) {
        
        MessageResponse response = messageRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 删除消息
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    @Operation(summary = "Delete Message", description = "Delete the specified message record")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_DELETE)
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MessageRequest request) {
        
        messageRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * 当客户端长连接断开时，启用此rest接口发送消息
     * send offline message
     *
     * @param map 包含JSON消息的Map
     * @return 发送结果
     */
    @Operation(summary = "Send Offline Message", description = "Send a message through the REST API when the client long connection is disconnected")
    @PostMapping("/rest/send")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_CREATE)
    public ResponseEntity<?> sendRestMessage(@RequestBody Map<String, String> map) {
        String json = (String) map.get("json");
        log.debug("json {}", json);
        messageSendService.sendJsonMessage(json);
        //
        return ResponseEntity.ok(JsonResult.success(json));
    }

    /**
     * 导出消息列表
     * 
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @Operation(summary = "Export Message Data", description = "Export message data to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE, action = I18Consts.I18N_ACTION_EXPORT, description = "export message")
    @GetMapping("/export")
    // @PreAuthorize(MessagePermissions.HAS_MESSAGE_EXPORT)
    public Object export(MessageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            messageRestService,
            MessageExcel.class,
            "消息",
            "Message"
        );
    }

    /**
     * 标记消息为已读
     * 
     * @param messageUid 消息UID
     * @return 更新后的消息
     * @deprecated 此功能已迁移到企业版，请使用 /api/v1/vip/message/{messageUid}/read
     */
    @Operation(summary = "Mark Message as Read", description = "Update the specified message status to read, migrated to the enterprise edition")
    @PostMapping("/{messageUid}/read")
    @Deprecated
    public ResponseEntity<?> markAsRead(@PathVariable String messageUid) {
        // 此功能已迁移到企业版
        return ResponseEntity.status(410).body(JsonResult.error("此功能已迁移到企业版，请使用 /api/v1/vip/message/" + messageUid + "/read"));
    }

    /**
     * 批量标记会话中所有消息为已读
     * 
     * @param threadUid 会话UID
     * @return 更新的消息数量
     * @deprecated 此功能已迁移到企业版，请使用 /api/v1/vip/message/thread/{threadUid}/read
     */
    @Operation(summary = "Batch Mark Thread Messages as Read", description = "Update all unread messages in the thread to read, migrated to the enterprise edition")
    @PostMapping("/thread/{threadUid}/read")
    @Deprecated
    public ResponseEntity<?> markThreadAsRead(@PathVariable String threadUid) {
        // 此功能已迁移到企业版
        return ResponseEntity.status(410).body(JsonResult.error("此功能已迁移到企业版，请使用 /api/v1/vip/message/thread/" + threadUid + "/read"));
    }

    

    
}
