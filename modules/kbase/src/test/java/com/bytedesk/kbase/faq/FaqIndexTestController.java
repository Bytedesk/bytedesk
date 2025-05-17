/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 11:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 11:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * FAQ索引测试控制器
 * 用于手动触发FAQ索引测试
 */
@Tag(name = "FAQ索引测试")
@RestController
@RequestMapping("/api/v1/kbase/faq/test")
public class FaqIndexTestController {

    @Autowired
    private FaqBulkIndexTester faqBulkIndexTester;
    
    @Operation(summary = "测试批量FAQ索引")
    @PostMapping("/bulk-index")
    public ResponseEntity<String> testBulkFaqIndexing(
            @RequestParam(value = "count", defaultValue = "10") int count) {
        
        faqBulkIndexTester.testBulkFaqIndexing(count);
        
        return ResponseEntity.ok("FAQ批量索引测试已启动，索引数量: " + count);
    }
}
