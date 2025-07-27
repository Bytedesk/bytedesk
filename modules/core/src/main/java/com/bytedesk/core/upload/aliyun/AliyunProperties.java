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
package com.bytedesk.core.upload.aliyun;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperties {

    private String regionId;

    @Value("${aliyun.access.key.id:placeholder}")
    private String accessKeyId;

    @Value("${aliyun.access.key.secret:placeholder}")
    private String accessKeySecret;

    @Value("${aliyun.oss.endpoint:https://oss-cn-shenzhen.aliyuncs.com}")
    private String ossEndpoint;

    @Value("${aliyun.oss.bucket.name:}")
    private String ossBucketName;

    @Value("${aliyun.oss.base.url:https://bytedesk.oss-cn-shenzhen.aliyuncs.com}")
    private String ossBaseUrl;

    @Value("${upload.dir.prefix:}")
    private String uploadDirPrefix;

}
