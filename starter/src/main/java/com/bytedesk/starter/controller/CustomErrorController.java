/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-07 17:24:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-07 17:44:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        log.info("handleError: "  + status.toString());

        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404"; // 返回自定义的404页面视图名称，不要加.html扩展名
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403"; // 返回403错误页面
            }
        }
        return "error/error"; // 返回默认的错误页面视图名称，不要加.html扩展名
    }

    public String getErrorPath() {
        log.info("getErrorPath");
        return "/error"; // 定义错误处理的路径
    }
    
}
