/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-17 15:08:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 16:01:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.segment;

import com.bytedesk.core.utils.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试分词控制器，上线可注释掉
 * https://github.com/houbb/segment?tab=readme-ov-file
 * 
 * @author jackning
 */
@Slf4j
@Tag(name = "分词接口", description = "中文分词相关API")
@RestController
@RequestMapping("/segment/api/v1")
@AllArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk", name = "debug", havingValue = "true", matchIfMissing = false)
public class SegmentController {

    private final SegmentService segmentService;
    
    // http://127.0.0.1:9003/segment/api/v1/words?text=这是一个伸手不见五指的黑夜。
    @Operation(summary = "基础分词", description = "对文本进行基础分词，返回词语列表")
    @GetMapping("/words")
    public ResponseEntity<?> segmentWords(
            @Parameter(description = "待分词的文本", required = true)
            @RequestParam String text) {
        List<String> words = segmentService.segmentWords(text);
        return ResponseEntity.ok(JsonResult.success("分词成功", words));
    }
    
    // http://127.0.0.1:9003/segment/api/v1/details?text=这是一个伸手不见五指的黑夜。
    @Operation(summary = "详细分词", description = "对文本进行详细分词，返回包含位置信息的分词结果")
    @GetMapping("/details")
    public ResponseEntity<?> segmentDetails(@Parameter(description = "待分词的文本", required = true) @RequestParam String text) {
        List<SegmentWordDetail> details = segmentService.segmentDetails(text);
        return ResponseEntity.ok(JsonResult.success("详细分词成功", details));
    }
    
    // http://127.0.0.1:9003/segment/api/v1/count?text=这是一个伸手不见五指的黑夜。
    @Operation(summary = "词频统计", description = "对文本进行分词并统计词频")
    @GetMapping("/count")
    public ResponseEntity<?> wordCount(@Parameter(description = "待分词的文本", required = true) @RequestParam String text) {
        Map<String, Integer> wordCount = segmentService.wordCount(text);
        return ResponseEntity.ok(JsonResult.success("词频统计成功", wordCount));
    }
    
    // http://127.0.0.1:9003/segment/api/v1/segment?text=这是一个伸手不见五指的黑夜。
    @Operation(summary = "高级分词", description = "支持多种分词类型和过滤选项的高级分词接口")
    @PostMapping("/segment")
    public ResponseEntity<?> segment(@Valid @RequestBody SegmentRequest request) {
        SegmentResponse response = segmentService.segment(request);
        return ResponseEntity.ok(JsonResult.success("分词处理成功", response));
    }
    
    // http://127.0.0.1:9003/segment/api/v1/batch?type=word
    @Operation(summary = "批量分词", description = "批量处理多个文本的分词")
    @GetMapping("/batch")
    public ResponseEntity<?> batchSegment(
            @Parameter(description = "文本列表", required = true) 
            @RequestBody List<String> texts,
            @Parameter(description = "分词类型：word(词语)、detail(详细)、count(词频)", required = false)
            @RequestParam(defaultValue = "word") String type) {
        List<SegmentResponse> responses = segmentService.batchSegment(texts, type);
        return ResponseEntity.ok(JsonResult.success("批量分词成功", responses));
    }
    
    // http://127.0.0.1:9003/segment/api/v1/simple?text=这是一个伸手不见五指的黑夜。
    @Operation(summary = "简单分词", description = "简单的GET请求分词接口")
    @GetMapping("/simple")
    public ResponseEntity<?> simpleSegment(
            @Parameter(description = "待分词的文本", required = true)
            @RequestParam String text,
            @Parameter(description = "是否过滤标点符号", required = false)
            @RequestParam(defaultValue = "false") Boolean filterPunctuation,
            @Parameter(description = "最小词长度", required = false)
            @RequestParam(defaultValue = "1") Integer minLength) {
        List<String> words = segmentService.segmentWords(text);
        words = segmentService.filterWords(words, filterPunctuation, minLength);
        return ResponseEntity.ok(JsonResult.success("简单分词成功", words));
    }
    
}
