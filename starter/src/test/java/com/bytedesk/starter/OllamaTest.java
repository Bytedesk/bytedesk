/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 11:23:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 10:32:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OllamaTest {
    
    // @Autowired
    // private OllamaChatModel chatModel;

    @Test
    void ollamaChat() {
        // ChatResponse response = chatModel.call(
        //         new Prompt(
        //                 "Spring Boot适合做什么？",
        //                 OllamaOptions.builder()
        //                         .withModel(OllamaModel.MISTRAL)
        //                         .withTemperature(0.4)
        //                         .build()
        //         ));
        // System.out.println(response);
    }
}
