/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-15 15:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-15 15:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.alibaba;

import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.alibaba.json_object.ContactInfo;
import com.bytedesk.ai.alibaba.json_object.ProductReview;
import com.bytedesk.ai.alibaba.json_object.TextAnalysis;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

/**
 * BeanOutputConverter 示例：生成 JSON Schema 便于接口测试
 */
@RestController
@RequestMapping("/ai/alibaba")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.dashscope.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AlibabaDemoController {

    private final BytedeskProperties bytedeskProperties;

    private final AlibabaDemoService alibabaDemoService;

    // http://127.0.0.1:9003/ai/alibaba/contact
    @GetMapping("/contact")
    public ResponseEntity<JsonResult<?>> contactSchema() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        BeanOutputConverter<ContactInfo> outputConverter = new BeanOutputConverter<>(ContactInfo.class);
        String format = outputConverter.getFormat();
        //
        String result = alibabaDemoService.getContactSchema(format);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // http://127.0.0.1:9003/ai/alibaba/contact/type
    @GetMapping("/contact/type")
    public ResponseEntity<JsonResult<?>> contactType() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        
        String result = alibabaDemoService.getContactType();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // http://127.0.0.1:9003/ai/alibaba/product-review
    @GetMapping("/product-review")
    public ResponseEntity<JsonResult<?>> productReviewSchema() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        BeanOutputConverter<ProductReview> outputConverter = new BeanOutputConverter<>(ProductReview.class);
        String format = outputConverter.getFormat();
        //
        String result = alibabaDemoService.getProductReviewSchema(format);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // http://127.0.0.1:9003/ai/alibaba/text-analysis
    @GetMapping("/text-analysis")
    public ResponseEntity<JsonResult<?>> textAnalysisSchema() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        BeanOutputConverter<TextAnalysis> outputConverter = new BeanOutputConverter<>(TextAnalysis.class);
        String format = outputConverter.getFormat();
        //
        String result = alibabaDemoService.getTextAnalysisSchema(format);
        return ResponseEntity.ok(JsonResult.success(result));

    }
}
