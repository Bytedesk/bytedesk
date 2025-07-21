/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 10:55:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 11:18:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.shopping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/springai/demo/shopping")
public class SpringAIShoppingController {

    @GetMapping("/chat")
    public String chat() {
        return "Hello, World!";
    }

}
