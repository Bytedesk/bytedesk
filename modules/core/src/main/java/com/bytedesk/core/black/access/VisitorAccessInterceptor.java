package com.bytedesk.core.black.access;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VisitorAccessInterceptor implements HandlerInterceptor {

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