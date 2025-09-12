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

import jakarta.annotation.PostConstruct;

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
    private List<String> allowedExtensions = getDefaultAllowedExtensions();

    /**
     * 危险文件扩展名黑名单
     */
    private List<String> dangerousExtensions = getDefaultDangerousExtensions();

    /**
     * 允许的MIME类型
     */
    private List<String> allowedMimeTypes = getDefaultAllowedMimeTypes();

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
     * 初始化默认配置
     */
    @PostConstruct
    public void initDefaults() {
        // 如果配置文件中没有设置，则使用默认值
        if (allowedExtensions == null || allowedExtensions.isEmpty()) {
            allowedExtensions = getDefaultAllowedExtensions();
        }
        if (dangerousExtensions == null || dangerousExtensions.isEmpty()) {
            dangerousExtensions = getDefaultDangerousExtensions();
        }
        if (allowedMimeTypes == null || allowedMimeTypes.isEmpty()) {
            allowedMimeTypes = getDefaultAllowedMimeTypes();
        }
    }

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

    /**
     * 获取默认允许的文件扩展名列表
     */
    private static List<String> getDefaultAllowedExtensions() {
        return Arrays.asList(
            // 图片文件
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "tiff", "tif",
            // iOS相机图片格式
            "heic", "heif", "raw", "dng",
            // 文档文件
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf", "pages", "numbers", "keynote",
            // 压缩文件
            "zip", "rar", "7z", "tar", "gz",
            // 音频文件
            "mp3", "wav", "aac", "ogg", "flac", "m4a",
            // iOS音频格式
            "caf", "aiff", "alac",
            // Android音频格式
            "amr", "3gp", "opus",
            // 视频文件
            "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm",
            // 移动端视频格式
            "3gp", "3g2", "m4v",
            // iOS视频格式（ProRes等）
            "prores",
            // 其他移动端常见格式
            "vcf", // 联系人文件
            "gpx", // GPS轨迹文件
            "kml", "kmz" // 地图文件
        );
    }

    /**
     * 获取默认危险文件扩展名列表
     */
    private static List<String> getDefaultDangerousExtensions() {
        return Arrays.asList(
            // 可执行文件
            "exe", "msi", "dmg", "app", "deb", "rpm",
            // 脚本文件
            "jsp", "php", "asp", "aspx", "sh", "bat", "cmd", "vbs", "ps1",
            // 网页文件
            "js", "html", "htm", "xml", "xhtml",
            // Java相关
            "jar", "war", "class", "java",
            // .NET相关
            "dll", "exe", "msi",
            // 其他危险文件
            "com", "cgi", "scr", "pif", "lnk", "reg", "hta"
        );
    }

    /**
     * 获取默认允许的MIME类型列表
     */
    private static List<String> getDefaultAllowedMimeTypes() {
        return Arrays.asList(
            // 图片类型
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml",
            "image/x-icon", "image/tiff", "image/vnd.adobe.photoshop",
            // iOS图片格式
            "image/heic", "image/heif", "image/x-canon-cr2", "image/x-adobe-dng",
            // 文档类型
            "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain", "application/rtf",
            // iWork文档格式
            "application/vnd.apple.pages", "application/vnd.apple.numbers", "application/vnd.apple.keynote",
            // 压缩文件
            "application/zip", "application/x-rar-compressed", "application/x-7z-compressed",
            "application/x-tar", "application/gzip",
            // 音频文件
            "audio/mpeg", "audio/wav", "audio/aac", "audio/ogg", "audio/flac", "audio/mp4",
            // iOS音频格式
            "audio/x-caf", "audio/x-aiff", "audio/x-m4a",
            // Android音频格式
            "audio/amr", "audio/3gpp", "audio/opus",
            // 视频文件
            "video/mp4", "video/avi", "video/quicktime", "video/x-ms-wmv", "video/x-flv", 
            "video/x-matroska", "video/webm",
            // 移动端视频格式
            "video/3gpp", "video/3gpp2", "video/x-m4v",
            // 其他移动端格式
            "text/vcard", "text/x-vcard", // 联系人文件
            "application/gpx+xml", // GPS轨迹文件
            "application/vnd.google-earth.kml+xml", "application/vnd.google-earth.kmz" // 地图文件
        );
    }
}
