/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 16:45:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

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
@RequestMapping("/api/v1/autoreply/keyword")
@AllArgsConstructor
public class AutoReplyKeywordRestController extends BaseRestController<AutoReplyKeywordRequest> {

    private final AutoReplyKeywordRestService keywordRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "关键词", action = "新建", description = "create keyword")
    @Override
    public ResponseEntity<?> create(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "关键词", action = "更新", description = "update keyword")
    @Override
    public ResponseEntity<?> update(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "关键词", action = "删除", description = "delete keyword")
    @Override
    public ResponseEntity<?> delete(@RequestBody AutoReplyKeywordRequest request) {

        keywordRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @ActionAnnotation(title = "关键词", action = "启用", description = "enable keyword")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "关键词", action = "导出", description = "export keyword")
    @GetMapping("/export")
    public Object export(AutoReplyKeywordRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            keywordRestService,
            AutoReplyKeywordExcel.class,
            "关键词",
            "auto_reply-keyword"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(AutoReplyKeywordRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }


    
}
