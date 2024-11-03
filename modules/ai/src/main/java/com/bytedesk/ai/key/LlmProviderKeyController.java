/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-26 10:36:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:29:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.key;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ai/key")
@AllArgsConstructor
public class LlmProviderKeyController extends BaseRestController<LlmProviderKeyRequest> {

    private final LlmProviderKeyService service;

    @Override
    public ResponseEntity<?> queryByOrg(LlmProviderKeyRequest request) {
        
        Page<LlmProviderKeyResponse> page = service.queryByOrg(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(LlmProviderKeyRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> create(LlmProviderKeyRequest request) {
        
        LlmProviderKeyResponse response = service.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(LlmProviderKeyRequest request) {
        
        LlmProviderKeyResponse response = service.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(LlmProviderKeyRequest request) {
       
        service.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}
