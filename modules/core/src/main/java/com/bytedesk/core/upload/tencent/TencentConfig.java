/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 20:24:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 23:24:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.tencent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;

@Configuration
public class TencentConfig {

    @Autowired
    TencentProperties tencentProperties;

    @Bean
    public COSClient createCOSClient() {
        COSCredentials credentials = new BasicCOSCredentials(
            tencentProperties.getSecretId(), 
            tencentProperties.getSecretKey()
        );
        ClientConfig clientConfig = new ClientConfig(new Region(tencentProperties.getBucketLocation()));
        return new COSClient(credentials, clientConfig);
    }

} 