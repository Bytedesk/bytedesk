/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-24 15:26:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-17 14:48:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
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
public class LlmModelJsonLoader {

    @Autowired
    private ResourceLoader resourceLoader;

    // 加载models.json，模型列表
    public Map<String, List<ModelJson>> loadModels() {
        Resource resource = resourceLoader.getResource("classpath:ai/models.json");
        if (!resource.exists() || !resource.isReadable()) {
            log.warn("models.json not found on classpath, returning empty model list");
            return Collections.emptyMap();
        }

        try (InputStream inputStream = resource.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            MapType mapType = objectMapper.getTypeFactory().constructMapType(
                    Map.class,
                    objectMapper.getTypeFactory().constructType(String.class),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ModelJson.class)
            );
            Map<String, List<ModelJson>> models = objectMapper.readValue(inputStream, mapType);
            return models != null ? models : Collections.emptyMap();
        } catch (IOException e) {
            log.error("Failed to load models.json", e);
            return Collections.emptyMap();
        }
    }

    // 
    @Data
    public static class ModelJson {
        private String name;
        private String nickname;
        private String description;
        private String type;
    }

}