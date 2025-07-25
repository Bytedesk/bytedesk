/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:16:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assistant")
// @Tag(name = "assistant - 助手", description = "assistant apis")
@Description("Assistant Management Controller - AI assistant and chatbot management APIs")
public class AssistantRestController extends BaseRestController<AssistantRequest> {

    private final AssistantRestService assistantRestService;

    @Override
    public ResponseEntity<?> queryByOrg(AssistantRequest request) {
        
        Page<AssistantResponse> assistantPage = assistantRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(assistantPage));
    }

    @Override
    public ResponseEntity<?> queryByUser(AssistantRequest request) {
        
        Page<AssistantResponse> assistantPage = assistantRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(assistantPage));
    }

    @Override
    public ResponseEntity<?> queryByUid(AssistantRequest request) {
        
        AssistantResponse assistantResponse = assistantRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(assistantResponse));
    }

    @Override
    public ResponseEntity<?> create(AssistantRequest request) {
        
        AssistantResponse assistantResponse = assistantRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(assistantResponse));
    }

    @Override
    public ResponseEntity<?> update(AssistantRequest request) {
        
        AssistantResponse assistantResponse = assistantRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(assistantResponse));
    }

    @Override
    public ResponseEntity<?> delete(AssistantRequest request) {
       
        assistantRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(AssistantRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

}
