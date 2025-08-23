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

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

/**
 * 文件上传安全日志记录器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UploadSecurityLogger {

    private final AuthService authService;
    private final UploadSecurityConfig uploadSecurityConfig;

    /**
     * 记录文件上传成功日志
     */
    public void logUploadSuccess(MultipartFile file, UploadRequest request, String fileUrl, HttpServletRequest httpRequest) {
        if (!uploadSecurityConfig.isEnableUploadLog()) {
            return;
        }

        UserEntity user = authService.getUser();
        String userInfo = user != null ? 
            String.format("用户[%s:%s]", user.getUid(), user.getNickname()) :
            String.format("访客[%s:%s]", request.getVisitorUid(), request.getVisitorNickname());
        
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : "Unknown";
        
        log.info("文件上传成功 - {} 上传文件: 原始名称[{}], 存储名称[{}], 大小[{}], 类型[{}], IP[{}], UA[{}], URL[{}]",
            userInfo,
            file.getOriginalFilename(),
            request.getFileName(),
            formatFileSize(file.getSize()),
            file.getContentType(),
            clientIp,
            userAgent,
            fileUrl
        );
    }

    /**
     * 记录文件上传失败日志
     */
    public void logUploadFailure(MultipartFile file, UploadRequest request, String errorMessage, HttpServletRequest httpRequest) {
        if (!uploadSecurityConfig.isEnableUploadLog()) {
            return;
        }

        UserEntity user = authService.getUser();
        String userInfo = user != null ? 
            String.format("用户[%s:%s]", user.getUid(), user.getNickname()) :
            String.format("访客[%s:%s]", request.getVisitorUid(), request.getVisitorNickname());
        
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : "Unknown";
        
        log.warn("文件上传失败 - {} 上传文件: 原始名称[{}], 大小[{}], 类型[{}], IP[{}], UA[{}], 错误原因[{}]",
            userInfo,
            file.getOriginalFilename(),
            formatFileSize(file.getSize()),
            file.getContentType(),
            clientIp,
            userAgent,
            errorMessage
        );
    }

    /**
     * 记录安全威胁日志
     */
    public void logSecurityThreat(MultipartFile file, UploadRequest request, String threatType, String description, HttpServletRequest httpRequest) {
        UserEntity user = authService.getUser();
        String userInfo = user != null ? 
            String.format("用户[%s:%s]", user.getUid(), user.getNickname()) :
            String.format("访客[%s:%s]", request.getVisitorUid(), request.getVisitorNickname());
        
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : "Unknown";
        
        log.error("检测到安全威胁 - {} 尝试上传恶意文件: 威胁类型[{}], 文件名[{}], 大小[{}], 类型[{}], IP[{}], UA[{}], 描述[{}]",
            userInfo,
            threatType,
            file.getOriginalFilename(),
            formatFileSize(file.getSize()),
            file.getContentType(),
            clientIp,
            userAgent,
            description
        );
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        
        return ip;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size >= 1024 * 1024) {
            return String.format("%.2fMB", size / (1024.0 * 1024.0));
        } else if (size >= 1024) {
            return String.format("%.2fKB", size / 1024.0);
        } else {
            return size + "B";
        }
    }
}
