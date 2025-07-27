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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.core.config.properties.BytedeskProperties;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "bytedesk.minio.enabled", havingValue = "true", matchIfMissing = false)
public class MinioConfig {

    @Autowired
    private BytedeskProperties bytedeskProperties;

    /**
     * 初始化 MinIO 客户端
     */
    @Bean
    public MinioClient getMinioClient() {
        log.info("初始化 MinIO 客户端: endpoint={}, bucket={}", 
                bytedeskProperties.getMinioEndpoint(), 
                bytedeskProperties.getMinioBucketName());
        
        MinioClient minioClient = MinioClient.builder()
                    .endpoint(bytedeskProperties.getMinioEndpoint())
                    .credentials(bytedeskProperties.getMinioAccessKey(), bytedeskProperties.getMinioSecretKey())
                    .build();
        
        log.info("MinIO 客户端初始化成功");
        return minioClient;
    }

    
}
