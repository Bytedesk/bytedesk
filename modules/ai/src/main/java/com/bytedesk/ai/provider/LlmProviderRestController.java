/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 17:03:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 14:06:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/provider")
@AllArgsConstructor
public class LlmProviderRestController extends BaseRestController<LlmProviderRequest> {

    private final LlmProviderRestService llmProviderRestService;

    @Override
    public ResponseEntity<?> queryByOrg(LlmProviderRequest request) {
        
        Page<LlmProviderResponse> page = llmProviderRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(LlmProviderRequest request) {
        
        Page<LlmProviderResponse> page = llmProviderRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(LlmProviderRequest request) {
        
        LlmProviderResponse response = llmProviderRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(LlmProviderRequest request) {
        
        LlmProviderResponse response = llmProviderRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(LlmProviderRequest request) {
        
        llmProviderRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}
