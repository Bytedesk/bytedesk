/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-27 17:37:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 17:38:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.minio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.core.config.properties.BytedeskProperties;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
 * S3 兼容对象存储客户端配置（io.minio 客户端为通用 S3 客户端，可连接
 * MinIO / RustFS / AWS S3 / 阿里云 OSS / 腾讯云 COS 等任意 S3 兼容后端）
 *
 * 启用条件见 {@link S3StorageEnabledCondition}：
 * bytedesk.storage.provider=s3 或旧键 bytedesk.minio.enabled=true
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
@Conditional(S3StorageEnabledCondition.class)
public class MinioConfig {

    private final BytedeskProperties bytedeskProperties;

    /**
     * 初始化 S3 兼容对象存储客户端
     */
    @Bean
    public MinioClient getMinioClient() {
        log.info("初始化 S3 兼容对象存储客户端: endpoint={}, bucket={}", 
                bytedeskProperties.getStorageEndpoint(), 
                bytedeskProperties.getStorageBucketName());
        
        MinioClient minioClient = MinioClient.builder()
                    .endpoint(bytedeskProperties.getStorageEndpoint())
                    .credentials(bytedeskProperties.getStorageAccessKey(), bytedeskProperties.getStorageSecretKey())
                    .build();
        
        log.info("S3 兼容对象存储客户端初始化成功");
        return minioClient;
    }

    
}
