/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 16:53:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.llm_webpage.WebpageEntity;
import com.bytedesk.kbase.llm_webpage.WebpageRepository;
import com.bytedesk.kbase.llm_website.WebsiteEntity;
import com.bytedesk.kbase.llm_website.WebsiteRepository;
import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlConfig;
import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlResult;
import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlStatus;
import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlTask;
import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlTaskRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 网站整站抓取服务
 * 负责从指定的网站根域名开始，按照设定的抓取深度和规则，批量抓取整个网站内容
 */
@Slf4j
@Service
@AllArgsConstructor
public class WebsiteCrawlerService {

    private final WebsiteRepository websiteRepository;
    private final WebpageRepository webpageRepository;
    private final WebsiteCrawlTaskRepository crawlTaskRepository;
    private final UidUtils uidUtils;

    // 线程池，用于并发抓取
    private final ExecutorService crawlExecutor = Executors.newFixedThreadPool(5);
    
    // 存储正在进行的抓取任务
    private final ConcurrentHashMap<String, WebsiteCrawlTask> activeTasks = new ConcurrentHashMap<>();

    /**
     * 开始整站抓取
     * 
     * @param websiteUid 网站UID
     * @param config 抓取配置
     * @return 抓取任务
     */
    @Async
    public CompletableFuture<WebsiteCrawlResult> startCrawl(String websiteUid, WebsiteCrawlConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                WebsiteEntity website = websiteRepository.findByUid(websiteUid)
                    .orElseThrow(() -> new RuntimeException("Website not found: " + websiteUid));
                
                // 创建抓取任务
                WebsiteCrawlTask task = createCrawlTask(website, config);
                activeTasks.put(task.getTaskId(), task);
                
                log.info("开始整站抓取: {} (任务ID: {})", website.getUrl(), task.getTaskId());
                
                // 执行抓取
                WebsiteCrawlResult result = performCrawl(website, task, config);
                
                // 更新任务状态
                task.setStatus(result.isSuccess() ? WebsiteCrawlStatus.COMPLETED : WebsiteCrawlStatus.FAILED);
                task.setEndTime(System.currentTimeMillis());
                saveCrawlTask(task);
                
                activeTasks.remove(task.getTaskId());
                
                log.info("整站抓取完成: {} (成功: {}, 总页面: {})", 
                    website.getUrl(), result.isSuccess(), result.getTotalPages());
                
                return result;
                
            } catch (Exception e) {
                log.error("整站抓取失败", e);
                return WebsiteCrawlResult.failure("抓取失败: " + e.getMessage());
            }
        }, crawlExecutor);
    }

    /**
     * 执行抓取任务
     */
    private WebsiteCrawlResult performCrawl(WebsiteEntity website, WebsiteCrawlTask task, WebsiteCrawlConfig config) {
        try {
            Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
            Set<String> urlsToVisit = Collections.synchronizedSet(new HashSet<>());
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);
            
            // 添加起始URL
            urlsToVisit.add(website.getUrl());
            
            // 多层级抓取
            for (int depth = 0; depth < config.getMaxDepth(); depth++) {
                if (urlsToVisit.isEmpty()) {
                    break;
                }
                
                log.info("开始抓取第 {} 层，待处理URL数量: {}", depth + 1, urlsToVisit.size());
                
                Set<String> currentLevelUrls = new HashSet<>(urlsToVisit);
                urlsToVisit.clear();
                
                // 并发处理当前层级的URL
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                
                for (String url : currentLevelUrls) {
                    if (visitedUrls.contains(url)) {
                        continue;
                    }
                    
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            if (crawlSinglePage(url, website, visitedUrls, urlsToVisit, config)) {
                                successCount.incrementAndGet();
                            } else {
                                failureCount.incrementAndGet();
                            }
                            
                            // 更新任务进度
                            task.setProcessedPages(successCount.get() + failureCount.get());
                            
                        } catch (Exception e) {
                            log.error("抓取单页失败: {}", url, e);
                            failureCount.incrementAndGet();
                        }
                    }, crawlExecutor);
                    
                    futures.add(future);
                    
                    // 控制并发数
                    if (futures.size() >= config.getConcurrentThreads()) {
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                        futures.clear();
                    }
                }
                
                // 等待当前层级的所有任务完成
                if (!futures.isEmpty()) {
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                }
                
                // 检查是否达到最大页面数限制
                if (successCount.get() >= config.getMaxPages()) {
                    log.info("达到最大页面数限制: {}", config.getMaxPages());
                    break;
                }
                
                // 移除已访问的URL
                urlsToVisit.removeAll(visitedUrls);
            }
            
            return WebsiteCrawlResult.success(successCount.get(), failureCount.get());
            
        } catch (Exception e) {
            log.error("执行抓取任务失败", e);
            return WebsiteCrawlResult.failure("执行抓取失败: " + e.getMessage());
        }
    }

    /**
     * 抓取单个页面
     */
    private boolean crawlSinglePage(String url, WebsiteEntity website, 
                                  Set<String> visitedUrls, Set<String> urlsToVisit, 
                                  WebsiteCrawlConfig config) {
        try {
            if (visitedUrls.contains(url)) {
                return false;
            }
            
            visitedUrls.add(url);
            
            log.debug("抓取页面: {}", url);
            
            // 使用JSoup抓取页面
            Document doc = Jsoup.connect(url)
                .timeout(config.getTimeout())
                .userAgent(config.getUserAgent())
                .followRedirects(true)
                .get();
            
            // 提取页面内容
            String title = doc.title();
            String description = extractDescription(doc);
            
            // 移除script、style等标签，只保留有用的文本
            doc.select("script, style, meta, link, nav, footer, header").remove();
            String content = doc.body().text();
            
            // 验证内容有效性
            if (!isValidContent(content, config)) {
                log.warn("页面内容无效，跳过: {}", url);
                return false;
            }
            
            // 创建或更新WebpageEntity
            createOrUpdateWebpage(url, title, description, content, website);
            
            // 提取链接用于下一层抓取
            if (config.isFollowLinks()) {
                extractLinks(doc, url, website.getUrl(), urlsToVisit, config);
            }
            
            // 添加延迟，避免过于频繁的请求
            if (config.getDelay() > 0) {
                Thread.sleep(config.getDelay());
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("抓取单页失败: {}", url, e);
            return false;
        }
    }

    /**
     * 提取页面描述
     */
    private String extractDescription(Document doc) {
        // 尝试从meta标签获取描述
        Element metaDesc = doc.select("meta[name=description]").first();
        if (metaDesc != null && StringUtils.hasText(metaDesc.attr("content"))) {
            return metaDesc.attr("content");
        }
        
        // 尝试从Open Graph获取描述
        Element ogDesc = doc.select("meta[property=og:description]").first();
        if (ogDesc != null && StringUtils.hasText(ogDesc.attr("content"))) {
            return ogDesc.attr("content");
        }
        
        // 使用页面第一段文字作为描述
        Element firstP = doc.select("p").first();
        if (firstP != null && StringUtils.hasText(firstP.text())) {
            String text = firstP.text();
            return text.length() > 200 ? text.substring(0, 200) + "..." : text;
        }
        
        return "";
    }

    /**
     * 提取页面链接
     */
    private void extractLinks(Document doc, String currentUrl, String baseUrl, 
                            Set<String> urlsToVisit, WebsiteCrawlConfig config) {
        try {
            URL base = new URL(baseUrl);
            URL current = new URL(currentUrl);
            
            Elements links = doc.select("a[href]");
            
            for (Element link : links) {
                String href = link.attr("href");
                if (!StringUtils.hasText(href)) {
                    continue;
                }
                
                // 解析绝对URL
                String absoluteUrl = resolveUrl(current, href);
                if (absoluteUrl == null) {
                    continue;
                }
                
                // 检查URL是否符合抓取规则
                if (shouldCrawlUrl(absoluteUrl, base, config)) {
                    urlsToVisit.add(absoluteUrl);
                }
            }
            
        } catch (Exception e) {
            log.error("提取链接失败: {}", currentUrl, e);
        }
    }

    /**
     * 解析相对URL为绝对URL
     */
    private String resolveUrl(URL base, String href) {
        try {
            URL url = new URL(base, href);
            return url.toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 判断URL是否应该被抓取
     */
    private boolean shouldCrawlUrl(String url, URL baseUrl, WebsiteCrawlConfig config) {
        try {
            URL urlObj = new URL(url);
            
            // 只抓取同域名的页面
            if (!urlObj.getHost().equals(baseUrl.getHost())) {
                return false;
            }
            
            // 检查URL模式过滤
            if (config.getExcludePatterns() != null) {
                for (String pattern : config.getExcludePatterns()) {
                    if (Pattern.compile(pattern).matcher(url).find()) {
                        return false;
                    }
                }
            }
            
            // 检查URL包含模式
            if (config.getIncludePatterns() != null && !config.getIncludePatterns().isEmpty()) {
                boolean matches = false;
                for (String pattern : config.getIncludePatterns()) {
                    if (Pattern.compile(pattern).matcher(url).find()) {
                        matches = true;
                        break;
                    }
                }
                if (!matches) {
                    return false;
                }
            }
            
            // 过滤常见的非内容文件
            String path = urlObj.getPath().toLowerCase();
            String[] excludeExtensions = {".jpg", ".jpeg", ".png", ".gif", ".pdf", ".zip", ".rar", 
                                        ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"};
            for (String ext : excludeExtensions) {
                if (path.endsWith(ext)) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证内容是否有效
     */
    private boolean isValidContent(String content, WebsiteCrawlConfig config) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        
        // 检查内容长度
        if (content.trim().length() < config.getMinContentLength()) {
            return false;
        }
        
        // 检查是否为错误页面（包含常见错误信息）
        String lowerContent = content.toLowerCase();
        String[] errorIndicators = {"404 not found", "page not found", "access denied", 
                                  "forbidden", "error occurred"};
        for (String indicator : errorIndicators) {
            if (lowerContent.contains(indicator)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 创建或更新网页实体
     */
    private void createOrUpdateWebpage(String url, String title, String description, 
                                     String content, WebsiteEntity website) {
        try {
            // 检查是否已存在该URL的网页
            List<WebpageEntity> existingPages = webpageRepository.findByKbase_UidAndDeletedFalse(
                website.getKbase().getUid());
            
            WebpageEntity existingPage = existingPages.stream()
                .filter(page -> url.equals(page.getUrl()))
                .findFirst()
                .orElse(null);
            
            if (existingPage != null) {
                // 更新现有页面
                existingPage.setTitle(title);
                existingPage.setDescription(description);
                existingPage.setContent(content);
                existingPage.setEnabled(true);
                webpageRepository.save(existingPage);
                
                log.debug("更新现有网页: {}", url);
            } else {
                // 创建新页面
                WebpageEntity newPage = WebpageEntity.builder()
                    .uid(uidUtils.getUid())
                    .title(title)
                    .url(url)
                    .description(description)
                    .content(content)
                    .enabled(true)
                    .kbase(website.getKbase())
                    .categoryUid(website.getCategoryUid())
                    .build();
                
                webpageRepository.save(newPage);
                
                log.debug("创建新网页: {}", url);
            }
            
        } catch (Exception e) {
            log.error("创建或更新网页失败: {}", url, e);
        }
    }

    /**
     * 创建抓取任务
     */
    private WebsiteCrawlTask createCrawlTask(WebsiteEntity website, WebsiteCrawlConfig config) {
        WebsiteCrawlTask task = WebsiteCrawlTask.builder()
            .taskId(uidUtils.getUid())
            .websiteUid(website.getUid())
            .websiteUrl(website.getUrl())
            .status(WebsiteCrawlStatus.RUNNING)
            .startTime(System.currentTimeMillis())
            .totalPages(0)
            .processedPages(0)
            .build();
        
        // 设置配置
        task.setConfig(config);
        
        return saveCrawlTask(task);
    }

    /**
     * 保存抓取任务
     */
    private WebsiteCrawlTask saveCrawlTask(WebsiteCrawlTask task) {
        return crawlTaskRepository.save(task);
    }

    /**
     * 获取抓取任务状态
     */
    public WebsiteCrawlTask getCrawlTaskStatus(String taskId) {
        return activeTasks.get(taskId);
    }

    /**
     * 停止抓取任务
     */
    public boolean stopCrawlTask(String taskId) {
        WebsiteCrawlTask task = activeTasks.get(taskId);
        if (task != null) {
            task.setStatus(WebsiteCrawlStatus.STOPPED);
            activeTasks.remove(taskId);
            return true;
        }
        return false;
    }

    /**
     * 解析站点地图
     */
    public List<String> parseSitemap(String sitemapUrl) {
        List<String> urls = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(sitemapUrl)
                .timeout(10000)
                .userAgent("Mozilla/5.0 (compatible; BytedeskBot/1.0; +https://www.bytedesk.com/bot)")
                .get();
            
            // 解析XML格式的sitemap
            Elements urlElements = doc.select("url > loc");
            for (Element element : urlElements) {
                String url = element.text();
                if (StringUtils.hasText(url)) {
                    urls.add(url);
                }
            }
            
            log.info("从sitemap解析到 {} 个URL: {}", urls.size(), sitemapUrl);
            
        } catch (Exception e) {
            log.error("解析sitemap失败: {}", sitemapUrl, e);
        }
        
        return urls;
    }

    /**
     * 清理资源
     */
    public void shutdown() {
        crawlExecutor.shutdown();
        try {
            if (!crawlExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                crawlExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            crawlExecutor.shutdownNow();
        }
    }
}
