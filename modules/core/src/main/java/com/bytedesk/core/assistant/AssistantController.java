/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-14 09:31:08
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assistant")
@Tag(name = "assistant - 助手", description = "assistant apis")
public class AssistantController {

    private final AssistantService assistantService;

    /**
     * query assistant
     *
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<?> query(AssistantRequest assistantRequest) {
        //
        Page<AssistantResponse> assistantPage = assistantService.query(assistantRequest);
        //
        return ResponseEntity.ok(JsonResult.success(assistantPage));
    }

}
