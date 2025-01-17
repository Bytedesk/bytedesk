/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-15 14:08:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 17:24:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bytedesk.core.black.access.VisitorAccessService;

@Component
public class BlackAccessInterceptor implements HandlerInterceptor {

    @Autowired
    private VisitorAccessService visitorAccessService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取visitor ID
        String visitorId = request.getHeader("X-Visitor-ID");
        if (visitorId == null) {
            visitorId = request.getParameter("visitorId");
        }

        // 检查访问权限
        if (visitorId != null && !visitorAccessService.isAllowed(visitorId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for visitor: " + visitorId);
            return false;
        }

        return true;
    }
} 