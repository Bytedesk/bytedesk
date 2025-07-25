/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 22:53:31
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
public class ThreadRestController extends BaseRestController<ThreadRequest> {

    private final ThreadRestService threadRestService;

    /**
     * 根据组织查询会话
     * 
     * @param request 查询请求
     * @return 分页会话列表
     */
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
    @Operation(summary = "查询邀请会话", description = "查询邀请相关的会话")
    @GetMapping("/query/invite")
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
    @Operation(summary = "根据主题查询会话", description = "通过主题查找相关会话")
    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByThreadTopic(ThreadRequest request) {
  
        ThreadResponse threadResponse = threadRestService.queryByTopic(request);
  
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 主要用于查询 某个成员 的 某个群组会话
     * @param request
     * @return
     */
    @Operation(summary = "根据主题和用户查询会话", description = "通过主题和用户查找相关会话")
    @GetMapping("/query/topic/owner")
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
    @Operation(summary = "创建会话", description = "创建新的会话")
    @ActionAnnotation(title = "会话", action = "新建", description = "create thread")
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
    @Operation(summary = "更新会话", description = "更新已存在的会话信息")
    @ActionAnnotation(title = "会话", action = "更新", description = "update thread")
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
    @Operation(summary = "更新会话置顶状态", description = "设置或取消会话置顶")
    @PostMapping("/update/top")
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
    @Operation(summary = "更新会话标星状态", description = "设置或取消会话标星")
    @PostMapping("/update/star")
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
    @Operation(summary = "更新会话静音状态", description = "设置或取消会话静音")
    @PostMapping("/update/mute")
    public ResponseEntity<?> updateMute(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateMute(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // update/hide
    @Operation(summary = "更新会话隐藏状态", description = "设置或取消会话隐藏")
    @PostMapping("/update/hide")
    public ResponseEntity<?> updateHide(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateHide(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // update/fold
    @Operation(summary = "更新会话折叠状态", description = "设置或取消会话折叠")
    @PostMapping("/update/fold")
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
    @Operation(summary = "更新会话用户信息", description = "更新会话关联的用户信息")
    @PostMapping("/update/user")
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
    @Operation(summary = "更新会话标签列表", description = "更新会话的标签信息")
    @PostMapping("/update/tagList")
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
    @Operation(summary = "更新会话未读状态", description = "标记会话为已读或未读")
    @PostMapping("/update/unread")
    public ResponseEntity<?> updateUnread(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateUnread(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话未读数量
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @Operation(summary = "更新会话未读数量", description = "更新会话的未读消息数量")
    @PostMapping("/update/unread/count")
    public ResponseEntity<?> updateUnreadCount(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateUnreadCount(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 根据用户UID查询所有会话
     * 
     * @param request 包含userUid的请求
     * @return 用户所有会话列表
     */
    @Operation(summary = "根据用户UID查询所有会话", description = "通过userUid查询用户相关的所有会话")
    @GetMapping("/query/by/user/topics")
    public ResponseEntity<?> queryByUserTopics(ThreadRequest request) {
        
        Page<ThreadResponse> responses = threadRestService.queryThreadsByUserTopics(request);

        return ResponseEntity.ok(JsonResult.success(responses));
    }

    // update/note
    @Operation(summary = "更新会话备注", description = "更新会话的备注信息")
    @PostMapping("/update/note")
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
    @Operation(summary = "关闭会话", description = "关闭指定会话")
    @ActionAnnotation(title = "会话", action = "close", description = "close thread")
    @PostMapping("/close")
    public ResponseEntity<?> close(@RequestBody ThreadRequest request) {

        request.setAutoClose(false);
        request.setStatus(ThreadProcessStatusEnum.CLOSED.name());
        ThreadResponse threadResponse = threadRestService.close(request);
        // 
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // close thread by topic
    @Operation(summary = "关闭会话", description = "关闭指定主题的会话")
    @PostMapping("/close/topic")
    public ResponseEntity<?> closeByTopic(@RequestBody ThreadRequest request) {
        
        request.setAutoClose(false);
        request.setStatus(ThreadProcessStatusEnum.CLOSED.name());
        ThreadResponse threadResponse = threadRestService.closeByTopic(request);
        if (threadResponse == null) {
            return ResponseEntity.ok(JsonResult.error("not found"));
        }
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }
    
    /**
     * 删除会话
     * 
     * @param request 删除请求
     * @return 删除结果
     */
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
    @Operation(summary = "导出会话列表", description = "将会话数据导出为Excel格式")
    @ActionAnnotation(title = "会话", action = "导出", description = "export thread")
    @GetMapping("/export")
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
    
}
