/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:22:12
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
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assistant")
// @Tag(name = "Assistant Management", description = "Assistant APIs")
@Description("Assistant Management Controller - AI assistant and chatbot management APIs")
public class AssistantRestController extends BaseRestController<AssistantRequest, AssistantRestService> {

    private final AssistantRestService assistantRestService;

    @Operation(summary = "Query Assistants by Organization", description = "Retrieve assistants for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_ASSISTANT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query assistant")
    @Override
    public ResponseEntity<?> queryByOrg(AssistantRequest request) {
        
        Page<AssistantResponse> assistantPage = assistantRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(assistantPage));
    }

    @Operation(summary = "Query Assistants by User", description = "Retrieve assistants for the current user")
    @ActionAnnotation(title = I18Consts.I18N_ASSISTANT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query assistant")
    @Override
    public ResponseEntity<?> queryByUser(AssistantRequest request) {
        
        Page<AssistantResponse> assistantPage = assistantRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(assistantPage));
    }

    @Operation(summary = "Query Assistants by UID", description = "Retrieve a specific assistant entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_ASSISTANT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query assistant")
    @Override
    public ResponseEntity<?> queryByUid(AssistantRequest request) {
        
        AssistantResponse assistantResponse = assistantRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(assistantResponse));
    }

    @Operation(summary = "Create Assistants", description = "Create a new assistant entry")
    @ActionAnnotation(title = I18Consts.I18N_ASSISTANT, action = I18Consts.I18N_ACTION_CREATE, description = "create assistant")
    @Override
    public ResponseEntity<?> create(AssistantRequest request) {
        
        AssistantResponse assistantResponse = assistantRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(assistantResponse));
    }

    @Operation(summary = "Update Assistants", description = "Update an existing assistant entry")
    @ActionAnnotation(title = I18Consts.I18N_ASSISTANT, action = I18Consts.I18N_ACTION_UPDATE, description = "update assistant")
    @Override
    public ResponseEntity<?> update(AssistantRequest request) {
        
        AssistantResponse assistantResponse = assistantRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(assistantResponse));
    }

    @Operation(summary = "Delete Assistants", description = "Delete a assistant entry")
    @ActionAnnotation(title = I18Consts.I18N_ASSISTANT, action = I18Consts.I18N_ACTION_DELETE, description = "delete assistant")
    @Override
    public ResponseEntity<?> delete(AssistantRequest request) {
       
        assistantRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Assistants", description = "Export assistants to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_ASSISTANT, action = I18Consts.I18N_ACTION_EXPORT, description = "export assistant")
    @Override
    public Object export(AssistantRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

}
