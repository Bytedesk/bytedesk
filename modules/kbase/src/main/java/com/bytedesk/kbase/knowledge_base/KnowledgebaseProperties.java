/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-29 17:18:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-29 23:13:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(KnowledgebaseProperties.CONFIG_PREFIX)
public class KnowledgebaseProperties {

    public static final String CONFIG_PREFIX = "bytedesk.kbase";

    private String theme;

    private String htmlPath;
    
    // private String templatePath;

    private String apiUrl;
    
}
