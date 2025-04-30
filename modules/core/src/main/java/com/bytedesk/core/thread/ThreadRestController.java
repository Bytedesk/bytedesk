/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 16:47:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/thread")
public class ThreadRestController extends BaseRestController<ThreadRequest> {

    private final ThreadRestService threadService;

    @Override
    public ResponseEntity<?> queryByOrg(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    @Override
    public ResponseEntity<?> queryByUser(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadService.query(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    // query invite threads
    @GetMapping("/query/invite")
    public ResponseEntity<?> queryByThreadInvite(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByThreadTopic(ThreadRequest request) {

        Optional<ThreadResponse> threadOptional = threadService.queryByTopic(request);
        //
        if (threadOptional.isPresent()) {
            return ResponseEntity.ok(JsonResult.success(threadOptional.get()));
        }
        return ResponseEntity.ok(JsonResult.error("not found"));
    }

    @Override
    public ResponseEntity<?> queryByUid(ThreadRequest request) {

        Optional<ThreadResponse> threadOptional = threadService.queryByThreadUid(request);
        //
        if (threadOptional.isPresent()) {
            return ResponseEntity.ok(JsonResult.success(threadOptional.get()));
        }
        return ResponseEntity.ok(JsonResult.error("not found"));
    }

    @ActionAnnotation(title = "会话", action = "新建", description = "create thread")
    @Override
    public ResponseEntity<?> create(@RequestBody ThreadRequest request) {
        //
        ThreadResponse thread = threadService.create(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    @ActionAnnotation(title = "会话", action = "更新", description = "update thread")
    @Override
    public ResponseEntity<?> update(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadService.update(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // update top
    @PostMapping("/update/top")
    public ResponseEntity<?> updateTop(@RequestBody ThreadRequest request) {

        ThreadResponse thread = threadService.updateTop(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    // update star
    @PostMapping("/update/star")
    public ResponseEntity<?> updateStar(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadService.updateStar(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // update mute
    @PostMapping("/update/mute")
    public ResponseEntity<?> updateMute(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadService.updateMute(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // update user info
    @PostMapping("/update/user")
    public ResponseEntity<?> updateUser(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadService.updateUser(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // update tagList
    @PostMapping("/update/tagList")
    public ResponseEntity<?> updateTagList(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadService.updateTagList(request);
        
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @PostMapping("/update/unread")
    public ResponseEntity<?> updateUnread(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadService.updateUnread(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    // updateThreadUnreadCount
    @PostMapping("/update/unread/count")
    public ResponseEntity<?> updateUnreadCount(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadService.updateUnreadCount(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @ActionAnnotation(title = "会话", action = "close", description = "close thread")
    @PostMapping("/close")
    public ResponseEntity<?> close(@RequestBody ThreadRequest request) {

        request.setAutoClose(false);
        request.setStatus(ThreadProcessStatusEnum.CLOSED.name());
        ThreadResponse threadResponse = threadService.close(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }
    
    @Override
    public ResponseEntity<?> delete(@RequestBody ThreadRequest request) {
        
        threadService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete thread success"));
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "会话", action = "导出", description = "export thread")
    @GetMapping("/export")
    public Object export(ThreadRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            threadService,
            ThreadExcel.class,
            "会话列表",
            "thread"
        );
    }
    
}
