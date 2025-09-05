/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 16:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website.crawl;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网站抓取配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlConfig {
    
    /**
     * 抓取深度（1-5层）
     */
    @Builder.Default
    private int maxDepth = 3;
    
    /**
     * 最大抓取页面数
     */
    @Builder.Default
    private int maxPages = 1000;
    
    /**
     * 并发线程数
     */
    @Builder.Default
    private int concurrentThreads = 3;
    
    /**
     * 请求超时时间（毫秒）
     */
    @Builder.Default
    private int timeout = 10000;
    
    /**
     * 请求间隔延迟（毫秒）
     */
    @Builder.Default
    private int delay = 1000;
    
    /**
     * 用户代理
     */
    @Builder.Default
    private String userAgent = "Mozilla/5.0 (compatible; BytedeskBot/1.0; +https://www.bytedesk.com/bot)";
    
    /**
     * 是否跟随链接
     */
    @Builder.Default
    private boolean followLinks = true;
    
    /**
     * 是否使用sitemap
     */
    @Builder.Default
    private boolean useSitemap = true;
    
    /**
     * sitemap URL（可选）
     */
    private String sitemapUrl;
    
    /**
     * 包含的URL模式（正则表达式）
     */
    private List<String> includePatterns;
    
    /**
     * 排除的URL模式（正则表达式）
     */
    private List<String> excludePatterns;
    
    /**
     * 最小内容长度
     */
    @Builder.Default
    private int minContentLength = 50;
    
    /**
     * 是否去重
     */
    @Builder.Default
    private boolean deduplication = true;
    
    /**
     * 是否支持断点续传
     */
    @Builder.Default
    private boolean resumable = true;
    
    /**
     * 抓取优先级（1-10，数字越大优先级越高）
     */
    @Builder.Default
    private int priority = 5;
    
    /**
     * 是否抓取图片
     */
    @Builder.Default
    private boolean crawlImages = false;
    
    /**
     * 是否抓取PDF文档
     */
    @Builder.Default
    private boolean crawlPdfs = false;
    
    /**
     * 验证配置有效性
     */
    public boolean isValid() {
        return maxDepth >= 1 && maxDepth <= 5 
            && maxPages > 0 && maxPages <= 10000
            && concurrentThreads >= 1 && concurrentThreads <= 10
            && timeout > 0 && timeout <= 60000
            && delay >= 0 && delay <= 10000
            && minContentLength >= 0;
    }
    
    /**
     * 获取默认配置
     */
    public static CrawlConfig getDefault() {
        return CrawlConfig.builder().build();
    }
    
    /**
     * 获取快速配置（较少深度和页面数）
     */
    public static CrawlConfig getFast() {
        return CrawlConfig.builder()
            .maxDepth(2)
            .maxPages(100)
            .concurrentThreads(2)
            .delay(500)
            .build();
    }
    
    /**
     * 获取深度配置（更大深度和页面数）
     */
    public static CrawlConfig getDeep() {
        return CrawlConfig.builder()
            .maxDepth(5)
            .maxPages(5000)
            .concurrentThreads(5)
            .delay(2000)
            .build();
    }
}
