/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-29 11:06:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.service;

import com.bytedesk.socket.mqtt.service.MqttAuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * 用户名和密码认证服务
 * 结合数据库授权验证
 *
 * @author jackning
 */
@Slf4j
@Service
@AllArgsConstructor
public class MqttAuthService {

    // private final BCryptPasswordEncoder bCryptPasswordEncoder;
    //
    // private final UserService userService;

    // TODO: 待实现
    public boolean checkValid(String username, String password) {
        log.debug("auth username {}, password {}", username, password);

        // 客户端使用accessToken作为password传递，避免客户端存储密码

        return true;
    }

}
