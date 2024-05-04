/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:30:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-03 20:30:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bytedesk.core.message.Message;

import lombok.extern.slf4j.Slf4j;

/**
 * https://springdoc.cn/spring-boot-email/
 * https://springdoc.cn/spring/integration.html#mail
 * https://mailtrap.io/blog/spring-send-email/
 */
@Slf4j
@Service
public class PushServiceImplEmail extends Notifier {


    @Async
    @Override
    void notify(Message e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notify'");
    }

    @Async
    @Override
    void send(String to, String content) {
        log.info("TODO: send email to {}, content {}", to, content);
        
        
    }
    
}
