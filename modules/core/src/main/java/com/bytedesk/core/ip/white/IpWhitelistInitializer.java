/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 22:23:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 15:35:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.white;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class IpWhitelistInitializer {

    @Autowired
    private IpWhitelistRepository ipWhitelistRepository;

    @PostConstruct
    public void init() {
        if (ipWhitelistRepository.count() > 0) {
            return;
        }
        // 初始化白名单
        IpWhitelistEntity ipWhitelist = IpWhitelistEntity.builder()
            .ip("127.0.0.1")
            .description("127.0.0.1")
            .build();
        ipWhitelistRepository.save(ipWhitelist);
    }
}
