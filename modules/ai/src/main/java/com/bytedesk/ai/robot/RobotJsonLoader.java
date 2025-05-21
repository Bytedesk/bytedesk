/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-24 15:26:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-27 09:17:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

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

@Slf4j
@Service
public class RobotJsonLoader {

    @Autowired
    private ResourceLoader resourceLoader;

    public RobotConfiguration loadRobots() {
        try {
            Resource resource = resourceLoader.getResource("classpath:ai/robots.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(resource.getInputStream(), RobotConfiguration.class);
        } catch (IOException e) {
            log.error("Failed to load robots.json", e);
            throw new RuntimeException("Failed to load robots.json", e);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RobotConfiguration {
        private List<Robot> robots;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Robot {
        private String uid;
        private String name;
        private String type;
        private String category;
        private I18n i18n;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class I18n {
        private LocaleData en;
        private LocaleData zh_cn;
        private LocaleData zh_tw;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocaleData {
        private String nickname;
        private String prompt;
        private String description;
    }
}