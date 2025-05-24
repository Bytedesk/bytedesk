/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 12:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 12:00:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.config;

import java.io.IOException;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 浏览器语言检测过滤器
 * 优先使用URL参数中的语言设置，其次使用浏览器语言设置，最后使用默认语言
 */
public class BrowserLanguageFilter extends OncePerRequestFilter {

    private final LocaleResolver localeResolver;

    public BrowserLanguageFilter(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // 检查URL参数是否包含语言设置
        String langParam = request.getParameter("lang");
        if (langParam != null && !langParam.isEmpty()) {
            // 将语言参数转换为Locale对象
            Locale locale = parseLocale(langParam);
            localeResolver.setLocale(request, response, locale);
            LocaleContextHolder.setLocale(locale);
        } else {
            // 获取浏览器的语言设置
            Locale browserLocale = getBrowserLocale(request);
            if (browserLocale != null) {
                localeResolver.setLocale(request, response, browserLocale);
                LocaleContextHolder.setLocale(browserLocale);
            }
        }
        
        // 继续处理请求
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取浏览器语言设置
     */
    private Locale getBrowserLocale(HttpServletRequest request) {
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage == null || acceptLanguage.isEmpty()) {
            return null;
        }
        
        // 取第一个语言设置
        String primaryLang = acceptLanguage.split(",")[0];
        return parseLocale(primaryLang);
    }

    /**
     * 解析语言字符串为Locale对象
     */
    private Locale parseLocale(String localeStr) {
        if (localeStr == null || localeStr.isEmpty()) {
            return Locale.getDefault();
        }
        
        // 支持的语言：en、zh_CN、zh_TW
        if ("en".equalsIgnoreCase(localeStr)) {
            return Locale.ENGLISH;
        } else if ("zh_CN".equalsIgnoreCase(localeStr) || "zh-CN".equalsIgnoreCase(localeStr)) {
            return Locale.SIMPLIFIED_CHINESE;
        } else if ("zh_TW".equalsIgnoreCase(localeStr) || "zh-TW".equalsIgnoreCase(localeStr)) {
            return Locale.TRADITIONAL_CHINESE;
        } else if (localeStr.startsWith("zh")) {
            return Locale.SIMPLIFIED_CHINESE; // 默认中文使用简体中文
        }
        
        // 尝试标准解析
        try {
            String[] parts = localeStr.split("[_-]");
            if (parts.length > 1) {
                return new Locale(parts[0], parts[1]);
            } else {
                return new Locale(parts[0]);
            }
        } catch (Exception e) {
            return Locale.getDefault();
        }
    }
}
