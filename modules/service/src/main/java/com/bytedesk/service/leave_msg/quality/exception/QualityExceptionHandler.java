/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 11:09:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-09 11:19:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg.quality.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Data;

@RestControllerAdvice
public class QualityExceptionHandler {

    @ExceptionHandler(QualityException.class)
    public ResponseEntity<ErrorResponse> handleQualityException(QualityException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(QualityInspectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInspectionNotFound(QualityInspectionNotFoundException ex) {
        // ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(QualityRuleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRuleNotFound(QualityRuleNotFoundException ex) {
        // ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}

@Data
class ErrorResponse {
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
    
    public ErrorResponse(String message) {
        this.message = message;
    }
} 