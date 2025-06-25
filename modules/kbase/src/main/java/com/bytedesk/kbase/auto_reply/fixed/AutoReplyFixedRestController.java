/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:39:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 16:45:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

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

@RestController
@RequestMapping("/api/v1/autoreply/fixed")
@AllArgsConstructor
public class AutoReplyFixedRestController extends BaseRestController<AutoReplyFixedRequest> {

    private final AutoReplyFixedRestService autoReplyService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(AutoReplyFixedRequest request) {
        
        Page<AutoReplyFixedResponse> page = autoReplyService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(AutoReplyFixedRequest request) {
        
        Page<AutoReplyFixedResponse> page = autoReplyService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "自动回复", action = "新建", description = "create autoReplyFixed")
    @Override
    public ResponseEntity<?> create(AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "自动回复", action = "更新", description = "update autoReplyFixed")
    @Override
    public ResponseEntity<?> update(AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "自动回复", action = "删除", description = "delete autoReplyFixed")
    @Override
    public ResponseEntity<?> delete(AutoReplyFixedRequest request) {
        
        autoReplyService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    @ActionAnnotation(title = "自动回复", action = "启用", description = "enable autoReply")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.enable(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }
    
    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "自动回复", action = "导出", description = "export autoReplyFixed")
    @GetMapping("/export")
    public Object export(AutoReplyFixedRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            autoReplyService,
            AutoReplyFixedExcel.class,
            "AutoReplyFixed",
            "auto_reply-fixed"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(AutoReplyFixedRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
}
