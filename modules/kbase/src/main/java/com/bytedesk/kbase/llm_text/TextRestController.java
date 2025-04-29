/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 11:47:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/text")
@AllArgsConstructor
public class TextRestController extends BaseRestController<TextRequest> {

    private final TextRestService textRestService;

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    @Override
    public ResponseEntity<?> queryByOrg(TextRequest request) {
        
        Page<TextResponse> texts = textRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUser(TextRequest request) {
        
        Page<TextResponse> texts = textRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(TextRequest request) {
        
        TextResponse text = textRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(TextRequest request) {
        
        TextResponse text = textRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(TextRequest request) {
        
        textRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(TextRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            textRestService,
            TextExcel.class,
            "知识库文本",
            "text"
        );
    }

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUid(TextRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}