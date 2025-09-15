/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-15 16:51:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 17:02:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/feedback")
@AllArgsConstructor
public class FeedbackRestControllerVisitor {

    private final FeedbackRestService feedbackRestService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request) {
        
        FeedbackResponse response = feedbackRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }
    
}
