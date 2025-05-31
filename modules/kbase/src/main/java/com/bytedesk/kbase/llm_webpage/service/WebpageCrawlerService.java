/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 09:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 12:09:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.llm_webpage.WebpageEntity;
import com.bytedesk.kbase.llm_webpage.WebpageRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 网页内容抓取服务
 * 负责从网页URL抓取内容并更新WebpageEntity的content字段
 */
@Slf4j
@Service
@AllArgsConstructor
public class WebpageCrawlerService {

    private final WebpageRestService webpageRestService;

    /**
     * 抓取网页内容并更新实体
     * 
     * @param webpage 网页实体
     * @return 更新后的网页实体，如果抓取失败返回原实体
     */
    public WebpageEntity crawlAndUpdateContent(WebpageEntity webpage) {
        if (webpage == null || !StringUtils.hasText(webpage.getUrl())) {
            log.warn("网页实体或URL为空，跳过内容抓取");
            return webpage;
        }

        try {
            // 检查是否已有内容
            if (StringUtils.hasText(webpage.getContent())) {
                log.debug("网页已有内容，跳过抓取: {}", webpage.getUrl());
                return webpage;
            }

            log.info("开始抓取网页内容: {}", webpage.getUrl());
            
            // 使用JSoup抓取网页内容
            Document jsoupDoc = Jsoup.connect(webpage.getUrl())
                    .timeout(10000) // 10秒超时
                    .userAgent("Mozilla/5.0 (compatible; BytedeskBot/1.0; +https://www.bytedesk.com/bot)")
                    .get();

            // 提取标题和内容
            String title = jsoupDoc.title();
            // 移除script、style等标签，只保留有用的文本
            jsoupDoc.select("script, style, meta, link").remove();
            String content = jsoupDoc.body().text();
            
            // 验证内容有效性
            if (!StringUtils.hasText(content)) {
                log.warn("抓取到的内容为空: {}", webpage.getUrl());
                return webpage;
            }
            
            // 更新网页实体
            webpage.setContent(content);
            
            // 如果网页标题为空，使用抓取到的标题
            if (!StringUtils.hasText(webpage.getTitle()) && StringUtils.hasText(title)) {
                webpage.setTitle(title);
            }
            
            // 保存更新后的实体
            WebpageEntity savedWebpage = webpageRestService.save(webpage);
            
            log.info("成功抓取并更新网页内容: {} (内容长度: {})", 
                webpage.getUrl(), content.length());
            
            return savedWebpage;
            
        } catch (Exception e) {
            log.error("抓取网页内容失败: {}, 错误: {}", webpage.getUrl(), e.getMessage(), e);
            return webpage; // 返回原实体，不中断后续处理
        }
    }

    /**
     * 仅抓取网页内容，不保存到数据库
     * 
     * @param url 网页URL
     * @return 抓取到的内容，失败返回null
     */
    public String crawlContent(String url) {
        if (!StringUtils.hasText(url)) {
            log.warn("URL为空，无法抓取内容");
            return null;
        }

        try {
            log.debug("开始抓取网页内容: {}", url);
            
            // 使用JSoup抓取网页内容
            Document jsoupDoc = Jsoup.connect(url)
                    .timeout(10000) // 10秒超时
                    .userAgent("Mozilla/5.0 (compatible; BytedeskBot/1.0; +https://www.bytedesk.com/bot)")
                    .get();

            // 移除script、style等标签，只保留有用的文本
            jsoupDoc.select("script, style, meta, link").remove();
            String content = jsoupDoc.body().text();
            
            if (!StringUtils.hasText(content)) {
                log.warn("抓取到的内容为空: {}", url);
                return null;
            }
            
            log.debug("成功抓取网页内容: {} (内容长度: {})", url, content.length());
            return content;
            
        } catch (Exception e) {
            log.error("抓取网页内容失败: {}, 错误: {}", url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 检查网页是否需要抓取内容
     * 
     * @param webpage 网页实体
     * @return true如果需要抓取，false如果不需要
     */
    public boolean needsCrawling(WebpageEntity webpage) {
        if (webpage == null) {
            return false;
        }
        
        // 没有URL的不需要抓取
        if (!StringUtils.hasText(webpage.getUrl())) {
            return false;
        }
        
        // 已有内容的不需要抓取
        if (StringUtils.hasText(webpage.getContent())) {
            return false;
        }
        
        return true;
    }

    /**
     * 验证抓取到的内容是否有效
     * 
     * @param content 内容
     * @return true如果内容有效，false如果无效
     */
    public boolean isValidContent(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        
        // 检查内容长度，太短的内容可能是错误页面
        if (content.trim().length() < 10) {
            return false;
        }
        
        return true;
    }
}
