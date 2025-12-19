/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:06:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * 会话管理接口
 * 
 * @author Jackning
 * @since 2024-01-29
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/thread")
@Tag(name = "会话管理", description = "会话管理相关接口，包括查询、创建、更新、删除、置顶、标星等操作")
public class ThreadRestController extends BaseRestController<ThreadRequest, ThreadRestService> {

    private final ThreadRestService threadRestService;

    /**
     * 根据组织查询会话
     * 
     * @param request 查询请求
     * @return 分页会话列表
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "查询组织会话", description = "queryByOrg thread")
    @Operation(summary = "根据组织查询会话", description = "返回当前组织的会话列表")
    @Override
    public ResponseEntity<?> queryByOrg(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    /**
     * 根据用户查询会话
     * 
     * @param request 查询请求
     * @return 分页会话列表
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "查询用户会话", description = "queryByUser thread")
    @Operation(summary = "根据用户查询会话", description = "返回当前用户的会话列表") 
    @Override
    public ResponseEntity<?> queryByUser(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadRestService.query(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    /**
     * 查询邀请会话
     * 
     * @param request 查询请求
     * @return 分页邀请会话列表
     */
    @GetMapping("/query/invite")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "查询邀请会话", description = "query invite threads")
    @Operation(summary = "查询邀请会话", description = "查询邀请相关的会话")
    public ResponseEntity<?> queryByThreadInvite(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    /**
     * 根据主题查询会话
     * 
     * @param request 查询请求
     * @return 会话信息
     */
    @GetMapping("/query/topic")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "根据主题查询", description = "query thread by topic")
    @Operation(summary = "根据主题查询会话", description = "通过主题查找相关会话")
    public ResponseEntity<?> queryByThreadTopic(ThreadRequest request) {
  
        Page<ThreadResponse> threadResponse = threadRestService.queryByTopic(request);
  
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 主要用于查询 某个成员 的 某个群组会话
     * @param request
     * @return
     */
    @GetMapping("/query/topic/owner")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "根据主题和用户查询", description = "query thread by topic and owner")
    @Operation(summary = "根据主题和用户查询会话", description = "通过主题和用户查找相关会话")
    public ResponseEntity<?> queryByTopicAndOwner(ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.queryByTopicAndOwner(request);
 
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 根据UID查询会话
     * 
     * @param request 查询请求
     * @return 会话信息
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "查询会话详情", description = "queryByUid thread")
    @Operation(summary = "根据UID查询会话", description = "通过唯一标识符查询会话")
    @Override
    public ResponseEntity<?> queryByUid(ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 创建会话
     * 
     * @param request 创建会话请求
     * @return 创建的会话
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_CREATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "创建会话", description = "create thread")
    @Operation(summary = "创建会话", description = "创建新的会话")
    @Override
    public ResponseEntity<?> create(@RequestBody ThreadRequest request) {
        //
        ThreadResponse threadResponse = threadRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话
     * 
     * @param request 更新会话请求
     * @return 更新后的会话
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新会话", description = "update thread")
    @Operation(summary = "更新会话", description = "更新已存在的会话信息")
    @Override
    public ResponseEntity<?> update(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话置顶状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/top")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新置顶状态", description = "update thread top")
    @Operation(summary = "更新会话置顶状态", description = "设置或取消会话置顶")
    public ResponseEntity<?> updateTop(@RequestBody ThreadRequest request) {

        ThreadResponse thread = threadRestService.updateTop(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    /**
     * 更新会话标星状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/star")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新标星状态", description = "update thread star")
    @Operation(summary = "更新会话标星状态", description = "设置或取消会话标星")
    public ResponseEntity<?> updateStar(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateStar(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话静音状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/mute")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新静音状态", description = "update thread mute")
    @Operation(summary = "更新会话静音状态", description = "设置或取消会话静音")
    public ResponseEntity<?> updateMute(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateMute(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @PostMapping("/update/hide")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新隐藏状态", description = "update thread hide")
    @Operation(summary = "更新会话隐藏状态", description = "设置或取消会话隐藏")
    public ResponseEntity<?> updateHide(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateHide(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @PostMapping("/update/fold")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新折叠状态", description = "update thread fold")
    @Operation(summary = "更新会话折叠状态", description = "设置或取消会话折叠")
    public ResponseEntity<?> updateFold(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateFold(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话用户信息
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/user")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新用户信息", description = "update thread user")
    @Operation(summary = "更新会话用户信息", description = "更新会话关联的用户信息")
    public ResponseEntity<?> updateUser(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateUser(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话标签列表
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/tagList")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新标签列表", description = "update thread tagList")
    @Operation(summary = "更新会话标签列表", description = "更新会话的标签信息")
    public ResponseEntity<?> updateTagList(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateTagList(request);
        
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话未读状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/unread")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新未读状态", description = "update thread unread")
    @Operation(summary = "更新会话未读状态", description = "标记会话为已读或未读")
    public ResponseEntity<?> updateUnread(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateUnread(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 查询用户所有客服会话
     * 
     * @return 用户所有会话列表
     */
    @GetMapping("/query/by/user/topics")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "查询用户客服会话", description = "query threads by user topics")
    @Operation(summary = "查询用户所有客服会话", description = "查询用户所有客服会话")
    public ResponseEntity<?> queryByUserTopics(ThreadRequest request) {
        
        Page<ThreadResponse> responses = threadRestService.queryThreadsByUserTopics(request);

        return ResponseEntity.ok(JsonResult.success(responses));
    }

    @PostMapping("/update/note")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "更新备注", description = "update thread note")
    @Operation(summary = "更新会话备注", description = "更新会话的备注信息")
    public ResponseEntity<?> updateNote(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateNote(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 关闭会话
     * 
     * @param request 关闭请求
     * @return 关闭后的会话
     */
    @PostMapping("/close")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "关闭会话", description = "close thread")
    @Operation(summary = "关闭会话", description = "关闭指定会话")
    public ResponseEntity<?> close(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.closeByUid(request);
        // 
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @PostMapping("/close/topic")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "根据主题关闭会话", description = "close thread by topic")
    @Operation(summary = "关闭会话", description = "关闭指定主题的会话")
    public ResponseEntity<?> closeByTopic(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.closeByTopic(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }
    
    /**
     * 删除会话
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_DELETE_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "删除会话", description = "delete thread")
    @Operation(summary = "删除会话", description = "删除指定会话")
    @Override
    public ResponseEntity<?> delete(@RequestBody ThreadRequest request) {
        
        threadRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete thread success"));
    }

    /**
     * 导出会话列表
     * 
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @GetMapping("/export")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_EXPORT_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "导出会话", description = "export thread")
    @Operation(summary = "导出会话列表", description = "将会话数据导出为Excel格式")
    public Object export(ThreadRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            threadRestService,
            ThreadExcel.class,
            "会话列表",
            "thread"
        );
    }

    /**
     * 发送消息前申请服务端分配的消息元信息
     *
     * @param request 包含 threadUid 的请求
        * @return 包含 messageUid、timestamp 的元信息
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ_ANY_LEVEL)
    @ActionAnnotation(title = "会话管理", action = "申请消息元信息", description = "request message metadata")
    @Operation(summary = "申请消息元信息", description = "发送消息前获取服务端分配的消息UID与时间戳")
    @PostMapping("/message/meta")
    public ResponseEntity<?> requestMessageMetadata(@RequestBody ThreadRequest request) {
        if (request == null || !StringUtils.hasText(request.getUid())) {
            return ResponseEntity.ok(JsonResult.error("thread uid required"));
        }
        ThreadSequenceResponse response = threadRestService.allocateMessageMetadata(request.getUid());
        return ResponseEntity.ok(JsonResult.success("获取消息元信息成功", response));
    }

    
}
