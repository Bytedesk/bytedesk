/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 20:24:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:20:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传控制器示例
 * 展示如何使用统一的上传服务
 *
 * @author bytedesk.com
 */
@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Autowired(required = false)
    private UploadServiceFactory uploadServiceFactory;

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (uploadServiceFactory == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "没有可用的云存储服务，请至少启用一个云存储服务");
            return ResponseEntity.badRequest().body(result);
        }
        
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File tempFile = File.createTempFile("upload_", "_" + fileName);
            file.transferTo(tempFile);

            UploadService uploadService = uploadServiceFactory.getUploadService();
            String url = uploadService.uploadAvatar(fileName, tempFile);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("url", url);
            result.put("provider", uploadServiceFactory.getCurrentProvider());

            // 删除临时文件
            tempFile.delete();

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (uploadServiceFactory == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "没有可用的云存储服务，请至少启用一个云存储服务");
            return ResponseEntity.badRequest().body(result);
        }
        
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File tempFile = File.createTempFile("upload_", "_" + fileName);
            file.transferTo(tempFile);

            UploadService uploadService = uploadServiceFactory.getUploadService();
            String url = uploadService.uploadImage(fileName, tempFile);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("url", url);
            result.put("provider", uploadServiceFactory.getCurrentProvider());

            // 删除临时文件
            tempFile.delete();

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 上传文件
     */
    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File tempFile = File.createTempFile("upload_", "_" + fileName);
            file.transferTo(tempFile);

            UploadService uploadService = uploadServiceFactory.getUploadService();
            String url = uploadService.uploadFile(fileName, tempFile);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("url", url);
            result.put("provider", uploadServiceFactory.getCurrentProvider());

            // 删除临时文件
            tempFile.delete();

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 上传附件
     */
    @PostMapping("/attachment")
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File tempFile = File.createTempFile("upload_", "_" + fileName);
            file.transferTo(tempFile);

            UploadService uploadService = uploadServiceFactory.getUploadService();
            String url = uploadService.uploadAttachment(
                MediaType.parseMediaType(file.getContentType()),
                fileName,
                0, 0, username, tempFile
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("url", url);
            result.put("provider", uploadServiceFactory.getCurrentProvider());

            // 删除临时文件
            tempFile.delete();

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取当前上传提供商
     */
    @GetMapping("/provider")
    public ResponseEntity<Map<String, Object>> getCurrentProvider() {
        Map<String, Object> result = new HashMap<>();
        result.put("provider", uploadServiceFactory.getCurrentProvider());
        return ResponseEntity.ok(result);
    }

    /**
     * 切换上传提供商
     */
    @PostMapping("/provider/{provider}")
    public ResponseEntity<Map<String, Object>> switchProvider(@PathVariable String provider) {
        uploadServiceFactory.setUploadProvider(provider);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("provider", uploadServiceFactory.getCurrentProvider());
        result.put("message", "上传提供商切换成功");
        
        return ResponseEntity.ok(result);
    }
} 