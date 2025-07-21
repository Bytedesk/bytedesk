/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 11:37:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 09:27:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.airline;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class AirlineService {
    
}
