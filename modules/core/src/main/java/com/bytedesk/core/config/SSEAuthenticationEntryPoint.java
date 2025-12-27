/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-13 10:51:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 16:19:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.slf4j.Logger;
// import org.springframework.http.MediaType;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.AuthenticationEntryPoint;

// import java.io.IOException;

// /**
//  * 自定义的SSE认证入口点
//  * 用于处理SSE请求中的认证异常
//  */
// public class SSEAuthenticationEntryPoint implements AuthenticationEntryPoint {


//     @Override
//     public void commence(HttpServletRequest request, HttpServletResponse response,
//                          AuthenticationException authException) throws IOException, ServletException {
        
//         logger.debug("SSE authentication error: {}", authException.getMessage());
        
//         // 检查响应是否已经提交
//         if (!response.isCommitted()) {
//             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//             response.setContentType(MediaType.TEXT_PLAIN_VALUE);
//             response.getWriter().write("Authentication failed: " + authException.getMessage());
//         } else {
//             logger.warn("Cannot send SSE authentication error - response already committed");
//         }
//     }
// }
