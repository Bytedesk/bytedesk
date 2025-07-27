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

    @Value("${upload.provider:aliyun}")
    private String uploadProvider;

    @Autowired
    private AliyunUploadService aliyunUploadService;

    @Autowired
    private TencentUploadService tencentUploadService;

    /**
     * 获取上传服务实例
     *
     * @return UploadService
     */
    public UploadService getUploadService() {
        switch (uploadProvider.toLowerCase()) {
            case "tencent":
            case "cos":
                return tencentUploadService;
            case "aliyun":
            case "oss":
            default:
                return aliyunUploadService;
        }
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