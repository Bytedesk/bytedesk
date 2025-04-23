/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 19:21:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 16:51:52
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传接口，可匿名访问
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/upload")
public class UploadRestControllerVisitor {

    private final UploadRestService uploadService;

    // 文件上传
    @PostMapping("/file")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            UploadRequest request) {
        
        UploadResponse response = uploadService.handleFileUpload(file, request);
        
        return ResponseEntity.ok(JsonResult.success("upload success", response));
    }
    
}
