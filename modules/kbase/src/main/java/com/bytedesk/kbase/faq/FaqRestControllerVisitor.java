/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-13 11:16:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-13 11:35:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/visitor/api/v1/faq")
@AllArgsConstructor
public class FaqRestControllerVisitor {

    private final FaqRestService faqService;

    @GetMapping("/search")
    public ResponseEntity<?> search(FaqRequest request) {
        List<FaqEntity> faqList = faqService.findByQuestionContains(request.getQuestion());
        return ResponseEntity.ok(JsonResult.success(faqList));
    }
    
}
