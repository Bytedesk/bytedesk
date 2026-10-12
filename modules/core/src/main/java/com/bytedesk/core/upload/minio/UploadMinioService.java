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
package com.bytedesk.core.upload.minio;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.SetBucketPolicyArgs;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.BdDateUtils;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.http.Method;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
 * S3 兼容对象存储服务（io.minio 客户端为通用 S3 客户端）
 * 用于将图片、音频、视频等文件存储到任意 S3 兼容后端
 * （MinIO / RustFS / AWS S3 / 阿里云 OSS / 腾讯云 COS 等）
 *
 * 启用条件见 {@link S3StorageEnabledCondition}
 * 
 * @author bytedesk.com
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Conditional(S3StorageEnabledCondition.class)
public class UploadMinioService {

    private final BytedeskProperties bytedeskProperties;

    private final MinioClient minioClient;

    /**
     * 初始化 S3 兼容存储桶和策略
     */
    @PostConstruct
    public void initMinio() {
        try {
            String bucketName = bytedeskProperties.getStorageBucketName();
            
            // 检查存储桶是否存在，如果不存在则创建
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                log.info("创建对象存储桶: {}", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            
            // 公开读策略可配置（bytedesk.storage.public-read，默认 true 保持存量行为）
            if (!bytedeskProperties.isStoragePublicRead()) {
                log.info("跳过桶公开读策略（bytedesk.storage.public-read=false），fileUrl 将使用预签名直链: bucket={}", bucketName);
                log.info("对象存储初始化完成，存储桶: {}, 策略: 私有（预签名访问）", bucketName);
                return;
            }
            
            // 设置存储桶策略为公开读取
            String bucketPolicy = String.format("""
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {
                                "AWS": "*"
                            },
                            "Action": [
                                "s3:GetObject"
                            ],
                            "Resource": [
                                "arn:aws:s3:::%s/*"
                            ]
                        }
                    ]
                }
                """, bucketName);
            
            log.info("设置存储桶 {} 为公开读取", bucketName);
            try {
                minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(bucketPolicy)
                        .build()
                );
                log.info("对象存储初始化完成，存储桶: {}, 策略: 公开读取", bucketName);
            } catch (Exception policyException) {
                // 部分后端（如 RustFS 早期版本）可能不完全支持 PutBucketPolicy：
                // 不阻断启动，提示补救路径（预签名 or 控制台/rc anonymous 手动配置）
                log.warn("设置桶公开读策略失败（不影响启动）: bucket={}, reason={}", bucketName, policyException.getMessage());
                log.warn("若后端不支持该策略，请任选其一：1) 设 bytedesk.storage.public-read=false 改用预签名直链；"
                        + " 2) 通过后端控制台或 rc anonymous 手动配置匿名读取");
            }
            
        } catch (Exception e) {
            log.error("对象存储初始化失败: {}", e.getMessage(), e);
        }
    }

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

            // 上传文件到对象存储
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bytedeskProperties.getStorageBucketName())
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

            // 上传文件到对象存储
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bytedeskProperties.getStorageBucketName())
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

            // 从 URL 下载文件并上传到对象存储
            try (InputStream inputStream = URI.create(url).toURL().openStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bytedeskProperties.getStorageBucketName())
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
                    .bucket(bytedeskProperties.getStorageBucketName())
                    .object(objectPath)
                    .build()
            );
            log.info("成功删除对象存储文件: {}", objectPath);
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
                    .bucket(bytedeskProperties.getStorageBucketName())
                    .object(objectPath)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 下载 MinIO 对象到本地文件
     * 用于后端服务（如 ASR 语音转写）需要读取 MinIO 中文件的场景，
     * 不依赖存储桶公开读策略（走 MinIO 客户端鉴权读取）
     *
     * @param objectPath 对象路径（如：audios/20260914170900_xxx.webm）
     * @param targetFile 目标本地文件路径
     */
    public void downloadObject(String objectPath, Path targetFile) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bytedeskProperties.getStorageBucketName())
                    .object(objectPath)
                    .build())) {
            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("下载 MinIO 文件失败: objectPath={}, reason={}", objectPath, e.getMessage(), e);
            throw new RuntimeException("下载 MinIO 文件失败: " + e.getMessage(), e);
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
                    .bucket(bytedeskProperties.getStorageBucketName())
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
                    .bucket(bytedeskProperties.getStorageBucketName())
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
     * 获取文件访问URL：按公开读策略分支
     * - public-read=true（默认，保持存量行为）：裸直链 {endpoint}/{bucket}/{objectPath}，依赖桶公开读策略
     * - public-read=false：预签名 URL（有效期 bytedesk.storage.presigned-expiry-days，上限 7 天），
     *   摆脱「桶必须公开读」假设，适配不支持 PutBucketPolicy 的后端（如 RustFS 早期版本）
     * 
     * @param objectPath 对象路径
     * @return 文件访问URL
     */
    private String getFileUrl(String objectPath) {
        if (!bytedeskProperties.isStoragePublicRead()) {
            return getDownloadUrl(objectPath, bytedeskProperties.getStoragePresignedExpirySeconds());
        }
        return bytedeskProperties.getStorageEndpoint() + "/" + 
               bytedeskProperties.getStorageBucketName() + "/" + objectPath;
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