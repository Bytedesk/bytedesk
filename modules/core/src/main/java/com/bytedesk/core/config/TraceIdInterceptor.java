/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 12:45:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-19 12:50:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.bytedesk.core.constant.BytedeskConsts;

public class TraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String traceId = MDC.get(BytedeskConsts.TRACE_ID);
        if (traceId != null) {
            request.getHeaders().add(BytedeskConsts.TRACE_ID, traceId);
            request.getHeaders().add(BytedeskConsts.TRACE_ID_HTTP_HEADER, traceId);
        }

        String requestId = MDC.get(BytedeskConsts.REQUEST_ID_MDC);
        if (requestId != null) {
            request.getHeaders().add(BytedeskConsts.REQUEST_ID, requestId);
        }
        return execution.execute(request, body);
    }
}