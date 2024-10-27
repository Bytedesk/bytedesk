/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-17 12:53:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-24 23:46:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * block ip interceptor
 * https://juejin.cn/s/springboot%20ip%E6%8B%A6%E6%88%AA
 */
@Slf4j
@Component
@AllArgsConstructor
public class IpInterceptor implements HandlerInterceptor {
    
    private final IpService ipService;

    // 测试：要拦截的IP列表
    private static final List<String> BLACKLISTED_IPS = Arrays.asList(
            // "175.27.32.31",
            // "112.53.2.93"
    // TODO: 可以根据需要动态配置这个列表，例如从数据库或配置文件中加载
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 获取当前请求的URL
        // String requestURL = request.getRequestURL().toString();
        // String queryString = request.getQueryString(); // 获取URL中的参数部分
        // if (queryString != null) {
        //     requestURL += "?" + queryString; // 将URL和参数拼接起来
        // }
        // log.info("当前请求的完整URL（包括参数）：" + requestURL);

        // // 如果需要获取具体的参数值，可以使用以下方法：
        // String orgUid = request.getParameter("orgUid"); // 将"paramName"替换为实际的参数名
        // log.info("参数'orgUid'的值为：" + orgUid);

        String ip = ipService.getIp(request);
        // 检查IP是否在黑名单中
        if (BLACKLISTED_IPS.contains(ip)) {
            log.warn("Blocked IP address: {}", ip);
            response.sendError(403, "Forbidden: IP address is blacklisted.");
            return false;
        }

        String ipLocation = ipService.getIpLocation(ip);
        log.info("IpInterceptor ip: {}, ipLocation {}", ip, ipLocation);

        // 其他的拦截逻辑可以在这里添加，例如检查用户是否登录，或者请求是否包含某些参数等

        return true;
    }
    
    // @Override
    // public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    //     // 在请求处理之后调用，但在视图渲染之前
    // }

    // @Override
    // public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    //         throws Exception {
    //     // 在整个请求处理完毕之后调用，即视图渲染之后
    // }
    
}
