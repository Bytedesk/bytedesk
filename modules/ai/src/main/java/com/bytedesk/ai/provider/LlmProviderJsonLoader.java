/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-24 15:26:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 17:21:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.io.IOException;
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
        try {
            Resource resource = resourceLoader.getResource("classpath:ai/providers.json");
            ObjectMapper objectMapper = new ObjectMapper();
            MapType mapType = objectMapper.getTypeFactory().constructMapType(
                    Map.class, // 或者使用具体的Map实现类，如LinkedHashMap.class
                    objectMapper.getTypeFactory().constructType(String.class), // key的类型
                    objectMapper.getTypeFactory().constructType(ProviderJson.class) // value的类型
            );
            // 使用getInputStream()而不是getFile()
            Map<String, ProviderJson> providers = objectMapper.readValue(resource.getInputStream(), mapType);
            // 你可以遍历providers映射，并访问每个Provider对象的属性
            // for (Map.Entry<String, ProviderJson> entry : providers.entrySet()) {
            //     String key = entry.getKey();
            //     // log.info("loadProviders key: {} value: {}", key, entry.getValue());
            //     ProviderJson provider = entry.getValue();
            //     log.info("loadProviders key {} api.url {}", key, provider.getApi().getUrl());
            // }
            return providers;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load providers.json", e);
        }
    }

    @Data
    public static class ProviderJson {
        private String nickname;
        private String logo;
        private String apiUrl;
        private String webUrl;
        private String status;
    }
    

}