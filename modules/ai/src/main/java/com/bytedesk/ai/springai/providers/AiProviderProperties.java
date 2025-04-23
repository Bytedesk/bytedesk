/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-16 11:20:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 10:32:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.ai")
public class AiProviderProperties {
    
    /**
     * 指定要使用的AI提供商的名称
     * 可选值：baidu, deepseek, gitee, siliconFlow, tencent, volcengine
     */
    private String provider = "volcengine"; // 默认提供商
}
