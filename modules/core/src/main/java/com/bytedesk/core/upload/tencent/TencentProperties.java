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
package com.bytedesk.core.upload.tencent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "tencent")
public class TencentProperties {

    @Value("${tencent.bucket.location:ap-shanghai}")
    private String bucketLocation;

    @Value("${tencent.bucket.name:}")
    private String bucketName;

    @Value("${tencent.bucket.domain:}")
    private String bucketDomain;

    @Value("${tencent.appid:}")
    private String appId;

    @Value("${tencent.secretid:}")
    private String secretId;

    @Value("${tencent.secretkey:}")
    private String secretKey;

    @Value("${upload.dir.prefix:}")
    private String uploadDirPrefix;

} 