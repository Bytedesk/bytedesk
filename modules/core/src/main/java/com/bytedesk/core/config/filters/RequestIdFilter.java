/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.config.filters;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HTTP request correlation id.
 *
 * - If client sends X-Request-Id, reuse it (sanitized)
 * - Otherwise generate a new one
 * - Put into MDC for logging and echo back in response header
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements Filter {

    private static final Pattern SAFE_REQUEST_ID = Pattern.compile("^[a-zA-Z0-9._:-]{1,64}$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestId = httpRequest.getHeader(BytedeskConsts.REQUEST_ID);
        if (requestId == null || requestId.isBlank() || !SAFE_REQUEST_ID.matcher(requestId).matches()) {
            requestId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(BytedeskConsts.REQUEST_ID_MDC, requestId);
            httpResponse.setHeader(BytedeskConsts.REQUEST_ID, requestId);
            exposeHeaderIfNeeded(httpResponse, BytedeskConsts.REQUEST_ID);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(BytedeskConsts.REQUEST_ID_MDC);
        }
    }

    private static void exposeHeaderIfNeeded(HttpServletResponse response, String headerName) {
        String existing = response.getHeader("Access-Control-Expose-Headers");
        if (existing == null || existing.isBlank()) {
            response.setHeader("Access-Control-Expose-Headers", headerName);
            return;
        }
        String lc = existing.toLowerCase();
        if (!lc.contains(headerName.toLowerCase())) {
            response.setHeader("Access-Control-Expose-Headers", existing + ", " + headerName);
        }
    }
}
