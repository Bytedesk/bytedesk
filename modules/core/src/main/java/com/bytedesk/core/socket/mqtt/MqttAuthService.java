/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 12:45:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.bytedesk.core.socket.mqtt.MqttAuthService;

@Slf4j
@Service
@AllArgsConstructor
public class MqttAuthService {

    // private final UserService userService;

    public Boolean checkValid(String username, String password) {
        // 客户端使用accessToken作为password传递，避免客户端存储密码
        String accessToken = password;
        log.debug("auth username {}, accessToken {}", username, accessToken);
        // if (!userService.checkToken(username, accessToken)) {
        //     return false;
        // }

        return true;
    }

}
