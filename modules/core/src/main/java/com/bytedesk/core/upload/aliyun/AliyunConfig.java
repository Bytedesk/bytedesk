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
package com.bytedesk.core.upload.aliyun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;

@Configuration
public class AliyunConfig {

    @Autowired
    AliyunProperties aliyunProperties;

    @Bean
    public OSSClient createOSSClient() {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(aliyunProperties.getAccessKeyId(), aliyunProperties.getAccessKeySecret());
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        return new OSSClient(aliyunProperties.getOssEndpoint(), credentialsProvider, clientConfiguration);
    }

    

}
