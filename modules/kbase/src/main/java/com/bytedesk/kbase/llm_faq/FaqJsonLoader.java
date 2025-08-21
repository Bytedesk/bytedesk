/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-04 15:36:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 15:41:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk  
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * FAQ JSON文件加载器
 * 用于从JSON文件中加载FAQ数据并解析成对象
 */
@Slf4j
@Service
public class FaqJsonLoader {
    
    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 加载FAQ配置
     * 从classpath:kbasedemo/faq.json文件中加载FAQ数据
     * 
     * @return FaqConfiguration FAQ配置对象
     * @throws RuntimeException 如果加载失败
     */
    public FaqConfiguration loadFaqs() {
        try {
            Resource resource = resourceLoader.getResource("classpath:kbasedemo/faq.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(resource.getInputStream(), FaqConfiguration.class);
        } catch (IOException e) {
            log.error("Failed to load faq.json", e);
            throw new RuntimeException("Failed to load faq.json", e);
        }
    }

    /**
     * FAQ配置类
     * 对应JSON文件的根节点
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaqConfiguration {
        private List<Faq> faqs;
    }

    /**
     * FAQ实体类
     * 对应JSON文件中的每个FAQ条目
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Faq {
        private String uid;
        private String question;
        private String answer;
    }
}
