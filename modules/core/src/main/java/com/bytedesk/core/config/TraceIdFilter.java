/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 11:36:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-19 12:47:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BdConstants;

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
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader(BdConstants.TRACE_ID);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        // 设置到ThreadLocal，方便后续在业务代码中获取
        MDC.put(BdConstants.TRACE_ID, traceId);

        // 添加到响应头，便于下游服务获取
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader(BdConstants.TRACE_ID, traceId);

        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        log.info("TraceIdFilter destroy");
        // 请求处理完成后，清理ThreadLocal中存储的traceId
        // MDC.clear();
        MDC.remove(BdConstants.TRACE_ID);
    }
    
}
