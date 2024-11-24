/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-24 15:26:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-23 13:04:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RobotJsonService {

    @Autowired
    private ResourceLoader resourceLoader;

    // 加载robots.json，智能体列表
    public List<RobotJson> loadRobots() {
        try {
            Resource resource = resourceLoader.getResource("classpath:json/robots.json");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RobotJson.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load robots.json", e);
        }
    }

    // 定义一个Robot类，与JSON结构相匹配
    @Data
    public static class RobotJson {
        private String uid;
        private String nickname;
        // private String avatar;
        private String type;
        private String category;
        private String prompt;
        private String description;
    }

}