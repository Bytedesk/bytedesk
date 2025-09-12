/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-12 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 文件上传相关工具类
 * 包含文件扩展名提取、文件类型判断、文件名过滤等通用功能
 * 
 * @author jackning
 */
@Slf4j
@UtilityClass
public class BdUploadUtils {

    /**
     * 安全文件名的正则表达式，只允许字母数字下划线点横线
     */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");

    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 文件扩展名（小写），如果没有扩展名返回空字符串
     */
    public static String getFileExtension(@Nullable String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取不带扩展名的文件名
     * 
     * @param fileName 文件名
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(@Nullable String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }

    /**
     * 判断是否为图片文件
     * 
     * @param fileName 文件名
     * @param contentType MIME类型
     * @return 是否为图片文件
     */
    public static boolean isImageFile(@Nullable String fileName, @Nullable String contentType) {
        // 先检查MIME类型
        if (StringUtils.hasText(contentType) && contentType.startsWith("image/")) {
            return true;
        }
        
        // 再检查文件扩展名
        String ext = getFileExtension(fileName);
        return ext.matches("jpg|jpeg|png|gif|bmp|webp|svg|ico|tiff|tif|heic|heif|raw|dng");
    }

    /**
     * 判断是否为音频文件
     * 
     * @param fileName 文件名
     * @param contentType MIME类型
     * @return 是否为音频文件
     */
    public static boolean isAudioFile(@Nullable String fileName, @Nullable String contentType) {
        // 先检查MIME类型
        if (StringUtils.hasText(contentType) && contentType.startsWith("audio/")) {
            return true;
        }
        
        // 再检查文件扩展名
        String ext = getFileExtension(fileName);
        return ext.matches("mp3|wav|aac|ogg|flac|m4a|caf|aiff|alac|amr|3gp|opus");
    }

    /**
     * 判断是否为视频文件
     * 
     * @param fileName 文件名
     * @param contentType MIME类型
     * @return 是否为视频文件
     */
    public static boolean isVideoFile(@Nullable String fileName, @Nullable String contentType) {
        // 先检查MIME类型
        if (StringUtils.hasText(contentType) && contentType.startsWith("video/")) {
            return true;
        }
        
        // 再检查文件扩展名
        String ext = getFileExtension(fileName);
        return ext.matches("mp4|avi|mov|wmv|flv|mkv|webm|3gp|3g2|m4v|prores");
    }

    /**
     * 判断是否为文档文件
     * 
     * @param fileName 文件名
     * @param contentType MIME类型
     * @return 是否为文档文件
     */
    public static boolean isDocumentFile(@Nullable String fileName, @Nullable String contentType) {
        // 检查MIME类型
        if (StringUtils.hasText(contentType)) {
            if (contentType.equals("application/pdf") ||
                contentType.contains("document") ||
                contentType.contains("word") ||
                contentType.contains("excel") ||
                contentType.contains("powerpoint") ||
                contentType.equals("text/plain") ||
                contentType.equals("application/rtf")) {
                return true;
            }
        }
        
        // 检查文件扩展名
        String ext = getFileExtension(fileName);
        return ext.matches("pdf|doc|docx|xls|xlsx|ppt|pptx|txt|rtf|pages|numbers|keynote");
    }

    /**
     * 判断是否为压缩文件
     * 
     * @param fileName 文件名
     * @param contentType MIME类型
     * @return 是否为压缩文件
     */
    public static boolean isArchiveFile(@Nullable String fileName, @Nullable String contentType) {
        // 检查MIME类型
        if (StringUtils.hasText(contentType)) {
            if (contentType.contains("zip") ||
                contentType.contains("rar") ||
                contentType.contains("7z") ||
                contentType.contains("tar") ||
                contentType.contains("gzip")) {
                return true;
            }
        }
        
        // 检查文件扩展名
        String ext = getFileExtension(fileName);
        return ext.matches("zip|rar|7z|tar|gz");
    }

    /**
     * 根据文件类型获取对应的文件夹名称
     * 
     * @param fileName 文件名
     * @param contentType MIME类型
     * @return 文件夹名称
     */
    public static String getFileFolderByType(@Nullable String fileName, @Nullable String contentType) {
        if (isImageFile(fileName, contentType)) {
            return "images";
        } else if (isAudioFile(fileName, contentType)) {
            return "audios";
        } else if (isVideoFile(fileName, contentType)) {
            return "videos";
        } else if (isDocumentFile(fileName, contentType)) {
            return "documents";
        } else if (isArchiveFile(fileName, contentType)) {
            return "archives";
        }
        return "others";
    }

    /**
     * 过滤文件名，移除危险字符
     * 
     * @param fileName 原始文件名
     * @return 安全的文件名
     */
    public static String sanitizeFileName(@Nullable String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "file";
        }
        
        // 移除路径分隔符和其他危险字符
        String sanitized = fileName.replaceAll("[/\\\\:*?\"<>|]", "_");
        
        // 防止目录穿越
        sanitized = sanitized.replace("..", "_");
        
        // 移除开头和结尾的点和空格
        sanitized = sanitized.trim().replaceAll("^[.\\s]+|[.\\s]+$", "");
        
        // 如果文件名为空或只包含特殊字符，则使用默认名称
        if (!StringUtils.hasText(sanitized)) {
            return "file";
        }
        
        return sanitized;
    }

