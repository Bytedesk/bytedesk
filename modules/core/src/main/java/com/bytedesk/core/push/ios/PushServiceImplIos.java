/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:30:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-25 15:24:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.ios;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.push.PushNotifier;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PushServiceImplIos extends PushNotifier {

    @Async
    @Override
    public void notify(MessageEntity e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notify'");
    }

    @Override
    public void send(String to, String content, HttpServletRequest request) {

        // TODO: 检测同一个ip是否短时间内有发送过验证码，如果短时间内发送过，则不发送

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

}
