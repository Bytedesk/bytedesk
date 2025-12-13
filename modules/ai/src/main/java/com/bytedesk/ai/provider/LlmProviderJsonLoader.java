/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-24 15:26:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-10 13:17:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LlmProviderJsonLoader {

    @Autowired
    private ResourceLoader resourceLoader;

    public Map<String, ProviderJson> loadProviders() {
        Resource resource = resourceLoader.getResource("classpath:ai/providers.json");
        if (!resource.exists() || !resource.isReadable()) {
            log.warn("providers.json not found on classpath, returning empty provider list");
            return Collections.emptyMap();
        }

        try (InputStream inputStream = resource.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            MapType mapType = objectMapper.getTypeFactory().constructMapType(
                    Map.class,
                    objectMapper.getTypeFactory().constructType(String.class),
                    objectMapper.getTypeFactory().constructType(ProviderJson.class)
            );
            Map<String, ProviderJson> providers = objectMapper.readValue(inputStream, mapType);
            return providers != null ? providers : Collections.emptyMap();
        } catch (IOException e) {
            log.error("Failed to load providers.json", e);
            return Collections.emptyMap();
        }
    }

    @Data
    public static class ProviderJson {
        private String nickname;
        private String logo;
        private String baseUrl;
        private String webUrl;
        private String status;
    }
    

}