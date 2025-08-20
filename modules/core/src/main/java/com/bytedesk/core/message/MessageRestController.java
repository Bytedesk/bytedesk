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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@Tag(name = "消息管理", description = "消息管理相关接口，包括查询、创建、更新、删除等操作")
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
    @Operation(summary = "根据组织查询消息", description = "返回当前组织的消息列表")
    @Override
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
    @Operation(summary = "根据用户查询消息", description = "返回当前用户的消息列表")
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
    @Operation(summary = "根据UID查询消息", description = "通过唯一标识符查询消息")
    @Override
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
    @Operation(summary = "查询未读消息", description = "查询未读消息（已迁移到企业版）")
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
    @Operation(summary = "根据主题查询消息", description = "根据主题查询相关消息")
    @GetMapping("/thread/topic")
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
    @Operation(summary = "根据会话UID查询消息", description = "通过会话唯一标识符查询相关消息")
    @GetMapping("/thread/uid")
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
    @Operation(summary = "创建消息", description = "创建新的消息记录")
    @Override
    public ResponseEntity<?> create(MessageRequest request) {
        
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
    @Operation(summary = "更新消息", description = "更新已存在的消息记录")
    @Override
    public ResponseEntity<?> update(MessageRequest request) {
        
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
    @Operation(summary = "删除消息", description = "删除指定的消息记录")
    @Override
    public ResponseEntity<?> delete(MessageRequest request) {
        
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
    @Operation(summary = "发送离线消息", description = "当客户端长连接断开时，通过REST接口发送消息")
    @PostMapping("/rest/send")
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
    @Operation(summary = "导出消息数据", description = "将消息数据导出为Excel格式")
    @ActionAnnotation(title = "消息", action = "导出", description = "export message")
    @GetMapping("/export")
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
    @Operation(summary = "标记消息为已读", description = "将指定消息的状态更新为已读（已迁移到企业版）")
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
    @Operation(summary = "批量标记会话消息为已读", description = "将会话中所有未读消息的状态更新为已读（已迁移到企业版）")
    @PostMapping("/thread/{threadUid}/read")
    @Deprecated
    public ResponseEntity<?> markThreadAsRead(@PathVariable String threadUid) {
        // 此功能已迁移到企业版
        return ResponseEntity.status(410).body(JsonResult.error("此功能已迁移到企业版，请使用 /api/v1/vip/message/thread/" + threadUid + "/read"));
    }

    

    
}
