/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传安全配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.upload.security")
public class UploadSecurityConfig {

    /**
     * 最大文件大小（字节），默认10MB
     */
    private long maxFileSize = 10 * 1024 * 1024L;

    /**
     * 允许的文件扩展名白名单
     */
    private List<String> allowedExtensions = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg",
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",
        "zip", "rar", "7z", "tar", "gz",
        "mp3", "wav", "aac", "ogg", "flac", "m4a",
        "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm"
    );

    /**
     * 危险文件扩展名黑名单
     */
    private List<String> dangerousExtensions = Arrays.asList(
        "exe", "jsp", "php", "sh", "bat", "js", "html", "htm", 
        "asp", "aspx", "dll", "com", "cgi", "jar", "war", "class",
        "vbs", "cmd", "scr", "pif", "lnk", "reg"
    );

    /**
     * 允许的MIME类型
     */
    private List<String> allowedMimeTypes = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml",
        "application/pdf", 
        "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain",
        "application/zip", "application/x-rar-compressed", "application/x-7z-compressed",
        "audio/mpeg", "audio/wav", "audio/aac", "audio/ogg", "audio/flac", "audio/mp4",
        "video/mp4", "video/avi", "video/quicktime", "video/x-ms-wmv", "video/x-flv", "video/x-matroska", "video/webm"
    );

    /**
     * 是否启用图片内容验证
     */
    private boolean enableImageValidation = true;

    /**
     * 是否启用文件名过滤
     */
    private boolean enableFileNameFilter = true;

    /**
     * 是否强制重命名文件
     */
    private boolean forceRename = true;

    /**
     * 文件名最大长度
     */
    private int maxFileNameLength = 255;

    /**
     * 是否记录上传日志
     */
    private boolean enableUploadLog = true;

    /**
     * 是否启用病毒扫描（预留接口）
     */
    private boolean enableVirusScan = false;

    /**
     * 检查文件扩展名是否被允许
     */
    public boolean isExtensionAllowed(String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            return false;
        }
        String ext = extension.toLowerCase().trim();
        
        // 先检查黑名单
        if (dangerousExtensions.contains(ext)) {
            return false;
        }
        
        // 再检查白名单
        return allowedExtensions.contains(ext);
    }

    /**
     * 检查MIME类型是否被允许
     */
    public boolean isMimeTypeAllowed(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            return false;
        }
        return allowedMimeTypes.contains(mimeType.toLowerCase().trim());
    }

    /**
     * 检查文件大小是否超限
     */
    public boolean isFileSizeValid(long fileSize) {
        return fileSize > 0 && fileSize <= maxFileSize;
    }

    /**
     * 获取格式化的最大文件大小描述
     */
    public String getMaxFileSizeDescription() {
        if (maxFileSize >= 1024 * 1024) {
            return (maxFileSize / (1024 * 1024)) + "MB";
        } else if (maxFileSize >= 1024) {
            return (maxFileSize / 1024) + "KB";
        } else {
            return maxFileSize + "B";
        }
    }
}
