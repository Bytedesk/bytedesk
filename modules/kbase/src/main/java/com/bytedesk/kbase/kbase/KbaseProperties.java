/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-29 17:18:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-29 23:13:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(KbaseProperties.CONFIG_PREFIX)
public class KbaseProperties {

    public static final String CONFIG_PREFIX = "bytedesk.kbase";

    /**
     * 默认主题：避免未配置时 NPE。
     */
    private String theme = "default";

    /**
     * 静态化输出目录（helpcenter 根目录）。
     *
     * 默认值保持与 starter 的本地配置一致：helpcenter
     */
    private String htmlPath = "helpcenter";
    
    /**
     * 模板中使用的 API Host。
     *
     * 默认值用于本地开发兜底；生产环境建议显式配置。
     */
    private String apiUrl = "http://127.0.0.1:9003";

    /**
     * 兼容扩展配置：
     * - bytedesk.kbase.helpcenter.* 用于 helpcenter
     * - bytedesk.kbase.blog.* 用于 blog
     *
     * 兼容旧配置：
     * - bytedesk.kbase.theme/html-path/api-url 仍然可用，作为 helpcenter 默认值
     * - blog 未配置时，默认使用旧 html-path 的子目录 blog/
     */
    private Helpcenter helpcenter = new Helpcenter();

    private Blog blog = new Blog();

    @Data
    public static class Helpcenter {
        private String theme;
        private String htmlPath;
        private String apiUrl;
    }

    @Data
    public static class Blog {
        private String theme;
        private String htmlPath;
        private String apiUrl;
    }

    // -------------------- helpcenter resolve (fallback to legacy) --------------------
    public String resolveHelpcenterTheme() {
        return StringUtils.hasText(helpcenter.getTheme()) ? helpcenter.getTheme() : theme;
    }

    public String resolveHelpcenterHtmlPath() {
        return StringUtils.hasText(helpcenter.getHtmlPath()) ? helpcenter.getHtmlPath() : htmlPath;
    }

    public String resolveHelpcenterApiUrl() {
        return StringUtils.hasText(helpcenter.getApiUrl()) ? helpcenter.getApiUrl() : apiUrl;
    }

    // -------------------- blog resolve (compatible default) --------------------
    public String resolveBlogTheme() {
        if (StringUtils.hasText(blog.getTheme())) {
            return blog.getTheme();
        }
        // blog 未配置时，复用旧 theme（或 helpcenter 主题）
        return resolveHelpcenterTheme();
    }

    /**
     * 返回 blog 静态文件磁盘根目录（该目录下包含 {kbUid}/...）。
     *
     * - 若配置 bytedesk.kbase.blog.html-path，则它就是 blog 根目录
     * - 否则，使用旧 bytedesk.kbase.html-path + "/blog/" 作为兼容默认
     */
    public String resolveBlogHtmlRootDir() {
        String root;
        if (StringUtils.hasText(blog.getHtmlPath())) {
            root = blog.getHtmlPath();
        } else {
            String hcRoot = resolveHelpcenterHtmlPath();
            root = (hcRoot == null ? null : hcRoot + "/blog");
        }
        if (!StringUtils.hasText(root)) {
            return root;
        }
        return root.endsWith("/") ? root : root + "/";
    }

    public String resolveBlogApiUrl() {
        return StringUtils.hasText(blog.getApiUrl()) ? blog.getApiUrl() : resolveHelpcenterApiUrl();
    }
    
}
