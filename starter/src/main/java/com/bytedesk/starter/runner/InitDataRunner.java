/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 22:36:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.runner;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.bytedesk.core.utils.NetworkUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bytedesk.com on 2019/4/21
 */
@Slf4j
@Component
public class InitDataRunner implements ApplicationRunner {

    @Value("${application.version}")
    private String version;
    
    @Value("${server.port}")
    private String port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 在应用的主类或配置类中
        // TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        // TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        String localIP = NetworkUtils.getFirstNonLoopbackIP();
        List<String> allIPs = NetworkUtils.getLocalIPs();

        log.info("bytedesk.im v{} started at:", version);
        log.info("Local Access:  http://127.0.0.1:{}", port);
        log.info("Network Access: http://{}:{}", localIP, port);
        
        if (allIPs.size() > 1) {
            log.info("Other Network IPs:");
            allIPs.stream()
                .filter(ip -> !ip.equals(localIP))
                .forEach(ip -> log.info("http://{}:{}", ip, port));
        }
    }

}