    /**
     * 生成安全的文件名（时间戳 + 随机数 + 扩展名）
     * 
     * @param originalFileName 原始文件名
     * @return 安全的文件名
     */
    public static String generateSafeFileName(@Nullable String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int) (Math.random() * 10000));
        
        if (StringUtils.hasText(extension)) {
            return timestamp + "_" + random + "." + extension;
        } else {
            return timestamp + "_" + random;
        }
    }

    /**
     * 截断文件名到指定长度，保留扩展名
     * 
     * @param fileName 文件名
     * @param maxLength 最大长度
     * @return 截断后的文件名
     */
    public static String truncateFileName(@NonNull String fileName, int maxLength) {
        if (fileName.length() <= maxLength) {
            return fileName;
        }
        
        String extension = getFileExtension(fileName);
        String nameWithoutExt = getFileNameWithoutExtension(fileName);
        
        if (StringUtils.hasText(extension)) {
            int extensionLength = extension.length() + 1; // +1 for dot
            int maxNameLength = maxLength - extensionLength;
            
            if (maxNameLength > 0) {
                String truncatedName = nameWithoutExt.substring(0, Math.min(nameWithoutExt.length(), maxNameLength));
                return truncatedName + "." + extension;
            }
        }
        
        return fileName.substring(0, maxLength);
    }

    /**
     * 从文件URL中提取相对路径
     * 例如：http://127.0.0.1:9003/file/2025/09/05/filename.pdf 
     * 提取为：2025/09/05/filename.pdf
     * 
     * @param fileUrl 完整的文件URL
     * @return 相对路径，如果提取失败返回null
     */
    public static String extractRelativePathFromUrl(@Nullable String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return null;
        }
        
        try {
            // 查找 "/file/" 的位置
            int fileIndex = fileUrl.indexOf("/file/");
            if (fileIndex != -1) {
                // 提取 "/file/" 之后的部分
                return fileUrl.substring(fileIndex + 6); // 6 是 "/file/".length()
            }
        } catch (Exception e) {
            log.warn("Failed to extract relative path from URL: {}", fileUrl, e);
        }
        
        return null;
    }

    /**
     * 从时间戳文件名中提取日期路径
     * 例如：20240916144702_身份证-背面.jpg 提取为：2024/09/16
     * 
     * @param fileName 包含时间戳的文件名
     * @return 日期路径，如果提取失败返回null
     */
    public static String extractDatePathFromTimestampFileName(@Nullable String fileName) {
        if (!StringUtils.hasText(fileName) || fileName.length() < 8) {
            return null;
        }
        
        try {
            // 提取前8位作为日期
            String dateString = fileName.substring(0, 8);
            
            // 验证是否为有效的日期格式
            if (dateString.matches("\\d{8}")) {
                return dateString.substring(0, 4) + "/" + 
                       dateString.substring(4, 6) + "/" + 
                       dateString.substring(6, 8);
            }
        } catch (Exception e) {
            log.warn("Failed to extract date path from filename: {}", fileName, e);
        }
        
        return null;
    }

    /**
     * 验证文件名是否安全
     * 
     * @param fileName 文件名
     * @return 是否安全
     */
    public static boolean isSafeFileName(@Nullable String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        
        // 检查是否包含路径分隔符
        if (fileName.contains("/") || fileName.contains("\\")) {
            return false;
        }
        
        // 检查是否包含目录穿越
        if (fileName.contains("..")) {
            return false;
        }
        
        // 检查是否只包含安全字符
        return SAFE_FILENAME_PATTERN.matcher(fileName).matches();
    }

    /**
     * 格式化文件大小
     * 
     * @param sizeInBytes 文件大小（字节）
     * @return 格式化后的文件大小字符串
     */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = sizeInBytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        if (unitIndex == 0) {
            return String.format("%.0f %s", size, units[unitIndex]);
        } else {
            return String.format("%.2f %s", size, units[unitIndex]);
        }
    }

    /**
     * 检查文件名是否为Excel文件
     * 
     * @param fileName 文件名
     * @return 如果是Excel文件返回true，否则返回false
     */
    public static boolean isExcelFile(@Nullable String fileName) {
        String ext = getFileExtension(fileName);
        return ext.matches("xlsx|xls");
    }

    /**
     * 检查文件名是否为Word文档
     * 
     * @param fileName 文件名
     * @return 如果是Word文档返回true，否则返回false
     */
    public static boolean isWordDocument(@Nullable String fileName) {
        String ext = getFileExtension(fileName);
        return ext.matches("docx|doc");
    }

    /**
     * 检查文件名是否为PowerPoint文档
     * 
     * @param fileName 文件名
     * @return 如果是PowerPoint文档返回true，否则返回false
     */
    public static boolean isPowerPointDocument(@Nullable String fileName) {
        String ext = getFileExtension(fileName);
        return ext.matches("pptx|ppt");
    }
}