/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 07:04:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-19 07:04:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.prompt;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/prompt")
@AllArgsConstructor
public class PromptController extends BaseController<PromptRequest> {

    private final PromptService promptService;

    @Override
    public ResponseEntity<?> queryByOrg(PromptRequest request) {
        
        Page<PromptResponse> page = promptService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override   
    public ResponseEntity<?> query(PromptRequest request) {
        
        Page<PromptResponse> page = promptService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "prompt", action = "create", description = "create prompt")
    @Override
    public ResponseEntity<?> create(PromptRequest request) {
        
        PromptResponse response = promptService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "prompt", action = "update", description = "update prompt")
    @Override
    public ResponseEntity<?> update(PromptRequest request) {
        
        PromptResponse response = promptService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "prompt", action = "delete", description = "delete prompt")
    @Override
    public ResponseEntity<?> delete(PromptRequest request) {
        
        promptService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success());
    }
    
}
