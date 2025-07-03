/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-15 14:08:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 17:36:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerInterceptor;

// @Slf4j
// @Component
// public class BlackAccessInterceptor implements HandlerInterceptor {

//     // @Autowired
//     // private BlackRestService blackRestService;

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//             throws Exception {

//         // String uid = null;
//         // String orgUid = null;
//         // String endpoint = request.getRequestURI();
//         // 获取请求方法
//         // String method = request.getMethod();

//         // log.info("BlackAccessInterceptor endpoint: {}, uid: {}, orgUid: {}", endpoint, uid, orgUid);

//         // 如果缺少必要参数，直接放行
//         // if (uid == null || orgUid == null) {
//         //     return true;
//         // }

//         // 检查是否在黑名单中
//         // Optional<BlackEntity> blackOpt = blackRestService.findByVisitorUidAndOrgUid(uid, orgUid);
//         // if (blackOpt.isPresent()) {
//         //     BlackEntity black = blackOpt.get();
//         //     // 检查黑名单是否在有效期内
//         //     if (black.getEndTime() == null ||
//         //             black.getEndTime().isAfter(ZonedDateTime.now())) {
//         //         response.sendError(
//         //                 HttpServletResponse.SC_FORBIDDEN,
//         //                 "Access denied for visitor: " + uid + " in org: " + orgUid);
//         //         return false;
//         //     }
//         // }

//         return true;
//     }
// }