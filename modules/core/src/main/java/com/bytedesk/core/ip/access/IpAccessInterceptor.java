/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:44:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:14:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.access;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bytedesk.core.ip.IpUtils;

@Component
public class IpAccessInterceptor implements HandlerInterceptor {
    
    @Autowired
    private IpAccessRestService ipAccessService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = IpUtils.getClientIp(request);
        String endpoint = request.getRequestURI();
        
        // 检查是否被封禁
        if (ipAccessService.isIpBlocked(ip)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("IP has been blocked due to excessive requests");
            return false;
        }
        
        // 记录访问
        ipAccessService.recordAccess(ip, endpoint, request.getQueryString());

        return true;
    }
    
    
} 