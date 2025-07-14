/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-01 13:48:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-01 13:56:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Description;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Description("HTTP REST Service - Service for making HTTP GET and POST requests using RestTemplate")
public class HttpRestService {
    
    private final RestTemplate restTemplate;

    public String httpGet(String url) {
        return restTemplate.getForObject(url, String.class);
    }

    public String httpPost(String url, String requestBody) {
        return restTemplate.postForObject(url, requestBody, String.class);
    }
}
