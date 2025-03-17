/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-13 11:16:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-17 21:49:32
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

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/visitor/api/v1/faq")
@AllArgsConstructor
public class FaqRestControllerVisitor {

    private final FaqRestService faqService;

    // 输入联想搜索faq
    @GetMapping("/search")
    public ResponseEntity<?> search(FaqRequest request) {

        List<FaqEntity> faqList = faqService.findByQuestionContains(request.getQuestion());
        
        return ResponseEntity.ok(JsonResult.success(faqList));
    }

    // 换一换faq
    @GetMapping("/change")
    public ResponseEntity<?> change(FaqRequest request) {

        Page<FaqResponse> page = faqService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // query by uid
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(FaqRequest request) {
        
        FaqResponse faq = faqService.queryByUid(request);
        if (faq == null) {
            return ResponseEntity.ok(JsonResult.error("faq not found"));
        }
        return ResponseEntity.ok(JsonResult.success(faq));
    }

    // rate up faq
    @PostMapping("/rate/up")
    public ResponseEntity<?> rateUp(@RequestBody FaqRequest request) {

        FaqResponse faq = faqService.rateUp(request.getUid());

        return ResponseEntity.ok(JsonResult.success(faq));
    }

    // rate down faq
    @PostMapping("/rate/down")
    public ResponseEntity<?> rateDown(@RequestBody FaqRequest request) {

        FaqResponse faq = faqService.rateDown(request.getUid());

        return ResponseEntity.ok(JsonResult.success(faq));
    }

    // comment faq
    @PostMapping("/comment")
    public ResponseEntity<?> comment(@RequestBody FaqRequest request) {

        // FaqResponse faq = faqService.comment(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}
