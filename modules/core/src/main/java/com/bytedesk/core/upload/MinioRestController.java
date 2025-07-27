/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-20 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MinIO 文件存储控制器
 * 提供 MinIO 文件上传、下载、删除等 API 接口
 * 
 * @author bytedesk.com
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/minio")
@Tag(name = "MinIO Storage", description = "MinIO object storage management APIs")
public class MinioRestController {

    private final UploadRestService uploadRestService;

    /**
     * 上传文件到 MinIO
     * 
     * @param file 文件
     * @param request 上传请求
     * @return 上传结果
     */
    @Operation(summary = "Upload file to MinIO", description = "Upload file to MinIO object storage")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadToMinio(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Upload request") UploadRequest request) {
        
        try {
            String fileName = request.getFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = file.getOriginalFilename();
            }
            
            String fileUrl = uploadRestService.storeToMinio(file, fileName, request);
            
            return ResponseEntity.ok(JsonResult.success("File uploaded to MinIO successfully", fileUrl));
            
        } catch (Exception e) {
            log.error("上传文件到 MinIO 失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Upload to MinIO failed: " + e.getMessage()));
        }
    }

    /**
     * 从 URL 上传文件到 MinIO
     * 
     * @param url 文件URL
     * @param fileName 文件名
     * @param request 上传请求
     * @return 上传结果
     */
    @Operation(summary = "Upload file from URL to MinIO", description = "Download file from URL and upload to MinIO")
    @PostMapping("/upload/url")
    public ResponseEntity<?> uploadUrlToMinio(
            @Parameter(description = "File URL") @RequestParam("url") String url,
            @Parameter(description = "File name") @RequestParam("fileName") String fileName,
            @Parameter(description = "Upload request") UploadRequest request) {
        
        try {
            String fileUrl = uploadRestService.storeUrlToMinio(url, fileName, request);
            
            return ResponseEntity.ok(JsonResult.success("File uploaded from URL to MinIO successfully", fileUrl));
            
        } catch (Exception e) {
            log.error("从 URL 上传文件到 MinIO 失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Upload from URL to MinIO failed: " + e.getMessage()));
        }
    }

    /**
     * 删除 MinIO 中的文件
     * 
     * @param objectPath 对象路径
     * @return 删除结果
     */
    @Operation(summary = "Delete file from MinIO", description = "Delete file from MinIO object storage")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFromMinio(
            @Parameter(description = "Object path in MinIO") @RequestParam("objectPath") String objectPath) {
        
        try {
            uploadRestService.deleteFromMinio(objectPath);
            
            return ResponseEntity.ok(JsonResult.success("File deleted from MinIO successfully"));
            
        } catch (Exception e) {
            log.error("删除 MinIO 文件失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Delete from MinIO failed: " + e.getMessage()));
        }
    }

    /**
     * 检查文件是否存在于 MinIO
     * 
     * @param objectPath 对象路径
     * @return 检查结果
     */
    @Operation(summary = "Check if file exists in MinIO", description = "Check if file exists in MinIO object storage")
    @GetMapping("/exists")
    public ResponseEntity<?> fileExistsInMinio(
            @Parameter(description = "Object path in MinIO") @RequestParam("objectPath") String objectPath) {
        
        try {
            boolean exists = uploadRestService.fileExistsInMinio(objectPath);
            
            return ResponseEntity.ok(JsonResult.success("File existence check completed", exists));
            
        } catch (Exception e) {
            log.error("检查 MinIO 文件存在性失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("File existence check failed: " + e.getMessage()));
        }
    }

    /**
     * 获取文件下载URL（预签名URL）
     * 
     * @param objectPath 对象路径
     * @param expiry 过期时间（秒），默认3600秒
     * @return 下载URL
     */
    @Operation(summary = "Get file download URL", description = "Get presigned download URL for file in MinIO")
    @GetMapping("/download-url")
    public ResponseEntity<?> getDownloadUrl(
            @Parameter(description = "Object path in MinIO") @RequestParam("objectPath") String objectPath,
            @Parameter(description = "URL expiry time in seconds") @RequestParam(value = "expiry", defaultValue = "3600") int expiry) {
        
        try {
            String downloadUrl = uploadRestService.getMinioDownloadUrl(objectPath, expiry);
            
            return ResponseEntity.ok(JsonResult.success("Download URL generated successfully", downloadUrl));
            
        } catch (Exception e) {
            log.error("获取 MinIO 下载URL失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Get download URL failed: " + e.getMessage()));
        }
    }

    /**
     * 获取文件上传URL（预签名URL）
     * 
     * @param objectPath 对象路径
     * @param expiry 过期时间（秒），默认3600秒
     * @return 上传URL
     */
    @Operation(summary = "Get file upload URL", description = "Get presigned upload URL for file in MinIO")
    @GetMapping("/upload-url")
    public ResponseEntity<?> getUploadUrl(
            @Parameter(description = "Object path in MinIO") @RequestParam("objectPath") String objectPath,
            @Parameter(description = "URL expiry time in seconds") @RequestParam(value = "expiry", defaultValue = "3600") int expiry) {
        
        try {
            String uploadUrl = uploadRestService.getMinioUploadUrl(objectPath, expiry);
            
            return ResponseEntity.ok(JsonResult.success("Upload URL generated successfully", uploadUrl));
            
        } catch (Exception e) {
            log.error("获取 MinIO 上传URL失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Get upload URL failed: " + e.getMessage()));
        }
    }

    /**
     * 上传图片到 MinIO
     * 
     * @param file 图片文件
     * @param request 上传请求
     * @return 上传结果
     */
    @Operation(summary = "Upload image to MinIO", description = "Upload image file to MinIO object storage")
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImageToMinio(
            @Parameter(description = "Image file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Upload request") UploadRequest request) {
        
        try {
            String fileName = request.getFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = file.getOriginalFilename();
            }
            
            // 设置文件类型为图片
            request.setKbType("image");
            
            String fileUrl = uploadRestService.storeToMinio(file, fileName, request);
            
            return ResponseEntity.ok(JsonResult.success("Image uploaded to MinIO successfully", fileUrl));
            
        } catch (Exception e) {
            log.error("上传图片到 MinIO 失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Upload image to MinIO failed: " + e.getMessage()));
        }
    }

    /**
     * 上传音频到 MinIO
     * 
     * @param file 音频文件
     * @param request 上传请求
     * @return 上传结果
     */
    @Operation(summary = "Upload audio to MinIO", description = "Upload audio file to MinIO object storage")
    @PostMapping("/upload/audio")
    public ResponseEntity<?> uploadAudioToMinio(
            @Parameter(description = "Audio file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Upload request") UploadRequest request) {
        
        try {
            String fileName = request.getFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = file.getOriginalFilename();
            }
            
            // 设置文件类型为音频
            request.setKbType("audio");
            
            String fileUrl = uploadRestService.storeToMinio(file, fileName, request);
            
            return ResponseEntity.ok(JsonResult.success("Audio uploaded to MinIO successfully", fileUrl));
            
        } catch (Exception e) {
            log.error("上传音频到 MinIO 失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Upload audio to MinIO failed: " + e.getMessage()));
        }
    }

    /**
     * 上传视频到 MinIO
     * 
     * @param file 视频文件
     * @param request 上传请求
     * @return 上传结果
     */
    @Operation(summary = "Upload video to MinIO", description = "Upload video file to MinIO object storage")
    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideoToMinio(
            @Parameter(description = "Video file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Upload request") UploadRequest request) {
        
        try {
            String fileName = request.getFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = file.getOriginalFilename();
            }
            
            // 设置文件类型为视频
            request.setKbType("video");
            
            String fileUrl = uploadRestService.storeToMinio(file, fileName, request);
            
            return ResponseEntity.ok(JsonResult.success("Video uploaded to MinIO successfully", fileUrl));
            
        } catch (Exception e) {
            log.error("上传视频到 MinIO 失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Upload video to MinIO failed: " + e.getMessage()));
        }
    }

    /**
     * 上传文档到 MinIO
     * 
     * @param file 文档文件
     * @param request 上传请求
     * @return 上传结果
     */
    @Operation(summary = "Upload document to MinIO", description = "Upload document file to MinIO object storage")
    @PostMapping("/upload/document")
    public ResponseEntity<?> uploadDocumentToMinio(
            @Parameter(description = "Document file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Upload request") UploadRequest request) {
        
        try {
            String fileName = request.getFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = file.getOriginalFilename();
            }
            
            // 设置文件类型为文档
            request.setKbType("document");
            
            String fileUrl = uploadRestService.storeToMinio(file, fileName, request);
            
            return ResponseEntity.ok(JsonResult.success("Document uploaded to MinIO successfully", fileUrl));
            
        } catch (Exception e) {
            log.error("上传文档到 MinIO 失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(JsonResult.error("Upload document to MinIO failed: " + e.getMessage()));
        }
    }
} 