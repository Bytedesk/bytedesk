/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-17 12:53:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-19 18:50:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * block ip interceptor
 * https://juejin.cn/s/springboot%20ip%E6%8B%A6%E6%88%AA
 */
@Slf4j
@Component
@AllArgsConstructor
public class IpInterceptor implements HandlerInterceptor {
    
    private final IpService ipService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String ip = ipService.getIp(request);
        log.info("IpInterceptor ip: {}", ip);

        // TODO: blacklist check
        // if (ipService.isIpInBlackList(ip)) {
        //     response.sendError(403, "Forbidden");
        //     return false;
        // }

        // TODO: restrict login ip, white ip list
        


        return true;
    }
}
