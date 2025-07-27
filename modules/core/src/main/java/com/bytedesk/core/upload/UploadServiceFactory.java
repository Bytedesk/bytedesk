/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 20:24:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 23:51:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import com.bytedesk.core.upload.aliyun.AliyunUploadService;
import com.bytedesk.core.upload.tencent.TencentUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 上传服务工厂类
 * 根据配置选择不同的云存储提供商
 *
 * @author bytedesk.com
 */
@Component
public class UploadServiceFactory {

    @Value("${bytedesk.upload.provider:aliyun}")
    private String uploadProvider;

    @Autowired(required = false)
    private AliyunUploadService aliyunUploadService;

    @Autowired(required = false)
    private TencentUploadService tencentUploadService;

    /**
     * 获取上传服务实例
     *
     * @return UploadService
     */
    public UploadService getUploadService() {
        // 首先检查是否有任何可用的上传服务
        if (!hasAvailableUploadService()) {
            throw new IllegalStateException("没有可用的云存储服务，请至少启用一个云存储服务（设置 bytedesk.aliyun.enabled=true 或 bytedesk.tencent.enabled=true）");
        }
        
        switch (uploadProvider.toLowerCase()) {
            case "tencent":
            case "cos":
                if (tencentUploadService != null) {
                    return tencentUploadService;
                } else {
                    throw new IllegalStateException("腾讯云COS未启用，请设置 bytedesk.tencent.enabled=true");
                }
            case "aliyun":
            case "oss":
            default:
                if (aliyunUploadService != null) {
                    return aliyunUploadService;
                } else {
                    throw new IllegalStateException("阿里云OSS未启用，请设置 bytedesk.aliyun.enabled=true");
                }
        }
    }

    /**
     * 检查是否有可用的上传服务
     *
     * @return 是否有可用的上传服务
     */
    public boolean hasAvailableUploadService() {
        return aliyunUploadService != null || tencentUploadService != null;
    }

    /**
     * 获取可用的上传服务（如果有的话）
     *
     * @return 可用的上传服务，如果没有则返回null
     */
    public UploadService getAvailableUploadService() {
        if (aliyunUploadService != null) {
            return aliyunUploadService;
        } else if (tencentUploadService != null) {
            return tencentUploadService;
        }
        return null;
    }

    /**
     * 获取当前使用的上传提供商
     *
     * @return 提供商名称
     */
    public String getCurrentProvider() {
        return uploadProvider;
    }

    /**
     * 设置上传提供商
     *
     * @param provider 提供商名称
     */
    public void setUploadProvider(String provider) {
        this.uploadProvider = provider;
    }
} 