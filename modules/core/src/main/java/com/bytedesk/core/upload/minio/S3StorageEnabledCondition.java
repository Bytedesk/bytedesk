/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-02 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.minio;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * S3 兼容对象存储启用条件：
 * 满足以下任一配置即装配 MinioClient 与 UploadMinioService——
 * 1. bytedesk.storage.provider=s3（推荐，任意 S3 兼容后端：MinIO / RustFS / AWS S3 / OSS / COS）
 * 2. bytedesk.minio.enabled=true（旧键，向后兼容存量部署）
 *
 * 替代原 @ConditionalOnProperty(name = "bytedesk.minio.enabled")，
 * 因后者无法表达「新键 OR 旧键」的或语义。
 */
public class S3StorageEnabledCondition implements Condition {

    public static final String PROPERTY_STORAGE_PROVIDER = "bytedesk.storage.provider";
    public static final String PROPERTY_MINIO_ENABLED = "bytedesk.minio.enabled";
    public static final String PROVIDER_S3 = "s3";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String provider = context.getEnvironment().getProperty(PROPERTY_STORAGE_PROVIDER);
        if (PROVIDER_S3.equalsIgnoreCase(provider)) {
            return true;
        }
        String minioEnabled = context.getEnvironment().getProperty(PROPERTY_MINIO_ENABLED);
        return Boolean.parseBoolean(minioEnabled);
    }
}
