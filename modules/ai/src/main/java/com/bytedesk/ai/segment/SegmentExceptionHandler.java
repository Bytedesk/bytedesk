/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-17 16:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 16:35:00
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 分词模块异常处理器
 * 专门处理与分词相关的异常
 * 
 * @author jackning
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.bytedesk.ai.segment")
@ConditionalOnProperty(prefix = "bytedesk", name = "debug", havingValue = "true", matchIfMissing = false)
public class SegmentExceptionHandler {
    
    /**
     * 处理分词业务异常
     * 
     * @param e 分词异常
     * @return 异常响应
     */
    @ExceptionHandler(SegmentException.class)
    public ResponseEntity<?> handleSegmentException(SegmentException e) {
        log.error("分词处理异常: {}", e.getMessage(), e);
        return ResponseEntity.ok(JsonResult.error(e.getMessage()));
    }
    
    /**
     * 处理参数异常
     * 
     * @param e 参数异常
     * @return 异常响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("分词参数异常: {}", e.getMessage(), e);
        return ResponseEntity.ok(JsonResult.error("参数错误: " + e.getMessage()));
    }
    
    /**
     * 处理其他未预期的异常
     * 
     * @param e 运行时异常
     * @return 异常响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error("分词服务运行时异常: {}", e.getMessage(), e);
        return ResponseEntity.ok(JsonResult.error("服务处理失败: " + e.getMessage()));
    }
}