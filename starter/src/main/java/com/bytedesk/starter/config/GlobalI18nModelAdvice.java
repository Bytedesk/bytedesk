/*
 * Inject i18n map and lang into all MVC views based on request param or cookie.
 */
package com.bytedesk.starter.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@Component
public class GlobalI18nModelAdvice {

    private static final String DEFAULT_LANG = "zh-CN";

    @ModelAttribute
    public void addI18n(Model model, HttpServletRequest request, HttpServletResponse response) {
        String lang = resolveLang(request);
        // if request explicitly sets lang, refresh cookie
        String reqLang = request.getParameter("lang");
        if (reqLang != null && !reqLang.isBlank()) {
            Cookie c = new Cookie("lang", reqLang);
            c.setPath("/");
            c.setMaxAge(60 * 60 * 24 * 365); // 1 year
            response.addCookie(c);
            lang = reqLang;
        }

        Map<String, String> i18n = new HashMap<>();
        // load base
        mergeProps(i18n, "templates/ftl/i18n/messages_" + lang + ".properties");
        // load meta
        mergeProps(i18n, "templates/ftl/i18n/messages_meta_" + lang + ".properties");

        model.addAttribute("lang", lang);
        model.addAttribute("i18n", i18n);
    }

    private String resolveLang(HttpServletRequest request) {
        String q = request.getParameter("lang");
        if (q != null && !q.isBlank()) {
            return q;
        }
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("lang".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    return c.getValue();
                }
            }
        }
        return DEFAULT_LANG;
    }

    private void mergeProps(Map<String, String> target, String classpathLocation) {
        try {
            ClassPathResource res = new ClassPathResource(classpathLocation);
            if (!res.exists()) {
                return;
            }
            try (java.io.InputStream is = res.getInputStream();
                 java.io.InputStreamReader reader = new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)) {
                Properties p = new Properties();
                p.load(reader);
                for (String name : p.stringPropertyNames()) {
                    target.put(name, p.getProperty(name));
                }
            }
        } catch (Exception e) {
            log.warn("Failed loading i18n properties: {}", classpathLocation, e);
        }
    }

}
