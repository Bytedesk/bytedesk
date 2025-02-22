/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 13:37:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 13:38:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class XSSFilter implements Filter {

	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器配置
    }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 对请求进行 XSS 过滤处理
        // 例如：清理请求参数中的恶意脚本代码
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setHeader("Content-Security-Policy", "default-src 'self';");
		chain.doFilter(request, response);
	}

	@Override
    public void destroy() {
        // 销毁过滤器时的清理工作
    }

}
