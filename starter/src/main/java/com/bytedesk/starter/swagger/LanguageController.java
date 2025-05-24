/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 11:55:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 语言切换控制器
 */
@Controller
@RequestMapping("/language")
public class LanguageController {

    /**
     * 切换语言
     * @param request HTTP请求
     * @param response HTTP响应
     * @param lang 语言代码：en - 英语, zh_CN - 简体中文, zh_TW - 繁体中文
     * @param redirectUrl 重定向URL
     * @throws IOException
     */
    @GetMapping("/switch")
    public void switchLanguage(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "lang", defaultValue = "en") String lang,
            @RequestParam(name = "redirectUrl", defaultValue = "/swagger-ui/index.html") String redirectUrl) throws IOException {
        
        // 重定向到原始页面，并添加语言参数
        response.sendRedirect(redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "lang=" + lang);
    }
}
