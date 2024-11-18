/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-24 15:26:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 12:07:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.io.IOException;
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
public class LlmModelJsonService {

    @Autowired
    private ResourceLoader resourceLoader;

    // 加载models.json，模型列表
    public Map<String, List<ModelJson>> loadModels() {
        try {
            Resource resource = resourceLoader.getResource("classpath:json/models.json");
            ObjectMapper objectMapper = new ObjectMapper();
            MapType mapType = objectMapper.getTypeFactory().constructMapType(
                    Map.class, // 或者使用具体的Map实现类，如LinkedHashMap.class
                    objectMapper.getTypeFactory().constructType(String.class), // key的类型
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ModelJson.class) // value的类型，即List<ModelJson>
            );
            // 使用getInputStream()而不是getFile()
            Map<String, List<ModelJson>> models = objectMapper.readValue(resource.getInputStream(), mapType);
            // for (Map.Entry<String, List<ModelJson>> entry : models.entrySet()) {
            //     String key = entry.getKey();
            //     // log.info("loadModels key {} value {}", key, entry.getValue());
            //     List<ModelJson> values = entry.getValue(); //(List<ModelJson>) entry.getValue();
            //     StringBuilder sb = new StringBuilder();
            //     for (ModelJson model : values) {
            //         // sb.append(objectMapper.writeValueAsString(model));
            //         sb.append(model.getName());
            //         sb.append("\n");
            //     }
            //     log.info("loadModels key: {} value: {}", key, sb.toString());
            // }
            return models;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load providers.json", e);
        }
    }

    // 
    @Data
    public static class ModelJson {
        private String name;
        private String nickname;
    }

}