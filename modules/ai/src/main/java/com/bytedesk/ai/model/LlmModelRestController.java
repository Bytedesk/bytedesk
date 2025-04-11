/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:20:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 18:42:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/model")
@AllArgsConstructor
public class LlmModelRestController extends BaseRestController<LlmModelRequest> {

    private final LlmModelRestService llmModelRestService;

    @Override
    public ResponseEntity<?> queryByOrg(LlmModelRequest request) {
        
        Page<LlmModelResponse> result = llmModelRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Override
    public ResponseEntity<?> queryByUser(LlmModelRequest request) {
        
        Page<LlmModelResponse> result = llmModelRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Override
    public ResponseEntity<?> create(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Override
    public ResponseEntity<?> update(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Override
    public ResponseEntity<?> delete(LlmModelRequest request) {
        
        llmModelRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // import 
    // @PostMapping("/import")
    // public ResponseEntity<?> importModel(@RequestBody LlmModelRequest request) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'importData'");
    // }

    @Override
    public Object export(LlmModelRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(LlmModelRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

}
