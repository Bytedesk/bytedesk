/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-17 17:37:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author bytedesk.com on 2019-08-01
 */
@Configuration
@Import({ProtobufHttpMessageConverter.class})
@Description("Protocol Buffers Configuration - Protocol Buffers message converter configuration")
public class ProtoConfig {

    /**
     * protobuf 序列化
     */
    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

    /**
     * protobuf 反序列化
     */
    @Bean
    RestTemplate protobufRestTemplate(ProtobufHttpMessageConverter protobufHttpMessageConverter) {
        return new RestTemplate(Collections.singletonList(protobufHttpMessageConverter));
    }

}
