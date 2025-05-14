/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-13 11:16:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 11:01:40
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
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/visitor/api/v1/faq")
@AllArgsConstructor
public class FaqRestControllerVisitor {

    private final FaqElasticService faqElasticService;

    private final FaqRestService faqRestService;

    // 输入联想搜索faq
    @GetMapping("/suggest")
    public ResponseEntity<?> suggest(FaqRequest request) {

        List<FaqElasticSearchResult> suggestList = faqElasticService.suggestFaq(request);

        return ResponseEntity.ok(JsonResult.success(suggestList));
    }

    // 换一换faq
    @GetMapping("/change")
    public ResponseEntity<?> change(FaqRequest request) {

        Page<FaqResponse> page = faqRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // 点击faq
    @GetMapping("/click/uid")
    public ResponseEntity<?> clickFaq(FaqRequest request) {
        
        FaqResponse faq = faqRestService.clickFaq(request);
        if (faq == null) {
            return ResponseEntity.ok(JsonResult.error("faq not found"));
        }
        return ResponseEntity.ok(JsonResult.success(faq));
    }
    
    


}
