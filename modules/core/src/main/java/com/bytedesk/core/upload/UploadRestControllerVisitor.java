/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 19:21:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 09:36:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传接口，可匿名访问
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/upload")
@Tag(name = "Visitor Upload Management", description = "Visitor upload APIs for anonymous file uploads")
@Description("Visitor Upload Controller - Anonymous file upload APIs for visitors without authentication")
public class UploadRestControllerVisitor {

    private final UploadRestService uploadRestService;

    private final UploadSecurityConfig uploadSecurityConfig;

    // 文件上传
    @Operation(summary = "Upload File", description = "Upload file anonymously")
    @PostMapping("/file")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, UploadRequest request) {
        
        UploadResponse response = uploadRestService.handleFileUpload(file, request);
        
        return ResponseEntity.ok(JsonResult.success("upload success", response));
    }

    // 文件上传（支持水印控制）
    @Operation(summary = "Upload File with Watermark", description = "Upload file with watermark control")
    @PostMapping("/file/watermark")
    public ResponseEntity<?> uploadWithWatermark(
            @RequestParam("file") MultipartFile file, 
            UploadRequest request,
            @RequestParam(value = "addWatermark", defaultValue = "true") boolean addWatermark,
            @RequestParam(value = "watermarkText", required = false) String watermarkText,
            @RequestParam(value = "watermarkPosition", required = false) String watermarkPosition) {
        
        // 设置水印相关参数
        if (watermarkText != null && !watermarkText.trim().isEmpty()) {
            request.setWatermarkText(watermarkText);
        }
        if (watermarkPosition != null && !watermarkPosition.trim().isEmpty()) {
            request.setWatermarkPosition(watermarkPosition);
        }
        request.setAddWatermark(addWatermark);
        
        UploadResponse response = uploadRestService.handleFileUpload(file, request);
        
        return ResponseEntity.ok(JsonResult.success("upload success", response));
    }

    // 获取危险文件后缀黑名单（匿名可访问）
    // 获取上传安全配置（匿名可访问，供前端提示与预校验使用）
    @Operation(summary = "Get Upload Security Config", description = "Get upload security config for UI hints and pre-validation (anonymous)")
    @GetMapping("/security/config")
    public ResponseEntity<?> getUploadSecurityConfig() {
        Map<String, Object> data = new HashMap<>();
        data.put("maxFileSize", uploadSecurityConfig.getMaxFileSize());
        data.put("maxFileSizeDescription", uploadSecurityConfig.getMaxFileSizeDescription());
        data.put("allowedExtensions", uploadSecurityConfig.getAllowedExtensions());
        data.put("dangerousExtensions", uploadSecurityConfig.getDangerousExtensions());
        data.put("allowedMimeTypes", uploadSecurityConfig.getAllowedMimeTypes());
        data.put("enableMimeTypeValidation", uploadSecurityConfig.isEnableMimeTypeValidation());
        return ResponseEntity.ok(JsonResult.success("success", data));
    }
    
}
