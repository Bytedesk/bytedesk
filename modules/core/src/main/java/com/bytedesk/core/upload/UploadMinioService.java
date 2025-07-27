/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-20 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 17:38:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.BdDateUtils;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.http.Method;

import lombok.extern.slf4j.Slf4j;

/**
 * MinIO 对象存储服务
 * 用于将图片、音频、视频等文件存储到 MinIO
 * 
 * @author bytedesk.com
 */
@Slf4j
@Component
public class UploadMinioService {

    @Autowired
    private BytedeskProperties bytedeskProperties;

    private MinioClient minioClient;

    /**
     * 上传文件到 MinIO
     * 
     * @param file MultipartFile 文件
     * @param fileName 文件名
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String fileName, String folder) {
        try {
            // 生成带时间戳的文件名
            String timestampedFileName = BdDateUtils.formatDatetimeUid() + "_" + fileName;
            
            // 构建完整的对象路径
            String objectPath = folder + "/" + timestampedFileName;
            
            // 获取文件内容类型
            String contentType = file.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            // 上传文件到 MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bytedeskProperties.getMinioBucketName())
                    .object(objectPath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build()
            );

            // 返回文件访问URL
            return getFileUrl(objectPath);
            
        } catch (Exception e) {
            log.error("上传文件到 MinIO 失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件到 MinIO 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传本地文件到 MinIO
     * 
     * @param localFile 本地文件
     * @param fileName 文件名
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    public String uploadFile(File localFile, String fileName, String folder) {
        try {
            // 生成带时间戳的文件名
            String timestampedFileName = BdDateUtils.formatDatetimeUid() + "_" + fileName;
            
            // 构建完整的对象路径
            String objectPath = folder + "/" + timestampedFileName;
            
            // 获取文件内容类型
            String contentType = getContentType(fileName);

            // 上传文件到 MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bytedeskProperties.getMinioBucketName())
                    .object(objectPath)
                    .stream(Files.newInputStream(localFile.toPath()), localFile.length(), -1)
                    .contentType(contentType)
                    .build()
            );

            // 返回文件访问URL
            return getFileUrl(objectPath);
            
        } catch (Exception e) {
            log.error("上传本地文件到 MinIO 失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传本地文件到 MinIO 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 URL 下载并上传到 MinIO
     * 
     * @param url 文件URL
     * @param fileName 文件名
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    public String uploadFromUrl(String url, String fileName, String folder) {
        try {
            // 生成带时间戳的文件名
            String timestampedFileName = BdDateUtils.formatDatetimeUid() + "_" + fileName;
            
            // 构建完整的对象路径
            String objectPath = folder + "/" + timestampedFileName;
            
            // 获取文件内容类型
            String contentType = getContentType(fileName);

            // 从 URL 下载文件并上传到 MinIO
            URL fileUrl = new URL(url);
            try (InputStream inputStream = fileUrl.openStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bytedeskProperties.getMinioBucketName())
                        .object(objectPath)
                        .stream(inputStream, -1, -1)
                        .contentType(contentType)
                        .build()
                );
            }

            // 返回文件访问URL
            return getFileUrl(objectPath);
            
        } catch (Exception e) {
            log.error("从 URL 上传文件到 MinIO 失败: {}", e.getMessage(), e);
            throw new RuntimeException("从 URL 上传文件到 MinIO 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传图片文件
     * 
     * @param file MultipartFile 文件
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadImage(MultipartFile file, String fileName) {
        return uploadFile(file, fileName, "images");
    }

    /**
     * 上传音频文件
     * 
     * @param file MultipartFile 文件
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadAudio(MultipartFile file, String fileName) {
        return uploadFile(file, fileName, "audios");
    }

    /**
     * 上传视频文件
     * 
     * @param file MultipartFile 文件
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadVideo(MultipartFile file, String fileName) {
        return uploadFile(file, fileName, "videos");
    }

    /**
     * 上传文档文件
     * 
     * @param file MultipartFile 文件
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadDocument(MultipartFile file, String fileName) {
        return uploadFile(file, fileName, "documents");
    }

    /**
     * 上传头像文件
     * 
     * @param file MultipartFile 文件
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadAvatar(MultipartFile file, String fileName) {
        return uploadFile(file, fileName, "avatars");
    }

    /**
     * 上传附件文件
     * 
     * @param file MultipartFile 文件
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadAttachment(MultipartFile file, String fileName) {
        return uploadFile(file, fileName, "attachments");
    }

    /**
     * 删除文件
     * 
     * @param objectPath 对象路径
     */
    public void deleteFile(String objectPath) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bytedeskProperties.getMinioBucketName())
                    .object(objectPath)
                    .build()
            );
            log.info("成功删除 MinIO 文件: {}", objectPath);
        } catch (Exception e) {
            log.error("删除 MinIO 文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除 MinIO 文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查文件是否存在
     * 
     * @param objectPath 对象路径
     * @return 是否存在
     */
    public boolean fileExists(String objectPath) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bytedeskProperties.getMinioBucketName())
                    .object(objectPath)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件下载URL（预签名URL）
     * 
     * @param objectPath 对象路径
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    public String getDownloadUrl(String objectPath, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bytedeskProperties.getMinioBucketName())
                    .object(objectPath)
                    .expiry(expiry, TimeUnit.SECONDS)
                    .build()
            );
        } catch (Exception e) {
            log.error("获取文件下载URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取文件下载URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件上传URL（预签名URL）
     * 
     * @param objectPath 对象路径
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    public String getUploadUrl(String objectPath, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bytedeskProperties.getMinioBucketName())
                    .object(objectPath)
                    .expiry(expiry, TimeUnit.SECONDS)
                    .build()
            );
        } catch (Exception e) {
            log.error("获取文件上传URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取文件上传URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件访问URL
     * 
     * @param objectPath 对象路径
     * @return 文件访问URL
     */
    private String getFileUrl(String objectPath) {
        return bytedeskProperties.getMinioEndpoint() + "/" + 
               bytedeskProperties.getMinioBucketName() + "/" + objectPath;
    }

    /**
     * 根据文件扩展名获取内容类型
     * 
     * @param fileName 文件名
     * @return 内容类型
     */
    private String getContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String extension = fileName.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        } else if (extension.endsWith(".webp")) {
            return "image/webp";
        } else if (extension.endsWith(".mp4")) {
            return "video/mp4";
        } else if (extension.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (extension.endsWith(".mov")) {
            return "video/quicktime";
        } else if (extension.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (extension.endsWith(".wav")) {
            return "audio/wav";
        } else if (extension.endsWith(".pdf")) {
            return "application/pdf";
        } else if (extension.endsWith(".doc") || extension.endsWith(".docx")) {
            return "application/msword";
        } else if (extension.endsWith(".xls") || extension.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else if (extension.endsWith(".ppt") || extension.endsWith(".pptx")) {
            return "application/vnd.ms-powerpoint";
        } else if (extension.endsWith(".txt")) {
            return "text/plain";
        } else if (extension.endsWith(".zip")) {
            return "application/zip";
        } else if (extension.endsWith(".rar")) {
            return "application/x-rar-compressed";
        } else {
            return "application/octet-stream";
        }
    }
} 