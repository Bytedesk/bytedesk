/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 11:36:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 13:37:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config.filters;

import java.io.IOException;
import java.util.UUID;

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
import lombok.extern.slf4j.Slf4j;

/**
 * 日志链路跟踪
 * https://developer.aliyun.com/article/1581355?spm=5176.26934562.main.1.67c12f7755gBG5
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String traceId = httpRequest.getHeader(BytedeskConsts.TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(BytedeskConsts.TRACE_ID, traceId);
            httpResponse.setHeader(BytedeskConsts.TRACE_ID, traceId);
            httpResponse.setHeader(BytedeskConsts.TRACE_ID_HTTP_HEADER, traceId);
            exposeHeaderIfNeeded(httpResponse, BytedeskConsts.TRACE_ID);
            exposeHeaderIfNeeded(httpResponse, BytedeskConsts.TRACE_ID_HTTP_HEADER);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(BytedeskConsts.TRACE_ID);
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
